package com.exe.carenest.authorizeservice.infrastructure.exception;

import com.exe.carenest.authorizeservice.infrastructure.responseConfig.BaseResponse;
import com.exe.carenest.authorizeservice.infrastructure.responseConfig.MetaInfo;
import com.exe.carenest.authorizeservice.infrastructure.responseConfig.MetaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MetaBuilder metaBuilder;

    @Value("${app.version:1.0.0}")
    private String version;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<?>> handleApiException(ApiException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail(ex.getDetail());
        meta.setTraceId(ex.getCode());

        log.error("ApiException traceId={} message={}", ex.getCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatus())
                .body(BaseResponse.fail(
                        ex.getMessage(),
                        ex.getStatus(),
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail("Validation error: " + errors.toString());
        meta.setTraceId("VALIDATION_FAILED");

        log.error("Validation failed traceId=VALIDATION_FAILED errors={}", errors, ex);

        return ResponseEntity
                .status(422)
                .body(BaseResponse.fail(
                        "Validation Error",
                        422,
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail("Method not allowed: " + ex.getMethod());
        meta.setTraceId("METHOD_NOT_ALLOWED");

        log.error("Method not allowed traceId=METHOD_NOT_ALLOWED method={}", ex.getMethod(), ex);

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.fail(
                        "Method Not Allowed",
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }

    // Bổ sung xử lý AccessDeniedException (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<?>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail("Access denied");
        meta.setTraceId(UUID.randomUUID().toString());

        log.error("Access denied traceId={} message={}", meta.getTraceId(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.fail(
                        "Forbidden",
                        HttpStatus.FORBIDDEN.value(),
                        meta,
                        meta.getTraceId(),
                        version
                ));
    }

    // Xử lý HttpMediaTypeNotSupportedException (415 Unsupported Media Type)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<?>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail("Unsupported media type: " + ex.getContentType());
        meta.setTraceId(UUID.randomUUID().toString());

        log.error("Unsupported media type traceId={} mediaType={}", meta.getTraceId(), ex.getContentType(), ex);

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(BaseResponse.fail(
                        "Unsupported Media Type",
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        meta,
                        meta.getTraceId(),
                        version
                ));
    }

    // Xử lý ResponseStatusException (được ném trong controller để trả status code động)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseResponse<?>> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail(ex.getReason());
        meta.setTraceId(UUID.randomUUID().toString());

        log.error("ResponseStatusException traceId={} status={} reason={}", meta.getTraceId(), ex.getStatusCode(), ex.getReason(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(BaseResponse.fail(
                        ex.getReason(),
                        ex.getStatusCode().value(),
                        meta,
                        meta.getTraceId(),
                        version
                ));
    }

    // Bắt chung tất cả các Exception còn lại
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleUnknown(Exception ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail(ex.getMessage());
        meta.setTraceId(UUID.randomUUID().toString());

        log.error("Internal server error traceId={} message={}", meta.getTraceId(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(
                        "Internal Server Error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        meta,
                        meta.getTraceId(),
                        version
                ));
    }
}
