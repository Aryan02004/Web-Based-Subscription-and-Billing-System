package com.app.otp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyOtpRequest {

	@NotNull
    private Long userId;

    @NotBlank
    private String otp;

    @NotBlank
    private String purpose;
}
