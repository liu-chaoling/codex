package com.personal.site.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String bio;
}
