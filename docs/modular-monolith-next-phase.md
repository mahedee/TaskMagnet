# Modular Monolith – Next Phase Architecture
**TaskMagnet Project – Domain-Driven Modular Monolith Migration Plan**

---

## 📋 Document Information
- **Version**: 1.0
- **Date**: March 15, 2026
- **Status**: Approved for Implementation
- **Author**: TaskMagnet Development Team
- **Purpose**: Define the target architecture, module structure, cross-module communication patterns, and step-by-step migration plan from the current 2-module structure to a full domain-driven modular monolith.

---

## 🎯 Overview

### What is a Domain-Driven Modular Monolith?

A **Modular Monolith** is a single deployable application internally organized into well-defined, loosely-coupled modules with strict, enforced boundaries. Combined with **Domain-Driven Design (DDD)**, each module maps to a **business domain (Bounded Context)** rather than a technical layer.

```
Traditional Monolith       Modular Monolith (Target)       Microservices
──────────────────────     ──────────────────────────      ──────────────────────
1 process, tangled code    1 process, clean boundaries     N processes, N databases
No module enforcement      Maven compile-time enforced     Network enforced
Simple to deploy           Simple to deploy                Complex orchestration
Hard to scale teams        Teams own modules               Teams own services
Big rewrite to extract     Surgical extraction possible    Already extracted
```

### Why Change From the Current Structure?

The current structure (`taskmagnet-core` + `taskmagnet-web`) is a **horizontal/technical layer split**:

- `taskmagnet-core` = all entities and repositories
- `taskmagnet-web` = all controllers, services, and security

This works for a simple CRUD app but **does not scale** for a Jira-equivalent. As features like sprints, boards, workflows, notifications, automation, and search are added, `taskmagnet-web` will become an unmanageable monolith with hundreds of tangled service classes.

---

## 🏗️ Current vs. Target Structure

### Current Structure (2 modules)
```
taskmagnet/                          ← parent POM
├── taskmagnet-core/                 ← entities, repos, DTOs, utils, enums
│   └── com.mahedee.taskmagnet.core/
│       ├── entity/    (User, Project, Task, Category)
│       ├── entity/audit/  (BaseAuditEntity)
│       ├── repository/    (UserRepository, ProjectRepository, TaskRepository, CategoryRepository)
│       ├── dto/       (BaseResponse, PagedResponse, ApiError)
│       ├── enums/     (Priority, TaskStatus, ProjectStatus)
│       ├── util/      (DateUtil, ValidationUtil)
│       └── config/
│
└── taskmagnet-web/                  ← controllers, services, security, main app
    └── com.mahedee.backend/
        ├── controllers/  (AuthController, TaskStatusController, TaskCategoryController)
        ├── models/       (User, Role, ERole, TaskStatus, TaskCategory, BaseEntity)
        ├── repository/   (UserRepository, RoleRepository, TaskStatusRepository, ...)
        ├── security/     (WebSecurityConfig, JwtAuthFilter, UserDetailsImpl, ...)
        └── configuration/ (SwaggerOpenApiConfig, DataSeeder)
```

**Problems:**
- Entities duplicated between core and web (`User` exists in both)
- No domain boundaries — all business logic mixes in one module
- Cross-cutting concerns (security, notifications) are interleaved with business logic
- Cannot add sprint/board/workflow features without making `taskmagnet-web` enormous

---

### Target Structure (12 modules)

```
taskmagnet/                              ← Root parent POM
├── taskmagnet-common/                   ← Truly shared: base entity, DTOs, exceptions, events
├── taskmagnet-auth/                     ← Authentication, JWT, sessions, roles, permissions
├── taskmagnet-user/                     ← User profiles, teams, organizations
├── taskmagnet-project/                  ← Projects, components, versions, categories
├── taskmagnet-issue/                    ← Issues, subtasks, attachments, links, labels
├── taskmagnet-sprint/                   ← Sprints, boards, backlog, Kanban
├── taskmagnet-workflow/                 ← Workflow engine, statuses, transitions
├── taskmagnet-comment/                  ← Comments, reactions, activity stream
├── taskmagnet-notification/             ← Email, in-app notifications, schemes
├── taskmagnet-search/                   ← JQL engine, Elasticsearch indexing
├── taskmagnet-reporting/                ← Dashboards, charts, exports
├── taskmagnet-automation/               ← Automation rules engine
└── taskmagnet-api/                      ← Runnable app: REST controllers, Spring Boot main, config
```

