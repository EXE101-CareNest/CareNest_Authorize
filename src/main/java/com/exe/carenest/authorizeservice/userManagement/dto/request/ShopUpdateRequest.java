package com.exe.carenest.authorizeservice.userManagement.dto.request;

public record ShopUpdateRequest(
    String shopName,
    String phone,
    String description,
    String status,
    String bankName,
    String bankNum,
    String workingDay,
    String hotline,
    String identityCard
) {}
