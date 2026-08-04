package com.app.publicapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.organization.entity.Organization;
import com.app.organization.repository.OrganizationRepository;
import com.app.publicapi.dto.PublicOrganizationPlanResponse;
import com.app.publicapi.dto.PublicOrganizationResponse;
import com.app.publicapi.dto.PublicSubscriptionPlanResponse;
import com.app.subscriptionplan.entity.SubscriptionPlan;
import com.app.subscriptionplan.repo.SubscriptionPlanRepo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final OrganizationRepository organizationRepository;
    private final SubscriptionPlanRepo subscriptionPlanRepo;

    @GetMapping("/org/{token}")
    public ResponseEntity<PublicOrganizationPlanResponse> getOrganizationPlans(
            @PathVariable String token) {

        Organization organization = organizationRepository
                .findByPublicLinkToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid public link"));

        if (!Boolean.TRUE.equals(organization.getLinkActive())) {
            throw new RuntimeException("This public link is disabled.");
        }

        List<SubscriptionPlan> plans =
                subscriptionPlanRepo.findByOrganizationAndActiveTrue(organization);

        PublicOrganizationResponse organizationData = new PublicOrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getIndustry(),
                null);

        List<PublicSubscriptionPlanResponse> planDtos = plans.stream()
                .map(plan -> new PublicSubscriptionPlanResponse(
                        plan.getId(),
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getPrice(),
                        plan.getMaxUsers(),
                        plan.getStorageLimitGb(),
                        plan.getFeatures(),
                        plan.getActive(),
                        plan.getBillingCycle() != null ? plan.getBillingCycle().name() : null))
                .collect(Collectors.toList());

        PublicOrganizationPlanResponse response = new PublicOrganizationPlanResponse(
                organizationData,
                planDtos);

        return ResponseEntity.ok(response);
    }
}
