package com.mss301.adminservice.config;

import com.mss301.adminservice.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
            new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setOrder(1); // Chạy sau nếu có filters khác
        registration.addUrlPatterns("/*"); // Apply cho tất cả URLs
        return registration;
    }

    // CORS filter removed - Gateway handles CORS
    // When accessing through Gateway, CORS is handled at Gateway level
    // When accessing directly, CORS is not needed for server-to-server communication
}

