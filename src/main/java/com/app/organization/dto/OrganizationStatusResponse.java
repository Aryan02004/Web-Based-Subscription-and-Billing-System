package com.app.organization.dto;

import java.time.LocalDateTime;

import com.app.common.enums.OrganizationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationStatusResponse {

	private Long id;

	private String organizationName;

	private OrganizationStatus status;

	private String rejectionReason;

	private LocalDateTime approvedAt;

	private LocalDateTime rejectedAt;

	private LocalDateTime lastStatusChangedAt;
}