<div align="center">

# Service Watcher

**Self-hosted uptime monitoring for your APIs and servers.**
Register a URL, and Service Watcher pings it on a schedule, records whether it's up, and shows it all on a live dashboard.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue?style=flat-square&logo=gnu&logoColor=white)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.5](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React 19](https://img.shields.io/badge/React-19-20232A?style=flat-square&logo=react&logoColor=61DAFB)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-7-3178C6?style=flat-square&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)

</div>

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Project structure](#project-structure)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [REST API](#rest-api)
- [How monitoring works](#how-monitoring-works)
- [Development](#development)
- [Roadmap](#roadmap)
- [License](#license)

## Features

- **Scheduled health checks**: every registered URL is pinged on a configurable interval (default 30 s).
- **Parallel probing**: checks run concurrently on Java 21 virtual threads with a configurable concurrency cap, so one slow endpoint never delays the others.
- **Status tracking**: each service is `UP`, `DOWN` or `UNKNOWN`, with the time of its last check.
- **REST API**: full CRUD for monitored services, with validation and RFC 9457 `problem+json` errors.
- **Live dashboard**: status summary tiles, a services table and a detail page, auto-refreshed in the browser.
- **User preferences**: light/dark/system theme, refresh interval, pause auto-refresh and table density, all saved locally.
- **Versioned database schema**: managed by Flyway migrations; Hibernate only validates against it.

## Tech stack

### Backend

[![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/orm/)
[![Lombok](https://img.shields.io/badge/Lombok-BC2025?style=for-the-badge)](https://projectlombok.org/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

### Database & infrastructure

[![MySQL](https://img.shields.io/badge/MySQL_8.4-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://dev.mysql.com/doc/refman/8.4/en/)
[![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://documentation.red-gate.com/flyway)
[![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docs.docker.com/compose/)

### Frontend

[![React](https://img.shields.io/badge/React_19-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vite.dev/)
[![TanStack Router](https://img.shields.io/badge/TanStack_Router-FF4154?style=for-the-badge&logo=tanstack&logoColor=white)](https://tanstack.com/router)
[![TanStack Query](https://img.shields.io/badge/TanStack_Query-FF4154?style=for-the-badge&logo=reactquery&logoColor=white)](https://tanstack.com/query)
[![Zustand](https://img.shields.io/badge/Zustand-443E38?style=for-the-badge)](https://zustand.docs.pmnd.rs/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS_4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![shadcn/ui](https://img.shields.io/badge/shadcn%2Fui-000000?style=for-the-badge&logo=shadcnui&logoColor=white)](https://ui.shadcn.com/)
[![Radix UI](https://img.shields.io/badge/Radix_UI-161618?style=for-the-badge&logo=radixui&logoColor=white)](https://www.radix-ui.com/)
[![Lucide](https://img.shields.io/badge/Lucide_Icons-F56565?style=for-the-badge&logo=lucide&logoColor=white)](https://lucide.dev/)

### Testing & tooling

[![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge)](https://site.mockito.org/)
[![Oxlint](https://img.shields.io/badge/Oxlint-1F2937?style=for-the-badge&logo=oxc&logoColor=white)](https://oxc.rs/docs/guide/usage/linter)

## Architecture

```mermaid
flowchart LR
    subgraph Browser
        UI["React dashboard<br/>(TanStack Query polling)"]
    end

    subgraph Backend["Spring Boot backend :8080"]
        API["REST API<br/>/api/v1/services"]
        Pinger["PingService<br/>@Scheduled + virtual threads"]
    end

    DB[("MySQL 8.4<br/>Flyway-managed schema")]
    Targets["Monitored URLs<br/>(your APIs & servers)"]

    UI -- "HTTP / JSON" --> API
    API --> DB
    Pinger -- "read targets,<br/>write status" --> DB
    Pinger -- "HTTP GET" --> Targets
```

In development the Vite dev server proxies `/api` to the backend, so the browser talks to a single origin and no CORS setup is needed.

## Project structure

This is a monorepo with an independent backend and frontend:

```
infra-hub/
├── backend/                  # Spring Boot 3 application (Java 21, Maven)
│   └── src/main/java/com/infrahub/watcher/
│       ├── monitoring/
│       │   ├── domain/       # MonitoredService entity, ServiceStatus enum
│       │   ├── persistence/  # Spring Data JPA repository
│       │   ├── ping/         # Scheduler, HTTP probe, ping settings
│       │   ├── application/  # Use cases / transaction boundary for the API
│       │   └── api/          # REST controller + request/response DTOs
│       ├── shared/web/       # Cross-cutting web concerns (errors)
│       └── config/           # App-wide beans
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/     # Flyway SQL migrations
├── frontend/                 # Vite + React + TypeScript SPA
│   └── src/
│       ├── app/              # Query client + router setup
│       ├── routes/           # File-based routes (TanStack Router)
│       ├── features/services/# Feature slice: api, hooks, components, types
│       ├── components/       # shadcn/ui primitives + app layout
│       ├── stores/           # Zustand UI-preferences store
│       └── lib/              # API client, formatting helpers
├── docker-compose.yml        # Local MySQL
└── LICENSE
```

See [`frontend/README.md`](frontend/README.md) for frontend-specific details.

## Getting started

### Prerequisites

| Tool | Version |
| --- | --- |
| JDK | 21 or newer |
| Node.js | 22 or newer (with npm) |
| Docker | with the Compose plugin |

Maven isn't required: the backend ships with the Maven Wrapper (`./mvnw`).

### 1. Start the database

```bash
docker compose up -d
```

This starts MySQL 8.4 on `localhost:3306` with database `service_watcher` and user `watcher` / `watcher`.

### 2. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

Flyway applies the schema on startup. The API is now on `http://localhost:8080`, and the pinger starts its first run after 10 seconds.

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

### 4. Add a service to monitor

```bash
curl -X POST http://localhost:8080/api/v1/services \
     -H 'Content-Type: application/json' \
     -d '{"name": "Example", "url": "https://example.com"}'
```

It appears on the dashboard as `UNKNOWN` and switches to `UP` or `DOWN` after the next check.

## Configuration

### Backend

Defined in [`backend/src/main/resources/application.yml`](backend/src/main/resources/application.yml); each setting can be overridden with an environment variable.

| Environment variable | Default | Description |
| --- | --- | --- |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `service_watcher` | Database name |
| `DB_USER` / `DB_PASSWORD` | `watcher` / `watcher` | Database credentials |
| `DB_POOL_SIZE` | `10` | Max JDBC connections |
| `SERVER_PORT` | `8080` | HTTP port |
| `PING_ENABLED` | `true` | Turn the scheduler on/off (e.g. for API-only instances) |
| `PING_INTERVAL` | `PT30S` | Pause between the end of one run and the start of the next (ISO-8601) |
| `PING_INITIAL_DELAY` | `PT10S` | Delay before the first run after startup |

Probe timeouts (`watcher.ping.connect-timeout`, `request-timeout`) and the parallelism cap (`watcher.ping.max-concurrency`, default 50) are set in `application.yml`.

### Frontend

Copy `frontend/.env.example` to `frontend/.env.local` to override:

| Variable | Default | Description |
| --- | --- | --- |
| `VITE_API_BASE_URL` | `/api/v1` | API base path as seen by the browser |
| `VITE_API_PROXY_TARGET` | `http://localhost:8080` | Where the dev server proxies `/api` (dev only) |

## REST API

Base path: `/api/v1/services`

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `GET` | `/api/v1/services` | List all services (sorted by name) | `200` |
| `GET` | `/api/v1/services/{id}` | Get one service | `200` |
| `POST` | `/api/v1/services` | Register a service | `201` + `Location` header |
| `PUT` | `/api/v1/services/{id}` | Replace name and URL | `200` |
| `DELETE` | `/api/v1/services/{id}` | Stop monitoring a service | `204` |

**Request body** (`POST`, `PUT`):

```json
{ "name": "Payments API", "url": "https://payments.example.com/health" }
```

`name` is required (max 100 characters); `url` is required, must be `http(s)://` and at most 2048 characters. Changing a service's URL resets its status to `UNKNOWN`.

**Response body:**

```json
{
  "id": 1,
  "name": "Payments API",
  "url": "https://payments.example.com/health",
  "lastStatus": "UP",
  "lastCheckedAt": "2026-11-01T10:24:47.347Z"
}
```

**Errors** use [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) `application/problem+json`:

```json
{ "type": "about:blank", "title": "Not Found", "status": 404, "detail": "MonitoredService with id 42 not found", "instance": "/api/v1/services/42" }
```

## How monitoring works

1. `PingService` runs on a **fixed delay**, so a slow run can never overlap the next one.
2. It loads all services, then probes them **in parallel on virtual threads**, capped by `max-concurrency`.
3. Each probe sends an HTTP `GET` with connect and request timeouts, following redirects:
   - **`UP`**: final response is `2xx` or `3xx`
   - **`DOWN`**: `4xx`/`5xx`, timeout, DNS failure, refused connection or TLS error
4. Results are written with a **targeted update** of only `last_status` and `last_checked_at`. No transaction is held during network calls, and a check finishing in the background never overwrites a name/URL edit made through the API.
5. Status changes are logged (`Service 'X' changed UP -> DOWN`).

In the browser, the dashboard polls the API on the interval you pick (5–60 s). Polling pauses in background tabs, refreshes immediately when you return, keeps showing the last good data if a request fails, and slows down to every 30 s while the API is unreachable.

## Development

```bash
# Backend: compile and run unit tests
cd backend && ./mvnw verify

# Frontend: type-check + production build, and lint
cd frontend && npm run build && npm run lint
```

New database changes go in a new Flyway migration (`backend/src/main/resources/db/migration/V2__....sql`); never edit a migration that has already been applied.

## Roadmap

- [ ] Create / edit / delete services from the dashboard
- [ ] Response time and status history per service
- [ ] Notifications on status change (e-mail, Slack, webhooks)
- [ ] Distributed scheduler lock (e.g. ShedLock) for running multiple backend instances
- [ ] Integration tests against real MySQL with Testcontainers
- [ ] Production deployment setup (container images, reverse proxy for `/api`)

## License

Distributed under the **GNU General Public License v3.0**. See [`LICENSE`](LICENSE) for the full text.
