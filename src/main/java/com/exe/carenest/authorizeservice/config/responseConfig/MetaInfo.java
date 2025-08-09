package com.exe.carenest.authorizeservice.config.responseConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MetaInfo {
    private String clientIp;
    private String path;
    private String method;
    private String host;
    private String userAgent;
    private String referer;
    private String traceId;
    private String detail;
}
