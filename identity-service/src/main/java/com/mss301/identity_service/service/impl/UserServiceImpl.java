package com.mss301.identity_service.service.impl;

import com.mss301.identity_service.config.JwtUtils;
import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.*;
import com.mss301.identity_service.dto.response.LoginResponse;
import com.mss301.identity_service.dto.response.TokenVerificationResponse;
import com.mss301.identity_service.entity.Role;
import com.mss301.identity_service.entity.Status;
import com.mss301.identity_service.entity.User;
import com.mss301.identity_service.entity.UserPrinciple;
import com.mss301.identity_service.repository.RoleRepository;
import com.mss301.identity_service.repository.UserRepository;
import com.mss301.identity_service.service.EmailService;
import com.mss301.identity_service.service.OtpService;
import com.mss301.identity_service.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;

    // Lưu trữ tạm thời thông tin đăng ký chờ xác thực
    private final Map<String, RegisterWithOtpRequestDTO> pendingRegistrations = new HashMap<>();

    @Override
    public ResponseApi<LoginResponse> login(LoginRequestDTO request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User user = userPrinciple.getUser();

            // Generate JWT token
            String token = jwtUtils.generateToken(userPrinciple);

            // Create login response
            LoginResponse response = new LoginResponse(
                    user.getId(),
                    token,
                    user.getFullName(),
                    user.getRole().getCode(),
                    user.getEmail()
            );

            return ResponseApi.<LoginResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Login successful")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ResponseApi.<LoginResponse>builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid email or password")
                    .build();
        }
    }

    @Override
    public ResponseApi<String> register(RegisterRequestDTO request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email already in use")
                    .build();
        }

        try {
            // Create new user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setStatus(Status.ACTIVE);
            user.setCreatedAt(OffsetDateTime.now());
            user.setUpdatedAt(OffsetDateTime.now());

            // Lấy role VIEWER mặc định
            Role defaultRole = roleRepository.findByCode("VIEWER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setCode("VIEWER");
                        newRole.setName("Viewer");
                        return roleRepository.save(newRole);
                    });

            user.setRole(defaultRole);

            userRepository.save(user);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("User registered successfully")
                    .data("Registration successful")
                    .build();
        } catch (Exception e) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error registering user: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseApi<String> registerWithOtp(RegisterWithOtpRequestDTO request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email đã được sử dụng")
                    .build();
        }

        try {
            // Lưu thông tin đăng ký vào bộ nhớ tạm thời
            pendingRegistrations.put(request.getEmail(), request);

            // Tạo mã OTP
            String otpCode = otpService.generateOtp(request.getEmail());

            // Gửi email chứa mã OTP
            emailService.sendOtpEmail(request.getEmail(), otpCode);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Mã xác thực đã được gửi đến email của bạn")
                    .data("Vui lòng kiểm tra email để lấy mã xác thực")
                    .build();
        } catch (MessagingException e) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi gửi email: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi đăng ký người dùng: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> verifyRegistration(VerifyOtpRequestDTO request) {
        // Xác thực mã OTP
        boolean isValidOtp = otpService.validateOtp(request.getEmail(), request.getOtpCode());

        if (!isValidOtp) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Mã OTP không hợp lệ hoặc đã hết hạn")
                    .build();
        }

        // Lấy thông tin đăng ký từ bộ nhớ tạm thời
        RegisterWithOtpRequestDTO registrationData = pendingRegistrations.get(request.getEmail());

        if (registrationData == null) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Không tìm thấy thông tin đăng ký")
                    .build();
        }

        try {
            // Tạo người dùng mới
            User user = new User();
            user.setEmail(registrationData.getEmail());
            user.setPassword(passwordEncoder.encode(registrationData.getPassword()));
            user.setFullName(registrationData.getFullName());
            user.setStatus(Status.ACTIVE);
            user.setCreatedAt(OffsetDateTime.now());
            user.setUpdatedAt(OffsetDateTime.now());

            // Lấy role VIEWER mặc định
            Role defaultRole = roleRepository.findByCode("VIEWER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setCode("VIEWER");
                        newRole.setName("Viewer");
                        return roleRepository.save(newRole);
                    });

            user.setRole(defaultRole);

            userRepository.save(user);

            // Xóa thông tin đăng ký khỏi bộ nhớ tạm thời
            pendingRegistrations.remove(request.getEmail());

            return ResponseApi.<String>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Đăng ký thành công")
                    .data("Tài khoản đã được tạo thành công")
                    .build();
        } catch (Exception e) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi đăng ký người dùng: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseApi<String> resendOtp(String email) {
        // Kiểm tra xem email đã đăng ký chưa
        if (userRepository.existsByEmail(email)) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email đã được đăng ký")
                    .build();
        }

        // Kiểm tra xem email có trong danh sách chờ xác thực không
        if (!pendingRegistrations.containsKey(email)) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Không tìm thấy thông tin đăng ký cho email này")
                    .build();
        }

        try {
            // Tạo mã OTP mới
            String otpCode = otpService.generateOtp(email);

            // Gửi email chứa mã OTP
            emailService.sendOtpEmail(email, otpCode);

            return ResponseApi.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Mã xác thực mới đã được gửi đến email của bạn")
                    .data("Vui lòng kiểm tra email để lấy mã xác thực")
                    .build();
        } catch (MessagingException e) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi gửi email: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseApi.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi gửi lại mã OTP: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<TokenVerificationResponse> introspect(TokenVerificationRequestDTO request) {
        try {
            String token = request.getToken();
            boolean isValid = jwtUtils.validateToken(token);

            TokenVerificationResponse.TokenVerificationResponseBuilder responseBuilder = TokenVerificationResponse.builder();

            if (isValid) {
                // Lấy thông tin từ token hợp lệ
                String username = jwtUtils.extractUsername(token);
                Date expiration = jwtUtils.extractExpiration(token);

                TokenVerificationResponse tokenResponse = responseBuilder
                        .valid(true)
                        .username(username)
                        .expiration(expiration)
                        .message("Token hợp lệ")
                        .build();

                return ResponseApi.<TokenVerificationResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Token hợp lệ")
                        .data(tokenResponse)
                        .success(true)
                        .build();
            } else {
                TokenVerificationResponse tokenResponse = responseBuilder
                        .valid(false)
                        .message("Token không hợp lệ")
                        .build();

                return ResponseApi.<TokenVerificationResponse>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Token không hợp lệ")
                        .data(tokenResponse)
                        .success(false)
                        .build();
            }
        } catch (ExpiredJwtException e) {
            log.warn("Token đã hết hạn: {}", e.getMessage());

            TokenVerificationResponse tokenResponse = TokenVerificationResponse.builder()
                    .valid(false)
                    .username(e.getClaims().getSubject())
                    .expiration(e.getClaims().getExpiration())
                    .message("Token đã hết hạn")
                    .build();

            return ResponseApi.<TokenVerificationResponse>builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Token đã hết hạn")
                    .data(tokenResponse)
                    .success(false)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi xác thực token: {}", e.getMessage());

            TokenVerificationResponse tokenResponse = TokenVerificationResponse.builder()
                    .valid(false)
                    .message("Lỗi xác thực token: " + e.getMessage())
                    .build();

            return ResponseApi.<TokenVerificationResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi xác thực token: " + e.getMessage())
                    .data(tokenResponse)
                    .success(false)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<List<User>> getUser() {
        return ResponseApi.<List<User>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách người dùng thành công")
                .data(userRepository.findAll())
                .success(true)
                .build();
    }
}
