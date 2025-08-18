package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp không tìm thấy người dùng.
 * Kế thừa từ ApiException, với code, message và status mặc định từ enum Messages (nếu có).
 * Sử dụng: throw new UserNotFoundException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class UserNotFoundException extends ApiException {

    // Mặc định sử dụng giá trị từ enum Messages (giả sử bạn đã có enum như trước)
    private static final String DEFAULT_CODE = "USER_NOT_FOUND";
    private static final String DEFAULT_MESSAGE = "Không tìm thấy người dùng";
    private static final int DEFAULT_STATUS = 404;

    // Constructor không tham số (sử dụng mặc định)
    public UserNotFoundException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh (ví dụ: thêm thông tin cụ thể như userId)
    public UserNotFoundException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}
