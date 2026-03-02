package com.personal.site.service;

import com.personal.site.dto.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse create(Long userId, String content, List<MultipartFile> images);

    List<PostResponse> list();
}
