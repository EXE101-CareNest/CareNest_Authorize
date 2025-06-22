package com.exe.carenest.authorizeservice.authManagement.repository;

import com.exe.carenest.authorizeservice.authManagement.model.ModuleFunc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<ModuleFunc, String> {

}
