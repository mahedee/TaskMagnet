# TaskMagnet Epics & User Stories

---

## Epic 1: User Authentication & Authorization

### User Story 1.1 — User Registration

**As a** new user,
**I want** to register an account using my email and password,
**so that** I can create a personal profile and access TaskMagnet.

**Acceptance Criteria:**

* [ ] Registration form includes Full Name, Email, Password, Confirm Password fields
* [ ] Email format is validated and must be unique
* [ ] Password must be at least 8 characters with letters, numbers, and symbols
* [ ] On submission, user receives a verification email
* [ ] User cannot log in before verifying email

**Definition of Done:**

* Code for registration UI and backend API completed
* Email verification mechanism implemented
* Validation errors displayed appropriately on UI
* Unit and integration tests cover registration scenarios
* Manual testing passed for success and failure flows
* Documentation updated for registration process

---

### User Story 1.2 — Email Verification

**As a** newly registered user,
**I want** to verify my email address via a confirmation link,
**so that** my account is activated and secure.

**Acceptance Criteria:**

* [ ] Verification link sent by email expires after 24 hours
* [ ] Clicking the link activates the user account
* [ ] Expired or invalid links display an informative error message
* [ ] User is redirected to login page after successful verification

**Definition of Done:**

* Backend endpoint to handle verification link created
* Email templates and delivery configured
* Link expiry logic implemented and tested
* UI handles success and error states gracefully
* Automated tests cover positive and negative cases

---

### User Story 1.3 — User Login

**As a** registered and verified user,
**I want** to log in using my email and password,
**so that** I can securely access my projects and tasks.

**Acceptance Criteria:**

* [ ] Users with unverified emails cannot log in and receive proper error message
* [ ] Successful login returns JWT token for authentication
* [ ] Invalid credentials show a generic error without leaking info
* [ ] Account locks after 5 consecutive failed attempts for 15 minutes

**Definition of Done:**

* Login UI and backend authentication implemented
* JWT token generation and validation integrated
* Security measures for failed login attempts implemented
* Tests cover all success and failure cases
* Documentation updated with login flow

---

### User Story 1.4 — Password Reset

**As a** user who forgot my password,
**I want** to request a password reset link via email,
**so that** I can regain access to my account.

**Acceptance Criteria:**

* [ ] Password reset form accepts registered email only
* [ ] Reset link emailed expires after 1 hour
* [ ] User can submit a new password via reset link
* [ ] Password complexity enforced on reset
* [ ] After reset, user can log in with the new password

**Definition of Done:**

* Password reset request and update APIs implemented
* Email service configured for sending reset links
* Validation on reset password enforced
* UI guides user through reset process with messages
* Tests validate reset workflow including expiry and errors

---

### User Story 1.5 — Role-Based Access Control

**As an** admin or project manager,
**I want** to assign roles like Admin, Manager, Developer, and Tester to users,
**so that** permissions and access are properly managed.

**Acceptance Criteria:**

* [ ] Roles can be assigned or changed from user management UI
* [ ] Role changes reflect immediately in access control enforcement
* [ ] Users only see features and projects according to their roles
* [ ] Unauthorized actions are blocked with clear messages

**Definition of Done:**

* Backend role and permission model implemented
* UI for role assignment built
* Access control enforced in APIs and frontend
* Automated security tests included
* Documentation updated for role management

---

## Epic 2: Workspace and Project Management

### User Story 2.1 — Create Workspace

**As a** user,
**I want** to create a workspace to organize projects and team members,
**so that** I can separate work for different organizations or teams.

**Acceptance Criteria:**

* [ ] Workspace creation form validates unique workspace name
* [ ] Creator automatically assigned workspace admin role
* [ ] Workspace appears in user’s dashboard after creation

**Definition of Done:**

* Workspace creation API and UI implemented
* Validation and error handling present
* Tests for creation, duplicate names, and permissions
* User dashboard shows new workspace
* Documentation updated

---

### User Story 2.2 — Invite Members to Workspace

**As a** workspace admin,
**I want** to invite users by email and assign roles,
**so that** they can join and collaborate on projects.

**Acceptance Criteria:**

* [ ] Admin can send email invitations with role assignment
* [ ] Invited users receive email with accept link
* [ ] Accepting invitation adds user to workspace with assigned role
* [ ] Invites expire after 7 days

**Definition of Done:**

* Invitation API and email configured
* UI for managing invitations built
* Expiry and re-invite flows implemented
* Tests for invitation lifecycle
* Documentation updated

---

### User Story 2.3 — Create Project

**As a** project manager,
**I want** to create projects inside a workspace with details like name and key,
**so that** I can organize work by projects.

**Acceptance Criteria:**

* [ ] Project key/code is unique within workspace
* [ ] Project creation form validates required fields
* [ ] Project owner/lead assigned on creation
* [ ] Project visible to workspace members with correct roles

**Definition of Done:**

* Project creation API and UI built
* Validation for uniqueness and roles enforced
* Project listing reflects new projects
* Tests for creation and permission checks
* Documentation updated

---

### User Story 2.4 — Archive / Reactivate Project

**As a** project manager,
**I want** to archive projects that are no longer active and reactivate them later,
**so that** I keep the workspace organized without losing data.

**Acceptance Criteria:**

* [ ] Archived projects do not show in active project lists by default
* [ ] Reactivation restores project visibility and status
* [ ] Users can view archived projects if they choose

**Definition of Done:**

* Archive/reactivate APIs implemented
* UI options for archiving/reactivating projects available
* Tests for state changes and visibility
* Documentation updated

---

## Epic 3: Task and Issue Management

### User Story 3.1 — Create Task

**As a** project member,
**I want** to create tasks with title, description, priority, due date, and assignee,
**so that** I can clearly define work items.

**Acceptance Criteria:**

* [ ] Task creation form enforces required fields and validations
* [ ] Tasks can be assigned to workspace members
* [ ] Tasks support multiple types: Story, Task, Bug, Epic
* [ ] Created tasks appear on project boards and lists immediately

**Definition of Done:**

* Task creation backend and UI implemented
* Validation on fields including task type
* Notifications triggered for assignees
* Tests for creation, validation, and notifications
* Documentation updated

---

### User Story 3.2 — Edit Task

**As a** task assignee or project member,
**I want** to update task details, including status and attachments,
**so that** task information remains accurate and current.

**Acceptance Criteria:**

* [ ] Users with appropriate permissions can edit tasks
* [ ] Attachment upload supports common file types and size limits
* [ ] Status transitions follow project workflow rules
* [ ] Changes logged in audit trail

**Definition of Done:**

* Task edit API and UI built
* File upload and validation implemented
* Workflow engine enforces transitions
* Audit logging added for changes
* Tests cover editing and error cases
* Documentation updated

---

### User Story 3.3 — Clone and Move Task

**As a** project member,
**I want** to clone or move tasks between projects or sprints,
**so that** I can reuse or reorganize work efficiently.

**Acceptance Criteria:**

* [ ] Clone creates a duplicate with a new unique ID
* [ ] Move updates task’s project and sprint association
* [ ] All related subtasks and attachments are handled appropriately
* [ ] Users notified of move or clone success

**Definition of Done:**

* Clone and move functionality implemented backend and UI
* Data integrity ensured during operations
* Notifications and audit logs updated
* Tests for cloning/moving various task types
* Documentation updated


