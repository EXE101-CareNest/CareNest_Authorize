package com.exe.carenest.authorizeservice.controller;

import com.exe.carenest.authorizeservice.dto.request.RegisterRequest;
import com.exe.carenest.authorizeservice.dto.request.ShopRegistrationRequest;
import com.exe.carenest.authorizeservice.dto.response.ShopResponse;
import com.exe.carenest.authorizeservice.service.IAccountService;
import com.exe.carenest.authorizeservice.service.IShopService;
import com.exe.carenest.authorizeservice.service.OTPService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shop Account Management", description = "APIs for shop registration and management")
@PreAuthorize("hasAuthority('ROLE_SHOP')")
public class ShopAccountController {
    private final IAccountService accountService;
    private final IShopService shopService;
    private final OTPService otpService;

    public ShopAccountController(IAccountService accountService, IShopService shopService, OTPService otpService) {
        this.accountService = accountService;
        this.shopService = shopService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void createShopAccount(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        accountService.createAccount(request, "ROLE_SHOP");
        String otpToken = otpService.sendRegistrationOtp(request.email());
        response.setHeader("X-Key-APT", otpToken);
    }

    @PostMapping("/information")
    @ResponseStatus(HttpStatus.CREATED)
    public ShopResponse shopRegister(@Valid @RequestBody ShopRegistrationRequest request) {
        return shopService.shopRegister(request);
    }
}
