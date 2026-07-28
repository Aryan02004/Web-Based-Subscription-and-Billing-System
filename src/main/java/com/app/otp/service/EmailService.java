package com.app.otp.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
}