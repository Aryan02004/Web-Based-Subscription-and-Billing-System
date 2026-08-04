package com.app.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.customer.entity.CustomerEntity;
import com.app.customer.repository.CustomerRepository;
import com.app.invoice.email.EmailService;
import com.app.notification.entity.NotificationEntity;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationStatus;
import com.app.notification.enums.NotificationType;
import com.app.notification.repository.NotificationRepository;
import com.app.notification.service.NotificationService;
import com.app.notification.template.ReminderEmailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final EmailService emailService;

	/**
	 * Returns the ID of the currently logged-in user.
	 */
	private Long getCurrentUserId() {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		return user.getId();
	}

	@Override
	public List<NotificationEntity> getNotificationsForCurrentUser() {

		return notificationRepository.findByUserIdOrderByCreatedAtDesc(getCurrentUserId());
	}

	@Override
	public void markAsRead(Long notificationId) {

		NotificationEntity notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found"));

		notification.setStatus(NotificationStatus.READ);
		notification.setReadAt(LocalDateTime.now());

		notificationRepository.save(notification);
	}

	@Override
	public void createCustomerNotification(Long customerId, String title, String message, NotificationType type,
			NotificationChannel channel) {

		NotificationEntity notification = new NotificationEntity();

		notification.setCustomerId(customerId);
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setType(type);
		notification.setChannel(channel);
		notification.setStatus(NotificationStatus.SENT);
		notification.setSentAt(LocalDateTime.now());

		notificationRepository.save(notification);

		CustomerEntity customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		String html = ReminderEmailTemplate.build(customer, message);

//		emailService.sendHtmlEmail(customer.getEmail(), title, html);
	}

	@Override
	public void createUserNotification(String title, String message, NotificationType type,
			NotificationChannel channel) {

		NotificationEntity notification = new NotificationEntity();

		// Recipient is the logged-in USER
		notification.setUserId(getCurrentUserId());

		notification.setTitle(title);
		notification.setMessage(message);
		notification.setType(type);
		notification.setChannel(channel);

		notification.setStatus(NotificationStatus.SENT);
		notification.setSentAt(LocalDateTime.now());

		notificationRepository.save(notification);
	}

	@Override
	public List<NotificationEntity> getNotificationsForCustomer(Long customerId) {

		return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
	}
}