---

## 📦 Module Definitions

### `taskmagnet-common`
**Purpose**: Shared infrastructure with zero business/domain logic. All other modules depend on this.

```
taskmagnet-common/
└── src/main/java/com/mahedee/taskmagnet/common/
    ├── entity/
    │   └── BaseAuditEntity.java          ← id, createdDate, modifiedDate, createdBy, modifiedBy, isActive, version
    ├── dto/
    │   ├── ApiResponse.java              ← Standard success/error response wrapper
    │   ├── PagedResponse.java            ← Paginated list wrapper
    │   └── ErrorDetail.java
    ├── exception/
    │   ├── BusinessException.java
    │   ├── ResourceNotFoundException.java
    │   ├── ValidationException.java
    │   ├── ForbiddenException.java
    │   └── GlobalExceptionHandler.java   ← @ControllerAdvice
    ├── event/                            ← Cross-module Spring Events (POJOs only)
    │   ├── IssueCreatedEvent.java
    │   ├── IssueUpdatedEvent.java
    │   ├── IssueStatusChangedEvent.java
    │   ├── IssueDeletedEvent.java
    │   ├── CommentAddedEvent.java
    │   ├── SprintStartedEvent.java
    │   ├── SprintCompletedEvent.java
    │   ├── UserRegisteredEvent.java
    │   └── ProjectCreatedEvent.java
    ├── api/                              ← Public API interfaces (Façade contracts)
    │   ├── IssueApi.java
    │   ├── UserApi.java
    │   ├── ProjectApi.java
    │   └── SprintApi.java
    ├── enums/
    │   └── Priority.java                 ← Only truly shared enums
    └── util/
        ├── DateUtil.java
        ├── ValidationUtil.java
        └── SecurityUtil.java            ← Get current user from SecurityContext
```

**Maven packaging**: `jar` (library, no main class)
**Dependencies**: Spring Boot Starter, Spring Data JPA, Hibernate Validator

---

### `taskmagnet-auth`
**Purpose**: All authentication and authorization concerns. Owns `User`, `Role`, and `Permission` entities.

```
taskmagnet-auth/
└── src/main/java/com/mahedee/taskmagnet/auth/
    ├── entity/
    │   ├── User.java                     ← username, email, password, firstName, lastName
    │   ├── Role.java                     ← name, displayName, hierarchyLevel
    │   ├── Permission.java               ← name, resourceType, action
    │   ├── UserRole.java                 ← user-role mapping with expiry
    │   ├── UserPermission.java           ← direct permission overrides (GRANT/DENY)
    │   ├── RolePermission.java           ← role-permission mapping
    │   ├── RefreshToken.java             ← token, userId, expiresAt, isRevoked
    │   ├── UserSession.java              ← session tracking across devices
    │   ├── LoginAttempt.java             ← brute-force tracking
    │   └── PasswordHistory.java          ← prevent password reuse
    ├── repository/
    │   ├── UserRepository.java
    │   ├── RoleRepository.java
    │   ├── PermissionRepository.java
    │   └── RefreshTokenRepository.java
    ├── service/
    │   ├── AuthService.java              ← login, logout, register, refresh token
    │   ├── JwtService.java               ← token generation and validation
    │   ├── UserDetailsServiceImpl.java   ← Spring Security integration
    │   ├── PermissionService.java        ← evaluate user permissions
    │   └── SessionService.java           ← session lifecycle management
    ├── security/
    │   ├── JwtAuthFilter.java            ← JWT request filter
    │   └── RateLimitFilter.java          ← Bucket4j rate limiting
    ├── api/
    │   └── UserApiImpl.java              ← implements UserApi from common
    └── dto/
        ├── LoginRequest.java
        ├── LoginResponse.java
        ├── RegisterRequest.java
        ├── TokenRefreshRequest.java
        └── UserDto.java
```

**Dependencies**: `taskmagnet-common`, Spring Security, jjwt, Bucket4j

---

### `taskmagnet-user`
**Purpose**: User profiles, teams, and organizational structure.

