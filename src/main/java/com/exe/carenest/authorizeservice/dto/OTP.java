package com.exe.carenest.authorizeservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTP {
    private String email;
    private String otp;
    private int expireTime;
}
