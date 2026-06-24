# Viaverse domain map (short)

Keep this to one screen. Update when modules or commands change.

## What Viaverse is

A local-first, multi-service marketplace + social product: a **community/local-info feed**, a **service
marketplace** (seekers post requests → providers make offers → jobs), and **business discovery**. Three user
modes: **Customer/Seeker**, **Individual Provider**, **Business**.

## Where things live

| Area | Location |
|---|---|
| Product web app (Next.js) | `apps/web-next` |
| Admin web app (Next.js) | `apps/admin-next` |
| Mobile (Kotlin) | `apps/mobile-android` (Compose UI), `apps/mobile-kmp` (shared logic) |
| Backend services (Spring Boot, hexagonal) | `services/*` |
| BFFs | `services/web-bff`, `services/admin-bff`, `services/mock-web-bff` (prototype backend) |
| Shared backend libs | `packages/*` (`shared-kernel`, `observability`, `web-kernel`, `messaging-kernel`, `security-kernel`, `api-contracts`) |
| Infra | `infra/docker-compose`, `infra/kubernetes` |
| Docs | `Docs/` (see `docs-routing.md`) |
| Gradle convention plugins | `build-logic/` |

## Backend services

Implemented: `identity-service`, `profile-service`, `content-service`, `media-service`, `marketplace-service`,
`trust-gamification-service`. Stub shells: `messaging-service`, `notification-service`, `payment-service`,
`search-service`, `ads-monetization-service`.

## Key domain concepts

- **Capabilities/modes:** `CUSTOMER` (real backend) / `STANDARD` (mock web), `INDIVIDUAL_PROVIDER`, `BUSINESS`.
- **Feed content:** posts, announcements/incidents, events (content-service; mock via mock-web-bff).
- **Marketplace:** service requests → offers → jobs (lifecycle: AGREED → IN_PROGRESS → COMPLETED).
- **Profiles:** customer/provider/business profiles + business verification (profile-service).
- **Trust/gamification:** trust score + badges (trust-gamification-service).
- **Architecture:** hexagonal (domain / application / infrastructure); events via Kafka outbox; gRPC planned;
  REST only at BFFs.

## Build / test / run (Windows PowerShell)

```powershell
# Backend build + checks
.\gradlew.bat check
.\gradlew.bat projects
.\gradlew.bat :apps:mobile-kmp:check

# Local infra + migrations
.\scripts\dev\start-core-infra.ps1
.\scripts\dev\migrate-local.ps1

# Run a service
.\gradlew.bat :services:identity-service:bootRun

# Product UI prototype (fastest path, no Docker)
.\gradlew.bat :services:mock-web-bff:bootRun   # port 8120
cd apps\web-next; npm install; npm run dev      # set NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL=http://localhost:8120

# Web build
cd apps\web-next; npm run build
```

Health: `/actuator/health`. API docs (local profile): `/scalar`, `/v3/api-docs`.
