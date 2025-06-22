package com.exe.carenest.authorizeservice.userManagement.service.impl;

import com.exe.carenest.authorizeservice.infrastructure.exception.ApiException;
import com.exe.carenest.authorizeservice.userManagement.dto.respone.CustomerResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.CustomerUpdateRequest;
import com.exe.carenest.authorizeservice.userManagement.model.Customer;
import com.exe.carenest.authorizeservice.userManagement.repository.CustomerRepository;
import com.exe.carenest.authorizeservice.userManagement.service.ICustomerService;
import com.exe.carenest.authorizeservice.ultil.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {
    
    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("CUSTOMER_NOT_FOUND", "Customer not found", 404));
        return UserMapper.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("CUSTOMER_NOT_FOUND", "Customer not found", 404));
        
        if (request.username() != null) {
            customer.setUsername(request.username());
        }
        if (request.phone() != null) {
            customer.setPhone(request.phone());
        }
        if (request.gender() != null) {
            customer.setGender(request.gender());
        }
        if (request.birthday() != null) {
            customer.setBirthday(request.birthday());
        }
        if (request.point() != null) {
            customer.setPoint(request.point());
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return UserMapper.toCustomerResponse(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("CUSTOMER_NOT_FOUND", "Customer not found", 404));
        customer.set_active(false);
        customerRepository.save(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(UserMapper::toCustomerResponse)
                .collect(Collectors.toList());
    }
}
