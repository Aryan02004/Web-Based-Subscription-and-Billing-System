package com.app.subscriptionplan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.subscriptionplan.entity.SubscriptionPlan;
import com.app.subscriptionplan.service.SubscriptionPlanService;

@RestController
@RequestMapping("/api/plans")
public class SubscriptionPlanController {

    @Autowired
    private SubscriptionPlanService service;

    @PostMapping
    public SubscriptionPlan createPlan(@RequestBody SubscriptionPlan plan) {
        return service.createPlan(plan);
    }

    @GetMapping
    public List<SubscriptionPlan> getAllPlans() {
        return service.getAllPlans();
    }

    @GetMapping("/{id}")
    public SubscriptionPlan getPlanById(@PathVariable Long id) {
        return service.getPlanById(id);
    }

    @PutMapping("/{id}")
    public SubscriptionPlan updatePlan(@PathVariable Long id,
                                       @RequestBody SubscriptionPlan plan) {
        return service.updatePlan(id, plan);
    }

    @DeleteMapping("/{id}")
    public String deletePlan(@PathVariable Long id) {

        service.deletePlan(id);

        return "Subscription Plan Deleted Successfully";
    }
}