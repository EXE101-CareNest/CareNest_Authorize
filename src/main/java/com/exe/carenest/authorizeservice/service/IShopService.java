package com.exe.carenest.authorizeservice.service;

import com.exe.carenest.authorizeservice.dto.request.ShopRegistrationRequest;
import com.exe.carenest.authorizeservice.dto.response.ShopResponse;
import com.exe.carenest.authorizeservice.dto.request.ShopUpdateRequest;

import java.util.List;

public interface IShopService {
    ShopResponse getShopById(Long id);
    ShopResponse updateShop(Long id, ShopUpdateRequest request);
    void deleteShop(Long id);
    List<ShopResponse> getAllShops();
    ShopResponse shopRegister(ShopRegistrationRequest request);

    boolean shopLogin(String shopName,String password);
}
