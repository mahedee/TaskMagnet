# Product Requirements Document (PRD)

---

## Story Card

* **Product Name:** TaskMagnet
* **Owner:** Mahedee Hasan
* **Version:** 1.0
* **Last Updated:** August 3, 2025

---

## 1. Overview

TaskMagnet is a full-featured project and task management platform, designed as an alternative to Jira. It aims to empower Agile teams and organizations with tools for project planning, issue tracking, sprint management, and real-time collaboration, all via a modern, intuitive web interface.

---

## 2. Goals & Objectives

* Develop a scalable, modular Jira-like system for managing projects and tasks.
* Provide support for Agile methodologies including Kanban and Scrum.
* Implement role-based access and workflow customization.
* Deliver a responsive UI with collaboration and reporting features.

---

## 3. Target Users

* Software engineers, testers, and managers
* Product owners and project managers
* Startups and enterprise teams looking for Jira alternatives

---

## 4. Functional Requirements

---

### FR-01: User Registration

* **Description:** The system shall allow new users to register with email and password, collecting Full Name, Email, Password, and Confirm Password.
* **Actors:** New user
* **Preconditions:** User is not logged in and does not have an account.
* **Postconditions:** A new inactive user account is created, and an email verification link is sent.
* **Flow:** User submits registration form → System validates → Creates account → Sends verification email.
* **Validation:** Email must be unique and valid format; password min 8 chars with complexity.
* **Acceptance Criteria:**

  * User receives verification email.
  * User cannot log in before verification.
  * Proper validation error messages are shown.

---

### FR-02: User Login & Authentication

* **Description:** Users can log in using email and password once verified; sessions are managed using JWT tokens.
* **Actors:** Registered user
* **Preconditions:** User must have a verified account.
* **Postconditions:** User is authenticated and granted appropriate access.
* **Flow:** User submits login form → Credentials validated → JWT token issued → User session started.
* **Acceptance Criteria:**

  * Invalid credentials show error messages.
  * Users with unverified email cannot log in.
  * Sessions expire after predefined timeout.

---

### FR-03: Password Reset

* **Description:** Users can request password reset via email and update their password securely.
* **Actors:** Registered user
* **Preconditions:** User provides registered email.
* **Postconditions:** Password is reset after verification.
* **Flow:** User requests reset → System emails reset link → User submits new password.
* **Acceptance Criteria:**

  * Reset link expires after time limit.
  * Password complexity enforced on reset.

---

### FR-04: Create Workspace

* **Description:** Authenticated users can create workspaces to group projects and teams.
* **Actors:** Authenticated user
* **Preconditions:** User is logged in.
* **Postconditions:** New workspace is created and user becomes admin.
* **Flow:** User inputs workspace name → Submits form → Workspace created.
* **Acceptance Criteria:**

  * Workspace name is unique per user.
  * User receives confirmation.

---

### FR-05: Invite Members to Workspace

* **Description:** Workspace admins can invite users by email to join workspace with defined roles.
* **Actors:** Workspace Admin
* **Preconditions:** Workspace exists and user is admin.
* **Postconditions:** Invitations sent; users can accept to join workspace.
* **Flow:** Admin enters email and role → Invitation sent → Recipient accepts.
* **Acceptance Criteria:**

  * Invited user receives email.
  * Role assigned upon acceptance.

---

### FR-06: Project Management

* **Description:** Users can create, edit, archive, and reactivate projects within a workspace.
* **Actors:** Project Manager, Workspace Admin
* **Preconditions:** User belongs to workspace.
* **Postconditions:** Project data updated accordingly.
* **Flow:** User creates or modifies project → Changes saved → Project visible to members.
* **Acceptance Criteria:**

  * Project key (e.g., TM-101) is unique.
  * Archived projects are hidden by default.

---

### FR-07: Task Management

* **Description:** Users can create, update, assign, clone, and delete tasks with fields including title, description, priority, status, assignee, due/start dates, labels, attachments, and subtasks.
* **Actors:** Project Members
* **Preconditions:** Project exists and user has permission.
* **Postconditions:** Task updated in system and reflected on boards/reports.
* **Flow:** User submits task data → Validated → Stored → Notifications sent as needed.
* **Acceptance Criteria:**

  * Required fields validated.
  * Attachments upload securely.
  * Task changes trigger audit logs.

---

### FR-08: Kanban and Scrum Boards

