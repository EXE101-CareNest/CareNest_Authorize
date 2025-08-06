package com.exe.carenest.authorizeservice.ultil;

import com.exe.carenest.authorizeservice.authManagement.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.ShopResponse;
import com.exe.carenest.authorizeservice.authManagement.model.Account;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;

public class UserMapper {



    public static ShopResponse toShopResponse(Shop shop) {
        if (shop == null) return null;
        return ShopResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .owner(shop.getOwner() == null ? null : shop.getOwner().getUsername())
                .status(shop.isStatus())
                .workingDay(shop.getWorkingDays())
                .build();
    }


    public static AccountResponse toAccountResponse(Account account) {
        if (account == null) return null;
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .role(account.getRole())
                .isActive(account.is_active())
                .build();
    }
}
