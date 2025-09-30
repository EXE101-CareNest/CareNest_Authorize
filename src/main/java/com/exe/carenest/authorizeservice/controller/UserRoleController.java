package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.dto.request.CreateRoleRequest;
import com.exe.carenest.authorizeservice.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
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
    public List<UserRole> getAllRoles() {
        return userRoleService.getAllRoles();
    }
    
    // POST: Thêm role mới
    @PostMapping
    public UserRole createRole(@RequestBody CreateRoleRequest request) {
        return userRoleService.createRole(request.name());
    }
    
    // GET: Lấy role theo tên
    @GetMapping("/{name}")
    public UserRole getRoleByName(@PathVariable String name) {
        return userRoleService.findByName(name);
    }
    
    // DELETE: Xóa role
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Integer id) {
        userRoleService.deleteRole(id);
    }
}
