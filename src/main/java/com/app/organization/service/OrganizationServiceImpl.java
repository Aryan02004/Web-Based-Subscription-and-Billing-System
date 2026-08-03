package com.app.organization.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.Role;
import com.app.auth.entity.User;
import com.app.auth.repository.RoleRepository;
import com.app.auth.repository.UserRepository;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;
import com.app.notification.service.NotificationService;
import com.app.organization.dto.AddMemberRequest;
import com.app.organization.dto.OrganizationRequest;
import com.app.organization.dto.OrganizationResponse;
import com.app.organization.entity.Organization;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationRepository;
import com.app.organization.repository.OrganizationUserRepository;

@Service
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;
	@Autowired
	private OrganizationUserRepository organizationUserRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private NotificationService notificationService;

	@Override
	public OrganizationResponse createOrganization(OrganizationRequest request) {

		User user = userRepository.findById(request.getCreatedBy())
				.orElseThrow(() -> new RuntimeException("User not found"));

		Organization organization = new Organization();

		organization.setName(request.getName());
		organization.setIndustry(request.getIndustry());
		organization.setContactEmail(request.getContactEmail());
		organization.setStatus("ACTIVE");
		organization.setCreatedBy(user);

		Organization savedOrganization = organizationRepository.save(organization);
		notificationService.createUserNotification("New Organization Registered",
				savedOrganization.getName() + " has registered successfully.", NotificationType.ADMIN,
				NotificationChannel.IN_APP);

		return mapToResponse(savedOrganization);
	}

	@Override
	public OrganizationResponse getOrganizationById(Long id) {

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return mapToResponse(organization);
	}

	@Override
	public List<OrganizationResponse> getAllOrganizations() {

		return organizationRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		User user = userRepository.findById(request.getCreatedBy())
				.orElseThrow(() -> new RuntimeException("User not found"));

		organization.setName(request.getName());
		organization.setIndustry(request.getIndustry());
		organization.setContactEmail(request.getContactEmail());
		organization.setCreatedBy(user);

		Organization updatedOrganization = organizationRepository.save(organization);

		return mapToResponse(updatedOrganization);
	}

	@Override
	public void deleteOrganization(Long id) {

		organizationRepository.deleteById(id);

	}

	private OrganizationResponse mapToResponse(Organization organization) {

		OrganizationResponse response = new OrganizationResponse();

		response.setId(organization.getId());
		response.setName(organization.getName());
		response.setIndustry(organization.getIndustry());
		response.setContactEmail(organization.getContactEmail());
		response.setStatus(organization.getStatus());

		if (organization.getCreatedBy() != null) {
			response.setCreatedBy(organization.getCreatedBy().getId());
		}

		return response;
	}

	@Override
	public void addMember(Long organizationId, AddMemberRequest request) {

		Organization organization = organizationRepository.findById(organizationId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		Role role = roleRepository.findById(request.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		OrganizationUser organizationUser = new OrganizationUser();

		organizationUser.setOrganization(organization);
		organizationUser.setUser(user);
		organizationUser.setRole(role);
		organizationUser.setJoinedAt(LocalDateTime.now());
		organizationUser.setStatus("ACTIVE");

		organizationUserRepository.save(organizationUser);
	}

	@Override
	public String generatePublicLink() {
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		Organization organization = organizationUser.getOrganization();

		// If already generated, return existing token
		if (organization.getPublicLinkToken() != null && !organization.getPublicLinkToken().isBlank()) {

			return "http://localhost:8080/billing/" + organization.getPublicLinkToken();
		}

		String token = UUID.randomUUID().toString();

		organization.setPublicLinkToken(token);
		organization.setLinkActive(true);

		organizationRepository.save(organization);

		return "http://localhost:8080/billing/" + token;
	}
}