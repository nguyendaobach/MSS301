package com.mss301.premiumservice.repository;

import com.mss301.premiumservice.entity.Premium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiumRepository extends JpaRepository<Premium,Integer> {
    Premium findByUserId(String userId);
}
