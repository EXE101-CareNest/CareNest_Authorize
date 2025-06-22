package com.exe.carenest.authorizeservice.userManagement.service.impl;


import com.exe.carenest.authorizeservice.authManagement.model.Role;
import com.exe.carenest.authorizeservice.infrastructure.exception.ApiException;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.StaffResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.StaffUpdateRequest;
import com.exe.carenest.authorizeservice.userManagement.model.Shop;
import com.exe.carenest.authorizeservice.userManagement.model.Staff;
import com.exe.carenest.authorizeservice.userManagement.repository.ShopRepository;
import com.exe.carenest.authorizeservice.userManagement.repository.StaffRepository;
import com.exe.carenest.authorizeservice.userManagement.service.IStaffService;
import com.exe.carenest.authorizeservice.ultil.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService implements IStaffService {
    
    private final StaffRepository staffRepository;
    private final ShopRepository shopRepository;
    @Override
    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ApiException("STAFF_NOT_FOUND", "Staff not found", 404));
        return UserMapper.toStaffResponse(staff);
    }

    @Override
    public StaffResponse updateStaff(Long id, StaffUpdateRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ApiException("STAFF_NOT_FOUND", "Staff not found", 404));
        
        if (request.username() != null) {
            staff.setUsername(request.username());
        }
        if (request.phone() != null) {
            staff.setPhone(request.phone());
        }
        if (request.gender() != null) {
            staff.setGender(request.gender());
        }
        if (request.position() != null) {
            staff.setPosition(request.position());
        }
        if (request.birthday() != null) {
            staff.setBirthday(request.birthday());
        }
        if (request.shopAddress() != null) {
            staff.setShopAddress(request.shopAddress());
        }
        if (request.platformServiceId() != null) {
            staff.setPlatformServiceId(request.platformServiceId());
        }
        
        Staff savedStaff = staffRepository.save(staff);
        return UserMapper.toStaffResponse(savedStaff);
    }

    @Override
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ApiException("STAFF_NOT_FOUND", "Staff not found", 404));
        staff.set_active(false);
        staffRepository.save(staff);
    }


    @Override
    public List<StaffResponse> getAllStaffByShopId(Long shopId) {
        return staffRepository.findByShopId(shopId).stream()
                .map(UserMapper::toStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void assignStaffToShop(Long staffId, Long shopId) {
        staffRepository.findById(staffId)
                .ifPresentOrElse(
                        staff -> {
                            // Logic khi tìm thấy staff
                            Shop shop = shopRepository.findById(shopId).orElseThrow(
                                    () -> new ApiException("SHOP_NOT_FOUND","Shop not found", 404));

                            staff.setShop(shop);
                            staffRepository.save(staff);
                        },
                        () -> {
                            // Logic khi không tìm thấy staff
                            throw new ApiException("STAFF_NOT_FOUND", "Staff not found", 404);
                        }
                );
    }

    @Override
    public void unassignStaffFromShop(Long staffId, Long shopId){
        staffRepository.findById(staffId)
                .ifPresentOrElse(
                        staff -> {
                            // Logic khi tìm thấy staff
                            Shop shop = shopRepository.findById(shopId).orElseThrow(
                                    () -> new ApiException("SHOP_NOT_FOUND","Shop not found", 404));

                            staff.setShop(null);
                            staffRepository.save(staff);
                        },
                        () -> {
                            // Logic khi không tìm thấy staff
                            throw new ApiException("STAFF_NOT_FOUND", "Staff not found", 404);
                        }
                );
    }
}
