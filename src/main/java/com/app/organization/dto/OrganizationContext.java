package com.app.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrganizationContext {

	private Long organizationId;

	private Long userId;
}