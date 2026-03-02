package com.personal.site.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id;
    private Long userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
