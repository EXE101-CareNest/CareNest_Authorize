package com.exe.carenest.authorizeservice.service.impl;

import com.exe.carenest.authorizeservice.exception.ApiException;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService implements IShopService {

    private final ShopRepository shopRepository;
    private final IAccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ApiException("SHOP_NOT_FOUND", "Shop not found", 404));
        return UserMapper.toShopResponse(shop);
    }

    @Override
    public ShopResponse updateShop(Long id, ShopUpdateRequest request) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ApiException("SHOP_NOT_FOUND", "Shop not found", 404));

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
                .orElseThrow(() -> new ApiException("SHOP_NOT_FOUND", "Shop not found", 404));
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
    public boolean shopLogin(String shopName, String password){
        Shop shop = shopRepository.findByShopName(shopName);
        return passwordEncoder.matches(password, shop.getPassword()); // đăng nhập
    }
}
