package com.app.organization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.organization.dto.OrganizationResponse;
import com.app.organization.dto.RejectOrganizationRequest;
import com.app.organization.dto.SuspendOrganizationRequest;
import com.app.organization.service.OrganizationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/super-admin/organizations")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminOrganizationController {

	private final OrganizationService organizationService;

	/**
	 * Get all pending organizations
	 */
	@GetMapping("/pending")
	@ResponseStatus(HttpStatus.OK)
	public List<OrganizationResponse> getPendingOrganizations() {
		return organizationService.getPendingOrganizations();
	}

	/**
	 * Approve organization
	 */
	@PutMapping("/{organizationId}/approve")
	@ResponseStatus(HttpStatus.OK)
	public OrganizationResponse approveOrganization(@PathVariable Long organizationId) {

		return organizationService.approveOrganization(organizationId);
	}

	/**
	 * Reject organization
	 */
	@PutMapping("/{organizationId}/reject")
	@ResponseStatus(HttpStatus.OK)
	public OrganizationResponse rejectOrganization(@PathVariable Long organizationId,
			@Valid @RequestBody RejectOrganizationRequest request) {

		return organizationService.rejectOrganization(organizationId, request);
	}

	/**
	 * Suspend organization
	 */
	@PutMapping("/{organizationId}/suspend")
	@ResponseStatus(HttpStatus.OK)
	public OrganizationResponse suspendOrganization(@PathVariable Long organizationId,
			@Valid @RequestBody SuspendOrganizationRequest request) {

		return organizationService.suspendOrganization(organizationId, request);
	}

}