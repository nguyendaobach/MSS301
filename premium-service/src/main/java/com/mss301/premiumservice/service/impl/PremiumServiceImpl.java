package com.mss301.premiumservice.service.impl;

import com.mss301.premiumservice.entity.Premium;
import com.mss301.premiumservice.repository.PremiumRepository;
import com.mss301.premiumservice.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PremiumRepository premiumRepository;


    @Override
    public void save(String userId) {
        premiumRepository.save(
                Premium.builder()
                        .userId(userId)
                        .isPremium(true)
                        .time(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public Premium findByUserId(String userId) {
        return premiumRepository.findByUserId(userId);
    }
}
