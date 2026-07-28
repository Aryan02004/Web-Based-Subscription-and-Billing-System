package com.app.subscription.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationUserRepository;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{


    @Autowired
    private SubscriptionRepository repository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    private Long getCurrentOrganizationId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

		User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrganizationUser organizationUser =
                organizationUserRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return organizationUser.getOrganization().getId();
    }
	@Override
	public SubscriptionEntity createSubscription(SubscriptionEntity subscription) {
		// TODO Auto-generated method stub
		subscription.setOrganizationId(getCurrentOrganizationId());

		return repository.save(subscription);
	}

	@Override
	public List<SubscriptionEntity> getAllSubscriptions() {
		// TODO Auto-generated method stub
		return repository.findByOrganizationId(getCurrentOrganizationId());
	}

	@Override
	public SubscriptionEntity getSubscriptionById(Long id) {
		// TODO Auto-generated method stub
		return repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
		        .orElseThrow(() -> new RuntimeException("Subscription not found"));
	}

	@Override
	public SubscriptionEntity updateSubscription(Long id, SubscriptionEntity subscription) {
		// TODO Auto-generated method stub
		SubscriptionEntity existingSubscription =
		        repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
		        .orElseThrow(() -> new RuntimeException("Subscription not found"));

        
        existingSubscription.setCustomerId(subscription.getCustomerId());
        existingSubscription.setPlan(subscription.getPlan());
        existingSubscription.setStartDate(subscription.getStartDate());
        existingSubscription.setEndDate(subscription.getEndDate());
        existingSubscription.setNextBillingDate(subscription.getNextBillingDate());
        existingSubscription.setRenewalDate(subscription.getRenewalDate());
        existingSubscription.setCancelledAt(subscription.getCancelledAt());
        existingSubscription.setStatus(subscription.getStatus());

        return repository.save(existingSubscription);
	}

	@Override
	public void deleteSubscription(Long id) {
		// TODO Auto-generated method stub
		SubscriptionEntity subscription =
		        repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
		        .orElseThrow(() -> new RuntimeException("Subscription not found"));

		repository.delete(subscription);
		
	}

}
