package com.app.subscriptionplan.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.subscriptionplan.entity.SubscriptionPlan;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan, Long> {

    boolean existsByPlanName(String planName);
    
    List<SubscriptionPlan> findByOrganizationId(Long organizationId);

}