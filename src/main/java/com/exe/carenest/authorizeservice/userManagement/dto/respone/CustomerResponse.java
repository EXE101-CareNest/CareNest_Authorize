package com.exe.carenest.authorizeservice.userManagement.dto.respone;

import com.exe.carenest.authorizeservice.authManagement.model.Role;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerResponse(
        Long id,
        String username,
        String email,
        Role role,
        String gender,
        String phone,
        LocalDate birthday,
        Integer point,
        Long platformServiceId
) {}