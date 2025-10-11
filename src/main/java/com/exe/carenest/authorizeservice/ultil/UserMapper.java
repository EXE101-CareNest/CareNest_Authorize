package com.exe.carenest.authorizeservice.ultil;

import com.exe.carenest.authorizeservice.auth.model.Account;
import com.exe.carenest.authorizeservice.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.dto.response.ShopResponse;
import com.exe.carenest.authorizeservice.user.model.Shop;

import java.util.Objects;

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
                .role(account.getRole() == null ? null : account.getRole().getName())
                .isActive(account.is_active())
                .address(account.getPermanentAddress() != null ? account.getPermanentAddress() : "Không có địa chỉ")
                .build();
    }
}
