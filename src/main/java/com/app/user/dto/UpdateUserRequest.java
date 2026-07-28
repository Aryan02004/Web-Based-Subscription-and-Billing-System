package com.app.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 100)
	private String firstName;

	@Size(max = 100)
	private String lastName;

}