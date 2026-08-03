package com.app.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.user.dto.ChangePasswordRequest;
import com.app.user.dto.UpdateUserRequest;
import com.app.user.dto.UserResponse;
import com.app.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<UserResponse> getCurrentUser() {

		return ResponseEntity.ok(userService.getCurrentUser());
	}

	@PutMapping("/me")
	public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {

		return ResponseEntity.ok(userService.updateCurrentUser(request));
	}

	@PutMapping("/me/change-password")
	public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {

		userService.changePassword(request);

		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteCurrentUser() {

	    userService.deleteCurrentUser();

	    return ResponseEntity.noContent().build();
	}

}