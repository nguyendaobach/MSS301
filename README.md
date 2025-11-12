# MSS301 - Microservices System

## Tổng quan hệ thống

Hệ thống MSS301 là một ứng dụng microservices bao gồm các service sau:

### Core Services
1. **Eureka Server** (port 8761) - Service Discovery
2. **Gateway** (port 8080) - API Gateway
3. **Identity Service** (port 8085) - Authentication & User Management
4. **Admin Service** (port 8810) - Admin Management

### Business Services
5. **AI Service** - AI Processing
6. **Document Service** - Document Management
7. **Mindmap Service** - Mindmap Management
8. **Quiz Service** - Quiz Management
9. **Ownership Service** - Ownership Management
10. **Premium Service** - Premium Features
11. **Vector Service** - Vector Processing

---

## Quick Start

### 1. Khởi động các service

#### Bắt đầu với Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Truy cập: http://localhost:8761

#### Khởi động Gateway
```bash
cd gateway
mvn spring-boot:run
```

#### Khởi động Identity Service
```bash
cd identity-service
mvn spring-boot:run
```

#### Khởi động Admin Service
```bash
cd admin-service
mvn spring-boot:run
```

### 2. Kiểm tra các service đã đăng ký

Truy cập Eureka Dashboard: http://localhost:8761

Bạn sẽ thấy các service đã đăng ký:
- IDENTITY-SERVICE
- ADMIN-SERVICE
- GATEWAY-SERVICE
- (và các service khác nếu đã khởi động)

---

## API Documentation

### Swagger UI

Sau khi khởi động các service, truy cập Swagger UI:

- **Gateway Swagger:** http://localhost:8080/swagger-ui.html
- **Identity Service:** http://localhost:8085/swagger-ui.html
- **Admin Service:** http://localhost:8810/swagger-ui.html

### API Prefix qua Gateway

Tất cả requests nên đi qua Gateway với prefix:

```
http://localhost:8080/api/v1/{service-name}/{endpoint}
```

Ví dụ:
- Identity: `http://localhost:8080/api/v1/identity/auth/login`
- Admin: `http://localhost:8080/api/v1/admin/users`

---

## Hướng dẫn sử dụng

### Cho Developers

1. **Setup Development Environment**
   - JDK 17+
   - Maven 3.8+
   - PostgreSQL (hoặc sử dụng Supabase)
   - IDE (IntelliJ IDEA hoặc Eclipse)

2. **Clone & Build**
   ```bash
   git clone <repository-url>
   cd MSS301
   mvn clean install -DskipTests
   ```

3. **Configuration**
   - Cấu hình database trong `application.properties` của mỗi service
   - Cấu hình JWT secret trong identity-service
   - Cấu hình email trong identity-service (cho OTP)

### Cho End Users

📖 **[User Profile Management Guide](identity-service/USER_PROFILE_API_GUIDE.md)**

Hướng dẫn cho người dùng về:
- Đăng nhập/Đăng ký
- Xem và cập nhật thông tin cá nhân
- Đổi mật khẩu
- Quên mật khẩu

### Cho Admins

📖 **[Admin Management Guide](ADMIN_MANAGEMENT_GUIDE.md)**

Hướng dẫn chi tiết cho admin về:
- Quản lý users
- Phân quyền
- Thống kê
- Best practices
- Frontend integration examples

---

## Roles & Permissions

### ADMIN
- ✅ Quản lý tất cả users
- ✅ Thay đổi roles
- ✅ Vô hiệu hóa/Kích hoạt users
- ✅ Xem thống kê hệ thống
- ✅ Quản lý tất cả resources

### TEACHER
- ✅ Tạo và quản lý tài liệu của mình
- ✅ Tạo quiz
- ✅ Xem danh sách students
- ❌ Không thể quản lý users

### STUDENT
- ✅ Xem tài liệu được chia sẻ
- ✅ Làm quiz
- ✅ Xem và cập nhật profile
- ❌ Không thể tạo tài liệu
- ❌ Không thể xem danh sách users

---

## Architecture

### Microservices Pattern
```
Client
  ↓
Gateway (8080)
  ↓
├── Identity Service (8085) - Authentication, User Management
├── Admin Service (8810) - Admin Operations
├── Document Service - Documents
├── Mindmap Service - Mindmaps
├── Quiz Service - Quizzes
└── Other Services...
  ↓
Eureka Server (8761) - Service Discovery
```

### Security Flow
```
1. User Login → Identity Service
2. Receive JWT Token
3. Include Token in Header: "Authorization: Bearer <token>"
4. Gateway validates token
5. Forward to appropriate service
6. Service checks permissions
7. Return response
```

