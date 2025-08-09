package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionService implements IRolePermissionService {

    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;

    @Override
    public RolePermission createRolePermission(String role, String moduleUrlPattern) {
        ModuleFunc module = moduleRepository.findById(moduleUrlPattern)
                .orElseThrow(() -> new ApiException("MODULE_NOT_FOUND", "Module not found", 404));

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setModule(module);

        return roleRepository.save(rolePermission);
    }

    @Override
    public List<RolePermission> findByRole(String role) {
        return roleRepository.findByRole(role);
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
}
