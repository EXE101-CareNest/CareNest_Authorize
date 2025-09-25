package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.config.annotation.AllowAllRoles;
import com.exe.carenest.authorizeservice.dto.request.PermissionCheckRequest;
import com.exe.carenest.authorizeservice.dto.response.PermissionCheckResponse;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IRolePermissionService rolePermissionService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @PostMapping("/check/url")
    @AllowAllRoles
    public ResponseEntity<PermissionCheckResponse> checkUrl(@RequestBody PermissionCheckRequest request) {
        String url = request != null ? request.url() : null;
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body(new PermissionCheckResponse(false));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(new PermissionCheckResponse(false));
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean allowed = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .flatMap(role -> {
                    List<RolePermission> perms = rolePermissionService.findByRole(role);
                    return perms.stream();
                })
                .anyMatch(rp -> rp.getModule() != null && matches(rp.getModule().getUrlPattern(), url));

        return ResponseEntity.ok(new PermissionCheckResponse(allowed));
    }

    private boolean matches(String pattern, String url) {
        if (pattern == null || pattern.isBlank()) return false;
        // Normalize: ensure leading slash
        String p = pattern.startsWith("/") ? pattern : "/" + pattern;
        String u = url.startsWith("/") ? url : "/" + url;
        return antPathMatcher.match(p, u);
    }
}
