# Viaverse

Viaverse is a greenfield platform implementation. The repository now contains:

- a shared backend foundation,
- local infrastructure and observability,
- `identity-service`,
- `profile-service` through the functional Phase 4 boundary,
- first real `content-service` and `media-service` slices,
- the first real `trust-gamification-service` slice,
- first web/admin/mobile client follow-through for that slice,
- and technical shells for the remaining backend domains.

## Module overview

```text
services/      Java Spring Boot services and BFFs
apps/          Kotlin Multiplatform mobile app and Next.js web/admin apps
packages/      Shared API, kernel, security, messaging, and observability foundations
infra/         Local Docker Compose development infrastructure
Docs/          Architecture notes and development runbooks
build-logic/   Gradle convention plugins
```

## Read these first

- Fresh clone / first local run: `Docs/Development/initial-development-start-guide.md`
- Current implementation status and known gaps: `Docs/Development/current-implementation-status.md`
- Profile-service architecture: `Docs/Architecture/profile-service/README.md`
- Trust / verification / moderation direction: `Docs/Architecture/trust-and-moderation.md`

## Prerequisites

- JDK 25+
- Node.js 22+
- npm
- Docker Desktop with Compose v2
- Android Studio only for Android emulator/device work

## First local run

From the repository root:

```powershell
.\scripts\dev\start-core-infra.ps1
.\scripts\dev\migrate-local.ps1
```

Then start the minimum currently useful product stack in separate terminals:

```powershell
.\gradlew.bat :services:identity-service:bootRun
.\gradlew.bat :services:profile-service:bootRun
.\gradlew.bat :services:content-service:bootRun
.\gradlew.bat :services:media-service:bootRun
.\gradlew.bat :services:web-bff:bootRun
.\gradlew.bat :services:admin-bff:bootRun
```

Then start the clients:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev\start-next-app.ps1 -App web-next
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev\start-next-app.ps1 -App admin-next
```

For Android:

```powershell
.\scripts\dev\start-mobile-android.ps1
```

## VS Code launch setup

The committed `.vscode/launch.json` includes:

- `docker local infra`
- `debug identity-service`
- `debug profile-service`
- `debug backend: all services`
- `run web-next`
- `run admin-next`
- `run mobile android`
- `run local stack: backend + web apps`

If VS Code shows stale unresolved imports after shared-module changes, run **Java: Clean Java Language Server Workspace** before treating the red markers as source failures.

## Run without `launch.json`

### Minimum profile flow

```powershell
.\gradlew.bat :services:identity-service:bootRun
.\gradlew.bat :services:profile-service:bootRun
.\gradlew.bat :services:web-bff:bootRun
.\gradlew.bat :services:admin-bff:bootRun
```

### Full backend topology

```powershell
.\gradlew.bat :services:identity-service:bootRun
.\gradlew.bat :services:profile-service:bootRun
.\gradlew.bat :services:content-service:bootRun
.\gradlew.bat :services:marketplace-service:bootRun
.\gradlew.bat :services:payment-service:bootRun
.\gradlew.bat :services:messaging-service:bootRun
.\gradlew.bat :services:media-service:bootRun
.\gradlew.bat :services:notification-service:bootRun
.\gradlew.bat :services:search-service:bootRun
.\gradlew.bat :services:trust-gamification-service:bootRun
.\gradlew.bat :services:ads-monetization-service:bootRun
.\gradlew.bat :services:admin-bff:bootRun
.\gradlew.bat :services:web-bff:bootRun
```

## Local ports

| Component | Port |
|---|---:|
| web-next | 3000 |
| admin-next | 3001 |
| web-bff | 8001 |
| identity-service | 8101 |
| marketplace-service | 8102 |
| payment-service | 8103 |
| messaging-service | 8104 |
| media-service | 8105 |
| notification-service | 8106 |
| search-service | 8107 |
| trust-gamification-service | 8108 |
| ads-monetization-service | 8109 |
| admin-bff | 8110 |
| profile-service | 8111 |
| content-service | 8112 |

Health checks use `/actuator/health` across the backend. `profile-service` additionally exposes `/health`.

## Validation

From the repository root:

```powershell
.\gradlew.bat projects
.\gradlew.bat check
.\gradlew.bat :apps:mobile-kmp:check
```

Web clients:

```powershell
cd apps\web-next
npm install
npm run build
```

```powershell
cd apps\admin-next
npm install
npm run build
```

## Stop / reset helpers

Free known application ports:

```powershell
.\scripts\dev\stop-local-app-ports.ps1
```

Stop local Docker infrastructure:

```powershell
docker compose -f .\infra\docker-compose\docker-compose.yml down
```
