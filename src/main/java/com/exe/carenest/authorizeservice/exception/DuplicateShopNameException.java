package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho trường hợp tên shop đã tồn tại.
 * Kế thừa từ ApiException, với code, message và status mặc định.
 * Sử dụng: throw new DuplicateShopNameException(); hoặc với detail tùy chỉnh.
 */
@Getter
public class DuplicateShopNameException extends ApiException {

    private static final String DEFAULT_CODE = "DUPLICATE_SHOP_NAME";
    private static final String DEFAULT_MESSAGE = "Tên cửa hàng đã tồn tại";
    private static final int DEFAULT_STATUS = 409;

    // Constructor không tham số (sử dụng mặc định)
    public DuplicateShopNameException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    // Constructor với detail tùy chỉnh
    public DuplicateShopNameException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}