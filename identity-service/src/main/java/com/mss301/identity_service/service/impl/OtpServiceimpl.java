package com.mss301.identity_service.service.impl;

import com.mss301.identity_service.entity.OtpToken;
import com.mss301.identity_service.repository.OtpTokenRepository;
import com.mss301.identity_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceimpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    public String generateOtp(String email) {
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

        otpTokenRepository.save(otpToken);

        return otpCode;
    }

    public boolean validateOtp(String email, String otpCode) {
        Optional<OtpToken> otpTokenOpt = otpTokenRepository
                .findByEmailAndOtpCodeAndUsedFalseAndExpiryDateAfter(email, otpCode, LocalDateTime.now());

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
