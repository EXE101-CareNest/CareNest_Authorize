package com.exe.carenest.authorizeservice.userManagement.dto.request;

import java.time.LocalDate;

public record StaffUpdateRequest(
    String username,
    String phone,
    String gender,
    String position,
    LocalDate birthday,
    String shopAddress,
    Long platformServiceId
) {}
