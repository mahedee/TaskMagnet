# Technology Stack Document
**TaskMagnet Project – Tools & Technologies Reference**

---

## 📋 Document Information
- **Version**: 1.0
- **Date**: March 15, 2026
- **Status**: Active Reference
- **Purpose**: Definitive technology choices for the TaskMagnet platform

---

## 🎯 Overview

TaskMagnet is a full-featured Jira-equivalent project management system. The technology stack is chosen to support enterprise-grade reliability, security, scalability, and developer productivity. The primary database is **Oracle**, mirroring Jira's own Oracle support, with a modern Spring Boot backend and React frontend.

---

## 🏗️ Architecture Summary

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│         React 19 + TypeScript + Vite + Ant Design               │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTPS / WebSocket
┌─────────────────────────▼───────────────────────────────────────┐
│                      API GATEWAY / PROXY                        │
│                         Nginx                                   │
└─────────────────────────┬───────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────────┐
│                      BACKEND LAYER                              │
│          Spring Boot 3.x + Spring Security 6 + Java 21          │
│        REST API │ WebSocket/STOMP │ Spring Mail │ Scheduler      │
└──────┬──────────┬─────────┬──────────┬──────────┬──────────────┘
       │          │         │          │          │
  ┌────▼───┐ ┌───▼───┐ ┌───▼──┐ ┌────▼────┐ ┌───▼──────┐
  │Oracle  │ │ Redis │ │MinIO │ │Rabbit   │ │Elastic   │
  │21c XE  │ │       │ │      │ │   MQ    │ │  search  │
  └────────┘ └───────┘ └──────┘ └─────────┘ └──────────┘
