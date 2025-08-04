-- ===================================================================
-- TaskMagnet Database Schema - Domain 3: Collaboration Domain
-- Database: Oracle XE 21c
-- Version: 1.0
-- Created: August 4, 2025
-- Purpose: Comments, Attachments, and Collaboration Domain Tables
-- ===================================================================

-- ===================================================================
-- 1. COMMENTS TABLE (Task Discussion and Communication)
-- ===================================================================
CREATE TABLE comments (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    task_id NUMBER(19,0) NOT NULL,
    user_id NUMBER(19,0) NOT NULL,
    comment_text CLOB NOT NULL,
    parent_comment_id NUMBER(19,0),
    is_edited NUMBER(1) DEFAULT 0,
    edited_at TIMESTAMP,
    edited_by NUMBER(19,0),
    is_active NUMBER(1) DEFAULT 1,
    is_internal NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_comments PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_comments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_edited_by FOREIGN KEY (edited_by) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_comments_edited CHECK (is_edited IN (0, 1)),
    CONSTRAINT chk_comments_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_comments_internal CHECK (is_internal IN (0, 1)),
    CONSTRAINT chk_comments_edit_logic CHECK (
        (is_edited = 0 AND edited_at IS NULL AND edited_by IS NULL) OR
        (is_edited = 1 AND edited_at IS NOT NULL AND edited_by IS NOT NULL)
    )
);

-- ===================================================================
-- 2. ATTACHMENTS TABLE (File Management System)
-- ===================================================================
CREATE TABLE attachments (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    task_id NUMBER(19,0),
    comment_id NUMBER(19,0),
    user_id NUMBER(19,0) NOT NULL,
    file_name VARCHAR2(255) NOT NULL,
    original_file_name VARCHAR2(255) NOT NULL,
    file_path VARCHAR2(500) NOT NULL,
    file_size NUMBER(15,0) NOT NULL,
    file_type VARCHAR2(50),
    mime_type VARCHAR2(100),
    file_hash VARCHAR2(64),
    download_count NUMBER(10,0) DEFAULT 0,
    is_active NUMBER(1) DEFAULT 1,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_attachments PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_attachments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_attachments_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CONSTRAINT fk_attachments_user FOREIGN KEY (user_id) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_attachments_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_attachments_size CHECK (file_size > 0),
    CONSTRAINT chk_attachments_download_count CHECK (download_count >= 0),
    CONSTRAINT chk_attachments_parent CHECK (
        (task_id IS NOT NULL AND comment_id IS NULL) OR
        (task_id IS NULL AND comment_id IS NOT NULL)
    )
);

-- ===================================================================
-- 3. NOTIFICATIONS TABLE (User Notification System)
-- ===================================================================
CREATE TABLE notifications (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    type VARCHAR2(50) NOT NULL,
    title VARCHAR2(255) NOT NULL,
    message VARCHAR2(1000) NOT NULL,
    related_entity_type VARCHAR2(50),
    related_entity_id NUMBER(19,0),
    is_read NUMBER(1) DEFAULT 0,
    is_email_sent NUMBER(1) DEFAULT 0,
    email_sent_at TIMESTAMP,
    priority VARCHAR2(10) DEFAULT 'MEDIUM',
    action_url VARCHAR2(500),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_notifications_read CHECK (is_read IN (0, 1)),
    CONSTRAINT chk_notifications_email_sent CHECK (is_email_sent IN (0, 1)),
    CONSTRAINT chk_notifications_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    CONSTRAINT chk_notifications_type CHECK (type IN (
        'TASK_ASSIGNED', 'TASK_UPDATED', 'TASK_COMPLETED', 'TASK_OVERDUE',
        'COMMENT_ADDED', 'MENTION', 'PROJECT_INVITATION', 'DEADLINE_REMINDER',
        'STATUS_CHANGED', 'PRIORITY_CHANGED', 'ASSIGNEE_CHANGED'
    )),
    CONSTRAINT chk_notifications_entity_type CHECK (related_entity_type IN (
        'TASK', 'PROJECT', 'COMMENT', 'USER', 'ATTACHMENT'
    ))
);

