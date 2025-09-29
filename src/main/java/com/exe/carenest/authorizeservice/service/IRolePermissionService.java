package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.dto.response.RolePermissionDisplayDto;

import java.util.List;

public interface IRolePermissionService {
    RolePermission createRolePermission(String roleName, String moduleUrlPattern, HttpPermission httpPermission);
    List<RolePermission> findByRole(String roleName);
    List<RolePermission> getAllRolePermissions();
    void deleteRolePermission(Long id);

    boolean  checkPermission(String actualUrl, HttpPermission httpMethod, String role);

    RolePermission updateRolePermission(Long id, HttpPermission httpPermission);
    void batchUpdatePermissions(String roleName, String moduleUrlPattern, List<HttpPermission> permissions);
    List<RolePermissionDisplayDto> getRolePermissionsDisplay(String roleName);
}