---

## Database

### Identity Service Database

**Users Table:**
- id (UUID, Primary Key)
- email (unique)
- password (hashed)
- full_name
- status (ACTIVE, INACTIVE, BANNED)
- role_id (Foreign Key)
- created_at
- updated_at

**Roles Table:**
- id (UUID, Primary Key)
- code (ADMIN, TEACHER, STUDENT)
- name
- description

**Permissions Table:**
- id (UUID, Primary Key)
- code
- name
- description

### Connection

**Supabase PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.cvfvfeaxgyfpsicohtgq&password=bach129052004&prepareThreshold=0&preferQueryMode=simple
```

**Sử dụng Transaction mode (port 6543) thay vì Session mode (port 5432) để tránh lỗi connection pool.**

---

## Common Issues & Solutions

### 1. Service không đăng ký với Eureka

**Nguyên nhân:** Eureka Server chưa khởi động hoặc config sai

**Giải pháp:**
```bash
# Kiểm tra Eureka đã chạy chưa
curl http://localhost:8761

# Kiểm tra config trong application.properties
eureka.client.service-url.defaultZone=http://eureka:123456@localhost:8761/eureka
```

### 2. Lỗi 401 Unauthorized

**Nguyên nhân:** Token không hợp lệ hoặc đã hết hạn

**Giải pháp:**
- Login lại để lấy token mới
- Kiểm tra header `Authorization: Bearer <token>`
- Kiểm tra token chưa hết hạn (default 10 ngày)

### 3. Lỗi Database Connection

**Nguyên nhân:** Connection pool bị đầy hoặc prepared statement conflict

**Giải pháp:**
```properties
# Sử dụng Transaction mode
spring.datasource.url=jdbc:postgresql://...?prepareThreshold=0&preferQueryMode=simple

# Tăng connection pool
spring.datasource.hikari.maximum-pool-size=10
```

### 4. CORS Error

**Nguyên nhân:** Frontend và Backend khác origin

**Giải pháp:** 
- Gateway đã config CORS cho phép tất cả origins trong development
- Production cần config cụ thể trong `CorsConfig`

### 5. Gateway timeout

**Nguyên nhân:** Service không phản hồi kịp

**Giải pháp:**
```yaml
# Tăng timeout trong gateway config
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 10000
        response-timeout: 60s
```

---

## Development Guidelines

### Code Convention

Tham khảo:
- [Spring Convention](convention/springconvention.md)
- [React Convention](convention/reactconvention.md)

### Git Workflow

```bash
# 1. Create feature branch
git checkout -b feature/your-feature-name

# 2. Make changes and commit
git add .
git commit -m "feat: add new feature"

# 3. Push to remote
git push origin feature/your-feature-name

# 4. Create Pull Request
```

### Commit Message Format

```
<type>: <subject>

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Formatting
- refactor: Code restructuring
- test: Adding tests
- chore: Maintenance
```

---

## Testing

### Unit Testing
```bash
cd <service-name>
mvn test
```

### Integration Testing
```bash
mvn verify
```

### API Testing with cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"Admin@123"}'
```

**Get Users (Admin):**
```bash
curl -X GET http://localhost:8080/api/v1/admin/users \
  -H "Authorization: Bearer <your-token>"
```

---

## Deployment

### Docker (Recommended)

```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Manual Deployment

```bash
# Build JAR files
mvn clean package -DskipTests

# Run services
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar
java -jar gateway/target/gateway-0.0.1-SNAPSHOT.jar
java -jar identity-service/target/identity-service-0.0.1-SNAPSHOT.jar
java -jar admin-service/target/admin-service-0.0.1-SNAPSHOT.jar
```

---

## Monitoring

### Eureka Dashboard
http://localhost:8761

### Actuator Endpoints (if enabled)
```
http://localhost:8085/actuator/health
http://localhost:8085/actuator/info
```

---

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## License

[Add your license here]

---

## Contact & Support

- **Developer:** [Your Name]
- **Email:** [Your Email]
- **Documentation:** Check the guides in this repository

---

## Changelog

### Version 1.0.0 (2025-01-11)
- ✅ Initial release
- ✅ Identity Service with authentication
- ✅ Admin Service for user management
- ✅ User profile management APIs
- ✅ Gateway with routing
- ✅ Eureka service discovery
- ✅ Swagger documentation
- ✅ Comprehensive admin guide
- ✅ User guide for profile management

---

**Happy Coding! 🚀**

