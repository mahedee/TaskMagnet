-- ===================================================================
-- TaskMagnet Database Schema - Performance Optimization
-- Database: Oracle XE 21c
-- Version: 1.0
-- Created: August 4, 2025
-- Purpose: Additional indexes, constraints, and performance optimization
-- ===================================================================

-- ===================================================================
-- 1. ADDITIONAL PERFORMANCE INDEXES
-- ===================================================================

-- Composite indexes for common query patterns
CREATE INDEX idx_tasks_assignee_status ON tasks(assignee_id, status_id);
CREATE INDEX idx_tasks_project_status ON tasks(project_id, status_id);
CREATE INDEX idx_tasks_project_assignee ON tasks(project_id, assignee_id);
CREATE INDEX idx_tasks_due_date_status ON tasks(due_date, status_id);

-- Indexes for audit and security tables
CREATE INDEX idx_audit_user_action ON security_audit_log(user_id, action);
CREATE INDEX idx_audit_created_at_desc ON security_audit_log(created_at DESC);
CREATE INDEX idx_refresh_tokens_user_expires ON refresh_tokens(user_id, expires_at);

-- Indexes for collaboration features
CREATE INDEX idx_comments_task_created_at ON comments(task_id, created_at DESC);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
CREATE INDEX idx_activity_feed_project_created_at ON activity_feed(project_id, created_at DESC);

-- Indexes for user and session management
CREATE INDEX idx_users_active_last_login ON users(is_active, last_login);
CREATE INDEX idx_user_sessions_user_active ON user_sessions(user_id, is_active);

-- ===================================================================
-- 2. ADDITIONAL SPECIALIZED INDEXES
-- ===================================================================

-- Active records only indexes (Oracle doesn't support WHERE in CREATE INDEX like PostgreSQL)
-- These will be created as regular indexes
CREATE INDEX idx_users_active_only ON users(is_active, username);
CREATE INDEX idx_projects_active_only ON projects(is_active, name);
CREATE INDEX idx_tasks_active_only ON tasks(is_active, title);

-- Pending notifications
CREATE INDEX idx_notifications_unread ON notifications(is_read, user_id, created_at);

-- Open tasks (using function-based index for Oracle)
CREATE INDEX idx_tasks_status_closed ON tasks(status_id, project_id, assignee_id);

-- ===================================================================
-- 3. FUNCTION-BASED INDEXES (For Oracle)
-- ===================================================================

-- Case-insensitive searching
CREATE INDEX idx_users_username_upper ON users(UPPER(username));
CREATE INDEX idx_users_email_upper ON users(UPPER(email));
CREATE INDEX idx_projects_name_upper ON projects(UPPER(name));
CREATE INDEX idx_tasks_title_upper ON tasks(UPPER(title));

-- ===================================================================
-- 4. REFERENTIAL INTEGRITY VALIDATION
-- ===================================================================

-- Ensure all foreign key constraints are properly defined
-- (Most already defined in domain scripts, this is validation)

-- Verify user relationships
SELECT COUNT(*) as orphaned_user_roles 
FROM user_roles ur 
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.id = ur.user_id);

-- Verify task relationships
SELECT COUNT(*) as orphaned_tasks 
FROM tasks t 
WHERE NOT EXISTS (SELECT 1 FROM projects p WHERE p.id = t.project_id);

-- ===================================================================
-- 5. DATABASE TRIGGERS FOR AUDIT AND AUTOMATION
-- ===================================================================

-- Trigger to update timestamps automatically
CREATE OR REPLACE TRIGGER trg_users_update_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_projects_update_timestamp
    BEFORE UPDATE ON projects
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_tasks_update_timestamp
    BEFORE UPDATE ON tasks
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- Trigger to auto-generate task keys
CREATE OR REPLACE TRIGGER trg_tasks_generate_key
    BEFORE INSERT ON tasks
    FOR EACH ROW
DECLARE
    v_project_key VARCHAR2(10);
    v_task_number NUMBER;
