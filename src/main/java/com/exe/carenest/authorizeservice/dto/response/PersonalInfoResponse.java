package com.exe.carenest.authorizeservice.dto.response;

import com.exe.carenest.authorizeservice.auth.model.Gender;

import java.sql.Timestamp;

public record PersonalInfoResponse(
    String fullName,
    String email,
    Gender gender,
    Timestamp dateOfBirth,
    String nationality,
    String permanentAddress,
    String homeTown,
    String imgUrl
) {}