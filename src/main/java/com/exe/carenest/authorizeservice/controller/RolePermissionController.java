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
public class RolePermissionController {
    
    private final IRolePermissionService rolePermissionService;

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    // DELETE: Xóa permission
    @DeleteMapping("/{id}")
    public void deleteRolePermission(@PathVariable Long id) {
        rolePermissionService.deleteRolePermission(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    // POST: Batch update permissions cho role
    @PostMapping("/batch-update")
    public String batchUpdatePermissions(@RequestBody BatchUpdatePermissionsRequest request) {
        rolePermissionService.batchUpdatePermissions(
            request.roleName(),
            request.moduleUrlPattern(),
            request.httpPermissions()
        );
        return "Permissions updated successfully";
    }


    @PreAuthorize("hasRole('ADMIN')")
    // GET: Lấy permissions display cho role (như trong UI)
    @GetMapping("/display/{roleName}")
    public List<RolePermissionDisplayDto> getRolePermissionsDisplay(@PathVariable String roleName) {
        return rolePermissionService.getRolePermissionsDisplay(roleName);
    }

}
