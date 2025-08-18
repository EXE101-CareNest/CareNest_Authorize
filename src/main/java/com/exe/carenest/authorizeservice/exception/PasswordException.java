package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho các lỗi liên quan đến mật khẩu (sai, không khớp, không hợp lệ).
 * Kế thừa từ ApiException, với code, message và status mặc định từ enum Messages (nếu có).
 * Sử dụng: throw new PasswordException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class PasswordException extends ApiException {

    // Mặc định sử dụng giá trị từ enum Messages (giả sử bạn đã có enum như trước)
    private static final String DEFAULT_CODE = "INVALID_PASSWORD";
    private static final String DEFAULT_MESSAGE = "Mật khẩu không hợp lệ hoặc sai";
    private static final int DEFAULT_STATUS = 400;

    // Constructor không tham số (sử dụng mặc định)
    public PasswordException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh (ví dụ: "Mật khẩu nhập lại không khớp")
    public PasswordException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }

    // Constructor tùy chỉnh code/message nếu cần (ví dụ cho trường hợp không khớp cụ thể)
    public PasswordException(String code, String message, String detail, int status) {
        super(code, message, detail, status);
    }
}
