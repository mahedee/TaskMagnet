-- TaskMagnet Database Schema Migration
-- Version: V1__Create_initial_schema.sql
-- Description: Creates the initial database schema for TaskMagnet application
-- Author: Mahedee Hasan
-- Date: 2025-01-08

-- Create sequence for primary keys
CREATE SEQUENCE taskmagnet_seq START WITH 1 INCREMENT BY 1;

-- Create users table
CREATE TABLE users (
    id NUMBER(19) NOT NULL,
    username VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL,
    phone_number VARCHAR2(20),
    department VARCHAR2(100),
    job_title VARCHAR2(100),
    is_email_verified NUMBER(1) DEFAULT 0 NOT NULL,
    email_verification_token VARCHAR2(255),
    email_verification_token_expiry TIMESTAMP,
    password_reset_token VARCHAR2(255),
    password_reset_token_expiry TIMESTAMP,
    last_login_date TIMESTAMP,
    failed_login_attempts NUMBER(10) DEFAULT 0 NOT NULL,
    account_locked_until TIMESTAMP,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR2(50) NOT NULL,
    modified_by VARCHAR2(50) NOT NULL,
    is_active NUMBER(1) DEFAULT 1 NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_email_verified CHECK (is_email_verified IN (0, 1)),
    CONSTRAINT ck_users_active CHECK (is_active IN (0, 1))
);

-- Create categories table
CREATE TABLE categories (
    id NUMBER(19) NOT NULL,
    name VARCHAR2(100) NOT NULL,
    description VARCHAR2(500),
    color_code VARCHAR2(7),
    icon VARCHAR2(50),
    sort_order NUMBER(10) DEFAULT 0 NOT NULL,
    parent_id NUMBER(19),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR2(50) NOT NULL,
    modified_by VARCHAR2(50) NOT NULL,
    is_active NUMBER(1) DEFAULT 1 NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_categories_name_parent UNIQUE (name, parent_id),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id),
    CONSTRAINT ck_categories_active CHECK (is_active IN (0, 1))
);

-- Create projects table
CREATE TABLE projects (
    id NUMBER(19) NOT NULL,
    name VARCHAR2(200) NOT NULL,
    code VARCHAR2(20) NOT NULL,
    description VARCHAR2(1000),
    status VARCHAR2(20) DEFAULT 'PLANNING' NOT NULL,
    start_date DATE,
    end_date DATE,
    target_completion_date DATE,
    actual_completion_date DATE,
    budget NUMBER(19,2),
    color_code VARCHAR2(7),
    progress_percentage NUMBER(10) DEFAULT 0 NOT NULL,
    owner_id NUMBER(19) NOT NULL,
    category_id NUMBER(19),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR2(50) NOT NULL,
    modified_by VARCHAR2(50) NOT NULL,
    is_active NUMBER(1) DEFAULT 1 NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT uk_projects_code UNIQUE (code),
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_projects_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT ck_projects_status CHECK (status IN ('PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED', 'ARCHIVED')),
    CONSTRAINT ck_projects_progress CHECK (progress_percentage >= 0 AND progress_percentage <= 100),
    CONSTRAINT ck_projects_active CHECK (is_active IN (0, 1))
);

