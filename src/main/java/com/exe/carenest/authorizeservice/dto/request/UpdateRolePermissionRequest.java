package com.exe.carenest.authorizeservice.dto.request;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;

public record UpdateRolePermissionRequest(HttpPermission httpPermission) {
}
