package com.app.subscriptionplan.service;

import java.util.List;

import com.app.subscriptionplan.Entity.SubscriptionPlan;


public interface SubscriptionPlanService {

    SubscriptionPlan createPlan(SubscriptionPlan plan);

    List<SubscriptionPlan> getAllPlans();

    SubscriptionPlan getPlanById(Long id);

    SubscriptionPlan updatePlan(Long id, SubscriptionPlan plan);

    void deletePlan(Long id);

}

