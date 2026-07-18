package com.app.subscriptionplan.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.subscriptionplan.Entity.SubscriptionPlan;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan, Long> {

    boolean existsByPlanName(String planName);

}