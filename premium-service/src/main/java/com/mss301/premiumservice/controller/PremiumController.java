package com.mss301.premiumservice.controller;

import com.mss301.premiumservice.entity.Premium;
import com.mss301.premiumservice.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${allowed.origins}", methods = {RequestMethod.GET, RequestMethod.POST}, allowedHeaders = "*", allowCredentials = "true")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/premium/premiums/{userId}")
    public ResponseEntity<Void> savePremium(@PathVariable String userId) {
        premiumService.save(userId);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/premium/premiums/{userId}")
     public ResponseEntity<Premium> getPremiumByUserId(@PathVariable String userId) {
        Premium premium = premiumService.findByUserId(userId);
        return ResponseEntity.ok(premium);
    }
}
