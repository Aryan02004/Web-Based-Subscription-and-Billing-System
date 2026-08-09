package com.app.ai.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.ai.dto.AiDashboardResponse;
import com.app.ai.dto.DashboardMetrics;
import com.app.ai.util.PromptBuilder;
import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.customer.repository.CustomerRepository;
import com.app.organization.entity.Organization;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationRepository;
import com.app.organization.repository.OrganizationUserRepository;
import com.app.payment.entity.Payment;
import com.app.payment.enums.PaymentStatus.PaymentStatus;
import com.app.payment.repository.PaymentRepository;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;
import com.app.subscriptionplan.repo.SubscriptionPlanRepo;

@Service
public class AiServiceImpl implements AiService {

	private final UserRepository userRepository;
	private final OrganizationRepository organizationRepository;
	private final OrganizationUserRepository organizationUserRepository;
	private final CustomerRepository customerRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final SubscriptionPlanRepo subscriptionPlanRepo;
	private final PaymentRepository paymentRepository;
	private final GeminiService geminiService;

	public AiServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository,
			OrganizationUserRepository organizationUserRepository, CustomerRepository customerRepository,
			SubscriptionRepository subscriptionRepository, SubscriptionPlanRepo subscriptionPlanRepo,
			PaymentRepository paymentRepository, GeminiService geminiService) {

		this.userRepository = userRepository;
		this.organizationRepository = organizationRepository;
		this.organizationUserRepository = organizationUserRepository;
		this.customerRepository = customerRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.subscriptionPlanRepo = subscriptionPlanRepo;
		this.paymentRepository = paymentRepository;
		this.geminiService = geminiService;
	}

	private Long getCurrentOrganizationId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUserId(user.getId())
				.stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		Organization organization = organizationRepository.findByIdAndDeletedFalse(organizationUser.getOrganization().getId())
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organization.getId();
	}

	@Override
	public AiDashboardResponse generateDashboardSummary() {

		Long orgId = getCurrentOrganizationId();

		Organization organization = organizationRepository.findById(orgId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		int totalCustomers = customerRepository.findByOrganizationId(orgId).size();

		List<SubscriptionEntity> subscriptions = subscriptionRepository.findByOrganizationId(orgId);

		int totalSubscriptions = subscriptions.size();

		int totalPlans = subscriptionPlanRepo.findByOrganization(organization).size();

		List<Payment> payments = paymentRepository.findByInvoiceSubscriptionOrganizationId(orgId);

		double totalRevenue = payments.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
				.map(Payment::getAmount).map(BigDecimal::doubleValue).reduce(0.0, Double::sum);

		int renewalsDue = (int) subscriptions.stream().filter(s -> s.getNextBillingDate() != null)
				.filter(s -> !s.getNextBillingDate().isAfter(LocalDate.now().plusDays(7))).count();

		Map<String, Integer> planCount = new HashMap<>();

		for (SubscriptionEntity subscription : subscriptions) {

			String planName = subscription.getPlan().getPlanName();

			planCount.put(planName, planCount.getOrDefault(planName, 0) + 1);
		}

		String topPlan = planCount.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
				.map(Map.Entry::getKey).orElse("N/A");

		DashboardMetrics metrics = new DashboardMetrics(organization.getName(), totalCustomers, totalSubscriptions,
				totalPlans, totalRevenue, renewalsDue, topPlan);

		String prompt = PromptBuilder.buildPrompt(metrics);

		String aiReport = geminiService.generateContent(prompt);

		AiDashboardResponse response = new AiDashboardResponse();

		response.setOrganization(organization.getName());

		response.setAiReport(aiReport);

		response.setGeneratedAt(LocalDateTime.now());

		return response;
	}
}