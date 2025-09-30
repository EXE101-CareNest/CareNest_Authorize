package com.exe.carenest.authorizeservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ApiExceptionWithHeaders extends ApiException {
    private final Map<String, String> headers;

    public ApiExceptionWithHeaders(String code, String message, String detail, int status, Map<String, String> headers) {
        super(code, message, detail, status);
        this.headers = headers;
    }

    public ApiExceptionWithHeaders(String message, Map<String, String> headers) {
        super("SUCCESS", message, HttpStatus.OK.value());
        this.headers = headers;
    }
}