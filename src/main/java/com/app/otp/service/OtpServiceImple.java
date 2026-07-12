package com.app.otp.service;

import java.time.LocalDateTime;
import java.util.Random;

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

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;

    public OtpServiceImple(OtpVerificationRepository otpRepository,
                          UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OtpResponse generateOtp(GenerateOtpRequest request) {

        System.out.println("========== OTP DEBUG ==========");
        System.out.println("Request UserId = " + request.getUserId());
        System.out.println("Total Users = " + userRepository.count());

        userRepository.findAll().forEach(user ->
                System.out.println("ID = " + user.getId() +
                                   ", Email = " + user.getEmail()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Found User = " + user.getEmail());

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUser(user);
        otpVerification.setOtpHash(otp);
        otpVerification.setPurpose(request.getPurpose());
        otpVerification.setAttemptCount(0);
        otpVerification.setVerified(false);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpVerification);

        OtpResponse response = new OtpResponse();
        response.setSuccess(true);
        response.setMessage("OTP Generated Successfully : " + otp);

        return response;
    }

    @Override
    public OtpResponse verifyOtp(VerifyOtpRequest request) {

        OtpVerification otpVerification = otpRepository
                .findTopByUser_IdAndPurposeOrderByCreatedAtDesc(
                        request.getUserId(),
                        request.getPurpose())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otpVerification.getVerified()) {
            throw new RuntimeException("OTP already verified");
        }

        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otpVerification.getOtpHash().equals(request.getOtp())) {

            otpVerification.setAttemptCount(
                    otpVerification.getAttemptCount() + 1);

            otpRepository.save(otpVerification);

            throw new RuntimeException("Invalid OTP");
        }

        otpVerification.setVerified(true);

        otpRepository.save(otpVerification);

        OtpResponse response = new OtpResponse();

        response.setSuccess(true);
        response.setMessage("OTP Verified Successfully");

        return response;
    }
   
    @Override
    public OtpResponse resendOtp(GenerateOtpRequest request) {

        return generateOtp(request);

    }
}