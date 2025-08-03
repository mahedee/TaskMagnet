# Database Schema Design - TaskMagnet
**Modular Monolith Architecture with Enterprise Security**

---

## 📋 **Document Information**
- **Version**: 3.0
- **Date**: August 3, 2025
- **Status**: Updated for Modular Monolith Design
- **Database**: Oracle XE 21c
- **Architecture**: Modular Monolith with clear domain boundaries

---

## 🗄️ **Database Architecture Overview**

### **Domain-Based Organization**
The database schema is organized into logical domains that align with the modular monolith architecture:

1. **Security Domain**: Authentication, authorization, and audit
2. **User Management Domain**: User profiles, preferences, and organizations
3. **Project Management Domain**: Projects, templates, and planning
4. **Task Management Domain**: Tasks, categories, statuses, and workflows
5. **Collaboration Domain**: Comments, attachments, and notifications
6. **Reporting Domain**: Analytics, dashboards, and metrics

### **Key Design Principles**
- **Domain Boundaries**: Clear separation between functional domains
- **Microservices Ready**: Schema designed for future data partitioning
- **Audit Everything**: Comprehensive audit trails across all domains
- **Performance Optimized**: Strategic indexing and query optimization
- **Data Integrity**: Strong referential integrity with foreign key constraints

---

### **Core Authentication & Authorization Tables**

```plaintext
Table: users
- id (Primary Key, NUMBER)
- username (VARCHAR2(50), UNIQUE)
- email (VARCHAR2(100), UNIQUE)
- password (VARCHAR2(255)) -- BCrypt hashed
- first_name (VARCHAR2(50))
- last_name (VARCHAR2(50))
- is_active (NUMBER(1), DEFAULT 1)
- email_verified (NUMBER(1), DEFAULT 0)
- phone (VARCHAR2(20))
- profile_image_url (VARCHAR2(500))
- timezone (VARCHAR2(50), DEFAULT 'UTC')
- language (VARCHAR2(10), DEFAULT 'en')
- last_login (TIMESTAMP)
- password_changed_at (TIMESTAMP)
- failed_login_attempts (NUMBER, DEFAULT 0)
- account_locked_until (TIMESTAMP)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- updated_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)
- updated_by (NUMBER, FK to users.id)

Table: roles
- id (Primary Key, NUMBER)
- name (VARCHAR2(50), UNIQUE) -- ROLE_SUPER_ADMIN, ROLE_ADMIN, etc.
- display_name (VARCHAR2(100))
- description (VARCHAR2(500))
- hierarchy_level (NUMBER, DEFAULT 0) -- Higher = more privileges
- is_system (NUMBER(1), DEFAULT 0) -- System roles cannot be deleted
- color (VARCHAR2(7)) -- Hex color for UI
- max_users (NUMBER) -- Limit number of users
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- updated_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)
- updated_by (NUMBER, FK to users.id)

Table: permissions
- id (Primary Key, NUMBER)
- name (VARCHAR2(100), UNIQUE) -- USER_CREATE, TASK_UPDATE, etc.
- resource_type (VARCHAR2(50)) -- USER, TASK, PROJECT, SYSTEM, REPORT
- action (VARCHAR2(50)) -- CREATE, READ, UPDATE, DELETE, EXECUTE, APPROVE
- description (VARCHAR2(500))
- is_system (NUMBER(1), DEFAULT 0)
- category (VARCHAR2(50)) -- Grouping permissions
- risk_level (VARCHAR2(20), DEFAULT 'LOW') -- LOW, MEDIUM, HIGH, CRITICAL
- requires_approval (NUMBER(1), DEFAULT 0)
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Table: user_roles (Many-to-Many: Users ↔ Roles)
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- role_id (NUMBER, FK to roles.id)
- assigned_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- assigned_by (NUMBER, FK to users.id)
- expires_at (TIMESTAMP) -- Optional expiration
- is_active (NUMBER(1), DEFAULT 1)
- notes (VARCHAR2(500))

Table: role_permissions (Many-to-Many: Roles ↔ Permissions)
- id (Primary Key, NUMBER)
- role_id (NUMBER, FK to roles.id)
- permission_id (NUMBER, FK to permissions.id)
- granted_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- granted_by (NUMBER, FK to users.id)
- is_active (NUMBER(1), DEFAULT 1)

Table: user_permissions (Direct User Permissions - Overrides)
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- permission_id (NUMBER, FK to permissions.id)
- resource_id (NUMBER) -- Specific resource instance
- resource_type (VARCHAR2(50)) -- Type of the specific resource
- permission_type (VARCHAR2(10)) -- GRANT or DENY
- granted_by (NUMBER, FK to users.id)
- granted_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- expires_at (TIMESTAMP)
- reason (VARCHAR2(500))
- is_active (NUMBER(1), DEFAULT 1)
```

