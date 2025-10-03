package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.service.IAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@Tag(name = "Admin Account Management", description = "Admin APIs for managing user accounts")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminAccountController {
    private final IAccountService accountService;

    public AdminAccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable String id) {
        return accountService.findById(id);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteAccount(@PathVariable String id) {
        return accountService.deleteAccount(id);
    }

    @PutMapping("/{id}/role")
    public Boolean assignRole(@PathVariable String id, @RequestParam String role) {
        accountService.assignRole(id, role);
        return true;
    }
}
