package com.exe.carenest.authorizeservice.config.responseConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

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

    public static <T> BaseResponse<T> success(String message, int code, MetaInfo meta, String requestId, String version, T data) {
        return new BaseResponse<>("success", code, message, data, meta, requestId, version);
    }

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

    // Utility method for simple success response with data only
    public static <T> BaseResponse<T> of(T data) {
        return new BaseResponse<>("success", 200, "Success", data, null, UUID.randomUUID().toString(), "1.0.0");
    }

    // Utility method for success response with custom message
    public static <T> BaseResponse<T> of(T data, String message) {
        return new BaseResponse<>("success", 200, message, data, null, UUID.randomUUID().toString(), "1.0.0");
    }
}
