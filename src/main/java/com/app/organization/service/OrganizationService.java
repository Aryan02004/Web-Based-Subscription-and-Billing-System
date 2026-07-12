package com.app.organization.service;

import java.util.List;

import com.app.organization.dto.AddMemberRequest;
import com.app.organization.dto.OrganizationRequest;
import com.app.organization.dto.OrganizationResponse;

public interface OrganizationService {
	
	void addMember(Long organizationId, AddMemberRequest request);

    OrganizationResponse createOrganization(OrganizationRequest request);

    OrganizationResponse getOrganizationById(Long id);

    List<OrganizationResponse> getAllOrganizations();

    OrganizationResponse updateOrganization(Long id, OrganizationRequest request);

    void deleteOrganization(Long id);
}