package com.app.subscription.service;

import com.app.invoice.email.EmailService;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.customer.entity.CustomerEntity;
import com.app.customer.repository.CustomerRepository;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;
import com.app.notification.service.NotificationService;
import com.app.notification.template.RenewalConfirmationEmailTemplate;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationUserRepository;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private SubscriptionRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private NotificationService notificationService;

	private Long getCurrentOrganizationId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUserId(user.getId())
				.stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organizationUser.getOrganization().getId();
	}

	@Override
	public SubscriptionEntity createSubscription(SubscriptionEntity subscription) {
		// TODO Auto-generated method stub
		subscription.setOrganizationId(getCurrentOrganizationId());

		SubscriptionEntity saved = repository.save(subscription);

		notificationService.createUserNotification("New Subscription Purchased",
				"A customer has purchased a subscription.", NotificationType.ORGANIZATION, NotificationChannel.IN_APP);

		return saved;

	}

	@Override
	public SubscriptionEntity createSubscription(SubscriptionEntity subscription, Long organizationId) {

		subscription.setOrganizationId(organizationId);

		SubscriptionEntity saved = repository.save(subscription);
//
//		notificationService.createUserNotification("New Subscription Purchased",
//				"A customer has purchased a subscription.", NotificationType.ORGANIZATION, NotificationChannel.IN_APP);

		return saved;
	}

	@Override
	public List<SubscriptionEntity> getAllSubscriptions() {
        return repository.findByOrganizationId(getCurrentOrganizationId());
    }

    @Override
    public List<SubscriptionEntity> getSubscriptionsByOrganizationId(Long organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public SubscriptionEntity getSubscriptionById(Long id) {
        return repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    @Override
    public SubscriptionEntity updateSubscription(Long id, SubscriptionEntity subscription) {
		// TODO Auto-generated method stub
		SubscriptionEntity existingSubscription = repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
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
		SubscriptionEntity subscription = repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Subscription not found"));

		repository.delete(subscription);

	}

	@Override
	public SubscriptionEntity renewSubscription(Long id) {

		SubscriptionEntity subscription = repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Subscription not found"));

		long months = ChronoUnit.MONTHS.between(subscription.getStartDate(), subscription.getEndDate());

		if (months <= 0) {
			months = 1;
		}

		subscription.setStartDate(subscription.getEndDate());

		subscription.setEndDate(subscription.getEndDate().plusMonths(months));

		subscription.setNextBillingDate(subscription.getEndDate());

		subscription.setRenewalDate(subscription.getEndDate());

		subscription.setStatus("ACTIVE");

		SubscriptionEntity saved = repository.save(subscription);

		CustomerEntity customer = customerRepository.findById(saved.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		String html = RenewalConfirmationEmailTemplate.build(customer);

		emailService.sendHtmlEmail(customer.getEmail(), "Subscription Renewed", html);

		notificationService.createCustomerNotification(customer.getId(), "Subscription Renewed",
				"Your subscription has been renewed successfully.", NotificationType.SUBSCRIPTION,
				NotificationChannel.IN_APP);

		return saved;
	}
}
