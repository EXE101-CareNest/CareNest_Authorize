package com.exe.carenest.authorizeservice.exception;

import com.exe.carenest.authorizeservice.ultil.Messages;
import lombok.Getter;

/**
 * Custom exception cho trường hợp không được ủy quyền (token hợp lệ nhưng thiếu quyền).
 */
@Getter
public class UnauthorizedException extends ApiException {

    private static final String DEFAULT_CODE = Messages.UNAUTHORIZED.getCode();
    private static final String DEFAULT_MESSAGE = Messages.UNAUTHORIZED.getMessage();
    private static final int DEFAULT_STATUS = Messages.UNAUTHORIZED.getStatus();

    public UnauthorizedException() {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_STATUS);
    }

    public UnauthorizedException(String detail) {
        super(DEFAULT_CODE, DEFAULT_MESSAGE, detail, DEFAULT_STATUS);
    }
}
