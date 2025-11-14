# MSS301 Backend - Microservices Architecture

## 📋 Project Overview

Hệ thống backend cho ứng dụng học tập MSS301, sử dụng kiến trúc microservices với Spring Boot và Spring Cloud.

## 🏗️ Architecture

```
MSS301/
├── eureka-server/          # Service Discovery
├── gateway/                # API Gateway (Port 8080)
├── identity-service/       # Authentication & User Management (Port 8085)
├── admin-service/          # Admin Management (Port 8810)
├── quiz-service/           # Quiz Management
├── document-service/       # Document Management
├── ownership-service/      # Ownership Management
├── premium-service/        # Premium Features
├── mindmapservice/         # Mind Map Service
├── ai-service/             # AI Features
└── vector-service/         # Vector Processing
```

## 🚀 Recent Updates (2025-11-12)

### ✨ Register with OTP - Role Selection Feature

**What's New:**
- ✅ Users can now choose **STUDENT** or **TEACHER** role during registration with OTP
- ✅ Backend validation prevents privilege escalation (ADMIN/SUPER_ADMIN)
- ✅ Complete API documentation with Swagger
- ✅ Frontend examples (React & Vue)
- ✅ Comprehensive test scripts

**Documentation:**
- 📄 [REGISTER_WITH_OTP_GUIDE.md](./identity-service/REGISTER_WITH_OTP_GUIDE.md) - Complete guide for frontend developers
- 📄 [REGISTER_OTP_IMPLEMENTATION_SUMMARY.md](./identity-service/REGISTER_OTP_IMPLEMENTATION_SUMMARY.md) - Implementation summary
- 📄 [REGISTRATION_WITH_ROLE_UPDATE.md](./REGISTRATION_WITH_ROLE_UPDATE.md) - Both registration flows

**Test Scripts:**
- 🧪 [test-register-with-otp-role.bat](./identity-service/test-register-with-otp-role.bat)

## 🔑 Key Features

### Identity Service (Port 8085)
- ✅ User Registration (Direct & OTP)
- ✅ Role Selection (STUDENT/TEACHER)
- ✅ Login with JWT
- ✅ Forgot Password with OTP
- ✅ User Profile Management
- ✅ Token Introspection
- ✅ Role Management

### Admin Service (Port 8810)
- ✅ User Management (List, Update, Delete)
- ✅ Role Management
- ✅ Dashboard Statistics
- ✅ System Health Check
- ✅ CORS Configuration

### API Gateway (Port 8080)
- ✅ Single entry point for all services
- ✅ Load balancing with Eureka
- ✅ CORS handling
- ✅ Swagger UI integration
- ✅ Route management

## 🔐 Security

### Authentication Flow:
```
1. User registers with role selection (STUDENT/TEACHER)
2. Email verification via OTP
3. Login with email/password
4. Receive JWT token
5. Access protected APIs with token
```

### Authorization:
- **Public APIs**: Registration, Login, Forgot Password
- **User APIs**: Profile, Change Password
- **Admin APIs**: User Management, Role Assignment
- **Protected**: Quiz, Document, Premium features

## 📡 API Endpoints

### Gateway (http://localhost:8080)
```
GET  /api/v1/identity/**    → Identity Service
GET  /api/v1/admin/**       → Admin Service
GET  /api/v1/quiz/**        → Quiz Service
GET  /api/v1/document/**    → Document Service
...
```

### Identity Service APIs
```
POST /api/v1/identity/auth/register-with-otp  - Register with OTP & Role
POST /api/v1/identity/auth/verify-otp         - Verify OTP
POST /api/v1/identity/auth/login              - Login
POST /api/v1/identity/auth/forgot-password    - Forgot Password
POST /api/v1/identity/auth/reset-password     - Reset Password
GET  /api/v1/identity/users/profile           - Get Profile
PUT  /api/v1/identity/users/profile           - Update Profile
POST /api/v1/identity/users/change-password   - Change Password
GET  /api/v1/identity/auth/role               - Get All Roles
```

### Admin Service APIs
```
GET    /api/v1/admin/users              - Get All Users
GET    /api/v1/admin/users/{id}         - Get User by ID
PUT    /api/v1/admin/users/{id}         - Update User
DELETE /api/v1/admin/users/{id}         - Delete User
GET    /api/v1/admin/roles              - Get All Roles
GET    /api/v1/admin/stats              - Get Dashboard Stats
GET    /api/v1/admin/health             - Health Check
```

