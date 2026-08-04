package com.app.notification.entity;

import java.time.LocalDateTime;

import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationStatus;
import com.app.notification.enums.NotificationType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="customer_id")
    private Long customerId;

    private String title;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name="read_at")
    private LocalDateTime readAt;

    @Column(name="sent_at")
    private LocalDateTime sentAt;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}