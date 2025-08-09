package com.exe.carenest.authorizeservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record NewPasswordRequest(
        @Email
        @NotNull
        String email,
        String password,
        String reEnterPassword) {
}
