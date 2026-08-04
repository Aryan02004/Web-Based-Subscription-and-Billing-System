package com.app.billing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.billing.BillingCheckoutRequest;
import com.app.billing.BillingResponse;
import com.app.customer.entity.CustomerEntity;
import com.app.customer.repository.CustomerRepository;
import com.app.customer.service.CustomerService;
import com.app.invoice.entity.InvoiceEntity;
import com.app.invoice.repository.InvoiceRepository;
import com.app.invoice.service.InvoiceService;
import com.app.organization.entity.Organization;
import com.app.organization.repository.OrganizationRepository;
import com.app.payment.entity.Payment;
import com.app.payment.enums.PaymentMethod.PaymentMethod;
import com.app.payment.razorpay.RazorpayService;
import com.app.payment.repository.PaymentRepository;
import com.app.payment.service.PaymentService;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;
import com.app.subscription.service.SubscriptionService;
import com.app.subscriptionplan.entity.SubscriptionPlan;
import com.app.subscriptionplan.repo.SubscriptionPlanRepo;

@Service
public class BillingServiceImpl implements BillingService {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private SubscriptionPlanRepo subscriptionPlanRepo;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private RazorpayService razorpayService;
    
	@Value("${razorpay.key.id}")
	private String razorpayKeyId;

	@Override
	public BillingResponse getBillingPage(String token) {

		Organization organization = organizationRepository.findByPublicLinkToken(token)
				.orElseThrow(() -> new RuntimeException("Billing link not found"));

		if (Boolean.FALSE.equals(organization.getLinkActive())) {
			throw new RuntimeException("Billing link is disabled");
		}

		List<SubscriptionPlan> plans = subscriptionPlanRepo.findByOrganizationAndActiveTrue(organization);

		return new BillingResponse(organization.getName(), plans);
	}

	@Override
	public Object checkout(String token, BillingCheckoutRequest request) {

		Organization organization = organizationRepository.findByPublicLinkToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid billing link"));

		SubscriptionPlan plan = subscriptionPlanRepo.findByIdAndOrganizationAndActiveTrue(request.getPlanId(), organization)
				.orElseThrow(() -> new RuntimeException("Plan not found for this organization"));
		CustomerEntity customer = new CustomerEntity();

		customer.setOrganizationId(organization.getId());
		customer.setFirstName(request.getFirstName());
		customer.setLastName(request.getLastName());
		customer.setEmail(request.getEmail());
		customer.setPhone(request.getPhone());

		CustomerEntity savedCustomer = customerService.createCustomer(customer, organization.getId());

		SubscriptionEntity subscription = new SubscriptionEntity();

		subscription.setOrganizationId(organization.getId());
		subscription.setCustomerId(savedCustomer.getId());
		subscription.setPlan(plan);

		subscription.setStartDate(java.time.LocalDate.now());
		subscription.setEndDate(java.time.LocalDate.now().plusMonths(1));
		subscription.setNextBillingDate(java.time.LocalDate.now().plusMonths(1));
		subscription.setRenewalDate(java.time.LocalDate.now().plusMonths(1));

		subscription.setStatus("PENDING");

		SubscriptionEntity savedSubscription = subscriptionService.createSubscription(subscription,
				organization.getId());

		InvoiceEntity savedInvoice = invoiceService.generateInvoice(savedSubscription);

		Payment payment = new Payment();

		payment.setInvoice(savedInvoice);
		payment.setAmount(savedInvoice.getTotalAmount());
		payment.setCurrency(savedInvoice.getCurrency());
		payment.setPaymentMethod(PaymentMethod.UPI);
		payment.setStatus(com.app.payment.enums.PaymentStatus.PaymentStatus.PENDING);

		Payment savedPayment = paymentService.createPayment(payment, organization.getId());

		String order = razorpayService.createOrder(savedPayment.getId());

		return java.util.Map.of("customerId", savedCustomer.getId(), "subscriptionId", savedSubscription.getId(),
			"invoiceId", savedInvoice.getId(), "paymentId", savedPayment.getId(), "order", order,
			"razorpayKey", razorpayKeyId);
	}

	@Override
	public com.app.organization.entity.Organization getOrganizationByToken(String token) {
		return organizationRepository.findByPublicLinkToken(token)
				.orElseThrow(() -> new RuntimeException("Billing link not found"));
	}

	@Override
	public Object verifyPayment(Long paymentId, com.app.payment.request.RazorpayVerificationRequest request,
			Long organizationId) {

		return paymentService.verifyPaymentPublic(paymentId, request, organizationId);
	}
}