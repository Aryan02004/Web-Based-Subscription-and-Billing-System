package com.app.organization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApproveOrganizationRequest {

	@NotNull(message = "Organization Id is required")
	private Long organizationId;
}