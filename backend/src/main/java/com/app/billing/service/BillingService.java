package com.app.billing.service;

import com.app.billing.BillingCheckoutRequest;
import com.app.billing.BillingResponse;

public interface BillingService {

	BillingResponse getBillingPage(String token);

	Object checkout(String token, BillingCheckoutRequest request);

	com.app.organization.entity.Organization getOrganizationByToken(String token);

	Object verifyPayment(Long paymentId, com.app.payment.request.RazorpayVerificationRequest request, Long organizationId);
}