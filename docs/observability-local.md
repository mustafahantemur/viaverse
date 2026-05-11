# Local Observability

Viaverse services emit application logs to stdout/stderr as structured ECS/JSON. The local observability stack is optional and shared by all services.

## Start

Start core infrastructure:

```powershell
./scripts/dev/start-core-infra.ps1
```

Start observability infrastructure:

```powershell
./scripts/dev/start-observability-local.ps1
```

Run a service:

```powershell
./gradlew.bat :services:identity-service:bootRun
```

## Services

- OpenSearch: `http://localhost:9200`
- OpenSearch Dashboards: `http://localhost:5601`
- OTLP gRPC: `localhost:4317`
- OTLP HTTP: `http://localhost:4318`

## Log Search

Local logs are written to the `viaverse-logs-local` index. In OpenSearch Dashboards, create an index pattern for `viaverse-logs-local*`, then filter by fields such as:

- `service.name`
- `deployment.environment`
- `correlationId`
- `requestId`
- `trace.id`
- `span.id`
- `event.action`
- `event.outcome`
- `error.code`

When Docker stdout logs are ingested through the local OpenTelemetry Collector, parsed ECS fields can appear under `Attributes.*` depending on the OpenSearch exporter mapping, for example `Attributes.service.name` or `Attributes.event.action`.

## Service Onboarding

Any current or future Viaverse service joins the shared log pipeline by following the platform convention:

1. Set `spring.application.name` to the service name.
2. Use `logging.structured.format.console=ecs`.
3. Set `logging.structured.ecs.service.name=${spring.application.name}`.
4. Set `logging.structured.ecs.service.environment=${VIAVERSE_ENV:local}`.
5. Log domain/application events through SLF4J key-value logging.
6. Keep logs safe: never log access tokens, refresh tokens, Authorization headers, OTPs, passwords, API keys, client secrets, or raw phone/email identifiers.
7. Use `audit_log` only for typed security/legal/account-critical audit events.

The collector is not service-specific. It accepts OTLP logs/traces/metrics from any service and can tail Docker stdout logs from local containers.

## OpenTelemetry

When a service is configured with OpenTelemetry instrumentation, use generic environment keys:

```text
OTEL_SERVICE_NAME=<service-name>
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
OTEL_LOGS_EXPORTER=otlp
OTEL_TRACES_EXPORTER=otlp
```

OpenTelemetry is optional for local boot and tests. A service must still start and test without OpenSearch or the collector.
