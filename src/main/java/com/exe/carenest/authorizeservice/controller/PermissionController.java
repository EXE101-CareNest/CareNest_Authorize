package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.dto.response.PermissionCheckResponse;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor

public class PermissionController {

    private final IRolePermissionService rolePermissionService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final IAccountService accountService;

    @PostMapping("/check")
    public ResponseEntity<PermissionCheckResponse> checkUrl( @RequestParam("path") String path, @RequestParam("httpMethod") String httpMethod) {

        HttpPermission requiredPermission = getRequiredPermission(httpMethod);


        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().body(new PermissionCheckResponse(false));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(new PermissionCheckResponse(false));
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        String userRole = authorities.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("");


        boolean isAllowed = rolePermissionService.checkPermission(path, requiredPermission, userRole);


        return ResponseEntity.ok(new PermissionCheckResponse(isAllowed));
    }





    private boolean matches(String pattern, String url) {
        if (pattern == null || pattern.isBlank()) return false;
        // Normalize: ensure leading slash
        String p = pattern.startsWith("/") ? pattern : "/" + pattern;
        String u = url.startsWith("/") ? url : "/" + url;
        return antPathMatcher.match(p, u);
    }

    private HttpPermission getRequiredPermission(String httpMethod) {
        switch (httpMethod.toUpperCase()) {
            case "GET": return HttpPermission.READ;
            case "POST": return HttpPermission.CREATE;
            case "PUT":
            case "PATCH": return HttpPermission.UPDATE;
            case "DELETE": return HttpPermission.DELETE;
            default: return HttpPermission.READ;
        }
    }
}
