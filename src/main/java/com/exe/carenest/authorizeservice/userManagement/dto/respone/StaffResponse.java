package com.exe.carenest.authorizeservice.userManagement.dto.respone;

import com.exe.carenest.authorizeservice.authManagement.model.Role;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StaffResponse(
        Long id,
        String username,
        String email,
        Role role,
        String gender,
        String phone,
        String position,
        LocalDate birthday,
        LocalDate hiredAt,
        String shopAddress,
        Long platformServiceId,
        Long shopId
) {}