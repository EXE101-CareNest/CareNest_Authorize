package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.dto.request.BatchUpdatePermissionsRequest;
import com.exe.carenest.authorizeservice.dto.request.UpdateRolePermissionRequest;
import com.exe.carenest.authorizeservice.dto.response.RolePermissionDisplayDto;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RolePermissionController {
    
    private final IRolePermissionService rolePermissionService;


    // PUT: Cập nhật permission
    @PutMapping("/{id}")
    public RolePermission updateRolePermission(
            @PathVariable Long id, 
            @RequestBody UpdateRolePermissionRequest request) {
        return rolePermissionService.updateRolePermission(
            id, 
            request.httpPermission()
        );
    }


    @DeleteMapping("/{id}")
    public void deleteRolePermission(@PathVariable Long id) {
        rolePermissionService.deleteRolePermission(id);
    }



    @PostMapping("/batch-update")
    public String batchUpdatePermissions(@RequestBody BatchUpdatePermissionsRequest request) {
        rolePermissionService.batchUpdatePermissions(
            request.roleName(),
            request.moduleUrlPattern(),
            request.httpPermissions()
        );
        return "Permissions updated successfully";
    }


    // GET: Lấy permissions display cho role (như trong UI)
    @GetMapping("/display/{roleName}")
    public List<RolePermissionDisplayDto> getRolePermissionsDisplay(@PathVariable String roleName) {
        return rolePermissionService.getRolePermissionsDisplay(roleName);
    }

}
