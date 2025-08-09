package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<ModuleFunc, String> {

}
