package com.app.subscriptionplan.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.organization.entity.Organization;
import com.app.subscriptionplan.entity.SubscriptionPlan;


@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan, Long> {

    boolean existsByPlanName(String planName);
    
    List<SubscriptionPlan> findByOrganizationId(Long organizationId);

    List<SubscriptionPlan> findByOrganization(Organization organization);
    Optional<SubscriptionPlan> findByIdAndOrganization(Long id, Organization organization);
    Optional<SubscriptionPlan> findByIdAndOrganizationAndActiveTrue(Long id, Organization organization);
    
    List<SubscriptionPlan> findByOrganizationAndActiveTrue(Organization organization);
}