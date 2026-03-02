package com.personal.site.controller;

import com.personal.site.common.ApiResponse;
import com.personal.site.dto.UserProfileResponse;
import com.personal.site.entity.User;
import com.personal.site.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(userService.profile(user.getId()));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserProfileResponse> uploadAvatar(@AuthenticationPrincipal User user,
                                                         @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(userService.updateAvatar(user.getId(), file));
    }
}
