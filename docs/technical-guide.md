# Technical Guide - TaskMagnet

This comprehensive technical guide covers the architecture, technologies, and development practices used in the TaskMagnet project.

## 📋 Project Overview

TaskMagnet is a full-stack project management and issue tracking application built with modern technologies following industry best practices.

### Key Features
- User authentication and authorization (JWT-based)
- Role-based access control (Admin, Moderator, User)
- RESTful API design
- Swagger API documentation
- Automated seed data management
- Oracle database integration

---

## 🏗️ System Architecture

### Architecture Pattern
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Database     │
│   (React.js)    │◄──►│  (Spring Boot)  │◄──►│   (Oracle XE)   │
│                 │    │                 │    │                 │
│ - Redux Store   │    │ - REST APIs     │    │ - User Data     │
│ - Components    │    │ - JWT Security  │    │ - Task Data     │
│ - Routing       │    │ - JPA/Hibernate │    │ - Role Data     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Technology Stack

#### Backend Technologies
- **Java 17** - Programming language
- **Spring Boot 3.1.5** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Hibernate 6.2.13** - ORM framework
- **JWT (JJWT 0.11.5)** - Token-based authentication
- **Oracle JDBC** - Database connectivity
- **Maven** - Build and dependency management
- **Lombok** - Code generation
- **SpringDoc OpenAPI** - API documentation

#### Frontend Technologies (Planned)
- **React 18** - UI library
- **Redux** - State management
- **React Router** - Client-side routing
- **Axios** - HTTP client

#### Development Tools
- **VS Code** - IDE
- **Swagger UI** - API testing
- **Oracle SQL Developer** - Database management
- **Git** - Version control

---

## 🔧 Project Structure

### Backend Structure
```
src/backend/
├── src/main/java/com/mahedee/backend/
│   ├── BackendApplication.java           # Main application class
│   ├── configuration/
│   │   └── DataSeeder.java              # Seed data configuration
│   ├── controllers/
│   │   ├── AuthController.java          # Authentication endpoints
│   │   ├── TaskCategoryController.java  # Category management
│   │   └── TaskStatusController.java    # Status management
│   ├── models/
│   │   ├── BaseEntity.java              # Base entity class
│   │   ├── User.java                    # User entity
│   │   ├── Role.java                    # Role entity
│   │   ├── ERole.java                   # Role enumeration
│   │   ├── TaskCategory.java            # Category entity
│   │   └── TaskStatus.java              # Status entity
│   ├── payload/
│   │   ├── request/                     # Request DTOs
│   │   └── response/                    # Response DTOs
│   ├── repository/
│   │   ├── UserRepository.java          # User data access
│   │   ├── RoleRepository.java          # Role data access
│   │   ├── TaskCategoryRepository.java  # Category data access
│   │   └── TaskStatusRepository.java    # Status data access
│   └── security/
│       ├── WebSecurityConfig.java       # Security configuration
│       ├── jwt/
│       │   ├── AuthEntryPointJwt.java   # JWT entry point
│       │   ├── AuthTokenFilter.java     # JWT filter
│       │   └── JwtUtils.java            # JWT utilities
│       └── services/
│           ├── UserDetailsImpl.java     # User details implementation
│           └── UserDetailsServiceImpl.java # User details service
└── src/main/resources/
    ├── application.properties           # Main configuration
    └── application-dev.properties       # Development configuration
```

---

## 🔐 Security Implementation

### JWT Authentication Flow
```
1. User sends login credentials
2. Backend validates credentials
3. Backend generates JWT token
4. Client stores token
5. Client sends token in Authorization header
6. Backend validates token for each request
```

### Security Configuration
```java
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    // JWT token filter
    // Password encryption (BCrypt)
    // CORS configuration
    // Authorization rules
}
```

### Role-Based Access Control
- **ROLE_ADMIN**: Full system access
- **ROLE_MODERATOR**: Limited administrative access
- **ROLE_USER**: Basic user access

---

## 🗃️ Database Design

### Entity Relationships
```
Users (1) ←→ (M) UserRoles (M) ←→ (1) Roles
Users (1) ←→ (M) Tasks (M) ←→ (1) TaskCategory
Tasks (M) ←→ (1) TaskStatus
```

### Key Entities

