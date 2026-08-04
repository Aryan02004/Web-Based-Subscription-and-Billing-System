package com.app.notification.service;

import java.util.List;

import com.app.notification.entity.NotificationEntity;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;

public interface NotificationService {

	void createCustomerNotification(Long customerId, String title, String message, NotificationType type,
			NotificationChannel channel);

	void createUserNotification(String title, String message, NotificationType type, NotificationChannel channel);

	List<NotificationEntity> getNotificationsForCurrentUser();

	List<NotificationEntity> getNotificationsForCustomer(Long customerId);

	void markAsRead(Long notificationId);

}