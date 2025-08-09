package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import java.util.List;

public interface IModuleService {
    ModuleFunc createModule(String urlPattern, String name);
    ModuleFunc findByUrlPattern(String urlPattern);
    List<ModuleFunc> getAllModules();
    void deleteModule(String urlPattern);
}
