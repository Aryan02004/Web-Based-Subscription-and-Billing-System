package com.app.organization.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth.entity.RefreshToken;
import com.app.auth.entity.Role;
import com.app.auth.entity.User;
import com.app.auth.repository.RefreshTokenRepository;
import com.app.auth.repository.RoleRepository;
import com.app.auth.repository.UserRepository;
import com.app.common.enums.OrganizationStatus;
import com.app.common.enums.OrganizationUserStatus;
import com.app.common.enums.RoleType;
import com.app.exception.OrganizationAccessDeniedException;
import com.app.organization.dto.AddMemberRequest;
import com.app.organization.dto.MyOrganizationResponse;
import com.app.organization.dto.OrganizationRequest;
import com.app.organization.dto.OrganizationResponse;
import com.app.organization.dto.RejectOrganizationRequest;
import com.app.organization.dto.SuspendOrganizationRequest;
import com.app.organization.entity.Organization;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationRepository;
import com.app.organization.repository.OrganizationUserRepository;

@Service
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final OrganizationUserRepository organizationUserRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	public OrganizationServiceImpl(OrganizationRepository organizationRepository,
			OrganizationUserRepository organizationUserRepository, UserRepository userRepository,
			RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository) {
		super();
		this.organizationRepository = organizationRepository;
		this.organizationUserRepository = organizationUserRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Override
	@Transactional
	public OrganizationResponse createOrganization(OrganizationRequest request) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));

		Organization organization = new Organization();

		organization.setName(request.getName());
		organization.setIndustry(request.getIndustry());
		organization.setContactEmail(request.getContactEmail());
		organization.setStatus(OrganizationStatus.PENDING);
		organization.setCreatedBy(user);

		Organization savedOrganization = organizationRepository.save(organization);

		Role organizationAdminRole = roleRepository.findByName(RoleType.ORGANIZATION_ADMIN)
				.orElseThrow(() -> new RuntimeException("Organization Admin role not found"));

		OrganizationUser organizationUser = new OrganizationUser();
		organizationUser.setOrganization(savedOrganization);
		organizationUser.setUser(user);
		organizationUser.setRole(organizationAdminRole);
		organizationUser.setJoinedAt(LocalDateTime.now());
		organizationUser.setStatus(OrganizationUserStatus.ACTIVE);

		organizationUserRepository.save(organizationUser);

		return mapToResponse(savedOrganization);

	}

	@Override
	public OrganizationResponse getOrganizationById(Long id) {

		Organization organization = organizationRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return mapToResponse(organization);
	}

	@Override
	public List<OrganizationResponse> getAllOrganizations() {

		return organizationRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {

		Organization organization = organizationRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		organization.setName(request.getName());
		organization.setIndustry(request.getIndustry());
		organization.setContactEmail(request.getContactEmail());

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

		return response;
	}

	@Override
	public void addMember(Long organizationId, AddMemberRequest request) {

		Organization organization = organizationRepository.findByIdAndDeletedFalse(organizationId)
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
		organizationUser.setStatus(OrganizationUserStatus.ACTIVE);

		organizationUserRepository.save(organizationUser);
	}

	@Override
	public List<OrganizationResponse> getPendingOrganizations() {
		// TODO Auto-generated method stub
		return organizationRepository.findByStatus(OrganizationStatus.PENDING).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public OrganizationResponse approveOrganization(Long organizationId) {
		// TODO Auto-generated method stub
		Organization organization = organizationRepository.findByIdAndDeletedFalse(organizationId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		if (organization.getStatus() != OrganizationStatus.PENDING) {
			throw new RuntimeException("Only pending organizations can be approved.");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User superAdmin = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Super Admin not found"));

		organization.setStatus(OrganizationStatus.APPROVED);
		organization.setApprovedBy(superAdmin);
		organization.setApprovedAt(LocalDateTime.now());
		organization.setLastStatusChangedAt(LocalDateTime.now());

		organizationRepository.save(organization);

		User owner = organization.getCreatedBy();

		Role organizationAdminRole = roleRepository.findByName(RoleType.ORGANIZATION_ADMIN)
				.orElseThrow(() -> new RuntimeException("Role not found"));

		owner.setRole(organizationAdminRole);

		userRepository.save(owner);

		List<RefreshToken> tokens = refreshTokenRepository.findByUser(owner);

		for (RefreshToken token : tokens) {
			token.setRevoked(true);
		}

		refreshTokenRepository.saveAll(tokens);

		return mapToResponse(organization);
	}

	@Override
	@Transactional
	public OrganizationResponse rejectOrganization(Long organizationId, RejectOrganizationRequest reason) {
		// TODO Auto-generated method stub
		Organization organization = organizationRepository.findByIdAndDeletedFalse(organizationId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		if (organization.getStatus() != OrganizationStatus.PENDING) {
			throw new RuntimeException("Only pending organizations can be rejected.");
		}

		organization.setStatus(OrganizationStatus.REJECTED);
		organization.setRejectedAt(LocalDateTime.now());
		organization.setRejectionReason(reason.getReason());
		organization.setLastStatusChangedAt(LocalDateTime.now());

		organizationRepository.save(organization);

		return mapToResponse(organization);
	}

	@Override
	@Transactional
	public OrganizationResponse suspendOrganization(Long organizationId, SuspendOrganizationRequest reason) {
		// TODO Auto-generated method stub
		Organization organization = organizationRepository.findByIdAndDeletedFalse(organizationId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		if (organization.getStatus() != OrganizationStatus.APPROVED) {
			throw new RuntimeException("Only approved organizations can be suspended.");
		}

		organization.setStatus(OrganizationStatus.SUSPENDED);

		organization.setRejectionReason(reason.getReason());

		organization.setLastStatusChangedAt(LocalDateTime.now());

		organizationRepository.save(organization);

		return mapToResponse(organization);
	}

	@Override
	@Transactional
	public void validateOrganizationAccess(Long organizationId, Long userId) {
		// TODO Auto-generated method stub
		OrganizationUser organizationUser = organizationUserRepository
				.findByOrganizationIdAndUserId(organizationId, userId)
				.orElseThrow(() -> new OrganizationAccessDeniedException("You are not a member of this organization"));

		Organization organization = organizationUser.getOrganization();

		switch (organization.getStatus()) {

		case APPROVED:
			return;

		case PENDING:
			throw new OrganizationAccessDeniedException("Organization approval is pending.");

		case REJECTED:
			throw new OrganizationAccessDeniedException("Organization has been rejected.");

		case SUSPENDED:
			throw new OrganizationAccessDeniedException("Organization has been suspended.");

		default:
			throw new OrganizationAccessDeniedException("Organization cannot access this resource.");
		}

	}

	@Override
	public List<MyOrganizationResponse> getMyOrganizations() {
		// TODO Auto-generated method stub

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		List<OrganizationUser> organizationUsers = organizationUserRepository.findByUserId(user.getId());

		return organizationUsers.stream().map(orgUser -> {
			Organization organization = orgUser.getOrganization();

			MyOrganizationResponse dto = new MyOrganizationResponse();
			dto.setOrganizationId(organization.getId());
			dto.setOrganizationName(organization.getName());
			dto.setIndustry(organization.getIndustry());
			dto.setContactEmail(organization.getContactEmail());
			dto.setStatus(organization.getStatus());
			dto.setRole(orgUser.getRole().getName().name());
			dto.setCreatedAt(organization.getCreatedAt());
			dto.setRejectionReason(organization.getRejectionReason());
			return dto;
		}).toList();
	}

	@Override
	public String generatePublicLink() {
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findById(user.getId())
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		Organization organization = organizationUser.getOrganization();

		// If already generated, return existing token
		if (organization.getPublicLinkToken() != null && !organization.getPublicLinkToken().isBlank()) {

			return "http://localhost:8080/public/org/" + organization.getPublicLinkToken();
		}

		String token = UUID.randomUUID().toString();

		organization.setPublicLinkToken(token);
		organization.setLinkActive(true);

		organizationRepository.save(organization);

		return "http://localhost:8080/public/org/" + token;
	}
}