package com.exe.carenest.authorizeservice.dto.request;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;

public record CreateRolePermissionRequest(String roleName,
                                          String moduleUrlPattern,
                                          HttpPermission httpPermission) {

}