```
taskmagnet-user/
└── src/main/java/com/mahedee/taskmagnet/user/
    ├── entity/
    │   ├── UserProfile.java              ← avatar, bio, timezone, locale, preferences
    │   ├── Team.java                     ← name, description, lead
    │   └── TeamMember.java              ← user-team mapping with role
    ├── repository/
    ├── service/
    │   ├── UserProfileService.java
    │   └── TeamService.java
    └── dto/
        ├── UserProfileDto.java
        └── TeamDto.java
```

**Dependencies**: `taskmagnet-common`, `taskmagnet-auth` (for user identity)

---

### `taskmagnet-project`
**Purpose**: Project lifecycle, components, release versions, and project membership.

```
taskmagnet-project/
└── src/main/java/com/mahedee/taskmagnet/project/
    ├── entity/
    │   ├── Project.java                  ← name, key (TM), description, status, lead
    │   ├── ProjectMember.java            ← user-project role mapping
    │   ├── ProjectRole.java              ← Administrator, Developer, Viewer, etc.
    │   ├── Component.java                ← project subsystems (e.g., Backend, Frontend)
    │   └── Version.java                  ← release versions with dates
    ├── repository/
    ├── service/
    │   ├── ProjectService.java
    │   ├── ComponentService.java
    │   └── VersionService.java
    ├── api/
    │   └── ProjectApiImpl.java           ← implements ProjectApi from common
    └── dto/
        ├── ProjectDto.java
        ├── ComponentDto.java
        └── VersionDto.java
```

**Dependencies**: `taskmagnet-common`

---

### `taskmagnet-issue`
**Purpose**: The core domain — issues (Epic, Story, Task, Bug, Sub-task), attachments, links, labels, watchers, and votes.

```
taskmagnet-issue/
└── src/main/java/com/mahedee/taskmagnet/issue/
    ├── entity/
    │   ├── Issue.java                    ← key (TM-42), summary, description, type, status, priority
    │   ├── IssueType.java                ← Epic, Story, Task, Bug, Subtask, etc.
    │   ├── IssueLink.java                ← link between issues with direction
    │   ├── IssueLinkType.java            ← blocks, duplicates, relates to, etc.
    │   ├── Attachment.java               ← filename, size, mimeType, storageKey (MinIO)
    │   ├── IssueLabel.java               ← issue-label mapping
    │   ├── Label.java                    ← global label pool
    │   ├── Watcher.java                  ← user watching an issue
    │   └── Vote.java                     ← user voting on an issue
    ├── repository/
    ├── service/
    │   ├── IssueService.java             ← CRUD, transitions, cloning, moving
    │   ├── IssueLinkService.java
    │   ├── AttachmentService.java        ← uploads to MinIO
    │   └── LabelService.java
    ├── api/
    │   └── IssueApiImpl.java             ← implements IssueApi from common
    └── dto/
        ├── IssueDto.java
        ├── CreateIssueRequest.java
        ├── UpdateIssueRequest.java
        └── AttachmentDto.java
```

**Dependencies**: `taskmagnet-common`, `taskmagnet-project`

---

### `taskmagnet-sprint`
**Purpose**: Sprints, Scrum boards, Kanban boards, backlog management, and issue ranking.

```
taskmagnet-sprint/
└── src/main/java/com/mahedee/taskmagnet/sprint/
    ├── entity/
    │   ├── Sprint.java                   ← name, goal, startDate, endDate, state
    │   ├── Board.java                    ← name, type (SCRUM/KANBAN), project
    │   ├── BoardColumn.java              ← column name, statusMapping, wipLimit, order
    │   ├── BoardFilter.java              ← saved quick filters on board
    │   └── SprintIssue.java              ← issue-sprint mapping with rank order
    ├── repository/
    ├── service/
    │   ├── SprintService.java            ← create, start, complete sprint
    │   ├── BoardService.java             ← board configuration, column management
    │   └── BacklogService.java           ← rank issues, move to/from sprint
    ├── api/
    │   └── SprintApiImpl.java            ← implements SprintApi from common
    └── dto/
        ├── SprintDto.java
        ├── BoardDto.java
        └── BoardColumnDto.java
```

**Dependencies**: `taskmagnet-common`, `taskmagnet-issue` (via `IssueApi`)

---

### `taskmagnet-workflow`
**Purpose**: Workflow engine — custom statuses, transitions, conditions, validators, and post-functions.

