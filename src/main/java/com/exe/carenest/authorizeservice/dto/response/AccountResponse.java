package com.exe.carenest.authorizeservice.dto.response;

import lombok.Builder;


@Builder
public record AccountResponse(
        String id,
        String username,
        String email,
        String role,
        boolean isActive
) {}
