package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.dto.response.PermissionCheckResponse;
import com.exe.carenest.authorizeservice.exception.BadRequestException;
import com.exe.carenest.authorizeservice.exception.UnauthorizedException;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/check")
    public PermissionCheckResponse checkUrl( @RequestParam("path") String path, @RequestParam("httpMethod") String httpMethod) {

        HttpPermission requiredPermission = getRequiredPermission(httpMethod);


        if (path == null || path.isBlank()) {
            throw new BadRequestException("Path cannot be null or empty");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        String userRole = authorities.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("");


        boolean isAllowed = rolePermissionService.checkPermission(path, requiredPermission, userRole);


        return new PermissionCheckResponse(isAllowed);
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
