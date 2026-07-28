package com.app.user.mapper;

import org.springframework.stereotype.Component;

import com.app.auth.entity.User;
import com.app.user.dto.UpdateUserRequest;
import com.app.user.dto.UserResponse;

@Component
public class UserProfileMapper {

	public UserResponse toResponse(User user) {

		if (user == null) {
			return null;
		}

		UserResponse response = new UserResponse();

		response.setId(user.getId());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setEmail(user.getEmail());
		response.setStatus(user.getStatus());
		response.setEmailVerified(user.isEmailVerified());

		return response;
	}

	public void updateEntity(UpdateUserRequest request, User user) {

		if (request == null || user == null) {
			return;
		}

		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
	}

}