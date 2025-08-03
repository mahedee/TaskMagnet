# Authentication & Authorization Design Document
**TaskMagnet Project - High-Level Design & ERD**

---

## 📋 **Document Information**
- **Version**: 2.0
- **Date**: August 3, 2025
- **Status**: Draft
- **Next Review**: September 3, 2025

---

## 🎯 **Overview**

This document outlines the authentication and authorization architecture for TaskMagnet, implementing secure user management with JWT-based authentication and fine-grained permission-based authorization, including comprehensive Entity Relationship Diagram.

### **Key Requirements**
- Secure user authentication with JWT tokens
- Role-based access control (RBAC) with granular permissions
- Dynamic UI permission management
- Session management and security
- Audit trail for security events
- Scalable database design with performance optimization

---

## 🏗️ **Architecture Overview**

### **Security Flow**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Database     │
│   (React)       │    │  (Spring Boot)  │    │   (Oracle)      │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ • Login Form    │◄──►│ • JWT Auth      │◄──►│ • Users         │
│ • Token Storage │    │ • Security      │    │ • Roles         │
│ • Route Guards  │    │ • Permissions   │    │ • Permissions   │
│ • Permission UI │    │ • Method Sec    │    │ • Audit Logs    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🗄️ **Entity Relationship Diagram**

### **Complete ERD Structure**

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          AUTHENTICATION & AUTHORIZATION ERD                     │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│     USERS       │         │   USER_ROLES    │         │     ROLES       │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ PK: id          │         │ FK: user_id     │         │ PK: id          │
│    username     │◄────────┤ FK: role_id     ├────────►│    name         │
│    email        │         │    assigned_at  │         │    display_name │
│    password     │         │    assigned_by  │         │    description  │
│    first_name   │         │    is_active    │         │    hierarchy_lv │
│    last_name    │         └─────────────────┘         │    is_system    │
│    is_active    │                                     │    created_at   │
│    last_login   │                                     │    updated_at   │
│    created_at   │                                     └─────────────────┘
│    updated_at   │                                              │
└─────────────────┘                                              │
         │                                                       │
         │                                                       │
         │              ┌─────────────────┐                     │
         │              │ROLE_PERMISSIONS │                     │
         │              ├─────────────────┤                     │
         │              │ FK: role_id     │◄────────────────────┘
         │              │ FK: permission_id│
         │              │    granted_at   │
         │              │    granted_by   │
         │              └─────────────────┘
         │                       │
         │                       │
         │                       ▼
         │              ┌─────────────────┐
         │              │  PERMISSIONS    │
         │              ├─────────────────┤
         │              │ PK: id          │
         │              │    name         │
         │              │    resource_type│
         │              │    action       │
         │              │    description  │
         │              │    is_system    │
         │              │    created_at   │
         │              └─────────────────┘
         │                       ▲
         │                       │
         │              ┌─────────────────┐
         │              │USER_PERMISSIONS │
         │              ├─────────────────┤
         │              │ PK: id          │
         └─────────────►│ FK: user_id     │
                        │ FK: permission_id│
                        │    resource_id  │
                        │    resource_type│
                        │    permission_tp│ -- GRANT/DENY
                        │    granted_by   │
                        │    granted_at   │
                        │    expires_at   │
                        │    is_active    │
                        └─────────────────┘

┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│ REFRESH_TOKENS  │         │PASSWORD_HISTORY │         │SECURITY_AUDIT   │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ PK: id          │         │ PK: id          │         │ PK: id          │
│ FK: user_id     │◄────────┤ FK: user_id     │◄────────┤ FK: user_id     │
│    token        │         │    password_hash│         │    action       │
│    expires_at   │         │    created_at   │         │    ip_address   │
│    is_revoked   │         │    is_current   │         │    user_agent   │
│    created_at   │         └─────────────────┘         │    resource_type│
└─────────────────┘                                     │    resource_id  │
                                                        │    details      │
