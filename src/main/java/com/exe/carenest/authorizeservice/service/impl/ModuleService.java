package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.service.IModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService implements IModuleService {

    private final ModuleRepository moduleRepository;

    @Override
    public ModuleFunc createModule(String urlPattern, String name) {
        if (moduleRepository.existsById(urlPattern)) {
            throw new ApiException("MODULE_EXISTS", "Module with this URL pattern already exists", 400);
        }
        ModuleFunc module = new ModuleFunc(urlPattern, name);
        return moduleRepository.save(module);
    }

    @Override
    public ModuleFunc findByUrlPattern(String urlPattern) {
        return moduleRepository.findById(urlPattern)
                .orElseThrow(() -> new ApiException("MODULE_NOT_FOUND", "Module not found", 404));
    }

    @Override
    public List<ModuleFunc> getAllModules() {
        return moduleRepository.findAll();
    }

    @Override
    public void deleteModule(String urlPattern) {
        if (!moduleRepository.existsById(urlPattern)) {
            throw new ApiException("MODULE_NOT_FOUND", "Module not found", 404);
        }
        moduleRepository.deleteById(urlPattern);
    }
}
