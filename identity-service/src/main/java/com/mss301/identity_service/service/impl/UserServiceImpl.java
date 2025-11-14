package com.mss301.identity_service.service.impl;

import com.mss301.identity_service.config.JwtUtils;
import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.*;
import com.mss301.identity_service.dto.response.LoginResponse;
import com.mss301.identity_service.dto.response.TokenVerificationResponse;
import com.mss301.identity_service.dto.response.UserProfileResponse;
import com.mss301.identity_service.entity.*;
import com.mss301.identity_service.repository.RoleRepository;
import com.mss301.identity_service.repository.UserRepository;
import com.mss301.identity_service.service.OtpService;
import com.mss301.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<LoginResponse> login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User user = userPrinciple.getUser();

            if (user.getStatus() != Status.ACTIVE) {
                return ResponseApi.<LoginResponse>builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("Account is not active. Please verify your email first.")
                        .build();
            }

            String token = jwtUtils.generateToken(userPrinciple);

            LoginResponse loginResponse = new LoginResponse(
                    user.getId(),
                    token,
                    user.getFullName(),
                    user.getRole().getCode(),
                    user.getEmail()
            );

            return ResponseApi.<LoginResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Login successful")
                    .data(loginResponse)
                    .build();
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            return ResponseApi.<LoginResponse>builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid email or password")
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> register(RegisterRequestDTO request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseApi.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Email already exists")
                        .build();
            }

            Role studentRole = roleRepository.findByCode("ROLE_STUDENT")
                    .orElseThrow(() -> new RuntimeException("Role STUDENT not found"));

            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .role(studentRole)
                    .status(Status.ACTIVE)
                    .build();

            userRepository.save(user);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("User registered successfully")
                    .build();
        } catch (Exception e) {
            log.error("Registration failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> registerWithOtp(RegisterWithOtpRequestDTO request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseApi.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Email already exists")
                        .build();
            }

            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .role(role)
                    .status(Status.INACTIVE)
                    .build();

            userRepository.save(user);

            otpService.generateAndSendOtp(request.getEmail(), OtpType.REGISTRATION);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Registration successful. Please check your email for OTP verification.")
                    .build();
        } catch (Exception e) {
            log.error("Registration with OTP failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> verifyRegistration(VerifyOtpRequestDTO request) {
        try {
            if (!otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpType.REGISTRATION)) {
                return ResponseApi.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid or expired OTP")
                        .build();
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setStatus(Status.ACTIVE);
            userRepository.save(user);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Email verified successfully. You can now login.")
                    .build();
        } catch (Exception e) {
            log.error("OTP verification failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Verification failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> resendOtp(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getStatus() == Status.ACTIVE) {
                return ResponseApi.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("User is already verified")
                        .build();
            }

            otpService.generateAndSendOtp(email, OtpType.REGISTRATION);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("OTP sent successfully")
                    .build();
        } catch (Exception e) {
            log.error("Resend OTP failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to resend OTP: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<TokenVerificationResponse> introspect(TokenVerificationRequestDTO request) {
        try {
            boolean isValid = jwtUtils.validateToken(request.getToken());

            if (!isValid) {
                return ResponseApi.<TokenVerificationResponse>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Invalid or expired token")
                        .data(new TokenVerificationResponse(false))
                        .build();
            }

            String email = jwtUtils.extractUsername(request.getToken());
            List<String> roles = jwtUtils.extractRoles(request.getToken());

            TokenVerificationResponse response = new TokenVerificationResponse(true);
            response.setEmail(email);
            response.setRoles(roles);

            return ResponseApi.<TokenVerificationResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Token is valid")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Token introspection failed", e);
            return ResponseApi.<TokenVerificationResponse>builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .data(new TokenVerificationResponse(false))
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<List<User>> getUser() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseApi.<List<User>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Users retrieved successfully")
                    .data(users)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get users", e);
            return ResponseApi.<List<User>>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to retrieve users: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> forgotPassword(ForgotPasswordRequestDTO request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            otpService.generateAndSendOtp(request.getEmail(), OtpType.PASSWORD_RESET);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("OTP sent to your email for password reset")
                    .build();
        } catch (Exception e) {
            log.error("Forgot password failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to process forgot password: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> resetPassword(ResetPasswordRequestDTO request) {
        try {
            if (!otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpType.PASSWORD_RESET)) {
                return ResponseApi.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid or expired OTP")
                        .build();
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Password reset successfully")
                    .build();
        } catch (Exception e) {
            log.error("Reset password failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to reset password: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> resendForgotPasswordOtp(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            otpService.generateAndSendOtp(email, OtpType.PASSWORD_RESET);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("OTP sent successfully")
                    .build();
        } catch (Exception e) {
            log.error("Resend forgot password OTP failed", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to resend OTP: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<UserProfileResponse> getCurrentUserProfile(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserProfileResponse profile = new UserProfileResponse();
            profile.setId(user.getId());
            profile.setEmail(user.getEmail());
            profile.setFullName(user.getFullName());
            profile.setRole(user.getRole().getCode());
            profile.setStatus(user.getStatus());
            profile.setCreatedAt(user.getCreatedAt());
            profile.setUpdatedAt(user.getUpdatedAt());
            return ResponseApi.<UserProfileResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Profile retrieved successfully")
                    .data(profile)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get user profile", e);
            return ResponseApi.<UserProfileResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to retrieve profile: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<UserProfileResponse> updateProfile(String email, UpdateProfileRequestDTO request) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setFullName(request.getFullName());
            userRepository.save(user);

            UserProfileResponse profile = new UserProfileResponse();
            profile.setId(user.getId());
            profile.setEmail(user.getEmail());
            profile.setFullName(user.getFullName());
            profile.setRole(user.getRole().getCode());
            profile.setStatus(user.getStatus());

            return ResponseApi.<UserProfileResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Profile updated successfully")
                    .data(profile)
                    .build();
        } catch (Exception e) {
            log.error("Failed to update profile", e);
            return ResponseApi.<UserProfileResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to update profile: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> changePassword(String email, ChangePasswordRequestDTO request) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseApi.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Current password is incorrect")
                        .build();
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Password changed successfully")
                    .build();
        } catch (Exception e) {
            log.error("Failed to change password", e);
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to change password: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }
}

