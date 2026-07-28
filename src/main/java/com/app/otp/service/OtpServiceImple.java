package com.app.otp.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.otp.dto.GenerateOtpRequest;
import com.app.otp.dto.OtpResponse;
import com.app.otp.dto.VerifyOtpRequest;
import com.app.otp.entity.OtpVerification;
import com.app.otp.repository.OtpVerificationRepository;

@Service
public class OtpServiceImple implements OtpService {

    // Repository for OTP table
    private final OtpVerificationRepository otpRepository;

    // Repository for User table
    private final UserRepository userRepository;

    // Service to send email
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    
    // Constructor Injection
    public OtpServiceImple(OtpVerificationRepository otpRepository, UserRepository userRepository,
			EmailService emailService, PasswordEncoder passwordEncoder) {
		super();
		this.otpRepository = otpRepository;
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
	}


    @Override
    public OtpResponse generateOtp(GenerateOtpRequest request) {

        // Find user by ID
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // Create OTP entity
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUser(user);
        otpVerification.setOtpHash(passwordEncoder.encode(otp));
        otpVerification.setPurpose(request.getPurpose());
        otpVerification.setAttemptCount(0);
        otpVerification.setVerified(false);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        // Save OTP in database
        otpRepository.save(otpVerification);

        // Send OTP to user's email
        emailService.sendOtpEmail(user.getEmail(), otp);

        // Return success response (don't expose OTP)
        OtpResponse response = new OtpResponse();
        response.setSuccess(true);
        response.setMessage("OTP sent successfully to your registered email.");

        return response;
    }


	@Override
    public OtpResponse verifyOtp(VerifyOtpRequest request) {

        // Get latest OTP for user and purpose
        OtpVerification otpVerification = otpRepository
                .findTopByUser_IdAndPurposeOrderByCreatedAtDesc(
                        request.getUserId(),
                        request.getPurpose())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        // Check if OTP already verified
        if (otpVerification.getVerified()) {
            throw new RuntimeException("OTP already verified");
        }

        // Check OTP expiry
        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // Validate OTP
        if (!passwordEncoder.matches(
                request.getOtp(),
                otpVerification.getOtpHash())) {

            otpVerification.setAttemptCount(
                    otpVerification.getAttemptCount() + 1);

            otpRepository.save(otpVerification);

            throw new RuntimeException("Invalid OTP");
        }

     // Mark OTP as verified
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        // Mark user's email as verified
        User user = otpVerification.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        /*
         * OPTIONAL
         * If User entity has:
         * private Boolean emailVerified;
         *
         * Uncomment below:
         *
         * User user = otpVerification.getUser();
         * user.setEmailVerified(true);
         * userRepository.save(user);
         */

        OtpResponse response = new OtpResponse();
        response.setSuccess(true);
        response.setMessage("OTP verified successfully.");

        return response;
    }

    @Override
    public OtpResponse resendOtp(GenerateOtpRequest request) {

        // Generate a new OTP and send it again
        return generateOtp(request);
    }
}