package com.mss301.adminservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "Service health check endpoints (No authentication required)")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Check if the admin service is running")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "admin-service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/swagger-test")
    @Operation(summary = "Swagger test", description = "Test endpoint to verify Swagger is working")
    public ResponseEntity<Map<String, String>> swaggerTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Swagger is working!");
        response.put("service", "admin-service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/swagger-info")
    @Operation(summary = "Swagger configuration info", description = "Get current Swagger configuration details")
    public ResponseEntity<Map<String, Object>> swaggerInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("swagger-ui-url", "http://localhost:8087/swagger-ui.html");
        response.put("api-docs-url", "http://localhost:8087/v3/api-docs");
        response.put("gateway-swagger-url", "http://localhost:8080/swagger-ui.html");
        response.put("gateway-api-docs-url", "http://localhost:8080/api/v1/admin/v3/api-docs");
        response.put("service", "admin-service");
        response.put("port", 8087);
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
}

