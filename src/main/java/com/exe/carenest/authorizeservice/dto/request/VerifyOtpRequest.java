package com.exe.carenest.authorizeservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(
        @Email @NotBlank String email,
        @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String otp
) {}