```

---

## 1. Frontend Technologies

### 1.1 Core Framework
| Technology | Version | Purpose |
|---|---|---|
| **React** | 19.x | UI component framework |
| **TypeScript** | 5.x | Type-safe JavaScript |
| **Vite** | 6.x | Build tool and dev server (replaces CRA) |

> **Note**: Migrate from Create React App (CRA) to Vite. CRA is deprecated and Vite is 10-20x faster in dev mode.

### 1.2 UI Component Library
| Technology | Version | Purpose |
|---|---|---|
| **Ant Design** | 5.x | Enterprise UI components (tables, forms, modals, menus, trees) |
| **Lucide React** | Latest | Icon library |
| **Ant Design Charts** | 2.x | Built-in chart components |

### 1.3 State Management
| Technology | Version | Purpose |
|---|---|---|
| **Redux Toolkit** | 2.x | Global application state |
| **RTK Query** | (bundled with RTK) | Server-state caching, data fetching, API layer |

### 1.4 Routing
| Technology | Version | Purpose |
|---|---|---|
| **React Router** | v6.x | Client-side routing, protected routes, nested layouts |

### 1.5 Forms & Validation
| Technology | Version | Purpose |
|---|---|---|
| **React Hook Form** | 7.x | Performant form state management |
| **Zod** | 3.x | Schema validation with TypeScript inference |

### 1.6 Rich Text & Content
| Technology | Version | Purpose |
|---|---|---|
| **TipTap** | 2.x | Rich text editor for issue descriptions and comments |

### 1.7 Drag & Drop
| Technology | Version | Purpose |
|---|---|---|
| **@dnd-kit/core** | 6.x | Kanban board drag-and-drop, backlog ranking |

### 1.8 Data Display
| Technology | Version | Purpose |
|---|---|---|
| **TanStack Table v8** | 8.x | Issue list view with sort, filter, pagination |
| **TanStack Virtual** | 3.x | Virtualized lists for large backlogs |

### 1.9 Charts & Reporting
| Technology | Version | Purpose |
|---|---|---|
| **Recharts** | 2.x | Burndown, burnup, velocity, CFD charts |

### 1.10 Date & Time
| Technology | Version | Purpose |
|---|---|---|
| **Day.js** | 1.x | Date formatting, relative times, sprint date calculations |

### 1.11 Timeline / Roadmap
| Technology | Version | Purpose |
|---|---|---|
| **Frappe Gantt** or **react-gantt-task** | Latest | Epic timeline / roadmap view |

### 1.12 Real-Time
| Technology | Version | Purpose |
|---|---|---|
| **SockJS-client** | 1.x | WebSocket fallback transport |
| **@stomp/stompjs** | 7.x | STOMP protocol client for live board updates |

### 1.13 HTTP Client
| Technology | Version | Purpose |
|---|---|---|
| **Axios** | 1.x | HTTP requests with JWT interceptors and refresh logic |

### 1.14 Testing (Frontend)
| Technology | Version | Purpose |
|---|---|---|
| **Vitest** | 2.x | Fast unit and component test runner |
| **React Testing Library** | 16.x | Component behavioral testing |
| **Playwright** | 1.x | End-to-end browser testing |

---

## 2. Backend Technologies

### 2.1 Core Framework
| Technology | Version | Purpose |
|---|---|---|
| **Java** | 21 LTS | Primary language (LTS, virtual threads via Project Loom) |
| **Spring Boot** | 3.3.x | Application framework |
| **Spring Web MVC** | (bundled) | REST API controllers |
| **Spring WebSocket** | (bundled) | WebSocket + STOMP for real-time features |
| **Maven** | 3.9.x | Multi-module build tool |

### 2.2 Security
| Technology | Version | Purpose |
|---|---|---|
| **Spring Security** | 6.x | Authentication, authorization, filters |
| **jjwt** | 0.12.x | JWT access and refresh token generation/validation |
| **BCrypt** | (Spring Security) | Password hashing |
| **Spring Security OAuth2 Client** | 6.x | Google, GitHub social login |
| **Bucket4j** | 8.x | API rate limiting and brute-force protection |

> **Note**: Upgrade from jjwt 0.11.5 → 0.12.x. The 0.11.x API is deprecated; 0.12.x has a cleaner builder pattern and RS256/RS512 support.

### 2.3 Data Access
| Technology | Version | Purpose |
|---|---|---|
| **Spring Data JPA** | 3.x | Repository abstraction over Hibernate |
| **Hibernate** | 6.x | ORM, entity mapping |
| **Hibernate Envers** | 6.x | Automatic entity change history (powers activity stream) |
| **HikariCP** | 5.x | High-performance database connection pooling |
| **Flyway** | 10.x | Version-controlled database schema migrations |

### 2.4 Database
| Technology | Version | Purpose |
|---|---|---|
| **Oracle Database XE** | 21c | Primary relational database |
| **Oracle JDBC Driver** | ojdbc11 21.9.x | Java JDBC connectivity to Oracle |

> Oracle is the primary data store for all transactional and relational data — users, projects, issues, sprints, workflows, permissions, and audit logs.

### 2.5 Caching
| Technology | Version | Purpose |
|---|---|---|
| **Redis** | 7.x | Session cache, permission cache, token blacklist, rate limit counters |
| **Spring Data Redis** | 3.x | Redis integration for Spring |
| **Spring Cache** | (bundled) | `@Cacheable` abstraction over Redis |

### 2.6 Search
| Technology | Version | Purpose |
|---|---|---|
| **Elasticsearch** | 8.x | Full-text issue search, JQL text clauses (`~` operator) |
| **Spring Data Elasticsearch** | 5.x | Elasticsearch repository and template |

> Mirrors how Jira uses Lucene/Elasticsearch internally for its search index. Oracle Text can be used as a simpler alternative for early phases.

### 2.7 Messaging & Events
| Technology | Version | Purpose |
|---|---|---|
| **RabbitMQ** | 3.13.x | Async messaging for notifications, automation triggers, webhooks |
| **Spring AMQP** | 3.x | RabbitMQ integration |
| **Spring Events** | (bundled) | In-process event bus for module decoupling |

### 2.8 File Storage
| Technology | Version | Purpose |
|---|---|---|
| **MinIO** | Latest | S3-compatible self-hosted object storage for attachments and avatars |
| **AWS SDK for Java** | 2.x | MinIO/S3 client (S3-compatible API) |

### 2.9 Email
| Technology | Version | Purpose |
|---|---|---|
| **Spring Mail** | 3.x | SMTP email sending |
| **Thymeleaf** | 3.x | HTML email templates |
| **Jakarta Mail** | 2.x | JavaMail API |

### 2.10 Scheduling & Automation
| Technology | Version | Purpose |
|---|---|---|
| **Quartz Scheduler** | 2.3.x | Persistent scheduled jobs (automation rules, SLA monitoring, digests) |
| **Spring `@Scheduled`** | (bundled) | Simple periodic tasks (health checks, cleanup jobs) |

### 2.11 Mapping
| Technology | Version | Purpose |
|---|---|---|
| **MapStruct** | 1.6.x | High-performance entity ↔ DTO mapping (compile-time generated) |
| **Lombok** | 1.18.x | Boilerplate reduction (`@Data`, `@Builder`, `@Slf4j`) |

### 2.12 API Documentation
| Technology | Version | Purpose |
|---|---|---|
| **Springdoc OpenAPI** | 2.5.x | Auto-generated OpenAPI 3.0 spec + Swagger UI |

### 2.13 Validation
| Technology | Version | Purpose |
|---|---|---|
| **Hibernate Validator** | 8.x | Bean Validation (JSR-380) annotations |
| **Spring Validation** | (bundled) | `@Valid` / `@Validated` controller integration |

### 2.14 Export & Reports
| Technology | Version | Purpose |
|---|---|---|
| **Apache POI** | 5.x | Excel (.xlsx) report export |
| **iText / OpenPDF** | 8.x / 1.x | PDF export for issues and reports |

### 2.15 Monitoring & Observability
| Technology | Version | Purpose |
|---|---|---|
| **Spring Boot Actuator** | 3.x | Health checks, metrics endpoints |
| **Micrometer** | 1.13.x | Metrics facade (Prometheus, Grafana) |
| **SLF4J + Logback** | 2.x | Structured JSON logging |

### 2.16 Testing (Backend)
| Technology | Version | Purpose |
|---|---|---|
| **JUnit 5** | 5.x | Unit test framework |
| **Mockito** | 5.x | Mocking framework |
| **Spring Boot Test** | 3.x | Integration test context |
| **Testcontainers** | 1.20.x | Spin up real Oracle/Redis/RabbitMQ in tests |
| **REST Assured** | 5.x | API integration testing |

---

## 3. Infrastructure & DevOps

### 3.1 Containerization
| Technology | Version | Purpose |
|---|---|---|
| **Docker** | 26.x | Containerize all services |
| **Docker Compose** | 2.x | Local development environment orchestration |

**Docker Compose services (local dev):**
```yaml
services:
  oracle-db     # Oracle XE 21c
  redis         # Redis 7
  rabbitmq      # RabbitMQ 3.13 with management UI
  minio         # MinIO object storage
  elasticsearch # Elasticsearch 8
  kibana        # Kibana (log/search UI) - optional
  mailhog       # Local SMTP trap for email dev/testing