```
taskmagnet-workflow/
└── src/main/java/com/mahedee/taskmagnet/workflow/
    ├── entity/
    │   ├── Workflow.java                 ← name, description, isDefault
    │   ├── WorkflowStatus.java           ← name, category (TODO/IN_PROGRESS/DONE), color
    │   ├── WorkflowTransition.java       ← from status → to status, name
    │   ├── TransitionCondition.java      ← rules that must pass before transition
    │   ├── TransitionValidator.java      ← field validation before transition
    │   └── TransitionPostFunction.java   ← actions after transition (assign, notify)
    ├── repository/
    ├── service/
    │   ├── WorkflowService.java          ← workflow CRUD, scheme assignment
    │   └── TransitionService.java        ← evaluate conditions, run post-functions
    └── dto/
        ├── WorkflowDto.java
        └── TransitionDto.java
```

**Dependencies**: `taskmagnet-common`

---

### `taskmagnet-comment`
**Purpose**: Issue comments with rich text, reactions, and the unified activity stream.

```
taskmagnet-comment/
└── src/main/java/com/mahedee/taskmagnet/comment/
    ├── entity/
    │   ├── Comment.java                  ← issueId, body (rich text), authorId, visibility
    │   ├── CommentReaction.java          ← emoji reactions per comment
    │   └── IssueHistory.java             ← field change records (Envers supplement)
    ├── repository/
    ├── service/
    │   ├── CommentService.java
    │   └── ActivityService.java          ← unified activity stream (comments + history)
    └── dto/
        ├── CommentDto.java
        └── ActivityDto.java
```

**Dependencies**: `taskmagnet-common`

---

### `taskmagnet-notification`
**Purpose**: All notification delivery — email, in-app, and notification scheme management.

```
taskmagnet-notification/
└── src/main/java/com/mahedee/taskmagnet/notification/
    ├── entity/
    │   ├── Notification.java             ← userId, type, message, isRead, link
    │   ├── NotificationScheme.java       ← named scheme assigned to projects
    │   └── NotificationPreference.java  ← per-user per-event opt-in/out
    ├── repository/
    ├── service/
    │   ├── NotificationService.java      ← create and dispatch notifications
    │   ├── EmailService.java             ← Spring Mail + Thymeleaf templates
    │   └── InAppNotificationService.java ← WebSocket push for in-app alerts
    ├── listener/                         ← Spring Event listeners
    │   ├── IssueEventListener.java       ← listens to IssueCreatedEvent, etc.
    │   ├── SprintEventListener.java      ← listens to SprintStartedEvent, etc.
    │   └── CommentEventListener.java     ← listens to CommentAddedEvent
    └── template/                         ← Thymeleaf email templates
        ├── issue-created.html
        ├── issue-assigned.html
        ├── comment-added.html
        └── sprint-started.html
```

**Dependencies**: `taskmagnet-common`, Spring Mail, Thymeleaf, Spring WebSocket

---

### `taskmagnet-search`
**Purpose**: JQL query engine and Elasticsearch-based full-text search indexing.

```
taskmagnet-search/
└── src/main/java/com/mahedee/taskmagnet/search/
    ├── jql/
    │   ├── JqlParser.java                ← Parses JQL string into query AST
    │   ├── JqlExecutor.java              ← Translates AST to Oracle SQL or ES query
    │   ├── JqlFunction.java              ← currentUser(), openSprints(), membersOf()
    │   └── JqlValidator.java             ← Validates JQL syntax and field names
    ├── index/
    │   ├── IssueDocument.java            ← Elasticsearch document model
    │   └── IssueIndexService.java        ← Index/update/delete in Elasticsearch
    ├── listener/
    │   └── IssueSearchIndexListener.java ← Listens to IssueCreatedEvent etc.
    └── service/
        └── SearchService.java            ← Processes search requests
```

**Dependencies**: `taskmagnet-common`, Spring Data Elasticsearch

---

### `taskmagnet-reporting`
**Purpose**: Agile reports, dashboards, and data exports.

```
taskmagnet-reporting/
└── src/main/java/com/mahedee/taskmagnet/reporting/
    ├── service/
    │   ├── BurndownService.java          ← Sprint burndown data
    │   ├── BurnupService.java            ← Sprint burnup data
    │   ├── VelocityService.java          ← Velocity across sprints
    │   ├── CumulativeFlowService.java    ← CFD data
    │   ├── SprintReportService.java      ← Completed/incomplete/added issues
    │   ├── WorkloadService.java          ← Issues per assignee
    │   └── ExportService.java            ← Excel (POI) and PDF (iText) export
    └── dto/
        ├── BurndownDataDto.java
        ├── VelocityDataDto.java
        └── CfdDataDto.java
```

