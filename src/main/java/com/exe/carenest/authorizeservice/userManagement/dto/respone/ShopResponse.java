package com.exe.carenest.authorizeservice.userManagement.dto.respone;


import com.exe.carenest.authorizeservice.authManagement.model.Role;
import lombok.Builder;


@Builder
public record ShopResponse(
        Long id,
        String username,
        String email,
        Role role,
        String shopName,
        String description,
        String phone,
        String status,
        String bankName,
        String bankNum,
        String workingDay,
        String hotline,
        String identityCard
) {}