package com.app.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth.dto.CreateRoleRequest;
import com.app.auth.dto.RoleResponse;
import com.app.auth.entity.Role;
import com.app.auth.repository.RoleRepository;
import com.app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	@Override
	public RoleResponse createRole(CreateRoleRequest request) {

		if (roleRepository.existsByName(request.getName())) {
			throw new IllegalArgumentException("Role already exists");
		}

		Role role = new Role();

		role.setName(request.getName());
		role.setDescription(request.getDescription());

		Role savedRole = roleRepository.save(role);

		return mapToResponse(savedRole);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleResponse> getAllRoles() {

		return roleRepository.findAll().stream().filter(role -> !role.getDeleted()).map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RoleResponse getRoleById(Long id) {

		Role role = roleRepository.findById(id).filter(r -> !r.getDeleted())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		return mapToResponse(role);
	}

	@Override
	public RoleResponse updateRole(Long id, CreateRoleRequest request) {

		Role role = roleRepository.findById(id).filter(r -> !r.getDeleted())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		role.setName(request.getName());
		role.setDescription(request.getDescription());

		Role updatedRole = roleRepository.save(role);

		return mapToResponse(updatedRole);
	}

	@Override
	public void deleteRole(Long id) {

		Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		// Soft delete
		role.setDeleted(true);

		roleRepository.save(role);
	}

	private RoleResponse mapToResponse(Role role) {

		RoleResponse response = new RoleResponse();

		response.setId(role.getId());
		response.setName(role.getName());
		response.setDescription(role.getDescription());

		return response;
	}

}