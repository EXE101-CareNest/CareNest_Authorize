package com.exe.carenest.authorizeservice.dto.request;

public record NewPasswordRequest(
        String password,
        String reEnterPassword) {
}
