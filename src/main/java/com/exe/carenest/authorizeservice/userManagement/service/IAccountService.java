package com.exe.carenest.authorizeservice.userManagement.service;

import com.exe.carenest.authorizeservice.authManagement.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.authManagement.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.authManagement.model.Role;

import java.util.List;

public interface IAccountService {
    void createAccount(RegisterRequest registerRequest);
//    Account findByUsername(String username);
//    AccountResponse findByUsername(String username);

    AccountResponse findByUsernameResponse(String username);

    boolean existsByUsername(String username);
    void updatePassword(Long accountId, String newPassword);
    void deleteAccount(Long accountId);
    List<AccountResponse> getAllAccounts(); // For admin hehe, oke ?
    AccountResponse findById(Long id);

    void assignRole(Long id, Role role);

}
