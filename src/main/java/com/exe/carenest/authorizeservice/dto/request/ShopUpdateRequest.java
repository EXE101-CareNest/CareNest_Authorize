package com.exe.carenest.authorizeservice.dto.request;

public record ShopUpdateRequest(
    String shopName,
    String description,
    boolean status,
    String workingDays,
    String imgUrl,
    String password
) {}
