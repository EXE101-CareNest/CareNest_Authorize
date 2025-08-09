package com.exe.carenest.authorizeservice.dto.response;

import com.exe.carenest.authorizeservice.auth.model.Roles;
import lombok.Builder;


@Builder
public record AccountResponse(
        Long id,
        String username,
        String email,
        Roles role,
        boolean isActive
) {}
