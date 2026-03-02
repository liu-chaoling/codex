package com.personal.site.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, String category);

    Resource loadAsResource(String path);
}
