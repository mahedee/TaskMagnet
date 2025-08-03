# TaskMagnet - Individual Assignable Task List

## Project Overview
This document contains all individual assignable tasks for TaskMagnet project with dependencies and priority levels.

---

## Phase 1: Foundation & Setup

### TASK-000: Create a database in Oracle
- **Description**: Set up the initial database in Oracle
- **Dependencies**: None
- **Assignee**: Database Administrator
- **Estimated Hours**: 4
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Install Oracle Database
  - [ ] Create initial schema
  - [ ] Set up user roles and permissions

### TASK-001: Project Setup & Configuration
- **Description**: Complete project setup and basic configuration
- **Dependencies**: None
- **Assignee**: Lead Developer
- **Estimated Hours**: 8
- **Priority**: Critical
- **Status**: ✅ Completed
- **Subtasks**:
  - [x] Spring Boot project initialization
  - [x] Maven dependencies configuration
  - [x] MySQL database setup
  - [x] JWT authentication setup

### TASK-002: Database Schema Design
- **Description**: Design and implement complete database schema
- **Dependencies**: TASK-001
- **Assignee**: Backend Developer
- **Estimated Hours**: 12
- **Priority**: High
- **Status**: 🔄 In Progress
- **Subtasks**:
  - [ ] User entity design
  - [ ] Task entity design
  - [ ] Category entity design
  - [ ] Comment entity design
  - [ ] Attachment entity design
  - [ ] Relationship mapping

### TASK-003: Core Entity Models
- **Description**: Create JPA entities for all core models
- **Dependencies**: TASK-002
- **Assignee**: Backend Developer
- **Estimated Hours**: 16
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] User entity implementation
  - [ ] Task entity implementation
  - [ ] Category entity implementation
  - [ ] Comment entity implementation
  - [ ] Attachment entity implementation
  - [ ] Entity validation annotations

---

## Phase 2: Backend API Development

### TASK-004: User Management APIs
- **Description**: Implement user-related REST APIs
- **Dependencies**: TASK-003
- **Assignee**: Backend Developer
- **Estimated Hours**: 20
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] User registration endpoint
  - [ ] User login endpoint
  - [ ] User profile endpoints
  - [ ] Password reset functionality
  - [ ] User role management

### TASK-005: Task Management APIs
- **Description**: Implement task CRUD operations
- **Dependencies**: TASK-003, TASK-004
- **Assignee**: Backend Developer
- **Estimated Hours**: 24
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Create task endpoint
  - [ ] Get tasks endpoints (by user, project, status)
  - [ ] Update task endpoint
  - [ ] Delete task endpoint
  - [ ] Task assignment functionality
  - [ ] Task status management

### TASK-006: Category Management APIs
- **Description**: Implement category management system
- **Dependencies**: TASK-003
- **Assignee**: Backend Developer
- **Estimated Hours**: 12
- **Priority**: Medium
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Create category endpoint
  - [ ] List categories endpoint
  - [ ] Update category endpoint
  - [ ] Delete category endpoint

### TASK-007: Comment System APIs
- **Description**: Implement commenting functionality
- **Dependencies**: TASK-005
- **Assignee**: Backend Developer
- **Estimated Hours**: 16
- **Priority**: Medium
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Add comment endpoint
  - [ ] Get comments for task
  - [ ] Update comment endpoint
  - [ ] Delete comment endpoint

---

## Phase 3: Frontend Development

### TASK-008: React Project Setup
- **Description**: Initialize React frontend with Redux
- **Dependencies**: TASK-001
- **Assignee**: Frontend Developer
- **Estimated Hours**: 10
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Create React app
  - [ ] Redux store setup
  - [ ] React Router configuration
  - [ ] Axios setup for API calls
  - [ ] Basic project structure

### TASK-009: Authentication UI
- **Description**: Create login and registration components
- **Dependencies**: TASK-008, TASK-004
- **Assignee**: Frontend Developer
- **Estimated Hours**: 16
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Login component
  - [ ] Registration component
  - [ ] JWT token management
  - [ ] Protected routes
  - [ ] User session handling

