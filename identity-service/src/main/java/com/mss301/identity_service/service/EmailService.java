package com.mss301.identity_service.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmail(String to, String subject, String message) throws MessagingException;

    void sendOtpEmail(String to, String otp) throws MessagingException;


    void sendPasswordResetEmail(String to, String otp) throws MessagingException;
}
