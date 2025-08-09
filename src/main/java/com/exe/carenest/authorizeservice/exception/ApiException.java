package com.exe.carenest.authorizeservice.exception;

public class ApiException extends RuntimeException {
    private final String code;
    private final String message;
    private final String detail;
    private final int status;

    public ApiException(String code, String message, String detail, int status) {
        super(detail);
        this.code = code;
        this.message = message;
        this.detail = detail;
        this.status = status;
    }

    public ApiException(String code, String message, int status) {
        this(code, message, message, status);
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getDetail() { return detail; }
    public int getStatus() { return status; }
}