**Dependencies**: `taskmagnet-common`, `taskmagnet-issue` (via `IssueApi`), `taskmagnet-sprint` (via `SprintApi`), Apache POI, iText

---

### `taskmagnet-automation`
**Purpose**: Automation rules engine — triggers, conditions, and actions.

```
taskmagnet-automation/
└── src/main/java/com/mahedee/taskmagnet/automation/
    ├── entity/
    │   ├── AutomationRule.java           ← name, projectId, isEnabled
    │   ├── AutomationTrigger.java        ← type (ISSUE_CREATED, STATUS_CHANGED, etc.)
    │   ├── AutomationCondition.java      ← field checks, JQL match, role checks
    │   └── AutomationAction.java         ← ASSIGN, TRANSITION, COMMENT, WEBHOOK, EMAIL
    ├── engine/
    │   ├── TriggerRegistry.java          ← maps event types to rules
    │   ├── ConditionEvaluator.java       ← evaluates all conditions
    │   └── ActionExecutor.java           ← executes actions
    ├── listener/
    │   └── AutomationEventListener.java  ← listens to all events, matches rules
    └── service/
        └── AutomationRuleService.java    ← CRUD for rules
```

**Dependencies**: `taskmagnet-common`, Quartz Scheduler

---

### `taskmagnet-api`
**Purpose**: The **only runnable module** — Spring Boot main class, all REST controllers, security configuration wiring, and infrastructure setup.

```
taskmagnet-api/
└── src/main/java/com/mahedee/taskmagnet/api/
    ├── TaskMagnetApplication.java        ← @SpringBootApplication (main entry point)
    ├── config/
    │   ├── SecurityConfig.java           ← Wires JwtAuthFilter, SecurityFilterChain
    │   ├── RedisConfig.java
    │   ├── RabbitMQConfig.java
    │   ├── ElasticsearchConfig.java
    │   ├── MinIOConfig.java
    │   ├── WebSocketConfig.java
    │   ├── SwaggerConfig.java
    │   └── DataSeeder.java               ← Seeds default roles, permissions on startup
    └── controller/
        ├── AuthController.java
        ├── UserController.java
        ├── ProjectController.java
        ├── IssueController.java
        ├── SprintController.java
        ├── BoardController.java
        ├── WorkflowController.java
        ├── CommentController.java
        ├── NotificationController.java
        ├── SearchController.java
        ├── ReportController.java
        └── AutomationController.java
└── src/main/resources/
    ├── application.properties
    ├── application-dev.properties
    ├── application-prod.properties
    └── db/migration/                     ← Flyway SQL scripts
        ├── V1__create_users.sql
        ├── V2__create_roles_permissions.sql
        ├── V3__create_projects.sql
        ├── V4__create_issues.sql
        ├── V5__create_sprints.sql
        ├── V6__create_workflows.sql
        ├── V7__create_comments.sql
        ├── V8__create_notifications.sql
        └── V9__create_automation_rules.sql
```

**Dependencies**: ALL domain modules

---

## 🔗 Module Dependency Graph

```
                        taskmagnet-api
                              │
         ┌────────────────────┼─────────────────────┐
         │                    │                     │
    taskmagnet-auth    taskmagnet-project    taskmagnet-issue
         │                    │                     │
         └────────────────────┼─────────────────────┘
                              │
         ┌────────────────────┼─────────────────────┐
         │                    │                     │
  taskmagnet-sprint  taskmagnet-workflow   taskmagnet-comment
         │
         ├── taskmagnet-notification
         ├── taskmagnet-search
         ├── taskmagnet-reporting
         └── taskmagnet-automation
                              │
                   ALL depend on:
                  taskmagnet-common
```

**Golden Rules:**
1. Domain modules **never** import each other directly
2. Only `taskmagnet-api` imports all modules
3. All share `taskmagnet-common`
4. Cross-domain data flows via **Spring Events** or **Public API interfaces**

---

## 📡 Cross-Module Communication Patterns

### Pattern 1: Public API Interface (Synchronous)

