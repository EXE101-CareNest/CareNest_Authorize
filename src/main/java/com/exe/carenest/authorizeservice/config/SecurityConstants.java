package com.exe.carenest.authorizeservice.config;

import java.util.Arrays;
import java.util.stream.Stream;

public class SecurityConstants {
    // Authentication endpoints
    private static final String[] AUTH_WHITELIST = {
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/verify",
            "/api/auth/verify/otp",
            "/api/auth/newPassword",
            "/api/auth/registerVerifyToken",
            "/api/auth/re-send-otp-code",
    };

    // Account registration endpoints
    private static final String[] ACCOUNT_WHITELIST = {
            "/api/shops/register",
            "/api/accounts/register/customer",
            "/api/shops/information"
    };

    // Permission and email endpoints
    private static final String[] PUBLIC_API_WHITELIST = {
            "/api/permission/**",
            "/email/**"
    };

    // Swagger documentation endpoints
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security",
            "/favicon.ico"
    };

    // Combine all whitelisted URLs
    public static String[] getAllWhitelistedUrls() {
        return Stream.of(AUTH_WHITELIST, ACCOUNT_WHITELIST, PUBLIC_API_WHITELIST, SWAGGER_WHITELIST)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }
}
