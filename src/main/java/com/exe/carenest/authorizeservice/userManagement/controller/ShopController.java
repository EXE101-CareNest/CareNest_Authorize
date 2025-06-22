package com.exe.carenest.authorizeservice.userManagement.controller;

import com.exe.carenest.authorizeservice.userManagement.dto.respone.ShopResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.ShopUpdateRequest;
import com.exe.carenest.authorizeservice.userManagement.service.IShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Tag(name = "Shop Management", description = "APIs for managing shops")
public class ShopController {

    private final IShopService shopService;

    @GetMapping("/{id}")
    @Operation(summary = "Get shop by ID")
    public ResponseEntity<ShopResponse> getShop(@PathVariable Long id) {
        ShopResponse shop = shopService.getShopById(id);
        return ResponseEntity.ok(shop);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shop")
    public ResponseEntity<ShopResponse> updateShop(@PathVariable Long id,
                                                  @RequestBody ShopUpdateRequest request) {
        ShopResponse shop = shopService.updateShop(id, request);
        return ResponseEntity.ok(shop);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shop")
    public ResponseEntity<String> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.ok("Shop deleted successfully");
    }

    @GetMapping
    @Operation(summary = "Get all shops")
    public ResponseEntity<List<ShopResponse>> getAllShops() {
        List<ShopResponse> shops = shopService.getAllShops();
        return ResponseEntity.ok(shops);
    }
}
