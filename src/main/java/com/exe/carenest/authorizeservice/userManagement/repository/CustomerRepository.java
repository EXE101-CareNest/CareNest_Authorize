package com.exe.carenest.authorizeservice.userManagement.repository;

import com.exe.carenest.authorizeservice.userManagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
