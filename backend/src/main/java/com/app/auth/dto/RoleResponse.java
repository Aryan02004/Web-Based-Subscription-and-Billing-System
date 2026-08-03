package com.app.auth.dto;

import com.app.common.enums.RoleType;

import lombok.Data;

@Data
public class RoleResponse {

    private Long id;

    private RoleType name;

    private String description;
}
