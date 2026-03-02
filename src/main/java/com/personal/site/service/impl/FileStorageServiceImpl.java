package com.personal.site.service.impl;

import com.personal.site.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root;

    public FileStorageServiceImpl(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    @Override
    public String store(MultipartFile file, String category) {
        try {
            String rawFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int idx = rawFileName.lastIndexOf('.');
            if (idx >= 0) {
                ext = rawFileName.substring(idx);
            }
            String safeName = UUID.randomUUID() + ext;
            Path categoryPath = root.resolve(category);
            Files.createDirectories(categoryPath);
            Path target = categoryPath.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return category + "/" + safeName;
        } catch (IOException ex) {
            throw new IllegalStateException("文件存储失败", ex);
        }
    }

    @Override
    public Resource loadAsResource(String path) {
        try {
            Path file = root.resolve(path).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new IllegalArgumentException("文件不存在");
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("文件路径非法", ex);
        }
    }
}
