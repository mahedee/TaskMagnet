-- ===================================================================
-- TaskMagnet Database Schema - Domain 2: Business Domain
-- Database: Oracle XE 21c
-- Version: 1.0
-- Created: August 4, 2025
-- Purpose: Project and Task Management Business Domain Tables
-- ===================================================================

-- ===================================================================
-- 1. PROJECTS TABLE (Project Management)
-- ===================================================================
CREATE TABLE projects (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    name VARCHAR2(100) NOT NULL,
    key VARCHAR2(10) NOT NULL,
    description VARCHAR2(2000),
    project_lead_id NUMBER(19,0),
    status VARCHAR2(20) DEFAULT 'ACTIVE',
    start_date DATE,
    end_date DATE,
    budget NUMBER(15,2),
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    updated_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_projects PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_projects_lead FOREIGN KEY (project_lead_id) REFERENCES users(id),
    CONSTRAINT fk_projects_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_projects_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_projects_key UNIQUE (key),
    
    -- Check Constraints
    CONSTRAINT chk_projects_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_projects_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'COMPLETED')),
    CONSTRAINT chk_projects_key_format CHECK (REGEXP_LIKE(key, '^[A-Z]{2,5}$')),
    CONSTRAINT chk_projects_budget CHECK (budget >= 0)
);

-- ===================================================================
-- 2. TASK_CATEGORIES TABLE (Task Classification)
-- ===================================================================
CREATE TABLE task_categories (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    name VARCHAR2(50) NOT NULL,
    description VARCHAR2(255),
    color VARCHAR2(7) DEFAULT '#2563eb',
    project_id NUMBER(19,0),
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_task_categories PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_categories_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_categories_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_task_categories_name_project UNIQUE (name, project_id),
    
    -- Check Constraints
    CONSTRAINT chk_task_categories_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_task_categories_color CHECK (REGEXP_LIKE(color, '^#[0-9A-Fa-f]{6}$'))
);

-- ===================================================================
-- 3. TASK_STATUSES TABLE (Task Status Management)
-- ===================================================================
CREATE TABLE task_statuses (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    name VARCHAR2(50) NOT NULL,
    description VARCHAR2(255),
    color VARCHAR2(7) DEFAULT '#6b7280',
    order_index NUMBER NOT NULL,
    is_closed_status NUMBER(1) DEFAULT 0,
    project_id NUMBER(19,0),
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_task_statuses PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_statuses_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_statuses_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_task_statuses_name_project UNIQUE (name, project_id),
    CONSTRAINT uk_task_statuses_order_project UNIQUE (order_index, project_id),
    
    -- Check Constraints
    CONSTRAINT chk_task_statuses_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_task_statuses_closed CHECK (is_closed_status IN (0, 1)),
    CONSTRAINT chk_task_statuses_color CHECK (REGEXP_LIKE(color, '^#[0-9A-Fa-f]{6}$')),
    CONSTRAINT chk_task_statuses_order CHECK (order_index > 0)
);

-- ===================================================================
-- 4. TASKS TABLE (Core Task Management)
-- ===================================================================
CREATE TABLE tasks (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    project_id NUMBER(19,0) NOT NULL,
    title VARCHAR2(255) NOT NULL,
    description CLOB,
    task_key VARCHAR2(20) NOT NULL,
    task_type VARCHAR2(20) DEFAULT 'TASK',
    priority VARCHAR2(10) DEFAULT 'MEDIUM',
    status_id NUMBER(19,0) NOT NULL,
    category_id NUMBER(19,0),
    assignee_id NUMBER(19,0),
    reporter_id NUMBER(19,0) NOT NULL,
    parent_task_id NUMBER(19,0),
    story_points NUMBER,
    due_date DATE,
    start_date DATE,
    estimated_hours NUMBER(5,2),
    actual_hours NUMBER(5,2),
    progress_percentage NUMBER(3,0) DEFAULT 0,
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    updated_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_status FOREIGN KEY (status_id) REFERENCES task_statuses(id),
    CONSTRAINT fk_tasks_category FOREIGN KEY (category_id) REFERENCES task_categories(id),
    CONSTRAINT fk_tasks_assignee FOREIGN KEY (assignee_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_task_id) REFERENCES tasks(id),
    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_tasks_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_tasks_key UNIQUE (task_key),
    
    -- Check Constraints
    CONSTRAINT chk_tasks_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_tasks_type CHECK (task_type IN ('STORY', 'TASK', 'BUG', 'EPIC', 'SUBTASK')),
    CONSTRAINT chk_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_tasks_progress CHECK (progress_percentage BETWEEN 0 AND 100),
    CONSTRAINT chk_tasks_hours CHECK (estimated_hours >= 0 AND actual_hours >= 0),
    CONSTRAINT chk_tasks_story_points CHECK (story_points >= 0),
    CONSTRAINT chk_tasks_dates CHECK (start_date <= due_date OR start_date IS NULL OR due_date IS NULL)
);

