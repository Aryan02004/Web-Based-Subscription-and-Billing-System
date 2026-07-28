package com.app.organization.dto;

import com.app.common.enums.OrganizationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyOrganizationResponse {

	private Long organizationId;

	private String organizationName;

	private OrganizationStatus status;

	private String industry;

	private String contactEmail;

	private String role;

}