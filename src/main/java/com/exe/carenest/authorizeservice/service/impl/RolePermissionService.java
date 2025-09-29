package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import com.exe.carenest.authorizeservice.auth.model.ModuleFunc;
import com.exe.carenest.authorizeservice.auth.model.RolePermission;
import com.exe.carenest.authorizeservice.auth.model.UserRole;
import com.exe.carenest.authorizeservice.dto.response.RolePermissionDisplayDto;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.repository.RoleRepository;
import com.exe.carenest.authorizeservice.repository.UserRoleRepository;
import com.exe.carenest.authorizeservice.service.IRolePermissionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionService implements IRolePermissionService {

    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    private static final PathPatternParser pathPatternParser = new PathPatternParser();
    private static final String CACHE_PREFIX = "permission:";


    @Override
    public RolePermission createRolePermission(String roleName, String moduleUrlPattern, HttpPermission httpPermission) {
        ModuleFunc module = moduleRepository.findById(moduleUrlPattern)
                .orElseThrow(() -> new ApiException("MODULE_NOT_FOUND", "Module not found", 404));

        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + roleName, 404));

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setModule(module);
        rolePermission.setHttpPermission(httpPermission);

        return roleRepository.save(rolePermission);
    }

    @Override
    public List<RolePermission> findByRole(String roleName) {
        return roleRepository.findByRole_Name(roleName);
    }

    @Override
    public List<RolePermission> getAllRolePermissions() {
        return roleRepository.findAll();
    }

    @Override
    public void deleteRolePermission(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ApiException("PERMISSION_NOT_FOUND", "Role permission not found", 404);
        }
        roleRepository.deleteById(id);
    }


    @Override
    public RolePermission updateRolePermission(Long id, HttpPermission httpPermission) {
        return null;
    }

    @Override
    public void batchUpdatePermissions(String roleName, String moduleUrlPattern, List<HttpPermission> permissions) {
        // Delete existing permissions for this role-module combination
        roleRepository.deleteByRole_NameAndModule_UrlPattern(roleName, moduleUrlPattern);

        // Add new permissions
        for (HttpPermission permission : permissions) {
            createRolePermission(roleName, moduleUrlPattern, permission);
        }
    }


    @Override
    public List<RolePermissionDisplayDto> getRolePermissionsDisplay(String roleName) {
        List<ModuleFunc> allModules = moduleRepository.findAll();
        List<RolePermission> rolePermissions = findByRole(roleName);

        Map<String, List<HttpPermission>> permissionMap = rolePermissions.stream()
                .collect(Collectors.groupingBy(
                        rp -> rp.getModule().getUrlPattern(),
                        Collectors.mapping(RolePermission::getHttpPermission, Collectors.toList())
                ));

        return allModules.stream()
                .map(module -> RolePermissionDisplayDto.builder()
                        .moduleUrlPattern(module.getUrlPattern())
                        .moduleName(module.getName())
                        .currentPermissions(permissionMap.getOrDefault(module.getUrlPattern(), new ArrayList<>()))
                        .availablePermissions(Arrays.asList(HttpPermission.values()))
                        .build())
                .collect(Collectors.toList());
    }
    @CacheEvict(value = "user-permissions", allEntries = true)
    public void clearPermissionCache() {
        // This method will clear all cached permissions
    }

    private void updateRedisCache(){

    }
