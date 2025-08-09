package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.auth.model.ShopRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRoleRepository extends JpaRepository<ShopRole, Integer> {
}