### **Security & Audit Tables**

```plaintext
Table: security_audit_log
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- action (VARCHAR2(100)) -- LOGIN, LOGOUT, PERMISSION_CHANGE, etc.
- resource_type (VARCHAR2(50))
- resource_id (NUMBER)
- old_values (CLOB) -- JSON of old values
- new_values (CLOB) -- JSON of new values
- ip_address (VARCHAR2(45)) -- IPv4/IPv6
- user_agent (VARCHAR2(1000))
- session_id (VARCHAR2(255))
- status (VARCHAR2(20)) -- SUCCESS, FAILED, BLOCKED, WARNING
- risk_score (NUMBER(3,2)) -- 0.00 to 10.00
- details (VARCHAR2(2000))
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Table: refresh_tokens
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- token (VARCHAR2(255), UNIQUE)
- expires_at (TIMESTAMP)
- is_revoked (NUMBER(1), DEFAULT 0)
- revoked_at (TIMESTAMP)
- revoked_reason (VARCHAR2(100))
- ip_address (VARCHAR2(45))
- user_agent (VARCHAR2(1000))
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Table: password_history
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- password_hash (VARCHAR2(255))
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- is_current (NUMBER(1), DEFAULT 0)

Table: login_attempts
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- username (VARCHAR2(50)) -- For failed attempts with invalid username
- ip_address (VARCHAR2(45))
- user_agent (VARCHAR2(1000))
- attempted_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- status (VARCHAR2(20)) -- SUCCESS, FAILED, BLOCKED
- failure_reason (VARCHAR2(100))
- failure_count (NUMBER, DEFAULT 1)

Table: user_sessions
- id (Primary Key, NUMBER)
- user_id (NUMBER, FK to users.id)
- session_token (VARCHAR2(255))
- ip_address (VARCHAR2(45))
- user_agent (VARCHAR2(1000))
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- last_activity (TIMESTAMP)
- expires_at (TIMESTAMP)
- is_active (NUMBER(1), DEFAULT 1)
```

### **Business Domain Tables**

```plaintext
Table: projects
- id (Primary Key, NUMBER)
- name (VARCHAR2(100))
- key (VARCHAR2(10), UNIQUE) -- Project key like 'TM'
- description (VARCHAR2(2000))
- project_lead_id (NUMBER, FK to users.id)
- status (VARCHAR2(20)) -- ACTIVE, INACTIVE, ARCHIVED
- start_date (DATE)
- end_date (DATE)
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- updated_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)
- updated_by (NUMBER, FK to users.id)

Table: tasks
- id (Primary Key, NUMBER)
- project_id (NUMBER, FK to projects.id)
- title (VARCHAR2(255))
- description (CLOB)
- task_key (VARCHAR2(20), UNIQUE) -- TM-001, TM-002, etc.
- task_type (VARCHAR2(20)) -- STORY, TASK, BUG, EPIC
- priority (VARCHAR2(10)) -- LOW, MEDIUM, HIGH, CRITICAL
- status_id (NUMBER, FK to task_statuses.id)
- category_id (NUMBER, FK to task_categories.id)
- assignee_id (NUMBER, FK to users.id)
- reporter_id (NUMBER, FK to users.id)
- parent_task_id (NUMBER, FK to tasks.id) -- For subtasks
- story_points (NUMBER)
- due_date (DATE)
- start_date (DATE)
- estimated_hours (NUMBER(5,2))
- actual_hours (NUMBER(5,2))
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- updated_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)
- updated_by (NUMBER, FK to users.id)

Table: task_categories
- id (Primary Key, NUMBER)
- name (VARCHAR2(50))
- description (VARCHAR2(255))
- color (VARCHAR2(7)) -- Hex color
- project_id (NUMBER, FK to projects.id)
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)

Table: task_statuses
- id (Primary Key, NUMBER)
- name (VARCHAR2(50))
- description (VARCHAR2(255))
- color (VARCHAR2(7)) -- Hex color
- order_index (NUMBER) -- For display ordering
- is_closed_status (NUMBER(1), DEFAULT 0)
- project_id (NUMBER, FK to projects.id)
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)

Table: comments
- id (Primary Key, NUMBER)
- task_id (NUMBER, FK to tasks.id)
- user_id (NUMBER, FK to users.id)
- comment_text (CLOB)
- parent_comment_id (NUMBER, FK to comments.id) -- For reply threads
- is_edited (NUMBER(1), DEFAULT 0)
- edited_at (TIMESTAMP)
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- updated_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Table: attachments
- id (Primary Key, NUMBER)
- task_id (NUMBER, FK to tasks.id)
- user_id (NUMBER, FK to users.id)
- file_name (VARCHAR2(255))
- file_path (VARCHAR2(500))
- file_size (NUMBER)
- file_type (VARCHAR2(50))
- mime_type (VARCHAR2(100))
- is_active (NUMBER(1), DEFAULT 1)
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Table: project_members
- id (Primary Key, NUMBER)
- project_id (NUMBER, FK to projects.id)
- user_id (NUMBER, FK to users.id)
- role (VARCHAR2(50)) -- PROJECT_LEAD, DEVELOPER, TESTER, VIEWER
- joined_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- is_active (NUMBER(1), DEFAULT 1)
- added_by (NUMBER, FK to users.id)

Table: task_watchers
- id (Primary Key, NUMBER)
- task_id (NUMBER, FK to tasks.id)
- user_id (NUMBER, FK to users.id)
- watch_type (VARCHAR2(20)) -- WATCHING, IGNORING
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Table: task_links
- id (Primary Key, NUMBER)
- source_task_id (NUMBER, FK to tasks.id)
- target_task_id (NUMBER, FK to tasks.id)
- link_type (VARCHAR2(20)) -- BLOCKS, DUPLICATES, RELATES_TO, SUBTASK_OF
- created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- created_by (NUMBER, FK to users.id)
```

