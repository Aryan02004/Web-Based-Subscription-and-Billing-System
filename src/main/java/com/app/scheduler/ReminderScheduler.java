package com.app.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;
import com.app.notification.service.NotificationService;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

	private final SubscriptionRepository subscriptionRepository;
	private final NotificationService notificationService;

	@Scheduled(cron = "0 0 9 * * *") // every minute (testing)
	public void sendRenewalReminders() {
		System.out.println("Reminder Scheduler Running...");
		LocalDate reminderDate = LocalDate.now().plusDays(3);

		List<SubscriptionEntity> subscriptions = subscriptionRepository.findByNextBillingDate(reminderDate);

		for (SubscriptionEntity subscription : subscriptions) {

			notificationService.createCustomerNotification(subscription.getCustomerId(),
					"Subscription Renewal Reminder",
					"Your subscription will renew on " + subscription.getNextBillingDate(),
					NotificationType.SUBSCRIPTION, NotificationChannel.EMAIL);
		}
	}
}