# Hospital Referrals Management and Tracking System.

A secure, multi-tenant web application for coordinating patient referrals between hospitals. It gives each hospital a scoped workspace for patients, staff, departments, referrals, clinical updates, supporting documents, notifications, and reporting—while providing platform-wide administration for super administrators.

Built with Spring Boot, PostgreSQL, Thymeleaf, Spring Security, Flyway, and Docker.

## Contents

- [Capabilities](#capabilities)
- [Architecture](#architecture)
- [Roles and access](#roles-and-access)
- [Referral lifecycle](#referral-lifecycle)
- [Technology stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick start with Docker](#quick-start-with-docker)
- [Local development](#local-development)
- [Configuration](#configuration)
- [Database and migrations](#database-and-migrations)
- [Application routes](#application-routes)
- [Project layout](#project-layout)
- [Testing](#testing)
- [Deployment](#deployment)
- [Security and operational notes](#security-and-operational-notes)

## Capabilities

- **Hospital tenancy:** Hospitals work in one shared database schema, with application-level hospital scoping for operational records.
- **Registration and approval:** A hospital can submit a registration; a super administrator can approve or reject it.
- **Role-based workspaces:** Dedicated dashboards and access rules for super administrators, hospital administrators, referral officers, doctors, and viewers.
- **Directory management:** Hospital administrators manage departments, doctor profiles, and user accounts.
- **Patient registry:** Each hospital maintains its own patient records and generated patient numbers.
- **Referral coordination:** Create, receive, review, assign, forward, update, close, and search referrals.
- **Clinical collaboration:** Add notes, assign departments or doctors, and attach supporting documents.
- **Referral journeys:** Link a forwarded referral to its parent referral and retain a cross-hospital journey code.
- **Timeline and audit context:** Significant changes are stored as referral events, including assignment, status, note, attachment, forwarding, and closure activity.
- **Notifications:** Users receive in-app notifications associated with referral activity.
- **Reporting:** Viewer dashboards and tenant-level reporting include filters and CSV export.

## Architecture

```text
Browser
  │
  ▼
Spring Boot MVC application
  ├── Thymeleaf server-rendered UI
  ├── Spring Security authentication and role authorization
  ├── Tenant context filter and hospital-scoped services
  ├── Spring Data JPA / Hibernate
  ├── Flyway migrations
  ├── Local attachment-file storage
  └── PostgreSQL
```

The application uses a shared-schema, multi-tenant model. Hospitals, users, patients, departments, doctors, and referrals are related through hospital identifiers. Service-layer and controller authorization ensure an ordinary hospital user operates only within their assigned hospital context.

## Roles and access

| Role | Primary workspace | Responsibilities |
| --- | --- | --- |
| `SUPER_ADMIN` | `/super-admin` | Manage the platform, approve hospital registrations, manage hospitals and hospital administrators, and monitor platform setup. |
| `HOSPITAL_ADMIN` | `/hospital-admin` | Manage the local hospital directory, staff, patient registry, incoming and outgoing referrals, assignments, and outcomes. |
| `REFERRAL_OFFICER` | `/referral-officer` | Run day-to-day referral operations: create, receive, route, forward, update, and close referrals. |
| `DOCTOR` | `/doctor` | Review assigned referrals, consult attached documents, and add clinical notes or attachments. |
| `VIEWER` | `/viewer` | Review tenant dashboards and reports, including CSV exports. |

Authentication is form-based. Successful login redirects the user to the workspace appropriate to their role. Passwords are stored using BCrypt.

## Referral lifecycle

The application supports the following referral statuses:

```text
SUBMITTED → RECEIVED → UNDER_REVIEW → ACCEPTED / REJECTED
                                      └→ COMPLETED / CANCELLED
```

The terminal states are `REJECTED`, `COMPLETED`, and `CANCELLED`. A closed referral can record one of these outcomes: `TREATED`, `ADMITTED`, `REDIRECTED`, or `CLOSED_WITHOUT_TREATMENT`.

Priorities carry the following service-level targets:

| Priority | Target response time |
| --- | --- |
| `LOW` | 72 hours |
| `NORMAL` | 48 hours |
| `HIGH` | 24 hours |
| `URGENT` | 4 hours |

When a receiving hospital forwards a case, the new referral retains the journey code and references its parent referral. This preserves the sequence of care across hospitals.

## Technology stack

| Area | Technology |
| --- | --- |
| Runtime | Java 21 |
| Framework | Spring Boot 3.5 |
| Web UI | Spring MVC, Thymeleaf, HTML, CSS |
| Security | Spring Security with BCrypt passwords and remember-me support |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 17+ |
| Schema migrations | Flyway |
| Build | Maven |
| Tests | JUnit 5, Spring Boot Test, Spring Security Test, H2 (PostgreSQL mode) |
| Containers | Docker and Docker Compose |
| Deployment configuration | Render Blueprint (`render.yaml`) |

## Prerequisites

Choose one of the following local setups:

- Docker Desktop with Docker Compose, or
- Java 21, Maven 3.9+, and PostgreSQL 17+.

## Quick start with Docker

Docker Compose starts PostgreSQL, builds the application, applies Flyway migrations, and creates the bootstrap super administrator on its first run.

1. Create a local environment file from the example.

   ```bash
   cp .env.example .env
   ```

   On PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Replace the example passwords in `.env`, particularly the PostgreSQL password and bootstrap administrator password.

3. Start the stack.

   ```bash
   docker compose up --build
   ```

4. Open [http://localhost:8080](http://localhost:8080), then sign in with the bootstrap super administrator credentials configured in `.env`.

5. Stop the stack when finished.

   ```bash
   docker compose down
   ```

The Compose setup persists database data in `postgres-data` and uploaded documents in `referral-attachments`. `docker compose down` preserves those named volumes. Use `docker compose down --volumes` only when you deliberately want to remove all local database and attachment data.

## Local development

### 1. Create the database

Run the following with a PostgreSQL administrator account. The database owner is the application user, so Flyway can create its history table and apply migrations.

```sql
CREATE USER referrals WITH PASSWORD 'replace-with-a-strong-password';
CREATE DATABASE hospital_referrals OWNER referrals;
```

### 2. Configure the application

Set environment variables in your terminal or IDE run configuration:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hospital_referrals
SPRING_DATASOURCE_USERNAME=referrals
SPRING_DATASOURCE_PASSWORD=replace-with-a-strong-password
APP_BOOTSTRAP_SUPER_ADMIN_EMAIL=superadmin@example.org
APP_BOOTSTRAP_SUPER_ADMIN_PASSWORD=replace-this-password
```

On Windows PowerShell, set them for the current terminal session with:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/hospital_referrals"
$env:SPRING_DATASOURCE_USERNAME = "referrals"
$env:SPRING_DATASOURCE_PASSWORD = "replace-with-a-strong-password"
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Without `SERVER_PORT`, a direct local run listens at [http://localhost:8081](http://localhost:8081). Docker overrides this to port `8080`.

### First login

At startup, the application creates the configured bootstrap super administrator only if an account with that email does not already exist. The development defaults are:

| Field | Default |
| --- | --- |
| Email | `superadmin@referrals.local` |
| Password | `ChangeMe123!` |

Change both values outside local development before exposing the application to users.

## Configuration

### Application variables

| Variable | Purpose | Default |
| --- | --- | --- |
| `SERVER_PORT` | HTTP port for the application | `8081` for direct runs; Compose sets `8080` |
| `SPRING_DATASOURCE_URL` | Complete JDBC connection URL; takes precedence over component variables | `jdbc:postgresql://localhost:5432/hospital_referrals` |
| `SPRING_DATASOURCE_USERNAME` | JDBC user | `referrals` |
| `SPRING_DATASOURCE_PASSWORD` | JDBC password | `referrals` |
| `DB_HOST` | PostgreSQL host used in the default JDBC URL | `localhost` |
| `DB_PORT` | PostgreSQL port used in the default JDBC URL | `5432` |
| `DB_NAME` | Database name used in the default JDBC URL | `hospital_referrals` |
| `DB_USERNAME` | Fallback database username | `referrals` |
| `DB_PASSWORD` | Fallback database password | `referrals` |
| `APP_BOOTSTRAP_SUPER_ADMIN_EMAIL` | Bootstrap super administrator email | `superadmin@referrals.local` |
| `APP_BOOTSTRAP_SUPER_ADMIN_PASSWORD` | Bootstrap super administrator password | `ChangeMe123!` |
| `APP_STORAGE_ATTACHMENTS_DIR` | Directory for uploaded referral files | `storage/referral-attachments` |

### Docker Compose variables

| Variable | Purpose | Default |
| --- | --- | --- |
| `POSTGRES_DB` | Database initialized by the PostgreSQL container | `hospital_referrals` |
| `POSTGRES_USER` | PostgreSQL application user | `referrals` |
| `POSTGRES_PASSWORD` | PostgreSQL application password | `referrals` |
| `POSTGRES_HOST_PORT` | Host port mapped to PostgreSQL | `5432` |
| `APP_HOST_PORT` | Host port mapped to the web application | `8080` |

See [.env.example](.env.example) for the complete Compose-oriented configuration template.

## Database and migrations

Flyway migrations are in [`src/main/resources/db/migration`](src/main/resources/db/migration). They create and evolve the platform schema, including:

- hospitals and hospital registration status;
- application users and role assignments;
- patients, departments, and doctors;
- referrals, assignments, closure details, and parent/journey links;
- referral timeline events and attachments; and
- user notifications.

Hibernate uses `ddl-auto: validate`: it validates the mapped schema at startup but does not create or modify it. Make schema changes through a new Flyway migration instead of relying on Hibernate DDL generation.

> **Migration note:** This repository now targets PostgreSQL. A production MySQL database requires a separate data-migration plan (export, transform, import, and validation); switching the application configuration alone does not move existing data.

## Application routes

These are server-rendered UI routes, not a public REST API contract.

| Path | Access | Purpose |
| --- | --- | --- |
| `/login` | Public | Sign in. |
| `/hospital-registration` | Public | Submit a hospital registration. |
| `/super-admin` | `SUPER_ADMIN` | Platform dashboard. |
| `/super-admin/hospitals` | `SUPER_ADMIN` | Hospital management and registration approval. |
| `/super-admin/admins` | `SUPER_ADMIN` | Hospital administrator management. |
| `/hospital-admin` | `HOSPITAL_ADMIN` | Hospital administration dashboard. |
| `/hospital-admin/departments` | `HOSPITAL_ADMIN` | Department management. |
| `/hospital-admin/doctors` | `HOSPITAL_ADMIN` | Doctor profile management. |
| `/hospital-admin/users` | `HOSPITAL_ADMIN` | Local user management. |
| `/hospital-admin/patients` | `HOSPITAL_ADMIN` | Patient registry. |
| `/hospital-admin/referrals` | `HOSPITAL_ADMIN` | Referral operations and administration. |
| `/referral-officer` | `REFERRAL_OFFICER` | Referral officer dashboard. |
| `/referral-officer/referrals` | `REFERRAL_OFFICER` | Referral work queue and actions. |
| `/doctor` | `DOCTOR` | Doctor dashboard. |
| `/doctor/referrals` | `DOCTOR` | Assigned referrals. |
| `/viewer` | `VIEWER` | Reporting dashboard. |
| `/viewer/reports` | `VIEWER` | Filtered reports and CSV export. |
| `/notifications` | Authenticated users | Notification inbox. |
| `/attachments/{attachmentId}/download` | Authorized users | Download an attachment. |
| `/attachments/{attachmentId}/view` | Authorized users | View an attachment inline when supported. |

## Project layout

```text
.
├── src/main/java/com/example/referrals
│   ├── common/          Shared models and current-user helpers
│   ├── config/          Security and application bootstrap configuration
│   ├── directory/       Department and doctor domain
│   ├── hospital/        Hospital registration and tenant domain
│   ├── hospitaladmin/   Hospital administration forms and services
│   ├── notification/    User notifications
│   ├── patient/         Patient registry
│   ├── referral/        Referral, event, attachment, and workflow domain
│   ├── reporting/       Tenant reporting services
│   ├── security/        User details and post-login routing
│   ├── superadmin/      Platform administration services
│   ├── tenant/          Tenant context resolution
│   ├── user/            Application users and roles
│   └── web/             MVC controllers
├── src/main/resources
│   ├── db/migration/    Versioned Flyway migrations
│   ├── static/css/      Application styles
│   ├── templates/       Thymeleaf pages by workspace
│   ├── application.yml  Runtime configuration
│   └── application-test.yml
├── postgres-render/     PostgreSQL image for Render private service deployment
├── docker-compose.yml   Local application and PostgreSQL stack
├── Dockerfile           Application image build
├── render.yaml          Render Blueprint
└── pom.xml              Maven dependencies and build configuration
```

## Testing

Run the test suite with:

```bash
mvn test
```

Tests use an in-memory H2 database configured in PostgreSQL compatibility mode. This makes automated tests fast while production and container deployments use PostgreSQL.

## Deployment

### Docker image

Build the application image:

```bash
docker build -t hospital-referrals .
```

Provide the PostgreSQL JDBC URL, credentials, attachment storage path, and bootstrap administrator credentials as environment variables when running it.

### Render

[`render.yaml`](render.yaml) defines:

- a persistent PostgreSQL private service backed by `postgres-render/Dockerfile` and a 10 GB disk; and
- a web service backed by the root `Dockerfile`, with a 1 GB persistent attachment disk.

Before deploying, set secure values for `POSTGRES_PASSWORD` and `APP_BOOTSTRAP_SUPER_ADMIN_PASSWORD` in Render. Confirm the web service can reach the private database service and persist attachments at `/var/data/referral-attachments`.

## Security and operational notes

- Use strong, unique database and bootstrap administrator passwords in every non-development environment.
- Keep `.env` out of source control; it is already ignored by Git.
- Back up both PostgreSQL data and the attachment-storage volume. Database backups alone do not include uploaded files.
- Treat referral records and attachments as sensitive health information. Deploy behind HTTPS, restrict database network access, and align retention, access logging, and backup policies with your organization’s obligations.
- Keep Flyway migrations immutable once applied to a shared environment. Add a new migration for every schema change.
- The upload limits are 10 MB per file and 50 MB per request; adjust `spring.servlet.multipart` settings only with corresponding storage and security controls.

