package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;

/**
 * Custom exception cho token JWT hết hạn.
 */
@Getter
public class ExpiredTokenException extends ApiException {

    private static final String DEFAULT_CODE = "EXPIRED_TOKEN";  // Thêm vào enum Messages nếu cần
    private static final String DEFAULT_MESSAGE = "Token đã hết hạn";
    private static final int DEFAULT_STATUS = 401;

    public ExpiredTokenException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    public ExpiredTokenException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}