-- Create tasks table
CREATE TABLE tasks (
    id NUMBER(19) NOT NULL,
    title VARCHAR2(200) NOT NULL,
    description VARCHAR2(2000),
    status VARCHAR2(20) DEFAULT 'NOT_STARTED' NOT NULL,
    priority VARCHAR2(20) DEFAULT 'MEDIUM' NOT NULL,
    due_date DATE,
    start_date DATE,
    completion_date TIMESTAMP,
    estimated_hours NUMBER(5,2),
    actual_hours NUMBER(5,2),
    progress_percentage NUMBER(10) DEFAULT 0 NOT NULL,
    notes VARCHAR2(1000),
    tags VARCHAR2(500),
    is_billable NUMBER(1) DEFAULT 0 NOT NULL,
    billable_hours NUMBER(5,2),
    billable_rate NUMBER(10,2),
    assigned_to_id NUMBER(19),
    created_by_id NUMBER(19) NOT NULL,
    project_id NUMBER(19),
    category_id NUMBER(19),
    parent_task_id NUMBER(19),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR2(50) NOT NULL,
    modified_by VARCHAR2(50) NOT NULL,
    is_active NUMBER(1) DEFAULT 1 NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_tasks_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_task_id) REFERENCES tasks(id),
    CONSTRAINT ck_tasks_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED', 'BLOCKED', 'REVIEW', 'APPROVED')),
    CONSTRAINT ck_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT', 'CRITICAL')),
    CONSTRAINT ck_tasks_progress CHECK (progress_percentage >= 0 AND progress_percentage <= 100),
    CONSTRAINT ck_tasks_billable CHECK (is_billable IN (0, 1)),
    CONSTRAINT ck_tasks_active CHECK (is_active IN (0, 1))
);

-- Create project_members table (many-to-many relationship)
CREATE TABLE project_members (
    project_id NUMBER(19) NOT NULL,
    user_id NUMBER(19) NOT NULL,
    CONSTRAINT pk_project_members PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_project_members_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(is_active);

CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_categories_active ON categories(is_active);

CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_projects_category ON projects(category_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_active ON projects(is_active);

CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to_id);
CREATE INDEX idx_tasks_created_by ON tasks(created_by_id);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_category ON tasks(category_id);
CREATE INDEX idx_tasks_parent ON tasks(parent_task_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_active ON tasks(is_active);

-- Create triggers for updating modified_date
CREATE OR REPLACE TRIGGER trg_users_modified_date
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    :NEW.modified_date := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_categories_modified_date
    BEFORE UPDATE ON categories
    FOR EACH ROW
BEGIN
    :NEW.modified_date := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_projects_modified_date
    BEFORE UPDATE ON projects
    FOR EACH ROW
BEGIN
    :NEW.modified_date := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_tasks_modified_date
    BEFORE UPDATE ON tasks
    FOR EACH ROW
BEGIN
    :NEW.modified_date := CURRENT_TIMESTAMP;
END;
/

-- Insert default categories
INSERT INTO categories (id, name, description, color_code, icon, sort_order, created_by, modified_by)
VALUES (taskmagnet_seq.NEXTVAL, 'General', 'General category for uncategorized items', '#6B7280', 'folder', 1, 'system', 'system');

INSERT INTO categories (id, name, description, color_code, icon, sort_order, created_by, modified_by)
VALUES (taskmagnet_seq.NEXTVAL, 'Development', 'Software development related tasks', '#3B82F6', 'code', 2, 'system', 'system');

INSERT INTO categories (id, name, description, color_code, icon, sort_order, created_by, modified_by)
VALUES (taskmagnet_seq.NEXTVAL, 'Design', 'Design and UI/UX related tasks', '#8B5CF6', 'palette', 3, 'system', 'system');

INSERT INTO categories (id, name, description, color_code, icon, sort_order, created_by, modified_by)
VALUES (taskmagnet_seq.NEXTVAL, 'Testing', 'Quality assurance and testing tasks', '#10B981', 'check-circle', 4, 'system', 'system');

INSERT INTO categories (id, name, description, color_code, icon, sort_order, created_by, modified_by)
VALUES (taskmagnet_seq.NEXTVAL, 'Documentation', 'Documentation and knowledge management', '#F59E0B', 'document-text', 5, 'system', 'system');

-- Create default admin user
INSERT INTO users (
    id, username, email, password, first_name, last_name, 
    is_email_verified, created_by, modified_by
) VALUES (
    taskmagnet_seq.NEXTVAL, 'admin', 'admin@taskmagnet.com', 
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHMzZD6m', -- password: secret
    'System', 'Administrator', 1, 'system', 'system'
);

COMMIT;
