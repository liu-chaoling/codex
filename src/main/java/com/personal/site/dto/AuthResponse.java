package com.personal.site.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String displayName;
    private String avatarUrl;
}
