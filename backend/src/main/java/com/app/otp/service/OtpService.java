package com.app.otp.service;

import com.app.otp.dto.GenerateOtpRequest;
import com.app.otp.dto.OtpResponse;
import com.app.otp.dto.VerifyOtpRequest;

public interface OtpService {

	OtpResponse generateOtp(GenerateOtpRequest request);
	
	OtpResponse verifyOtp(VerifyOtpRequest request);
	
	OtpResponse resendOtp(GenerateOtpRequest request);
	
}
