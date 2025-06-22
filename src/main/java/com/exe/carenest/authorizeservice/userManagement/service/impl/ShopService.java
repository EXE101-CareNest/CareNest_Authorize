package com.exe.carenest.authorizeservice.userManagement.service.impl;

import com.exe.carenest.authorizeservice.infrastructure.exception.ApiException;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.ShopResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.ShopUpdateRequest;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import com.exe.carenest.authorizeservice.userManagement.repository.ShopRepository;
import com.exe.carenest.authorizeservice.userManagement.service.IAccountService;
import com.exe.carenest.authorizeservice.userManagement.service.IShopService;
import com.exe.carenest.authorizeservice.ultil.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService implements IShopService {
    
    private final ShopRepository shopRepository;
    private final IAccountService accountService;

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
        if (request.phone() != null) {
            shop.setPhone(request.phone());
        }
        if (request.description() != null) {
            shop.setDescription(request.description());
        }
        if (request.status() != null) {
            shop.setStatus(request.status());
        }
        if (request.bankName() != null) {
            shop.setBankName(request.bankName());
        }
        if (request.bankNum() != null) {
            shop.setBankNum(request.bankNum());
        }
        if (request.workingDay() != null) {
            shop.setWorkingDay(request.workingDay());
        }
        if (request.hotline() != null) {
            shop.setHotline(request.hotline());
        }
        if (request.identityCard() != null) {
            shop.setIdentityCard(request.identityCard());
        }
        
        Shop savedShop = shopRepository.save(shop);
        return UserMapper.toShopResponse(savedShop);
    }

    @Override
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ApiException("SHOP_NOT_FOUND", "Shop not found", 404));
        shop.set_active(false);
        shopRepository.save(shop);
    }

    @Override
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(UserMapper::toShopResponse)
                .collect(Collectors.toList());
    }
}
