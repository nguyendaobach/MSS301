package com.mss301.identity_service.service;

public interface OtpService {

    String generateOtp(String email);

    boolean validateOtp(String email, String otpCode);
}
