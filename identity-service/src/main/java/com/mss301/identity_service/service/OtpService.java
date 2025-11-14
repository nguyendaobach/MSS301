package com.mss301.identity_service.service;

import com.mss301.identity_service.entity.OtpType;

public interface OtpService {

    void generateAndSendOtp(String email, OtpType otpType);

    boolean verifyOtp(String email, String otpCode, OtpType otpType);
}
