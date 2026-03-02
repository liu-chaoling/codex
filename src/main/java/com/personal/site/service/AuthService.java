package com.personal.site.service;

import com.personal.site.dto.AuthResponse;
import com.personal.site.dto.LoginRequest;
import com.personal.site.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