```

### 3.2 Reverse Proxy & Web Server
| Technology | Version | Purpose |
|---|---|---|
| **Nginx** | 1.26.x | Serve React frontend + proxy `/api` to Spring Boot + SSL termination |

### 3.3 CI/CD
| Technology | Version | Purpose |
|---|---|---|
| **GitHub Actions** | — | Automated build, test, and deployment pipeline |
| **Docker Hub / GHCR** | — | Container image registry |

### 3.4 Monitoring Stack
| Technology | Version | Purpose |
|---|---|---|
| **Prometheus** | 2.x | Metrics scraping from Spring Actuator |
| **Grafana** | 10.x | Metrics dashboards and alerting |

### 3.5 Log Management
| Technology | Version | Purpose |
|---|---|---|
| **Elasticsearch** | 8.x | Log storage and search (shared with app search) |
| **Logstash** | 8.x | Log ingestion and transformation |
| **Kibana** | 8.x | Log visualization and dashboards |

### 3.6 Secret & Config Management
| Technology | Purpose |
|---|---|
| **Spring Profiles** | Environment-specific config (dev, test, prod) |
| **Environment variables** | Secrets injection at runtime (never hardcoded) |
| **Spring Cloud Config** *(future)* | Centralized config server for multi-instance deployments |

### 3.7 Database Schema Management
| Technology | Version | Purpose |
|---|---|---|
| **Flyway** | 10.x | Versioned SQL migration scripts for Oracle |

---

## 4. Development Tools

### 4.1 IDE & Editor
| Tool | Purpose |
|---|---|
| **IntelliJ IDEA** (recommended) or **VS Code** | Backend Java development |
| **VS Code** | Frontend TypeScript/React development |
| **DBeaver** or **Oracle SQL Developer** | Oracle database GUI |
| **RedisInsight** | Redis data browser |
| **RabbitMQ Management UI** | Message broker monitoring (built into RabbitMQ) |

### 4.2 API Testing
| Tool | Purpose |
|---|---|
| **Postman** | Manual API testing and collection management |
| **Swagger UI** | Auto-generated interactive API docs (via Springdoc) |
| **REST Assured** | Automated API integration tests |

### 4.3 Version Control
| Tool | Purpose |
|---|---|
| **Git** | Source control |
| **GitHub** | Remote repository, PR workflow, GitHub Actions CI/CD |

---

## 5. Technology Decision Summary

### Why Oracle?
- Full Jira parity — Jira natively supports Oracle 19c/21c
- Enterprise-grade ACID transactions across all modules
- Advanced SQL features: analytic functions, CTEs, partitioning
- Oracle Text for built-in full-text search (alternative to Elasticsearch in early phases)
- Strong JSON support (`JSON_TABLE`, `JSON_VALUE`) for audit log payloads
- Proven at enterprise scale

### Why Spring Boot?
- Industry standard for Java enterprise applications
- Native integration with every technology in this stack
- Spring Security handles JWT, OAuth2, method-level security out of the box
- Excellent Oracle/Hibernate support

### Why React + TypeScript?
- Component-driven UI matches Jira's complex, interactive board and form patterns
- TypeScript eliminates entire classes of runtime bugs in a large frontend codebase
- Rich ecosystem of drag-and-drop, rich text, and chart libraries

### Why Redis?
- Stateless JWT architecture requires external token blacklist storage
- Permission caching dramatically reduces Oracle round-trips on every API request
- Required for distributed rate limiting (Bucket4j)

### Why RabbitMQ?
- Decouples notification sending from API request handling
- Enables reliable async delivery of emails and webhooks
- Supports automation rule execution without blocking the API

### Why MinIO?
- S3-compatible object storage, fully self-hosted
- Avoids storing binary files (attachments) in Oracle BLOBs
- Scales independently from the database

### Why Elasticsearch?
- Powers JQL `~` (text search) operator, same as Jira internally uses Lucene
- Full-text search across issue summaries, descriptions, and comments
- Fast even with millions of issues

### Why Flyway?
- Tracks every schema change as a versioned SQL script
- Critical for a multi-developer project and production deployments
- Works natively with Oracle and integrates with Spring Boot auto-run on startup

### Why Hibernate Envers?
- Automatically records before/after values for every entity change
- Directly powers the **issue activity stream** ("John changed Priority from Medium to High")
- Zero boilerplate — just `@Audited` annotation on entities

---

## 6. Version Compatibility Matrix

| Component | Version | Java Compatibility |
|---|---|---|
| Spring Boot | 3.3.x | Java 17+ (Java 21 recommended) |
| Spring Security | 6.3.x | Java 17+ |
| Hibernate | 6.5.x | Java 11+ |
| Flyway | 10.x | Java 17+ |
| Oracle JDBC (ojdbc11) | 21.9+ | Java 11+ |
| Elasticsearch Client | 8.14.x | Java 8+ |
| Testcontainers | 1.20.x | Java 8+ |
| MapStruct | 1.6.x | Java 8+ |
| jjwt | 0.12.x | Java 8+ |

---

## 7. Package Dependency Overview

### Backend (`pom.xml` additions needed)
```xml
<!-- Upgraded dependencies -->
<spring-boot.version>3.3.5</spring-boot.version>
<java.version>21</java.version>

