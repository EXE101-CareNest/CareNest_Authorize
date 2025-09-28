package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.dto.request.CreateRoleRequest;
import com.exe.carenest.authorizeservice.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin
public class UserRoleController {
    
    private final IUserRoleService userRoleService;
    
    // GET: Lấy tất cả roles
    @GetMapping
    public ResponseEntity<List<UserRole>> getAllRoles() {
        List<UserRole> roles = userRoleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    
    // POST: Thêm role mới
    @PostMapping
    public ResponseEntity<UserRole> createRole(@RequestBody CreateRoleRequest request) {
        UserRole role = userRoleService.createRole(request.name());
        return ResponseEntity.ok(role);
    }
    
    // GET: Lấy role theo tên
    @GetMapping("/{name}")
    public ResponseEntity<UserRole> getRoleByName(@PathVariable String name) {
        UserRole role = userRoleService.findByName(name);
        return ResponseEntity.ok(role);
    }
    
    // DELETE: Xóa role
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        userRoleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }
}
