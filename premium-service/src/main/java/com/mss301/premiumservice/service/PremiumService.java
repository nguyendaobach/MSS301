package com.mss301.premiumservice.service;

import com.mss301.premiumservice.entity.Premium;

public interface PremiumService {
    void save(String userId);
    Premium findByUserId(String userId);

    void premiumRequest(String userId);
}
