package com.mss301.identity_service.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    /**
     * Send OTP verification email to user
     * @param to Recipient email address
     * @param otp OTP code to send
     * @throws MessagingException if email sending fails
     */
    void sendOtpEmail(String to, String otp) throws MessagingException;
}
