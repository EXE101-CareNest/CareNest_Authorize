package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.dto.request.VerifyOtpRequest;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import com.exe.carenest.authorizeservice.service.impl.RedisService;
import com.exe.carenest.authorizeservice.ultil.CryptoHelper;
import com.exe.carenest.authorizeservice.exception.OTPException;
import com.exe.carenest.authorizeservice.exception.OTPExpiredException;
import com.exe.carenest.authorizeservice.exception.OTPVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OTPService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.api.url}")
    private String apiUrl;

    private final int otpExpiredTime = 300; // 5 minutes in seconds

    private final RedisService redisCache;

    private final CryptoHelper helper;

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    public String sendOtp(String toEmail) {
        // Validate input
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new OTPException("Email không được để trống");
        }
        
        // Basic email format validation
        if (!toEmail.contains("@") || !toEmail.contains(".")) {
            throw new OTPException("Định dạng email không hợp lệ");
        }

        // Note: Removed email existence check to support both registration and forgot password flows
        // For registration: email should not exist yet
        // For forgot password: email should exist
        // We'll let the calling service handle email existence validation
        
        try {
            String otpCode = helper.generateOtp();
            RestTemplate restTemplate = new RestTemplate();

            // Body JSON
            Map<String, Object> body = new HashMap<>();
            Map<String, String> sender = Map.of(
                    "name", "Care Nest Support",
                    "email", "trungksdoa@gmail.com"
            );
            Map<String, String> to = Map.of(
                    "email", toEmail,
                    "name", toEmail
            );
            body.put("sender", sender);
            body.put("to", List.of(to));
            body.put("subject", "Mã OTP đặt lại mật khẩu - Care Nest");
            body.put("htmlContent",
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<title>Mã OTP Care Nest</title>" +
                            "</head>" +
                            "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                            "  <table align='center' cellpadding='0' cellspacing='0' style='max-width: 500px; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                            "    <tr>" +
                            "      <td style='background-color: #4CAF50; color: white; text-align: center; padding: 16px 0; font-size: 20px; font-weight: bold;'>Mã OTP Care Nest</td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding: 20px; color: #333333; font-size: 16px;'>" +
                            "        <p>Xin chào " + toEmail + ",</p>" +
                            "        <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Care Nest. Mã OTP của bạn là:</p>" +
                            "        <p style='text-align: center; margin: 30px 0;'>" +
                            "          <span style='display: inline-block; padding: 10px 20px; font-size: 24px; letter-spacing: 4px; color: #ffffff; background-color: #4CAF50; border-radius: 6px;'>" + otpCode + "</span>" +
                            "        </p>" +
                            "        <p style='color: #666666; font-size: 14px;'>Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                            "        <p style='color: #ff6b6b; font-size: 14px;'>Nếu bạn không yêu cầu đặt lại mật khẩu hay khác, vui lòng bỏ qua email này.</p>" +
                            "        <p>Trân trọng,<br>Đội ngũ Care Nest</p>" +
                            "      </td>" +
                            "    </tr>" +
                            "  </table>" +
                            "</body>" +
                            "</html>"
            );

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // Call API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // Check if email sent successfully
            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
                throw new OTPException("Không thể gửi email OTP. Vui lòng thử lại sau");
            }
            
            long expirationMillis = 5 * 60 * 1000; // 5 phút = 300000 mili giây

            String otpToken = jwtProvider.generateTokenByEmail(toEmail, new Date(System.currentTimeMillis() + expirationMillis));
            redisCache.save("otp:" + otpToken, otpCode, otpExpiredTime, TimeUnit.SECONDS);
            
            // Log for debugging (remove in production)
            System.out.println("OTP sent to: " + toEmail + ", token: " + otpToken);
            return otpToken;
        } catch (Exception e) {
            if (e instanceof OTPException) {
                throw e;
            }
            throw new OTPException("Lỗi hệ thống khi gửi OTP: " + e.getMessage());
        }
    }

    public boolean isOtpExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new OTPException("Token OTP không được để trống");
        }
        
        try {
            // Check if token is valid first
            if (!jwtProvider.validateToken(token)) {
                return true; // Consider invalid token as expired
            }
            
            // Check if OTP exists in Redis
            String otpCode = (String) redisCache.get("otp:" + token);
            return otpCode == null; // If null, means expired or not found
        } catch (Exception e) {
            // If any error occurs during validation, consider as expired
            return true;
        }
    }

    public boolean verifyOTP(String token, VerifyOtpRequest verifyOtpRequest) {
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            throw new OTPException("Token OTP không được để trống");
        }
        
        if (verifyOtpRequest == null || verifyOtpRequest.otp().trim().isEmpty()) {
            throw new OTPVerificationException("Mã OTP không được để trống");
        }
        
        // Validate OTP format (should be 6 digits)
        if (!verifyOtpRequest.otp().matches("\\d{6}")) {
            throw new OTPVerificationException("Mã OTP phải là 6 chữ số");
        }
        
        try {
            // Validate JWT token first
            if (!jwtProvider.validateToken(token)) {
                throw new OTPExpiredException("Token OTP không hợp lệ hoặc đã hết hạn");
            }
            
            // Get OTP from Redis
            String storedOtpCode = (String) redisCache.get("otp:" + token);
            if (storedOtpCode == null) {
                throw new OTPExpiredException("Mã OTP đã hết hạn hoặc không tồn tại");
            }

            //Check email that correct with saving as subject
            String  emailSubject = jwtProvider.getSubject(token);

            if(!emailSubject.equals(verifyOtpRequest.email())){
                throw   new OTPException("Something wrong");
            }


            // Verify OTP code
            if (!verifyOtpRequest.otp().equals(storedOtpCode)) {
                throw new OTPVerificationException("Mã OTP không chính xác");
            }
            
            // OTP verified successfully - remove it from Redis to prevent reuse
            redisCache.delete("otp:" + token);
            
            return true;
        } catch (Exception e) {
            if (e instanceof OTPException) {
                throw e; // Re-throw custom exceptions
            }
            
            // Handle JWT validation exceptions
            if (e.getMessage().contains("expired")) {
                throw new OTPExpiredException("Token OTP đã hết hạn: " + e.getMessage());
            } else {
                throw new OTPException("Lỗi xác thực OTP: " + e.getMessage());
            }
        }
    }

    public String sendRegistrationOtp(String toEmail) {
        // Validate input
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new OTPException("Email không được để trống");
        }
        
        // Basic email format validation
        if (!toEmail.contains("@") || !toEmail.contains(".")) {
            throw new OTPException("Định dạng email không hợp lệ");
        }
        
