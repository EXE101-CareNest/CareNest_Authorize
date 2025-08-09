package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.config.SkipWrap;
import com.exe.carenest.authorizeservice.dto.request.VerifyOtpRequest;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final OTPService otpService;
    private final IAccountService  accountService;

    @GetMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email, HttpServletResponse response) {
        response.setHeader("X-Key-APT", otpService.sendOtp(email));
        return ResponseEntity.ok("Forgot password reset successfully");
    }


    @GetMapping("/check-otp")
    @SkipWrap
    public boolean checkOtp(@RequestParam String email) {
       return otpService.isOtpExpired(email);
    }


    @PostMapping("/verify-otp")
    @SkipWrap
    public ResponseEntity<String> verify(@RequestHeader("X-Key-APT") String token, @RequestBody
    VerifyOtpRequest otpRequest) {
        if( otpService.verifyOTP(token, otpRequest.otp())){
            return ResponseEntity.ok("Correct otp");
        }


        return ResponseEntity.badRequest().body("OTP is incorrect");
    }
}
