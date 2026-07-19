package com.app.subscription.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{


    @Autowired
    private SubscriptionRepository repository;

	@Override
	public SubscriptionEntity createSubscription(SubscriptionEntity subscription) {
		// TODO Auto-generated method stub
		   return repository.save(subscription);
	}

	@Override
	public List<SubscriptionEntity> getAllSubscriptions() {
		// TODO Auto-generated method stub
	    return repository.findAll();
	}

	@Override
	public SubscriptionEntity getSubscriptionById(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id).orElse(null);
	}

	@Override
	public SubscriptionEntity updateSubscription(Long id, SubscriptionEntity subscription) {
		// TODO Auto-generated method stub

        SubscriptionEntity existingSubscription = repository.findById(id).orElse(null);

        if (existingSubscription == null) {
            return null;
        }

        existingSubscription.setOrganizationId(subscription.getOrganizationId());
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
		  repository.deleteById(id);
		
	}

}
