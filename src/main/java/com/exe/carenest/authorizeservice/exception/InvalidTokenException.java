package com.exe.carenest.authorizeservice.exception;

import com.exe.carenest.authorizeservice.ultil.Messages;
import lombok.Getter;

/**
 * Custom exception cho token JWT không hợp lệ.
 */
@Getter
public class InvalidTokenException extends ApiException {

    private static final String DEFAULT_CODE = Messages.INVALID_TOKEN.getCode();
    private static final String DEFAULT_MESSAGE = Messages.INVALID_TOKEN.getMessage();
    private static final int DEFAULT_STATUS = Messages.INVALID_TOKEN.getStatus();

    public InvalidTokenException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    public InvalidTokenException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}