<!-- Security -->
<dependency>spring-boot-starter-oauth2-client</dependency>   <!-- Social login -->
<dependency>bucket4j-core</dependency>                       <!-- Rate limiting -->
<dependency>jjwt-api 0.12.x</dependency>                    <!-- JWT upgrade -->

<!-- Data -->
<dependency>spring-boot-starter-data-redis</dependency>      <!-- Redis -->
<dependency>hibernate-envers</dependency>                    <!-- Audit history -->
<dependency>flyway-core</dependency>                         <!-- DB migrations -->
<dependency>flyway-database-oracle</dependency>              <!-- Oracle support -->
<dependency>mapstruct</dependency>                           <!-- DTO mapping -->

<!-- Search -->
<dependency>spring-data-elasticsearch</dependency>           <!-- Full-text search -->

<!-- Messaging -->
<dependency>spring-boot-starter-amqp</dependency>            <!-- RabbitMQ -->

<!-- Email -->
<dependency>spring-boot-starter-mail</dependency>            <!-- SMTP -->
<dependency>spring-boot-starter-thymeleaf</dependency>       <!-- Email templates -->

<!-- Scheduler -->
<dependency>quartz</dependency>                              <!-- Persistent jobs -->

<!-- File Storage -->
<dependency>aws-sdk-java-s3 2.x</dependency>                 <!-- MinIO/S3 client -->

