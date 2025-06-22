package com.exe.carenest.authorizeservice.userManagement.repository;

import com.exe.carenest.authorizeservice.authManagement.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
}
