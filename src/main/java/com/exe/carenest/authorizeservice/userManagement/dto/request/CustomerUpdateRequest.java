package com.exe.carenest.authorizeservice.userManagement.dto.request;

import java.time.LocalDate;

public record CustomerUpdateRequest(
    String username,
    String phone,
    String gender,
    LocalDate birthday,
    Integer point
) {}
