package com.exe.carenest.authorizeservice.repository;

import com.exe.carenest.authorizeservice.user.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean findByPassword(String password);

    Shop findByShopNameAndPassword(String shopName, String password);

    Optional<Shop> findByShopName(String shopName);
}