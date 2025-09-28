package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.dto.response.RolePermissionDisplayDto;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.repository.RoleRepository;
import com.exe.carenest.authorizeservice.repository.UserRoleRepository;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService implements IRolePermissionService {

    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public RolePermission createRolePermission(String roleName, String moduleUrlPattern, HttpPermission httpPermission) {
        ModuleFunc module = moduleRepository.findById(moduleUrlPattern)
                .orElseThrow(() -> new ApiException("MODULE_NOT_FOUND", "Module not found", 404));

        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + roleName, 404));

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setModule(module);
        rolePermission.setHttpPermission(httpPermission);

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

            //Chuyển url  thành prefiđể check v́ d
            ///api/admin/accounts/1 => /api/admin/accounts/{id}
        return roleRepository.existsByRoleAndModuleAndPermission(roleName,moduleUrlPattern,httpMethod);
    }

    @Override
    public RolePermission updateRolePermission(Long id, HttpPermission httpPermission) {
        return null;
    }

    @Override
    public void batchUpdatePermissions(String roleName, String moduleUrlPattern, List<HttpPermission> permissions) {
        // Delete existing permissions for this role-module combination
        roleRepository.deleteByRole_NameAndModule_UrlPattern(roleName, moduleUrlPattern);

        // Add new permissions
        for (HttpPermission permission : permissions) {
            createRolePermission(roleName, moduleUrlPattern, permission);
        }
    }


    @Override
    public List<RolePermissionDisplayDto> getRolePermissionsDisplay(String roleName) {
        List<ModuleFunc> allModules = moduleRepository.findAll();
        List<RolePermission> rolePermissions = findByRole(roleName);

        Map<String, List<HttpPermission>> permissionMap = rolePermissions.stream()
                .collect(Collectors.groupingBy(
                        rp -> rp.getModule().getUrlPattern(),
                        Collectors.mapping(RolePermission::getHttpPermission, Collectors.toList())
                ));

        return allModules.stream()
                .map(module -> RolePermissionDisplayDto.builder()
                        .moduleUrlPattern(module.getUrlPattern())
                        .moduleName(module.getName())
                        .currentPermissions(permissionMap.getOrDefault(module.getUrlPattern(), new ArrayList<>()))
                        .availablePermissions(Arrays.asList(HttpPermission.values()))
                        .build())
                .collect(Collectors.toList());
    }
    @CacheEvict(value = "user-permissions", allEntries = true)
    public void clearPermissionCache() {
        // This method will clear all cached permissions
    }
}
