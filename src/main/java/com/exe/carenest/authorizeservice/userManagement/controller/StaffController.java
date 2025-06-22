package com.exe.carenest.authorizeservice.userManagement.controller;

import com.exe.carenest.authorizeservice.authManagement.model.Role;
import com.exe.carenest.authorizeservice.userManagement.dto.request.StaffUpdateRequest;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.StaffResponse;
import com.exe.carenest.authorizeservice.userManagement.service.IAccountService;
import com.exe.carenest.authorizeservice.userManagement.service.IStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff Management", description = "APIs for managing staff")
public class StaffController {

    private final IStaffService staffService;
    private final IAccountService accountService;

    @GetMapping("/{id}")
    @Operation(summary = "Get staff by ID")
    public ResponseEntity<StaffResponse> getStaff(@PathVariable Long id) {
        StaffResponse staff = staffService.getStaffById(id);
        return ResponseEntity.ok(staff);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update staff")
    public ResponseEntity<StaffResponse> updateStaff(@PathVariable Long id,
                                                     @RequestBody StaffUpdateRequest request) {
        StaffResponse staff = staffService.updateStaff(id, request);
        return ResponseEntity.ok(staff);
    }

    @PostMapping("/assign/{id}")
    public ResponseEntity<Void> assignStaff(@PathVariable Long id) {
        accountService.assignRole(id, Role.ROLE_STAFF);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign/{id}/shop/{shopId}")
    public ResponseEntity<Void> assignStaffToShop(@PathVariable("id") Long id
            , @PathVariable("shopId") Long shopId) {
        staffService.assignStaffToShop(id, shopId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unassign/{staffId}/shop/{shopId}")
    @Operation(summary = "Remove staff from shop")
    public ResponseEntity<Void> unassignStaffFromShop(@PathVariable Long staffId, @PathVariable("shopId") Long shopId) {
        staffService.unassignStaffFromShop(staffId,shopId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete staff")
    public ResponseEntity<String> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok("Staff deleted successfully");
    }

    @GetMapping("/shop/{shopId}")
    @Operation(summary = "Get all staff by shop ID")
    public ResponseEntity<List<StaffResponse>> getStaffByShop(@PathVariable Long shopId) {
        List<StaffResponse> staffList = staffService.getAllStaffByShopId(shopId);
        return ResponseEntity.ok(staffList);
    }
}
