package com.mss301.identity_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request để đặt lại mật khẩu với mã OTP")
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Email của người dùng", example = "user@example.com")
    private String email;

    @NotBlank(message = "Mã OTP không được để trống")
    @Size(min = 6, max = 6, message = "Mã OTP phải có 6 ký tự")
    @Schema(description = "Mã OTP xác thực", example = "123456")
    private String otpCode;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Schema(description = "Mật khẩu mới", example = "newPassword123")
    private String newPassword;
}



