package com.app.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.auth.validator.PasswordValidator;
import com.app.common.enums.UserStatus;
import com.app.security.service.CustomUserDetails;
import com.app.user.dto.ChangePasswordRequest;
import com.app.user.dto.UpdateUserRequest;
import com.app.user.dto.UserResponse;
import com.app.user.mapper.UserProfileMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserProfileMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final PasswordValidator passwordValidator;

	@Override
	public UserResponse getCurrentUser() {

		User user = getAuthenticatedUser();

		return userMapper.toResponse(user);
	}

	@Override
	public UserResponse updateCurrentUser(UpdateUserRequest request) {
		User user = getAuthenticatedUser();

		userMapper.updateEntity(request, user);

		User updatedUser = userRepository.save(user);

		return userMapper.toResponse(updatedUser);
	}

	@Override
	public void changePassword(ChangePasswordRequest request) {
		User user = getAuthenticatedUser();

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {

			throw new RuntimeException("Current password is incorrect.");
		}

		passwordValidator.validate(request.getNewPassword());

		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

		userRepository.save(user);
	}

	@Override
	public void deleteCurrentUser() {
		User user = getAuthenticatedUser();

		user.setDeleted(true);
		user.setStatus(UserStatus.DEACTIVATED);

		userRepository.save(user);
	}

	/**
	 * Returns the currently authenticated platform user.
	 */
	private User getAuthenticatedUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("User is not authenticated.");
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		return userDetails.getUser();
	}
}