* **Description:** Users can view and move tasks across customizable columns on Kanban or Scrum boards, with drag-and-drop functionality and filters.
* **Actors:** Project Members
* **Preconditions:** Tasks exist for the project.
* **Postconditions:** Board reflects current task status and filters.
* **Flow:** User interacts with board → Drag tasks → Status updates → UI refreshes.
* **Acceptance Criteria:**

  * Drag-and-drop is smooth and persists changes.
  * Filters apply correctly.

---

### FR-09: Sprint Planning

* **Description:** Users can create, edit, start, and complete sprints; add tasks to sprints and define sprint goals/durations.
* **Actors:** Project Manager, Scrum Master
* **Preconditions:** Project active with backlog tasks.
* **Postconditions:** Sprint data updated; incomplete tasks carry over.
* **Flow:** Create sprint → Assign tasks → Start sprint → Complete sprint → Tasks updated.
* **Acceptance Criteria:**

  * Sprint dates validate correctly.
  * Tasks outside sprint are unaffected.

---

### FR-10: Comments & Collaboration

* **Description:** Users can comment on tasks, mention others with `@username`, and attach files within comments.
* **Actors:** Project Members
* **Preconditions:** Task exists.
* **Postconditions:** Comments stored and notifications triggered.
* **Flow:** User types comment → Submits → System saves and notifies mentioned users.
* **Acceptance Criteria:**

  * Mentions trigger notifications.
  * Comments editable and deletable by authors.

---

### FR-11: Notifications

* **Description:** Users receive in-app and email notifications for task assignments, mentions, status changes, and comments.
* **Actors:** All users
* **Preconditions:** User is subscribed to relevant notifications.
* **Postconditions:** Notifications delivered promptly.
* **Acceptance Criteria:**

  * Users can configure notification preferences.
  * Notifications respect user roles.

---

### FR-12: Reporting & Analytics

* **Description:** Provide burndown charts, velocity reports, task completion stats, and time tracking summaries.
* **Actors:** Project Manager, Stakeholders
* **Preconditions:** Data exists for selected projects/sprints.
* **Postconditions:** Accurate reports generated and displayed.
* **Acceptance Criteria:**

  * Reports update dynamically.
  * Data visualizations are clear and exportable.

---

### FR-13: User Profile Management

* **Description:** Users can update their profile information, including avatar and notification preferences.
* **Actors:** Registered users
* **Preconditions:** User logged in.
* **Postconditions:** Profile updates saved and applied.
* **Acceptance Criteria:**

  * Profile pictures upload securely.
  * Changes reflect immediately.

---

### FR-14: Audit Logging

* **Description:** System logs critical user actions on tasks, projects, and settings for accountability.
* **Actors:** Admins, Project Managers
* **Preconditions:** Actions performed on tracked entities.
* **Postconditions:** Logs stored and filterable by user, date, and action type.
* **Acceptance Criteria:**

  * Logs are tamper-proof.
  * Accessible via admin UI.

---

## 5. Non-Functional Requirements (NFRs)

---

### NFR-01: Performance

* 90% of API calls should respond within 500ms under normal load.
* Board UI renders within 2 seconds with up to 200 tasks.

---

### NFR-02: Scalability

* Support at least 10,000 concurrent users with horizontal scaling capabilities.

---

### NFR-03: Security

* Passwords stored using bcrypt hashing.
* JWT-based authentication and authorization.
* Protect against XSS, CSRF, SQL Injection attacks.
* Secure file upload and storage with access control.

---

### NFR-04: Availability

* Target uptime of 99.9% in production environments.

---

### NFR-05: Usability

* Responsive design compatible with desktops, tablets, and mobile browsers.
* WCAG 2.1 accessibility compliance.

---

### NFR-06: Maintainability

* Modular codebase with separation of concerns (backend/frontend).
* Use CI/CD pipelines with automated tests and rollback support.

---

### NFR-07: Internationalization

* English supported at launch; architecture prepared for future multi-language support.

---

### NFR-08: Logging & Monitoring

* Application and error logs captured centrally for analysis and alerting.

---

## 6. Assumptions & Dependencies

* Backend technology: Java Spring Boot
* Frontend technology: React.js with CSS framework (e.g., Tailwind or Material UI)
* Initial database: MySQL, planned Oracle migration
* Hosting on AWS/GCP cloud environment
* Email SMTP service for notifications
* Source control and issue tracking on GitHub

---

## 7. Out of Scope (MVP)

* Mobile native applications
* AI-powered task recommendations
* Third-party integrations (e.g., Zapier, Slack)
* Marketplace or plugin architecture

---

## 8. Success Metrics

* 95%+ of users complete onboarding within 5 minutes.
* API error rate below 1% in production.
* User engagement: Average daily active users > 1000 within 3 months.
* Board load times < 2 seconds consistently.

---