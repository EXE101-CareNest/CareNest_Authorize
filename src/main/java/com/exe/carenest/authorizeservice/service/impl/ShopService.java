package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.auth.model.RegisterStatus;
import com.exe.carenest.authorizeservice.dto.request.ShopRegistrationRequest;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.exception.ShopNotFoundException;
import com.exe.carenest.authorizeservice.exception.InvalidShopCredentialsException;
import com.exe.carenest.authorizeservice.exception.DuplicateShopNameException;
import com.exe.carenest.authorizeservice.exception.ShopRegistrationException;
import com.exe.carenest.authorizeservice.repository.UserRepository;
import com.exe.carenest.authorizeservice.ultil.UserMapper;
import com.exe.carenest.authorizeservice.dto.request.ShopUpdateRequest;
import com.exe.carenest.authorizeservice.dto.response.ShopResponse;
import com.exe.carenest.authorizeservice.user.model.Shop;
import com.exe.carenest.authorizeservice.repository.ShopRepository;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService implements IShopService {

    private final ShopRepository shopRepository;
    private final IAccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException("Không tìm thấy cửa hàng với ID: " + id));
        return UserMapper.toShopResponse(shop);
    }

    @Override
    public ShopResponse updateShop(Long id, ShopUpdateRequest request) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException("Không tìm thấy cửa hàng với ID: " + id));

        if (request.shopName() != null) {
            shop.setShopName(request.shopName());
        }
        if (request.description() != null) {
            shop.setDescription(request.description());
        }
        shop.setStatus(request.status());
        if (request.workingDays() != null) {
            shop.setWorkingDays(request.workingDays());
        }
        if (request.password() != null) {
            shop.setPassword(passwordEncoder.encode(request.password()));
        }


        Shop savedShop = shopRepository.save(shop);
        return UserMapper.toShopResponse(savedShop);
    }

    @Override
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException("Không tìm thấy cửa hàng với ID: " + id));
        shop.setStatus(false);
        shopRepository.save(shop);
    }

    @Override
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(UserMapper::toShopResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShopResponse shopRegister(ShopRegistrationRequest request) {
        try {
            // Validate input
            if (request.shopName() == null || request.shopName().trim().isEmpty()) {
                throw new ShopRegistrationException("Tên cửa hàng không được để trống");
            }
            if (request.shopPassword() == null || request.shopPassword().trim().isEmpty()) {
                throw new ShopRegistrationException("Mật khẩu cửa hàng không được để trống");
            }
            
            // Lấy current user từ security context
            Account currentUser = accountService.getCurrentUser();
            
            // Kiểm tra user đã verify eKYC chưa
            if (currentUser.getRegisterStatus() != RegisterStatus.EKYC_VERIFIED) {
                throw new ShopRegistrationException("Tài khoản chưa verify eKYC");
            }
            
            // Kiểm tra tên shop đã tồn tại chưa
            if (shopRepository.findByShopName(request.shopName()).isPresent()) {
                throw new DuplicateShopNameException("Tên cửa hàng '" + request.shopName() + "' đã tồn tại");
            }
            
            // Tạo shop mới
            Shop shop = new Shop();
            shop.setShopName(request.shopName().trim());
            shop.setDescription(request.description());
            shop.setPassword(passwordEncoder.encode(request.shopPassword()));
            shop.setImgUrl(request.imgUrl());
            shop.setWorkingDays(request.workingDays());
            shop.setStatus(true);
            shop.setOwner(currentUser);
            
            // Lưu shop
            Shop savedShop = shopRepository.save(shop);
            
            // Cập nhật register status của account
            currentUser.setRegisterStatus(RegisterStatus.SHOP_REGISTRATION_COMPLETED);
            userRepository.save(currentUser);
            
            return UserMapper.toShopResponse(savedShop);
        } catch (Exception e) {
            if (e instanceof ShopRegistrationException || e instanceof DuplicateShopNameException) {
                throw e;
            }
            throw new ShopRegistrationException("Lỗi không xác định trong quá trình đăng ký: " + e.getMessage());
        }
    }

    @Override
    public boolean shopLogin(String shopName, String password){
        if (shopName == null || shopName.trim().isEmpty()) {
            throw new InvalidShopCredentialsException("Tên cửa hàng không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidShopCredentialsException("Mật khẩu không được để trống");
        }
        
        Shop shop = shopRepository.findByShopName(shopName)
                .orElseThrow(() -> new InvalidShopCredentialsException("Không tìm thấy cửa hàng với tên: " + shopName));
        
        if (!passwordEncoder.matches(password, shop.getPassword())) {
            throw new InvalidShopCredentialsException("Mật khẩu cửa hàng không đúng");
        }
        
        return true; // đăng nhập thành công
    }
}