Use when Module A needs **data from Module B immediately** (synchronous call).

Interfaces are defined in `taskmagnet-common`, implemented in the owning module.

```java
// taskmagnet-common: contract definition
public interface IssueApi {
    IssueDto getIssueById(Long issueId);
    List<IssueDto> getIssuesBySprintId(Long sprintId);
    long countIssuesByProjectId(Long projectId);
}

// taskmagnet-issue: implementation
@Service
public class IssueApiImpl implements IssueApi {
    private final IssueRepository issueRepository;
    // implementation...
}

// taskmagnet-sprint: consumer
@Service
public class SprintService {
    private final IssueApi issueApi;  // injected by Spring

    public SprintCompletionResult completeSprint(Long sprintId) {
        List<IssueDto> unfinished = issueApi.getIssuesBySprintId(sprintId);
        // logic to move unfinished issues to next sprint...
    }
}
```

---

### Pattern 2: Spring Application Events (Asynchronous / Reactive)

Use when Module A **fires an event** and other modules react independently.

Events are plain POJOs defined in `taskmagnet-common`.

```java
// taskmagnet-common: event definition (plain POJO)
public class IssueCreatedEvent {
    private final Long issueId;
    private final Long projectId;
    private final Long reporterId;
    private final Long assigneeId;
    private final String issueKey;
    // constructor + getters
}

// taskmagnet-issue: fires the event
@Service
public class IssueService {
    private final ApplicationEventPublisher eventPublisher;

    public Issue createIssue(CreateIssueRequest request) {
        Issue saved = issueRepository.save(buildIssue(request));
        eventPublisher.publishEvent(new IssueCreatedEvent(
            saved.getId(), saved.getProjectId(),
            saved.getReporterId(), saved.getAssigneeId(), saved.getKey()
        ));
        return saved;
    }
}

// taskmagnet-notification: reacts to the event
@Component
public class IssueNotificationListener {
    @EventListener
    public void onIssueCreated(IssueCreatedEvent event) {
        notificationService.notifyAssignee(event.getAssigneeId(), event.getIssueKey());
    }
}

// taskmagnet-search: also reacts independently
@Component
public class IssueSearchIndexListener {
    @EventListener
    public void onIssueCreated(IssueCreatedEvent event) {
        issueIndexService.indexIssue(event.getIssueId());
    }
}

// taskmagnet-automation: also reacts independently
@Component
public class AutomationEventListener {
    @EventListener
    public void onIssueCreated(IssueCreatedEvent event) {
        automationEngine.processTrigger(TriggerType.ISSUE_CREATED, event);
    }
}
```

---

### Pattern 3: Shared DTOs — Never Share JPA Entities

Modules share **DTOs** through `taskmagnet-common`, never JPA entity objects.

```java
// ✅ Correct — pass a DTO across module boundaries
IssueDto dto = issueApi.getIssueById(id);

// ❌ Wrong — never expose a JPA entity to another module
Issue entity = issueRepository.findById(id);  // belongs to taskmagnet-issue only
```

---

### Cross-Module Event Reference Table

| Event | Fired By | Consumed By |
|---|---|---|
| `IssueCreatedEvent` | `taskmagnet-issue` | notification, search, automation |
| `IssueUpdatedEvent` | `taskmagnet-issue` | search, automation |
| `IssueStatusChangedEvent` | `taskmagnet-workflow` | notification, search, automation |
| `IssueDeletedEvent` | `taskmagnet-issue` | search, automation |
| `CommentAddedEvent` | `taskmagnet-comment` | notification, automation |
| `SprintStartedEvent` | `taskmagnet-sprint` | notification, automation |
| `SprintCompletedEvent` | `taskmagnet-sprint` | notification, reporting, automation |
| `ProjectCreatedEvent` | `taskmagnet-project` | notification, search |
| `UserRegisteredEvent` | `taskmagnet-auth` | notification, user-profile |

---

## 🗃️ Parent POM Structure

