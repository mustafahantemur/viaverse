# Viaverse

Viaverse is a local-first workspace for a multi-service marketplace/social product. It contains Spring Boot backend services and BFFs, Next.js web clients, Kotlin Multiplatform/mobile apps, shared packages, local Docker infrastructure, and architecture/design notes.

## Repository Map

```text
apps/          Next.js web/admin apps and Kotlin Multiplatform/mobile code
services/      Spring Boot microservices, BFFs, and mock-web-bff
packages/      Shared API contracts, web/security/messaging kernels, observability helpers
infra/         Docker Compose and Kubernetes infrastructure assets
scripts/dev/   Local startup, migrations, ports, and debug helpers
Docs/          Architecture notes, development docs, prototypes, and design assets
build-logic/   Gradle convention plugins
```

## Backend Modules

- `identity-service`: authentication, registration, OTP, sessions, account APIs.
- `profile-service`: user/provider profile and business verification workflow.
- `content-service`: posts and feed-facing content slices.
- `marketplace-service`: marketplace/order-oriented domain slices.
- `media-service`: media metadata and object-storage integration.
- `trust-gamification-service`: trust score and gamification slices.
- `web-bff`: public web/mobile BFF; fronts identity/profile/content/marketplace/media.
- `admin-bff`: admin app BFF.
- `mock-web-bff`: standalone mock backend for the web product prototype.
- Remaining service folders are technical shells for planned domains.

## Read First

- Fresh clone / first local run: `Docs/Development/initial-development-start-guide.md`
- Current status and known gaps: `Docs/Development/current-implementation-status.md`
- Profile service architecture: `Docs/Architecture/profile-service/README.md`
- Identity service archived notes: `Docs/Architecture/identity-service/`
- Local observability notes: `Docs/Development/observability.md`

## Codex Context Routing

This repo has many large Markdown files. To keep AI agents (Codex and others) from loading them in full, use
the lightweight `viaverse-context` skill: it routes through a short domain map, a docs index, and a search
script so only the relevant section is read.

- Repo-specific agent skills live under `.agents/skills/` (here: `.agents/skills/viaverse-context/`).
- Full explanation, setup, and maintenance: [`Docs/codex-context-routing.md`](Docs/codex-context-routing.md).

## Prerequisites

- JDK 25+
- Node.js 22+ and npm
- Docker Desktop with Compose v2
- Android Studio only for Android emulator/device work

## First Local Run

```powershell
.\scripts\dev\start-core-infra.ps1
.\scripts\dev\migrate-local.ps1
```

Run the common web/mobile backend flow in separate terminals:

```powershell
.\gradlew.bat :services:identity-service:bootRun
.\gradlew.bat :services:profile-service:bootRun
.\gradlew.bat :services:content-service:bootRun
.\gradlew.bat :services:media-service:bootRun
.\gradlew.bat :services:web-bff:bootRun
```

For admin:

```powershell
.\gradlew.bat :services:admin-bff:bootRun
```

For the standalone product prototype:

```powershell
.\gradlew.bat :services:mock-web-bff:bootRun
cd apps\web-next
$env:NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL="http://localhost:8120"
npm run dev
```

Seeded mock accounts:

- `deniz@viaverse.test` / `Password123!`
- `ece@viaverse.test` / `Password123!`
- `mert@viaverse.test` / `Password123!`

