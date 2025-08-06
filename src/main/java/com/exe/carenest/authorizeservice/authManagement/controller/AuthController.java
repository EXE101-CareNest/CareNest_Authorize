package com.exe.carenest.authorizeservice.authManagement.controller;

import com.exe.carenest.authorizeservice.authManagement.customAnnotation.AllowAllRoles;
import com.exe.carenest.authorizeservice.authManagement.dto.request.ForgotPasswordRequest;
import com.exe.carenest.authorizeservice.authManagement.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.authManagement.dto.response.LoginResponse;
import com.exe.carenest.authorizeservice.authManagement.dto.response.TokenResponse;
import com.exe.carenest.authorizeservice.authManagement.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    @AllowAllRoles
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);

        // Tạo cookie chứa refresh token
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.refreshToken());
        refreshTokenCookie.setHttpOnly(true);                   // Không cho JS truy cập
        refreshTokenCookie.setSecure(true);                     // Chỉ gửi qua HTTPS
        refreshTokenCookie.setPath("/");                        // Đường dẫn áp dụng
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);         // Hết hạn trong 7 ngày (đơn vị: giây)

        // Thêm cookie vào response
        response.addCookie(refreshTokenCookie);

        log.info("Login");
        // Trả về accessToken trong body
        return ResponseEntity.ok(new LoginResponse(tokenResponse.accessToken(), request.username()));
    }

    @GetMapping("/verify")
    @AllowAllRoles
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String token) {
        boolean valid = authService.verify(token.replace("Bearer ", ""));
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @GetMapping("/authorize")
    @AllowAllRoles
    public ResponseEntity<?> authorize(@RequestHeader("Authorization") String token,
                                       @RequestParam String role) {
        boolean allowed = authService.authorize(token.replace("Bearer ", ""), role);
        return ResponseEntity.ok(Map.of("authorized", allowed));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @PostMapping("/refresh")
    @AllowAllRoles
    public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }

    @PostMapping("/revoke")
    @AllowAllRoles
    public ResponseEntity<?> revoke(@RequestBody Map<String, String> body) {
        authService.revokeRefreshToken(body.get("refreshToken"));
        return ResponseEntity.ok(Map.of("revoked", true));
    }
}
