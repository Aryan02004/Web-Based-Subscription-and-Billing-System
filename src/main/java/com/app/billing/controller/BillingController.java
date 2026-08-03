package com.app.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.billing.BillingCheckoutRequest;
import com.app.billing.BillingResponse;
import com.app.billing.service.BillingService;

@RestController
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @GetMapping("/{token}")
    public BillingResponse getBillingPage(@PathVariable String token) {

        return billingService.getBillingPage(token);
    }
    
    @PostMapping("/{token}/subscribe")
    public Object checkout(@PathVariable String token,
                           @RequestBody BillingCheckoutRequest request) {

        return billingService.checkout(token, request);
    }
}