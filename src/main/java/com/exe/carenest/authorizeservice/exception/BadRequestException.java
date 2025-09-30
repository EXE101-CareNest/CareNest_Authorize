package com.exe.carenest.authorizeservice.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super("BAD_REQUEST", message, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestException(String message, String detail) {
        super("BAD_REQUEST", message, detail, HttpStatus.BAD_REQUEST.value());
    }
}