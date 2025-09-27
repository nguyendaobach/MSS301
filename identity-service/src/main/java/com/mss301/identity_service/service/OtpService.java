package com.mss301.identity_service.service;

public interface OtpService {
    /**
     * Generate OTP code for email verification
     * @param email User email
     * @return Generated OTP code
     */
    String generateOtp(String email);

    /**
     * Validate OTP code
     * @param email User email
     * @param otpCode OTP code to validate
     * @return true if valid, false otherwise
     */
    boolean validateOtp(String email, String otpCode);
}
