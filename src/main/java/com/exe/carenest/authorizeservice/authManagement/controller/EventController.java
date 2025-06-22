package com.exe.carenest.authorizeservice.authManagement.controller;


import com.exe.carenest.authorizeservice.userManagement.service.impl.RedisPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final RedisPermissionService redisPermissionService;
//    @GetMapping("/reload")
//    public String reload() {
//        redisPermissionService.triggerReload();
//        return "Reload O";
//    }
}