-- ===================================================================
-- 4. MENTIONS TABLE (User Mention System in Comments)
-- ===================================================================
CREATE TABLE mentions (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    comment_id NUMBER(19,0) NOT NULL,
    mentioned_user_id NUMBER(19,0) NOT NULL,
    mentioned_by_user_id NUMBER(19,0) NOT NULL,
    is_read NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_mentions PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_mentions_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CONSTRAINT fk_mentions_mentioned_user FOREIGN KEY (mentioned_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_mentions_mentioning_user FOREIGN KEY (mentioned_by_user_id) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_mentions_comment_user UNIQUE (comment_id, mentioned_user_id),
    
    -- Check Constraints
    CONSTRAINT chk_mentions_read CHECK (is_read IN (0, 1)),
    CONSTRAINT chk_mentions_different_users CHECK (mentioned_user_id != mentioned_by_user_id)
);

-- ===================================================================
-- 5. ACTIVITY_FEED TABLE (Project and Task Activity Tracking)
-- ===================================================================
CREATE TABLE activity_feed (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    user_id NUMBER(19,0) NOT NULL,
    activity_type VARCHAR2(50) NOT NULL,
    entity_type VARCHAR2(50) NOT NULL,
    entity_id NUMBER(19,0) NOT NULL,
    project_id NUMBER(19,0),
    description VARCHAR2(500) NOT NULL,
    metadata CLOB,
    is_public NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_activity_feed PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_activity_feed_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_activity_feed_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_activity_feed_public CHECK (is_public IN (0, 1)),
    CONSTRAINT chk_activity_feed_type CHECK (activity_type IN (
        'TASK_CREATED', 'TASK_UPDATED', 'TASK_DELETED', 'TASK_ASSIGNED',
        'TASK_COMPLETED', 'COMMENT_ADDED', 'COMMENT_UPDATED', 'COMMENT_DELETED',
        'ATTACHMENT_UPLOADED', 'ATTACHMENT_DELETED', 'PROJECT_CREATED',
        'PROJECT_UPDATED', 'USER_JOINED_PROJECT', 'USER_LEFT_PROJECT'
    )),
    CONSTRAINT chk_activity_feed_entity_type CHECK (entity_type IN (
        'TASK', 'PROJECT', 'COMMENT', 'ATTACHMENT', 'USER'
    ))
);

-- ===================================================================
-- 6. COLLABORATION_SPACES TABLE (Project Communication Channels)
-- ===================================================================
CREATE TABLE collaboration_spaces (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    project_id NUMBER(19,0) NOT NULL,
    name VARCHAR2(100) NOT NULL,
    description VARCHAR2(500),
    space_type VARCHAR2(20) DEFAULT 'GENERAL',
    is_private NUMBER(1) DEFAULT 0,
    created_by NUMBER(19,0) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active NUMBER(1) DEFAULT 1,
    
    -- Primary Key
    CONSTRAINT pk_collaboration_spaces PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_collaboration_spaces_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_collaboration_spaces_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_collaboration_spaces_project_name UNIQUE (project_id, name),
    
    -- Check Constraints
    CONSTRAINT chk_collaboration_spaces_private CHECK (is_private IN (0, 1)),
    CONSTRAINT chk_collaboration_spaces_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_collaboration_spaces_type CHECK (space_type IN (
        'GENERAL', 'ANNOUNCEMENTS', 'BUGS', 'FEATURE_REQUESTS', 'DISCUSSION'
    ))
);

-- ===================================================================
-- 7. SPACE_MESSAGES TABLE (Messages within Collaboration Spaces)
-- ===================================================================
CREATE TABLE space_messages (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    space_id NUMBER(19,0) NOT NULL,
    user_id NUMBER(19,0) NOT NULL,
    message_text CLOB NOT NULL,
    parent_message_id NUMBER(19,0),
    is_edited NUMBER(1) DEFAULT 0,
    edited_at TIMESTAMP,
    is_pinned NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_space_messages PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_space_messages_space FOREIGN KEY (space_id) REFERENCES collaboration_spaces(id) ON DELETE CASCADE,
    CONSTRAINT fk_space_messages_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_space_messages_parent FOREIGN KEY (parent_message_id) REFERENCES space_messages(id) ON DELETE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_space_messages_edited CHECK (is_edited IN (0, 1)),
    CONSTRAINT chk_space_messages_pinned CHECK (is_pinned IN (0, 1))
);

-- ===================================================================
-- 8. TASK_TAGS TABLE (Flexible Task Tagging System)
-- ===================================================================
CREATE TABLE task_tags (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    name VARCHAR2(50) NOT NULL,
    color VARCHAR2(7) DEFAULT '#6b7280',
    project_id NUMBER(19,0),
    created_by NUMBER(19,0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active NUMBER(1) DEFAULT 1,
    
    -- Primary Key
    CONSTRAINT pk_task_tags PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_tags_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tags_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_task_tags_name_project UNIQUE (name, project_id),
    
    -- Check Constraints
    CONSTRAINT chk_task_tags_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_task_tags_color CHECK (REGEXP_LIKE(color, '^#[0-9A-Fa-f]{6}$'))
);

-- ===================================================================
-- 9. TASK_TAG_ASSIGNMENTS TABLE (Many-to-Many: Tasks ↔ Tags)
-- ===================================================================
CREATE TABLE task_tag_assignments (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    task_id NUMBER(19,0) NOT NULL,
    tag_id NUMBER(19,0) NOT NULL,
    assigned_by NUMBER(19,0),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Primary Key
    CONSTRAINT pk_task_tag_assignments PRIMARY KEY (id),
    
    -- Foreign Key Constraints
    CONSTRAINT fk_task_tag_assignments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tag_assignments_tag FOREIGN KEY (tag_id) REFERENCES task_tags(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tag_assignments_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(id),
    
    -- Unique Constraints
    CONSTRAINT uk_task_tag_assignments_task_tag UNIQUE (task_id, tag_id)
);

-- ===================================================================
-- COLLABORATION DOMAIN INDEXES (Performance Optimization)
-- ===================================================================

-- Comment indexes
CREATE INDEX idx_comments_task ON comments(task_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);
CREATE INDEX idx_comments_parent ON comments(parent_comment_id);

-- Attachment indexes
CREATE INDEX idx_attachments_task ON attachments(task_id);
CREATE INDEX idx_attachments_comment ON attachments(comment_id);
CREATE INDEX idx_attachments_user ON attachments(user_id);
CREATE INDEX idx_attachments_uploaded_at ON attachments(uploaded_at);
CREATE INDEX idx_attachments_file_type ON attachments(file_type);

-- Notification indexes
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_entity ON notifications(related_entity_type, related_entity_id);

-- Mention indexes
CREATE INDEX idx_mentions_comment ON mentions(comment_id);
CREATE INDEX idx_mentions_user ON mentions(mentioned_user_id);
CREATE INDEX idx_mentions_read ON mentions(is_read);

-- Activity feed indexes
CREATE INDEX idx_activity_feed_user ON activity_feed(user_id);
CREATE INDEX idx_activity_feed_project ON activity_feed(project_id);
CREATE INDEX idx_activity_feed_created_at ON activity_feed(created_at);
CREATE INDEX idx_activity_feed_entity ON activity_feed(entity_type, entity_id);

-- Collaboration space indexes
CREATE INDEX idx_collaboration_spaces_project ON collaboration_spaces(project_id);
CREATE INDEX idx_space_messages_space ON space_messages(space_id);
CREATE INDEX idx_space_messages_user ON space_messages(user_id);
CREATE INDEX idx_space_messages_created_at ON space_messages(created_at);

-- Tag indexes
CREATE INDEX idx_task_tags_project ON task_tags(project_id);
CREATE INDEX idx_task_tag_assignments_task ON task_tag_assignments(task_id);
CREATE INDEX idx_task_tag_assignments_tag ON task_tag_assignments(tag_id);

-- ===================================================================
-- DOMAIN COMPLETION MARKER
-- ===================================================================
COMMENT ON TABLE comments IS 'Collaboration Domain: Task discussion and communication';
COMMENT ON TABLE attachments IS 'Collaboration Domain: File attachment and document management';
COMMENT ON TABLE notifications IS 'Collaboration Domain: User notification and alert system';
COMMENT ON TABLE mentions IS 'Collaboration Domain: User mention system in comments';
COMMENT ON TABLE activity_feed IS 'Collaboration Domain: Project and task activity tracking';
COMMENT ON TABLE collaboration_spaces IS 'Collaboration Domain: Project communication channels';
COMMENT ON TABLE space_messages IS 'Collaboration Domain: Messages within collaboration spaces';
COMMENT ON TABLE task_tags IS 'Collaboration Domain: Flexible task tagging system';
COMMENT ON TABLE task_tag_assignments IS 'Collaboration Domain: Task and tag associations';

-- ===================================================================
-- END OF COLLABORATION DOMAIN SCRIPT
-- ===================================================================
