package com.exe.carenest.authorizeservice.authManagement.service.impl;

import com.exe.carenest.authorizeservice.authManagement.service.IAuthService;
import com.exe.carenest.authorizeservice.authManagement.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.authManagement.dto.response.TokenResponse;
import com.exe.carenest.authorizeservice.authManagement.model.Account;
import com.exe.carenest.authorizeservice.userManagement.repository.UserRepository;
import com.exe.carenest.authorizeservice.infrastructure.JwtProvider;
import com.exe.carenest.authorizeservice.userManagement.service.impl.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {


    private final UserRepository userRepo;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

//    private final RedisTemplate<String, String> redis;

    private final long refreshTokenTTL = 7 * 24 * 60 * 60L; // 7 ngày


    public TokenResponse login(LoginRequest req) {
        Account user = userRepo.findByUsername(req.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        redisService.save("refresh:" + refreshToken,user.getUsername(),refreshTokenTTL, TimeUnit.SECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }

    public boolean verify(String token) {
        return jwtProvider.validateToken(token);
    }

    public boolean authorize(String token, String requiredRole) {
        return verify(token) && jwtProvider.getRole(token).equals(requiredRole);
    }

    @Override
    public void logout(String jwtToken) {
        Claims claims = jwtProvider.claimToken(jwtToken);

        long expirationMs = claims.getExpiration().getTime() - System.currentTimeMillis();

        // Add token to blacklist in Redis with TTL
        redisService.save("blacklist:" + jwtToken, "invalidated",expirationMs,TimeUnit.MILLISECONDS);
    }



    public TokenResponse refresh(String refreshToken) {
        Object username = redisService.get("refresh:" + refreshToken);
        if (username == null) throw new RuntimeException("Invalid refresh token");

        Account user = userRepo.findByUsername(username.toString()).orElseThrow();
        String newAccessToken = jwtProvider.generateToken(user);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    public void revokeRefreshToken(String refreshToken) {
        redisService.delete("refresh:" + refreshToken);
    }

}
