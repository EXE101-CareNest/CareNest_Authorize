package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp không tìm thấy shop.
 * Kế thừa từ ApiException, với code, message và status mặc định.
 * Sử dụng: throw new ShopNotFoundException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class ShopNotFoundException extends ApiException {

    private static final String DEFAULT_CODE = "SHOP_NOT_FOUND";
    private static final String DEFAULT_MESSAGE = "Không tìm thấy cửa hàng";
    private static final int DEFAULT_STATUS = 404;

    // Constructor không tham số (sử dụng mặc định)
    public ShopNotFoundException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh (ví dụ: thêm thông tin cụ thể như shopId)
    public ShopNotFoundException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}