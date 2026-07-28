package com.app.auth.service;

import com.app.auth.dto.LoginRequest;
import com.app.auth.dto.LoginResponse;
import com.app.auth.dto.MessageResponse;
import com.app.auth.dto.RefreshTokenRequest;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.RegisterResponse;
import com.app.auth.dto.VerifyEmailRequest;
import com.app.auth.dto.VerifyEmailResponse;

public interface AuthService {

	/**
	 * Register a new user.
	 *
	 * @param request registration request
	 * @return registration response
	 */
	RegisterResponse register(RegisterRequest request);

	/**
	 * Authenticate a user and generate JWT.
	 *
	 * @param request login request
	 * @return login response containing JWT
	 */
	LoginResponse login(LoginRequest request);

	/**
	 * Logout the authenticated user. (For JWT, this may later invalidate refresh
	 * tokens or blacklist access tokens.)
	 *
	 * @param token JWT access token
	 */
	MessageResponse logout(String refreshToken);

	/**
	 * Generate a new access token using a refresh token.
	 *
	 * @param refreshToken refresh token
	 * @return new login response containing a fresh access token
	 */
	LoginResponse refreshToken(RefreshTokenRequest request);

	/**
	 * Verify the user's email using an OTP or verification token.
	 *
	 * @param email user's email
	 * @param otp   verification code
	 */
	VerifyEmailResponse verifyEmail(VerifyEmailRequest request);
}