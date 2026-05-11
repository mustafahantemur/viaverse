# Viaverse

Viaverse is a greenfield platform implementation. This repository starts with architecture, operating rules, Gradle orchestration, technical service shells, client shells, shared backend foundation, and local development infrastructure before business behavior is added.

## Module Overview

```text
services/      Java Spring Boot service skeletons
apps/          Kotlin Multiplatform mobile shell and Next.js web/admin shells
packages/      Shared API, kernel, and observability foundations
infra/         Local Docker Compose development infrastructure
docs/          ADRs, implementation notes, and development runbooks
build-logic/   Gradle convention plugins
```

## First Setup

Required tools:

- JDK 25 or newer.
- Node.js 22 or newer.
- npm from the installed Node.js distribution.
- Docker with Compose v2.
- Android Studio later for Android emulator/device work.

VS Code users can use the committed one-click launch/task setup:

```powershell
code .
```

Then use `Run and Debug`:

- `docker initial setup`
- `debug identity-service`
- `debug backend: all services`
- `run web-next`
- `run admin-next`
- `run mobile-kmp desktop`

Manual local infrastructure setup:

```powershell
./scripts/dev/start-core-infra.ps1
```

## Validation

From the repository root:

```powershell
./gradlew projects
./gradlew check
./gradlew :apps:mobile-kmp:check
```

Client shell builds:

```powershell
cd apps/web-next
npm install
npm run build
```

```powershell
cd apps/admin-next
npm install
npm run build
```

## Run Commands

Backend services:

```powershell
./gradlew :services:identity-service:bootRun
./gradlew :services:marketplace-service:bootRun
./gradlew :services:payment-service:bootRun
./gradlew :services:messaging-service:bootRun
./gradlew :services:media-service:bootRun
./gradlew :services:notification-service:bootRun
./gradlew :services:search-service:bootRun
./gradlew :services:trust-gamification-service:bootRun
./gradlew :services:ads-monetization-service:bootRun
./gradlew :services:admin-bff:bootRun
```

Mobile desktop shell:

```powershell
./gradlew :apps:mobile-kmp:desktopRun
```

Web shells:

```powershell
cd apps/web-next
npm run dev
```

```powershell
cd apps/admin-next
npm run dev
```