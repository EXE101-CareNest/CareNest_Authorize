package com.exe.carenest.authorizeservice.infrastructure.data;


import com.exe.carenest.authorizeservice.authManagement.model.Account;
import com.exe.carenest.authorizeservice.authManagement.model.Role;
import com.exe.carenest.authorizeservice.userManagement.model.Customer;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import com.exe.carenest.authorizeservice.userManagement.model.Staff;
import com.exe.carenest.authorizeservice.userManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local") // Only run in local profile
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findByUsername("admin").isPresent()) {
            createAdminAccount();
            createTestAccounts();
            log.info("Development test accounts created!");
        }
    }

    private void createAdminAccount() {
        Account admin = new Account();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@carenest.com");
        admin.setRole(Role.ROLE_ADMIN);
        admin.set_active(true);
        userRepository.save(admin);
        log.info("Admin account created: admin/admin123");
    }

    private void createTestAccounts() {
        // Test Shop
        Shop testShop = new Shop();
        testShop.setUsername("testshop");
        testShop.setPassword(passwordEncoder.encode("test123"));
        testShop.setEmail("testshop@carenest.com");
        testShop.setRole(Role.ROLE_SHOP);
        testShop.set_active(true);
        testShop.setShopName("Test Pet Shop");
        testShop.setPhone("0900000001");
        testShop.setDescription("Shop dùng để test");
        testShop.setStatus("ACTIVE");
        userRepository.save(testShop);

        // Test Customer
        Customer testCustomer = new Customer();
        testCustomer.setUsername("testcustomer");
        testCustomer.setPassword(passwordEncoder.encode("test123"));
        testCustomer.setEmail("testcustomer@gmail.com");
        testCustomer.setRole(Role.ROLE_CUSTOMER);
        testCustomer.set_active(true);
        testCustomer.setPhone("0900000002");
        testCustomer.setGender("Nam");
        testCustomer.setBirthday(LocalDate.of(1990, 1, 1));
        testCustomer.setPoint(100);
        userRepository.save(testCustomer);

        // Test Staff
        Staff testStaff = new Staff();
        testStaff.setUsername("teststaff");
        testStaff.setPassword(passwordEncoder.encode("test123"));
        testStaff.setEmail("teststaff@carenest.com");
        testStaff.setRole(Role.ROLE_STAFF);
        testStaff.set_active(true);
        testStaff.setPhone("0900000003");
        testStaff.setGender("Nữ");
        testStaff.setBirthday(LocalDate.of(1995, 1, 1));
        testStaff.setPosition("Tester");
        testStaff.setShop(testShop);
        userRepository.save(testStaff);

        log.info("Test accounts created:");
        log.info("- testshop/test123");
        log.info("- testcustomer/test123");
        log.info("- teststaff/test123");
    }
}
