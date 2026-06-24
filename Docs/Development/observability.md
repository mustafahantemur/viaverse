# Viaverse Observability

## Standard

Every service emits ECS/JSON logs to stdout/stderr and includes:

- `service.name`
- `service.namespace`
- `deployment.environment`
- `correlation.id`
- `request.id`
- `trace.id` and `span.id` when tracing is active
- `event.action`, `event.outcome`, and `error.code` for application actions

Fluent Bit tails container stdout logs and ships ECS/JSON application logs to
OpenSearch. Host-run services can also export Logback events over OTLP to the
shared OpenTelemetry Collector during local debugging. Services send traces and
metrics to the same collector. Business code does not write directly to
OpenSearch.

## Local startup

```powershell
./scripts/dev/start-core-infra.ps1
```

The main local infra script starts the full developer stack: PostgreSQL, Valkey,
Kafka, Mailpit, SeaweedFS, OpenSearch, OpenSearch Dashboards, Fluent Bit,
OpenTelemetry Collector, Prometheus, Jaeger, and Kafka UI. It also applies the
shared log index template, retention policy, and the `viaverse-logs-*`
OpenSearch Dashboards data view used by Discover.

`./scripts/dev/start-observability-local.ps1` remains available when only the
observability layer needs to be started or refreshed independently.

## Environment promotion

The JSON files under `infra/docker-compose/opensearch/` are intentionally plain
OpenSearch API payloads so staging and production automation can apply the same
contract with environment-specific overrides:

- `viaverse-logs-template.json`
- `viaverse-logs-retention-policy.json`

Local defaults use one shard, zero replicas, and 30-day retention. Staging and
production deployment automation should override shard/replica counts and
retention to match capacity and policy while preserving the same field mappings.

## Onboarding a new service

1. Log ECS/JSON to stdout/stderr.
2. Set `spring.application.name` and `VIAVERSE_ENV`.
3. Enable OTLP export to the shared collector for traces/metrics.
4. Register the shared `CorrelationIdFilter`.
5. Use structured action logs with `event.action` and `event.outcome`.

Logs from all services land in the same `viaverse-logs-*` family and remain
filterable by `service.name`. Containerized services arrive through Fluent Bit;
host-run local services arrive through OTLP log export.

## Where to view telemetry locally

- Logs: OpenSearch Dashboards at `http://localhost:5601`, using the
  `viaverse-logs-*` data view in Discover.
- Traces: Jaeger at `http://localhost:16686`.
- Metrics: Prometheus at `http://localhost:9090`.

OpenTelemetry is the instrumentation and transport standard, not a separate UI.
In the local stack the collector receives OTLP traffic, forwards traces to
Jaeger, forwards logs to OpenSearch, and currently keeps the OTLP metrics
pipeline at debug-export only while Prometheus scrapes service
`/actuator/prometheus` endpoints directly.

Useful Prometheus starter queries:

- `jvm_memory_used_bytes{service_name="identity-service"}`
- `http_server_requests_seconds_count{service_name="identity-service"}`
- `hikaricp_connections_active{service_name="identity-service"}`
- `sum by (uri, status) (http_server_requests_seconds_count{service_name="identity-service"})`
