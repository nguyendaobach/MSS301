package com.mss301.quizservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SecurityConfig extends OncePerRequestFilter {
    @Value("${gateway.secret}")
    private String gatewaySecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Allow actuator endpoints for Prometheus monitoring
        String path = request.getRequestURI();
        if (path != null && (path.startsWith("/actuator") || 
                             path.startsWith("/v3/api-docs") || 
                             path.startsWith("/swagger-ui") || 
                             path.startsWith("/swagger-ui.html") ||
                             path.startsWith("/webjars"))) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Allow OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String secret = request.getHeader("X-Gateway-Secret");
        if (!gatewaySecret.equals(secret)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Only Gateway can access this service");
            return;
        }
        filterChain.doFilter(request, response);
    }
    
    @Configuration
    static class CorsConfig {
        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:8080",
                "http://localhost:8081"
            ));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
            config.setAllowedHeaders(List.of("*"));
            config.setMaxAge(3600L);
            source.registerCorsConfiguration("/**", config);
            return new CorsFilter(source);
        }
    }
}
