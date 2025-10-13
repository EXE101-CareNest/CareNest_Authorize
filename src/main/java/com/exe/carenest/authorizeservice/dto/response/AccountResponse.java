package com.exe.carenest.authorizeservice.dto.response;

import lombok.Builder;


@Builder
public record AccountResponse(
        String id,
        String username,
        String fullName,
        String email,
        String role,
        boolean isActive,
        String address
) {}
