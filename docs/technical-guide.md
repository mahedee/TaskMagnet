# Technical Guide - TaskMagnet
**Enterprise-Grade Modular Monolith Architecture**

This comprehensive technical guide covers the modular monolith architecture, technologies, and development practices used in the TaskMagnet project with enterprise-grade security and scalability features.

## 📋 Project Overview

TaskMagnet is a comprehensive enterprise project management system built with a **modular monolith architecture** that provides enterprise-grade functionality while maintaining simplicity and future scalability. The system implements robust security, efficient data management, and modern development practices.

### Key Architectural Features
- **Modular Monolith**: Single deployable unit with well-defined module boundaries
- **Microservices-Ready**: Architecture designed for future microservices migration
- **Security-First**: Comprehensive authentication and authorization framework
- **Enterprise Security**: Advanced JWT with Redis session management
- **Performance Optimized**: Multi-level caching and database optimization
- **Observable**: Comprehensive monitoring and audit logging
- **Oracle Integration**: Enterprise database with ACID compliance

---

## 🏗️ Modular Monolith Architecture

### Architecture Pattern Overview
The system follows a **modular monolith** approach that combines the simplicity of monolithic deployment with the organizational benefits of microservices architecture.

```
TaskMagnet Modular Monolith
┌─────────────────────────────────────────────────────────────────┐
│                     API Gateway Module                         │
├─────────────────────────────────────────────────────────────────┤
│ Security Module │ User Mgmt │ Project Mgmt │ Task Mgmt │ Reports │
├─────────────────┼───────────┼──────────────┼───────────┼─────────┤
│              Notification Module                               │
├─────────────────────────────────────────────────────────────────┤
│                    Core Module (Shared)                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │   Oracle Database │
                    │   + Redis Cache   │
                    └───────────────────┘
```

### Module Architecture

#### Core Module (Shared Infrastructure)
- **Configuration Management**: Environment-specific settings and profiles
- **Common Utilities**: Shared helper classes and utilities
- **Exception Handling**: Centralized error management and responses
- **Cross-cutting Concerns**: Logging, monitoring, and audit trails
- **Shared DTOs**: Common data transfer objects and response models

#### Security Module (Authentication/Authorization)
- **JWT Management**: Token generation, validation, and refresh
- **Authentication Services**: User login, logout, and session management
- **Authorization Engine**: Role-based access control with granular permissions
- **Security Filters**: Request interception and security validation
- **Audit Logging**: Security event tracking and compliance

#### User Management Module
- **User Lifecycle**: Registration, profile management, and deactivation
- **Organization Management**: Multi-tenant organization support
- **Team Management**: Team creation and member assignments
- **User Preferences**: Personalization and configuration settings

#### Project Management Module
- **Project Lifecycle**: Creation, configuration, and archival
- **Project Templates**: Reusable project structures and workflows
- **Resource Planning**: Allocation and capacity management
- **Project Analytics**: Reporting and performance metrics

#### Task Management Module
- **Task Operations**: CRUD operations with lifecycle management
- **Assignment Engine**: Task assignment with permission validation
- **Dependency Management**: Task relationships and scheduling
- **Category System**: Task organization with hierarchical categories

#### Notification Module
- **Event Processing**: Real-time event handling and routing
- **Multi-channel Delivery**: Email, in-app, and push notifications
- **Template Management**: Notification templates and personalization
- **Delivery Tracking**: Status monitoring and retry mechanisms

#### Reporting Module
- **Dashboard Engine**: Dynamic dashboard generation
- **Report Builder**: Custom report creation and scheduling
- **Data Visualization**: Charts, graphs, and analytics
- **Export Services**: Multiple format export capabilities

### Enhanced Technology Stack

#### Backend Technologies (Enterprise Stack)
- **Java 17** - Programming language with modern features and LTS support
- **Spring Boot 3.1.5+** - Enterprise application framework with auto-configuration
- **Spring Security 6.x** - Comprehensive authentication & authorization framework
- **Spring Data JPA** - Data persistence with advanced querying capabilities
- **Hibernate 6.2.13+** - ORM framework with Oracle optimizations
- **JWT (JJWT 0.11.5)** - Token-based authentication with refresh token support
- **Redis** - Session management and high-performance caching
- **Oracle XE 21c** - Enterprise database with ACID compliance
- **HikariCP** - High-performance JDBC connection pooling
- **Maven** - Build and dependency management with multi-module support
- **Lombok** - Code generation and boilerplate reduction
- **SpringDoc OpenAPI** - API documentation and interactive testing
- **Micrometer** - Application metrics and monitoring integration

#### Infrastructure Components
- **Embedded Tomcat** - Application server with production tuning
- **SLF4J + Logback** - Structured logging with JSON output
- **Spring Boot Actuator** - Health checks and operational endpoints
- **Jackson** - JSON serialization/deserialization
- **Validation API (JSR-303)** - Input validation and sanitization
- **Spring Profiles** - Environment-specific configuration management