┌─────────────────┐         ┌─────────────────┐         │    status       │
│LOGIN_ATTEMPTS   │         │  USER_SESSIONS  │         │    created_at   │
├─────────────────┤         ├─────────────────┤         └─────────────────┘
│ PK: id          │         │ PK: id          │
│ FK: user_id     │         │ FK: user_id     │
│    ip_address   │         │    session_token│
│    attempted_at │         │    ip_address   │
│    status       │         │    user_agent   │
│    failure_count│         │    created_at   │
└─────────────────┘         │    last_activity│
                           │    expires_at   │
                           │    is_active    │
                           └─────────────────┘
```

### **Entity Relationships Summary**

#### **Core Authentication Entities:**
1. **USERS** - Primary authentication table with security features
2. **ROLES** - Hierarchical role system with privilege levels
3. **PERMISSIONS** - Granular resource-action based permissions
4. **USER_ROLES** - Many-to-many user-role mapping with expiration
5. **ROLE_PERMISSIONS** - Many-to-many role-permission mapping
6. **USER_PERMISSIONS** - Direct permission assignments (overrides)

#### **Security & Audit Entities:**
7. **SECURITY_AUDIT_LOG** - Comprehensive audit trail
8. **REFRESH_TOKENS** - JWT token management and rotation
9. **PASSWORD_HISTORY** - Password policy enforcement
10. **LOGIN_ATTEMPTS** - Security monitoring and brute-force protection
11. **USER_SESSIONS** - Active session tracking

#### **Relationship Cardinalities:**
```
Users (1) ←→ (M) User_Roles (M) ←→ (1) Roles
Roles (1) ←→ (M) Role_Permissions (M) ←→ (1) Permissions
Users (1) ←→ (M) User_Permissions (M) ←→ (1) Permissions
Users (1) ←→ (M) Security_Audit_Log
Users (1) ←→ (M) Refresh_Tokens
Users (1) ←→ (M) Password_History
Users (1) ←→ (M) Login_Attempts
Users (1) ←→ (M) User_Sessions
```

---

## 🔐 **Authentication Design**

### **JWT Token Structure**
The authentication system uses JWT tokens with the following structure:
- **Header**: Algorithm (HS512) and token type
- **Payload**: User ID, username, roles, permissions, issued/expiry times
- **Signature**: Cryptographic signature for token verification

### **Authentication Flow**
1. **User Login**: Submit credentials (username/password)
2. **Credential Validation**: Backend validates against encrypted database records
3. **JWT Generation**: Create token with user claims and permissions
4. **Token Response**: Return signed JWT to frontend client
5. **Token Storage**: Frontend securely stores token (recommended: httpOnly cookies)
6. **Request Authorization**: Include token in all API request headers
7. **Token Validation**: Backend validates and extracts user context per request
8. **Token Refresh**: Automatic renewal before expiration

### **Security Features**
- **Password Encryption**: BCrypt hashing with salt rounds
- **Token Expiration**: Configurable JWT lifetime (default: 24 hours)
- **Refresh Tokens**: Secure token renewal mechanism
- **Session Management**: Track active user sessions
- **Account Lockout**: Automatic locking after failed attempts
- **Password History**: Prevent password reuse
- **Multi-Factor Authentication**: Support for future 2FA implementation

---

## 🛡️ **Authorization Design**

### **Permission Model Architecture**
The authorization system implements a flexible **Role-Based Access Control (RBAC)** with granular permissions:

#### **Core Components:**
1. **Roles**: Named collections of permissions with hierarchy levels
2. **Permissions**: Specific resource-action combinations (e.g., USER_CREATE, TASK_UPDATE)
3. **Resource Types**: USER, TASK, PROJECT, SYSTEM, REPORT
4. **Actions**: CREATE, READ, UPDATE, DELETE, EXECUTE, APPROVE
5. **Direct Permissions**: User-specific overrides that can GRANT or DENY access

#### **Permission Evaluation Logic:**
1. **Direct Permissions** (Highest Priority): Check user-specific GRANT/DENY permissions
2. **Role Permissions** (Standard): Evaluate permissions from assigned roles
3. **Hierarchy Inheritance**: Higher-level roles inherit lower-level permissions
4. **Resource-Specific**: Permissions can target specific resource instances
5. **Expiration Support**: Time-limited permission assignments

### **Role Hierarchy System**
```
SUPER_ADMIN (Level 100) → Full system access
    ↓