//    @Cacheable(value = "user-permissions",
//            key = "#roleName + ':' + #moduleUrlPattern + ':' + #httpMethod.name()",
//            condition = "#roleName != null and #moduleUrlPattern != null and #httpMethod != null",
//            unless = "#result == false")
//    @Override
//    public boolean hasPermission(String roleName, String moduleUrlPattern, HttpPermission httpMethod) {
//        if (roleName == null || moduleUrlPattern == null || httpMethod == null) {
//            return false;
//        }
//
//            //Chuyển url  thành prefiđể check v́ d
//            ///api/admin/accounts/1 => /api/admin/accounts/{id}
//        RolePermission rolePermission = roleRepository.getRolePermissionByRoleAndModuleAndPermission(roleName,moduleUrlPattern,httpMethod);;
//        return Ultils.checkPermissions(rolePermission.getModule().getUrlPattern(),moduleUrlPattern);
//    }




    /**
     * Check permission using existing RedisTemplate<String, Object>
     */
    @Override
    public boolean checkPermission(String actualUrl, HttpPermission httpMethod, String role) {

        String cacheKey = CACHE_PREFIX + role + ":" + httpMethod.toString();

        // Get all allowed patterns - cast to Set<String>
        Set<Object> allowedPatternsObj = redisTemplate.opsForSet().members(cacheKey);

        if (allowedPatternsObj == null || allowedPatternsObj.isEmpty()) {
            log.debug("No patterns found in cache for key: {}", cacheKey);
            return false;
        }

        // Convert Object Set to String Set
        Set<String> allowedPatterns = allowedPatternsObj.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        // PathPattern matching
        PathContainer pathContainer = PathContainer.parsePath(actualUrl);

        boolean hasPermission = allowedPatterns.stream()
                .anyMatch(pattern -> {
                    try {
                        PathPattern compiled = pathPatternParser.parse(pattern);
                        boolean matches = compiled.matches(pathContainer);

                        if (matches) {
                            log.debug("URL '{}' matches pattern '{}'", actualUrl, pattern);
                        }

                        return matches;
                    } catch (Exception e) {
                        log.warn("Error parsing pattern '{}': {}", pattern, e.getMessage());
                        return false;
                    }
                });

        log.debug("Permission check - URL: {}, Method: {}, Role: {}, Result: {}",
                actualUrl, httpMethod, role, hasPermission);

        return hasPermission;
    }

    /**
     * Build complete cache with Object template
     */
    @PostConstruct
    public void buildCache() {
        log.info("Building Redis permission cache...");

        try {
            // Clear existing permission cache
            clearCache();

            // Get all permissions from database
            List<RolePermission> permissions = roleRepository.findAll();

            if (permissions.isEmpty()) {
                log.warn("No active permissions found in database");
                return;
            }

            // Group permissions by role:method
            Map<String, Set<String>> cacheData = permissions.stream()
                    .collect(Collectors.groupingBy(
                            p -> CACHE_PREFIX + p.getRole().getName() + ":" + p.getHttpPermission().toString(), // <- Fix here
                            Collectors.mapping(
                                    p -> p.getModule().getUrlPattern(),
                                    Collectors.toSet()
                            )
                    ));

            // Store in Redis Sets - convert String to Object
            cacheData.forEach((key, patterns) -> {
                patterns.forEach(pattern -> {
                    // Cast String pattern to Object for template
                    redisTemplate.opsForSet().add(key, (Object) pattern);
                });

                // Set expiration (24 hours)
                redisTemplate.expire(key, Duration.ofHours(24));

                log.debug("Cached {} patterns for key: {}", patterns.size(), key);
            });

            log.info("Successfully built cache for {} role:method combinations", cacheData.size());

        } catch (Exception e) {
            log.error("Error building permission cache", e);
            throw new RuntimeException("Failed to build permission cache", e);
        }
    }

    /**
     * Update cache for specific role with Object template
     */
    public void updateCacheForRole(String role) {
        log.info("Updating cache for role: {}", role);

        try {
            // Remove old entries for this role
            Set<String> keysToDelete = redisTemplate.keys(CACHE_PREFIX + role + ":*");

            if (keysToDelete != null && !keysToDelete.isEmpty()) {
                redisTemplate.delete(keysToDelete);
                log.debug("Deleted {} cache keys for role: {}", keysToDelete.size(), role);
            }

            // Get updated permissions for this role
            List<RolePermission> rolePermissions = roleRepository.findByRole_Name(role);

            if (rolePermissions.isEmpty()) {
                log.warn("No active permissions found for role: {}", role);
                return;
            }

            // Group by method
            Map<String, Set<String>> roleCache = rolePermissions.stream()
                    .collect(Collectors.groupingBy(
                            p -> CACHE_PREFIX + role + ":" + p.getHttpPermission(),
                            Collectors.mapping(
                                    p -> p.getModule().getUrlPattern(),
                                    Collectors.toSet()
                            )
                    ));

            // Update Redis - cast to Object
            roleCache.forEach((key, patterns) -> {
                patterns.forEach(pattern ->
                        redisTemplate.opsForSet().add(key, (Object) pattern)
                );
                redisTemplate.expire(key, Duration.ofHours(24));
            });

            log.info("Updated cache for role '{}' with {} method combinations",
                    role, roleCache.size());

        } catch (Exception e) {
            log.error("Error updating cache for role: {}", role, e);
            throw new RuntimeException("Failed to update cache for role: " + role, e);
        }
    }

    /**
     * Add single permission to cache with Object template
     */
    public void addPermissionToCache(String role, String httpMethod, String moduleUrl) {
        String cacheKey = CACHE_PREFIX + role + ":" + httpMethod;

        redisTemplate.opsForSet().add(cacheKey, (Object) moduleUrl);
        redisTemplate.expire(cacheKey, Duration.ofHours(24));

        log.debug("Added permission to cache - Key: {}, Pattern: {}", cacheKey, moduleUrl);
    }

    /**
     * Remove single permission from cache with Object template
     */
    public void removePermissionFromCache(String role, String httpMethod, String moduleUrl) {
        String cacheKey = CACHE_PREFIX + role + ":" + httpMethod;

        Long removed = redisTemplate.opsForSet().remove(cacheKey, (Object) moduleUrl);

        log.debug("Removed permission from cache - Key: {}, Pattern: {}, Success: {}",
                cacheKey, moduleUrl, removed > 0);
    }

    /**
     * Clear all permission cache
     */
    public void clearCache() {
        Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");

        // ✅ Fix: Add null check
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Cleared {} permission cache keys", keys.size());
        } else {
            log.info("No cache keys to clear");
        }
    }

    /**
     * Get cache statistics with Object template
     */
    public Map<String, Object> getCacheStats() {
        Set<String> allKeys = redisTemplate.keys(CACHE_PREFIX + "*");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalKeys", allKeys != null ? allKeys.size() : 0);

        if (allKeys != null && !allKeys.isEmpty()) {
            Map<String, Long> keySizes = allKeys.stream()
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> redisTemplate.opsForSet().size(key)
                    ));

            stats.put("keySizes", keySizes);
            stats.put("totalPatterns", keySizes.values().stream().mapToLong(Long::longValue).sum());

            // Sample some keys to show content
            Map<String, Set<String>> sampleContent = allKeys.stream()
                    .limit(5)
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> {
                                Set<Object> members = redisTemplate.opsForSet().members(key);
                                return members != null ?
                                        members.stream().map(Object::toString).collect(Collectors.toSet()) :
                                        new HashSet<>();
                            }
                    ));
            stats.put("sampleContent", sampleContent);
        }

        return stats;
    }

    /**
     * Helper method to safely convert Object Set to String Set
     */
    private Set<String> convertToStringSet(Set<Object> objectSet) {
        if (objectSet == null) {
            return new HashSet<>();
        }

        return objectSet.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
