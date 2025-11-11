package com.mss301.identity_service.service;

import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.*;
import com.mss301.identity_service.dto.response.LoginResponse;
import com.mss301.identity_service.dto.response.TokenVerificationResponse;
import com.mss301.identity_service.entity.User;

import java.util.List;

public interface UserService {
    ResponseApi<LoginResponse> login(LoginRequestDTO request);
    ResponseApi<String> register(RegisterRequestDTO request);

    // Thêm các phương thức mới cho đăng ký với OTP
    ResponseApi<String> registerWithOtp(RegisterWithOtpRequestDTO request);
    ResponseApi<String> verifyRegistration(VerifyOtpRequestDTO request);
    ResponseApi<String> resendOtp(String email);

    // Phương thức xác thực token chi tiết
    ResponseApi<TokenVerificationResponse> introspect(TokenVerificationRequestDTO request);
    ResponseApi<List<User>> getUser();

    // Phương thức quên mật khẩu
    ResponseApi<String> forgotPassword(ForgotPasswordRequestDTO request);
    ResponseApi<String> resetPassword(ResetPasswordRequestDTO request);
}
