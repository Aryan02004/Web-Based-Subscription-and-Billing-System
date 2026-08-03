package com.app.auth.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

	// At least one uppercase, one lowercase, one digit,
	// one special character, minimum 8 characters
	private static final Pattern PASSWORD_PATTERN = Pattern
			.compile("^(?=.*[a-z])" + "(?=.*[A-Z])" + "(?=.*\\d)" + "(?=.*[@#$%^&+=!])" + ".{8,}$");

//Returns true if password matches the security policy.

	public boolean isValid(String password) {
		if (password == null || password.isBlank()) {
			return false;
		}

		return PASSWORD_PATTERN.matcher(password).matches();
	}

//Validates password and throws an exception if invalid.

	public void validate(String password) {
		if (!isValid(password)) {
			throw new IllegalArgumentException("Password must be at least 8 characters long and contain "
					+ "one uppercase letter, one lowercase letter, one number, " + "and one special character.");
		}
	}
}