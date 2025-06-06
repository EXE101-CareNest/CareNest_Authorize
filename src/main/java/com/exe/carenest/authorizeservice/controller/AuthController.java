package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.dto.LoginRequest;
import com.exe.carenest.authorizeservice.dto.LoginResponse;
import com.exe.carenest.authorizeservice.dto.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.TokenResponse;
import com.exe.carenest.authorizeservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registered successfully");
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

        // Trả về accessToken trong body
        return ResponseEntity.ok(new LoginResponse(tokenResponse.accessToken(),request.username()));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String token) {
        boolean valid = authService.verify(token.replace("Bearer ", ""));
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestHeader("Authorization") String token,
                                       @RequestParam String role) {
        boolean allowed = authService.authorize(token.replace("Bearer ", ""), role);
        return ResponseEntity.ok(Map.of("authorized", allowed));
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revoke(@RequestBody Map<String, String> body) {
        authService.revokeRefreshToken(body.get("refreshToken"));
        return ResponseEntity.ok(Map.of("revoked", true));
    }
}
