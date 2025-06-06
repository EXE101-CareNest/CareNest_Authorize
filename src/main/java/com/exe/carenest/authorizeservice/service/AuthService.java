package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.dto.LoginRequest;
import com.exe.carenest.authorizeservice.dto.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.TokenResponse;
import com.exe.carenest.authorizeservice.entity.User;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import com.exe.carenest.authorizeservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepo;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    private final RedisTemplate<String, String> redis;

    private final long refreshTokenTTL = 7 * 24 * 60 * 60L; // 7 ngày


    public void register(RegisterRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            throw new RuntimeException("Username exists");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(req.role());
        userRepo.save(user);
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepo.findByUsername(req.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        redis.opsForValue().set("refresh:" + refreshToken, user.getUsername(), refreshTokenTTL, TimeUnit.SECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }

    public boolean verify(String token) {
        return jwtProvider.validateToken(token);
    }

    public boolean authorize(String token, String requiredRole) {
        return verify(token) && jwtProvider.getRole(token).equals(requiredRole);
    }

    public TokenResponse refresh(String refreshToken) {
        String username = redis.opsForValue().get("refresh:" + refreshToken);
        if (username == null) throw new RuntimeException("Invalid refresh token");

        User user = userRepo.findByUsername(username).orElseThrow();
        String newAccessToken = jwtProvider.generateToken(user);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    public void revokeRefreshToken(String refreshToken) {
        redis.delete("refresh:" + refreshToken);
    }
}
