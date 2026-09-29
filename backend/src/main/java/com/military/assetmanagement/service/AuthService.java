package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AuthResponse;
import com.military.assetmanagement.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
}