ADMIN (Level 80) → Administrative functions
    ↓
PROJECT_MANAGER (Level 60) → Project oversight
    ↓
TEAM_LEAD (Level 40) → Team management
    ↓
DEVELOPER (Level 20) → Task execution
    ↓
VIEWER (Level 10) → Read-only access
```

### **Permission Categories**
- **User Management**: Create, modify, delete user accounts
- **Task Operations**: Full task lifecycle management
- **Project Control**: Project creation and administration
- **System Functions**: Backup, audit logs, system configuration
- **Reporting**: Generate and access various reports

### **Security Enforcement**
- **Method-Level Security**: Backend endpoint protection
- **UI Permission Guards**: Dynamic frontend component rendering
- **Resource-Level Access**: Instance-specific permission checking
- **Audit Trail**: Complete logging of authorization decisions
- **Risk Assessment**: Permission assignments based on risk levels

---

## 🔒 **Security Features**

### **Password Security**
- **Strong Hashing**: BCrypt with configurable salt rounds (minimum 12)
- **Password Policies**: Minimum length, complexity requirements, expiration
- **Password History**: Prevent reuse of last N passwords
- **Account Lockout**: Automatic locking after consecutive failed attempts
- **Password Recovery**: Secure reset mechanism with time-limited tokens

### **JWT Security**
- **Strong Signing**: HMAC SHA-512 algorithm for token signatures
- **Token Expiration**: Configurable lifetime with automatic refresh
- **Secure Storage**: Recommendations for httpOnly cookies vs localStorage
- **Token Validation**: Comprehensive verification including signature and claims
- **Token Blacklisting**: Ability to revoke tokens before expiration

### **Session Management**
- **Session Tracking**: Monitor active user sessions across devices
- **Concurrent Sessions**: Configurable limits on simultaneous logins
- **Session Timeout**: Automatic logout after inactivity period
- **Device Fingerprinting**: Enhanced security through device identification
- **Forced Logout**: Administrative ability to terminate user sessions

### **Security Monitoring**
- **Login Attempt Tracking**: Monitor successful and failed authentication attempts
- **IP Address Monitoring**: Track login locations and detect anomalies
- **Rate Limiting**: Prevent brute force attacks on authentication endpoints
- **Risk Scoring**: Assess login attempts based on various factors
- **Alert System**: Notifications for suspicious activities

---

## 🏷️ **Default Roles & Permissions**

### **System Roles Hierarchy**
The system implements a hierarchical role structure where higher-level roles inherit permissions from lower levels:

#### **Role Definitions:**
1. **SUPER_ADMIN** (Level 100)
   - Complete system access and configuration
   - User management and role assignment
   - System backup and maintenance functions
   - Full audit log access

2. **ADMIN** (Level 80)
   - User account management (create, update, deactivate)
   - Project and task oversight across all teams
   - System audit log viewing
   - Role assignment (below admin level)

3. **PROJECT_MANAGER** (Level 60)
   - Full project lifecycle management
   - Task assignment and tracking
   - Team member management within projects
   - Project-specific reporting

4. **TEAM_LEAD** (Level 40)
   - Team task management and assignment
   - Team member performance monitoring
   - Limited user account updates
   - Team-specific reporting

5. **DEVELOPER** (Level 20)
   - Personal task management (create, update, complete)
   - View assigned project information
   - Update own profile and settings
   - Comment on tasks and collaborate

6. **VIEWER** (Level 10)
   - Read-only access to assigned projects
   - View task information and progress
   - Access to personal dashboard
   - Basic profile viewing

### **Permission Matrix**
Each role is granted specific permissions based on resource types and actions:

#### **Resource-Action Combinations:**
- **USER Management**: Create, read, update, delete user accounts
- **TASK Operations**: Create, read, update, delete, assign tasks
- **PROJECT Control**: Create, read, update, delete, manage projects
- **SYSTEM Functions**: Backup, configuration, audit log access
- **REPORT Generation**: Create, view, export various reports

### **Permission Inheritance**
Higher-level roles automatically inherit all permissions from lower-level roles, creating a cascading permission structure that ensures appropriate access levels while maintaining security boundaries.

---

## 📊 **Security Monitoring & Audit**

### **Comprehensive Audit Logging**
The system maintains detailed audit trails for all security-related activities:

#### **Tracked Events:**
- **Authentication Events**: Login, logout, failed attempts, account lockouts
- **Authorization Changes**: Role assignments, permission modifications
- **Resource Access**: CRUD operations on sensitive resources
- **Administrative Actions**: User account changes, system configuration updates
- **Security Incidents**: Suspicious activities, policy violations

#### **Audit Information Captured:**
- **User Context**: User ID, username, roles at time of action
- **Action Details**: Specific operation performed, resource affected
- **System Context**: IP address, user agent, session information
- **Data Changes**: Before/after values for modifications (JSON format)
- **Risk Assessment**: Automatic risk scoring based on action and context
- **Timestamps**: Precise timing of all events

### **Security Monitoring Features**
- **Real-time Alerting**: Immediate notifications for high-risk activities
- **Pattern Detection**: Identification of unusual access patterns
- **Compliance Reporting**: Generate reports for regulatory requirements
- **Data Retention**: Configurable retention policies for audit data
- **Performance Impact**: Minimal overhead through asynchronous logging

### **Monitoring Dashboards**
- **Security Overview**: High-level security metrics and trends
- **User Activity**: Individual user access patterns and behaviors
- **System Health**: Authentication system performance and availability
- **Threat Detection**: Potential security threats and anomalies
- **Compliance Status**: Adherence to security policies and standards

---

## 🧪 **Testing Strategy**

### **Security Testing Approach**
Comprehensive testing strategy to ensure robust authentication and authorization:

#### **Unit Testing**
- **Authentication Services**: Token generation, validation, and expiration
- **Permission Evaluation**: Role-based and direct permission checking
- **Password Security**: Hashing, validation, and policy enforcement
- **Audit Logging**: Event capture and data integrity

#### **Integration Testing**
- **API Security**: Endpoint protection and access control
- **Database Integration**: User, role, and permission data consistency
- **Token Management**: JWT lifecycle and refresh mechanisms
- **Cross-Service Communication**: Secure inter-service authentication

#### **Security Testing**
- **Authentication Bypass**: Attempt to circumvent login mechanisms
- **Authorization Escalation**: Test for privilege escalation vulnerabilities
- **Token Manipulation**: JWT tampering and replay attacks
- **Brute Force Protection**: Rate limiting and account lockout mechanisms
- **Session Security**: Session hijacking and fixation attacks

#### **Performance Testing**
- **Permission Queries**: Database performance under load
- **Authentication Throughput**: Login/logout performance at scale
- **Token Validation**: JWT processing performance
- **Audit Logging**: Impact on system performance

#### **User Experience Testing**
- **Login Flows**: User-friendly authentication processes
- **Permission UI**: Dynamic interface based on user permissions
- **Error Handling**: Clear security error messages
- **Session Management**: Seamless token refresh and logout

### **Test Coverage Requirements**
- **Minimum 90%** code coverage for security-related components
- **100%** coverage for critical authentication and authorization paths
- **Comprehensive** testing of all permission combinations
- **Regular** security penetration testing by external experts

---

## 📈 **Performance Optimization**

### **Database Design Considerations**
- **Strategic Indexing**: Composite indexes for common query patterns
- **Optimized Queries**: Efficient permission evaluation using database views
- **Connection Pooling**: Proper database connection management
- **Query Caching**: Cache frequently accessed permission data

### **Permission Evaluation Performance**
- **Hierarchical Caching**: Cache role inheritance chains
- **Permission Aggregation**: Pre-compute effective user permissions
- **Lazy Loading**: Load permissions only when needed
- **Batch Processing**: Group permission checks for efficiency

### **Scalability Features**
- **Horizontal Scaling**: Support for multiple application instances
- **Session Distribution**: Distributed session management
- **Load Balancing**: Proper load distribution across instances
- **Monitoring**: Performance metrics and alerting

---

## 📊 **Sample Data Structure**

### **Default Permissions Matrix**
```
RESOURCE_TYPE | ACTION | PERMISSION_NAME     | DESCRIPTION
-------------|--------|-------------------|----------------------------------
USER         | CREATE | USER_CREATE       | Create new users
USER         | READ   | USER_READ         | View user information
USER         | UPDATE | USER_UPDATE       | Modify user details
USER         | DELETE | USER_DELETE       | Delete users
TASK         | CREATE | TASK_CREATE       | Create new tasks
TASK         | READ   | TASK_READ         | View tasks
TASK         | UPDATE | TASK_UPDATE       | Modify tasks
TASK         | DELETE | TASK_DELETE       | Delete tasks
PROJECT      | CREATE | PROJECT_CREATE    | Create projects
PROJECT      | READ   | PROJECT_READ      | View projects
PROJECT      | UPDATE | PROJECT_UPDATE    | Modify projects
PROJECT      | DELETE | PROJECT_DELETE    | Delete projects
SYSTEM       | READ   | SYSTEM_AUDIT_LOG  | View audit logs
SYSTEM       | EXECUTE| SYSTEM_BACKUP     | Execute system backup
```

### **Default Role Hierarchy**
```
HIERARCHY | ROLE_NAME          | PERMISSIONS_COUNT | DESCRIPTION
----------|-------------------|------------------|---------------------------
100       | ROLE_SUPER_ADMIN  | ALL              | Full system access
80        | ROLE_ADMIN        | 15               | Administrative access
60        | ROLE_PROJECT_MGR  | 10               | Project management
40        | ROLE_TEAM_LEAD    | 8                | Team leadership
20        | ROLE_DEVELOPER    | 5                | Development tasks
10        | ROLE_VIEWER       | 3                | Read-only access
```

---

## 🚀 **Implementation Phases**

---

## 🚀 **Implementation Phases**

### **Phase 1: Authentication Foundation (Week 1)**
- JWT implementation and token management
- Login/logout functionality
- Password security and validation
- Basic user registration

### **Phase 2: Authorization Framework (Week 2)**
- Database schema for roles and permissions
- Permission evaluation service
- Method-level security annotations
- Seed data for default roles

### **Phase 3: Frontend Integration (Week 3)**
- Permission context provider
- Protected routes and components
- Dynamic UI based on permissions
- User session management

### **Phase 4: Advanced Security (Week 4)**
- Audit logging and monitoring
- Permission management UI
- Security testing and validation
- Documentation and training---

## 🎯 **Security Checklist**

### **Authentication Security**
- ✅ Strong password policies enforced
- ✅ JWT tokens with proper expiration
- ✅ Secure token storage (httpOnly cookies recommended)
- ✅ Session timeout and refresh logic
- ✅ Rate limiting on authentication endpoints

### **Authorization Security**
- ✅ Principle of least privilege
- ✅ Method-level security enforcement
- ✅ Frontend permission validation
- ✅ Resource-level access control
- ✅ Permission inheritance and overrides

### **General Security**
- ✅ HTTPS enforcement in production
- ✅ CORS configuration
- ✅ Security headers implementation
- ✅ Input validation and sanitization
- ✅ Comprehensive audit logging

---

## 📚 **References**

- [OWASP Authentication Guide](https://owasp.org/www-project-authentication-cheat-sheet/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/)
- [React Security Best Practices](https://blog.logrocket.com/security-best-practices-react-applications/)

---

*Last Updated: August 3, 2025*  
*Version: 2.0*  
*Next Review: September 3, 2025*
