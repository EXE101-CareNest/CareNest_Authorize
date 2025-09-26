package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import java.util.List;

public interface IRolePermissionService {
    RolePermission createRolePermission(String roleName, String moduleUrlPattern);
    List<RolePermission> findByRole(String roleName);
    List<RolePermission> getAllRolePermissions();
    void deleteRolePermission(Long id);

    boolean  hasPermission(String roleName, String moduleUrlPattern, HttpPermission httpMethod);
}