#### Frontend Technologies (Planned)
- **React 18** - Modern UI library with hooks
- **Redux Toolkit** - State management with RTK Query
- **React Router 6** - Client-side routing with guards
- **Axios** - HTTP client with interceptors
- **Material-UI** - Component library
- **React Hook Form** - Form handling and validation

#### Development & Security Tools
- **VS Code** - IDE with extensions
- **Swagger UI** - Interactive API documentation
- **Oracle SQL Developer** - Database management
- **Git** - Version control with conventional commits
- **SonarQube** - Code quality analysis
- **OWASP ZAP** - Security vulnerability scanning
- **JaCoCo** - Code coverage reporting
- **Axios** - HTTP client

#### Development Tools
- **VS Code** - IDE
- **Swagger UI** - API testing
- **Oracle SQL Developer** - Database management
- **Git** - Version control

---

## 🔧 Modular Monolith Project Structure

### Backend Structure (Module-Based Organization)
```
src/backend/
├── src/main/java/com/mahedee/backend/
│   ├── BackendApplication.java           # Main application class
│   │
│   ├── core/                            # Core Module (Shared)
│   │   ├── config/
│   │   │   ├── DataSeeder.java          # Database initialization
│   │   │   ├── SwaggerOpenApiConfig.java # API documentation
│   │   │   ├── CacheConfig.java         # Redis cache configuration
│   │   │   └── DatabaseConfig.java      # Database configuration
│   │   ├── dto/
│   │   │   ├── BaseResponse.java        # Standard response wrapper
│   │   │   ├── PagedResponse.java       # Pagination wrapper
│   │   │   └── ApiError.java            # Error response structure
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java # Centralized error handling
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── BusinessLogicException.java
│   │   │   └── SecurityException.java
│   │   └── util/
│   │       ├── DateUtil.java            # Date/time utilities
│   │       ├── SecurityUtil.java        # Security helper methods
│   │       └── ValidationUtil.java      # Validation utilities
│   │
│   ├── security/                        # Security Module
│   │   ├── config/
│   │   │   ├── WebSecurityConfig.java   # Main security configuration
│   │   │   ├── MethodSecurityConfig.java # Method-level security
│   │   │   └── PasswordConfig.java      # Password policies
│   │   ├── controllers/
│   │   │   └── AuthController.java      # Authentication endpoints
│   │   ├── services/
│   │   │   ├── AuthService.java         # Authentication logic
│   │   │   ├── JwtService.java          # JWT token management
│   │   │   ├── RefreshTokenService.java # Token refresh logic
│   │   │   └── AuditService.java        # Security audit logging
│   │   ├── filters/
│   │   │   ├── JwtAuthenticationFilter.java # JWT validation filter
│   │   │   └── RateLimitingFilter.java  # Rate limiting protection
│   │   └── models/
│   │       ├── User.java                # User entity
│   │       ├── Role.java                # Role entity
│   │       ├── Permission.java          # Permission entity
│   │       ├── RefreshToken.java        # Refresh token entity
│   │       └── SecurityAuditLog.java    # Audit log entity
│   │
│   ├── usermanagement/                  # User Management Module
│   │   ├── controllers/
│   │   │   ├── UserController.java      # User CRUD operations
│   │   │   ├── RoleController.java      # Role management
│   │   │   └── PermissionController.java # Permission management
│   │   ├── services/
│   │   │   ├── UserService.java         # User business logic
│   │   │   ├── RoleService.java         # Role business logic
│   │   │   └── PermissionService.java   # Permission business logic
│   │   └── repository/
│   │       ├── UserRepository.java      # User data access
│   │       ├── RoleRepository.java      # Role data access
│   │       └── PermissionRepository.java # Permission data access
│   │
│   ├── projectmanagement/               # Project Management Module
│   │   ├── controllers/
│   │   │   └── ProjectController.java   # Project operations
│   │   ├── services/
│   │   │   ├── ProjectService.java      # Project business logic
│   │   │   └── ProjectTemplateService.java # Template management
│   │   ├── models/
│   │   │   ├── Project.java             # Project entity
│   │   │   └── ProjectTemplate.java     # Project template entity
│   │   └── repository/
│   │       └── ProjectRepository.java   # Project data access
│   │
│   ├── taskmanagement/                  # Task Management Module
│   │   ├── controllers/
│   │   │   ├── TaskController.java      # Task operations
│   │   │   ├── TaskCategoryController.java # Category management
│   │   │   └── TaskStatusController.java # Status management
│   │   ├── services/
│   │   │   ├── TaskService.java         # Task business logic
│   │   │   ├── TaskAssignmentService.java # Assignment logic
│   │   │   └── TaskWorkflowService.java # Workflow management
│   │   ├── models/
│   │   │   ├── Task.java                # Task entity
│   │   │   ├── TaskCategory.java        # Category entity
│   │   │   ├── TaskStatus.java          # Status entity
│   │   │   └── TaskDependency.java      # Task dependency entity
│   │   └── repository/
│   │       ├── TaskRepository.java      # Task data access
│   │       ├── TaskCategoryRepository.java
│   │       └── TaskStatusRepository.java
│   │
│   ├── notification/                    # Notification Module
│   │   ├── controllers/
│   │   │   └── NotificationController.java
│   │   ├── services/
│   │   │   ├── NotificationService.java # Notification orchestration
│   │   │   ├── EmailService.java        # Email notifications
│   │   │   └── InAppNotificationService.java
│   │   ├── models/
│   │   │   ├── Notification.java        # Notification entity
│   │   │   └── NotificationTemplate.java
│   │   └── events/
│   │       ├── TaskAssignedEvent.java   # Event definitions
│   │       └── ProjectCreatedEvent.java
│   │
│   └── reporting/                       # Reporting Module
│       ├── controllers/
│       │   └── ReportController.java    # Report endpoints
│       ├── services/
│       │   ├── DashboardService.java    # Dashboard generation
│       │   ├── ReportService.java       # Report generation
│       │   └── AnalyticsService.java    # Analytics processing
│       └── models/
│           └── ReportDefinition.java    # Report configuration
│
├── src/main/resources/
│   ├── application.properties           # Main configuration
│   ├── application-dev.properties       # Development profile
│   ├── application-prod.properties      # Production profile
│   ├── application-test.properties      # Test profile
│   └── db/migration/                    # Database migration scripts
│       ├── V1__Create_security_tables.sql
│       ├── V2__Create_business_tables.sql
│       └── V3__Insert_initial_data.sql
│
└── src/test/java/                       # Test structure mirrors main
    ├── integration/                     # Integration tests
    ├── unit/                           # Unit tests
    └── security/                       # Security-specific tests
```
│   │   ├── jwt/
│   │   │   ├── AuthEntryPointJwt.java   # JWT authentication entry point
│   │   │   ├── AuthTokenFilter.java     # JWT request filter
│   │   │   └── JwtUtils.java            # Enhanced JWT utilities
│   │   ├── services/
│   │   │   ├── UserDetailsImpl.java     # User details implementation
│   │   │   ├── UserDetailsServiceImpl.java # User details service
│   │   │   ├── PermissionService.java   # Permission evaluation service
│   │   │   ├── SecurityAuditService.java # Security audit service
│   │   │   ├── RefreshTokenService.java # Refresh token service
│   │   │   ├── PasswordService.java     # Password management service
│   │   │   └── SessionService.java      # Session management service
│   │   └── annotations/
│   │       ├── RequiresPermission.java  # Custom permission annotation
│   │       └── AuditAction.java         # Audit logging annotation
│   └── services/
│       ├── UserService.java             # User business logic
│       ├── RoleService.java             # Role business logic
│       ├── TaskService.java             # Task business logic
│       ├── ProjectService.java          # Project business logic
│       └── NotificationService.java     # Notification service
└── src/main/resources/
    ├── application.properties           # Main configuration
    ├── application-dev.properties       # Development configuration
    ├── application-prod.properties      # Production configuration
    └── data/
        ├── default-roles.json           # Default role definitions
        ├── default-permissions.json     # Default permission definitions
        └── role-permission-mappings.json # Default role-permission mappings
