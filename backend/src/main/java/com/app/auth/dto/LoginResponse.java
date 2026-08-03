package com.app.auth.dto;

import com.app.common.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

	private String accessToken;

	private String tokenType = "Bearer";

	private Long userId;

	private String firstName;

	private String lastName;

	private String email;

	private RoleType role;

	private String refreshToken;

	private Long expiresIn;

	// Getters and Setters
}