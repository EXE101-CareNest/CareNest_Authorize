package com.exe.carenest.authorizeservice.dto.response;

public record LoginResponse(String accessToken, String refreshToken,String username) {
}
