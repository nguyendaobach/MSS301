package com.mss301.identity_service.controller;

import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.*;
import com.mss301.identity_service.dto.response.LoginResponse;
import com.mss301.identity_service.dto.response.TokenVerificationResponse;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API xác thực và đăng ký người dùng")
public class AuthController {

    private final UserService userService;

    @Operation(
        summary = "Đăng nhập người dùng",
        description = "API cho phép người dùng đăng nhập bằng email và mật khẩu, trả về JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Email hoặc mật khẩu không chính xác",
            content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseApi<LoginResponse>> login(@Valid @RequestBody LoginRequestDTO request) {
        ResponseApi<LoginResponse> response = userService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Đăng ký người dùng",
        description = "API cho phép người dùng đăng ký tài khoản mới (không yêu cầu xác thực email)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
        @ApiResponse(responseCode = "400", description = "Email đã tồn tại hoặc dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseApi<String>> register(@Valid @RequestBody RegisterRequestDTO request) {
        ResponseApi<String> response = userService.register(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Đăng ký với xác thực OTP qua email",
        description = "API cho phép người dùng bắt đầu quá trình đăng ký có xác thực email bằng mã OTP"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đã gửi mã OTP đến email"),
        @ApiResponse(responseCode = "400", description = "Email đã tồn tại hoặc dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server hoặc lỗi gửi email")
    })
    @PostMapping("/register-with-otp")
    public ResponseEntity<ResponseApi<String>> registerWithOtp(@Valid @RequestBody RegisterWithOtpRequestDTO request) {
        ResponseApi<String> response = userService.registerWithOtp(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Xác thực mã OTP",
        description = "API cho phép người dùng xác thực mã OTP đã nhận được qua email để hoàn tất đăng ký"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Xác thực thành công, tài khoản đã được tạo"),
        @ApiResponse(responseCode = "400", description = "Mã OTP không hợp lệ hoặc đã hết hạn"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseApi<String>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        ResponseApi<String> response = userService.verifyRegistration(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Gửi lại mã OTP",
        description = "API cho phép người dùng yêu cầu gửi lại mã OTP nếu không nhận được hoặc mã đã hết hạn"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đã gửi lại mã OTP thành công"),
        @ApiResponse(responseCode = "400", description = "Email không tồn tại trong danh sách chờ xác thực"),
        @ApiResponse(responseCode = "500", description = "Lỗi server hoặc lỗi gửi email")
    })
    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseApi<String>> resendOtp(
            @Parameter(description = "Email đã đăng ký chờ xác thực", required = true)
            @RequestParam String email) {
        ResponseApi<String> response = userService.resendOtp(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Kiểm tra tính hợp lệ của token JWT chi tiết",
        description = "API xác thực và phân tích thông tin chi tiết của token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trả về thông tin chi tiết về token"),
        @ApiResponse(responseCode = "400", description = "Token không hợp lệ hoặc đã hết hạn")
    })
    @PostMapping("/introspect")
    public ResponseEntity<ResponseApi<TokenVerificationResponse>> introspect(@Valid @RequestBody TokenVerificationRequestDTO request) {
        ResponseApi<TokenVerificationResponse> response = userService.introspect(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Quên mật khẩu",
        description = "API yêu cầu đặt lại mật khẩu, gửi mã OTP đến email người dùng"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đã gửi mã OTP đến email"),
        @ApiResponse(responseCode = "404", description = "Email không tồn tại trong hệ thống"),
        @ApiResponse(responseCode = "500", description = "Lỗi server hoặc lỗi gửi email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseApi<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        ResponseApi<String> response = userService.forgotPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
        summary = "Đặt lại mật khẩu",
        description = "API xác thực mã OTP và đặt lại mật khẩu mới cho người dùng"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đặt lại mật khẩu thành công"),
        @ApiResponse(responseCode = "400", description = "Mã OTP không hợp lệ hoặc đã hết hạn"),
        @ApiResponse(responseCode = "404", description = "Người dùng không tồn tại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseApi<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        ResponseApi<String> response = userService.resetPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
