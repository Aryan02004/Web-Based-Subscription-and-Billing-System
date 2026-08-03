package com.app.auth.mapper;

import org.springframework.stereotype.Component;

import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.RegisterResponse;
import com.app.auth.entity.User;
import com.app.common.enums.UserStatus;

@Component
public class AuthUserMapper {

	/**
	 * Convert RegisterRequest to User entity. Password encoding should be handled
	 * in the service.
	 */
	public User toEntity(RegisterRequest request) {

		if (request == null) {
			return null;
		}

		User user = new User();

		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());

		// Raw password for now.
		// AuthService will replace this with the encoded password.
		user.setPasswordHash(request.getPassword());

		user.setStatus(UserStatus.ACTIVE);
		user.setEmailVerified(false);

		return user;
	}

	/**
	 * Convert User entity to RegisterResponse.
	 */
	public RegisterResponse toRegisterResponse(User user) {

		if (user == null) {
			return null;
		}

		return RegisterResponse.builder().userId(user.getId()).email(user.getEmail())
				.message("Registration successful. Please verify your email.").emailVerificationRequired(true).build();
	}
}