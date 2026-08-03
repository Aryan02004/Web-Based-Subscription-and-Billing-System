package com.app.otp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.otp.dto.GenerateOtpRequest;
import com.app.otp.dto.OtpResponse;
import com.app.otp.dto.VerifyOtpRequest;
import com.app.otp.service.OtpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

	private final OtpService otpService;

	public OtpController(OtpService otpService) {
		super();
		this.otpService = otpService;
	}
	
	@PostMapping("/generate")
	@ResponseStatus(HttpStatus.CREATED)
	public OtpResponse generateOtp(@Valid @RequestBody GenerateOtpRequest request) {
		return otpService.generateOtp(request);
	}
	
	@PostMapping("/verify")
	public OtpResponse verifyOtp(
	        @Valid @RequestBody VerifyOtpRequest request) {

	    return otpService.verifyOtp(request);
	}
	
	@PostMapping("/resend")
	public OtpResponse resendOtp(
	        @Valid @RequestBody GenerateOtpRequest request) {

	    return otpService.resendOtp(request);
	}
}
