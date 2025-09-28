package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.UserRole;

import java.util.List;

public interface IUserRoleService {
    List<UserRole> getAllRoles();
    UserRole createRole(String name);
    UserRole findByName(String name);
    void deleteRole(Integer id);
}
