package com.app.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.auth.dto.CreateRoleRequest;
import com.app.auth.dto.RoleResponse;
import com.app.auth.service.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	@PostMapping
	public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {

		return new ResponseEntity<>(roleService.createRole(request), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<RoleResponse>> getAllRoles() {

		return ResponseEntity.ok(roleService.getAllRoles());
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {

		return ResponseEntity.ok(roleService.getRoleById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id,
			@Valid @RequestBody CreateRoleRequest request) {

		return ResponseEntity.ok(roleService.updateRole(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteRole(@PathVariable Long id) {

		roleService.deleteRole(id);

		return ResponseEntity.ok("Role deleted successfully");
	}

}