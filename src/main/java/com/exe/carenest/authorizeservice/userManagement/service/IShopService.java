package com.exe.carenest.authorizeservice.userManagement.service;

import com.exe.carenest.authorizeservice.userManagement.dto.respone.ShopResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.ShopUpdateRequest;

import java.util.List;

public interface IShopService {
    ShopResponse getShopById(Long id);
    ShopResponse updateShop(Long id, ShopUpdateRequest request);
    void deleteShop(Long id);
    List<ShopResponse> getAllShops();
}
