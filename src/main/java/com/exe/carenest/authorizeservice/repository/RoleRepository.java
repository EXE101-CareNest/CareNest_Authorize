package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(String role);

    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.module")
    List<RolePermission> findAllWithModule();
}
