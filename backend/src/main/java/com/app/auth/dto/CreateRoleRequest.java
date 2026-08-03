package com.app.auth.dto;

import com.app.common.enums.RoleType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRoleRequest {

    @NotNull
    private RoleType name;

    private String description;
}
