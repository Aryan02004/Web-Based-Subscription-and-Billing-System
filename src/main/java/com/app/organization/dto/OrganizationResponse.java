package com.app.organization.dto;

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

	private String status;

	private Long createdBy;
}
