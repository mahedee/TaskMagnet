-- ===================================================================
-- TaskMagnet Database Schema - Domain 1: Security Domain
-- Database: Oracle XE 21c
-- Version: 1.0
-- Created: August 4, 2025
-- Purpose: Authentication, Authorization, and Security Domain Tables
-- ===================================================================

-- ===================================================================
-- 1. ENHANCED USERS TABLE (Core Authentication)
-- ===================================================================
CREATE TABLE users (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    username VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    first_name VARCHAR2(50),
    last_name VARCHAR2(50),
    is_active NUMBER(1) DEFAULT 1,
    email_verified NUMBER(1) DEFAULT 0,
    phone VARCHAR2(20),
    profile_image_url VARCHAR2(500),
    timezone VARCHAR2(50) DEFAULT 'UTC',
    language VARCHAR2(10) DEFAULT 'en',
    last_login TIMESTAMP,
    password_changed_at TIMESTAMP,
    failed_login_attempts NUMBER DEFAULT 0,
    account_locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    updated_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_users PRIMARY KEY (id),
    
    -- Unique Constraints
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_users_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_users_email_verified CHECK (email_verified IN (0, 1)),
    CONSTRAINT chk_users_failed_attempts CHECK (failed_login_attempts >= 0)
);

-- ===================================================================
-- 2. ENHANCED ROLES TABLE (Role-Based Access Control)
-- ===================================================================
CREATE TABLE roles (
    id NUMBER(10,0) GENERATED AS IDENTITY,
    name VARCHAR2(50) NOT NULL,
    display_name VARCHAR2(100),
    description VARCHAR2(500),
    hierarchy_level NUMBER DEFAULT 0,
    is_system NUMBER(1) DEFAULT 0,
    color VARCHAR2(7) DEFAULT '#2563eb',
    max_users NUMBER,
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    updated_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_roles PRIMARY KEY (id),
    
    -- Unique Constraints
    CONSTRAINT uk_roles_name UNIQUE (name),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_roles_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_roles_system CHECK (is_system IN (0, 1)),
    CONSTRAINT chk_roles_hierarchy CHECK (hierarchy_level >= 0),
    CONSTRAINT chk_roles_color CHECK (REGEXP_LIKE(color, '^#[0-9A-Fa-f]{6}$')),
    CONSTRAINT chk_roles_name_enum CHECK (name IN (
        'ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PROJECT_MANAGER', 
        'ROLE_TEAM_LEAD', 'ROLE_DEVELOPER', 'ROLE_TESTER', 
        'ROLE_VIEWER', 'ROLE_GUEST', 'ROLE_MODERATOR', 'ROLE_USER'
    ))
);

