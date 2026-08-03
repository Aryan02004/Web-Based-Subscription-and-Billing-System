package com.app.publicapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.organization.entity.Organization;
import com.app.organization.repository.OrganizationRepository;
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
    public ResponseEntity<?> getOrganizationPlans(
            @PathVariable String token) {

        Organization organization = organizationRepository
                .findByPublicLinkToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid public link"));

        if (!Boolean.TRUE.equals(organization.getLinkActive())) {
            throw new RuntimeException("This public link is disabled.");
        }

        List<SubscriptionPlan> plans =
                subscriptionPlanRepo.findByOrganization(organization);

        return ResponseEntity.ok(plans);
    }
}