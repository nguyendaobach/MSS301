# JWT Token Structure Documentation

## 📋 Cấu trúc JWT Token trong Admin Service

Admin Service hiện tại đã được cập nhật để xử lý JWT token với **roles là array**.

## 🔐 JWT Token Claims Structure

### Cấu trúc Token:
```json
{
  "userId": "uuid-string",
  "email": "user@example.com",
  "roles": [
    "ROLE_ADMIN",
    "ROLE_MODERATOR"
  ],
  "permissions": ["USER_READ", "USER_WRITE", "USER_DELETE"],
  "iat": 1234567890,
  "exp": 1234567890
}
```

### Giải thích:
- **`roles`**: Array của các roles, format: `"ROLE_XXX"`
- **`permissions`**: Array của các permissions (optional)
- **Primary Role**: Hệ thống sẽ lấy role đầu tiên trong array và tự động loại bỏ prefix `ROLE_`

## 🎯 Cách Extract Roles

### 1. Extract Primary Role (Role đầu tiên)
```java
String role = jwtUtils.extractRole(token);
// Input: ["ROLE_ADMIN", "ROLE_MODERATOR"]
// Output: "ADMIN"
```

### 2. Extract All Roles
```java
List<String> roles = jwtUtils.extractRoles(token);
// Input: ["ROLE_ADMIN", "ROLE_MODERATOR"]
// Output: ["ADMIN", "MODERATOR"]
```

## 📚 API Endpoints cho Token Info

### 1. Get Complete User Info
```bash
GET /admin/auth/me
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "User information retrieved successfully",
  "data": {
    "userId": "uuid-here",
    "email": "admin@example.com",
    "role": "ADMIN",
    "roles": ["ADMIN", "MODERATOR"],
    "permissions": ["USER_READ", "USER_WRITE"]
  }
}
```

### 2. Get Primary Role
```bash
GET /admin/auth/role
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "Role extracted successfully",
  "data": {
    "role": "ADMIN"
  }
}
```

### 3. Get All Roles (NEW)
```bash
GET /admin/auth/roles
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "All roles extracted successfully",
  "data": {
    "roles": ["ADMIN", "MODERATOR"]
  }
}
```

### 4. Get Permissions
```bash
GET /admin/auth/permissions
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "Permissions extracted successfully",
  "data": {
    "permissions": ["USER_READ", "USER_WRITE", "USER_DELETE"]
  }
}
```

## 🔧 Role-Based Authorization

### Annotation @RequireRole
Admin Service sử dụng **primary role** (role đầu tiên) để kiểm tra quyền:

```java
@GetMapping("/users")
@RequireRole({"ADMIN", "SUPER_ADMIN"})
public ResponseEntity<?> getAllUsers() {
    // Only users with primary role "ADMIN" or "SUPER_ADMIN" can access
}
```

### Logic kiểm tra:
1. Extract primary role từ token (role đầu tiên)
2. Loại bỏ prefix "ROLE_" nếu có
3. Kiểm tra xem primary role có trong danh sách allowed roles không

## ⚙️ Prefix "ROLE_" Handling

Hệ thống tự động xử lý prefix "ROLE_":

| Token Value | Extracted Value | Used for Authorization |
|-------------|----------------|----------------------|
| `ROLE_ADMIN` | `ADMIN` | ✅ `ADMIN` |
| `ROLE_SUPER_ADMIN` | `SUPER_ADMIN` | ✅ `SUPER_ADMIN` |
| `ADMIN` | `ADMIN` | ✅ `ADMIN` |

**Note**: Cả hai format đều được hỗ trợ!

## 🎨 Example JWT Tokens

### Admin User:
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"],
  "permissions": ["USER_READ", "USER_WRITE", "USER_DELETE"],
  "iat": 1698765432,
  "exp": 1698851832
}
```

### Super Admin User:
```json
{
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "email": "superadmin@example.com",
  "roles": ["ROLE_SUPER_ADMIN", "ROLE_ADMIN"],
  "permissions": ["*"],
  "iat": 1698765432,
  "exp": 1698851832
}
```

### Regular User (Will be denied):
```json
{
  "userId": "770e8400-e29b-41d4-a716-446655440002",
  "email": "user@example.com",
  "roles": ["ROLE_USER"],
  "permissions": ["USER_READ"],
  "iat": 1698765432,
  "exp": 1698851832
}
```

## 🚨 Authorization Flow

```
1. User sends request with JWT token
   ↓
2. JwtAuthenticationFilter validates token
   ↓
3. Extract userId, email, primary role, permissions
   ↓
4. Set attributes in request
   ↓
5. @RequireRole annotation checks primary role
   ↓
6. If role matches → Allow access
   If role doesn't match → 403 Forbidden
```

## 📝 Testing Examples

### Using cURL:

**Get user info:**
```bash
curl -X GET "http://localhost:8087/admin/auth/me" \
  -H "Authorization: Bearer eyJhbGc..."
```

**Get all roles:**
```bash
curl -X GET "http://localhost:8087/admin/auth/roles" \
  -H "Authorization: Bearer eyJhbGc..."
```

**Call protected endpoint:**
```bash
curl -X GET "http://localhost:8087/admin/users" \
  -H "Authorization: Bearer eyJhbGc..."
```

### Expected Responses:

**Success (200):**
```json
{
  "status": 200,
  "message": "Success message",
  "data": { ... }
}
```

**Unauthorized (401) - Invalid Token:**
```json
{
  "status": 401,
  "message": "Invalid or missing token",
  "data": null
}
```

**Forbidden (403) - Insufficient Role:**
```json
{
  "status": 403,
  "message": "You don't have permission to access this resource. Required role: [ADMIN, SUPER_ADMIN]",
  "data": null
}
```

## ✅ Key Points

1. ✅ **Roles là Array**: Token chứa nhiều roles
2. ✅ **Primary Role**: Sử dụng role đầu tiên cho authorization
3. ✅ **Auto Remove Prefix**: Tự động loại bỏ "ROLE_" prefix
4. ✅ **Backward Compatible**: Hỗ trợ cả với và không có prefix
5. ✅ **Multiple Roles Support**: Có thể extract tất cả roles nếu cần

## 🔑 JWT Secret

**Important**: JWT secret phải **GIỐNG NHAU** giữa Identity Service và Admin Service!

```properties
# application.properties
app.jwt.secret=gK3jTJtK1e9dWsMLPVojKM+7E1qZ9jMUBhiAJYA6I3SjGFjdzzvIQg6Cq+vQCipm
```

