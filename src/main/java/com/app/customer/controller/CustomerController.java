package com.app.customer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.customer.entity.CustomerEntity;
import com.app.customer.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping
    public CustomerEntity createCustomer(@RequestBody CustomerEntity customer) {
        return service.createCustomer(customer);
    }

    @GetMapping
    public List<CustomerEntity> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerEntity getCustomerById(@PathVariable Long id) {
        return service.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public CustomerEntity updateCustomer(@PathVariable Long id,
                                         @RequestBody CustomerEntity customer) {
        return service.updateCustomer(id, customer);
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return "Customer Deleted Successfully";
    }
}