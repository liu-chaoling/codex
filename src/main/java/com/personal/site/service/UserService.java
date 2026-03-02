package com.personal.site.service;

import com.personal.site.dto.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserProfileResponse profile(Long userId);

    UserProfileResponse updateAvatar(Long userId, MultipartFile file);
}
