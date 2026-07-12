package com.app.user.service;

import com.app.user.dto.ChangePasswordRequest;
import com.app.user.dto.UpdateUserRequest;
import com.app.user.dto.UserResponse;

public interface UserService {

	UserResponse getCurrentUser();

	UserResponse updateCurrentUser(UpdateUserRequest request);

	void changePassword(ChangePasswordRequest request);

	void deleteCurrentUser();

}