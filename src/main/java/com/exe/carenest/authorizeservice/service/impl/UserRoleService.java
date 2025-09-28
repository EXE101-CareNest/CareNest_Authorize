package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.repository.UserRoleRepository;
import com.exe.carenest.authorizeservice.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserRoleService implements IUserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Override
    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    public UserRole createRole(String name) {
        return userRoleRepository.save(new UserRole(name));
    }

    @Override
    public UserRole findByName(String name) {
        return userRoleRepository.findByName(name).orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + name, 404));
    }

    @Override
    public void deleteRole(Integer id) {
        userRoleRepository.deleteById(id);
    }
}
