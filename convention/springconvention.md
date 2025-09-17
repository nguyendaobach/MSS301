# Java Spring Boot Code Convention

This document defines the coding style for this project, based on the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) and with [Spring Boot framework Code Style](https://github.com/spring-projects/spring-framework/wiki/Code-Style) practices.

---

## 1. General Code Style

- **Indentation:** 4 spaces, no tabs.  
- **Line length:** Max 120 characters.  
- **Braces:** Always use braces, even for single-line blocks. Opening brace on the same line.  
  
  ```java
  if (condition) {
    doSomething();
  }
  ```

- Naming conventions:
  - Classes & Interfaces: `PascalCase`
  - Methods & Variables: `camelCase`
  - Constants: `UPPER_CASE_WITH_UNDERSCORES`
  - Packages: `lowercase`, no underscores (`com.example.myapp`)

- Imports:
  - No wildcard imports (import java.util.* is forbidden).
  - Order: java.*, jakarta.*, then third-party libs, then org.springframework.*, then project imports.
  - Javadoc: Required for public classes and methods.

## 2. Project Structure (Spring Boot Standard)

```
com.example.myapp
├── config        // Configuration classes
├── controller    // REST controllers (API endpoints)
├── dto           // Data Transfer Objects
├── entity        // JPA entities
├── exception     // Custom exceptions & handlers
├── repository    // Spring Data repositories
├── service       // Business logic
└── util          // Utility/helper classes
```

- Application entry point:
  - Named Application or `<ProjectName>Application`.

```java
@SpringBootApplication
public class MyAppApplication {
  public static void main(String[] args) {
    SpringApplication.run(MyAppApplication.class, args);
  }
}
```

## 3. Spring Boot Specific Conventions
### Dependency Injection
- Use constructor injection (preferred).
- Avoid field injection (`@Autowired` on fields).

```java
@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}
```

### REST Controllers
- Annotate classes with `@RestController`, methods with `@GetMapping`, `@PostMapping`, etc.
- Use meaningful request mappings (`/api/v1/users`).

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }
}
```

### Interface

### DTO Usage
- Always separate Entity and DTO.
- Use MapStruct or manual mapping in a mapper class.

### Exception Handling
- Use a global exception handler with `@RestControllerAdvice`.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }
}
```

### Configuration
- Externalize config in `application.properties`.
- Use `@ConfigurationProperties` for structured config.

### Logging
- Use `Slf4j` (via Lombok) or Spring Boot default logging
- Never use `System.out.println`.

```java
@Slf4j
@Service
public class EmailService {
  public void sendEmail(String to) {
    log.info("Sending email to {}", to);
  }
}
```

### Tests
- Place tests under `src/test/java` with mirrored package structure.
- Use `@SpringBootTest` for integration tests, `@WebMvcTest` for controller tests.
- Naming convention: `<ClassName>Test`.

## 4. Environment Variables (`.env`)

- Use a `.env` file for sensitive values (e.g., DB passwords, API keys, tokens).

- Never commit `.env` to Git (add it to `.gitignore`).

- Variable names: `UPPER_CASE_WITH_UNDERSCORES`.

- Reference them in `application.properties` using Spring’s placeholder syntax.

Example `.env`:

```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mydb
DB_USER=myuser
DB_PASSWORD=secret
JWT_SECRET=supersecretkey
```

Example `application.properties` referencing `.env`:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

app.security.jwt.secret=${JWT_SECRET}
```

- In Docker/CI/CD, inject these .env variables into the container environment.

## 5. Additional Best Practices
### Database Layer
- Use JpaRepository or CrudRepository.
- Follow Spring Data method naming (findByEmail, existsByUsername).

### Validation
- Use `jakarta.validation` annotations in DTOs (`@NotNull`, `@Email`, `@Size`, ...).

### Security
- Centralize config in a SecurityConfig class.

### Properties Naming
- Use kebab-case in application.yml.

```properties
server.port=8080

app.email.sender=noreply@example.com
app.feature.enabled=true
```
