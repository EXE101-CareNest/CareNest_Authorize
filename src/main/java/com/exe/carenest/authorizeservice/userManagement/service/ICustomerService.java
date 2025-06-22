package com.exe.carenest.authorizeservice.userManagement.service;

import com.exe.carenest.authorizeservice.userManagement.dto.respone.CustomerResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.CustomerUpdateRequest;

import java.util.List;

public interface ICustomerService {
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request);
    void deleteCustomer(Long id);
    List<CustomerResponse> getAllCustomers(); 
}
