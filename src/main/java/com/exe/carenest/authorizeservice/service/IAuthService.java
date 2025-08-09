package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.dto.response.TokenResponse;

public interface IAuthService {
    TokenResponse login(LoginRequest req);
    boolean verify(String token);
    boolean authorize(String token, String requiredRole);
    TokenResponse refresh(String refreshToken);
    void revokeRefreshToken(String refreshToken);
    void logout(String jwtToken);
}
