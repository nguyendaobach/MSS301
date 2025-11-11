package com.mss301.adminservice.controller;

import com.mss301.adminservice.config.JwtUtils;
import com.mss301.adminservice.dto.ResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Info", description = "APIs for extracting user information from token")
@SecurityRequirement(name = "bearerAuth")
public class AuthInfoController {

    private final JwtUtils jwtUtils;

    @GetMapping("/me")
    @Operation(summary = "Get current user info", description = "Extract user information from JWT token")
    public ResponseEntity<ResponseApi<Map<String, Object>>> getCurrentUser(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) request.getAttribute("permissions");

        // Extract token to get all roles
        String token = extractTokenFromRequest(request);
        List<String> allRoles = jwtUtils.extractRoles(token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("email", email);
        userInfo.put("role", role); // Primary role (first role)
        userInfo.put("roles", allRoles); // All roles
        userInfo.put("permissions", permissions);

        ResponseApi<Map<String, Object>> response = new ResponseApi<>(
                200,
                "User information retrieved successfully",
                userInfo
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/role")
    @Operation(summary = "Get current user role", description = "Extract primary role from JWT token")
    public ResponseEntity<ResponseApi<Map<String, String>>> getCurrentUserRole(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        Map<String, String> roleInfo = new HashMap<>();
        roleInfo.put("role", role);

        ResponseApi<Map<String, String>> response = new ResponseApi<>(
                200,
                "Role extracted successfully",
                roleInfo
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    @Operation(summary = "Get all user roles", description = "Extract all roles from JWT token")
    public ResponseEntity<ResponseApi<Map<String, Object>>> getCurrentUserRoles(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        List<String> roles = jwtUtils.extractRoles(token);

        Map<String, Object> rolesInfo = new HashMap<>();
        rolesInfo.put("roles", roles);

        ResponseApi<Map<String, Object>> response = new ResponseApi<>(
                200,
                "All roles extracted successfully",
                rolesInfo
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/permissions")
    @Operation(summary = "Get current user permissions", description = "Extract permissions from JWT token")
    public ResponseEntity<ResponseApi<Map<String, Object>>> getCurrentUserPermissions(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) request.getAttribute("permissions");

        Map<String, Object> permissionInfo = new HashMap<>();
        permissionInfo.put("permissions", permissions);

        ResponseApi<Map<String, Object>> response = new ResponseApi<>(
                200,
                "Permissions extracted successfully",
                permissionInfo
        );

        return ResponseEntity.ok(response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
