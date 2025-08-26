package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp lỗi trong quá trình đăng ký shop.
 * Kế thừa từ ApiException, với code, message và status mặc định.
 * Sử dụng: throw new ShopRegistrationException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class ShopRegistrationException extends ApiException {

    private static final String DEFAULT_CODE = "SHOP_REGISTRATION_FAILED";
    private static final String DEFAULT_MESSAGE = "Đăng ký cửa hàng thất bại";
    private static final int DEFAULT_STATUS = 400;

    // Constructor không tham số (sử dụng mặc định)
    public ShopRegistrationException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh
    public ShopRegistrationException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}