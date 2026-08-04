package com.app.organization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.organization.dto.MyOrganizationResponse;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.app.organization.dto.OrganizationRequest;
import com.app.organization.dto.OrganizationResponse;
import com.app.organization.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organizations")
@Validated
@PreAuthorize("hasAnyRole('CUSTOMER','ORGANIZATION_ADMIN')")
public class OrganizationController {

	private final OrganizationService organizationService;

	public OrganizationController(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrganizationResponse createOrganization(@Valid @RequestBody OrganizationRequest request) {
		return organizationService.createOrganization(request);

	}

	@GetMapping("/{id}")
	public OrganizationResponse getOrgnization(@PathVariable Long id) {

		return organizationService.getOrganizationById(id);
	}

	@GetMapping
	public List<OrganizationResponse> getAllOrganizationResponses() {
		return organizationService.getAllOrganizations();
	}

	@PutMapping("/{id}")
	public OrganizationResponse updateOrganization(@PathVariable Long id,
			@Valid @RequestBody OrganizationRequest request) {

		return organizationService.updateOrganization(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrganization(@PathVariable Long id) {
		organizationService.deleteOrganization(id);
	}

	@GetMapping("/my-organizations")
	public List<MyOrganizationResponse> getMyOrganizations() {

		return organizationService.getMyOrganizations();
	}
	@PostMapping("/generate-link")
	public ResponseEntity<java.util.Map<String, String>> generatePublicLink(@RequestBody(required = false) java.util.Map<String, Object> body) {
		String token;
		if (body != null && body.containsKey("organizationId")) {
			Object raw = body.get("organizationId");
			Long orgId = null;
			try {
				if (raw instanceof Number) orgId = ((Number) raw).longValue();
				else orgId = Long.parseLong(String.valueOf(raw));
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Invalid organizationId"));
			}

			token = organizationService.generatePublicLink(orgId);
		} else {
			token = organizationService.generatePublicLink();
		}

		return ResponseEntity.ok(java.util.Collections.singletonMap("token", token));
	}
}
