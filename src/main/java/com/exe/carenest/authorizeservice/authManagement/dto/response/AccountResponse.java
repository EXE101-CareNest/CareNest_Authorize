package com.exe.carenest.authorizeservice.authManagement.dto.response;

import com.exe.carenest.authorizeservice.authManagement.model.Roles;
import lombok.Builder;


@Builder
public record AccountResponse(
        Long id,
        String username,
        String email,
        Roles role,
        boolean isActive
) {}
