package com.exe.carenest.authorizeservice.userManagement.service.impl;

import com.exe.carenest.authorizeservice.infrastructure.event.PermissionReloadEvent;
import com.exe.carenest.authorizeservice.authManagement.model.RolePermission;
import com.exe.carenest.authorizeservice.authManagement.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class RedisPermissionService implements ApplicationListener<PermissionReloadEvent> {

    private  RedisTemplate<String, Object> redisTemplate;
    private  RoleRepository rolePermissionRepository; // Giả định bạn đã có



//    private final ApplicationEventPublisher publisher;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public boolean checkPermission(String role, String url) {
        String redisKey = "role_permission:" + role;
        Map<Object, Object> patternMap = redisTemplate.opsForHash().entries(redisKey);

        return patternMap.keySet().stream()
                .anyMatch(pattern -> matcher.match((String) pattern, url));
    }



    public void reloadPermissionsToRedis() {
        System.out.println("🔁 Đang reload permission từ DB vào Redis...");

        List<RolePermission> allPermissions = rolePermissionRepository.findAllWithModule();

        // Map<role, Set<url_pattern>>
        Map<String, Set<String>> roleToPatterns = new HashMap<>();

        for (RolePermission rp : allPermissions) {
            String role = rp.getRole();
            String pattern = rp.getModule().getUrlPattern();
            roleToPatterns.computeIfAbsent(role, k -> new HashSet<>()).add(pattern);
        }

        for (Map.Entry<String, Set<String>> entry : roleToPatterns.entrySet()) {
            String redisKey = "role_permission:" + entry.getKey();
            Map<String, String> redisMap = entry.getValue().stream()
                    .collect(Collectors.toMap(p -> p, p -> "true"));
            redisTemplate.opsForHash().putAll(redisKey, redisMap);
        }

        System.out.println("[RedisPermissionService] Reloaded permission cache");
    }


    @Override
    public void onApplicationEvent(PermissionReloadEvent event) {
        this.reloadPermissionsToRedis();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}