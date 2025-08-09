package com.exe.carenest.authorizeservice.config.responseConfig;


import jakarta.servlet.http.HttpServletRequest;
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
}
