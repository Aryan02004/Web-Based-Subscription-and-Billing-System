package com.app.otp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {

        // Create email
        SimpleMailMessage message = new SimpleMailMessage();

        // Recipient email
        message.setTo(to);

        // Email subject
        message.setSubject("Email Verification OTP");

        // Email body
        message.setText(
                "Dear User,\n\n" +
                "Your OTP for email verification is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "Regards,\n" +
                "Subscription & Billing System");

        // Send email
        mailSender.send(message);
    }
}