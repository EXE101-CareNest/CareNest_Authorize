package com.exe.carenest.authorizeservice.authManagement.service;

import com.exe.carenest.authorizeservice.authManagement.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.authManagement.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.authManagement.dto.response.TokenResponse;

public interface IAuthService {
    TokenResponse login(LoginRequest req);
    boolean verify(String token);
    boolean authorize(String token, String requiredRole);
    TokenResponse refresh(String refreshToken);
    void revokeRefreshToken(String refreshToken);
    void logout(String jwtToken);
}
