package com.mss301.identity_service.repository;

import com.mss301.identity_service.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {
    Optional<OtpToken> findByEmailAndOtpCodeAndUsedFalseAndExpiryDateAfter(
            String email, String otpCode, LocalDateTime now);
    Optional<OtpToken> findTopByEmailOrderByExpiryDateDesc(String email);
}
