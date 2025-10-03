package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.dto.response.TokenResponse;
import com.exe.carenest.authorizeservice.exception.*;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import com.exe.carenest.authorizeservice.service.IAuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final RedisService redisService;  // Giả sử bạn có RedisService để lưu blacklist/refresh

//    @Autowired
    private final AuthenticationManager authenticationManager;


    private final long refreshTokenTTL = 7 * 24 * 60 * 60L; // 7 ngày

    @Override
    public TokenResponse login(LoginRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            throw new InvalidTokenException("Thông tin đăng nhập không hợp lệ");
        }

        // Let AuthenticationManager handle all validation
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        } catch (BadCredentialsException e) {
            throw new PasswordException("Mật khẩu không đúng");
        } catch (DisabledException e) {
            throw new UnauthorizedException("Tài khoàn chưa được kích hoạt");
        }

        //Get
        //        redisService.save("blacklist:" + jwtToken, "invalidated", expirationMs, TimeUnit.MILLISECONDS);


        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Account user = userRepo.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        log.info("Login successful for user: {}", req.username());

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();
        redisService.save("refresh:" + refreshToken, user.getUsername(), refreshTokenTTL, TimeUnit.SECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public boolean verify(String token) {
        try {
            return jwtProvider.validateToken(token);
        } catch (Exception e) {
            if (e.getMessage().contains("expired")) {
                throw new ExpiredTokenException("Token đã hết hạn: " + e.getMessage());
            } else {
                throw new InvalidTokenException("Token không hợp lệ: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean authorize(String token, String requiredRole) {
        if (!verify(token)) {
            return false;
        }

        String role = jwtProvider.getRole(token);
        if (!role.equals(requiredRole)) {
            throw new UnauthorizedException("Quyền truy cập không đủ cho role: " + requiredRole);
        }

        return true;
    }

    @Override
    public void logout(String jwtToken) {
        // Check token valid trước
        Claims claims = jwtProvider.claimToken(jwtToken);
        long expirationMs = claims.getExpiration().getTime() - System.currentTimeMillis();

        // Add token to blacklist in Redis with TTL
        redisService.save("blacklist:" + jwtToken, "invalidated", expirationMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidTokenException("Refresh token không được cung cấp");
        }

        Object username = redisService.get("refresh:" + refreshToken);
        if (username == null) {
            throw new InvalidTokenException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        Account user = userRepo.findByUsername(username.toString()).orElseThrow(UserNotFoundException::new);

        String newAccessToken = jwtProvider.generateToken(user);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidTokenException("Refresh token không được cung cấp");
        }

        redisService.delete("refresh:" + refreshToken);
    }
}
