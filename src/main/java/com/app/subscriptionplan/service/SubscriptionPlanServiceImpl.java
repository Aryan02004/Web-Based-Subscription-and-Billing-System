package com.app.subscriptionplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.organization.entity.Organization;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationUserRepository;
import com.app.subscriptionplan.Entity.SubscriptionPlan;
import com.app.subscriptionplan.repo.SubscriptionPlanRepo;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

	@Autowired
	private SubscriptionPlanRepo repository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	private Organization getCurrentOrganization() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organizationUser.getOrganization();
	}

	@Override
	public SubscriptionPlan createPlan(SubscriptionPlan plan) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		plan.setOrganization(organizationUser.getOrganization());

		return repository.save(plan);
	}

	@Override
	public List<SubscriptionPlan> getAllPlans() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return repository.findByOrganization(organizationUser.getOrganization());
	}

	
	@Override
	public SubscriptionPlan getPlanById(Long id) {
		// TODO Auto-generated method stub
		Organization organization = getCurrentOrganization();

		return repository.findByIdAndOrganization(id, organization)
		        .orElseThrow(() -> new RuntimeException("Plan not found"));
	}

	@Override
	public SubscriptionPlan updatePlan(Long id, SubscriptionPlan plan) {
		Organization organization = getCurrentOrganization();

		SubscriptionPlan existingPlan =
		repository.findByIdAndOrganization(id, organization)
		        .orElseThrow(() -> new RuntimeException("Plan not found"));


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
		Organization organization = getCurrentOrganization();

		SubscriptionPlan plan =
		repository.findByIdAndOrganization(id, organization)
		        .orElseThrow(() -> new RuntimeException("Plan not found"));

		repository.delete(plan);
	}

}
