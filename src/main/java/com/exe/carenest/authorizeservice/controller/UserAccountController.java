package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.dto.request.NewPasswordRequest;
import com.exe.carenest.authorizeservice.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.OTPService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "User Account Management", description = "APIs for user account actions")
public class UserAccountController {
    private final IAccountService accountService;
    private final OTPService otpService;

    public UserAccountController(IAccountService accountService, OTPService otpService) {
        this.accountService = accountService;
        this.otpService = otpService;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<Void> createCustomerAccount(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        accountService.createAccount(request, "ROLE_USER");
        String otpToken = otpService.sendRegistrationOtp(request.email());
        response.setHeader("X-Key-APT", otpToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<AccountResponse> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(accountService.findByUsernameResponse(username));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        accountService.updatePassword(newPasswordRequest.email(), newPasswordRequest.password());
        return ResponseEntity.ok().build();
    }
}
