package com.exe.carenest.authorizeservice.authManagement.dto.response;

import com.exe.carenest.authorizeservice.authManagement.model.Role;
import lombok.Builder;


@Builder
public record AccountResponse(
        Long id,
        String username,
        String email,
        Role role,
        boolean isActive
) {}
