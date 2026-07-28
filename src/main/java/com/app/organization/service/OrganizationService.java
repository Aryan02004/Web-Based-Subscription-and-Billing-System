package com.app.organization.service;

import java.util.List;

import com.app.organization.dto.AddMemberRequest;
import com.app.organization.dto.MyOrganizationResponse;
import com.app.organization.dto.OrganizationRequest;
import com.app.organization.dto.OrganizationResponse;
import com.app.organization.dto.RejectOrganizationRequest;
import com.app.organization.dto.SuspendOrganizationRequest;

public interface OrganizationService {

	void addMember(Long organizationId, AddMemberRequest request);

	OrganizationResponse createOrganization(OrganizationRequest request);

	OrganizationResponse getOrganizationById(Long id);

	List<OrganizationResponse> getAllOrganizations();

	OrganizationResponse updateOrganization(Long id, OrganizationRequest request);

	void deleteOrganization(Long id);

	List<OrganizationResponse> getPendingOrganizations();

	OrganizationResponse approveOrganization(Long organizationId);

	OrganizationResponse rejectOrganization(Long organizationId, RejectOrganizationRequest reason);

	OrganizationResponse suspendOrganization(Long organizationId, SuspendOrganizationRequest reason);

	void validateOrganizationAccess(Long organizationId, Long userId);

	List<MyOrganizationResponse> getMyOrganizations();
	String generatePublicLink();
}