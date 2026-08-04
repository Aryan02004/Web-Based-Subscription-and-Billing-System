package com.app.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.notification.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

	List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

	List<NotificationEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}