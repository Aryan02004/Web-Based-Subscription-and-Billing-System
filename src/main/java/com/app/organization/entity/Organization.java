package com.app.organization.entity;

import java.time.LocalDateTime;

import com.app.auth.entity.User;
import com.app.common.entity.BaseEntity;
import com.app.common.enums.OrganizationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizations")
@Setter
@Getter
@NoArgsConstructor
public class Organization extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@Column
	private String industry;

	@Column(name = "contact_email")
	private String contactEmail;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrganizationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approved_by")
	private User approvedBy;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "rejected_at")
	private LocalDateTime rejectedAt;

	@Column(name = "rejection_reason")
	private String rejectionReason;

	@Column(name = "last_status_changed_at")
	private LocalDateTime lastStatusChangedAt;
}
