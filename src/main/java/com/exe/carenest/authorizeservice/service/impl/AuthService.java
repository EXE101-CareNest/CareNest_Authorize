package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.dto.response.TokenResponse;
import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.service.IAuthService;
import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {


    private final UserRepository userRepo;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

//    private final RedisTemplate<String, String> redis;

    private final long refreshTokenTTL = 7 * 24 * 60 * 60L; // 7 ngày




    public TokenResponse login(LoginRequest req) {
        Account user = userRepo.findByUsername(req.username()).orElseThrow(() -> new UsernameNotFoundException(req.username()));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        log.info("Login");


        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        redisService.save("refresh:" + refreshToken, user.getUsername(), refreshTokenTTL, TimeUnit.SECONDS);

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
        redisService.save("blacklist:" + jwtToken, "invalidated", expirationMs, TimeUnit.MILLISECONDS);
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