//        // Check if email already exists for registration
//        if(userRepository.existsByEmail(toEmail)){
//            throw new OTPException("Email đã tồn tại trong hệ thống");
//        }
        
        try {
            String otpCode = helper.generateOtp();
            RestTemplate restTemplate = new RestTemplate();

            // Body JSON for registration verification
            Map<String, Object> body = new HashMap<>();
            Map<String, String> sender = Map.of(
                    "name", "Care Nest Support",
                    "email", "trungksdoa@gmail.com"
            );
            Map<String, String> to = Map.of(
                    "email", toEmail,
                    "name", toEmail
            );
            body.put("sender", sender);
            body.put("to", List.of(to));
            body.put("subject", "Xác thực email đăng ký - Care Nest");
            body.put("htmlContent",
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<title>Xác thực Email - Care Nest</title>" +
                            "</head>" +
                            "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                            "  <table align='center' cellpadding='0' cellspacing='0' style='max-width: 500px; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                            "    <tr>" +
                            "      <td style='background-color: #2196F3; color: white; text-align: center; padding: 16px 0; font-size: 20px; font-weight: bold;'>Xác thực Email - Care Nest</td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding: 20px; color: #333333; font-size: 16px;'>" +
                            "        <p>Xin chào " + toEmail + ",</p>" +
                            "        <p>Chào mừng bạn đến với Care Nest! Để hoàn tất quá trình đăng ký, vui lòng xác thực email của bạn bằng mã OTP sau:</p>" +
                            "        <p style='text-align: center; margin: 30px 0;'>" +
                            "          <span style='display: inline-block; padding: 10px 20px; font-size: 24px; letter-spacing: 4px; color: #ffffff; background-color: #2196F3; border-radius: 6px;'>" + otpCode + "</span>" +
                            "        </p>" +
                            "        <p style='color: #666666; font-size: 14px;'>Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                            "        <p style='color: #4CAF50; font-size: 14px;'>Sau khi xác thực thành công, tài khoản của bạn sẽ được kích hoạt và bạn có thể đăng nhập vào hệ thống.</p>" +
                            "        <p>Trân trọng,<br>Đội ngũ Care Nest</p>" +
                            "      </td>" +
                            "    </tr>" +
                            "  </table>" +
                            "</body>" +
                            "</html>"
            );

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // Call API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // Check if email sent successfully
            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
                throw new OTPException("Không thể gửi email OTP. Vui lòng thử lại sau");
            }
            
            long expirationMillis = 5 * 60 * 1000; // 5 phút = 300000 mili giây

            String otpToken = jwtProvider.generateTokenByEmail(toEmail, new Date(System.currentTimeMillis() + expirationMillis));
            redisCache.save("otp:" + otpToken, otpCode, otpExpiredTime, TimeUnit.SECONDS);
            
            // Log for debugging (remove in production)
            System.out.println("Registration OTP sent to: " + toEmail + ", token: " + otpToken);
            return otpToken;
        } catch (Exception e) {
            if (e instanceof OTPException) {
                throw e;
            }
            throw new OTPException("Lỗi hệ thống khi gửi OTP xác thực email: " + e.getMessage());
        }
    }

}
