package com.app.billing.service;

import com.app.billing.BillingCheckoutRequest;
import com.app.billing.BillingResponse;

public interface BillingService {

	
	BillingResponse getBillingPage(String token);

	Object checkout(String token, BillingCheckoutRequest request);
}