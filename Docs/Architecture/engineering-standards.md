# Engineering standards (all backend services)

The bar identity-service already meets. profile-service and every later service inherit this. Treat the list as a PR-gate checklist, not a wish list.

## Architecture

- **Hexagonal layering**, mirrored in package names:
  ```
  <svc>.<context>.domain.{model, value, enums, policy}
  <svc>.<context>.application.{port.in, port.out, service, usecase}
  <svc>.<context>.infrastructure.{adapter.in.web.{controller, dto, mapper}, adapter.out.{persistence, cache, messaging, …}, security}
  ```
- Domain has **no Spring, no JPA, no Jackson**. Pure Java. Validation, invariants, lifecycle transitions all live there.
- Application defines use-case interfaces (`*UseCase`) and orchestrates ports. No HTTP types, no JPA entities — only domain types and DTOs that the use case itself owns.
- Infrastructure does the dirty work: web controllers, JPA adapters, Kafka publishers, cache adapters, external HTTP clients.
- One use case per public verb. `*UseCaseImpl` is the implementation; it does **not** carry side effects beyond what its command implies.

## Boundaries between contexts

- Cross-context calls inside a service go through **ports**, not through the other context's domain types directly. (Auth depends on AccountRepository in identity-service for exactly this reason.)
- Cross-service calls go through **events** by default. Synchronous internal RPC is allowed for read-side cache misses; never for writes.

## Persistence

- Flyway, forward-only. One baseline migration per service (`V1__init_*.sql`); incremental migrations after that.
- JPA entities live in `infrastructure/adapter/out/persistence/entity`. **Never imported into domain or application.**
- MapStruct mappers convert entity ↔ domain. No hand-rolled mappers.
- Every table inherits `BaseJpaEntity` (created_at, updated_at, version). Optimistic locking by default.
- Identifiers (email, phone) are normalized **before** they reach persistence.

## Web layer

- One controller per resource group. Methods stay thin — they construct the use-case command, call, map result.
- Requests use `record` + Jakarta validation (`@NotBlank`, `@Email`, custom).
- Responses go through the shared `ApiResponse<T>` envelope (`{ success, data, code, identityCode, detail, fieldErrors }`). No raw maps in production endpoints.
- Errors thrown from domain/application implement a service-local `XxxException` extending `AppException` from `packages/shared-kernel`. `GlobalProblemDetailsHandler` from `packages/observability` translates them. Never throw `RuntimeException` from a use case.

## Security

- Stateless. Every authenticated endpoint reads JWT via Spring Security's `oauth2ResourceServer`.
- A service-local `SecurityConfiguration` enumerates exactly which paths are `permitAll` vs `authenticated`. `anyRequest().denyAll()` is the default.
- Internal-only endpoints (`/internal/**`) require either mTLS or a shared bearer secret; **never** open to the public BFF.
- CORS is configured per service; the BFFs don't pass CORS through.

## Messaging

- Every event publish goes through the **transactional outbox**. Domain writes the event row in the same DB transaction; a separate dispatcher publishes to Kafka and marks the row sent.
- Event payloads are typed records (`*V1KafkaEvent`), versioned in the type name. **Never** publish loose maps.
- Consumers are idempotent. Use `event_id` as the dedup key.

## Observability

- `@ObservedAction("svc.context.action")` on every use-case method that's worth a metric. Records duration + outcome on a Micrometer timer with the action name.
- `@AuditEvent(...)` on use cases that should be auditable (account creation, capability changes, password change, business approval). The aspect writes a row to the service's audit table.
- All logs are ECS-structured JSON via `logback-spring.xml` — same template across services.
- `CorrelationIdFilter` (from `packages/observability`) puts an `X-Correlation-Id` in MDC for every request and propagates it on outbound HTTP/Kafka.
- OpenTelemetry OTLP exporter wired by `packages/observability` — traces flow to Jaeger in local dev.

## Testing

- **Unit tests** live next to the code under test. Pure JUnit + AssertJ. No Spring context for domain or policy tests.
- **Integration tests** use Testcontainers (Postgres + Valkey + Kafka). Same pattern identity-service uses today. One `*IntegrationTest` per significant cross-layer flow.
- Tests assert on `identityCode`/`code` envelope fields, not on `detail`, so copy changes don't break them.
- Each new use case ships with at least one happy path and one failure-path integration test.

## Configuration

- All config is `@ConfigurationProperties` rooted at `viaverse.<svc>.*`. **No** `@Value` on business code.
- Defaults live in `application.yml`; per-environment overrides in `application-<profile>.yml`. Local dev defaults are usable as-is via Docker Compose.
- Secrets via env vars, never committed.

## Coding style

- Java 25, records where they fit. Sealed hierarchies for closed domains (e.g. `AuthNextStepEnum` is an enum, but a closed-result hierarchy can be sealed).
- Comments explain *why*, not *what*. The codebase already follows this — keep it.
- Public types have a one-line Javadoc when their name doesn't carry the story.
- One class per file. Test files share the package of the class under test.
- No `var` for public APIs; OK locally when the inferred type is obvious.

## What goes where (cheat sheet)

| Concern | Lives in |
|---|---|
| Outbox dispatcher | `packages/messaging-kernel` (after extraction) |
| `ApiResponse`, `ClientIpResolver`, `BaseJpaEntity`, `@ObservedAction` | `packages/web-kernel` (after extraction) |
| Service-specific errors, audit enums, normalization | `<svc>/shared/` |
| Domain model + policies | `<svc>/<context>/domain/` |
| Use cases | `<svc>/<context>/application/usecase/` |
| Controllers, DTOs, JPA adapters | `<svc>/<context>/infrastructure/adapter/...` |

If a piece of code keeps showing up in two services, it belongs in `packages/`. If it shows up in one service but two contexts, it belongs in that service's `shared/`. If it shows up in one context only, it lives in that context.
