package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.data.OTP_Purpose;
import com.exe.carenest.authorizeservice.dto.request.VerifyOtpRequest;
import com.exe.carenest.authorizeservice.exception.OTPException;
import com.exe.carenest.authorizeservice.exception.OTPExpiredException;
import com.exe.carenest.authorizeservice.exception.OTPVerificationException;
import com.exe.carenest.authorizeservice.service.impl.RedisService;
import com.exe.carenest.authorizeservice.ultil.CryptoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {

    private final int otpExpiredTime = 300; // 5 minutes in seconds

    private final RedisService redisCache;
    private final CryptoHelper helper;
    private final JwtProvider jwtProvider;
    private final EmailService emailService; // ✅ Inject EmailService

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Validate email format
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new OTPException("Email không được để trống");
        }
        if (!EMAIL_PATTERN.matcher(email.trim().toLowerCase()).matches()) {
            throw new OTPException("Định dạng email không hợp lệ");
        }
    }

    /**
     * Check and increment rate limiting
     */
    private void checkRateLimit(String email) {
        String emailRateKey = "otp_send:" + email;
        Integer attempts = (Integer) redisCache.get(emailRateKey);

        if (attempts != null && attempts >= 3) {
            throw new OTPException("Quá nhiều yêu cầu OTP. Chờ 15 phút");
        }

        // Increment attempts
        redisCache.save(emailRateKey, (attempts == null ? 1 : attempts + 1),
                900, TimeUnit.SECONDS); // 15 minutes
    }

    /**
     * Generate and save OTP token
     */
    private String generateAndSaveOTPToken(String email, String otpCode) {
        long expirationMillis = 5 * 60 * 1000; // 5 minutes
        String otpToken = jwtProvider.generateTokenByEmail(email,
                new Date(System.currentTimeMillis() + expirationMillis));

        redisCache.save("otp:" + otpToken, otpCode, expirationMillis, TimeUnit.MILLISECONDS);

        log.info("OTP token generated for email: {}", email);
        return otpToken;
    }



    private String getSubject(OTP_Purpose purpose) {
        return switch (purpose) {
            case REGISTER -> "Xác thực Email - Care Nest";
            case FORGET_PASSWORD -> "Đặt lại mật khẩu - Care Nest";
            default -> "OTP - Care Nest";
        };
    }


    // Trong OTPService
    @Transactional
    public String sendOtp(String email, OTP_Purpose purpose) {
        try {
            String otpCode = helper.generateOtp(); // "123456"

//            String templateHtml = emailService.getTemplate(purpose,email,otpCode);
            if(purpose == OTP_Purpose.FORGET_PASSWORD) {
                emailService.sendPasswordResetOTP(email, otpCode);
            } else if (purpose == OTP_Purpose.REGISTER) {
                emailService.sendRegistrationOTP(email, otpCode);
            }
            return generateAndSaveOTPToken(email, otpCode);
        } catch (Exception e) {
            if (e instanceof OTPException) {
                throw e;
            }
            throw new OTPException("Lỗi hệ thống khi gửi OTP xác thực email: " + e.getMessage());
        }
    }
    /**
     * Check if OTP token is expired
     */
    public boolean isOtpExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new OTPException("Token OTP không được để trống");
        }

        try {
            if (!jwtProvider.validateToken(token)) {
                return true;
            }

            String otpCode = (String) redisCache.get("otp:" + token);
            return otpCode == null;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Verify OTP code against token
     */
    public boolean verifyOTP(String token, VerifyOtpRequest verifyOtpRequest) {
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            throw new OTPException("Token OTP không được để trống");
        }

        if (verifyOtpRequest == null || verifyOtpRequest.otp().trim().isEmpty()) {
            throw new OTPVerificationException("Mã OTP không được để trống");
        }

        if (!verifyOtpRequest.otp().matches("\\d{6}")) {
            throw new OTPVerificationException("Mã OTP phải là 6 chữ số");
        }

        String attemptKey = "verify_attempts:" + token;
        Integer attempts = (Integer) redisCache.get(attemptKey);

        if (attempts != null && attempts >= 5) {
            throw new OTPVerificationException("Quá nhiều lần thử. Token bị khóa");
        }

        try {
            // Validate JWT token
            if (!jwtProvider.validateToken(token)) {
                throw new OTPExpiredException("Token OTP không hợp lệ hoặc đã hết hạn");
            }

            // Get OTP from Redis
            String storedOtpCode = (String) redisCache.get("otp:" + token);
            if (storedOtpCode == null) {
                throw new OTPExpiredException("Mã OTP đã hết hạn hoặc không tồn tại");
            }

            // Verify OTP code
            if (!MessageDigest.isEqual(
                    verifyOtpRequest.otp().getBytes("UTF-8"),
                    storedOtpCode.getBytes("UTF-8"))) {

                // Increase failed attempts
                redisCache.save(attemptKey, (attempts == null ? 1 : attempts + 1),
                        300, TimeUnit.SECONDS);
                throw new OTPVerificationException("Mã OTP không chính xác");
            }

            // OTP verified successfully - remove to prevent reuse
            redisCache.delete("otp:" + token);

            return true;
        } catch (Exception e) {
            if (e instanceof OTPException) {
                try {
                    throw e;
                } catch (UnsupportedEncodingException ex) {
                    throw new OTPException("Lỗi encoding: " + ex.getMessage());
                }
            }

            if (e.getMessage().contains("expired")) {
                throw new OTPExpiredException("Token OTP đã hết hạn: " + e.getMessage());
            } else {
                throw new OTPException("Lỗi xác thực OTP: " + e.getMessage());
            }
        }
    }
}
