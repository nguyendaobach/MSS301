package com.mss301.quizservice.util;

import com.mss301.quizservice.exception.AppException;
import com.mss301.quizservice.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.Map;

@UtilityClass
public class HeaderExtractor {

    public String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new AppException(ErrorCode.MISSING_ID_REQUESTHEADER);
        }
        return userId;
    }

    public String getUserEmail(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.MISSING_EMAIL_REQUESTHEADER);
        }
        return email;
    }

    public Map<String, String> getHeaders(HttpServletRequest request) {
        return Map.of(
                "X-User-Id", getUserId(request),
                "X-User-Email", getUserEmail(request)
        );
    }
}
