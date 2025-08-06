package com.exe.carenest.authorizeservice.userManagement.dto.respone;


import com.exe.carenest.authorizeservice.authManagement.model.Roles;
import lombok.Builder;


@Builder
public record ShopResponse(
        Long id,
        String shopName,
        String owner,
        String description,
        boolean status,
        String workingDay,
        String workingDays,
        String imgUrl
) {}