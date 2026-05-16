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

Services send telemetry to the shared OpenTelemetry Collector; business code does
not write directly to OpenSearch.

## Local startup

```powershell
./scripts/dev/start-core-infra.ps1
./scripts/dev/start-observability-local.ps1
```

The observability script starts OpenSearch, OpenSearch Dashboards, and the
collector, then applies the shared log index template and retention policy.

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
3. Enable OTLP export to the shared collector.
4. Register the shared `CorrelationIdFilter`.
5. Use structured action logs with `event.action` and `event.outcome`.

Logs from all services land in the same `viaverse-logs-*` family and remain
filterable by `service.name`.
