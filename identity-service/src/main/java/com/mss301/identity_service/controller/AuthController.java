package com.mss301.identity_service.controller;

import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.*;
import com.mss301.identity_service.dto.response.LoginResponse;
import com.mss301.identity_service.dto.response.TokenVerificationResponse;
import com.mss301.identity_service.entity.Role;
import com.mss301.identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API xác thực và đăng ký người dùng")
public class AuthController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<ResponseApi<LoginResponse>> login(@Valid @RequestBody LoginRequestDTO request) {
        ResponseApi<LoginResponse> response = userService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register-with-otp")
    public ResponseEntity<ResponseApi<String>> registerWithOtp(@Valid @RequestBody RegisterWithOtpRequestDTO request) {
        ResponseApi<String> response = userService.registerWithOtp(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseApi<String>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        ResponseApi<String> response = userService.verifyRegistration(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseApi<String>> resendOtp(
            @Parameter(description = "Email đã đăng ký chờ xác thực", required = true)
            @RequestParam String email) {
        ResponseApi<String> response = userService.resendOtp(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/introspect")
    public ResponseEntity<ResponseApi<TokenVerificationResponse>> introspect(@Valid @RequestBody TokenVerificationRequestDTO request) {
        ResponseApi<TokenVerificationResponse> response = userService.introspect(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseApi<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        ResponseApi<String> response = userService.forgotPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ResponseApi<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        ResponseApi<String> response = userService.resetPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/resend-forgot-password-otp")
    public ResponseEntity<ResponseApi<String>> resendForgotPasswordOtp(
            @Parameter(description = "Email cần đặt lại mật khẩu", required = true)
            @RequestParam String email) {
        ResponseApi<String> response = userService.resendForgotPasswordOtp(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/role")
    public List<Role> getRoles() {
        return userService.getAllRole();
    }
}
