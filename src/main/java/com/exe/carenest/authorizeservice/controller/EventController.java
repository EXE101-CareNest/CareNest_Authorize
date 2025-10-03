package com.exe.carenest.authorizeservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EventController {
//    private final CustomerService customerService;
//    @GetMapping("/test")
//    public List<Customer> reload() {
//        return customerService.findAllCustomersList();
//    }


}
