package com.personal.site.service;

import com.personal.site.dto.AlbumImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlbumService {
    AlbumImageResponse upload(Long userId, String title, MultipartFile file);

    List<AlbumImageResponse> listByUser(Long userId);
}
