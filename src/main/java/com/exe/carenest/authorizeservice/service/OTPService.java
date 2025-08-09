package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.service.impl.RedisService;
import com.exe.carenest.authorizeservice.ultil.CryptoHelper;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OTPService {

    @Value("${spring.brevo.api.key}")
    private String apiKey;

    @Value("${spring.brevo.api.url}")
    private String apiUrl;

    private final int otpExpiredTime = 300; // 5 minutes in seconds

    private final RedisService redisCache;

    private final CryptoHelper helper;

    private final JwtProvider jwtProvider;

    public String sendOtp(String toEmail) {
        String otpCode = helper.generateOtp();
        RestTemplate restTemplate = new RestTemplate();

        // Body JSON
        Map<String, Object> body = new HashMap<>();
        Map<String, String> sender = Map.of(
                "name", "My App",
                "email", "trungksdoa@gmail.com"
        );
        Map<String, String> to = Map.of(
                "email", toEmail,
                "name", toEmail
        );
        body.put("sender", sender);
        body.put("to", List.of(to));
        body.put("subject", "Your OTP Code");
        body.put("htmlContent",
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>Your OTP Code</title>" +
                        "</head>" +
                        "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                        "  <table align='center' cellpadding='0' cellspacing='0' style='max-width: 500px; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                        "    <tr>" +
                        "      <td style='background-color: #4CAF50; color: white; text-align: center; padding: 16px 0; font-size: 20px; font-weight: bold;'>Your OTP Code</td>" +
                        "    </tr>" +
                        "    <tr>" +
                        "      <td style='padding: 20px; color: #333333; font-size: 16px;'>" + toEmail +
                        "        <p>Xin chào,</p>" +
                        "        <p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Mã OTP của bạn là:</p>" +
                        "        <p style='text-align: center; margin: 30px 0;'>" +
                        "          <span style='display: inline-block; padding: 10px 20px; font-size: 24px; letter-spacing: 4px; color: #ffffff; background-color: #4CAF50; border-radius: 6px;'>" + otpCode + "</span>" +
                        "        </p>" +
                        "        <p style='color: #666666; font-size: 14px;'>Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                        "        <p>Trân trọng,<br>Đội ngũ hỗ trợ</p>" +
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
        long expirationMillis = 5 * 60 * 1000; // 5 phút = 300000 mili giây

        String otpToken = jwtProvider.generateTokenByEmail(toEmail, new Date(System.currentTimeMillis() + expirationMillis));
        redisCache.save("otp:" + otpToken, otpCode, otpExpiredTime, TimeUnit.SECONDS);
        System.out.println("OTP token: " + otpToken);
        return otpToken;
    }

    public boolean isOtpExpired(String email) {
        return redisCache.isExpired("otp:" + email);
    }

    public boolean verifyOTP(String token,String otpCode) {
        jwtProvider.validateToken(token);
        String otpToken = (String) redisCache.get("otp:" + token);
        if(otpToken == null){
            throw new BadRequestException("OTP token is expired");
        }

        return otpCode.equals(otpToken);
    }

}
