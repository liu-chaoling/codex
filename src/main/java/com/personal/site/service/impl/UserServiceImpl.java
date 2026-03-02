package com.personal.site.service.impl;

import com.personal.site.dto.UserProfileResponse;
import com.personal.site.entity.User;
import com.personal.site.repository.UserRepository;
import com.personal.site.service.FileStorageService;
import com.personal.site.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public UserProfileResponse profile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return map(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        String path = fileStorageService.store(file, "avatar");
        user.setAvatarUrl(path);
        return map(userRepository.save(user));
    }

    private UserProfileResponse map(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .build();
    }
}
