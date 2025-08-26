package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp thông tin đăng nhập shop không đúng.
 * Kế thừa từ ApiException, với code, message và status mặc định.
 * Sử dụng: throw new InvalidShopCredentialsException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class InvalidShopCredentialsException extends ApiException {

    private static final String DEFAULT_CODE = "INVALID_SHOP_CREDENTIALS";
    private static final String DEFAULT_MESSAGE = "Thông tin đăng nhập cửa hàng không đúng";
    private static final int DEFAULT_STATUS = 401;

    // Constructor không tham số (sử dụng mặc định)
    public InvalidShopCredentialsException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh
    public InvalidShopCredentialsException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}