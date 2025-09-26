package com.exe.carenest.authorizeservice.data;

import com.exe.carenest.authorizeservice.auth.model.*;
import com.exe.carenest.authorizeservice.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.repository.RoleRepository;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import com.exe.carenest.authorizeservice.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.exe.carenest.authorizeservice.ultil.Ultils.generateCode;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository accountRepository;
    private final ModuleRepository moduleFuncRepository;
    private final RoleRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Starting development data seeding with proper order...");

        // 1. Seed UserRoles FIRST
        seedUserRoles();

        // 2. Seed Users (Accounts) with roles
        seedUsers();

        // 3. Seed ModuleFuncs
        seedModuleFuncs();

        // 4. Seed RolePermissions LAST
        seedRolePermissions();

        log.info("✅ Development data seeding completed in order!");
    }

    // Step 1: Create UserRoles
    private void seedUserRoles() {
        if (userRoleRepository.count() == 0) {
            log.info("1️⃣ Seeding UserRoles...");

            createUserRole("ROLE_ADMIN");
            createUserRole("ROLE_MANAGER");
            createUserRole("ROLE_USER");
            createUserRole("ROLE_SHOP");

            log.info("✅ Created {} UserRoles", userRoleRepository.count());
        } else {
            log.info("⏩ UserRoles already exist, skipping...");
        }
    }

    // Step 2: Create Users (Accounts) with roles
    private void seedUsers() {
        if (accountRepository.count() == 0) {
            log.info("2️⃣ Seeding Users (Accounts)...");

            // Get roles for assignment
            UserRole adminRole = findRoleByName("ROLE_ADMIN");
            UserRole managerRole = findRoleByName("ROLE_MANAGER");
            UserRole userRole = findRoleByName("ROLE_USER");
            UserRole shopRole = findRoleByName("ROLE_SHOP");

            // Create admin user
            createAccount("admin", "admin@carenest.com", adminRole);

            // Create manager user
            createAccount("manager", "manager@carenest.com", managerRole);

            // Create regular user
            createAccount("user", "user@carenest.com", userRole);

            // Create shop user
            createAccount("shop", "shop@carenest.com", shopRole);

            log.info("✅ Created {} Users", accountRepository.count());
        } else {
            log.info("⏩ Users already exist, skipping...");
        }
    }

    // Step 3: Create ModuleFuncs
    private void seedModuleFuncs() {
        if (moduleFuncRepository.count() == 0) {
            log.info("3️⃣ Seeding ModuleFuncs...");

            // Admin modules
            createModule("/api/admin/accounts", "Quản lý tài khoản Admin");
            createModule("/api/admin/accounts/{id}", "Chi tiết tài khoản Admin");
            createModule("/api/admin/accounts/{id}/role", "Phân quyền người dùng");

            // User modules
            createModule("/api/accounts/register/customer", "Đăng ký khách hàng");
            createModule("/api/accounts/username/{username}", "Tìm kiếm theo username");
            createModule("/api/accounts/password", "Đổi mật khẩu");

            // Shop modules
            createModule("/api/shops/register", "Đăng ký cửa hàng");
            createModule("/api/shops/information", "Thông tin cửa hàng");

            log.info("✅ Created {} ModuleFuncs", moduleFuncRepository.count());
        } else {
            log.info("⏩ ModuleFuncs already exist, skipping...");
        }
    }

    // Step 4: Create RolePermissions (depends on UserRoles and ModuleFuncs)
    private void seedRolePermissions() {
        if (rolePermissionRepository.count() == 0) {
            log.info("4️⃣ Seeding RolePermissions...");

            // Get all roles and modules
            List<UserRole> roles = userRoleRepository.findAll();
            List<ModuleFunc> modules = moduleFuncRepository.findAll();

            // Find roles
            UserRole adminRole = findRoleByName("ROLE_ADMIN");
            UserRole managerRole = findRoleByName("ROLE_MANAGER");
            UserRole userRole = findRoleByName("ROLE_USER");
            UserRole shopRole = findRoleByName("ROLE_SHOP");

            // ADMIN: All permissions on all modules
            if (adminRole != null) {
                log.info("🔑 Granting ALL permissions to ADMIN...");
                for (ModuleFunc module : modules) {
                   for(HttpPermission permission : HttpPermission.values()) {
                       createRolePermission(adminRole, module, permission);
                   }
                }
            }

            // MANAGER: Limited permissions
            if (managerRole != null) {
                log.info("🔑 Granting LIMITED permissions to MANAGER...");

                modules.stream()
                        .filter(m -> m.getUrlPattern().startsWith("/api/accounts"))
                        .forEach(module -> {
                            createRolePermission(managerRole, module, HttpPermission.READ);
                            createRolePermission(managerRole, module, HttpPermission.UPDATE);
                        });

                modules.stream()
                        .filter(m -> m.getUrlPattern().startsWith("/api/shops"))
                        .forEach(module -> {
                            createRolePermission(managerRole, module, HttpPermission.READ);
                        });
            }

            // USER: Basic permissions on user modules
            if (userRole != null) {
                log.info("🔑 Granting BASIC permissions to USER...");

                modules.stream()
                        .filter(m -> m.getUrlPattern().startsWith("/api/accounts")
                                && !m.getUrlPattern().startsWith("/api/admin"))
                        .forEach(module -> {
                            createRolePermission(userRole, module, HttpPermission.READ);

                            if (module.getUrlPattern().contains("register") ||
                                    module.getUrlPattern().contains("password")) {
                                createRolePermission(userRole, module, HttpPermission.CREATE);
                                createRolePermission(userRole, module, HttpPermission.UPDATE);
                            }
                        });
            }

            // SHOP: Full permissions on shop modules
            if (shopRole != null) {
                log.info("🔑 Granting SHOP permissions to SHOP role...");

                modules.stream()
                        .filter(m -> m.getUrlPattern().startsWith("/api/shops"))
                        .forEach(module -> {
                            for (HttpPermission permission : HttpPermission.values()) {
                                createRolePermission(shopRole, module, permission);
                            }
                        });
            }

            log.info("✅ Created {} RolePermissions", rolePermissionRepository.count());
        } else {
            log.info("⏩ RolePermissions already exist, skipping...");
        }
    }

    // Helper methods
    private void createUserRole(String roleName) {
        UserRole role = new UserRole();
        role.setName(roleName);
        userRoleRepository.save(role);
    }

    private void createAccount(String username, String email, UserRole role) {
        Account account = new Account();
        // Account uses String @Id without @GeneratedValue, so we must set it manually
//        account.setId(com.exe.carenest.authorizeservice.ultil.Ultils.generateCode());
        account.setId(generateCode());
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode("123456"));
        account.setRole(role);
        account.set_active(true);
        account.setCreatedDate(LocalDateTime.now());
        accountRepository.save(account);
    }

    private void createModule(String urlPattern, String name) {
        ModuleFunc module = new ModuleFunc();
        module.setUrlPattern(urlPattern);
        module.setName(name);
        moduleFuncRepository.save(module);
    }

    private void createRolePermission(UserRole role, ModuleFunc module, HttpPermission typePermission) {
        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setModule(module);
        rp.setHttpPermission(typePermission);
        rolePermissionRepository.save(rp);
    }

    private UserRole findRoleByName(String roleName) {
        return userRoleRepository.findAll().stream()
                .filter(role -> roleName.equals(role.getName()))
                .findFirst()
                .orElse(null);
    }
    
}
