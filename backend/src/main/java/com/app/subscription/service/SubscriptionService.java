package com.app.subscription.service;

import java.util.List;

import com.app.subscription.entity.SubscriptionEntity;

public interface SubscriptionService {

	SubscriptionEntity createSubscription(SubscriptionEntity subscription);

	List<SubscriptionEntity> getAllSubscriptions();

	SubscriptionEntity getSubscriptionById(Long id);

    List<SubscriptionEntity> getSubscriptionsByOrganizationId(Long organizationId);

	SubscriptionEntity updateSubscription(Long id, SubscriptionEntity subscription);

	void deleteSubscription(Long id);

	SubscriptionEntity renewSubscription(Long id);
	
	SubscriptionEntity createSubscription(SubscriptionEntity subscription, Long organizationId);

}