package com.app.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 100)
	private String firstName;

	@Size(max = 100)
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email address")
	@Size(max = 255)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 100)
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "Password must contain uppercase, lowercase, number and special character")
	private String password;

	// Getters and Setters
}