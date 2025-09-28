package com.exe.carenest.authorizeservice.dto.response;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import lombok.Builder;

import java.util.List;

@Builder
public record RolePermissionDisplayDto (String moduleUrlPattern, String moduleName, List<HttpPermission> currentPermissions, List<HttpPermission> availablePermissions) {
}