```

### Frontend Structure (Planned)
```
src/frontend/
├── public/
├── src/
│   ├── components/
│   │   ├── auth/
│   │   │   ├── LoginForm.jsx           # Login component
│   │   │   ├── SignUpForm.jsx          # Registration component
│   │   │   ├── ForgotPassword.jsx      # Password recovery
│   │   │   └── ChangePassword.jsx      # Password change
│   │   ├── security/
│   │   │   ├── PermissionButton.jsx    # Permission-aware button
│   │   │   ├── PermissionRoute.jsx     # Protected route
│   │   │   ├── RoleGuard.jsx           # Role-based guard
│   │   │   └── PermissionProvider.jsx  # Permission context
│   │   ├── tasks/
│   │   │   ├── TaskList.jsx            # Task listing
│   │   │   ├── TaskCard.jsx            # Task card component
│   │   │   ├── TaskForm.jsx            # Task form
│   │   │   └── TaskDetails.jsx         # Task details
│   │   └── common/
│   │       ├── Header.jsx              # Application header
│   │       ├── Sidebar.jsx             # Navigation sidebar
│   │       └── Layout.jsx              # Main layout
│   ├── contexts/
│   │   ├── AuthContext.jsx             # Authentication context
│   │   ├── PermissionContext.jsx       # Permission context
│   │   └── ThemeContext.jsx            # Theme context
│   ├── hooks/
│   │   ├── useAuth.js                  # Authentication hook
│   │   ├── usePermissions.js           # Permission hook
│   │   └── useLocalStorage.js          # Local storage hook
│   ├── services/
│   │   ├── authService.js              # Authentication service
│   │   ├── apiService.js               # API service
│   │   ├── tokenService.js             # Token management
│   │   └── permissionService.js        # Permission service
│   ├── store/
│   │   ├── store.js                    # Redux store
│   │   ├── authSlice.js                # Authentication slice
│   │   ├── taskSlice.js                # Task slice
│   │   └── uiSlice.js                  # UI state slice
│   └── utils/
│       ├── constants.js                # Application constants
│       ├── validators.js               # Form validators
│       └── helpers.js                  # Utility functions
```
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
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
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
