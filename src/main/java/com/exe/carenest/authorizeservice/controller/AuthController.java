package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.config.annotation.AllowAllRoles;
import com.exe.carenest.authorizeservice.config.JwtProvider;
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

import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IAccountService accountService;
    private final OTPService otpService;
    private final JwtProvider jwtProvider;

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
        // Validate input
        if (request == null || request.email() == null || request.email().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được để trống");
        }
        
        try {
            // Security best practice: Always send OTP regardless of email existence
            // This prevents email enumeration attacks
            String otpToken = otpService.sendOtp(request.email());
            response.setHeader("X-Key-APT", otpToken);
            return ResponseEntity.ok("Nếu email tồn tại trong hệ thống, mã OTP đã được gửi");
        } catch (Exception e) {
            log.error("Error in forgot password for email: {}", request.email(), e);
            return ResponseEntity.internalServerError().body("Có lỗi xảy ra. Vui lòng thử lại sau");
        }
    }

    @PostMapping("/verify/otp")
    @Operation(summary = "Verify OTP for password reset")
    public ResponseEntity<String> verifyOtpForPasswordReset(@RequestHeader("X-Key-APT") String token, @RequestBody VerifyOtpRequest otpRequest) {
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token OTP không được để trống");
        }
        
        if (otpRequest == null || otpRequest.otp() == null || otpRequest.otp().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã OTP không được để trống");
        }
        
        try {
            // Verify OTP
            boolean isValid = otpService.verifyOTP(token, otpRequest.otp());
            
            if (isValid) {
                // Security improvement: Generate a new secure token for password reset
                // Instead of returning email directly, return a secure token
                String passwordResetToken = jwtProvider.generateTokenByEmail(otpRequest.email(), 
                    new Date(System.currentTimeMillis() + (15 * 60 * 1000))); // 15 minutes validity
                
                return ResponseEntity.ok().header("X-Password-Reset-Token", passwordResetToken)
                    .body("OTP xác thực thành công. Bạn có thể đặt lại mật khẩu");
            } else {
                return ResponseEntity.badRequest().body("Mã OTP không chính xác");
            }
        } catch (Exception e) {
            log.error("Error verifying OTP: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/newPassword")
    @Operation(summary = "Reset password with new password")
    public ResponseEntity<String> resetPassword(@RequestHeader("X-Password-Reset-Token") String resetToken, 
                                                @RequestBody NewPasswordRequest newPasswordRequest) {
        // Validate inputs
        if (resetToken == null || resetToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token đặt lại mật khẩu không được để trống");
        }
        
        if (newPasswordRequest == null) {
            return ResponseEntity.badRequest().body("Thông tin mật khẩu không hợp lệ");
        }
        
        if (newPasswordRequest.password() == null || newPasswordRequest.password().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không được để trống");
        }
        
        if (newPasswordRequest.reEnterPassword() == null || newPasswordRequest.reEnterPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Xác nhận mật khẩu không được để trống");
        }
        
        // Validate password confirmation
        if (!newPasswordRequest.password().equals(newPasswordRequest.reEnterPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp");
        }
        
        // Validate password strength
        if (newPasswordRequest.password().length() < 6) {
            return ResponseEntity.badRequest().body("Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        try {
            // Validate reset token and extract email
            if (!jwtProvider.validateToken(resetToken)) {
                return ResponseEntity.badRequest().body("Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn");
            }
            
            String email = jwtProvider.getSubject(resetToken);
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Không thể xác định email từ token");
            }
            
            // Update password
            accountService.updatePassword(email, newPasswordRequest.password());
            
            log.info("Password reset successful for email: {}", email);
            return ResponseEntity.ok("Đặt lại mật khẩu thành công");
        } catch (Exception e) {
            log.error("Error resetting password: ", e);
            
            if (e.getMessage().contains("expired")) {
                return ResponseEntity.badRequest().body("Token đặt lại mật khẩu đã hết hạn");
            } else if (e.getMessage().contains("invalid")) {
                return ResponseEntity.badRequest().body("Token đặt lại mật khẩu không hợp lệ");
            } else {
                return ResponseEntity.internalServerError().body("Có lỗi xảy ra khi đặt lại mật khẩu");
            }
        }
    }

    @PostMapping("/registerVerifyToken")
    @Operation(summary = "Verify email after registration")
    public ResponseEntity<String> registerVerifyToken(@RequestHeader("X-Key-APT") String token, @RequestBody VerifyOtpRequest otpRequest) {
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token OTP không được để trống");
        }
        
        if (otpRequest == null || otpRequest.otp() == null || otpRequest.otp().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã OTP không được để trống");
        }
        
        try {
            // Verify OTP
            boolean isValid = otpService.verifyOTP(token, otpRequest.otp());
            
            if (isValid) {
                // Extract email from JWT token to get the account
                String email = jwtProvider.getSubject(token);
                if (email == null || email.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Không thể xác định email từ token");
                }
                
                // Activate account after successful email verification
                accountService.activateAccountByEmail(email);
                
                log.info("Email verification successful for: {}", email);
                return ResponseEntity.ok("Xác thực email thành công. Tài khoản đã được kích hoạt");
            } else {
                return ResponseEntity.badRequest().body("Mã OTP không chính xác");
            }
        } catch (Exception e) {
            log.error("Error verifying registration OTP: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
