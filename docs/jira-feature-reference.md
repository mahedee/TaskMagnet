# Jira Feature Reference Document
**TaskMagnet Project – Reference Guide for Jira Feature Parity**

---

## 📋 Document Information
- **Version**: 1.0
- **Date**: March 15, 2026
- **Purpose**: Comprehensive reference of Jira's full feature set to guide TaskMagnet development
- **Status**: Active Reference

---

## 🎯 Overview

This document catalogs all major Jira features across every functional area. It serves as the canonical reference for TaskMagnet's feature roadmap. Each section maps Jira capabilities that TaskMagnet aims to replicate, extend, or improve upon.

---

## 📌 Table of Contents

1. [Project Management](#1-project-management)
2. [Issue & Task Tracking](#2-issue--task-tracking)
3. [Agile – Scrum](#3-agile--scrum)
4. [Agile – Kanban](#4-agile--kanban)
5. [Workflow Engine](#5-workflow-engine)
6. [Backlog Management](#6-backlog-management)
7. [Roadmaps & Planning](#7-roadmaps--planning)
8. [Reporting & Dashboards](#8-reporting--dashboards)
9. [Search & Filtering (JQL)](#9-search--filtering-jql)
10. [Notifications & Subscriptions](#10-notifications--subscriptions)
11. [User & Team Management](#11-user--team-management)
12. [Permissions & Security](#12-permissions--security)
13. [Time Tracking & Worklogs](#13-time-tracking--worklogs)
14. [Comments & Collaboration](#14-comments--collaboration)
15. [Attachments & Media](#15-attachments--media)
16. [Labels, Components & Versions](#16-labels-components--versions)
17. [Custom Fields](#17-custom-fields)
18. [Issue Links & Dependencies](#18-issue-links--dependencies)
19. [Automation Rules](#19-automation-rules)
20. [Integrations & API](#20-integrations--api)
21. [Administration & Configuration](#21-administration--configuration)
22. [Email & Incoming Mail](#22-email--incoming-mail)
23. [Audit Log](#23-audit-log)
24. [Mobile Experience](#24-mobile-experience)
25. [Advanced & Enterprise Features](#25-advanced--enterprise-features)
26. [TaskMagnet Implementation Priority Map](#-taskmagnet-implementation-priority-map)

---

## 1. Project Management

### 1.1 Project Types
| Feature | Description | Priority |
|---|---|---|
| Software Project | Agile/Scrum/Kanban board with backlog | High |
| Business Project | Simple task management without boards | High |
| Service Management Project | ITSM-style with SLAs and queues | Medium |
| Template-based creation | Pre-configured project templates | Medium |
| Project archiving | Archive inactive projects, retain data | Medium |
| Project deletion | Permanent project removal with confirmation | High |
| Project duplication | Clone project config and optionally data | Low |

### 1.2 Project Settings
| Feature | Description | Priority |
|---|---|---|
| Project key | Short unique identifier (e.g., TM, PROJ) | High |
| Project avatar/icon | Custom icon per project | Medium |
| Project description | Markdown-supported description | High |
| Project lead | Assign primary owner | High |
| Project category | Group projects by category | Medium |
| Project URL | Optional external project link | Low |
| Default assignee | Project lead or unassigned | High |
| Issue type scheme | Assign custom issue type sets | High |
| Workflow scheme | Map issue types to workflows | High |
| Screen scheme | Map issue types to screens | Medium |
| Field configuration scheme | Per-type field visibility/requirements | Medium |
| Permission scheme | Assign permission rules | High |
| Notification scheme | Assign notification rules | High |
| Issue security scheme | Restrict issue visibility | Medium |

### 1.3 Project Navigation
- Project overview/summary page
- Scrum board or Kanban board
- Backlog view
- Active sprint view
- Roadmap view
- Issues list view
- Reports section
- Project settings panel
- Project shortcuts and pinned links

---

## 2. Issue & Task Tracking

### 2.1 Issue Types
| Type | Description |
|---|---|
| Epic | Large body of work, groups stories |
| Story | User-facing functionality |
| Task | Technical or non-user-facing work |
| Sub-task | Child of any issue type |
| Bug | Defect or unexpected behavior |
| Improvement | Enhancement to existing functionality |
| New Feature | Wholly new capability |
| Question | Discussion or clarification needed |
| Incident | Production issue or outage |

### 2.2 Issue Fields (Core)
| Field | Type | Notes |
|---|---|---|
| Issue Key | Auto-generated | e.g., TM-42 |
| Summary | Short text | Required |
| Description | Rich text (Markdown/WYSIWYG) | |
| Issue Type | Select | Required |
| Status | Workflow-driven | |
| Priority | Select | Highest/High/Medium/Low/Lowest |
| Assignee | User picker | |
| Reporter | User (auto-filled) | |
| Labels | Multi-select tags | |
| Components | Multi-select project components | |
| Fix Version/s | Release version links | |
| Affects Version/s | Impacted versions | |
| Epic Link | Parent epic reference | |
| Sprint | Sprint assignment | |
| Story Points | Numeric estimation | |
| Original Estimate | Time estimate | |
| Time Spent | Time logged | |
| Remaining Estimate | Auto-calculated | |
| Due Date | Date picker | |
| Created Date | Auto-set | |
| Updated Date | Auto-set | |
| Resolved Date | Set on resolution | |
| Environment | Text for bug context | |
| Security Level | Visibility restriction | |

### 2.3 Issue Lifecycle
- Create issue (form, quick-create, email, API)
- View issue detail page
- Edit any field inline or via form
- Transition issue through workflow statuses
- Assign/reassign to team members
- Watch/unwatch for notifications
- Clone issue (duplicate with optional sub-tasks)
- Move issue between projects
- Link issues (blocks, is blocked by, relates to, duplicates, etc.)
- Delete issue (with permission check)
- Bulk edit/transition/delete multiple issues
- Print/export individual issue to PDF

### 2.4 Issue Detail View
- Full-width summary and description editor
- Activity stream (comments, history, worklogs)
- Sidebar with all metadata fields
- Child issues / sub-tasks panel
- Linked issues panel
- Attachment panel with preview
- Inline field editing
- Status transition buttons
- Watchers list
- Voter list (upvote feature)
- Share issue button (link / email)
- Keyboard shortcuts on issue view

---

## 3. Agile – Scrum

### 3.1 Sprint Management
| Feature | Description |
|---|---|
| Create sprint | Name, start date, end date, goal |
| Start sprint | Move sprint to active (one active at a time) |
| Complete sprint | Move unfinished issues to backlog or next sprint |
| Sprint goal | Visible goal on the board |
| Sprint velocity | Track story points completed per sprint |
| Multiple sprints | Manage future sprints in backlog |
| Parallel sprints | Multiple concurrent active sprints (advanced) |

### 3.2 Scrum Board
| Feature | Description |
|---|---|
| Column-per-status mapping | Map workflow statuses to board columns |
| Card swim lanes | Group by assignee, epic, priority, or none |
| Story point display | Points shown on issue cards |
| Epic color coding | Cards colored by epic |
| Quick filters | Filter board by assignee, label, etc. |
| Board settings | Configure columns, card layout, swimlanes |
| Drag-and-drop cards | Move cards between columns to trigger transitions |
| Collapsed swim lanes | Minimize lanes to reduce noise |
| Sub-task progress bar | Show sub-task completion on parent cards |

### 3.3 Sprint Planning
- Drag issues from backlog to sprint
- Estimate story points and time during planning
- View sprint capacity vs. committed points
- Sprint planning velocity indicator
- Rank issues within sprint by priority

---

## 4. Agile – Kanban

### 4.1 Kanban Board
| Feature | Description |
|---|---|
| WIP limits | Set max items per column |
| WIP limit violation indicators | Highlight columns over limit |
| Continuous backlog | No sprints; issues flow continuously |
| Sub-filter by assignee | Swimlane option |
| Column-based workflow | Map statuses to Kanban columns |
| Backlog option | Optional backlog for Kanban projects |
| Expedite swimlane | High-priority lane at the top |
| Cumulative flow diagram | Visual of work over time |

### 4.2 Kanban Metrics
- Cycle time tracking
- Lead time tracking
- Throughput metrics
- Cumulative flow diagram
- Control chart

---

## 5. Workflow Engine

### 5.1 Workflow Features
| Feature | Description |
|---|---|
| Custom workflow creation | Visual workflow designer |
| Statuses | Define statuses: To Do, In Progress, Done (+ custom) |
| Status categories | Map statuses to To Do / In Progress / Done categories |
| Transitions | Define allowed status changes |
| Transition conditions | Rules that must pass before transition |
| Transition validators | Validate field values before transition |
| Transition post-functions | Automated actions after transition (e.g., assign, notify) |
| Global transitions | Transition usable from any status |
| Directed transitions | From a specific status only |
| Workflow schemes | Assign different workflows per issue type |
| Workflow import/export | Share workflows between projects |
| Active workflow view | See who uses a workflow |
| Workflow diagram | Visual graph of all statuses and transitions |

### 5.2 Common Workflow Statuses
- **To Do** → **In Progress** → **In Review** → **Done**
- **Open** → **In Progress** → **Resolved** → **Closed**
- **Backlog** → **Selected for Development** → **In Progress** → **Done**
- **Reported** → **Triaged** → **In Progress** → **Fixed** → **Verified** → **Closed**

### 5.3 Resolution States
- Fixed
- Won't Fix
- Duplicate
- Incomplete
- Cannot Reproduce
- Done

---

## 6. Backlog Management

### 6.1 Backlog Features
| Feature | Description |
|---|---|
| Issue ranking | Drag-and-drop ordering of issues |
| Sprint sections | Visual grouping of issues per sprint |
| Backlog section | Unassigned-to-sprint issues |
| Issue creation in backlog | Add issue directly in the list |
| Bulk actions | Select multiple issues for bulk operations |
| Epic panel | Filter backlog by epic |
| Version panel | Filter and group by version |
| Quick filters | Filter by assignee, label, etc. |
| Inline editing | Edit summary from backlog list |
| Show epics | Colorize issues by epic in backlog |
| Estimate story points | Set story points inline |

---

## 7. Roadmaps & Planning

### 7.1 Basic Roadmap (Timeline)
| Feature | Description |
|---|---|
| Epic timeline | Visual horizontal bars per epic |
| Date ranges | Set start and end dates for epics/issues |
| Epic creation from roadmap | Create epics directly on timeline |
| Color coding | Color per epic or team |
| Progress indicators | Completion % based on child issues |
| Dependency arrows | Show inter-epic dependencies |
| Child issue rollup | Show story count under epics |
| Zoom levels | Week / Month / Quarter / Year views |

### 7.2 Advanced Roadmap (Jira Plans)
| Feature | Description |
|---|---|
| Cross-project planning | Plan across multiple projects |
| Teams and capacity | Assign teams, set capacity per sprint |
| Scenario planning | What-if analysis for scheduling |
| Dependency management | Cross-project dependency tracking |
| Resources view | Team workload visualization |
| Auto-scheduling | Automatic sprint assignment based on capacity |
| Release management | Plan and track releases on timeline |
| Portfolio view | Executive summary of all plans |
| Hierarchy levels | Epic → Initiative → Theme |
| Committed vs. planned | Track committed scope |

---

## 8. Reporting & Dashboards

### 8.1 Agile Reports
| Report | Description |
|---|---|
| Burndown Chart | Story points remaining per day in sprint |
| Burnup Chart | Work completed vs. total scope |
| Velocity Chart | Average story points per sprint over time |
| Sprint Report | Issues completed, incomplete, added mid-sprint |
| Cumulative Flow Diagram | Issues in each status over time |
| Control Chart | Cycle time distribution |
| Epic Report | Progress toward completing an epic |
| Release Burndown | Progress toward a version/release |
| Version Report | Issues completed for a release |

### 8.2 Project Reports
| Report | Description |
|---|---|
| Created vs. Resolved Chart | Issue creation vs. resolution rates |
| Pie Chart | Issue distribution by field (e.g., priority) |
| Average Age Chart | Average age of unresolved issues |
| Resolution Time Report | How long issues take to resolve |
| Workload Pie Chart | Issues assigned per team member |
| Time Tracking Report | Original estimate vs. time spent |
| User Workload Report | Tasks per user |

### 8.3 Dashboards
| Feature | Description |
|---|---|
| Personal dashboard | User-configurable home screen |
| Project dashboard | Project-specific metrics |
| System dashboard | Default global dashboard |
| Gadgets / Widgets | Plug-in charts and lists |
| Dashboard sharing | Share with teams or globally |
| Dashboard favoriting | Pin frequently used dashboards |
| Layout options | 1, 2, or 3 column layout |

### 8.4 Dashboard Gadgets
- Assigned to Me
- Activity Stream
- Sprint Stats
- Burndown Chart
- Created vs. Resolved
- Filter Results (issue list)
- Issue Statistics (pie/bar)
- Recently Created Issues
- Two Dimensional Filter Statistics
- Workload Pie Chart
- In Progress Issues
- Labels Cloud

---

## 9. Search & Filtering (JQL)

### 9.1 Search Modes
- **Basic Search**: Field-based dropdowns for criteria
- **Advanced Search (JQL)**: Query Language for complex filtering
- **Text Search**: Full-text search across issue fields
- **Quick Search**: Global search from navigation bar

### 9.2 JQL (Jira Query Language) Features
```sql
-- Example queries
project = "TM" AND status = "In Progress" AND assignee = currentUser()
priority in (High, Highest) AND created >= -7d ORDER BY created DESC
sprint in openSprints() AND issueType = Story AND "Story Points" > 5
labels = "bug" AND fixVersion = "v2.0" AND resolution = Unresolved
```

### 9.3 JQL Functions
| Function | Description |
|---|---|
| `currentUser()` | The logged-in user |
| `membersOf("group")` | All members of a group |
| `openSprints()` | All currently active sprints |
| `closedSprints()` | All closed sprints |
| `futureSprints()` | Upcoming sprints |
| `issueHistory()` | Issues recently viewed by user |
| `watchedIssues()` | Issues the user watches |
| `votedIssues()` | Issues the user voted for |
| `linkedIssues("TM-1")` | Issues linked to a specific issue |
| `subtasksOf("TM-1")` | Sub-tasks of an issue |
| `parentsOf("TM-5")` | Parent issues |
| `releasedVersions()` | Released fix versions |
| `unreleasedVersions()` | Unreleased fix versions |
| `startOfDay()` / `endOfDay()` | Day boundaries |
| `startOfWeek()` / `endOfWeek()` | Week boundaries |
| `startOfMonth()` / `endOfMonth()` | Month boundaries |

### 9.4 Saved Filters
- Save JQL query as a named filter
- Share filters with users, groups, or globally
- Subscribe to filter (email digest)
- Use filters in dashboards and boards
- Favorite filters for quick access
- Filter management (edit, delete, clone)

---

## 10. Notifications & Subscriptions

### 10.1 Notification Triggers
| Event | Description |
|---|---|
| Issue Created | When a new issue is created |
| Issue Updated | When any field changes |
| Issue Deleted | When an issue is removed |
| Issue Assigned | When assignee changes |
| Issue Resolved | When issue moves to resolved status |
| Issue Closed | When issue is closed |
| Issue Commented | When a new comment is added |
| Comment Edited | When an existing comment is modified |
| Comment Deleted | When a comment is removed |
| Worklog Added | When time is logged |
| Issue Moved | When issue is moved between projects |
| Watcher Added | When user starts watching |
| Priority Changed | When priority is updated |
| Status Transition | On specific workflow transitions |
| Sprint Started | When a sprint begins |
| Sprint Completed | When a sprint ends |

### 10.2 Notification Recipients
- Issue Reporter
- Current Assignee
- All Watchers
- Project Lead
- All Project Roles
- Specific User
- Specific Group
- Current User (self)
- The user who triggered the event

### 10.3 Notification Schemes
- Project-level notification schemes
- Custom notification event-to-recipient mapping
- Email digest options (immediate, daily, weekly)
- In-app notification center
- Push notification support (mobile)
- @mention notifications in comments
- Slack/Teams integration for notifications

---

## 11. User & Team Management

### 11.1 User Management
| Feature | Description |
|---|---|
| User registration | Self-register or admin-invite |
| User profile | Avatar, display name, time zone, locale |
| User deactivation | Disable without deleting account or data |
| User directory | Browse/search all users |
| Group management | Logical user groups for permissions |
| User import | Bulk import via CSV |
| Directory sync | LDAP/Active Directory synchronization |
| SSO integration | SAML 2.0 / OAuth 2.0 / OIDC |

### 11.2 Team Collaboration
| Feature | Description |
|---|---|
| Project roles | Developer, Viewer, Project Manager, etc. |
| Role-member assignment | Add users/groups to project roles |
| @mention in comments | Notify specific users inline |
| Watching issues | Subscribe to issue change notifications |
| Voting on issues | Upvote to indicate importance |
| User activity feed | Recent actions by team members |
| Shared dashboards | Team-wide dashboards |
| Team workload view | Issues assigned per team member |

### 11.3 Project Roles (Default)
- Administrators
- Developers
- Project Managers
- Team Leads
- Viewers
- Service Desk Team

---

## 12. Permissions & Security

### 12.1 Global Permissions
| Permission | Description |
|---|---|
| System Administrators | Full system administration |
| Administrators | Administrative functions |
| Browse Users | Search and select users in pickers |
| Create Shared Objects | Create shared filters and dashboards |
| Manage Group Filter Subscriptions | Manage group-level subscriptions |
| Bulk Change | Perform bulk issue operations |

### 12.2 Project Permissions
| Permission | Description |
|---|---|
| Browse Projects | View project and its issues |
| Create Issues | Create any issue type |
| Edit Issues | Modify issue fields |
| Delete Issues | Permanently remove issues |
| Assign Issues | Set assignee field |
| Assignable User | Be selectable as assignee |
| Resolve Issues | Transition to resolved |
| Close Issues | Transition to closed |
| Link Issues | Create issue links |
| Set Issue Security | Set security levels |
| Modify Reporter | Change reporter field |
| View Voters/Watchers | See who votes/watches |
| Manage Watchers | Add/remove watchers for others |
| Create Attachments | Upload attachments |
| Delete Attachments | Remove attachments |
| Create Comments | Post comments |
| Edit All Comments | Edit any comment |
| Edit Own Comments | Edit own comments only |
| Delete All Comments | Delete any comment |
| Delete Own Comments | Delete own comments only |
| Work On Issues | Log time on issues |
| Edit Own Worklogs | Edit own time logs |
| Delete Own Worklogs | Delete own time logs |
| Edit All Worklogs | Edit any time log |
| Delete All Worklogs | Delete any time log |
| View Development Tools | See dev-related panel |
| Administer Projects | Manage project settings |
| Schedule Issues | Set or change due dates |
| Move Issues | Move issues to another project |

### 12.3 Permission Schemes
- Create named permission scheme
- Assign scheme to one or multiple projects
- Grant permissions to users, groups, or roles
- Issue security levels (restrict who sees an issue)
- Issue security schemes (collection of levels)

### 12.4 Audit & Compliance
- Global audit log for admin actions
- User login/logout tracking
- Permission change audit
- Data export for compliance
- GDPR support (user data deletion)

---

## 13. Time Tracking & Worklogs

### 13.1 Time Logging Features
| Feature | Description |
|---|---|
| Log work | Time spent, remaining estimate, start time, description |
| Auto-adjust remaining | Reduce remaining estimate automatically |
| Manual remaining | Set remaining manually |
| Time format | Configurable: days/hours/minutes |
| Working hours per day | Configure work hours for time calculation |
| Working days per week | Configure work days |
| Worklog visibility | Restrict to roles or security levels |
| Edit/delete worklog | Update or remove logged time |
| Time tracking report | Export time logs per user/project |

### 13.2 Estimation
- Original Estimate field
- Remaining Estimate field (auto or manual)
- Story Points (sprint-based agile estimation)
- T-shirt sizing via custom fields
- Planning poker integration (third-party)

---

## 14. Comments & Collaboration

### 14.1 Comment Features
| Feature | Description |
|---|---|
| Rich text editor | Markdown / WYSIWYG |
| @mentions | Notify users inline |
| Restrict comment | Limit visibility to a role or group |
| Edit comment | Author with timestamp of edit |
| Delete comment | With permission |
| Comment ordering | Oldest first / Newest first |
| Comment reactions | Emoji reactions to comments |
| Quote reply | Reference a previous comment |
| Activity tab | Unified comments + history view |

### 14.2 Issue Activity Stream
- Comments (newest/oldest ordering)
- Field change history (who changed what, when)
- Worklog entries
- Status transitions
- Attachment additions/removals
- Link additions/removals
- Filter activity by type

---

## 15. Attachments & Media

### 15.1 Attachment Features
| Feature | Description |
|---|---|
| File upload | Drag-and-drop or file picker |
| Image preview | Inline image thumbnail preview |
| Video/audio preview | Basic media preview |
| Multiple attachments | Upload batch at once |
| Attachment size limit | Configurable admin limit |
| Download attachment | Download any file |
| Delete attachment | With permission |
| Storage management | Admin view of storage usage |
| Virus scanning | Integration with scanning tools |

### 15.2 Screenshot & Screen Capture
- Paste from clipboard directly into issue
- Annotate screenshots before attaching

---

## 16. Labels, Components & Versions

### 16.1 Labels
- Global label pool (shared across projects)
- Multi-select on issues
- Label autocomplete
- Filter by label in backlog/board
- Label statistics gadget

### 16.2 Components
| Feature | Description |
|---|---|
| Component creation | Name, description, lead, default assignee |
| Component assignment | Multi-select on issues |
| Component lead | Auto-assign to component lead |
| Component report | Issues per component |
| Delete/merge components | Management actions |

### 16.3 Versions (Releases)
| Feature | Description |
|---|---|
| Version creation | Name, description, start date, release date |
| Mark as released | Archives released version |
| Mark as archived | Hide from pickers |
| Version sequence | Reorder versions |
| Fix Version field | Target release on issue |
| Affects Version field | Impacted release on issue |
| Version report | Progress toward release |
| Release burndown | Story points/issues remaining |
| Unrelease | Rollback released status |

---

## 17. Custom Fields

### 17.1 Built-in Custom Field Types
| Type | Description |
|---|---|
| Text Field (single line) | Free text, single line |
| Text Area (multi-line) | Free text, multi-line |
| Number Field | Numeric input |
| Date Picker | Calendar date selector |
| Date-Time Picker | Date + time selector |
| URL Field | URL with link rendering |
| User Picker (single) | Select one user |
| User Picker (multiple) | Select multiple users |
| Group Picker (single) | Select one group |
| Group Picker (multiple) | Select multiple groups |
| Select List (single) | Dropdown with custom options |
| Select List (multiple) | Multi-select with custom options |
| Checkbox | Boolean flag |
| Radio Buttons | Single select with radio UI |
| Cascading Select | Parent-child dropdowns |
| Labels | Tag-style multi-select |
| Version Picker (single) | Select one version |
| Version Picker (multiple) | Select multiple versions |
| Project Picker | Select a project |
| Read-Only Text | Calculated or fixed display value |

### 17.2 Custom Field Management
- Create / edit / delete custom fields globally
- Add to screens (create, edit, view, transition)
- Configure field context (scope by project or issue type)
- Set field as required
- Set default value
- Custom field ordering on screens
- Field configuration schemes

---

## 18. Issue Links & Dependencies

### 18.1 Link Types (Default)
| Link Type | Inward | Outward |
|---|---|---|
| Blocks | is blocked by | blocks |
| Cloners | is cloned by | clones |
| Duplicate | is duplicated by | duplicates |
| Relates To | relates to | relates to |
| Problem/Incident | is caused by | causes |
| Dependency | depends on | is a dependency of |
| Epic-Story | is child of | is parent of |

### 18.2 Linking Features
- Add link from issue detail view
- Link to issues in the same or other projects
- Link to external URLs (web links)
- View linked issues with summary and status
- Remove links
- Show link direction visually
- Search by linked issue in JQL

### 18.3 Issue Hierarchy
```
Initiative / Theme (optional)
    └── Epic
          └── Story / Task / Bug / Feature
                    └── Sub-task
```

---

## 19. Automation Rules

### 19.1 Triggers
| Trigger | Description |
|---|---|
| Issue created | When a new issue is created |
| Issue updated | When any field changes |
| Issue transitioned | When status changes |
| Field value changed | When a specific field changes |
| Comment added | When a comment is posted |
| Comment edited | When a comment is modified |
| Issue assigned | When assignee changes |
| Issue linked | When a link is added |
| Sprint started | When sprint begins |
| Sprint completed | When sprint ends |
| Scheduled | Time-based (e.g., daily at midnight) |
| Manual trigger | User-initiated via button |
| Incoming webhook | External HTTP trigger |
| Child issue created | When sub-task is created |

### 19.2 Conditions
- Issue matches JQL filter
- Assignee is / is not
- Status is / is not
- Field has / has no value
- User is in group / role
- Issue type is
- Priority is
- Trigger user is

### 19.3 Actions
| Action | Description |
|---|---|
| Create issue | Creates a new issue |
| Edit issue | Update any field |
| Transition issue | Change status |
| Assign issue | Set or change assignee |
| Add comment | Post a comment |
| Add label | Add a tag |
| Remove label | Remove a tag |
| Log work | Record time entry |
| Send email | Email notification |
| Send webhook | HTTP call to external service |
| Create sub-task | Create a child issue |
| Link issues | Create a link between issues |
| Branch rule | Conditional logic |
| Then branch / Else branch | If/else logic |

### 19.4 Automation Templates (Common)
- Auto-assign issue to reporter
- Close issues when all sub-tasks are done
- Notify team on issue priority change
- Auto-transition parent when all children complete
- Weekly unresolved issue summary email
- Create sub-tasks when epic is created
- Sync status across linked issues
- Add label when due date is approaching

---

## 20. Integrations & API

### 20.1 REST API Endpoints
| Endpoint Category | Description |
|---|---|
| Issues API | CRUD for issues, transitions, comments, worklogs |
| Projects API | Project management operations |
| Users API | User and group management |
| Boards API | Agile board and sprint operations |
| Search API | JQL-based issue search |
| Filters API | Saved filter management |
| Dashboards API | Dashboard and gadget operations |
| Versions API | Release version management |
| Components API | Project component management |
| Workflows API | Workflow information |
| Permissions API | Permission checks |
| Webhooks API | Webhook registration |
| Audit Log API | Retrieve audit records |

### 20.2 Webhooks
- Register webhooks for specific events
- Select JQL filter to scope events
- Payload includes full issue data as JSON
- Retry logic on failure
- Webhook log/delivery history

### 20.3 Native Integrations
| Integration | Type |
|---|---|
| Confluence | Documentation and knowledge base |
| Bitbucket | Source control and PR tracking |
| GitHub | Repository linking and commit tracking |
| GitLab | CI/CD and merge request linking |
| Slack | Notification and command integration |
| Microsoft Teams | Notification integration |
| Opsgenie | Incident management |
| PagerDuty | On-call and alerting |
| Zendesk | Customer ticket linking |
| Salesforce | CRM issue linking |
| VS Code | Issue browsing in IDE |
| IntelliJ IDEA | Issue tracking in IDE |
| Jenkins | Build and deployment linking |
| CircleCI | CI pipeline status |

### 20.4 Developer Panel
- Linked branches per issue
- Linked commits per issue
- Linked pull/merge requests
- Build status per issue
- Deployment status per environment
- Feature flags linkage

---

## 21. Administration & Configuration

### 21.1 System Settings
| Setting | Description |
|---|---|
| Base URL | Application base URL |
| Email server (SMTP) | Outgoing mail configuration |
| Incoming mail | POP3/IMAP for email-to-issue |
| User directories | LDAP/AD/Internal |
| SSO providers | SAML/OAuth configuration |
| Indexing | Search index management |
| Database | Connection and performance settings |
| File storage | Attachment storage configuration |
| Backup & restore | Manual and scheduled backups |
| License management | Seat count and license info |
| Look & feel | Logo, colors, announcement banner |
| Default language | System-wide locale |
| Time zone | Server time zone |
| Issue collector | Embeddable feedback widget |
| Advanced settings | Fine-grained config properties |

### 21.2 Issue Type Management
- Create / edit / delete issue types
- Set icon per issue type
- Organize in issue type schemes
- Sub-task designation flag
- Issue type screen schemes (per project)

### 21.3 Workflow Management
- Visual workflow designer (drag-and-drop)
- Status creation and categorization
- Transition configuration with conditions/validators/post-functions
- Workflow scheme management
- Import/export workflows as XML
- Active workflow viewer

### 21.4 Screen Management
- Create screens (field layouts)
- Assign screens to operations: Create / Edit / View / Transition
- Screen schemes (per issue type)
- Issue type screen scheme (per project)
- Field ordering on screens
- Required field enforcement

### 21.5 Field Configuration
- Make fields required, optional, or hidden
- Set renderer (text vs. wiki vs. rich text)
- Field configuration schemes per project
- Global field defaults

---

## 22. Email & Incoming Mail

### 22.1 Outgoing Mail
- SMTP configuration with authentication
- TLS/SSL support
- Test email functionality
- HTML email templates
- Per-notification email format
- Email batching (group updates)

### 22.2 Incoming Mail (Email-to-Issue)
- POP3 / IMAP mailbox polling
- Map email subject → issue summary
- Map email body → issue description
- Map sender → reporter
- Configure target project and issue type
- Strip quoted replies in update comments
- Handle attachments from email
- Error handling for unparseable emails

---

## 23. Audit Log

### 23.1 Audited Events
- User login/logout and failed attempts
- User created, updated, deactivated, deleted
- Group created, updated, deleted
- Permission scheme created/modified
- Project created, updated, archived, deleted
- Role assignment changes
- Workflow changes
- Configuration changes (global settings)
- License changes
- Plugin/app install, remove, enable, disable
- Data export and import

### 23.2 Audit Log Features
- Date-range filtering
- Search by user, action, or resource
- Export audit log (CSV/Excel)
- Log retention policy configuration
- Real-time log streaming to SIEM
- Tamper-evident logging
- GDPR compliance actions logged

---

## 24. Mobile Experience

### 24.1 Mobile App Features
| Feature | iOS/Android |
|---|---|
| Issue browsing and search | ✅ |
| Issue creation | ✅ |
| Comment and update issues | ✅ |
| Transition issues | ✅ |
| View board (Scrum/Kanban) | ✅ |
| Backlog management (basic) | ✅ |
| Sprint management | ✅ |
| Notifications (push) | ✅ |
| Camera attachment | ✅ |
| Offline mode (read-only) | ✅ |
| Dashboard and gadgets | Partial |
| Reporting | Partial |
| Admin settings | ❌ |

### 24.2 Progressive Web App (PWA)
- Responsive web design for all screen sizes
- PWA installable from browser
- Offline read cache
- Push notifications via service worker
- Add to home screen support

---

## 25. Advanced & Enterprise Features

### 25.1 Service Management (ITSM)
| Feature | Description |
|---|---|
| Customer portal | External-facing issue submission form |
| Request types | Customer-friendly issue forms |
| SLA policies | Response/resolution time targets |
| SLA calendars | Business hours configuration |
| SLA reporting | Breach and compliance reports |
| Queues | Agent issue queue management |
| Asset management | CMDB and asset tracking |
| Change management | Change advisory board workflows |
| Incident management | Linked alert-to-incident workflow |
| Knowledge base | Self-service article integration |
| Customer satisfaction (CSAT) | Post-resolution ratings |

### 25.2 Advanced Roadmaps (Plans)
- Cross-project planning
- Capacity planning per sprint/team
- Scenario comparison
- Dependency timeline
- Automatic scheduling suggestions
- Team assignment and workload

### 25.3 Product Discovery (Ideation)
- Idea capture and prioritization
- Impact scoring
- Roadmap view from ideas to epics
- Customer feedback aggregation
- Voting on ideas

### 25.4 Enterprise / Self-Hosted
| Feature | Description |
|---|---|
| Clustering | Active-active node clustering |
| Smart mirroring | Geographic distribution |
| CDN support | Asset delivery optimization |
| Zero-downtime upgrades | Rolling restart capability |
| SAML 2.0 SSO | Enterprise identity providers |
| Personal Access Tokens | API token management |
| Rate limiting | Per-user/global API rate limits |
| IP whitelisting | Restrict access by IP range |
| Data sovereignty | On-premise data control |
| Custom data retention | Define how long data is kept |
| APM integration | Application performance monitoring |

### 25.5 Analytics & Insights
- Cross-project issue metrics
- Team performance analytics
- Predictive issue completion
- Custom metric dashboards
- Export to BI tools (CSV, API)

---

## 🗺️ TaskMagnet Implementation Priority Map

### Phase 1 – MVP Core (Must Have)
- [ ] User registration, login, JWT authentication
- [ ] Project creation and management (Software & Business project types)
- [ ] Issue types: Epic, Story, Task, Bug, Sub-task
- [ ] Core issue fields (summary, description, assignee, priority, status, labels)
- [ ] Basic Scrum board with drag-and-drop columns
- [ ] Backlog management with sprint assignment
- [ ] Sprint create / start / complete lifecycle
- [ ] Basic workflow (To Do → In Progress → Done)
- [ ] Comments on issues
- [ ] File attachments
- [ ] Role-based permissions (Admin, Project Manager, Developer, Viewer)
- [ ] Basic email notifications
- [ ] REST API for all core operations
- [ ] Issue key auto-generation (e.g., TM-1, TM-2)
- [ ] Issue detail view with activity stream
- [ ] User profile and avatar

### Phase 2 – Core Agile (Should Have)
- [ ] Kanban board with WIP limits
- [ ] Custom workflows (visual designer)
- [ ] Burndown and burnup charts
- [ ] Sprint report and velocity chart
- [ ] Advanced search with JQL-style query language
- [ ] Saved filters and filter sharing
- [ ] Labels, components, and versions/releases
- [ ] Issue linking (blocks, duplicates, relates to)
- [ ] Watchers and voting on issues
- [ ] Dashboard with configurable gadgets
- [ ] Time tracking (worklogs, original/remaining estimates)
- [ ] Activity stream on issues (field history)
- [ ] Bulk edit and bulk transition
- [ ] Clone/duplicate issues
- [ ] Move issues between projects
- [ ] Cumulative flow diagram
- [ ] Epic report and version report

### Phase 3 – Advanced Features (Nice to Have)
- [ ] Custom fields (all field types)
- [ ] Automation rules engine (triggers, conditions, actions)
- [ ] Roadmap / timeline view for epics
- [ ] Cross-project search and reporting
- [ ] Notification schemes (custom event-to-recipient mapping)
- [ ] OAuth login (Google, GitHub, Microsoft)
- [ ] Incoming webhooks
- [ ] Outgoing webhooks
- [ ] Incoming email-to-issue
- [ ] In-app notification center
- [ ] @mention notifications
- [ ] Issue security levels
- [ ] Dashboard sharing and favoriting
- [ ] Mobile PWA (responsive + installable)
- [ ] Control chart (cycle time)
- [ ] User workload report

### Phase 4 – Enterprise (Future)
- [ ] Advanced cross-project roadmaps with capacity planning
- [ ] SAML 2.0 / LDAP / Active Directory integration
- [ ] Audit log export (CSV/Excel) and SIEM streaming
- [ ] ITSM / Service Desk module (customer portal, SLAs, queues)
- [ ] Analytics dashboard with predictive metrics
- [ ] Multi-tenant / organization management
- [ ] Rate limiting and IP whitelisting
- [ ] Personal access tokens for API
- [ ] Plugin/extension framework
- [ ] Data retention policy configuration
- [ ] GDPR tooling (user data export and deletion)
- [ ] Clustering and high-availability deployment

---

## 📚 References

- [Jira Software Documentation](https://support.atlassian.com/jira-software-cloud/)
- [Jira REST API Reference](https://developer.atlassian.com/cloud/jira/platform/rest/v3/)
- [Jira Agile Boards Documentation](https://support.atlassian.com/jira-software-cloud/docs/what-is-a-board/)
- [Jira Automation Documentation](https://support.atlassian.com/cloud-automation/docs/jira-cloud-automation/)
- [JQL Reference](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/)
- [Jira Workflow Documentation](https://support.atlassian.com/jira-software-cloud/docs/what-is-a-workflow/)

---

*Created: March 15, 2026*
*Version: 1.0*
*Purpose: TaskMagnet – Jira Feature Parity Reference*
