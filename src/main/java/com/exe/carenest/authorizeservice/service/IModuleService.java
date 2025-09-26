package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.dto.response.ModuleWithPermissionsDto;
import java.util.List;

public interface IModuleService {
    ModuleFunc createModule(String urlPattern, String name);
    ModuleFunc findByUrlPattern(String urlPattern);
    List<ModuleFunc> getAllModules();
    void deleteModule(String urlPattern);
    
    // New method to get all modules with their permissions
    List<ModuleWithPermissionsDto> getAllModulesWithPermissions();
}
