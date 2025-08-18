package com.exe.carenest.authorizeservice.dto.request;

import com.exe.carenest.authorizeservice.auth.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Username không được để trống") String username,
        @NotBlank(message = "Full name không được để trống") String fullName,
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Birthday phải định dạng yyyy-MM-dd") String birthday,
        Gender gender,
        @Email(message = "Email không hợp lệ") String email,
        @NotBlank(message = "Password không được để trống") String password,
        @NotBlank(message = "Re-enter password không được để trống") String reEnterPassword
) {}