#### User Entity
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();
}
```

#### Role Entity
```java
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ERole name;
}
```

---

## 🚀 API Design

### RESTful Endpoints

#### Authentication Endpoints
```
POST /api/auth/signin     - User login
POST /api/auth/signup     - User registration
POST /api/auth/signout    - User logout
```

#### User Management
```
GET    /api/users         - Get all users (Admin)
GET    /api/users/{id}    - Get user by ID
PUT    /api/users/{id}    - Update user
DELETE /api/users/{id}    - Delete user (Admin)
```

#### Task Management (Future)
```
GET    /api/tasks         - Get all tasks
POST   /api/tasks         - Create task
GET    /api/tasks/{id}    - Get task by ID
PUT    /api/tasks/{id}    - Update task
DELETE /api/tasks/{id}    - Delete task
```

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

---

## 🌱 Data Seeding Strategy

### Automatic Seed Data
The `DataSeeder` class implements `CommandLineRunner` to insert initial data:

```java
@Component
public class DataSeeder implements CommandLineRunner {
    // Seeds roles and admin user on application startup
    // Checks for existing data before insertion
    // Provides comprehensive logging
}
```

### Seed Data Includes
- Default roles (USER, MODERATOR, ADMIN)
- Admin user (username: admin, password: Taskmagnet@2025)
- Configurable additional test users

---

## 🧪 Testing Strategy

### Testing Layers
1. **Unit Tests** - Individual component testing
2. **Integration Tests** - API endpoint testing
3. **Security Tests** - Authentication/authorization testing
4. **Performance Tests** - Load and stress testing

### Testing Tools
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing
- **TestContainers** - Database testing (future)

---

## 📊 Monitoring and Logging

### Logging Configuration
```properties
# Application logging
logging.level.com.mahedee=DEBUG
logging.level.org.springframework.security=DEBUG

# SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Monitoring Points
- Application startup and shutdown
- Authentication attempts
- Database operations
- API request/response cycles
- Error occurrences

---

## 🔧 Development Workflow

### Git Workflow
```bash
# Feature development
git checkout -b feature/task-management
# ... make changes ...
git add .
git commit -m "feat: add task CRUD operations"
git push origin feature/task-management
# ... create pull request ...
```

### Build Process
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

---

## 🚦 Configuration Management

### Profile-Based Configuration
- **Default Profile**: Production-ready settings
- **Dev Profile**: Development-specific settings
- **Test Profile**: Testing configurations

### Environment Variables
```properties
# Database configuration
ORACLE_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
ORACLE_USERNAME=taskmagnet
ORACLE_PASSWORD=mahedee.net

# JWT configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

---

## 🔒 Security Best Practices

### Authentication Security
- Strong password policies
- JWT token expiration
- Secure password hashing (BCrypt)
- Rate limiting (future implementation)

### Authorization Security
- Role-based access control
- Method-level security
- Resource-based authorization
- CORS configuration

### Data Security
- Input validation
- SQL injection prevention
- XSS protection
- HTTPS enforcement (production)

---

## 📈 Performance Considerations

### Database Optimization
- Proper indexing on frequently queried columns
- Connection pooling (HikariCP)
- Query optimization
- Lazy loading for relationships

### Application Performance
- Efficient JWT token handling
- Caching strategies (future)
- Pagination for large datasets
- Asynchronous processing (future)

---

## 🐛 Error Handling

### Exception Handling Strategy
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // Handles validation errors
    // Handles authentication errors
    // Handles authorization errors
    // Provides consistent error responses
}
```

### Error Response Format
```json
{
    "timestamp": "2025-08-03T10:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/users"
}
```

---

## 🚀 Deployment Strategy

### Development Deployment
- Local Oracle XE database
- Spring Boot embedded Tomcat
- Hot reload with Spring Boot DevTools

### Production Deployment (Future)
- Docker containerization
- Oracle Cloud/AWS RDS
- Load balancing
- CI/CD pipeline integration

---

## 📚 Code Quality Standards

### Coding Standards
- Java naming conventions
- Consistent code formatting
- Comprehensive JavaDoc comments
- SOLID principles adherence

### Code Review Guidelines
- Security vulnerability checks
- Performance impact assessment
- Test coverage requirements
- Documentation updates

---

## 🔮 Future Enhancements

### Planned Features
- Frontend React application
- Real-time notifications
- File attachment system
- Advanced reporting
- Mobile responsiveness
- Microservices architecture

### Technical Improvements
- Redis caching layer
- Elasticsearch integration
- Event-driven architecture
- GraphQL API support
- Advanced monitoring (Micrometer/Prometheus)

---

## 📚 Additional Resources

### Documentation
- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Oracle Database Documentation](https://docs.oracle.com/en/database/)

### Learning Resources
- [Spring Framework Tutorials](https://spring.io/guides)
- [JWT.io Documentation](https://jwt.io/introduction)
- [Oracle SQL Tutorial](https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/)

---

*Last Updated: August 3, 2025*
*Version: 1.0*
