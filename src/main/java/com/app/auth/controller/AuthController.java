package com.app.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.auth.dto.LoginRequest;
import com.app.auth.dto.LoginResponse;
import com.app.auth.dto.MessageResponse;
import com.app.auth.dto.RefreshTokenRequest;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.RegisterResponse;
import com.app.auth.dto.VerifyEmailRequest;
import com.app.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Validated @RequestBody RegisterRequest request) {
		System.out.println("HIT REGISTER CONTROLLER");

		RegisterResponse response = authService.register(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

		return ResponseEntity.ok(authService.refreshToken(request));
	}

	@PostMapping("/verify-email")
	public ResponseEntity<Void> verifyEmail(@RequestBody VerifyEmailRequest request) {

		// TODO: Integrate with OTP module (Intern B)

		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PostMapping("/logout")
	public ResponseEntity<MessageResponse> logout(@RequestHeader("Refresh-Token") String refreshToken) {

		return ResponseEntity.ok(authService.logout(refreshToken));
	}

}