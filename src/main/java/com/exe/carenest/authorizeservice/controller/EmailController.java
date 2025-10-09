package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.config.SkipWrap;
import com.exe.carenest.authorizeservice.dto.request.VerifyOtpRequest;
import com.exe.carenest.authorizeservice.exception.BadRequestException;
import com.exe.carenest.authorizeservice.service.EmailService;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EmailController {

    private final OTPService otpService;
    private final EmailService emailService;
    private final IAccountService accountService;

//    @GetMapping("/send-otp")
//    public String sendOtp(@RequestParam String email, HttpServletResponse response) {
//        response.setHeader("X-Key-APT", otpService.sendOtp(email));
//        return "Forgot password reset successfully";
//    }


    @GetMapping("/check-otp")
    @SkipWrap
    public boolean checkOtp(@RequestParam String email) {
       return otpService.isOtpExpired(email);
    }


    @PostMapping("/verify-otp")
    @SkipWrap
    public String verify(@RequestHeader("X-Key-APT") String token, @RequestBody
    VerifyOtpRequest otpRequest) {
        if( otpService.verifyOTP(token, otpRequest)){
            return "Correct otp";
        }


        throw new BadRequestException("OTP is incorrect");
    }

    @PostMapping("/send-mail")
    @SkipWrap
    public String sendMail(@RequestParam("userId") String userId, @RequestParam("subject") String subject, @RequestBody String htmlBody){
        String email = accountService.findByIdAccount(userId).getEmail();
        emailService.sendCustomEmail(email, subject, htmlBody);

        return "Chuyển mail thành con ông";
    }
}
