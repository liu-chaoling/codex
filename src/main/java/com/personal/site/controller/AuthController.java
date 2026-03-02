package com.personal.site.controller;

import com.personal.site.common.ApiResponse;
import com.personal.site.dto.AuthResponse;
import com.personal.site.dto.LoginRequest;
import com.personal.site.dto.RegisterRequest;
import com.personal.site.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Validated @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}
