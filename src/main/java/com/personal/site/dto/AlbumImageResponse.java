package com.personal.site.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlbumImageResponse {
    private Long id;
    private Long userId;
    private String title;
    private String fileName;
    private String fileUrl;
    private LocalDateTime createdAt;
}
