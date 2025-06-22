package com.exe.carenest.authorizeservice.userManagement.controller;

import com.exe.carenest.authorizeservice.userManagement.dto.respone.CustomerResponse;
import com.exe.carenest.authorizeservice.userManagement.dto.request.CustomerUpdateRequest;
import com.exe.carenest.authorizeservice.userManagement.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final ICustomerService customerService;

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id,
                                                  @RequestBody CustomerUpdateRequest request) {
        CustomerResponse customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
}
