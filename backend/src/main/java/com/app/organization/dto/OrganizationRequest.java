package com.app.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationRequest {

	@NotBlank(message = "Organization name is required")
	@Size(max = 255)
	private String name;

	@Size(max = 100)
	private String industry;

	@Email(message = "Invalid email")
	private String contactEmail;

//	@NotNull(message = "Created By is required")
//	private Long createdBy;
}