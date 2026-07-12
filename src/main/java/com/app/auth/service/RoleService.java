package com.app.auth.service;

import java.util.List;

import com.app.auth.dto.CreateRoleRequest;
import com.app.auth.dto.RoleResponse;

public interface RoleService {

    RoleResponse createRole(CreateRoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse updateRole(Long id, CreateRoleRequest request);

    void deleteRole(Long id);
}