---

## 🔗 **Key Relationships**

### **Authentication & Authorization Relationships**
- Users (1) ↔ (M) User_Roles (M) ↔ (1) Roles
- Roles (1) ↔ (M) Role_Permissions (M) ↔ (1) Permissions  
- Users (1) ↔ (M) User_Permissions (M) ↔ (1) Permissions
- Users (1) ↔ (M) Security_Audit_Log
- Users (1) ↔ (M) Refresh_Tokens
- Users (1) ↔ (M) Password_History
- Users (1) ↔ (M) Login_Attempts
- Users (1) ↔ (M) User_Sessions

### **Business Domain Relationships**
- Projects (1) ↔ (M) Tasks
- Projects (1) ↔ (M) Project_Members (M) ↔ (1) Users
- Tasks (1) ↔ (M) Comments
- Tasks (1) ↔ (M) Attachments
- Tasks (1) ↔ (M) Task_Watchers (M) ↔ (1) Users
- Tasks (1) ↔ (M) Task_Links ↔ (1) Tasks
- Task_Categories (1) ↔ (M) Tasks
- Task_Statuses (1) ↔ (M) Tasks
- Users (1) ↔ (M) Tasks (as assignee, reporter, creator)

---

## 📈 **Performance Indexes**

### **Authentication & Security Indexes**
```sql
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
```

### **Business Domain Indexes**
```sql
-- Project and task indexes
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_tasks_reporter ON tasks(reporter_id);
CREATE INDEX idx_tasks_status ON tasks(status_id);
CREATE INDEX idx_tasks_category ON tasks(category_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_key ON tasks(task_key);

-- Comment and attachment indexes
CREATE INDEX idx_comments_task ON comments(task_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_attachments_task ON attachments(task_id);
CREATE INDEX idx_attachments_user ON attachments(user_id);

-- Project member indexes
CREATE INDEX idx_project_members_project ON project_members(project_id);
CREATE INDEX idx_project_members_user ON project_members(user_id);
```

---

**Document Status**: Updated with comprehensive security and business schema  
**Next Step**: Implement database creation scripts  
**Database Version**: Oracle XE 21c Compatible
```
- UserID (Foreign Key, references User.UserID)
- RoleID (Foreign Key, references Role.RoleID)
```

Explanation:

- The `Role` table is added to store different roles.
- The `UserRole` table serves as a junction table to establish a many-to-many relationship between users and roles.
- The `UserRole` table includes `UserID` and `RoleID` foreign keys referencing the `User` and `Role` tables, respectively.

Now, a user can have multiple roles, and a role can be assigned to multiple users. This allows for more flexibility in managing user roles in your Task Management System. Adjust the schema based on your specific needs and requirements.