### TASK-010: Dashboard Component
- **Description**: Create main dashboard interface
- **Dependencies**: TASK-009
- **Assignee**: Frontend Developer
- **Estimated Hours**: 20
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Dashboard layout
  - [ ] Task overview cards
  - [ ] Recent activity feed
  - [ ] Quick actions menu
  - [ ] Navigation sidebar

### TASK-011: Task Management UI
- **Description**: Create task management interface
- **Dependencies**: TASK-010, TASK-005
- **Assignee**: Frontend Developer
- **Estimated Hours**: 28
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Task list component
  - [ ] Task creation form
  - [ ] Task detail view
  - [ ] Task editing interface
  - [ ] Task filtering and sorting
  - [ ] Task assignment interface

---

## Phase 4: Advanced Features

### TASK-012: Search Functionality
- **Description**: Implement search across tasks and projects
- **Dependencies**: TASK-005, TASK-011
- **Assignee**: Full Stack Developer
- **Estimated Hours**: 16
- **Priority**: Medium
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Backend search API
  - [ ] Search UI component
  - [ ] Advanced filters
  - [ ] Search optimization

### TASK-013: File Attachment System
- **Description**: Implement file upload and management
- **Dependencies**: TASK-007
- **Assignee**: Full Stack Developer
- **Estimated Hours**: 20
- **Priority**: Medium
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] File upload API
  - [ ] File storage configuration
  - [ ] File download endpoint
  - [ ] File management UI
  - [ ] File type validation

### TASK-014: Notification System
- **Description**: Implement user notifications
- **Dependencies**: TASK-005, TASK-011
- **Assignee**: Full Stack Developer
- **Estimated Hours**: 24
- **Priority**: Low
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Notification backend service
  - [ ] Email notification setup
  - [ ] In-app notification UI
  - [ ] Notification preferences

---

## Phase 5: Testing & Deployment

### TASK-015: Backend Testing
- **Description**: Implement comprehensive backend tests
- **Dependencies**: TASK-004, TASK-005, TASK-006, TASK-007
- **Assignee**: Backend Developer
- **Estimated Hours**: 20
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Unit tests for services
  - [ ] Integration tests for APIs
  - [ ] Security testing
  - [ ] Performance testing

### TASK-016: Frontend Testing
- **Description**: Implement frontend testing suite
- **Dependencies**: TASK-009, TASK-010, TASK-011
- **Assignee**: Frontend Developer
- **Estimated Hours**: 16
- **Priority**: High
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Component unit tests
  - [ ] Integration tests
  - [ ] E2E testing setup
  - [ ] UI/UX testing

### TASK-017: Deployment Setup
- **Description**: Configure production deployment
- **Dependencies**: TASK-015, TASK-016
- **Assignee**: DevOps Engineer
- **Estimated Hours**: 16
- **Priority**: Medium
- **Status**: 📋 Pending
- **Subtasks**:
  - [ ] Docker containerization
  - [ ] CI/CD pipeline setup
  - [ ] Production database setup
  - [ ] Environment configuration
  - [ ] SSL certificate setup

---

## Task Summary

### Status Overview
- ✅ **Completed**: 1 task
- 🔄 **In Progress**: 1 task  
- 📋 **Pending**: 15 tasks
- **Total Tasks**: 17

### Priority Breakdown
- **Critical**: 1 task
- **High**: 9 tasks
- **Medium**: 5 tasks
- **Low**: 1 task

### Estimated Total Hours: 284 hours

---

## Dependencies Chart
```
TASK-001 (Setup)
├── TASK-002 (DB Schema)
│   └── TASK-003 (Entities)
│       ├── TASK-004 (User APIs)
│       │   └── TASK-009 (Auth UI)
│       │       └── TASK-010 (Dashboard)
│       │           └── TASK-011 (Task UI)
│       ├── TASK-005 (Task APIs)
│       │   ├── TASK-007 (Comments)
│       │   └── TASK-012 (Search)
│       └── TASK-006 (Category APIs)
└── TASK-008 (React Setup)

TASK-013 (Attachments) ← TASK-007
TASK-014 (Notifications) ← TASK-005, TASK-011
TASK-015 (Backend Tests) ← TASK-004,005,006,007
TASK-016 (Frontend Tests) ← TASK-009,010,011
TASK-017 (Deployment) ← TASK-015,016
```

---

*Last Updated: August 3, 2025*
*Project Manager: [Assign Name]*