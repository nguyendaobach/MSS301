package com.mss301.adminservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mss301.adminservice.config.JwtUtils;
import com.mss301.adminservice.dto.ResponseApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip authentication for health check and swagger endpoints
        String path = request.getRequestURI();

        // Public endpoints - no authentication required
        if (path.startsWith("/health") ||
            path.contains("/swagger-ui") ||
            path.contains("/v3/api-docs") ||
            path.contains("/swagger-resources") ||
            path.contains("/webjars/") ||
            path.equals("/")) {

            log.debug("Skipping JWT authentication for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromRequest(request);

            if (token == null || !jwtUtils.validateToken(token)) {
                log.warn("Authentication failed for path: {} - Invalid or missing token", path);
                sendUnauthorizedResponse(response, "Invalid or missing token");
                return;
            }

            // Extract user information from token
            String userId = jwtUtils.extractUserId(token);
            String email = jwtUtils.extractEmail(token);
            String role = jwtUtils.extractRole(token);
            List<String> permissions = jwtUtils.extractPermissions(token);

            // Set user info in request attributes for later use
            request.setAttribute("userId", userId);
            request.setAttribute("email", email);
            request.setAttribute("role", role);
            request.setAttribute("permissions", permissions);

            log.debug("Authenticated user: {} with role: {} (permissions: {})", email, role, permissions);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Authentication failed");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseApi<String> errorResponse = new ResponseApi<>(401, message, null);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
