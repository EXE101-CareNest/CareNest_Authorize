package com.exe.carenest.authorizeservice.authManagement.dto.request;

import com.exe.carenest.authorizeservice.authManagement.model.Roles;

public record RegisterRequest(String username, String email, String password) {}