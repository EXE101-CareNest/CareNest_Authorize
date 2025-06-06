package com.exe.carenest.authorizeservice.config.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseResponse<T> {
    private String status;
    private int code;
    private String message;
    private String timestamp;
    private String requestId;
    private String version;
    private MetaInfo meta;
    private T data;

    // Constructors, Getters/Setters

    public static <T> BaseResponse<T> success(T data, String message, int code, MetaInfo meta, String requestId, String version) {
        return new BaseResponse<>("success", code, message, data, meta, requestId, version);
    }

    public static <T> BaseResponse<T> fail(String message, int code, MetaInfo meta, String requestId, String version) {
        return new BaseResponse<>("fail", code, message, null, meta, requestId, version);
    }

    public static <T> BaseResponse<T> error(String message, int code, MetaInfo meta, String requestId, String version) {
        return new BaseResponse<>("error", code, message, null, meta, requestId, version);
    }

    public BaseResponse(String status, int code, String message, T data, MetaInfo meta, String requestId, String version) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.meta = meta;
        this.requestId = requestId;
        this.version = version;
        this.timestamp = Instant.now().toString();
    }
}
