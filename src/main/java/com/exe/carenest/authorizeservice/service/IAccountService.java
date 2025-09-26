package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;

import java.util.List;

public interface IAccountService {
    void createAccount(RegisterRequest registerRequest, String roleName);
//    Account findByUsername(String username);
//    AccountResponse findByUsername(String username);

    Account getAccountByOTPToken(String token);
    AccountResponse findByUsernameResponse(String username);

    Account findByUsername(String username);

    boolean existsByUsername(String username);

    void updatePassword(String email, String newPassword);

    void deleteAccount(String accountId);

    List<AccountResponse> getAllAccounts(); // For admin hehe, oke ?

    AccountResponse findById(String id);

    Account getCurrentUser();

    void assignRole(String id, String roleName);
    
    void activateAccountByEmail(String email);

}
