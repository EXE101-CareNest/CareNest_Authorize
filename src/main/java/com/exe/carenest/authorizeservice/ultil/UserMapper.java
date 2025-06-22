package com.exe.carenest.authorizeservice.ultil;

import com.exe.carenest.authorizeservice.authManagement.dto.response.AccountResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.CustomerResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.ShopResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.StaffResponse;
import com.exe.carenest.authorizeservice.authManagement.model.Account;
import com.exe.carenest.authorizeservice.userManagement.model.Customer;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import com.exe.carenest.authorizeservice.userManagement.model.Staff;

public class UserMapper {

    public static CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) return null;
        return CustomerResponse.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .role(customer.getRole())
                .gender(customer.getGender())
                .phone(customer.getPhone())
                .birthday(customer.getBirthday())
                .point(customer.getPoint())
                .platformServiceId(customer.getPlatformServiceId())
                .build();
    }

    public static ShopResponse toShopResponse(Shop shop) {
        if (shop == null) return null;
        return ShopResponse.builder()
                .id(shop.getId())
                .username(shop.getUsername())
                .email(shop.getEmail())
                .role(shop.getRole())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .phone(shop.getPhone())
                .status(shop.getStatus())
                .bankName(shop.getBankName())
                .bankNum(shop.getBankNum())
                .workingDay(shop.getWorkingDay())
                .hotline(shop.getHotline())
                .identityCard(shop.getIdentityCard())
                .build();
    }

    public static StaffResponse toStaffResponse(Staff staff) {
        if (staff == null) return null;
        return StaffResponse.builder()
                .id(staff.getId())
                .username(staff.getUsername())
                .email(staff.getEmail())
                .role(staff.getRole())
                .gender(staff.getGender())
                .phone(staff.getPhone())
                .position(staff.getPosition())
                .birthday(staff.getBirthday())
                .hiredAt(staff.getHiredAt())
                .shopAddress(staff.getShopAddress())
                .platformServiceId(staff.getPlatformServiceId())
                .shopId(staff.getShop() != null ? staff.getShop().getId() : null)
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
