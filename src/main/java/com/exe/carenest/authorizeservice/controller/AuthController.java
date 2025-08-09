package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.config.annotation.AllowAllRoles;
import com.exe.carenest.authorizeservice.dto.request.*;
import com.exe.carenest.authorizeservice.dto.response.LoginResponse;
import com.exe.carenest.authorizeservice.dto.response.TokenResponse;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.IAuthService;
import com.exe.carenest.authorizeservice.service.OTPService;
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
    private final IAccountService  accountService;
    private final OTPService otpService;

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
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request, HttpServletResponse response) {
        response.setHeader("X-Key-APT", otpService.sendOtp(request.email()));
        return ResponseEntity.ok("Forgot password reset successfully");
    }

    @PostMapping("/verify/otp")
    @Operation(summary = "Request password reset")
    public ResponseEntity<String> forgotPassword(@RequestHeader("X-Key-APT") String token, @RequestBody VerifyOtpRequest otpRequest) {
       if(otpService.verifyOTP(token, otpRequest.otp())){
           return ResponseEntity.ok(otpRequest.email());
       }
        return ResponseEntity.badRequest().body("OTP is incorrect");
    }

    @PostMapping("/")
    public ResponseEntity<String> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest){
        if(!newPasswordRequest.password().equals(newPasswordRequest.reEnterPassword())){
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        accountService.updatePassword(newPasswordRequest.email(), newPasswordRequest.password());

        return ResponseEntity.ok("OK");
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
