package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.dto.request.UpdateAccountRequest;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.service.IAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
@Tag(name = "Admin Account Management", description = "Admin APIs for managing user accounts")

public class AdminAccountController {
    private final IAccountService accountService;

    public AdminAccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<AccountResponse> getAllAccounts(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        return accountService.getAllAccounts(pageable);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public long getAccountsCount() {
        return accountService.getAccountsCount();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SHOP, 'ROLE_USER')")
    public AccountResponse getAccountById(@PathVariable String id) {
        return accountService.findById(id);
    }

 

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Boolean deleteAccount(@PathVariable String id) {
        return accountService.deleteAccount(id);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Boolean assignRole(@PathVariable String id, @RequestParam String role) {
        accountService.assignRole(id, role);
        return true;
    }
}
