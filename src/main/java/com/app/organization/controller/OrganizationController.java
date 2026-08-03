	package com.app.organization.controller;
	
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
	
		@GetMapping("/billing-link")
		public ResponseEntity<String> generatePublicLink() {
	
			return ResponseEntity.ok(organizationService.generatePublicLink());
		}
	}
