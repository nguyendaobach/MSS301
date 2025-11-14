package com.mss301.identity_service.service.impl;

import com.mss301.identity_service.entity.OtpToken;
import com.mss301.identity_service.entity.OtpType;
import com.mss301.identity_service.repository.OtpTokenRepository;
import com.mss301.identity_service.service.EmailService;
import com.mss301.identity_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceimpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Override
    @Transactional
    public void generateAndSendOtp(String email, OtpType otpType) {
        // Tạo mã OTP ngẫu nhiên
        String otpCode = generateRandomOtp();

        // Đặt thời gian hết hạn sau 5 phút
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        // Lưu OTP vào database
        OtpToken otpToken = new OtpToken();
        otpToken.setEmail(email);
        otpToken.setOtpCode(otpCode);
        otpToken.setExpiryDate(expiryDate);
        otpToken.setUsed(false);
        otpToken.setOtpType(otpType);

        otpTokenRepository.save(otpToken);

        // Gửi email
        String subject = otpType == OtpType.REGISTRATION
            ? "Email Verification - OTP Code"
            : "Password Reset - OTP Code";
        String message = String.format(
            "Your OTP code is: %s\n\nThis code will expire in %d minutes.\n\nIf you didn't request this, please ignore this email.",
            otpCode, OTP_EXPIRY_MINUTES
        );

        try {
            emailService.sendEmail(email, subject, message);
            log.info("OTP sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", email, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otpCode, OtpType otpType) {
        Optional<OtpToken> otpTokenOpt = otpTokenRepository
                .findByEmailAndOtpCodeAndOtpTypeAndUsedFalseAndExpiryDateAfter(
                    email, otpCode, otpType, LocalDateTime.now()
                );

        if (otpTokenOpt.isPresent()) {
            OtpToken otpToken = otpTokenOpt.get();
            // Đánh dấu OTP đã được sử dụng
            otpToken.setUsed(true);
            otpTokenRepository.save(otpToken);
            return true;
        }

        return false;
    }

    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}
