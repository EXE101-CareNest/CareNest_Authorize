package com.exe.carenest.authorizeservice.userManagement.controller;

import com.exe.carenest.authorizeservice.authManagement.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.authManagement.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.userManagement.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing accounts (Admin only)")
public class AccountController {

    private final IAccountService accountService;

    @PostMapping("/register")
    @Operation(summary = "Create new account")
    public ResponseEntity<String> createAccount(@RequestBody RegisterRequest request) {
        accountService.createAccount(request);
        return ResponseEntity.ok("Account created successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        AccountResponse account = accountService.findById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get account by username")
    public ResponseEntity<AccountResponse> getAccountByUsername(@PathVariable String username) {
        AccountResponse account = accountService.findByUsernameResponse(username);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Update account password")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, 
                                                @RequestBody Map<String, String> request) {
        accountService.updatePassword(id, request.get("newPassword"));
        return ResponseEntity.ok("Password updated successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/exists/{username}")
    @Operation(summary = "Check if username exists")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = accountService.existsByUsername(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
