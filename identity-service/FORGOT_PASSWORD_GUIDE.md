# Hướng Dẫn Sử Dụng Tính Năng Quên Mật Khẩu

## Tổng Quan

Tính năng quên mật khẩu cho phép người dùng đặt lại mật khẩu của họ thông qua xác thực email bằng mã OTP (One-Time Password).

## Quy Trình

### Bước 1: Yêu Cầu Đặt Lại Mật Khẩu

**Endpoint:** `POST /auth/forgot-password`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response (Success - 200):**
```json
{
  "status": 200,
  "message": "Mã xác thực đặt lại mật khẩu đã được gửi đến email của bạn",
  "data": "Vui lòng kiểm tra email để lấy mã xác thực",
  "success": true
}
```

**Response (Email không tồn tại - 404):**
```json
{
  "status": 404,
  "message": "Email không tồn tại trong hệ thống",
  "success": false
}
```

**Mô tả:**
- Kiểm tra email có tồn tại trong hệ thống
- Tạo mã OTP 6 chữ số
- Gửi email chứa mã OTP đến địa chỉ email người dùng
- Mã OTP có hiệu lực trong 5 phút

### Bước 2: Đặt Lại Mật Khẩu

**Endpoint:** `POST /auth/reset-password`

**Request Body:**
```json
{
  "email": "user@example.com",
  "otpCode": "123456",
  "newPassword": "newPassword123"
}
```

**Response (Success - 200):**
```json
{
  "status": 200,
  "message": "Đặt lại mật khẩu thành công",
  "data": "Bạn có thể đăng nhập bằng mật khẩu mới",
  "success": true
}
```

**Response (OTP không hợp lệ - 400):**
```json
{
  "status": 400,
  "message": "Mã OTP không hợp lệ hoặc đã hết hạn",
  "success": false
}
```

**Mô tả:**
- Xác thực mã OTP với email
- Nếu OTP hợp lệ, cập nhật mật khẩu mới cho người dùng
- Mật khẩu được mã hóa bằng BCrypt trước khi lưu
- Sau khi đặt lại thành công, người dùng có thể đăng nhập bằng mật khẩu mới

## Validation Rules

### ForgotPasswordRequestDTO
- **email:** 
  - Không được để trống
  - Phải đúng định dạng email
  
### ResetPasswordRequestDTO
- **email:** 
  - Không được để trống
  - Phải đúng định dạng email
- **otpCode:** 
  - Không được để trống
  - Phải có đúng 6 ký tự
- **newPassword:** 
  - Không được để trống
  - Tối thiểu 6 ký tự

## Email Template

### Email Quên Mật Khẩu
- **Subject:** Đặt lại mật khẩu - MindMap
- **Nội dung:** HTML template với mã OTP được hiển thị rõ ràng
- **Thời gian hết hạn:** 5 phút

## Ví Dụ Sử Dụng

### Sử dụng cURL

#### 1. Yêu cầu đặt lại mật khẩu
```bash
curl -X POST http://localhost:8085/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

#### 2. Đặt lại mật khẩu với OTP
```bash
curl -X POST http://localhost:8085/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "otpCode": "123456",
    "newPassword": "newPassword123"
  }'
```

### Sử dụng Postman hoặc Swagger

1. Truy cập Swagger UI tại: `http://localhost:8085/swagger-ui.html`
2. Tìm mục **Authentication**
3. Sử dụng endpoint **POST /auth/forgot-password**
4. Nhập email và gửi request
5. Kiểm tra email để lấy mã OTP
6. Sử dụng endpoint **POST /auth/reset-password**
7. Nhập email, OTP và mật khẩu mới

## Lưu Ý Bảo Mật

1. **Mã OTP:**
   - Chỉ có hiệu lực trong 5 phút
   - Mỗi OTP chỉ sử dụng được 1 lần
   - OTP được lưu trong database với trạng thái `used`

2. **Mật khẩu:**
   - Được mã hóa bằng BCrypt với salt tự động
   - Không bao giờ lưu mật khẩu dạng plain text

3. **Email:**
   - Kiểm tra email có tồn tại trước khi gửi OTP
   - Tránh để lộ thông tin về việc email có tồn tại hay không (có thể điều chỉnh message)

## Các Lỗi Thường Gặp

1. **Email không tồn tại:**
   - Kiểm tra lại email đã đăng ký
   - Đảm bảo email chính xác

2. **OTP không hợp lệ:**
   - Kiểm tra lại mã OTP từ email
   - Mã OTP có thể đã hết hạn (5 phút)
   - Mã OTP đã được sử dụng

3. **Lỗi gửi email:**
   - Kiểm tra cấu hình SMTP trong `application.properties`
   - Kiểm tra kết nối internet
   - Xác minh thông tin đăng nhập email server

## Cấu Hình Email

Đảm bảo các cấu hình sau trong `application.properties` hoặc `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Testing

### Unit Tests
Tạo test cases cho:
- `forgotPassword()` method
- `resetPassword()` method
- Validation rules
- Email service

### Integration Tests
Test toàn bộ flow:
1. Gửi request forgot password
2. Verify OTP được tạo và gửi email
3. Gửi request reset password với OTP
4. Verify mật khẩu được cập nhật
5. Đăng nhập với mật khẩu mới

## API Documentation

Tất cả các endpoints được document đầy đủ với Swagger/OpenAPI annotations:
- Mô tả chi tiết về request/response
- Ví dụ về payload
- HTTP status codes
- Error messages

Truy cập Swagger UI để xem chi tiết: `http://localhost:8085/swagger-ui.html`

