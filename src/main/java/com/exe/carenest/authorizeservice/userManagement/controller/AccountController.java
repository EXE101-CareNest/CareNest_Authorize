package com.exe.carenest.authorizeservice.userManagement.controller;

import com.exe.carenest.authorizeservice.authManagement.customAnnotation.AdminOnly;
import com.exe.carenest.authorizeservice.authManagement.customAnnotation.AllowAllRoles;
import com.exe.carenest.authorizeservice.authManagement.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.authManagement.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.authManagement.model.Roles;
import com.exe.carenest.authorizeservice.userManagement.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Account Management", description = "APIs for managing user accounts")
@Slf4j
public class AccountController {
    private final IAccountService accountService;

    @PostMapping("/register")
    @Operation(summary = "Create new account", description = "Register a new user account with specified role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Username already exists or invalid role"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Void> createAccount(
            @Parameter(description = "Account registration information", required = true)
            @Valid @RequestBody RegisterRequest request) {
        accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Find account by username", description = "Retrieve account information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @AdminOnly
    public ResponseEntity<AccountResponse> findByUsername(
            @Parameter(description = "Username", required = true, example = "john_doe")
            @PathVariable String username) {
        AccountResponse account = accountService.findByUsernameResponse(username);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieve account information by unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @AdminOnly
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "Account ID", required = true, example = "1")
            @PathVariable Long id) {
        AccountResponse account = accountService.findById(id);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Update account password", description = "Change password for existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @AllowAllRoles
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Account ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New password", required = true)
            @RequestParam String newPassword) {
        accountService.updatePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Soft delete an account by setting inactive status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @AllowAllRoles
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account ID", required = true, example = "1")
            @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Retrieve list of all accounts (Admin only)")
    @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    @AdminOnly
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();

        log.info("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Assign role to account", description = "Update role for existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @AdminOnly
    public ResponseEntity<Void> assignRole(
            @Parameter(description = "Account ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Role to assign", required = true)
            @RequestParam Roles role) {
        accountService.assignRole(id, role);
        return ResponseEntity.ok().build();
    }
}