Start web clients:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev\start-next-app.ps1 -App web-next
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev\start-next-app.ps1 -App admin-next
```

For Android:

```powershell
.\scripts\dev\start-mobile-android.ps1
```

## Docker Infrastructure

The local Docker Compose stack stays under `infra/docker-compose/docker-compose.yml`.

```powershell
.\scripts\dev\start-core-infra.ps1
docker compose -f .\infra\docker-compose\docker-compose.yml down
docker compose -f .\infra\docker-compose\docker-compose.yml down -v
```

`down` stops containers and keeps volumes. `down -v` also deletes local data.

| Component | Purpose | Port/UI |
|---|---|---:|
| PostgreSQL | Service databases and Flyway migrations | 5432 |
| Valkey | Redis-compatible cache for sessions, OTP, rate limits | 6379 |
| Kafka | Local event bus | 9092 |
| Kafka UI | Inspect topics/messages | 8081 |
| Mailpit | Fake SMTP inbox for local OTP/email | 8025 UI, 1025 SMTP |
| SeaweedFS | Local S3-compatible media storage | 8333 |
| OpenTelemetry Collector | Receives OTLP data and forwards it | 4317/4318 |
| Jaeger | Trace UI | 16686 |
| Prometheus | Metrics scrape/query | 9090 |
| OpenSearch | Structured log/search store | 9200 |
| OpenSearch Dashboards | Log exploration | 5601 |
| Fluent Bit | Ships container logs to OpenSearch | no UI |

## Docker Images And Kubernetes

Dockerfiles are provided for Spring services/BFFs, Next.js apps, and mobile build images. Kubernetes manifests live under `infra/kubernetes/base`.

Build examples:

```powershell
docker build -f Dockerfile.spring --build-arg GRADLE_PROJECT=:services:identity-service -t viaverse/identity-service:local .
docker build -f apps\web-next\Dockerfile -t viaverse/web-next:local apps\web-next
docker build -f apps\admin-next\Dockerfile -t viaverse/admin-next:local apps\admin-next
docker build -f apps\mobile-android\Dockerfile -t viaverse/mobile-android-build:local .
docker build -f apps\mobile-kmp\Dockerfile -t viaverse/mobile-kmp-build:local .
```

Apply Kubernetes base manifests:

```powershell
kubectl apply -f infra/kubernetes/base/namespace.yml
kubectl create secret generic viaverse-vault-token -n viaverse --from-literal=token="$env:VIAVERSE_VAULT_TOKEN"
kubectl create secret generic viaverse-bootstrap-secrets -n viaverse --from-env-file=.env.k8s.local
kubectl apply -k infra/kubernetes/base
```

Spring services use ClusterIP services. Web/admin frontends use LoadBalancer services in the base manifests. Mobile images are build images, not long-running Kubernetes workloads.

The Kubernetes base includes a local HashiCorp Vault deployment. Runtime secrets and connection values are seeded into Vault by `vault-seed-runtime-secrets`, then app pods use a Vault Agent init container to render `/vault/secrets/runtime-env` before the main process starts. Keep `.env.k8s.local` outside Git; it must contain the real values for database URLs/users/passwords, Kafka brokers, Valkey, object storage, JWT/internal tokens, SMTP/SMS/OAuth settings, service base URLs, and OTLP endpoints.

When rotating or changing a value:

```powershell
kubectl delete job vault-seed-runtime-secrets -n viaverse --ignore-not-found
kubectl apply -k infra/kubernetes/base
kubectl rollout restart deployment -n viaverse
```

The included Vault deployment is for local/dev clusters. Production should use a managed or HA Vault installation with real auth policies and durable storage, while keeping the same rule: application pods receive secret material from Vault, not from committed manifests.

## API Docs

Springdoc + Scalar is enabled in the `local` profile and disabled by default elsewhere.

| Service | Scalar URL |
|---|---|
| identity-service | http://localhost:8101/scalar |
| web-bff | http://localhost:8001/scalar |
| admin-bff | http://localhost:8110/scalar |

OpenAPI JSON is available at `/v3/api-docs` when the local profile is active.

## VS Code Launch

Launch entries are grouped in the VS Code Run and Debug dropdown.

- `00 genel`: daily workflows and infrastructure controls.
- `10 apps`: individual web/admin/mobile app launches.
- `20 microservices`: individual service/BFF launches.

Useful entries:

- `dev: mock web app`: one-click product UI workflow. Starts `mock-web-bff` with the Java debugger on port `8120` and starts `web-next` on port `3000` with mock API env wired automatically. No Docker, `.env.local`, PostgreSQL, or migration step is required.
- `dev: real local stack`: starts the Docker-backed local backend flow plus web/admin apps, without Kubernetes.
- `k8s: init local`: starts Docker Compose infra, creates ignored `.env.k8s.local` if needed, initializes Vault bootstrap secrets, and applies `infra/kubernetes/base`.
- `k8s: stop local`: removes the local Kubernetes resources.
- `infra: docker local` / `infra: docker stop`: start or stop the Compose-only local infrastructure.

The mock web app workflow is the fastest path for product UI work. Use the Kubernetes entries when you specifically want to validate the container/orchestration path.

## Local Ports

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
| mock-web-bff | 8120 |

Health checks use `/actuator/health`.

## Configuration And Secrets

Use `infra/docker-compose/.env.example` as a local reference. Real developer overrides belong in `.env.local`, which is ignored by Git. Do not commit personal email addresses, real API keys, OAuth client secrets, production tokens, or real SMTP/SMS credentials.

Local placeholder values such as `viaverse`, `local-dev-...-change-me`, and `noreply@viaverse.local` are intentionally non-production defaults.

For Kubernetes, do not put secret or connection values in ConfigMaps or committed Secret YAML files. Put them in Vault, seed them from an ignored local/env file or CI secret store, and let Vault Agent provide them to the container at startup.

## Validation

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

## Stop Helpers

```powershell
.\scripts\dev\stop-local-app-ports.ps1
docker compose -f .\infra\docker-compose\docker-compose.yml down
```
