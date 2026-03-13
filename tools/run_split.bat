@echo off
setlocal
set SCRIPT_DIR=%~dp0

echo 请输入 zip 文件完整路径（例如 C:\data\report.zip）：
set /p ZIP_PATH=

if "%ZIP_PATH%"=="" (
  echo 未输入路径，程序结束。
  pause
  exit /b 1
)

python "%SCRIPT_DIR%split_zip_csv.py" "%ZIP_PATH%"

echo.
echo 处理完成，按任意键关闭窗口。
pause >nul
