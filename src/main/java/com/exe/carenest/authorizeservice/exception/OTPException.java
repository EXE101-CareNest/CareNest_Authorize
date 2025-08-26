package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho các lỗi liên quan đến OTP operations.
 * Kế thừa từ ApiException, với code, message và status mặc định.
 * Sử dụng: throw new OTPException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class OTPException extends ApiException {

    private static final String DEFAULT_CODE = "OTP_ERROR";
    private static final String DEFAULT_MESSAGE = "Lỗi xử lý OTP";
    private static final int DEFAULT_STATUS = 400;

    // Constructor không tham số (sử dụng mặc định)
    public OTPException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh
    public OTPException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }

    // Constructor với code và message tùy chỉnh
    public OTPException(String code, String message, String detail, int status) {
        super(code, message, detail, status);
    }
}