<!-- Export -->
<dependency>poi-ooxml</dependency>                           <!-- Excel export -->
<dependency>openpdf</dependency>                             <!-- PDF export -->

<!-- Monitoring -->
<dependency>micrometer-registry-prometheus</dependency>      <!-- Prometheus metrics -->

<!-- Testing -->
<dependency>testcontainers-oracle-xe</dependency>            <!-- Oracle in tests -->
<dependency>testcontainers-elasticsearch</dependency>         <!-- ES in tests -->
<dependency>rest-assured</dependency>                        <!-- API tests -->
```

### Frontend (`package.json` additions needed)
```json
{
  "dependencies": {
    "antd": "^5.x",
    "@ant-design/charts": "^2.x",
    "@reduxjs/toolkit": "^2.x",
    "react-redux": "^9.x",
    "react-router-dom": "^6.x",
    "axios": "^1.x",
    "react-hook-form": "^7.x",
    "zod": "^3.x",
    "@hookform/resolvers": "^3.x",
    "@tiptap/react": "^2.x",
    "@tiptap/starter-kit": "^2.x",
    "@dnd-kit/core": "^6.x",
    "@dnd-kit/sortable": "^8.x",
    "@tanstack/react-table": "^8.x",
    "@tanstack/react-virtual": "^3.x",
    "recharts": "^2.x",
    "dayjs": "^1.x",
    "lucide-react": "^0.x",
    "@stomp/stompjs": "^7.x",
    "sockjs-client": "^1.x"
  },
  "devDependencies": {
    "vite": "^6.x",
    "@vitejs/plugin-react": "^4.x",
    "vitest": "^2.x",
    "@playwright/test": "^1.x"
  }
}
```

---

## 8. Implementation Phases Alignment

| Phase | Key Technologies to Activate |
|---|---|
| **Phase 1 – MVP** | Spring Boot + Oracle + Flyway + JWT + React + Ant Design + Redux Toolkit + React Router |
| **Phase 2 – Core Agile** | Hibernate Envers + Redis + WebSocket/STOMP + RabbitMQ + Spring Mail + Recharts + dnd-kit |
| **Phase 3 – Advanced** | Elasticsearch + Quartz + MinIO + OAuth2 + MapStruct + TipTap + Bucket4j |
| **Phase 4 – Enterprise** | ELK Stack + Prometheus + Grafana + Testcontainers + Spring Cloud Config |

---

*Created: March 15, 2026*
*Version: 1.0*
*Purpose: TaskMagnet – Definitive Technology Stack Reference*
