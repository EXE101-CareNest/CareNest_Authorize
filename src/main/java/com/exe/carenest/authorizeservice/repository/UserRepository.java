package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.auth.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Account, String> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);

    boolean existsByEmail(String email);
}