```xml
<!-- Root pom.xml -->
<groupId>com.mahedee</groupId>
<artifactId>taskmagnet</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>pom</packaging>

<modules>
    <module>taskmagnet-common</module>
    <module>taskmagnet-auth</module>
    <module>taskmagnet-user</module>
    <module>taskmagnet-project</module>
    <module>taskmagnet-issue</module>
    <module>taskmagnet-sprint</module>
    <module>taskmagnet-workflow</module>
    <module>taskmagnet-comment</module>
    <module>taskmagnet-notification</module>
    <module>taskmagnet-search</module>
    <module>taskmagnet-reporting</module>
    <module>taskmagnet-automation</module>
    <module>taskmagnet-api</module>    <!-- must be last: depends on all others -->
</modules>

<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.5</spring-boot.version>
    <project.version>1.0.0-SNAPSHOT</project.version>
</properties>
```

Each domain module's `pom.xml` declares only its direct dependencies. For example:

```xml
<!-- taskmagnet-sprint/pom.xml -->
<dependencies>
    <dependency>
        <groupId>com.mahedee</groupId>
        <artifactId>taskmagnet-common</artifactId>    ← always
    </dependency>
    <!-- taskmagnet-sprint does NOT depend on taskmagnet-issue directly -->
    <!-- It calls IssueApi (interface in common) implemented by taskmagnet-issue -->
</dependencies>
```

---

## 🔄 Migration Plan (Step-by-Step)

### Prerequisites
- Java upgraded from 17 → 21
- Spring Boot upgraded from 3.1.5 → 3.3.5
- jjwt upgraded from 0.11.5 → 0.12.x
- Flyway added for schema migrations

---

### Step 1 — Create `taskmagnet-common`
- Create new Maven module `taskmagnet-common`
- Move from `taskmagnet-core`:
  - `BaseAuditEntity.java` → `common.entity`
  - `BaseResponse.java`, `PagedResponse.java`, `ApiError.java` → `common.dto`
  - `DateUtil.java`, `ValidationUtil.java` → `common.util`
  - `Priority.java` (enum) → `common.enums`
- Create `common.event` package with empty event POJOs
- Create `common.api` package with empty interface stubs
- Update `taskmagnet-core` and `taskmagnet-web` to depend on `taskmagnet-common`

---

### Step 2 — Create `taskmagnet-auth`
- Create new Maven module `taskmagnet-auth`
- Move from `taskmagnet-web`:
  - `models/User.java`, `models/Role.java`, `models/ERole.java` → `auth.entity`
  - `repository/UserRepository.java`, `repository/RoleRepository.java` → `auth.repository`
  - `security/WebSecurityConfig.java` → `auth.security`
  - `security/services/UserDetailsImpl.java`, `UserDetailsServiceImpl.java` → `auth.service`
  - JWT-related classes → `auth.service`
- Move from `taskmagnet-core`:
  - `entity/User.java` → consolidate into `auth.entity.User`
- Implement `UserApi` interface from `taskmagnet-common`
- Add `taskmagnet-auth` as dependency in `taskmagnet-api`

---

### Step 3 — Create `taskmagnet-project`
- Create new Maven module `taskmagnet-project`
- Move from `taskmagnet-core`:
  - `entity/Project.java` → `project.entity`
  - `repository/ProjectRepository.java` → `project.repository`
  - `enums/ProjectStatus.java` → `project.enums`
- Create `Component.java`, `Version.java`, `ProjectMember.java` entities
- Implement `ProjectApi` interface from `taskmagnet-common`

---

### Step 4 — Create `taskmagnet-issue`
- Create new Maven module `taskmagnet-issue`
- Move from `taskmagnet-core`:
  - `entity/Task.java` → `issue.entity.Issue` (rename to Issue, expand fields)
  - `entity/Category.java` → `issue.entity` (repurpose as Label or IssueType)
  - `repository/TaskRepository.java` → `issue.repository.IssueRepository`
  - `enums/TaskStatus.java`, `Priority.java` → `issue.enums`
- Create `IssueType`, `IssueLink`, `Attachment`, `Watcher`, `Vote` entities
- Implement `IssueApi` interface from `taskmagnet-common`
- Add event publishing on create/update/delete

---

### Step 5 — Create `taskmagnet-sprint`
- Create new Maven module `taskmagnet-sprint`
- Create new entities: `Sprint`, `Board`, `BoardColumn`, `SprintIssue`
- Consume `IssueApi` for issue-sprint operations
- Implement `SprintApi` interface from `taskmagnet-common`
- Publish `SprintStartedEvent`, `SprintCompletedEvent`

---

