package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.data.OTP_Purpose;
import com.exe.carenest.authorizeservice.exception.OTPException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.from.name:Care Nest}")
    private String senderName;

    /**
     * Send password reset OTP email
     */
    public void sendPasswordResetOTP(String toEmail, String otpCode) {
        String subject = "Mã OTP đặt lại mật khẩu - Care Nest";
        String htmlContent = createPasswordResetHtmlContent(toEmail, otpCode);
        sendEmail(toEmail, subject, htmlContent, "password reset OTP");
    }

    /**
     * Send registration verification OTP email
     */
    public void sendRegistrationOTP(String toEmail, String otpCode) {
        String subject = "Xác thực email đăng ký - Care Nest";
        String htmlContent = createRegistrationHtmlContent(toEmail, otpCode);
        sendEmail(toEmail, subject, htmlContent, "registration OTP");
    }

    /**
     * Generic method to send any email
     */
    public void sendCustomEmail(String toEmail, String subject, String htmlContent) {
        sendEmail(toEmail, subject, htmlContent, "custom email");
    }

    /**
     * Send email via SMTP
     */
    private void sendEmail(String toEmail, String subject, String htmlContent, String emailType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set sender
            helper.setFrom("ittrunghoang3715@gmail.com", senderName);

            // Set recipient
            helper.setTo(toEmail);

            // Set subject
            helper.setSubject(subject);

            // Set HTML content
            helper.setText(htmlContent, true);

            // Send email
            mailSender.send(message);

            log.info("Email {} sent successfully to: {}", emailType, toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email {} to {}: {}", emailType, toEmail, e.getMessage(), e);
            throw new OTPException("Lỗi hệ thống khi gửi email " + emailType + ": " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send email {}: {}", emailType, e.getMessage());
            throw new OTPException("Lỗi hệ thống khi gửi email " + emailType + ": " + e.getMessage());
        }
    }

    /**
     * Get email template based on purpose
     *
     * @param otpPurpose OTP purpose (REGISTER or FORGET_PASSWORD)
     * @param toEmail recipient email
     * @param otpCode OTP code to display
     * @return HTML content for email
     */
    public String getTemplate(OTP_Purpose otpPurpose, String toEmail, String otpCode) {
        return switch (otpPurpose) {
            case REGISTER -> createRegistrationHtmlContent(toEmail, otpCode);
            case FORGET_PASSWORD -> createPasswordResetHtmlContent(toEmail, otpCode);
            default -> throw new IllegalArgumentException("Invalid email purpose: " + otpPurpose.name().toLowerCase());
        };
    }

    /**
     * Create HTML content for password reset email
     */
    private String createPasswordResetHtmlContent(String toEmail, String otpCode) {
        return "<!DOCTYPE html>" +
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
                "        <p style='color: #ff6b6b; font-size: 14px;'>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "        <p>Trân trọng,<br>Đội ngũ Care Nest</p>" +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";
    }

    /**
     * Create HTML content for registration email
     */
    private String createRegistrationHtmlContent(String toEmail, String otpCode) {
        return "<!DOCTYPE html>" +
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
                "</html>";
    }
}
