package com.app.subscriptionplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.organization.entity.Organization;
import com.app.organization.repository.OrganizationRepository;
import com.app.organization.service.OrganizationService;
import com.app.subscriptionplan.entity.SubscriptionPlan;
import com.app.subscriptionplan.repo.SubscriptionPlanRepo;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

	@Autowired
	private SubscriptionPlanRepo repository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrganizationRepository organizationRepository;

	@Transactional
	@Override
	public SubscriptionPlan createPlan(SubscriptionPlan plan) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (plan.getOrganization() == null || plan.getOrganization().getId() == null) {
			throw new RuntimeException("Organization is required.");
		}

		Organization organization = organizationRepository.findByIdAndDeletedFalse(plan.getOrganization().getId())
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		organizationService.validateOrganizationAccess(organization.getId(), user.getId());

		plan.setOrganization(organization);

		return repository.save(plan);
	}

	@Override
	public List<SubscriptionPlan> getAllPlans() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	public SubscriptionPlan getPlanById(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id).orElse(null);
	}

	@Override
	public SubscriptionPlan updatePlan(Long id, SubscriptionPlan plan) {
		// TODO Auto-generated method stub
		SubscriptionPlan existingPlan = repository.findById(id).orElse(null);

		if (existingPlan == null) {
			return null;
		}

		existingPlan.setPlanName(plan.getPlanName());
		existingPlan.setDescription(plan.getDescription());
		existingPlan.setPrice(plan.getPrice());
		existingPlan.setBillingCycle(plan.getBillingCycle());
		existingPlan.setMaxUsers(plan.getMaxUsers());
		existingPlan.setStorageLimitGb(plan.getStorageLimitGb());
		existingPlan.setFeatures(plan.getFeatures());
		existingPlan.setActive(plan.getActive());

		return repository.save(existingPlan);
	}

	@Override
	public void deletePlan(Long id) {
		// TODO Auto-generated method stub
		repository.deleteById(id);

	}

}
