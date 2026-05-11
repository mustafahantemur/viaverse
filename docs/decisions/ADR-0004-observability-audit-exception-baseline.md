# ADR-0004: Observability, Audit, And Exception Baseline

## Status

Accepted

## Context

Viaverse will eventually handle identity, service requests, provider workflows, payment-sensitive actions, and moderation-sensitive events.

## Decision

Adopt structured JSON logging, correlation IDs, audit logging, and consistent exception handling as platform baselines.

Application logs are stdout/stderr only. Services must not write application logs to local text files and must not write application logs to `audit_log`.

The default local central log/search backend is OpenSearch behind a collector. OpenSearch is part of shared local infrastructure, not a service-specific dependency. The collector must be able to accept logs from any Viaverse service.

Each service should emit structured ECS/JSON logs with:

- `service.name`
- `deployment.environment`
- `correlationId` / `requestId`
- `trace.id` / `span.id` when tracing instrumentation is configured
- `event.action` / `event.outcome`
- `error.code` for handled errors

Sensitive values must not be logged. This includes access tokens, refresh tokens, Authorization headers, OTPs, passwords, API keys, client secrets, and raw phone/email identifiers unless explicitly masked.

OpenSearch ingestion is performed by local/shared infrastructure through a collector. Business code must not write directly to OpenSearch.

New services join the logging pipeline by:

1. Using the shared Spring service logging convention: structured ECS console logging.
2. Setting `spring.application.name` to the service name.
3. Setting `VIAVERSE_ENV` for local/stage/prod environment metadata.
4. Emitting safe SLF4J key-value logs for domain events.
5. Running under Docker stdout collection or exporting OTLP logs/traces to the shared collector.

## Consequences

- Logs must be machine-readable and safe.
- Correlation IDs must travel through request handling and outbound calls.
- Sensitive events must produce audit records once those flows exist.
- Public errors must be consistent and must not expose secrets or internals.
- `audit_log` remains reserved for typed security/legal/account-critical audit events.
- OpenSearch, OpenSearch Dashboards, and the collector are optional local infrastructure and must not be required for basic service tests.
