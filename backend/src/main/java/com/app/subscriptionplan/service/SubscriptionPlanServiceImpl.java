package com.app.subscriptionplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;
import com.app.notification.service.NotificationService;
import com.app.organization.entity.Organization;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationRepository;
import com.app.organization.repository.OrganizationUserRepository;
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

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	@Autowired
	private NotificationService notificationService;

	private Organization getCurrentOrganization() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUserId(user.getId())
			.stream()
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organizationUser.getOrganization();
	}

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

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUserId(user.getId())
			.stream()
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Organization not found"));

		return repository.findByOrganization(organizationUser.getOrganization());
	}

	@Override
	public List<SubscriptionPlan> getPlansByOrganizationId(Long organizationId) {
		Organization organization = organizationRepository.findByIdAndDeletedFalse(organizationId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return repository.findByOrganization(organization);
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

		SubscriptionPlan existingPlan = repository.findByIdAndOrganization(id, organization)
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

		SubscriptionPlan plan = repository.findByIdAndOrganization(id, organization)
				.orElseThrow(() -> new RuntimeException("Plan not found"));

		repository.delete(plan);
	}

}
