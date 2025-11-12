package com.mss301.identity_service.controller;

import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.ChangePasswordRequestDTO;
import com.mss301.identity_service.dto.request.UpdateProfileRequestDTO;
import com.mss301.identity_service.dto.response.UserProfileResponse;
import com.mss301.identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<ResponseApi<UserProfileResponse>> getCurrentUserProfile() {
        String email = getCurrentUserEmail();
        ResponseApi<UserProfileResponse> response = userService.getCurrentUserProfile(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/profile")
    public ResponseEntity<ResponseApi<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        String email = getCurrentUserEmail();
        ResponseApi<UserProfileResponse> response = userService.updateProfile(email, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/change-password")
    public ResponseEntity<ResponseApi<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        String email = getCurrentUserEmail();
        ResponseApi<String> response = userService.changePassword(email, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

