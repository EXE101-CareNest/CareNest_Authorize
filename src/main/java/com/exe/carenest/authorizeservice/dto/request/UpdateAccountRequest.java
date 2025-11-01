package com.exe.carenest.authorizeservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {
    private String fullName;
    private String email;
    private String gender; // Male, Female, Other
    private Timestamp dateOfBirth;
    private String nationality;
    private String permanentAddress;
    private String homeTown;
    private String issuedDate;
    private String issuedBy;
    private String imgUrl;
    private Boolean status; // true or false
}