# Shared modules — current state and target structure

## Today

`packages/` is now the shared-library boundary for backend services:

| Module | Contents | Verdict |
|---|---|---|
| `packages/shared-kernel` | Shared error primitives (`AppException`, `AppErrorCode`, `RetryAfterAware`, …) | Pure Java base layer. |
| `packages/observability` | Correlation filter, audit logger, global problem-details handler, OTel bootstrap | Cross-service observability. |
| `packages/web-kernel` | API envelope, HTTP context, auditing base entity, observed-action support | Extracted from identity-service; default for microservices. |
| `packages/messaging-kernel` | Transactional outbox + shared Jackson setup | Extracted from identity-service; default for event-emitting microservices. |
| `packages/security-kernel` | Identity-issued JWT validation primitives (`IdentityJwtClaims`, `RotatingJwtDecoder`, validator) | Extracted once profile-service became the second JWT consumer. |
| `packages/api-contracts` | Shared Kafka contracts for identity/profile events | Populated; no longer a placeholder. |

`identity-service` used to carry most of the generic toolbox locally. The split below is now the historical source we extracted from:

```
shared/api          ← ApiResponse envelope
shared/aspect       ← @ObservedAction, audit aspect, refresh-token-reuse aspect, AuditableCommand / Result markers
shared/audit        ← AuditEvent, AuditLogAdapter, AuditLogJpaEntity (per-service audit table)
shared/config       ← TimeConfiguration (Clock bean)
shared/error        ← IdentityErrors registry, RateLimitExceededException, RefreshTokenReuseDetectedException
shared/logging      ← ObservedAction annotation + aspect, ActionLogContext, LogParam
shared/messaging    ← Outbox: dispatcher, JPA entity, repository, properties, health indicator
shared/normalization← IdentifierNormalizer (libphonenumber-backed)
shared/persistence  ← BaseJpaEntity
shared/security     ← ClientContextFilter, ClientIpResolver
```

About 75% of that was **not identity-specific**. The reusable pieces now live under `packages/`, so `profile-service` did not need to grow its own generic `shared/` copy.

## Target structure

Reorganize `packages/` into focused libraries. Each Spring Boot service depends on the ones it needs.

| Module | Owns | Notes |
|---|---|---|
| `packages/shared-kernel` | Error types, value-object scaffolding, pure utilities with **no Spring or Jakarta** dependencies. | Stable pure-Java base. |
| `packages/observability` | Correlation, audit primitives, global error handlers, reusable OTel beans. | Stable cross-service layer. |
| `packages/web-kernel` | `ApiResponse` envelope, `ClientContextFilter`, `ClientIpResolver`, `BaseJpaEntity`, `TimeConfiguration`, `@ObservedAction` aspect, `ActionLogContext`. | Anything HTTP-or-JPA shaped that's identical across services. |
| `packages/messaging-kernel` | Transactional outbox: entity, repository, writer, dispatcher, health indicator, properties, shared Jackson mapper fallback. | The outbox is non-negotiable for every event-emitting service. |
| `packages/security-kernel` | Shared validation primitives for identity-issued JWTs. | Route policy still stays service-local. |
| `packages/api-contracts` | Cross-service DTOs and Kafka event envelopes that more than one service depends on. | Holds identity account and profile event contracts. |

What stays in each service's local `shared/`:

- Service-specific error registries (`IdentityErrors`, future `ProfileErrors`).
- Service-specific audit enums (`IdentityAuditEventEnum`).
- Service-specific normalization (identity needs `IdentifierNormalizer`; profile may not).
- Service-specific filters/aspects that key off domain rules.

Rule of thumb: **if a second service would need it tomorrow, it belongs in `packages/`. If only this service can ever need it, keep it local.**

## How this looks in Gradle

The convention plugin `viaverse.java-spring-service` already lives in `build-logic/`. Each new package is its own `viaverse.java-library` module under `packages/`, declared in `settings.gradle.kts`, and pulled in by services via `implementation(project(":packages:web-kernel"))`. `packages/observability` already shows the right shape (`api(project(":packages:shared-kernel"))`).

## Migration order completed before / during profile-service start

1. **`packages/messaging-kernel`** — extract `shared/messaging/outbox/*` from identity-service. Identity-service consumes the package; verify outbox dispatcher tests still pass.
2. **`packages/web-kernel`** — extract `ApiResponse`, `ClientContextFilter`, `ClientIpResolver`, `BaseJpaEntity`, `TimeConfiguration`, `@ObservedAction` aspect.
3. **`packages/api-contracts`** — move `AccountCreatedV1KafkaEvent` (and its sibling `AccountStatusChangedV1KafkaEvent`) here so profile-service can consume by typed record, not by JSON parsing.
4. Update `viaverse.java-spring-service` convention plugin to apply `packages/web-kernel` + `packages/messaging-kernel` by default (services opt out if they don't need them, but the default keeps copy-paste from creeping back).
5. Extract `packages/security-kernel` once `profile-service` became the second JWT consumer; keep route-specific security chains local.

Each step is a small PR with a passing identity-service test suite. No semantic changes.

## Why this matters for profile-service

Because we extracted first, `services/profile-service/shared/` stayed empty on day one. Outbox, observability, web envelope, base entity, and now JWT validation primitives all come from `packages/`. The bar for "is this shared?" is set correctly, and the bar carries forward to marketplace-service and beyond.

The alternative — start profile-service first, ship cross-cutting code from `services/profile-service/shared/` — is what creates two divergent copies of every "shared" pattern, then a third when marketplace lands, then nobody fixes any of them.
