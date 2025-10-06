package mss.mindmap.mindmapservice.mindmap.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class HeaderExtractor {

    public String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new NullPointerException("X-User-Id is null");
        }
        return userId;
    }

    public String getUserEmail(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
        if (email == null || email.isEmpty()) {
            throw new NullPointerException("X-User-Email is null");
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
