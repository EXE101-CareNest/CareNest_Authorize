package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.user.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean findByPassword(String password);

    Shop findByShopNameAndPassword(String shopName, String password);

    Shop findByShopName(String shopName);
}