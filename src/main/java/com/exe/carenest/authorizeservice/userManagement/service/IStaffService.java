package com.exe.carenest.authorizeservice.userManagement.service;

import com.exe.carenest.authorizeservice.authManagement.model.Role;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.StaffResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.StaffUpdateRequest;

import java.util.List;

public interface IStaffService {
    StaffResponse getStaffById(Long id);
    StaffResponse updateStaff(Long id, StaffUpdateRequest request);
    void deleteStaff(Long id);
    List<StaffResponse> getAllStaffByShopId(Long shopId); //Use for shop owner

    void assignStaffToShop(Long staffId, Long shopId);

    void unassignStaffFromShop(Long staffId, Long shopId);
}
