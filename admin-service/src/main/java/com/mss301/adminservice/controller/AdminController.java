package com.mss301.adminservice.controller;

import com.mss301.adminservice.annotation.RequireRole;
import com.mss301.adminservice.dto.ResponseApi;
import com.mss301.adminservice.dto.request.CreateUserRequest;
import com.mss301.adminservice.dto.request.UpdateUserRequest;
import com.mss301.adminservice.dto.response.RoleResponse;
import com.mss301.adminservice.dto.response.UserResponse;
import com.mss301.adminservice.dto.response.UserStatsResponse;
import com.mss301.adminservice.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for managing users (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final IAdminService adminService;

    @GetMapping("/users")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Get all users", description = "Retrieve list of all users (Admin only)")
    public ResponseEntity<ResponseApi<List<UserResponse>>> getAllUsers() {
        ResponseApi<List<UserResponse>> response = adminService.getAllUsers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/users/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID (Admin only)")
    public ResponseEntity<ResponseApi<UserResponse>> getUserById(
     @PathVariable UUID id) {
        ResponseApi<UserResponse> response = adminService.getUserById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/users")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Create new user", description = "Create a new user account (Admin only)")
    public ResponseEntity<ResponseApi<UserResponse>> createUser(
          @RequestBody CreateUserRequest request) {
        ResponseApi<UserResponse> response = adminService.createUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/users/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    public ResponseEntity<ResponseApi<UserResponse>> updateUser(
           @PathVariable UUID id,
             @RequestBody UpdateUserRequest request) {
        ResponseApi<UserResponse> response = adminService.updateUser(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/users/{id}")
    @RequireRole({"SUPER_ADMIN"})
    @Operation(summary = "Delete user", description = "Delete a user (Super Admin only)")
    public ResponseEntity<ResponseApi<String>> deleteUser(
           @PathVariable UUID id) {
        ResponseApi<String> response = adminService.deleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/users/{id}/toggle-status")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Toggle user status", description = "Enable/Disable user account (Admin only)")
    public ResponseEntity<ResponseApi<UserResponse>> toggleUserStatus(@PathVariable UUID id) {
        ResponseApi<UserResponse> response = adminService.toggleUserStatus(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/users/role/{roleCode}")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Get users by role", description = "Retrieve users with specific role (Admin only)")
    public ResponseEntity<ResponseApi<List<UserResponse>>> getUsersByRole(
            @PathVariable String roleCode) {
        ResponseApi<List<UserResponse>> response = adminService.getUsersByRole(roleCode);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/stats")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Get user statistics", description = "Retrieve user statistics (Admin only)")
    public ResponseEntity<ResponseApi<UserStatsResponse>> getUserStats() {
        ResponseApi<UserStatsResponse> response = adminService.getUserStats();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/roles")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    @Operation(summary = "Get all roles", description = "Retrieve all available roles in the system (Admin only)")
    public ResponseEntity<ResponseApi<List<RoleResponse>>> getAllRoles() {
        ResponseApi<List<RoleResponse>> response = adminService.getAllRoles();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