-- ===================================================================
-- 5. PROJECT_MEMBERS TABLE (Project Team Management)
-- ===================================================================
CREATE TABLE project_members (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    project_id NUMBER(19,0) NOT NULL,
    user_id NUMBER(19,0) NOT NULL,
    role VARCHAR2(50) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active NUMBER(1) DEFAULT 1,
    added_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_project_members PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_project_members_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_members_added_by FOREIGN KEY (added_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_project_members_project_user UNIQUE (project_id, user_id),
    
    -- Check Constraints
    CONSTRAINT chk_project_members_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_project_members_role CHECK (role IN (
        'PROJECT_LEAD', 'DEVELOPER', 'TESTER', 'DESIGNER', 'ANALYST', 'MEMBER', 'VIEWER'
    ))
);

-- ===================================================================
-- 6. TASK_WATCHERS TABLE (Task Subscription Management)
-- ===================================================================
CREATE TABLE task_watchers (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    task_id NUMBER(19,0) NOT NULL,
    user_id NUMBER(19,0) NOT NULL,
    watch_type VARCHAR2(20) DEFAULT 'WATCHING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_task_watchers PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_watchers_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_watchers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique Constraints
    CONSTRAINT uk_task_watchers_task_user UNIQUE (task_id, user_id),
    
    -- Check Constraints
    CONSTRAINT chk_task_watchers_type CHECK (watch_type IN ('WATCHING', 'IGNORING'))
);

-- ===================================================================
-- 7. TASK_LINKS TABLE (Task Relationship Management)
-- ===================================================================
CREATE TABLE task_links (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    source_task_id NUMBER(19,0) NOT NULL,
    target_task_id NUMBER(19,0) NOT NULL,
    link_type VARCHAR2(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by NUMBER(19,0),
    
    -- Primary Key
    CONSTRAINT pk_task_links PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_links_source FOREIGN KEY (source_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_links_target FOREIGN KEY (target_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_links_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_task_links_source_target_type UNIQUE (source_task_id, target_task_id, link_type),
    
    -- Check Constraints
    CONSTRAINT chk_task_links_type CHECK (link_type IN (
        'BLOCKS', 'DUPLICATES', 'RELATES_TO', 'SUBTASK_OF', 'DEPENDS_ON'
    )),
    CONSTRAINT chk_task_links_different_tasks CHECK (source_task_id != target_task_id)
);

-- ===================================================================
-- 8. TASK_HISTORY TABLE (Task Change Tracking)
-- ===================================================================
CREATE TABLE task_history (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    task_id NUMBER(19,0) NOT NULL,
    field_name VARCHAR2(50) NOT NULL,
    old_value VARCHAR2(2000),
    new_value VARCHAR2(2000),
    changed_by NUMBER(19,0) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_reason VARCHAR2(500),
    
    -- Primary Key
    CONSTRAINT pk_task_history PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_history_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_history_changed_by FOREIGN KEY (changed_by) REFERENCES users(id)
);

-- ===================================================================
-- BUSINESS DOMAIN INDEXES (Performance Optimization)
-- ===================================================================

-- Project indexes
CREATE INDEX idx_projects_lead ON projects(project_lead_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_key ON projects(key);

-- Task indexes
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_tasks_reporter ON tasks(reporter_id);
CREATE INDEX idx_tasks_status ON tasks(status_id);
CREATE INDEX idx_tasks_category ON tasks(category_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_key ON tasks(task_key);
CREATE INDEX idx_tasks_parent ON tasks(parent_task_id);
CREATE INDEX idx_tasks_priority_status ON tasks(priority, status_id);

-- Category and status indexes
CREATE INDEX idx_task_categories_project ON task_categories(project_id);
CREATE INDEX idx_task_statuses_project ON task_statuses(project_id);
CREATE INDEX idx_task_statuses_order ON task_statuses(order_index);

-- Project member indexes
CREATE INDEX idx_project_members_project ON project_members(project_id);
CREATE INDEX idx_project_members_user ON project_members(user_id);
CREATE INDEX idx_project_members_role ON project_members(role);

-- Task relationship indexes
CREATE INDEX idx_task_watchers_task ON task_watchers(task_id);
CREATE INDEX idx_task_watchers_user ON task_watchers(user_id);
CREATE INDEX idx_task_links_source ON task_links(source_task_id);
CREATE INDEX idx_task_links_target ON task_links(target_task_id);
CREATE INDEX idx_task_history_task ON task_history(task_id);
CREATE INDEX idx_task_history_changed_at ON task_history(changed_at);

-- ===================================================================
-- SEQUENCES FOR AUTO-GENERATION (If needed for custom key generation)
-- ===================================================================
CREATE SEQUENCE seq_project_counter START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_task_counter START WITH 1 INCREMENT BY 1;

-- ===================================================================
-- DOMAIN COMPLETION MARKER
-- ===================================================================
COMMENT ON TABLE projects IS 'Business Domain: Project management and organization';
COMMENT ON TABLE task_categories IS 'Business Domain: Task categorization and classification';
COMMENT ON TABLE task_statuses IS 'Business Domain: Task workflow status management';
COMMENT ON TABLE tasks IS 'Business Domain: Core task management and tracking';
COMMENT ON TABLE project_members IS 'Business Domain: Project team membership';
COMMENT ON TABLE task_watchers IS 'Business Domain: Task subscription and notifications';
COMMENT ON TABLE task_links IS 'Business Domain: Inter-task relationships and dependencies';
COMMENT ON TABLE task_history IS 'Business Domain: Task change audit trail';

-- ===================================================================
-- END OF BUSINESS DOMAIN SCRIPT
-- ===================================================================
