package com.exe.carenest.authorizeservice.dto.request;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;

import java.util.List;

public record BatchUpdatePermissionsRequest(String roleName, String moduleUrlPattern, List<HttpPermission> httpPermissions) {

}