package com.exe.carenest.authorizeservice.userManagement.repository;

import com.exe.carenest.authorizeservice.userManagement.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByShopId(Long shopId);
}