# Local Environment

## Required Tools

- Java: JDK 21 or newer. The build currently uses Java 21 source compatibility.
- Node.js: Node 22 or newer is recommended for Next.js 16.
- npm: use the npm version bundled with the installed Node version.
- Docker Desktop or Docker Engine with Compose v2.
- Android Studio: required later for Android emulator/device work. The active mobile validation target is currently desktop.

Current local validation was run with Temurin JDK 25, Node 25, npm 11, Docker 29, and Docker Compose v2.

## Start Infrastructure

VS Code:

```text
Run and Debug > docker initial setup
```

Script:

```powershell
./scripts/dev/start-core-infra.ps1
```

Manual Docker Compose:

```powershell
cd infra/docker-compose
docker compose up -d postgres valkey kafka mailpit minio
```

Optional observability:

```powershell
docker compose --profile observability up -d
```

Optional search dependency:

```powershell
docker compose --profile search up -d opensearch
```

OpenSearch is profile-gated because it is heavier than the default stack.

Kafka uses an Apache Kafka local broker in KRaft mode to stay aligned with the target Kafka / Amazon MSK architecture. RabbitMQ, Firebase, and Redpanda are not part of the local foundation.

## Database Naming

Each backend service owns its own PostgreSQL database:

- `viaverse_identity`
- `viaverse_marketplace`
- `viaverse_payment`
- `viaverse_messaging`
- `viaverse_media`
- `viaverse_notification`
- `viaverse_search`
- `viaverse_trust_gamification`
- `viaverse_ads_monetization`
- `viaverse_admin_bff`

Matching `_test` databases are created for future test profiles.

## Secrets

Do not commit `.env` files or real credentials. The committed `.env.example` is local-only sample configuration.

## Gradle

```powershell
./gradlew projects
./gradlew check
./gradlew :apps:mobile-kmp:check
```

## Client Apps

```powershell
cd apps/web-next
npm install
npm run dev
```

```powershell
cd apps/admin-next
npm install
npm run dev
```

Mobile desktop shell:

```powershell
./gradlew :apps:mobile-kmp:desktopRun
```

VS Code one-click launch instructions are in [VS_CODE.md](VS_CODE.md).
