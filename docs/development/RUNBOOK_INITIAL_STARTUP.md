# Initial Startup Runbook

## 1. Validate Tooling

```powershell
java -version
node -v
npm -v
docker --version
docker compose version
```

## 2. Start Local Infrastructure

```powershell
cd infra/docker-compose
Copy-Item .env.example .env
docker compose up -d postgres valkey kafka mailpit seaweedfs seaweedfs-bucket-init
docker compose ps
```

## 3. Validate Repository Build

From the repository root:

```powershell
./gradlew projects
./gradlew check
```

## 4. Start Backend Services

Run each service in a separate terminal when needed:

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

Actuator health is available at:

```text
http://localhost:<service-port>/actuator/health
```

## 5. Start Client Shells

```powershell
./gradlew :apps:mobile-kmp:desktopRun
```

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

Android emulator/device work is intentionally deferred until the Android KMP target is activated in a focused slice.
