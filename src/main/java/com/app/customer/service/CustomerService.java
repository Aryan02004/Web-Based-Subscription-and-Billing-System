package com.app.customer.service;

import java.util.List;

import com.app.customer.entity.CustomerEntity;

public interface CustomerService {

    CustomerEntity createCustomer(CustomerEntity customer);

    List<CustomerEntity> getAllCustomers();

    CustomerEntity getCustomerById(Long id);

    CustomerEntity updateCustomer(Long id, CustomerEntity customer);

    void deleteCustomer(Long id);
}