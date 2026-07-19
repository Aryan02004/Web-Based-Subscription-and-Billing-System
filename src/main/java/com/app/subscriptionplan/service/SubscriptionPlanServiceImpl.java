package com.app.subscriptionplan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.subscriptionplan.Entity.SubscriptionPlan;
import com.app.subscriptionplan.repo.SubscriptionPlanRepo;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

	 @Autowired
	    private SubscriptionPlanRepo repository;
	
	@Override
	public SubscriptionPlan createPlan(SubscriptionPlan plan) {
		// TODO Auto-generated method stub
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