-- ===================================================================
-- 3. PERMISSIONS TABLE (Granular Permission System)
-- ===================================================================
CREATE TABLE permissions (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    name VARCHAR2(100) NOT NULL,
    resource_type VARCHAR2(50) NOT NULL,
    action VARCHAR2(50) NOT NULL,
    description VARCHAR2(500),
    is_system NUMBER(1) DEFAULT 0,
    category VARCHAR2(50),
    risk_level VARCHAR2(20) DEFAULT 'LOW',
    requires_approval NUMBER(1) DEFAULT 0,
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    
    -- Unique Constraints
    CONSTRAINT uk_permissions_name UNIQUE (name),
    CONSTRAINT uk_permissions_resource_action UNIQUE (resource_type, action),
    
    -- Check Constraints
    CONSTRAINT chk_permissions_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_permissions_system CHECK (is_system IN (0, 1)),
    CONSTRAINT chk_permissions_approval CHECK (requires_approval IN (0, 1)),
    CONSTRAINT chk_permissions_resource_type CHECK (resource_type IN (
        'USER', 'TASK', 'PROJECT', 'SYSTEM', 'REPORT', 'AUDIT', 'SETTINGS'
    )),
    CONSTRAINT chk_permissions_action CHECK (action IN (
        'CREATE', 'READ', 'UPDATE', 'DELETE', 'EXECUTE', 'APPROVE', 'EXPORT', 'IMPORT'
    )),
    CONSTRAINT chk_permissions_risk_level CHECK (risk_level IN (
        'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    ))
);

-- ===================================================================
-- 4. USER_ROLES TABLE (Many-to-Many: Users ↔ Roles)
-- ===================================================================
CREATE TABLE user_roles (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    role_id NUMBER(10,0) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by NUMBER(19,0),
    expires_at TIMESTAMP,
    is_active NUMBER(1) DEFAULT 1,
    notes VARCHAR2(500),
    
    -- Primary Key
    CONSTRAINT pk_user_roles PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_user_roles_user_role UNIQUE (user_id, role_id),
    
    -- Check Constraints
    CONSTRAINT chk_user_roles_active CHECK (is_active IN (0, 1))
);

-- ===================================================================
-- 5. ROLE_PERMISSIONS TABLE (Many-to-Many: Roles ↔ Permissions)
-- ===================================================================
CREATE TABLE role_permissions (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    role_id NUMBER(10,0) NOT NULL,
    permission_id NUMBER(19,0) NOT NULL,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by NUMBER(19,0),
    is_active NUMBER(1) DEFAULT 1,
    
    -- Primary Key
    CONSTRAINT pk_role_permissions PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_granted_by FOREIGN KEY (granted_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_role_permissions_role_perm UNIQUE (role_id, permission_id),
    
    -- Check Constraints
    CONSTRAINT chk_role_permissions_active CHECK (is_active IN (0, 1))
);

-- ===================================================================
-- 6. USER_PERMISSIONS TABLE (Direct User Permission Overrides)
-- ===================================================================
CREATE TABLE user_permissions (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    permission_id NUMBER(19,0) NOT NULL,
    resource_id NUMBER(19,0),
    resource_type VARCHAR2(50),
    permission_type VARCHAR2(10) DEFAULT 'GRANT',
    granted_by NUMBER(19,0),
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    reason VARCHAR2(500),
    is_active NUMBER(1) DEFAULT 1,
    
    -- Primary Key
    CONSTRAINT pk_user_permissions PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_user_permissions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_permissions_granted_by FOREIGN KEY (granted_by) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_user_permissions_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_user_permissions_type CHECK (permission_type IN ('GRANT', 'DENY'))
);

-- ===================================================================
-- 7. SECURITY_AUDIT_LOG TABLE (Comprehensive Audit Trail)
-- ===================================================================
CREATE TABLE security_audit_log (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0),
    action VARCHAR2(100) NOT NULL,
    resource_type VARCHAR2(50),
    resource_id NUMBER(19,0),
    old_values CLOB,
    new_values CLOB,
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(1000),
    session_id VARCHAR2(255),
    status VARCHAR2(20) DEFAULT 'SUCCESS',
    risk_score NUMBER(3,2) DEFAULT 0.00,
    details VARCHAR2(2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_security_audit_log PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_audit_status CHECK (status IN ('SUCCESS', 'FAILED', 'BLOCKED', 'WARNING')),
    CONSTRAINT chk_audit_risk_score CHECK (risk_score BETWEEN 0.00 AND 10.00)
);

-- ===================================================================
-- 8. REFRESH_TOKENS TABLE (JWT Token Management)
-- ===================================================================
CREATE TABLE refresh_tokens (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    token VARCHAR2(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked NUMBER(1) DEFAULT 0,
    revoked_at TIMESTAMP,
    revoked_reason VARCHAR2(100),
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique Constraints
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    
    -- Check Constraints
    CONSTRAINT chk_refresh_tokens_revoked CHECK (is_revoked IN (0, 1))
);

-- ===================================================================
-- 9. PASSWORD_HISTORY TABLE (Password Policy Enforcement)
-- ===================================================================
CREATE TABLE password_history (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_current NUMBER(1) DEFAULT 0,
    
    -- Primary Key
    CONSTRAINT pk_password_history PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_password_history_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_password_history_current CHECK (is_current IN (0, 1))
);

-- ===================================================================
-- 10. LOGIN_ATTEMPTS TABLE (Security Monitoring)
-- ===================================================================
CREATE TABLE login_attempts (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0),
    username VARCHAR2(50),
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(1000),
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(20) DEFAULT 'FAILED',
    failure_reason VARCHAR2(100),
    failure_count NUMBER DEFAULT 1,
    
    -- Primary Key
    CONSTRAINT pk_login_attempts PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_login_attempts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_login_attempts_status CHECK (status IN ('SUCCESS', 'FAILED', 'BLOCKED')),
    CONSTRAINT chk_login_attempts_count CHECK (failure_count >= 0)
);

-- ===================================================================
-- 11. USER_SESSIONS TABLE (Active Session Tracking)
-- ===================================================================
CREATE TABLE user_sessions (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    session_token VARCHAR2(255) NOT NULL,
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active NUMBER(1) DEFAULT 1,
    
    -- Primary Key
    CONSTRAINT pk_user_sessions PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique Constraints
    CONSTRAINT uk_user_sessions_token UNIQUE (session_token),
    
    -- Check Constraints
    CONSTRAINT chk_user_sessions_active CHECK (is_active IN (0, 1))
);

-- ===================================================================
-- SECURITY DOMAIN INDEXES (Performance Optimization)
-- ===================================================================

-- User indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_last_login ON users(last_login);

-- Role and permission indexes
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);
CREATE INDEX idx_user_roles_active ON user_roles(is_active);
CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);
CREATE INDEX idx_user_permissions_user ON user_permissions(user_id);
CREATE INDEX idx_user_permissions_resource ON user_permissions(resource_id, resource_type);

-- Security audit indexes
CREATE INDEX idx_audit_user ON security_audit_log(user_id);
CREATE INDEX idx_audit_action ON security_audit_log(action);
CREATE INDEX idx_audit_created_at ON security_audit_log(created_at);
CREATE INDEX idx_audit_ip ON security_audit_log(ip_address);

-- Authentication token indexes
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens(expires_at);
CREATE INDEX idx_user_sessions_user ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_expires ON user_sessions(expires_at);
CREATE INDEX idx_login_attempts_user ON login_attempts(user_id);
CREATE INDEX idx_login_attempts_ip ON login_attempts(ip_address);

-- ===================================================================
-- DOMAIN COMPLETION MARKER
-- ===================================================================
COMMENT ON TABLE users IS 'Security Domain: Core user authentication and profile management';
COMMENT ON TABLE roles IS 'Security Domain: Role-based access control system';
COMMENT ON TABLE permissions IS 'Security Domain: Granular permission definitions';
COMMENT ON TABLE user_roles IS 'Security Domain: User-role assignments with expiration';
COMMENT ON TABLE role_permissions IS 'Security Domain: Role-permission mappings';
COMMENT ON TABLE user_permissions IS 'Security Domain: Direct user permission overrides';
COMMENT ON TABLE security_audit_log IS 'Security Domain: Comprehensive security audit trail';
COMMENT ON TABLE refresh_tokens IS 'Security Domain: JWT refresh token management';
COMMENT ON TABLE password_history IS 'Security Domain: Password history for policy enforcement';
COMMENT ON TABLE login_attempts IS 'Security Domain: Failed login attempt tracking';
COMMENT ON TABLE user_sessions IS 'Security Domain: Active user session management';

-- ===================================================================
-- END OF SECURITY DOMAIN SCRIPT
-- ===================================================================