BEGIN
    IF :NEW.task_key IS NULL THEN
        -- Get project key
        SELECT key INTO v_project_key
        FROM projects
        WHERE id = :NEW.project_id;
        
        -- Get next task number for this project
        SELECT COALESCE(MAX(TO_NUMBER(SUBSTR(task_key, LENGTH(v_project_key) + 2))), 0) + 1
        INTO v_task_number
        FROM tasks
        WHERE project_id = :NEW.project_id
        AND task_key LIKE v_project_key || '-%';
        
        -- Generate task key
        :NEW.task_key := v_project_key || '-' || LPAD(v_task_number, 3, '0');
    END IF;
END;
/

-- Trigger for activity feed logging
CREATE OR REPLACE TRIGGER trg_tasks_activity_log
    AFTER INSERT OR UPDATE OR DELETE ON tasks
    FOR EACH ROW
DECLARE
    v_activity_type VARCHAR2(50);
    v_description VARCHAR2(500);
    v_user_id NUMBER(19,0);
BEGIN
    -- Determine activity type and user
    IF INSERTING THEN
        v_activity_type := 'TASK_CREATED';
        v_description := 'Created task: ' || :NEW.title;
        v_user_id := :NEW.created_by;
    ELSIF UPDATING THEN
        v_activity_type := 'TASK_UPDATED';
        v_description := 'Updated task: ' || :NEW.title;
        v_user_id := :NEW.updated_by;
    ELSIF DELETING THEN
        v_activity_type := 'TASK_DELETED';
        v_description := 'Deleted task: ' || :OLD.title;
        v_user_id := :OLD.updated_by;
    END IF;
    
    -- Insert activity record
    INSERT INTO activity_feed (
        user_id, activity_type, entity_type, entity_id, 
        project_id, description, created_at
    ) VALUES (
        v_user_id, v_activity_type, 'TASK',
        COALESCE(:NEW.id, :OLD.id),
        COALESCE(:NEW.project_id, :OLD.project_id),
        v_description, CURRENT_TIMESTAMP
    );
END;
/

-- ===================================================================
-- 6. VIEWS FOR COMMON QUERIES
-- ===================================================================

-- View for user permissions (combining role and direct permissions)
CREATE OR REPLACE VIEW v_user_effective_permissions AS
SELECT DISTINCT
    u.id as user_id,
    u.username,
    p.id as permission_id,
    p.name as permission_name,
    p.resource_type,
    p.action,
    'ROLE' as permission_source
FROM users u
JOIN user_roles ur ON u.id = ur.user_id AND ur.is_active = 1
JOIN roles r ON ur.role_id = r.id AND r.is_active = 1
JOIN role_permissions rp ON r.id = rp.role_id AND rp.is_active = 1
JOIN permissions p ON rp.permission_id = p.id AND p.is_active = 1
WHERE u.is_active = 1

UNION ALL

SELECT DISTINCT
    u.id as user_id,
    u.username,
    p.id as permission_id,
    p.name as permission_name,
    p.resource_type,
    p.action,
    CASE WHEN up.permission_type = 'GRANT' THEN 'DIRECT_GRANT' ELSE 'DIRECT_DENY' END as permission_source
FROM users u
JOIN user_permissions up ON u.id = up.user_id AND up.is_active = 1
JOIN permissions p ON up.permission_id = p.id AND p.is_active = 1
WHERE u.is_active = 1;

-- View for task details with related information
CREATE OR REPLACE VIEW v_task_details AS
SELECT 
    t.id,
    t.task_key,
    t.title,
    t.description,
    t.task_type,
    t.priority,
    t.progress_percentage,
    t.due_date,
    t.start_date,
    t.estimated_hours,
    t.actual_hours,
    t.created_at,
    t.updated_at,
    
    -- Project information
    p.name as project_name,
    p.key as project_key,
    
    -- Status information
    ts.name as status_name,
    ts.color as status_color,
    ts.is_closed_status,
    
    -- Category information
    tc.name as category_name,
    tc.color as category_color,
    
    -- User information
    assignee.username as assignee_username,
    assignee.first_name as assignee_first_name,
    assignee.last_name as assignee_last_name,
    reporter.username as reporter_username,
    reporter.first_name as reporter_first_name,
    reporter.last_name as reporter_last_name,
    
    -- Calculated fields
    CASE 
        WHEN t.due_date < SYSDATE AND ts.is_closed_status = 0 THEN 1 
        ELSE 0 
    END as is_overdue,
    
    CASE 
        WHEN t.due_date BETWEEN SYSDATE AND SYSDATE + 3 AND ts.is_closed_status = 0 THEN 1 
        ELSE 0 
    END as is_due_soon
    
