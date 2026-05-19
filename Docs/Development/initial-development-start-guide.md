# Initial development start guide

This guide is the shortest safe path from a fresh clone to a debuggable local Viaverse stack.

## 1. Install the local toolchain

Required:

- JDK 25+
- Node.js 22+
- npm
- Docker Desktop with Compose v2
- Android Studio only if you will run the Android client

The repository already contains a local-only `.env.local` with dummy development values. Do not replace those with production secrets.

## 2. Bring up infrastructure and databases

From the repository root:

```powershell
.\scripts\dev\start-core-infra.ps1
.\scripts\dev\migrate-local.ps1
```

`start-core-infra.ps1` starts the local Docker dependencies and creates the service databases, including
`viaverse_profile`, `viaverse_content`, and their test databases.

Useful local consoles:

| Tool | URL |
|---|---|
| Mailpit | `http://localhost:8025` |
| OpenSearch | `http://localhost:9200` |
| OpenSearch Dashboards | `http://localhost:5601` |
| Prometheus | `http://localhost:9090` |
| Jaeger | `http://localhost:16686` |
| Kafka UI | `http://localhost:8088` |

## 3. Start the minimum application stack

Open separate terminals from the repository root and start these in order:

```powershell
.\gradlew.bat :services:identity-service:bootRun
.\gradlew.bat :services:profile-service:bootRun
.\gradlew.bat :services:content-service:bootRun
.\gradlew.bat :services:media-service:bootRun
.\gradlew.bat :services:marketplace-service:bootRun
.\gradlew.bat :services:trust-gamification-service:bootRun
.\gradlew.bat :services:web-bff:bootRun
.\gradlew.bat :services:admin-bff:bootRun
```

Then start the two web clients:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev\start-next-app.ps1 -App web-next
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev\start-next-app.ps1 -App admin-next
```

For Android:

```powershell
.\scripts\dev\start-mobile-android.ps1
```

This gives you the currently useful product slice:

- `identity-service` on `8101`
- `profile-service` on `8111`
- `content-service` on `8112`
- `media-service` on `8105`
- `marketplace-service` on `8102`
- `trust-gamification-service` on `8108`
- `web-bff` on `8001`
- `admin-bff` on `8110`
- `web-next` on `3000`
- `admin-next` on `3001`

The remaining backend services are currently technical shells. Start them only when you are working on them or when you want to validate the full local topology.

## 4. Start the full backend without VS Code

If you want every backend process running, use separate terminals and start:

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

## 5. Debug from any IDE

Import the repository as a Gradle project, then create run/debug configurations using:

| Service | Main class |
|---|---|
| identity-service | `app.viaverse.identity.IdentityServiceApplication` |
| profile-service | `app.viaverse.profile.ProfileServiceApplication` |
| content-service | `app.viaverse.content.ContentServiceApplication` |
| media-service | `app.viaverse.media.MediaServiceApplication` |
| web-bff | `app.viaverse.webbff.WebBffApplication` |
| admin-bff | `app.viaverse.adminbff.AdminBffApplication` |

For every local Spring run configuration:

1. Load environment variables from `.env.local`.
2. Add VM option `-Dspring.profiles.active=local`.
3. Start infrastructure and run migrations first.

VS Code already contains ready-made configurations in `.vscode/launch.json`, including `debug identity-service`, `debug profile-service`, `debug backend: all services`, and `run local stack: backend + web apps`.

## 6. Verify the stack

Use the actuator endpoint for every backend service:

```powershell
Invoke-WebRequest http://localhost:8101/actuator/health
Invoke-WebRequest http://localhost:8111/actuator/health
Invoke-WebRequest http://localhost:8112/actuator/health
Invoke-WebRequest http://localhost:8105/actuator/health
Invoke-WebRequest http://localhost:8102/actuator/health
Invoke-WebRequest http://localhost:8001/actuator/health
Invoke-WebRequest http://localhost:8110/actuator/health
```

`profile-service` also exposes the simple scaffold endpoint:

```powershell
Invoke-WebRequest http://localhost:8111/health
```

`identity-service` does **not** use `/health`; its public liveness endpoint is `/actuator/health`.

## 7. Build and test while developing

Backend and shared packages:

```powershell
.\gradlew.bat check
```

Mobile shared code:

```powershell
.\gradlew.bat :apps:mobile-kmp:check
```

Android debug APK:

```powershell
.\gradlew.bat :apps:mobile-android:assembleDebug
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

## 8. Common recovery steps

If a port is occupied:

```powershell
.\scripts\dev\stop-local-app-ports.ps1
```

If VS Code still shows unresolved imports such as `ObservedAction` after shared modules moved, but Gradle builds pass:

1. Run **Java: Clean Java Language Server Workspace** from the VS Code command palette.
2. Reload the window.

If `profile-service` cannot connect to PostgreSQL on a fresh environment:

```powershell
.\scripts\dev\start-core-infra.ps1
.\scripts\dev\migrate-local.ps1
```

That recreates the expected local databases and reapplies Flyway migrations.
