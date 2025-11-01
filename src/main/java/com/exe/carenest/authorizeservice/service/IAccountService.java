package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.dto.response.PersonalInfoResponse;
import com.exe.carenest.authorizeservice.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.request.UpdateAccountRequest;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAccountService {
    void createAccount(RegisterRequest registerRequest, String roleName);
//    Account findByUsername(String username);
//    AccountResponse findByUsername(String username);

    void activeAccount(String email);

    Account getAccountByOTPToken(String token);
    AccountResponse findByUsernameResponse(String username);

    Account findByUsername(String username);

    boolean existsByUsername(String username);

    void updatePassword(String userId, String newPassword);

    boolean deleteAccount(String accountId);

    List<AccountResponse> getAllAccounts(); // For admin hehe, oke ?

    Page<AccountResponse> getAllAccounts(Pageable pageable);

    long getAccountsCount();

    AccountResponse findById(String id);

    Account findByIdAccount(String id);

    Account getCurrentUser();

    void assignRole(String id, String roleName);
    
    void updateAccount(String id, UpdateAccountRequest request);
    
    void activateAccountByEmail(String email);

    PersonalInfoResponse getMe();


    boolean isAccountActive(String email);
}
