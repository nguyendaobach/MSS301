package com.mss301.identity_service.service;

import com.mss301.identity_service.dto.ResponseApi;
import com.mss301.identity_service.dto.request.*;
import com.mss301.identity_service.dto.response.LoginResponse;
import com.mss301.identity_service.dto.response.TokenVerificationResponse;
import com.mss301.identity_service.dto.response.UserProfileResponse;
import com.mss301.identity_service.entity.Role;
import com.mss301.identity_service.entity.User;

import java.util.List;

public interface UserService {
    ResponseApi<LoginResponse> login(LoginRequestDTO request);
    ResponseApi<String> register(RegisterRequestDTO request);

    ResponseApi<String> registerWithOtp(RegisterWithOtpRequestDTO request);
    ResponseApi<String> verifyRegistration(VerifyOtpRequestDTO request);
    ResponseApi<String> resendOtp(String email);

    ResponseApi<TokenVerificationResponse> introspect(TokenVerificationRequestDTO request);
    ResponseApi<List<User>> getUser();

    ResponseApi<String> forgotPassword(ForgotPasswordRequestDTO request);
    ResponseApi<String> resetPassword(ResetPasswordRequestDTO request);
        ResponseApi<String> resendForgotPasswordOtp(String email);

    ResponseApi<UserProfileResponse> getCurrentUserProfile(String email);
    ResponseApi<UserProfileResponse> updateProfile(String email, UpdateProfileRequestDTO request);
    ResponseApi<String> changePassword(String email, ChangePasswordRequestDTO request);

    List<Role> getAllRole();
}
