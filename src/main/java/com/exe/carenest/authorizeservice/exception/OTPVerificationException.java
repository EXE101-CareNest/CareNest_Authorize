package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp OTP verification thất bại.
 * Kế thừa từ OTPException với thông tin cụ thể cho verification failed.
 */
@Getter
public class OTPVerificationException extends OTPException {

    private static final String DEFAULT_CODE = "OTP_VERIFICATION_FAILED";
    private static final String DEFAULT_MESSAGE = "Xác thực mã OTP thất bại";
    private static final int DEFAULT_STATUS = 400; // Bad Request

    // Constructor không tham số (sử dụng mặc định)
    public OTPVerificationException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, null, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh
    public OTPVerificationException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}