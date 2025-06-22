package com.exe.carenest.authorizeservice.infrastructure.data;


import com.exe.carenest.authorizeservice.authManagement.model.ModuleFunc;
import com.exe.carenest.authorizeservice.authManagement.model.Role;
import com.exe.carenest.authorizeservice.authManagement.model.RolePermission;
import com.exe.carenest.authorizeservice.authManagement.repository.ModuleRepository;
import com.exe.carenest.authorizeservice.authManagement.repository.RoleRepository;
import com.exe.carenest.authorizeservice.userManagement.model.Customer;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import com.exe.carenest.authorizeservice.userManagement.model.Staff;
import com.exe.carenest.authorizeservice.userManagement.repository.CustomerRepository;
import com.exe.carenest.authorizeservice.userManagement.repository.ShopRepository;
import com.exe.carenest.authorizeservice.userManagement.repository.StaffRepository;
import com.exe.carenest.authorizeservice.userManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModuleRepository moduleRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Starting data seeding...");
            seedModules();
            seedRolePermissions();
            seedShops();
            seedCustomers();
            seedStaff();
            log.info("Data seeding completed!");
        } else {
            log.info("Data already exists, skipping seeding...");
        }
    }

    private void seedModules() {
        if (moduleRepository.count() == 0) {
            List<ModuleFunc> modules = Arrays.asList(
                new ModuleFunc("/api/customers/**", "Customer Management"),
                new ModuleFunc("/api/shops/**", "Shop Management"),
                new ModuleFunc("/api/staff/**", "Staff Management"),
                new ModuleFunc("/api/accounts/**", "Account Management"),
                new ModuleFunc("/api/auth/**", "Authentication"),
                new ModuleFunc("/api/bookings/**", "Booking Management"),
                new ModuleFunc("/api/services/**", "Service Management"),
                new ModuleFunc("/api/products/**", "Product Management")
            );
            moduleRepository.saveAll(modules);
            log.info("Seeded {} modules", modules.size());
        }
    }

    private void seedRolePermissions() {
        if (roleRepository.count() == 0) {
            // Customer permissions
            createRolePermissions("ROLE_CUSTOMER", Arrays.asList(
                "/api/customers/**",
                "/api/bookings/**",
                "/api/services/**",
                "/api/products/**",
                "/api/auth/**"
            ));

            // Shop permissions
            createRolePermissions("ROLE_SHOP", Arrays.asList(
                "/api/shops/**",
                "/api/staff/**",
                "/api/services/**",
                "/api/products/**",
                "/api/bookings/**",
                "/api/auth/**"
            ));

            // Staff permissions
            createRolePermissions("ROLE_STAFF", Arrays.asList(
                "/api/staff/**",
                "/api/bookings/**",
                "/api/services/**",
                "/api/products/**",
                "/api/auth/**"
            ));

            // Admin permissions (all modules)
            createRolePermissions("ROLE_ADMIN", Arrays.asList(
                "/api/customers/**",
                "/api/shops/**",
                "/api/staff/**",
                "/api/accounts/**",
                "/api/auth/**",
                "/api/bookings/**",
                "/api/services/**",
                "/api/products/**"
            ));

            log.info("Seeded role permissions");
        }
    }

    private void createRolePermissions(String role, List<String> modulePatterns) {
        modulePatterns.forEach(pattern -> {
            ModuleFunc module = moduleRepository.findById(pattern)
                    .orElseThrow(() -> new RuntimeException("Module not found: " + pattern));
            
            RolePermission permission = new RolePermission();
            permission.setRole(role);
            permission.setModule(module);
            roleRepository.save(permission);
        });
    }

    private void seedShops() {
        List<Shop> shops = Arrays.asList(
            createShop("PetLove Spa", "petlove_spa", "0901234567", 
                "Dịch vụ spa cao cấp cho thú cưng với đội ngũ chuyên nghiệp", 
                "ACTIVE", "VietcomBank", "1234567890", "Thứ 2 - Chủ nhật", 
                "1900-1234", "123456789012"),
                
            createShop("Happy Paws Clinic", "happy_paws", "0902345678",
                "Phòng khám thú y chuyên nghiệp với trang thiết bị hiện đại",
                "ACTIVE", "Techcombank", "0987654321", "Thứ 2 - Thứ 7",
                "1900-5678", "234567890123"),
                
            createShop("Furry Friends Hotel", "furry_hotel", "0903456789",
                "Khách sạn thú cưng 5 sao với dịch vụ chăm sóc 24/7",
                "ACTIVE", "BIDV", "1122334455", "24/7",
                "1900-9999", "345678901234"),
                
            createShop("Pet Paradise Grooming", "pet_paradise", "0904567890",
                "Salon làm đẹp chuyên nghiệp cho chó mèo",
                "ACTIVE", "ACB", "5566778899", "Thứ 3 - Chủ nhật",
                "1900-7777", "456789012345"),
                
            createShop("Healthy Pet Center", "healthy_pet", "0905678901",
                "Trung tâm chăm sóc sức khỏe toàn diện cho thú cưng",
                "ACTIVE", "MB Bank", "9988776655", "Thứ 2 - Thứ 6",
                "1900-6666", "567890123456")
        );

        shopRepository.saveAll(shops);
        log.info("Seeded {} shops", shops.size());
    }

    private void seedCustomers() {
        List<Customer> customers = Arrays.asList(
            createCustomer("Nguyễn Văn An", "nguyen_van_an", "0911111111", 
                "Nam", LocalDate.of(1990, 5, 15), 150),
                
            createCustomer("Trần Thị Bình", "tran_thi_binh", "0922222222",
                "Nữ", LocalDate.of(1985, 8, 22), 320),
                
            createCustomer("Lê Hoàng Châu", "le_hoang_chau", "0933333333",
                "Nam", LocalDate.of(1992, 12, 3), 75),
                
            createCustomer("Phạm Thị Dung", "pham_thi_dung", "0944444444",
                "Nữ", LocalDate.of(1988, 7, 18), 540),
                
            createCustomer("Võ Minh Đức", "vo_minh_duc", "0955555555",
                "Nam", LocalDate.of(1995, 2, 28), 220),
                
            createCustomer("Hoàng Thị Hoa", "hoang_thi_hoa", "0966666666",
                "Nữ", LocalDate.of(1983, 11, 12), 890),
                
            createCustomer("Đặng Văn Khải", "dang_van_khai", "0977777777",
                "Nam", LocalDate.of(1991, 4, 9), 180),
                
            createCustomer("Bùi Thị Lan", "bui_thi_lan", "0988888888",
                "Nữ", LocalDate.of(1987, 6, 25), 405),
                
            createCustomer("Ngô Văn Minh", "ngo_van_minh", "0999999999",
                "Nam", LocalDate.of(1993, 9, 14), 95),
                
            createCustomer("Lý Thị Nga", "ly_thi_nga", "0900000000",
                "Nữ", LocalDate.of(1989, 1, 7), 275)
        );

        customerRepository.saveAll(customers);
        log.info("Seeded {} customers", customers.size());
    }

    private void seedStaff() {
        List<Shop> shops = shopRepository.findAll();
        
        List<Staff> staffMembers = Arrays.asList(
            // PetLove Spa staff
            createStaff("Nguyễn Thị Mai", "nguyen_thi_mai", "0801111111",
                "Nữ", LocalDate.of(1994, 3, 20), "Spa Specialist", shops.get(0),
                LocalDate.of(2023, 1, 15), "123 Đường ABC, Quận 1, TP.HCM"),
                
            createStaff("Trần Văn Nam", "tran_van_nam", "0802222222",
                "Nam", LocalDate.of(1991, 7, 12), "Senior Groomer", shops.get(0),
                LocalDate.of(2022, 8, 10), "123 Đường ABC, Quận 1, TP.HCM"),
                
            // Happy Paws Clinic staff
            createStaff("Lê Thị Oanh", "le_thi_oanh", "0803333333",
                "Nữ", LocalDate.of(1986, 11, 5), "Veterinarian", shops.get(1),
                LocalDate.of(2021, 6, 1), "456 Đường DEF, Quận 3, TP.HCM"),
                
            createStaff("Phạm Văn Phúc", "pham_van_phuc", "0804444444",
                "Nam", LocalDate.of(1989, 9, 18), "Vet Assistant", shops.get(1),
                LocalDate.of(2023, 3, 20), "456 Đường DEF, Quận 3, TP.HCM"),
                
            // Furry Friends Hotel staff
            createStaff("Võ Thị Quỳnh", "vo_thi_quynh", "0805555555",
                "Nữ", LocalDate.of(1992, 12, 30), "Hotel Manager", shops.get(2),
                LocalDate.of(2022, 11, 5), "789 Đường GHI, Quận 7, TP.HCM"),
                
            createStaff("Hoàng Văn Sơn", "hoang_van_son", "0806666666",
                "Nam", LocalDate.of(1990, 4, 8), "Pet Caretaker", shops.get(2),
                LocalDate.of(2023, 2, 14), "789 Đường GHI, Quận 7, TP.HCM"),
                
            // Pet Paradise Grooming staff
            createStaff("Đặng Thị Thảo", "dang_thi_thao", "0807777777",
                "Nữ", LocalDate.of(1995, 8, 16), "Senior Stylist", shops.get(3),
                LocalDate.of(2023, 4, 1), "321 Đường JKL, Quận 10, TP.HCM"),
                
            createStaff("Bùi Văn Tùng", "bui_van_tung", "0808888888",
                "Nam", LocalDate.of(1993, 6, 22), "Grooming Assistant", shops.get(3),
                LocalDate.of(2023, 5, 15), "321 Đường JKL, Quận 10, TP.HCM"),
                
            // Healthy Pet Center staff
            createStaff("Ngô Thị Uyên", "ngo_thi_uyen", "0809999999",
                "Nữ", LocalDate.of(1987, 10, 11), "Health Consultant", shops.get(4),
                LocalDate.of(2022, 7, 20), "654 Đường MNO, Quận Bình Thạnh, TP.HCM"),
                
            createStaff("Lý Văn Vũ", "ly_van_vu", "0800000000",
                "Nam", LocalDate.of(1991, 2, 27), "Nutritionist", shops.get(4),
                LocalDate.of(2023, 1, 10), "654 Đường MNO, Quận Bình Thạnh, TP.HCM")
        );

        staffRepository.saveAll(staffMembers);
        log.info("Seeded {} staff members", staffMembers.size());
    }

    private Shop createShop(String shopName, String username, String phone, String description,
                           String status, String bankName, String bankNum, String workingDay,
                           String hotline, String identityCard) {
        Shop shop = new Shop();
        shop.setShopName(shopName);
        shop.setUsername(username);
        shop.setPassword(passwordEncoder.encode("password123"));
        shop.setEmail(username + "@carenest.com");
        shop.setRole(Role.ROLE_SHOP);
        shop.set_active(true);
        shop.setPhone(phone);
        shop.setDescription(description);
        shop.setStatus(status);
        shop.setBankName(bankName);
        shop.setBankNum(bankNum);
        shop.setWorkingDay(workingDay);
        shop.setHotline(hotline);
        shop.setIdentityCard(identityCard);
        return shop;
    }

    private Customer createCustomer(String fullName, String username, String phone, 
                                  String gender, LocalDate birthday, Integer points) {
        Customer customer = new Customer();
        customer.setUsername(fullName);
        customer.setPassword(passwordEncoder.encode("password123"));
        customer.setEmail(username + "@gmail.com");
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.set_active(true);
        customer.setPhone(phone);
        customer.setGender(gender);
        customer.setBirthday(birthday);
        customer.setPoint(points);
        customer.setPlatformServiceId(1L);
        return customer;
    }

    private Staff createStaff(String fullName, String username, String phone, String gender,
                            LocalDate birthday, String position, Shop shop, LocalDate hiredAt,
                            String shopAddress) {
        Staff staff = new Staff();
        staff.setUsername(fullName);
        staff.setPassword(passwordEncoder.encode("password123"));
        staff.setEmail(username + "@" + shop.getUsername() + ".com");
        staff.setRole(Role.ROLE_STAFF);
        staff.set_active(true);
        staff.setPhone(phone);
        staff.setGender(gender);
        staff.setBirthday(birthday);
        staff.setPosition(position);
        staff.setShop(shop);
        staff.setHiredAt(hiredAt);
        staff.setShopAddress(shopAddress);
        staff.setPlatformServiceId(1L);
        return staff;
    }
}
