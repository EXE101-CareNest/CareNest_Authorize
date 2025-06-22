package com.exe.carenest.authorizeservice.authManagement.service;

import com.exe.carenest.authorizeservice.authManagement.model.RolePermission;
import java.util.List;

public interface IRolePermissionService {
    RolePermission createRolePermission(String role, String moduleUrlPattern);
    List<RolePermission> findByRole(String role);
    List<RolePermission> getAllRolePermissions();
    void deleteRolePermission(Long id);
}
