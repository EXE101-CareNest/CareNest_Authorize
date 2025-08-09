package com.exe.carenest.authorizeservice.dto.response;


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