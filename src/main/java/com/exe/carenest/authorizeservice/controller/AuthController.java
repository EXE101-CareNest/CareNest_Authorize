package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.dto.request.ForgotPasswordRequest;
import com.exe.carenest.authorizeservice.dto.request.LoginRequest;
import com.exe.carenest.authorizeservice.dto.request.NewPasswordRequest;
import com.exe.carenest.authorizeservice.dto.request.VerifyOtpRequest;
import com.exe.carenest.authorizeservice.dto.response.LoginResponse;
import com.exe.carenest.authorizeservice.dto.response.TokenResponse;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.exception.BadRequestException;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.IAuthService;
import com.exe.carenest.authorizeservice.service.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public String logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken);
        SecurityContextHolder.clearContext();
        return "Logged out successfully";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);

        // Tạo cookie chứa refresh token
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        // Thêm cookie vào response
        response.addCookie(refreshTokenCookie);

        log.info("Login");
        // Trả về accessToken trong body
        return new LoginResponse(tokenResponse.accessToken(), tokenResponse.refreshToken(), request.username());
    }

    @GetMapping("/verify")
    public Map<String, Boolean> verify(@RequestHeader("Authorization") String token) {
        boolean valid = authService.verify(token.replace("Bearer ", ""));
        return Map.of("valid", valid);
    }

    @GetMapping("/authorize")
    public Map<String, Boolean> authorize(@RequestHeader("Authorization") String token,
                                       @RequestParam String role) {
        boolean allowed = authService.authorize(token.replace("Bearer ", ""), role);
        return Map.of("authorized", allowed);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request, HttpServletResponse response) {
        // Validate input
        if (request == null || request.email() == null || request.email().trim().isEmpty()) {
            throw new BadRequestException("Email không được để trống");
        }

        try {
            // Security best practice: Always send OTP regardless of email existence
            // This prevents email enumeration attacks
            String otpToken = otpService.sendOtp(request.email());
            response.setHeader("X-Key-APT", otpToken);
            return "Nếu email tồn tại trong hệ thống, mã OTP đã được gửi";
        } catch (Exception e) {
            log.error("Error in forgot password for email: {}", request.email(), e);
            throw new ApiException("INTERNAL_ERROR", "Có lỗi xảy ra. Vui lòng thử lại sau", 500);
        }
    }

    @PostMapping("/verify/otp")
    @Operation(summary = "Verify OTP for password reset")
    public String verifyOtpForPasswordReset(@RequestHeader("X-Key-APT") String token, @RequestBody VerifyOtpRequest otpRequest, HttpServletResponse response) {
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Token OTP không được để trống");
        }

        if (otpRequest == null || otpRequest.otp() == null || otpRequest.otp().trim().isEmpty()) {
            throw new BadRequestException("Mã OTP không được để trống");
        }

        try {
            // Verify OTP
            boolean isValid = otpService.verifyOTP(token, otpRequest);

            if (isValid) {
                // Security improvement: Generate a new secure token for password reset
                // Instead of returning email directly, return a secure token
                String passwordResetToken = jwtProvider.generateTokenByEmail(otpRequest.email(),
                        new Date(System.currentTimeMillis() + (15 * 60 * 1000))); // 15 minutes validity

                response.setHeader("X-Password-Reset-Token", passwordResetToken);
                return "OTP xác thực thành công. Bạn có thể đặt lại mật khẩu";
            } else {
                throw new BadRequestException("Mã OTP không chính xác");
            }
        } catch (Exception e) {
            log.error("Error verifying OTP: ", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/newPassword")
    @Operation(summary = "Reset password with new password")
    public String resetPassword(@RequestHeader("X-Password-Reset-Token") String resetToken,
                                                @RequestBody NewPasswordRequest newPasswordRequest) {
        // Validate inputs
        if (resetToken == null || resetToken.trim().isEmpty()) {
            throw new BadRequestException("Token đặt lại mật khẩu không được để trống");
        }

        if (newPasswordRequest == null) {
            throw new BadRequestException("Thông tin mật khẩu không hợp lệ");
        }

        if (newPasswordRequest.password() == null || newPasswordRequest.password().trim().isEmpty()) {
            throw new BadRequestException("Mật khẩu mới không được để trống");
        }

        if (newPasswordRequest.reEnterPassword() == null || newPasswordRequest.reEnterPassword().trim().isEmpty()) {
            throw new BadRequestException("Xác nhận mật khẩu không được để trống");
        }

        // Validate password confirmation
        if (!newPasswordRequest.password().equals(newPasswordRequest.reEnterPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        // Validate password strength
        if (newPasswordRequest.password().length() < 6) {
            throw new BadRequestException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        try {
            // Validate reset token and extract email
            if (!jwtProvider.validateToken(resetToken)) {
                throw new BadRequestException("Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn");
            }

            String email = jwtProvider.getSubject(resetToken);
            if (email == null || email.trim().isEmpty()) {
                throw new BadRequestException("Không thể xác định email từ token");
            }

            // Update password
            accountService.updatePassword(email, newPasswordRequest.password());

            log.info("Password reset successful for email: {}", email);
            return "Đặt lại mật khẩu thành công";
        } catch (Exception e) {
            log.error("Error resetting password: ", e);

            if (e.getMessage().contains("expired")) {
                throw new BadRequestException("Token đặt lại mật khẩu đã hết hạn");
            } else if (e.getMessage().contains("invalid")) {
                throw new BadRequestException("Token đặt lại mật khẩu không hợp lệ");
            } else {
                throw new ApiException("INTERNAL_ERROR", "Có lỗi xảy ra khi đặt lại mật khẩu", 500);
            }
        }
    }

    @PostMapping("/registerVerifyToken")
    @Operation(summary = "Verify email after registration")
    public String registerVerifyToken(@RequestHeader("X-Key-APT") String token, @RequestBody VerifyOtpRequest otpRequest) {
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Token OTP không được để trống");
        }

        if (otpRequest == null || otpRequest.otp() == null || otpRequest.otp().trim().isEmpty()) {
            throw new BadRequestException("Mã OTP không được để trống");
        }

        try {
            // Verify OTP
            boolean isValid = otpService.verifyOTP(token, otpRequest);

            if (isValid) {
                // Extract email from JWT token to get the account
                String email = jwtProvider.getSubject(token);
                if (email == null || email.trim().isEmpty()) {
                    throw new BadRequestException("Không thể xác định email từ token");
                }

                // Activate account after successful email verification
                accountService.activateAccountByEmail(email);

                log.info("Email verification successful for: {}", email);
                return "Xác thực email thành công. Tài khoản đã được kích hoạt";
            } else {
                throw new BadRequestException("Mã OTP không chính xác");
            }
        } catch (Exception e) {
            log.error("Error verifying registration OTP: ", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody Map<String, String> body) {
        return authService.refresh(body.get("refreshToken"));
    }

    @PostMapping("/revoke")
    public Map<String, Boolean> revoke(@RequestBody Map<String, String> body) {
        authService.revokeRefreshToken(body.get("refreshToken"));
        return Map.of("revoked", true);
    }

    @PostMapping("/re-send-otp-code")
    public String reSendOtpCode(@RequestParam String email, HttpServletResponse response) {
        String otpToken = otpService.sendRegistrationOtp(email);
        response.setHeader("X-Key-APT", otpToken);
        return "đã gửi lại mật khẩu";
    }
}