### Step 6 — Create Remaining Domain Modules
In order of dependency complexity:
1. `taskmagnet-workflow` — no dependencies on other domain modules
2. `taskmagnet-comment` — publishes `CommentAddedEvent`
3. `taskmagnet-notification` — only listens to events, never called directly
4. `taskmagnet-search` — only listens to events + calls `IssueApi`
5. `taskmagnet-reporting` — calls `IssueApi` + `SprintApi`
6. `taskmagnet-automation` — listens to all events

---

### Step 7 — Refactor `taskmagnet-api`
- Move all `@RestController` classes from `taskmagnet-web` to `taskmagnet-api`
- Move `BackendApplication.java` → `TaskMagnetApplication.java`
- Move `SwaggerOpenApiConfig.java`, `DataSeeder.java` → `api.config`
- Add `SecurityConfig.java` that imports and wires `JwtAuthFilter` from `taskmagnet-auth`
- Add infrastructure configs (Redis, RabbitMQ, MinIO, Elasticsearch, WebSocket)
- Set up Flyway migrations under `resources/db/migration/`

---

### Step 8 — Delete Old Modules
- Once all classes are migrated and tests pass:
  - Delete `taskmagnet-core`
  - Delete `taskmagnet-web`
- Update root `pom.xml` modules list

---

## 📐 Package Naming Convention

```
com.mahedee.taskmagnet.{module}.{layer}

Examples:
com.mahedee.taskmagnet.common.entity.BaseAuditEntity
com.mahedee.taskmagnet.auth.service.JwtService
com.mahedee.taskmagnet.issue.entity.Issue
com.mahedee.taskmagnet.sprint.service.SprintService
com.mahedee.taskmagnet.api.controller.IssueController
```

---

## 🧪 Testing Strategy Per Module

Each domain module should be fully testable in isolation.

| Module | Test Type | What to Test |
|---|---|---|
| `taskmagnet-common` | Unit | DTO serialization, utility methods, event POJO construction |
| `taskmagnet-auth` | Unit + Integration | JWT generation/validation, login flow, permission evaluation |
| `taskmagnet-issue` | Unit + Integration | Issue CRUD, status transitions, event publishing |
| `taskmagnet-sprint` | Unit + Integration | Sprint lifecycle, backlog ranking, board column mapping |
| `taskmagnet-workflow` | Unit | Condition evaluation, transition logic, post-function execution |
| `taskmagnet-notification` | Unit | Event listener receives event, correct email template selected |
| `taskmagnet-search` | Unit + Integration | JQL parsing, Elasticsearch indexing |
| `taskmagnet-automation` | Unit | Condition matching, action execution |
| `taskmagnet-api` | Integration (E2E) | Full API flows via REST Assured or MockMvc |

**Testing tools:**
- JUnit 5 + Mockito — unit tests within every module
- Testcontainers — spin up real Oracle XE and Redis for integration tests
- REST Assured — API layer tests in `taskmagnet-api`

---

## 📈 Benefits Summary

| Benefit | Description |
|---|---|
| **Single deployment** | One JAR, one Oracle database — zero distributed system complexity |
| **Enforced boundaries** | Maven compile-time enforcement prevents accidental coupling |
| **Team ownership** | Each developer/team owns one module; they cannot break others |
| **Independent testing** | Each module tests without loading the full Spring context |
| **Incremental builds** | Only changed modules and their dependents rebuild |
| **ACID transactions** | Single JVM = full transactional consistency across all modules |
| **Microservices path** | Any module can be extracted to a separate service when scale demands it — no rewrite |
| **Cognitive clarity** | A developer reads one module and fully understands one domain |

---

## 🚀 Implementation Phases Alignment

| Phase | Modules Introduced |
|---|---|
| **Phase 1 – MVP** | `taskmagnet-common`, `taskmagnet-auth`, `taskmagnet-project`, `taskmagnet-issue`, `taskmagnet-api` |
| **Phase 2 – Core Agile** | `taskmagnet-sprint`, `taskmagnet-workflow`, `taskmagnet-comment`, `taskmagnet-notification` |
| **Phase 3 – Advanced** | `taskmagnet-search`, `taskmagnet-reporting`, `taskmagnet-user` |
| **Phase 4 – Enterprise** | `taskmagnet-automation`, advanced audit, SAML, rate limiting |

---

*Created: March 15, 2026*
*Version: 1.0*
*Status: Approved for Implementation*
