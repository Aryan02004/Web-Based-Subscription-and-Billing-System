package com.app.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuspendOrganizationRequest {

	@NotNull(message = "Organization Id is required")
	private Long organizationId;

	@NotBlank(message = "Suspension reason is required")
	@Size(max = 500, message = "Reason cannot exceed 500 characters")
	private String reason;
}