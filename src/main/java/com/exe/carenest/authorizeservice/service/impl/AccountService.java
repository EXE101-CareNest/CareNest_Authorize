package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.auth.model.Gender;
import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.config.JwtProvider;
import com.exe.carenest.authorizeservice.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.request.UpdateAccountRequest;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.dto.response.PersonalInfoResponse;
import com.exe.carenest.authorizeservice.exception.*;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import com.exe.carenest.authorizeservice.repository.UserRoleRepository;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.ultil.DateUtil;
import com.exe.carenest.authorizeservice.ultil.Messages;
import com.exe.carenest.authorizeservice.ultil.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.exe.carenest.authorizeservice.ultil.Ultils.generateCode;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public void createAccount(RegisterRequest registerRequest, String roleName) {
        // Null check for safety
        if (registerRequest == null || registerRequest.password() == null || registerRequest.reEnterPassword() == null) {
            throw new PasswordException("Yêu cầu đăng ký không hợp lệ");
        }

        // Check password confirmation
        if (!registerRequest.reEnterPassword().equals(registerRequest.password())) {
            throw new PasswordException("Mật khẩu nhập lại không khớp");
        }

        // Check if username already exists
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new ApiException(Messages.USER_ALREADY_EXISTS.getCode(), Messages.USER_ALREADY_EXISTS.getMessage(), Messages.USER_ALREADY_EXISTS.getStatus());
        }

        //check if email exits
        if(userRepository.findByEmail(registerRequest.email()).isPresent()){
            throw new ApiException(Messages.USER_ALREADY_EXISTS.getCode(), Messages.MAIL_ALREADY_LINKED.getMessage(), Messages.MAIL_ALREADY_LINKED.getStatus());
        }

        // Resolve role by name
        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + roleName, 404));

        Account account = new Account();
        account.setId(generateCode());
        account.setUsername(registerRequest.username());
        account.setFullName(registerRequest.fullName());
        account.setDateOfBirth(DateUtil.stringToTimestamp(registerRequest.birthday()));
        account.setGender(registerRequest.gender());
        account.setEmail(registerRequest.email());
        account.setPassword(passwordEncoder.encode(registerRequest.password()));
        account.setRole(role);
        account.set_active(false);

        userRepository.save(account);
    }

    @Override
    public void activeAccount(String email){
        Account account = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("ACCOUNT_NOT_FOUND", "Account not found: " + email, 404));


        account.set_active(true);
        userRepository.save(account);
    }

    @Override
    public Account getAccountByOTPToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new InvalidTokenException("Token không được cung cấp");
        }

        try {
            // Giả sử JwtProvider có method validateToken để check invalid/expired
            if (!jwtProvider.validateToken(token)) {
                throw new InvalidTokenException("Token không hợp lệ");
            }
             if (jwtProvider.isTokenExpired(token)) { throw new ExpiredTokenException(); }

            String email = jwtProvider.getSubject(token);
            return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        } catch (Exception e) {  // Catch lỗi từ JWT library (như ExpiredJwtException)
            if (e.getMessage().contains("expired")) {
                throw new ExpiredTokenException("Token đã hết hạn: " + e.getMessage());
            } else {
                throw new InvalidTokenException("Lỗi xử lý token: " + e.getMessage());
            }
        }

    }

    @Override
    public void assignRole(String id, String roleName) {
        Account acc = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + roleName, 404));
        acc.setRole(role);
        userRepository.save(acc);
    }

    @Override
    public void updateAccount(String id, UpdateAccountRequest request) {
        Account account = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (request.getFullName() != null) account.setFullName(request.getFullName());
        if (request.getEmail() != null) account.setEmail(request.getEmail());
        if (request.getGender() != null) account.setGender(Gender.valueOf(request.getGender()));
        if (request.getDateOfBirth() != null) account.setDateOfBirth(request.getDateOfBirth());
        if (request.getNationality() != null) account.setNationality(request.getNationality());
        if (request.getPermanentAddress() != null) account.setPermanentAddress(request.getPermanentAddress());
        if (request.getHomeTown() != null) account.setHomeTown(request.getHomeTown());
        if (request.getIssuedDate() != null) account.setIssuedDate(request.getIssuedDate());
        if (request.getIssuedBy() != null) account.setIssuedBy(request.getIssuedBy());
        if (request.getImgUrl() != null) account.setImgUrl(request.getImgUrl());
        if (request.getStatus() != null) account.setStatus(request.getStatus());

        account.setUpdatedDate(java.time.LocalDateTime.now());
        userRepository.save(account);
    }

    @Override
    public AccountResponse findByUsernameResponse(String username) {
        Account account = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return UserMapper.toAccountResponse(account);
    }

    @Override
    public Account findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public void updatePassword(String userId, String newPassword) {
        // Null check for safety
        if (newPassword == null || newPassword.isEmpty()) {
            throw new PasswordException("Mật khẩu mới không hợp lệ");
        }

        Account account = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        account.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(account);
    }

    @Override
    public boolean deleteAccount(String accountId) {
        Account account = userRepository.findById(accountId)
                .orElseThrow(UserNotFoundException::new);
        account.set_active(false);
        userRepository.save(account);
        return true;
    }

    @Override
    public void hardDeleteAccount(String accountId) {
        if (!userRepository.existsById(accountId)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(accountId);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return userRepository.findAll().stream()
            .map(UserMapper::toAccountResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toAccountResponse);
    }

    @Override
    public long getAccountsCount() {
        return userRepository.count();
    }

    @Override
    public AccountResponse findById(String id) {
        Account account = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return UserMapper.toAccountResponse(account);
    }

    @Override
    public Account findByIdAccount(String id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Account getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UnauthorizedException("Không có quyền truy cập - vui lòng đăng nhập");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }
    
    @Override
    public void activateAccountByEmail(String email) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            throw new UserNotFoundException("Email không được để trống");
        }
        
        // Find account by email
        Account account = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy tài khoản với email: " + email));
        
        // Activate account
        account.set_active(true);
        userRepository.save(account);
    }

    @Override
    public PersonalInfoResponse getMe() {
        Account account =  getCurrentUser();

        return new PersonalInfoResponse(
                account.getFullName(),
                account.getEmail(),
                account.getGender(),
                account.getDateOfBirth(),
                account.getNationality(),
                account.getPermanentAddress(),
                account.getHomeTown(),
                account.getImgUrl()
        );
    }

    @Override
    public boolean isAccountActive(String email) {
        Account account = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return account.is_active();
    }

}