## 🛠️ Tech Stack

- **Java**: 17+
- **Spring Boot**: 3.5.7
- **Spring Cloud**: 2024.0.0
- **Database**: PostgreSQL (Supabase)
- **Authentication**: JWT
- **API Gateway**: Spring Cloud Gateway
- **Service Discovery**: Netflix Eureka
- **Email**: JavaMailSender (Gmail SMTP)
- **Documentation**: Swagger/OpenAPI 3

## 📦 Database

### Supabase PostgreSQL:
- **Transaction Mode** (Port 6543) - Production
- **Session Mode** (Port 5432) - Development

### Default Roles:
1. **STUDENT** - Default user role
2. **TEACHER** - Educator role
3. **ADMIN** - Administrator
4. **SUPER_ADMIN** - Super Administrator

## 🚀 Getting Started

### Prerequisites:
```bash
- Java 17+
- Maven 3.8+
- PostgreSQL (or Supabase account)
- Git
```

### 1. Clone Repository:
```bash
git clone <your-repo-url>
cd MSS301
```

### 2. Configure Database:
Update `application.yml` in each service with your database credentials.

### 3. Start Eureka Server:
```bash
cd eureka-server
mvn spring-boot:run
```
Access: http://localhost:8761

### 4. Start Gateway:
```bash
cd gateway
mvn spring-boot:run
```
Access: http://localhost:8080

### 5. Start Identity Service:
```bash
cd identity-service
mvn spring-boot:run
```
Access: http://localhost:8085

### 6. Start Admin Service:
```bash
cd admin-service
mvn spring-boot:run
```
Access: http://localhost:8810

### 7. Access Swagger UI:
```
Gateway Swagger: http://localhost:8080/swagger-ui.html
Identity Service: http://localhost:8085/swagger-ui.html
Admin Service: http://localhost:8810/swagger-ui.html
```

## 🧪 Testing

### Test Register with OTP:
```bash
cd identity-service
test-register-with-otp-role.bat
```

### Test Admin APIs:
```bash
cd admin-service
# Use Swagger UI at http://localhost:8080/swagger-ui.html
```

## 📚 Documentation

### Main Guides:
- [Register with OTP Guide](./identity-service/REGISTER_WITH_OTP_GUIDE.md)
- [Registration with Role](./REGISTRATION_WITH_ROLE_UPDATE.md)
- [Admin Management](./ADMIN_MANAGEMENT_GUIDE.md)
- [Forgot Password](./identity-service/FORGOT_PASSWORD_COMPLETE_GUIDE.md)
- [User Profile API](./identity-service/USER_PROFILE_API_GUIDE.md)
- [CORS Fix](./CORS_FIX_COMPLETE.md)

### Technical Guides:
- [Gateway Configuration](./gateway/CORS_FIX_GUIDE.md)
- [Authorization Fix](./admin-service/AUTHORIZATION_FIX_GUIDE.md)
- [JWT Filter Fix](./admin-service/JWT_FILTER_FIX.md)

## 🔄 Development Workflow

### 1. Create Feature Branch:
```bash
git checkout -b feature/your-feature-name
```

### 2. Make Changes:
```bash
# Code your feature
# Update documentation
# Add tests
```

### 3. Commit Changes:
```bash
git add .
git commit -m "Add feature: your feature description"
```

### 4. Push to GitHub:
```bash
git push origin feature/your-feature-name
```

### 5. Create Pull Request:
- Go to GitHub
- Create PR from your feature branch to main
- Request review
- Merge after approval

## 🐛 Common Issues

### Issue 1: Database Connection Error
```
Solution: Check database credentials and connection mode (Transaction/Session)
```

### Issue 2: Service Not Registered in Eureka
```
Solution: 
1. Ensure Eureka Server is running
2. Check eureka.client.service-url.defaultZone
3. Restart the service
```

### Issue 3: CORS Error
```
Solution: Check CORS configuration in Gateway and individual services
```

### Issue 4: JWT Token Invalid
```
Solution: 
1. Check token expiration
2. Verify JWT_SECRET in application.yml
3. Ensure token is sent in Authorization header
```

## 📞 Support

For issues and questions:
1. Check documentation in respective service folders
2. Review Swagger API documentation
3. Check logs for detailed error messages
4. Contact development team

## 📝 License

[Your License Here]

## 👥 Contributors

[Your Team Members]

---

**Last Updated**: 2025-11-12  
**Version**: 1.1.0  
**Status**: Active Development ✅

