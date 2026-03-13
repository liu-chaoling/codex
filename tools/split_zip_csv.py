#!/usr/bin/env python3
"""把 zip 中的 CSV 按“场景名字”优先拆分为小文件（默认 50MB）。"""

from __future__ import annotations

import argparse
import csv
import io
import re
import sys
import zipfile
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Tuple


def safe_name(name: str) -> str:
    name = (name or "未知场景").strip()
    name = re.sub(r"[\\/:*?\"<>|\r\n\t]+", "_", name)
    return name[:100] or "未知场景"


def detect_encoding_and_dialect(raw: bytes) -> Tuple[str, csv.Dialect]:
    candidates = ["utf-8-sig", "utf-8", "gb18030", "gbk"]
    decoded = None
    encoding = "utf-8-sig"
    for enc in candidates:
        try:
            decoded = raw.decode(enc)
            encoding = enc
            break
        except UnicodeDecodeError:
            continue
    if decoded is None:
        decoded = raw.decode("utf-8", errors="replace")
        encoding = "utf-8"

    try:
        dialect = csv.Sniffer().sniff(decoded[:4096])
    except csv.Error:
        dialect = csv.excel
    return encoding, dialect


def row_to_bytes(row: List[str], dialect: csv.Dialect, encoding: str) -> bytes:
    sio = io.StringIO()
    writer = csv.writer(
        sio,
        delimiter=dialect.delimiter,
        quotechar=dialect.quotechar,
        lineterminator="\n",
        quoting=dialect.quoting,
        escapechar=dialect.escapechar,
        doublequote=dialect.doublequote,
    )
    writer.writerow(row)
    return sio.getvalue().encode(encoding)


@dataclass
class SceneFileState:
    scene_name: str
    part_index: int
    fp: io.BufferedWriter
    bytes_written: int
    rows_written: int


class SceneSplitter:
    def __init__(self, out_dir: Path, header: List[str], dialect: csv.Dialect, encoding: str, max_bytes: int):
        self.out_dir = out_dir
        self.header = header
        self.dialect = dialect
        self.encoding = encoding
        self.max_bytes = max_bytes
        self.header_bytes = row_to_bytes(header, dialect, encoding)
        self.states: Dict[str, SceneFileState] = {}
        self.too_large_rows = 0

    def _open_new_part(self, scene_key: str, scene_name: str, part_index: int) -> SceneFileState:
        filename = f"{scene_key}_part{part_index:03d}.csv"
        path = self.out_dir / filename
        fp = open(path, "wb")
        fp.write(self.header_bytes)
        return SceneFileState(scene_name=scene_name, part_index=part_index, fp=fp, bytes_written=len(self.header_bytes), rows_written=0)

    def write_row(self, scene_name: str, row: List[str]) -> None:
        scene_key = safe_name(scene_name)
        row_bytes = row_to_bytes(row, self.dialect, self.encoding)

        if len(row_bytes) > self.max_bytes:
            self.too_large_rows += 1

        state = self.states.get(scene_key)
        if state is None:
            state = self._open_new_part(scene_key, scene_name, 1)
            self.states[scene_key] = state

        would_exceed = state.bytes_written + len(row_bytes) > self.max_bytes
        if would_exceed and state.rows_written > 0:
            state.fp.close()
            state = self._open_new_part(scene_key, scene_name, state.part_index + 1)
            self.states[scene_key] = state

        state.fp.write(row_bytes)
        state.bytes_written += len(row_bytes)
        state.rows_written += 1

    def close(self) -> None:
        for state in self.states.values():
            state.fp.close()


def split_zip_csv(zip_path: Path, out_dir: Path, scene_col: str, max_mb: float) -> None:
    max_bytes = int(max_mb * 1024 * 1024)
    out_dir.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(zip_path, "r") as zf:
        csv_members = [i for i in zf.infolist() if i.filename.lower().endswith(".csv") and not i.is_dir()]
        if not csv_members:
            raise FileNotFoundError("zip 中没有找到 CSV 文件")

        csv_info = csv_members[0]
        if len(csv_members) > 1:
            print(f"[提示] zip 内有多个 CSV，仅处理第一个：{csv_info.filename}")

        with zf.open(csv_info, "r") as raw_file:
            sample = raw_file.read(65536)

        encoding, dialect = detect_encoding_and_dialect(sample)
        print(f"[信息] 检测编码：{encoding}，分隔符：{repr(dialect.delimiter)}")

        with zf.open(csv_info, "r") as raw_file:
            text_file = io.TextIOWrapper(raw_file, encoding=encoding, newline="")
            reader = csv.reader(text_file, dialect=dialect)

            try:
                header = next(reader)
            except StopIteration:
                raise ValueError("CSV 是空文件")

            if scene_col not in header:
                raise KeyError(f"CSV 中未找到列名：{scene_col}；实际列有：{header}")

            scene_idx = header.index(scene_col)
            splitter = SceneSplitter(out_dir, header, dialect, encoding, max_bytes)

            total_rows = 0
            for row in reader:
                if len(row) < len(header):
                    row += [""] * (len(header) - len(row))
                scene_name = row[scene_idx] if scene_idx < len(row) else ""
                splitter.write_row(scene_name, row)
                total_rows += 1

            splitter.close()

    total_files = len(list(out_dir.glob("*.csv")))
    print(f"[完成] 共处理 {total_rows} 行，输出 {total_files} 个文件到：{out_dir}")
    if splitter.too_large_rows > 0:
        print(f"[警告] 有 {splitter.too_large_rows} 行单行已超过 {max_mb}MB，无法保证对应文件严格小于阈值。")


def parse_args(argv: Optional[Iterable[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="将 zip 中 CSV 按“场景名字”优先切分成小于 50MB 的文件")
    parser.add_argument("zip_path", help="zip 文件路径")
    parser.add_argument("--out-dir", default="output_split", help="输出目录（默认 output_split）")
    parser.add_argument("--scene-col", default="场景名字", help="用于优先切分的列名（默认 场景名字）")
    parser.add_argument("--max-mb", type=float, default=50, help="单文件最大体积（MB，默认 50）")
    return parser.parse_args(argv)


def main(argv: Optional[Iterable[str]] = None) -> int:
    args = parse_args(argv)
    zip_path = Path(args.zip_path).expanduser().resolve()
    out_dir = Path(args.out_dir).expanduser().resolve()

    if not zip_path.exists():
        print(f"[错误] 文件不存在：{zip_path}", file=sys.stderr)
        return 1

    try:
        split_zip_csv(zip_path, out_dir, args.scene_col, args.max_mb)
        return 0
    except Exception as exc:
        print(f"[错误] {exc}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
