package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.dto.request.CreateModuleRequest;
import com.exe.carenest.authorizeservice.service.IModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ModuleController {
    
    private final IModuleService moduleService;


    @GetMapping
    public List<ModuleFunc> getAllModules() {
        return moduleService.getAllModules();
    }


    // POST: Thêm module mới
    @PostMapping
    public ModuleFunc createModule(@RequestBody CreateModuleRequest request) {
        return moduleService.createModule(request.urlPattern(), request.name());
    }

    // GET: Lấy module theo URL pattern
    @GetMapping("/{urlPattern}")
    public ModuleFunc getModuleByUrlPattern(@PathVariable String urlPattern) {
        return moduleService.findByUrlPattern(urlPattern);
    }


    // DELETE: Xóa module
    @DeleteMapping("/{urlPattern}")
    public void deleteModule(@PathVariable String urlPattern) {
        moduleService.deleteModule(urlPattern);
    }
    
//    // GET: Lấy modules với permissions
//    @GetMapping("/with-permissions")
//    public ResponseEntity<List<ModuleWithPermissionsDto>> getModulesWithPermissions() {
//        List<ModuleWithPermissionsDto> modules = moduleService.getAllModulesWithPermissions();
//        return ResponseEntity.ok(modules);
//    }
}
