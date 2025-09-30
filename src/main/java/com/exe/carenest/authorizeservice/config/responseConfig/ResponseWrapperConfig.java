package com.exe.carenest.authorizeservice.config.responseConfig;

import com.exe.carenest.authorizeservice.config.SkipWrap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.UUID;

@ControllerAdvice
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.response.auto-wrap", havingValue = "true", matchIfMissing = true)
public class ResponseWrapperConfig implements ResponseBodyAdvice<Object> {

    private final MetaBuilder metaBuilder;

    @Value("${app.version:1.0.0}")
    private String version;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Skip wrapping if method is annotated with @SkipWrap
        if (returnType.hasMethodAnnotation(SkipWrap.class)) {
            return false;
        }

        // Skip wrapping if return type is already BaseResponse
        if (returnType.getParameterType().equals(BaseResponse.class)) {
            return false;
        }

        // Skip wrapping for Swagger/OpenAPI endpoints
        String methodName = returnType.getMethod().getName();
        if (methodName.contains("swagger") || methodName.contains("api-docs") || methodName.contains("openapi")) {
            return false;
        }



        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse serverHttpResponse) {

        // Create meta information from request
        MetaInfo meta = null;
        try {
            // Try to create meta info if MetaBuilder is available
            if (metaBuilder != null) {
                meta = metaBuilder.fromServerRequest(request);
            }
        } catch (Exception e) {
            // If meta creation fails, create minimal meta
            meta = new MetaInfo();
            meta.setMethod(request.getMethod().name());
            meta.setPath(request.getURI().getPath());
        }

        // Handle different response scenarios
        if (body == null) {
            return BaseResponse.success(
                    null,
                    "Success",
                    200,
                    meta,
                    UUID.randomUUID().toString(),
                    version
            );
        }

        // Handle String responses (like success messages)
        if (body instanceof String) {
            String message = (String) body;
            return BaseResponse.success(
                    null,
                    message,
                    200,
                    meta,
                    UUID.randomUUID().toString(),
                    version
            );
        }

        // Handle all other objects as data
        return BaseResponse.success(
                body,
                "Success",
                200,
                meta,
                UUID.randomUUID().toString(),
                version
        );
    }
}