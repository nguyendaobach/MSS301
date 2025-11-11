package com.mss301.aiservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HeaderExtractor {

    public static String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new NullPointerException("X-User-Id is null");
        }
        return userId;
    }

    public static String getUserEmail(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
        if (email == null || email.isEmpty()) {
            throw new NullPointerException("X-User-Email is null");
        }
        return email;
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        return Map.of(
                "X-User-Id", getUserId(request),
                "X-User-Email", getUserEmail(request)
        );
    }
}
