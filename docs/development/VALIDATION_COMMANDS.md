# Validation Commands

## Repository

```powershell
./gradlew projects
./gradlew check
```

## Backend Services

```powershell
./gradlew :services:identity-service:check
./gradlew :services:marketplace-service:check
./gradlew :services:payment-service:check
./gradlew :services:messaging-service:check
./gradlew :services:media-service:check
./gradlew :services:notification-service:check
./gradlew :services:search-service:check
./gradlew :services:trust-gamification-service:check
./gradlew :services:ads-monetization-service:check
./gradlew :services:admin-bff:check
```

Boot run commands:

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

## Mobile

```powershell
./gradlew :apps:mobile-kmp:check
./gradlew :apps:mobile-kmp:desktopRun
```

## Web And Admin

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

## Local Infrastructure

Preferred setup script:

```powershell
./scripts/dev/start-core-infra.ps1
```

Manual commands:

```powershell
cd infra/docker-compose
docker compose config
docker compose up -d postgres valkey kafka mailpit seaweedfs seaweedfs-bucket-init
docker compose ps
```

Optional profiles:

```powershell
docker compose --profile observability up -d
docker compose --profile search up -d opensearch
```
