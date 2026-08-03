package com.app.organization.dto;

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

//	private Long createdBy;
}
