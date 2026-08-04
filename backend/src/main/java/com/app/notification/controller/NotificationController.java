package com.app.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.notification.entity.NotificationEntity;
import com.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<List<NotificationEntity>> getNotifications() {

		return ResponseEntity.ok(notificationService.getNotificationsForCurrentUser());
	}

	@PatchMapping("/{id}/read")
	public ResponseEntity<String> markAsRead(@PathVariable Long id) {

		notificationService.markAsRead(id);

		return ResponseEntity.ok("Notification marked as read.");
	}
//
//	@PostMapping("/test/{customerId}")
//	public ResponseEntity<String> createCustomerTestNotification(@PathVariable Long customerId) {
//
//		notificationService.createCustomerNotification(customerId, "Subscription Expiry",
//				"Your subscription will expire in 3 days.", NotificationType.SUBSCRIPTION, NotificationChannel.IN_APP);
//
//		return ResponseEntity.ok("Customer notification created successfully.");
//	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<NotificationEntity>> getCustomerNotifications(@PathVariable Long customerId) {

		return ResponseEntity.ok(notificationService.getNotificationsForCustomer(customerId));
	}
}