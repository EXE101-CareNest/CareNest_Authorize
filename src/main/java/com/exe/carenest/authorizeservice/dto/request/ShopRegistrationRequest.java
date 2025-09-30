package com.exe.carenest.authorizeservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShopRegistrationRequest(
        @NotBlank(message = "Tên cửa hàng không được để trống")
        @Size(min = 1, max = 50, message = "Tên cửa hàng phải từ 1-50 ký tự")
        String shopName,
        
        String description,
        
        @NotBlank(message = "Mật khẩu cửa hàng không được để trống")
        String shopPassword,
        
        String imgUrl,
        
        String workingDays,

        String shopId
) {}