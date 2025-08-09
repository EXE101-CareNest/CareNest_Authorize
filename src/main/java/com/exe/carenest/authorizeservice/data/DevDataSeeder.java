package com.exe.carenest.authorizeservice.data;


import com.exe.carenest.authorizeservice.auth.model.Account;

import com.exe.carenest.authorizeservice.auth.model.Roles;
import com.exe.carenest.authorizeservice.auth.model.ShopRole;
import com.exe.carenest.authorizeservice.repository.ShopRoleRepository;
import com.exe.carenest.authorizeservice.user.model.Shop;
import com.exe.carenest.authorizeservice.repository.ShopRepository;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local") // Only run in local profile
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopRepository shopRepository;
    private final ShopRoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            ShopRole shopRole = new ShopRole();
            shopRole.setName("OWNER");
            roleRepository.save(shopRole);

            ShopRole shopRole2 = new ShopRole();
            shopRole2.setName("STAFF");
            roleRepository.save(shopRole2);

            createAdminAccount();
            createShopAccounts();
            createShopOwner(shopRole);
            createShopStaff(shopRole2);
            log.info("Development test accounts created!");
        } else {
            log.info("Test accounts already exist, skipping seeding.");
        }
    }

    private void createAdminAccount() {
        Account admin = new Account();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("@1"));
        admin.setEmail("admin@carenest.com");
        admin.setRole(Roles.ROLE_ADMIN);
        admin.set_active(true);
        userRepository.save(admin);
        log.info("Admin account created: admin/@1");
    }



    private void createShopOwner(ShopRole role) {
        Account user = new Account();
        user.setUsername("shopOwner");
        user.setPassword(passwordEncoder.encode("@1"));
        user.setEmail("shopOwner@carenest.com");
        user.setShopRole(role);
        user.setRole(Roles.ROLE_USER);
        user.set_active(true);
        userRepository.save(user);
        log.info("user account created: shopOwner/@1");
    }

    private void createShopStaff(ShopRole role) {
        Account user = new Account();
        user.setUsername("shopStaff");
        user.setPassword(passwordEncoder.encode("@1"));
        user.setEmail("shopStaff@carenest.com");
        user.setShopRole(role);
        user.setRole(Roles.ROLE_USER);
        user.set_active(true);
        userRepository.save(user);
        log.info("User account created: shopStaff/@1");
    }
    private void createShopAccounts() {
        // Test Shop
        Shop testShop = new Shop();
        testShop.setPassword(passwordEncoder.encode("test123"));
        testShop.setShopName("Test Pet Shop");

        Optional<Account> account = userRepository.findById(1L);
        testShop.setOwner(account.orElseGet(Account::new));
        testShop.setPassword(passwordEncoder.encode("shop@1"));
        testShop.setWorkingDays("7");
        testShop.setDescription("Shop dùng để test");
        testShop.setStatus(true);
        shopRepository.save(testShop);
    }
}
