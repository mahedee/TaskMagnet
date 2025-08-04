-- ===================================================================
-- TaskMagnet Database Schema - Default Data Seeding
-- Database: Oracle XE 21c
-- Version: 1.0
-- Created: August 4, 2025
-- Purpose: Insert default permissions, roles, and seed data
-- ===================================================================

-- ===================================================================
-- 1. DEFAULT PERMISSIONS (Resource-Action Based)
-- ===================================================================

-- User Management Permissions
INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('USER_CREATE', 'USER', 'CREATE', 'Create new users in the system', 'USER_MANAGEMENT', 'HIGH', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('USER_READ', 'USER', 'READ', 'View user information and profiles', 'USER_MANAGEMENT', 'LOW', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('USER_UPDATE', 'USER', 'UPDATE', 'Modify user details and profiles', 'USER_MANAGEMENT', 'MEDIUM', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('USER_DELETE', 'USER', 'DELETE', 'Delete users from the system', 'USER_MANAGEMENT', 'CRITICAL', 1);

-- Task Management Permissions
INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('TASK_CREATE', 'TASK', 'CREATE', 'Create new tasks', 'TASK_MANAGEMENT', 'LOW', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('TASK_READ', 'TASK', 'READ', 'View tasks and task details', 'TASK_MANAGEMENT', 'LOW', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('TASK_UPDATE', 'TASK', 'UPDATE', 'Modify task details and status', 'TASK_MANAGEMENT', 'LOW', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('TASK_DELETE', 'TASK', 'DELETE', 'Delete tasks from the system', 'TASK_MANAGEMENT', 'MEDIUM', 1);

-- Project Management Permissions
INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('PROJECT_CREATE', 'PROJECT', 'CREATE', 'Create new projects', 'PROJECT_MANAGEMENT', 'MEDIUM', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('PROJECT_READ', 'PROJECT', 'READ', 'View projects and project details', 'PROJECT_MANAGEMENT', 'LOW', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('PROJECT_UPDATE', 'PROJECT', 'UPDATE', 'Modify project settings and details', 'PROJECT_MANAGEMENT', 'MEDIUM', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('PROJECT_DELETE', 'PROJECT', 'DELETE', 'Delete projects from the system', 'PROJECT_MANAGEMENT', 'HIGH', 1);

-- System Administration Permissions
INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('SYSTEM_AUDIT_LOG', 'SYSTEM', 'READ', 'View system audit logs', 'SYSTEM_ADMIN', 'HIGH', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('SYSTEM_BACKUP', 'SYSTEM', 'EXECUTE', 'Execute system backup operations', 'SYSTEM_ADMIN', 'CRITICAL', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('SYSTEM_SETTINGS', 'SYSTEM', 'UPDATE', 'Modify system configuration settings', 'SYSTEM_ADMIN', 'CRITICAL', 1);

-- Report Permissions
INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('REPORT_CREATE', 'REPORT', 'CREATE', 'Create custom reports', 'REPORTING', 'MEDIUM', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('REPORT_READ', 'REPORT', 'READ', 'View reports and analytics', 'REPORTING', 'LOW', 1);

INSERT INTO permissions (name, resource_type, action, description, category, risk_level, is_system) VALUES
('REPORT_EXPORT', 'REPORT', 'EXPORT', 'Export reports and data', 'REPORTING', 'MEDIUM', 1);

-- ===================================================================
-- 2. DEFAULT ROLES (Hierarchical Role System)
-- ===================================================================

-- Super Admin (Full System Access)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_SUPER_ADMIN', 'Super Administrator', 'Full system access with all permissions', 100, 1, '#dc2626', 1);

-- System Admin (Administrative Access)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_ADMIN', 'Administrator', 'Administrative access to system management', 80, 1, '#ea580c', 1);

-- Project Manager (Project Leadership)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_PROJECT_MANAGER', 'Project Manager', 'Full project management capabilities', 60, 1, '#2563eb', 1);

-- Team Lead (Team Leadership)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_TEAM_LEAD', 'Team Lead', 'Team leadership and coordination', 40, 1, '#7c3aed', 1);

-- Developer (Development Tasks)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_DEVELOPER', 'Developer', 'Development and implementation tasks', 30, 1, '#059669', 1);

-- Tester (Quality Assurance)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_TESTER', 'Tester', 'Quality assurance and testing', 25, 1, '#0891b2', 1);

-- User (Standard User Access)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_USER', 'User', 'Standard user with basic task access', 20, 1, '#6b7280', 1);

-- Moderator (Content Moderation)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_MODERATOR', 'Moderator', 'Content moderation and user management', 50, 1, '#ca8a04', 1);

-- Viewer (Read-only Access)
INSERT INTO roles (name, display_name, description, hierarchy_level, is_system, color, is_active) VALUES
('ROLE_VIEWER', 'Viewer', 'Read-only access to projects and tasks', 10, 1, '#9ca3af', 1);

-- ===================================================================
-- 3. ROLE-PERMISSION ASSIGNMENTS
-- ===================================================================

-- Super Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active) 
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_SUPER_ADMIN';

-- Admin permissions (exclude SYSTEM_BACKUP and SYSTEM_SETTINGS)
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN'
AND p.name IN (
    'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE',
    'PROJECT_CREATE', 'PROJECT_READ', 'PROJECT_UPDATE', 'PROJECT_DELETE',
    'TASK_CREATE', 'TASK_READ', 'TASK_UPDATE', 'TASK_DELETE',
    'REPORT_CREATE', 'REPORT_READ', 'REPORT_EXPORT',
    'SYSTEM_AUDIT_LOG'
);

-- Project Manager permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_PROJECT_MANAGER'
AND p.name IN (
    'PROJECT_CREATE', 'PROJECT_READ', 'PROJECT_UPDATE',
    'TASK_CREATE', 'TASK_READ', 'TASK_UPDATE', 'TASK_DELETE',
    'USER_READ', 'REPORT_READ', 'REPORT_CREATE'
);

-- Team Lead permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_TEAM_LEAD'
AND p.name IN (
    'PROJECT_READ', 'TASK_CREATE', 'TASK_READ', 'TASK_UPDATE',
    'USER_READ', 'REPORT_READ'
);

-- Developer permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_DEVELOPER'
AND p.name IN (
    'TASK_CREATE', 'TASK_READ', 'TASK_UPDATE',
    'PROJECT_READ', 'USER_READ'
);

-- Tester permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_TESTER'
AND p.name IN (
    'TASK_READ', 'TASK_UPDATE', 'PROJECT_READ', 'USER_READ'
);

-- User permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
AND p.name IN (
    'TASK_READ', 'TASK_UPDATE', 'PROJECT_READ'
);

-- Moderator permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_MODERATOR'
AND p.name IN (
    'USER_READ', 'USER_UPDATE', 'TASK_READ', 'TASK_UPDATE',
    'PROJECT_READ', 'REPORT_READ'
);

-- Viewer permissions
INSERT INTO role_permissions (role_id, permission_id, granted_at, is_active)
SELECT r.id, p.id, CURRENT_TIMESTAMP, 1
FROM roles r, permissions p
WHERE r.name = 'ROLE_VIEWER'
AND p.name IN (
    'TASK_READ', 'PROJECT_READ', 'USER_READ'
);

-- ===================================================================
-- 4. DEFAULT TASK STATUSES (Global)
-- ===================================================================
INSERT INTO task_statuses (name, description, color, order_index, is_closed_status, is_active) VALUES
('TO_DO', 'Task is ready to be worked on', '#6b7280', 1, 0, 1);

INSERT INTO task_statuses (name, description, color, order_index, is_closed_status, is_active) VALUES
('IN_PROGRESS', 'Task is currently being worked on', '#2563eb', 2, 0, 1);

INSERT INTO task_statuses (name, description, color, order_index, is_closed_status, is_active) VALUES
('IN_REVIEW', 'Task is under review or testing', '#ea580c', 3, 0, 1);

INSERT INTO task_statuses (name, description, color, order_index, is_closed_status, is_active) VALUES
('BLOCKED', 'Task is blocked by dependencies', '#dc2626', 4, 0, 1);

INSERT INTO task_statuses (name, description, color, order_index, is_closed_status, is_active) VALUES
('DONE', 'Task has been completed', '#059669', 5, 1, 1);

INSERT INTO task_statuses (name, description, color, order_index, is_closed_status, is_active) VALUES
('CANCELLED', 'Task has been cancelled', '#9ca3af', 6, 1, 1);

-- ===================================================================
-- 5. DEFAULT TASK CATEGORIES (Global)
-- ===================================================================
INSERT INTO task_categories (name, description, color, is_active) VALUES
('FEATURE', 'New feature development', '#2563eb', 1);

INSERT INTO task_categories (name, description, color, is_active) VALUES
('BUG', 'Bug fixes and issue resolution', '#dc2626', 1);

INSERT INTO task_categories (name, description, color, is_active) VALUES
('IMPROVEMENT', 'Enhancements to existing features', '#059669', 1);

INSERT INTO task_categories (name, description, color, is_active) VALUES
('DOCUMENTATION', 'Documentation and knowledge base', '#7c3aed', 1);

INSERT INTO task_categories (name, description, color, is_active) VALUES
('RESEARCH', 'Research and investigation tasks', '#ea580c', 1);

INSERT INTO task_categories (name, description, color, is_active) VALUES
('MAINTENANCE', 'System maintenance and cleanup', '#6b7280', 1);

-- ===================================================================
-- 6. DEFAULT ADMIN USER (COMMENTED - To be created via DataSeeder)
-- ===================================================================
/*
-- Default admin user will be created via DataSeeder class
-- Username: admin
-- Email: admin@gmail.com  
-- Password: Taskmagnet@2025 (BCrypt encoded)
-- Role: ROLE_ADMIN
*/

-- ===================================================================
-- 7. DEFAULT PROJECT (Sample Project)
-- ===================================================================
INSERT INTO projects (name, key, description, status, is_active) VALUES
('TaskMagnet Development', 'TM', 'Main development project for TaskMagnet application', 'ACTIVE', 1);

-- ===================================================================
-- COMMIT ALL CHANGES
-- ===================================================================
COMMIT;

-- ===================================================================
-- VERIFICATION QUERIES (Optional - for testing)
-- ===================================================================
/*
-- Verify permissions
SELECT COUNT(*) as permission_count FROM permissions;

-- Verify roles
SELECT COUNT(*) as role_count FROM roles;

-- Verify role-permission assignments
SELECT r.name as role_name, COUNT(rp.permission_id) as permission_count
FROM roles r
LEFT JOIN role_permissions rp ON r.id = rp.role_id
GROUP BY r.name
ORDER BY r.hierarchy_level DESC;

-- Verify default statuses and categories
SELECT COUNT(*) as status_count FROM task_statuses;
SELECT COUNT(*) as category_count FROM task_categories;

-- Verify default project
SELECT COUNT(*) as project_count FROM projects;
*/

-- ===================================================================
-- END OF SEED DATA SCRIPT
-- ===================================================================
