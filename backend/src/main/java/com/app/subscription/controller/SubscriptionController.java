package com.app.subscription.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.subscription.service.SubscriptionService;
import com.app.subscription.entity.SubscriptionEntity;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

	@Autowired
	private SubscriptionService service;

	@PostMapping
	public SubscriptionEntity createSubscription(@RequestBody SubscriptionEntity subscription) {
		return service.createSubscription(subscription);
	}

	@GetMapping
	public List<SubscriptionEntity> getAllSubscriptions(@RequestParam(required = false) Long organizationId) {
		return organizationId != null
				? service.getSubscriptionsByOrganizationId(organizationId)
				: service.getAllSubscriptions();
	}

	@GetMapping("/{id}")
	public SubscriptionEntity getSubscriptionById(@PathVariable Long id) {
		return service.getSubscriptionById(id);
	}

	@PutMapping("/{id}")
	public SubscriptionEntity updateSubscription(@PathVariable Long id, @RequestBody SubscriptionEntity subscription) {
		return service.updateSubscription(id, subscription);
	}

	@DeleteMapping("/{id}")
	public String deleteSubscription(@PathVariable Long id) {
		service.deleteSubscription(id);
		return "Subscription Deleted Successfully";
	}

	@PatchMapping("/{id}/renew")
	public SubscriptionEntity renewSubscription(@PathVariable Long id) {

		return service.renewSubscription(id);
	}
}