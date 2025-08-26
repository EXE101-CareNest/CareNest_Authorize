package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp OTP đã hết hạn.
 * Kế thừa từ OTPException với thông tin cụ thể cho expired OTP.
 */
@Getter
public class OTPExpiredException extends OTPException {

    private static final String DEFAULT_CODE = "OTP_EXPIRED";
    private static final String DEFAULT_MESSAGE = "Mã OTP đã hết hạn";
    private static final int DEFAULT_STATUS = 410; // Gone - resource expired

    // Constructor không tham số (sử dụng mặc định)
    public OTPExpiredException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, null, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh
    public OTPExpiredException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}