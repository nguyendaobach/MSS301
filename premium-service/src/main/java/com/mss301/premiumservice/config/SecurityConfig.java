//package com.mss301.premiumservice.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class SecurityConfig extends OncePerRequestFilter {
//    @Value("${gateway.secret}")
//    private String gatewaySecret;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String secret = request.getHeader("X-Gateway-Secret");
//        if (!gatewaySecret.equals(secret)) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Only Gateway can access this service");
//            return;
//        }
//        filterChain.doFilter(request, response);
//    }
//}
