package com.exe.carenest.authorizeservice.userManagement.repository;

import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean findByPassword(String password);

    Shop findByShopNameAndPassword(String shopName, String password);

    Shop findByShopName(String shopName);
}