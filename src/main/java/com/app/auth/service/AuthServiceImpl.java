package com.app.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth.dto.LoginRequest;
import com.app.auth.dto.LoginResponse;
import com.app.auth.dto.MessageResponse;
import com.app.auth.dto.RefreshTokenRequest;
import com.app.auth.dto.RegisterRequest;
import com.app.auth.dto.RegisterResponse;
import com.app.auth.dto.VerifyEmailRequest;
import com.app.auth.dto.VerifyEmailResponse;
import com.app.auth.entity.RefreshToken;
import com.app.auth.entity.Role;
import com.app.auth.entity.User;
import com.app.auth.mapper.AuthUserMapper;
import com.app.auth.repository.RefreshTokenRepository;
import com.app.auth.repository.RoleRepository;
import com.app.auth.repository.UserRepository;
import com.app.auth.validator.PasswordValidator;
import com.app.common.enums.RoleType;
import com.app.exception.InvalidTokenException;
import com.app.exception.ResourceNotFoundException;
import com.app.security.service.CustomUserDetailsService;
import com.app.security.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final AuthUserMapper userMapper;
	private final PasswordValidator passwordValidator;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final CustomUserDetailsService userDetailsService;

	@Override
	public RegisterResponse register(RegisterRequest request) {

		// Check if email already exists
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email is already registered.");
		}

		// Validate password
		passwordValidator.validate(request.getPassword());

		// Convert DTO to Entity
		User user = userMapper.toEntity(request);

		// Encrypt password
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

		// Assign default role
		Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
				.orElseThrow(() -> new IllegalStateException("Default CUSTOMER role not found"));

		user.setRole(customerRole);

		// Save user
		User savedUser = userRepository.save(user);

		// Return response
		return userMapper.toRegisterResponse(savedUser);
	}

	@Override
	public LoginResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		// Check if email is verified
		if (!user.isEmailVerified()) {
			throw new RuntimeException("Please verify your email before logging in.");
		}

		// Check password
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new RuntimeException("Invalid email or password");
		}

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

		String accessToken = jwtService.generateToken(userDetails);

		String refreshToken = UUID.randomUUID().toString();

		RefreshToken refresh = new RefreshToken();
		refresh.setToken(refreshToken);
		refresh.setUser(user);
		refresh.setExpiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration() / 1000));
		refresh.setRevoked(false);

		refreshTokenRepository.save(refresh);

		LoginResponse response = new LoginResponse();
		response.setAccessToken(accessToken);
		response.setRefreshToken(refreshToken);
		response.setExpiresIn(jwtService.getJwtExpiration());
		response.setUserId(user.getId());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setEmail(user.getEmail());
		response.setRole(user.getRole().getName());

		return response;
	}

	@Override
	@Transactional
	public MessageResponse logout(String refreshToken) {

		RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

		if (!token.isRevoked()) {
			token.setRevoked(true);
			refreshTokenRepository.save(token);
		}

		return new MessageResponse("Logged out successfully");
	}

	@Override
	public LoginResponse refreshToken(RefreshTokenRequest request) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
				.orElseThrow(() -> new RuntimeException("Invalid refresh token"));

		if (refreshToken.isRevoked()) {
			throw new InvalidTokenException("Refresh token revoked");
		}

		if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException("Refresh token expired");
		}

		User user = refreshToken.getUser();

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

		String accessToken = jwtService.generateToken(userDetails);

		LoginResponse response = new LoginResponse();

		response.setAccessToken(accessToken);
		response.setRefreshToken(refreshToken.getToken());
		response.setExpiresIn(jwtService.getJwtExpiration());

		response.setUserId(user.getId());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setEmail(user.getEmail());
		response.setRole(user.getRole().getName());

		return response;
	}

	@Override
	public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {
		throw new UnsupportedOperationException("Email verification is not implemented yet.");
	}
}