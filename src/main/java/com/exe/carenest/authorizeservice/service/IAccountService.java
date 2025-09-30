package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import org.springframework.security.config.annotation.web.configurers.AbstractConfigAttributeRequestMatcherRegistry;

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

    boolean deleteAccount(String accountId);

    List<AccountResponse> getAllAccounts(); // For admin hehe, oke ?

    AccountResponse findById(String id);

    Account findByIdAccount(String id);

    Account getCurrentUser();

    void assignRole(String id, String roleName);
    
    void activateAccountByEmail(String email);

}
