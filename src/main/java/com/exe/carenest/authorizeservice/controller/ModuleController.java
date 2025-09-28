package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.dto.request.CreateModuleRequest;
import com.exe.carenest.authorizeservice.service.IModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@CrossOrigin
public class ModuleController {
    
    private final IModuleService moduleService;
    
    // GET: Lấy tất cả modules
    @GetMapping
    public ResponseEntity<List<ModuleFunc>> getAllModules() {
        List<ModuleFunc> modules = moduleService.getAllModules();
        return ResponseEntity.ok(modules);
    }
    
    // POST: Thêm module mới
    @PostMapping
    public ResponseEntity<ModuleFunc> createModule(@RequestBody CreateModuleRequest request) {
        ModuleFunc module = moduleService.createModule(request.urlPattern(), request.name());
        return ResponseEntity.ok(module);
    }
    
    // GET: Lấy module theo URL pattern
    @GetMapping("/{urlPattern}")
    public ResponseEntity<ModuleFunc> getModuleByUrlPattern(@PathVariable String urlPattern) {
        ModuleFunc module = moduleService.findByUrlPattern(urlPattern);
        return ResponseEntity.ok(module);
    }
    
    // DELETE: Xóa module
    @DeleteMapping("/{urlPattern}")
    public ResponseEntity<Void> deleteModule(@PathVariable String urlPattern) {
        moduleService.deleteModule(urlPattern);
        return ResponseEntity.ok().build();
    }
    
//    // GET: Lấy modules với permissions
//    @GetMapping("/with-permissions")
//    public ResponseEntity<List<ModuleWithPermissionsDto>> getModulesWithPermissions() {
//        List<ModuleWithPermissionsDto> modules = moduleService.getAllModulesWithPermissions();
//        return ResponseEntity.ok(modules);
//    }
}
