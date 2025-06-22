package com.exe.carenest.authorizeservice.userManagement.service.impl;

import com.exe.carenest.authorizeservice.authManagement.model.Role;
import com.exe.carenest.authorizeservice.infrastructure.exception.ApiException;
import com.exe.carenest.authorizeservice.authManagement.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.authManagement.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.authManagement.model.Account;
import com.exe.carenest.authorizeservice.userManagement.model.Customer;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import com.exe.carenest.authorizeservice.userManagement.model.Staff;
import com.exe.carenest.authorizeservice.userManagement.repository.UserRepository;
import com.exe.carenest.authorizeservice.userManagement.service.IAccountService;
import com.exe.carenest.authorizeservice.ultil.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createAccount(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new ApiException("USER_EXISTS", "Username already exists", 400);
        }

        Account account = switch (registerRequest.role()) {
            case ROLE_SHOP -> new Shop();
            case ROLE_CUSTOMER -> new Customer();
            case ROLE_STAFF -> new Staff();
            default -> throw new ApiException("INVALID_ROLE", "Invalid role specified", 400);
        };

        account.setUsername(registerRequest.username());
        account.setPassword(passwordEncoder.encode(registerRequest.password()));
        account.setRole(registerRequest.role());
        account.set_active(true);
        userRepository.save(account);
    }

    @Override
    public void assignRole(Long id, Role role) {
        Account staff = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("STAFF_NOT_FOUND", "Staff not found", 404));
        staff.setRole(role);
        userRepository.save(staff);
    }
//    @Override
//    public Account findByUsername(String username) {
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "User not found", 404));
//    }

    @Override
    public AccountResponse findByUsernameResponse(String username) {
        Account account = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "User not found", 404));
        return UserMapper.toAccountResponse(account);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public void updatePassword(Long accountId, String newPassword) {
        Account account = userRepository.findById(accountId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "User not found", 404));
        account.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId) {
        Account account = userRepository.findById(accountId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "User not found", 404));
        account.set_active(false);
        userRepository.save(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return userRepository.findAll().stream()
                .map(UserMapper::toAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse findById(Long id) {
        Account account = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "User not found", 404));
        return UserMapper.toAccountResponse(account);
    }
}
