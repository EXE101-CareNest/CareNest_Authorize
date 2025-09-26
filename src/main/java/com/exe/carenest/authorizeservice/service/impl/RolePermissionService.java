package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.repository.RoleRepository;
import com.exe.carenest.authorizeservice.repository.UserRoleRepository;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionService implements IRolePermissionService {

    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public RolePermission createRolePermission(String roleName, String moduleUrlPattern) {
        ModuleFunc module = moduleRepository.findById(moduleUrlPattern)
                .orElseThrow(() -> new ApiException("MODULE_NOT_FOUND", "Module not found", 404));

        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + roleName, 404));

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setModule(module);

        return roleRepository.save(rolePermission);
    }

    @Override
    public List<RolePermission> findByRole(String roleName) {
        return roleRepository.findByRole_Name(roleName);
    }

    @Override
    public List<RolePermission> getAllRolePermissions() {
        return roleRepository.findAll();
    }

    @Override
    public void deleteRolePermission(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ApiException("PERMISSION_NOT_FOUND", "Role permission not found", 404);
        }
        roleRepository.deleteById(id);
    }

    @Cacheable(value = "user-permissions",
            key = "#roleName + ':' + #moduleUrlPattern + ':' + #httpMethod.name()",
            condition = "#roleName != null and #moduleUrlPattern != null and #httpMethod != null",
            unless = "#result == false")
    @Override
    public boolean hasPermission(String roleName, String moduleUrlPattern, HttpPermission httpMethod) {
        if (roleName == null || moduleUrlPattern == null || httpMethod == null) {
            return false;
        }
        return roleRepository.existsByRoleAndModuleAndPermission(roleName,moduleUrlPattern,httpMethod);
    }

    @CacheEvict(value = "user-permissions", allEntries = true)
    public void clearPermissionCache() {
        // This method will clear all cached permissions
    }
}
