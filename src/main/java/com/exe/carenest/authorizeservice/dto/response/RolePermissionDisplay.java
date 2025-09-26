package com.exe.carenest.authorizeservice.dto.response;

import com.exe.carenest.authorizeservice.auth.model.HttpPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionDisplay {
    private String moduleUrl;
    private String moduleName;
    private String permissions; // "READ,CREATE,UPDATE,DELETE"

    
    public List<HttpPermission> getPermissionsList() {
        return Arrays.stream(permissions.split(","))
                .map(HttpPermission::valueOf)
                .collect(Collectors.toList());
    }
}