FROM tasks t
JOIN projects p ON t.project_id = p.id
JOIN task_statuses ts ON t.status_id = ts.id
LEFT JOIN task_categories tc ON t.category_id = tc.id
LEFT JOIN users assignee ON t.assignee_id = assignee.id
LEFT JOIN users reporter ON t.reporter_id = reporter.id
WHERE t.is_active = 1;

-- View for project statistics
CREATE OR REPLACE VIEW v_project_stats AS
SELECT 
    p.id as project_id,
    p.name as project_name,
    p.key as project_key,
    
    -- Task counts
    COUNT(t.id) as total_tasks,
    SUM(CASE WHEN ts.is_closed_status = 0 THEN 1 ELSE 0 END) as open_tasks,
    SUM(CASE WHEN ts.is_closed_status = 1 THEN 1 ELSE 0 END) as closed_tasks,
    SUM(CASE WHEN t.due_date < SYSDATE AND ts.is_closed_status = 0 THEN 1 ELSE 0 END) as overdue_tasks,
    
    -- Progress calculation
    CASE 
        WHEN COUNT(t.id) > 0 THEN 
            ROUND((SUM(CASE WHEN ts.is_closed_status = 1 THEN 1 ELSE 0 END) * 100.0) / COUNT(t.id), 2)
        ELSE 0 
    END as completion_percentage,
    
    -- Time tracking
    SUM(t.estimated_hours) as total_estimated_hours,
    SUM(t.actual_hours) as total_actual_hours,
    
    -- Team size
    (SELECT COUNT(DISTINCT pm.user_id) 
     FROM project_members pm 
     WHERE pm.project_id = p.id AND pm.is_active = 1) as team_size
     
FROM projects p
LEFT JOIN tasks t ON p.id = t.project_id AND t.is_active = 1
LEFT JOIN task_statuses ts ON t.status_id = ts.id
WHERE p.is_active = 1
GROUP BY p.id, p.name, p.key;

-- ===================================================================
-- 7. MATERIALIZED VIEWS FOR PERFORMANCE (Oracle Enterprise Edition)
-- ===================================================================

-- Note: Materialized views require Oracle Enterprise Edition
-- For Oracle XE, use regular views or scheduled refresh procedures

/*
-- User activity summary (Enterprise Edition only)
CREATE MATERIALIZED VIEW mv_user_activity_summary
REFRESH COMPLETE ON DEMAND
AS
SELECT 
    u.id as user_id,
    u.username,
    COUNT(DISTINCT t.id) as tasks_assigned,
    COUNT(DISTINCT c.id) as comments_made,
    MAX(u.last_login) as last_activity
FROM users u
LEFT JOIN tasks t ON u.id = t.assignee_id AND t.is_active = 1
LEFT JOIN comments c ON u.id = c.user_id AND c.is_active = 1
WHERE u.is_active = 1
GROUP BY u.id, u.username;
*/

-- ===================================================================
-- 8. TABLESPACE OPTIMIZATION (If custom tablespaces needed)
-- ===================================================================

-- Note: Oracle XE uses default tablespace
-- For production, consider creating separate tablespaces for:
-- - Large tables (tasks, comments, attachments)
-- - Indexes
-- - Audit data

-- ===================================================================
-- PERFORMANCE COMPLETION MARKER
-- ===================================================================

-- Performance optimizations completed
-- Key indexes created for:
-- - Common query patterns (assignee-status, project-status)
-- - Unread notifications and activity feeds  
-- - User and session management
-- - Task filtering and search
-- Views created for:
-- - User effective permissions consolidation
-- - Task details with full joins
-- - Project statistics and metrics

-- ===================================================================
-- END OF PERFORMANCE OPTIMIZATION SCRIPT
-- ===================================================================
