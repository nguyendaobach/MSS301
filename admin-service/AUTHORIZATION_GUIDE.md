# Admin Service - Authorization Documentation

## 📋 Tổng quan

Admin Service đã được tích hợp đầy đủ hệ thống **JWT Authentication & Authorization** để bảo mật các API quản lý người dùng.

## 🔐 Cơ chế hoạt động

### 1. **JWT Authentication Filter**
- Tất cả requests đến `/admin/*` sẽ được validate JWT token
- Token được lấy từ header: `Authorization: Bearer <token>`
- Các endpoint được bypass: `/health`, `/swagger-ui`, `/v3/api-docs`

### 2. **Role-Based Authorization (AOP)**
- Sử dụng annotation `@RequireRole` để phân quyền theo role
- Hệ thống tự động kiểm tra role của user trước khi cho phép truy cập endpoint

### 3. **Phân quyền theo Role**

| Role | Quyền hạn |
|------|-----------|
| **ADMIN** | Xem, tạo, sửa users; Bật/tắt tài khoản |
| **SUPER_ADMIN** | Toàn quyền bao gồm xóa user |

## 🚀 Cách sử dụng

### Bước 1: Login để lấy JWT Token
```bash
POST http://localhost:8085/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "your-password"
}
```

Response:
```json
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "uuid-here",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

### Bước 2: Sử dụng Token để gọi API Admin

#### Qua Gateway (Recommended)
```bash
GET http://localhost:8080/api/v1/admin/admin/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Direct đến Admin Service
```bash
GET http://localhost:8087/admin/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API Endpoints

### Authentication Info APIs

#### 1. Get Current User Info
```bash
GET /admin/auth/me
Authorization: Bearer <token>
```

Response:
```json
{
  "status": 200,
  "message": "User information retrieved successfully",
  "data": {
    "userId": "uuid",
    "email": "admin@example.com",
    "role": "ADMIN",
    "permissions": ["USER_READ", "USER_WRITE"]
  }
}
```

#### 2. Extract Role
```bash
GET /admin/auth/role
Authorization: Bearer <token>
```

#### 3. Extract Permissions
```bash
GET /admin/auth/permissions
Authorization: Bearer <token>
```

### User Management APIs (Require ADMIN or SUPER_ADMIN role)

#### 1. Get All Users
```bash
GET /admin/users
Authorization: Bearer <token>
```

#### 2. Get User by ID
```bash
GET /admin/users/{id}
Authorization: Bearer <token>
```

#### 3. Create User
```bash
POST /admin/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "New User",
  "roleId": "role-uuid"
}
```

#### 4. Update User
```bash
PUT /admin/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Updated Name",
  "roleId": "new-role-uuid",
  "status": "ACTIVE"
}
```

#### 5. Delete User (Require SUPER_ADMIN only)
```bash
DELETE /admin/users/{id}
Authorization: Bearer <token>
```

#### 6. Toggle User Status
```bash
PATCH /admin/users/{id}/toggle-status
Authorization: Bearer <token>
```

#### 7. Get Users by Role
```bash
GET /admin/users/role/{roleCode}
Authorization: Bearer <token>
```

#### 8. Get User Statistics
```bash
GET /admin/stats
Authorization: Bearer <token>
```

## 🔧 Cấu hình trong Code

### Thêm phân quyền cho endpoint mới

```java
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @GetMapping("/sensitive-data")
    @RequireRole({"SUPER_ADMIN"})  // Chỉ SUPER_ADMIN
    public ResponseEntity<?> getSensitiveData() {
        // Your code here
    }
    
    @PostMapping("/action")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})  // ADMIN hoặc SUPER_ADMIN
    public ResponseEntity<?> performAction() {
        // Your code here
    }
}
```

### Thêm phân quyền theo Permission

```java
@GetMapping("/special-feature")
@RequirePermission({"SPECIAL_FEATURE_ACCESS"})
public ResponseEntity<?> specialFeature() {
    // Your code here
}
```

## 🛠️ JWT Token Structure

Token phải chứa các claims sau:

```json
{
  "userId": "uuid-string",
  "email": "user@example.com",
  "role": "ADMIN",
  "permissions": ["USER_READ", "USER_WRITE", "USER_DELETE"],
  "iat": 1234567890,
  "exp": 1234567890
}
```

## ⚠️ Error Responses

### 401 Unauthorized - Token không hợp lệ
```json
{
  "status": 401,
  "message": "Invalid or missing token",
  "data": null
}
```

### 403 Forbidden - Không có quyền truy cập
```json
{
  "status": 403,
  "message": "You don't have permission to access this resource. Required role: [ADMIN, SUPER_ADMIN]",
  "data": null
}
```

## 📖 Swagger Documentation

Truy cập Swagger UI tại:
- Direct: http://localhost:8087/swagger-ui/index.html
- Gateway: http://localhost:8080/api/v1/admin/swagger-ui/index.html

Nhấn nút **Authorize** và nhập: `Bearer <your-token>`

## 🔑 JWT Secret Configuration

Trong `application.properties`:
```properties
app.jwt.secret=gK3jTJtK1e9dWsMLPVojKM+7E1qZ9jMUBhiAJYA6I3SjGFjdzzvIQg6Cq+vQCipm
app.jwt.expiration-ms=86400000
```

⚠️ **Lưu ý**: Secret key này phải **GIỐNG NHAU** giữa Identity Service và Admin Service!

## 📦 Dependencies

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Spring AOP for Authorization -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

## 🎯 Testing với Postman/Insomnia

1. **Lấy token từ Identity Service**
   ```
   POST http://localhost:8085/auth/login
   ```

2. **Set token vào Environment**
   - Tạo biến: `admin_token`
   - Giá trị: Token nhận được

3. **Gọi API với token**
   ```
   GET http://localhost:8087/admin/users
   Authorization: Bearer {{admin_token}}
   ```

## 🚨 Troubleshooting

### Token không hợp lệ
- Kiểm tra JWT secret có giống nhau không
- Kiểm tra token đã hết hạn chưa
- Kiểm tra format header: `Authorization: Bearer <token>`

### 403 Forbidden
- Kiểm tra role trong token có đúng không
- Kiểm tra endpoint yêu cầu role gì
- Xem log để biết role hiện tại và role yêu cầu

### Cannot resolve symbol 'RequireRole'
- Build lại project: `mvn clean compile`
- Restart IDE để refresh
- Kiểm tra file annotation đã được tạo đúng chưa

## 📝 Notes

- Service name đã được sửa từ `identity-service` thành `admin-service`
- Port: **8087**
- Database: Shared với Identity Service
- Eureka Client: Đã được đăng ký

