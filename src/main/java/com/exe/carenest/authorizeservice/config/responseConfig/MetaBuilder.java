package com.exe.carenest.authorizeservice.config.responseConfig;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class MetaBuilder {

    public MetaInfo fromRequest(HttpServletRequest request) {
        MetaInfo meta = new MetaInfo();
        meta.setClientIp(request.getRemoteAddr());
        meta.setPath(request.getRequestURI());
        meta.setMethod(request.getMethod());
        meta.setHost(request.getServerName());
        meta.setUserAgent(request.getHeader("User-Agent"));
        meta.setReferer(request.getHeader("Referer"));
        meta.setTraceId(request.getHeader("X-Trace-Id"));
        return meta;
    }

    public MetaInfo fromServerRequest(ServerHttpRequest request) {
        MetaInfo meta = new MetaInfo();
        meta.setPath(request.getURI().getPath());
        meta.setMethod(request.getMethod().name());
        meta.setHost(request.getURI().getHost());
        
        // Get headers if available
        if (request.getHeaders() != null) {
            meta.setUserAgent(request.getHeaders().getFirst("User-Agent"));
            meta.setReferer(request.getHeaders().getFirst("Referer"));
            meta.setTraceId(request.getHeaders().getFirst("X-Trace-Id"));
        }
        
        return meta;
    }
}
