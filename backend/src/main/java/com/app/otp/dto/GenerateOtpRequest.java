package com.app.otp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenerateOtpRequest {

	 @NotNull
	 private Long userId;

	 @NotNull
	 private String purpose;
}
