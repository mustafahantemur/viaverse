# Shared modules — current state and target structure

## Today

`packages/` already exists as a multi-module reusable library area. It holds three modules:

| Module | Contents | Verdict |
|---|---|---|
| `packages/shared-kernel` | Error types only (`AppException`, `AppErrorCode`, `ConflictException`, …) | Correctly placed, undersized. |
| `packages/observability` | Correlation filter, audit logger, global problem-details handler | Correctly placed, healthy. |
| `packages/api-contracts` | Empty placeholder. | Drop or repurpose. |

Each service additionally carries a **local** `<svc>/shared/` package that has grown into a generic toolbox. In `identity-service` today it holds:

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

About 75% of this is **not identity-specific**. Every future service (`profile-service`, `marketplace-service`, etc.) will need the same outbox, the same observed-action aspect, the same `ApiResponse`, the same `ClientIpResolver`, the same `BaseJpaEntity`. Copy-pasting that across ten services is exactly the regret we want to avoid.

## Target structure

Reorganize `packages/` into focused libraries. Each Spring Boot service depends on the ones it needs.

| Module | Owns | Notes |
|---|---|---|
| `packages/shared-kernel` | Error types, value-object scaffolding, pure utilities with **no Spring or Jakarta** dependencies. | Today's module — keep. Pure Java. |
| `packages/observability` | Correlation, audit primitives, global error handlers. | Today's module — keep. May absorb `ObservedAction` annotation/aspect from each service. |
| `packages/web-kernel` (**new**) | `ApiResponse` envelope, `ClientContextFilter`, `ClientIpResolver`, `BaseJpaEntity`, `TimeConfiguration`, `@ObservedAction` aspect, `ActionLogContext`. | Anything HTTP-or-JPA shaped that's identical across services. Depends on shared-kernel + observability. |
| `packages/messaging-kernel` (**new**) | Transactional outbox: entity, repository, writer, dispatcher, health indicator, properties. Kafka envelope shape. | The outbox is non-negotiable for every event-emitting service. |
| `packages/security-kernel` (**new, later**) | JWT decoder bootstrap, `RotatingJwtDecoder`, `BearerTokenResolver` helpers, `IdentityJwtClaims` constants. | Extract when we have a second service that needs it (marketplace will). For now identity owns it locally. |
| `packages/api-contracts` | Cross-service DTOs and Kafka event envelopes that more than one service depends on. | Currently empty. Populate when we have an event that two services need to share (e.g. `AccountCreatedV1KafkaEvent` already qualifies). |

What stays in each service's local `shared/`:

- Service-specific error registries (`IdentityErrors`, future `ProfileErrors`).
- Service-specific audit enums (`IdentityAuditEventEnum`).
- Service-specific normalization (identity needs `IdentifierNormalizer`; profile may not).
- Service-specific filters/aspects that key off domain rules.

Rule of thumb: **if a second service would need it tomorrow, it belongs in `packages/`. If only this service can ever need it, keep it local.**

## How this looks in Gradle

The convention plugin `viaverse.java-spring-service` already lives in `build-logic/`. Each new package is its own `viaverse.java-library` module under `packages/`, declared in `settings.gradle.kts`, and pulled in by services via `implementation(project(":packages:web-kernel"))`. `packages/observability` already shows the right shape (`api(project(":packages:shared-kernel"))`).

## Migration order (do BEFORE profile-service starts)

1. **`packages/messaging-kernel`** — extract `shared/messaging/outbox/*` from identity-service. Identity-service consumes the package; verify outbox dispatcher tests still pass.
2. **`packages/web-kernel`** — extract `ApiResponse`, `ClientContextFilter`, `ClientIpResolver`, `BaseJpaEntity`, `TimeConfiguration`, `@ObservedAction` aspect.
3. **`packages/api-contracts`** — move `AccountCreatedV1KafkaEvent` (and its sibling `AccountStatusChangedV1KafkaEvent`) here so profile-service can consume by typed record, not by JSON parsing.
4. Update `viaverse.java-spring-service` convention plugin to apply `packages/web-kernel` + `packages/messaging-kernel` by default (services opt out if they don't need them, but the default keeps copy-paste from creeping back).

Each step is a small PR with a passing identity-service test suite. No semantic changes.

## Why this matters for profile-service

If we extract first, profile-service's `services/profile-service/shared/` directory should be ~empty on day one — it will just have `ProfileErrors` and maybe a couple of profile-specific aspects. Outbox, audit, observability, web envelope, base entity — all from `packages/`. The bar for "is this shared?" is set correctly, and the bar carries forward to marketplace-service and beyond.

The alternative — start profile-service first, ship cross-cutting code from `services/profile-service/shared/` — is what creates two divergent copies of every "shared" pattern, then a third when marketplace lands, then nobody fixes any of them.
