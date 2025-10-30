package com.mss301.identity_service.controller;

import com.mss301.identity_service.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/token")
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtils jwtUtils;

    @GetMapping("/roles")
    public ResponseEntity<List<String>> extractRoles(@RequestParam String token) {
        List<String> roles = jwtUtils.extractRoles(token);
        return ResponseEntity.ok(roles);
    }
}
