package com.exe.carenest.authorizeservice.dto.response;

public record VerifyOtpResponse(boolean success, String message, String accessToken, Integer remainingAttempts) {}