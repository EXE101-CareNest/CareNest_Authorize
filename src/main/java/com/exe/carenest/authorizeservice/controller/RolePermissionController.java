package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.dto.request.BatchUpdatePermissionsRequest;
import com.exe.carenest.authorizeservice.dto.request.UpdateRolePermissionRequest;
import com.exe.carenest.authorizeservice.dto.response.RolePermissionDisplayDto;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
@CrossOrigin
public class RolePermissionController {
    
    private final IRolePermissionService rolePermissionService;

    
    // PUT: Cập nhật permission
    @PutMapping("/{id}")
    public ResponseEntity<RolePermission> updateRolePermission(
            @PathVariable Long id, 
            @RequestBody UpdateRolePermissionRequest request) {
        RolePermission permission = rolePermissionService.updateRolePermission(
            id, 
            request.httpPermission()
        );
        return ResponseEntity.ok(permission);
    }
    
    // DELETE: Xóa permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRolePermission(@PathVariable Long id) {
        rolePermissionService.deleteRolePermission(id);
        return ResponseEntity.ok().build();
    }
    
    // POST: Batch update permissions cho role
    @PostMapping("/batch-update")
    public ResponseEntity<String> batchUpdatePermissions(@RequestBody BatchUpdatePermissionsRequest request) {
        rolePermissionService.batchUpdatePermissions(
            request.roleName(),
            request.moduleUrlPattern(),
            request.httpPermissions()
        );
        return ResponseEntity.ok("Permissions updated successfully");
    }
    
    // GET: Lấy permissions display cho role (như trong UI)
    @GetMapping("/display/{roleName}")
    public ResponseEntity<List<RolePermissionDisplayDto>> getRolePermissionsDisplay(@PathVariable String roleName) {
        List<RolePermissionDisplayDto> display = rolePermissionService.getRolePermissionsDisplay(roleName);
        return ResponseEntity.ok(display);
    }

}
