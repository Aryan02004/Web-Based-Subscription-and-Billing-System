package com.app.organization.dto;

import java.time.LocalDateTime;

import com.app.common.enums.OrganizationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationResponse {

	private Long id;

	private String name;

	private String industry;

	private String contactEmail;

	private OrganizationStatus status;

	private LocalDateTime createdAt;

	private String rejectionReason;

	private String publicLinkToken;

	private Boolean linkActive;

	public String getOrganizationName() {
		return name;
	}

	public Long getOrganizationId() {
		return id;
	}

//	private Long createdBy;
}
