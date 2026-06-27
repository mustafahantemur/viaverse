# Repository, Backend, Mobile, and Architecture Plan

Status date: **2026-06-24**

This is the cross-cutting plan for the **repository, backend, mobile, DevOps, and security** direction of
Viaverse. It complements the product/UX plan ([Docs/Product/viaverse-ux-overhaul-roadmap.md](../Product/viaverse-ux-overhaul-roadmap.md))
and the existing architecture notes. It is a planning/recommendation artifact — **no files are deleted or
rewritten by this document**; the documentation audit below lists proposed changes for separate approval.

**Important grounding facts (verified against the repo, not assumed):**

- The backend is **Java 25 + Spring Boot**, already organized in **strict Hexagonal / Ports & Adapters** layering.
  `Docs/Architecture/engineering-standards.md` is the canonical, enforced standard; `identity-service` is the
  reference implementation (255 Java files).
- Cross-cutting infrastructure is **already extracted** into `packages/` (`shared-kernel`, `observability`,
  `web-kernel`, `messaging-kernel`, `security-kernel`, `api-contracts`).
- Implemented services: identity, profile (148), marketplace (86), content (45), media (45),
  trust-gamification (25), plus `web-bff` (30) and `admin-bff` (12). **Stub shells (9 files each):**
  messaging, notification, payment, search, ads-monetization.
- Infra present: Postgres, Valkey, Kafka, OpenSearch, Fluent Bit, OpenTelemetry Collector, Jaeger, Prometheus,
  SeaweedFS, Mailpit; K8s base with HashiCorp Vault; Dockerfiles for all services/apps.
- **Genuine gaps:** no `.proto`/gRPC yet (planned in `service-communication.md`); no AWS targets; no Cassandra;
  JWT uses an HMAC shared secret (asymmetric/JWK rotation not yet); HMAC *request signing* (distinct from JWT
  signing) not present.

> **Stack note:** the original request referenced ".NET / ASP.NET Core" and ".NET-flavored" cross-cutting terms
> (pipeline behaviors, global response models). Confirmed direction: **stay on Java/Spring Boot**. Those terms
> are mapped to their Spring equivalents throughout (§5).

---

## 1. Markdown documentation audit strategy

**Principles:** never delete blindly; prefer *merge/redirect/archive* over deletion; keep one canonical home per
topic; preserve history by moving outdated planning docs into an `archive/` area rather than removing them.

**Method:**
1. Inventory all project `.md` (44 today, excluding `node_modules`/`build`).
2. Classify each: **Keep** (current & canonical), **Update** (current but stale in places), **Merge** (content
   belongs in a canonical doc), **Archive** (historical planning, keep for provenance), **Reference** (vendored
   asset/prototype, not living docs).
3. Map every kept doc to one of the long-term topics (§2).
4. Apply changes only after this audit is approved, one small PR per move, updating inbound links.

**Audit findings (proposed actions — not yet applied):**

| Doc | Action | Why |
|---|---|---|
| `Architecture/engineering-standards.md` | **Keep (canonical)** + update | The PR-gate standard. Apply the `service-communication.md` note: rename "Messaging" → "Events" and add a sibling "RPC (gRPC)" subsection. |
| `Architecture/service-communication.md` | Keep | Canonical Kafka/gRPC/REST decision guide. |
| `Architecture/shared-modules.md` | Update | "Today/Target" split is now mostly "Today"; collapse the historical framing. |
| `Architecture/content-and-media-boundaries.md` | Keep | Bounded-context boundary. |
| `Architecture/feed-and-recommendation.md` | Keep | Feed/reco direction. |
| `Architecture/trust-and-moderation.md` | Keep | Trust/moderation direction. |
| `Architecture/web-app-mock-bff-product-prototype.md` | **Merge/redirect** | Overlaps with the new `Docs/Product/*`. Reduce to a short pointer to the Product docs to avoid three copies of the same prototype narrative. |
| `Development/web-app-mock-bff-product-prototype-brief.md` | **Merge/redirect** | Same overlap; fold into `Docs/Product/` and leave a one-line pointer. |
| `Architecture/identity-service/architecture.md` | Keep | Accurate reference architecture. |
| `Architecture/identity-service/coding-standards.md` | **Merge** | Largely duplicates `engineering-standards.md`; merge any identity-only deltas, then redirect. |
| `Architecture/identity-service/plan.md`, `phase-2-plan.md` | **Archive** | Historical planning; move to `Docs/Architecture/identity-service/archive/` with a "Historical" header. |
| `Architecture/marketplace-service/*`, `profile-service/*` | Keep | Per-service bounded-context docs; current. |
| `Development/current-implementation-status.md` | Keep + update date | Living status doc; keep current. |
| `Development/initial-development-start-guide.md` | Update | De-duplicate against README "First Local Run" (one canonical run guide; the other links to it). |
| `Development/observability.md` | Keep | Canonical observability runbook (consider moving under `Architecture/` for topic grouping). |
| `Product/*` (3 docs) + `Architecture/decisions/0001–0006` | Keep | New product plan + ADRs. |
| `Viaverse Design System/*` | **Reference** | Design assets/kit; not living architecture docs. |
| `ViaverseUIPrototype/*` | **Reference** | Vendored prototype source (now in-repo for inspiration); not living docs. |
| root `README.md` | Keep | Repo map + run guide. |

---

## 2. Which docs should exist long-term (topic taxonomy)

Organize living docs around clear topics. Most already exist; this is the target home for each.

| Topic | Canonical doc(s) |
|---|---|
| **Product vision** | `Docs/Product/viaverse-ux-overhaul-roadmap.md` |
| **UX flows** | `Docs/Product/role-based-navigation-model.md` |
| **Frontend architecture** | `Docs/Product/frontend-mock-bff-contract.md` |
| **Backend architecture** | `Docs/Architecture/engineering-standards.md` + this doc (§3) + per-service `Docs/Architecture/<svc>/` |
| **Mock BFF architecture** | `Docs/Product/frontend-mock-bff-contract.md` + `Docs/Architecture/web-app-mock-bff-product-prototype.md` (reduced) |
| **API contracts** | `packages/api-contracts` (code) + a new `Docs/Architecture/api-contracts.md` (governance/versioning) |
| **Mobile app plan** | new `Docs/Architecture/mobile-architecture.md` (seeded from §10) |
| **DevOps / infrastructure** | README "Docker/K8s" + new `Docs/Development/devops-deployment.md` (seeded from §11) |
| **Security** | new `Docs/Architecture/security.md` (seeded from §8) |
| **Observability** | `Docs/Development/observability.md` |
| **Messaging / events** | `Docs/Architecture/service-communication.md` + the "Events" section of `engineering-standards.md` |
| **Coding standards** | `Docs/Architecture/engineering-standards.md` (single source) |
| **Roadmap** | `Docs/Product/viaverse-ux-overhaul-roadmap.md` (product) + `Docs/Architecture/<svc>/*-roadmap.md` (per-service) |

New docs (security, mobile, devops, api-contracts governance) should be **extracted from this plan** once their
sections stabilize, so this document stays a plan and they become the living references.

---

## 3. Backend architecture proposal

**Keep the existing hexagonal model — do not redesign it.** The proposal is *conformance, consolidation, and
filling gaps*, not a rewrite.

- **Canonical layering** (already in `engineering-standards.md`):
  `domain.{model,value,enums,policy}` → `application.{port.in,port.out,service,usecase}` →
  `infrastructure.adapter.{in.web,out.persistence|cache|messaging}` + `infrastructure.security`.
  Domain is pure Java (no Spring/JPA/Jackson); controllers stay thin; one use case per public verb.
- **Make the standard the default, not a manual checklist.** Have the `viaverse.java-spring-service` convention
  plugin apply `packages/web-kernel` + `messaging-kernel` + `observability` by default, so the **5 stub services**
  (messaging, notification, payment, search, ads-monetization) start conformant instead of drifting.
- **Per-service docs** follow the marketplace/profile pattern (`01-bounded-context`, `02-data-model`,
  `03-key-flows`, `04-roadmap`) for each newly-built service.
- **Enums are first-class:** dedicated `domain/enums` packages, `*Enum` suffix (already the convention — e.g.
  `AuthNextStepEnum`), sealed hierarchies for closed result types.
- **"What goes where" stays explicit** (the cheat-sheet in `engineering-standards.md`): shared → `packages/`;
  service-but-multi-context → `<svc>/shared/`; single-context → that context.

**Build-order guidance for the 5 stub services:** wire them to the kernels + outbox + observability first
(empty but conformant skeleton), then implement one bounded context at a time behind ports, REST at the BFF,
events out via the outbox, gRPC only when a synchronous reader appears.

---

## 4. Identity-service refactor recommendations

Identity is "mostly done" and is the reference, but it predates the `packages/` extraction, so it carries
historical local copies and a few inconsistencies.

1. **Consume `packages/`, shed local duplicates.** Ensure identity uses `packages/web-kernel`
   (`ApiResponse`, `BaseJpaEntity`, `ClientIpResolver`, `@ObservedAction`), `packages/messaging-kernel` (outbox),
   `packages/observability` (correlation, audit, global error handler) — and delete any remaining local copies
   under `identity/shared/*` that now live in `packages/`. Each removal is a small PR with the suite green.
2. **One global error handler.** `config/GlobalExceptionHandler.java` (local) should defer to
   `GlobalProblemDetailsHandler` from `packages/observability`; keep only identity-specific exception→code
   mappings locally. Avoid two handlers diverging.
3. **Single coding standard.** Merge `identity-service/coding-standards.md` deltas into
   `engineering-standards.md` and redirect, so there is one standard.
4. **JWT validation modernization (cross-service, identity-led):** move from the HMAC shared-secret JWT to
   **asymmetric signing (RS256/ES256) with a JWK Set endpoint** on identity, and let resource servers use the
   existing `RotatingJwtDecoder` (`security-kernel`) against the JWK URI. This enables real **key rotation**
   without redeploying every service (see §8). Sequence behind a feature flag with dual-validation during
   cutover.
5. **Finish the documented "identity slimming gate":** drop the mirrored `identity_account` display columns once
   the profile-service mirror has burned in and clients stop reading `/me` for display data (already tracked in
   `current-implementation-status.md`).
6. **Reconcile capability naming** with the mock prototype: identity/profile use `CUSTOMER`; the mock-web-bff/web
   client uses `STANDARD`. Pick one (recommend `CUSTOMER` to match the real domain) and align the mock contract
   (see §9, §12).

---

## 5. Cross-cutting concerns strategy

These are **already implemented** in Spring idioms; the work is making them uniform across all services. The
table maps the requested (.NET-flavored) concepts to the Spring mechanism already in use.

| Concern | Requested term | Spring mechanism (in repo) |
|---|---|---|
| Global error handling | global exception middleware | `@RestControllerAdvice` `GlobalProblemDetailsHandler` (`packages/observability`) → RFC-7807 ProblemDetails + `ApiResponse<T>` |
| Global response model | global response wrapper | `ApiResponse<T>` envelope `{ success, data, code, identityCode, detail, fieldErrors }` (`packages/web-kernel`) |
| Validation | FluentValidation | Jakarta Bean Validation on request `record`s (`@NotBlank`, `@Email`, custom) + domain invariants |
| Logging / metrics | pipeline behavior | `@ObservedAction("svc.context.action")` AOP aspect → Micrometer timer + structured action log |
| Auditing | pipeline behavior | `@AuditEvent` AOP aspect → per-service audit table |
| Correlation / middleware | middleware | `CorrelationIdFilter` (`OncePerRequestFilter`) in `packages/observability` |
| Authorization | authorization filter | Spring Security filter chain per service; `anyRequest().denyAll()` default; method security where needed |
| Idempotency / abuse | behavior | Valkey rate-limit adapter; refresh-token reuse aspect |

**Strategy:** (a) apply these via the kernels-by-default convention plugin so new services inherit them; (b) keep
the *boundaries* explicit — aspects/filters for cross-cutting tech, not business rules; (c) standardize a
`@UseCase`-level observability + audit convention so every public verb is measurable and auditable.

---

## 6. Observability / logging / tracing strategy

The stack is in place (ECS JSON → Fluent Bit → OpenSearch; OTLP → Collector → Jaeger; Prometheus scrape). Plan:

- **Logging:** ECS/JSON to stdout everywhere; `correlation.id`/`request.id`/`trace.id` in MDC; `event.action` +
  `event.outcome` + `error.code` on every use case. Add **PII scrubbing** at the log-encoder level (identifiers,
  tokens, emails) so structured logs never carry secrets.
- **Tracing:** OpenTelemetry as the single instrumentation/transport standard; traces to Jaeger locally, OTLP to
  a managed backend in cloud. Propagate context across HTTP **and** Kafka **and** (future) gRPC interceptors.
- **Metrics:** finish the OTLP metrics pipeline (currently debug-export while Prometheus scrapes
  `/actuator/prometheus` directly) — pick one path per environment. Standardize **RED** (Rate/Errors/Duration)
  dashboards per service from `http_server_requests_*` and the `@ObservedAction` timers; add outbox lag and
  consumer lag dashboards.
- **Sampling & cost:** head-based sampling in dev, tail-based (error-biased) in cloud; per-environment OpenSearch
  shard/replica/retention overrides (already templated in `infra/docker-compose/opensearch/`).
- **Onboarding a service** follows the 5-step checklist in `Docs/Development/observability.md`.

---

## 7. Messaging / Kafka strategy

Canonical patterns already exist (`service-communication.md`, `engineering-standards.md`): **transactional
outbox**, typed versioned events (`*V1KafkaEvent`) in `packages/api-contracts`, idempotent consumers keyed by
`event_id`. Plan:

- **Keep the outbox non-negotiable** for every event-emitting service (domain write + outbox row in one tx;
  dispatcher publishes and marks sent).
- **Contract governance (§9):** events live in `packages/api-contracts` as typed records today. Add a written
  versioning policy (additive-only within `Vn`; new `Vn+1` type for breaking changes) and consider a
  **Schema Registry** (Avro/Protobuf) if/when external or polyglot consumers appear. Java-only consumers can stay
  on typed records.
- **Reliability:** standardize **retry + dead-letter topics** and a consumer-error policy; expose consumer lag and
  DLQ depth as metrics/alerts.
- **Topic conventions:** `viaverse.<domain>.<entity>-events` naming, partition key = aggregate id, documented
  partitioning/ordering guarantees per topic.
- **Events vs RPC:** "tell everyone who cares" → Kafka; "ask one service, use the reply" → gRPC; never a
  three-service-deep synchronous chain.

---

## 8. Security strategy

Security posture is already strong (stateless JWT resource servers, refresh rotation + reuse detection, Valkey
rate limiting, Vault-sourced secrets, ProblemDetails errors). Recommendations focus on the gaps.

- **JWT design:** keep `iss/sub/sid/iat/exp`; add explicit **roles/scopes/capabilities** claims so authorization
  is claim-driven (capabilities already exist in the domain). Short access-token TTL.
- **Key rotation (priority gap):** migrate JWT from HMAC shared-secret to **asymmetric RS256/ES256 with a JWK Set
  URI** published by identity-service; resource servers validate via the existing `RotatingJwtDecoder`
  (`security-kernel`). This is the idiomatic Spring Security path (`oauth2ResourceServer().jwt()` with a
  `JwtDecoder` backed by `jwkSetUri`) and enables rolling keys without mass redeploys. Dual-validate during
  cutover.
- **Refresh tokens:** keep rotation + **reuse detection** (already present); store hashes only; bind to
  device/session; revoke-all on reuse.
- **HMAC request signing (gap):** add HMAC signing for **service-to-service internal calls and inbound webhooks**
  (timestamp + nonce + body HMAC, short clock skew) — distinct from JWT signing. Pairs with mTLS for defense in
  depth.
- **Service-to-service:** `/internal/**` and gRPC require **mTLS in staging/prod**, shared bearer in local dev;
  never exposed through public BFFs.
- **gRPC security:** mTLS + correlation/trace interceptors; same claim propagation as HTTP; no public gRPC port.
- **Role/permission model:** formalize a capability→permission mapping carried in JWT claims; method-level
  authorization for sensitive verbs; `denyAll()` default per service.
- **Audit logs:** keep `@AuditEvent` per-service tables for security-relevant verbs (account, capability,
  password, business approval); ship to OpenSearch for retention/search.
- **Rate limiting & input validation:** Valkey INCR+TTL (present); Jakarta validation + domain invariants
  (present); add request-size/abuse guards at the BFF edge.
- **Secure error responses:** RFC-7807 ProblemDetails + `ApiResponse` codes only; **never** stack traces or
  internal identifiers to clients (present — keep enforcing).
- **Secret & sensitive-data handling:** Vault-sourced at runtime (present); no secrets in Git or ConfigMaps;
  add **field-level encryption** for any stored sensitive PII and **log PII scrubbing** (§6).

A dedicated `Docs/Architecture/security.md` should be extracted from this section once stable.

---

## 9. API and Mock BFF contract strategy

- **BFF boundary:** public clients (web, admin, mobile) speak **REST/JSON to a BFF only** (`web-bff`,
  `admin-bff`, future `mobile-bff`); BFFs aggregate via gRPC/events internally. No service is publicly exposed.
- **Mock BFF mirrors the future services:** `mock-web-bff` is the standalone prototype backend; the web client's
  `mockAppClient.ts`/`authClient.ts` already consume it over HTTP (see
  [frontend-mock-bff-contract.md](../Product/frontend-mock-bff-contract.md)). Keep all prototype mock data **in
  the Mock BFF**, never in UI components.
- **Contract parity is the invariant:** TypeScript `*View` ↔ Mock BFF DTOs ↔ real-service DTOs must stay aligned.
  **Reconcile `STANDARD` (mock) vs `CUSTOMER` (real)** as the first parity fix.
- **Event contracts:** `packages/api-contracts` is the typed home for cross-service Kafka events; add a
  governance doc (`Docs/Architecture/api-contracts.md`) covering naming, versioning, and the future
  proto/Schema-Registry path.
- **OpenAPI:** springdoc + Scalar already exposed in the `local` profile (`/scalar`, `/v3/api-docs`); keep it
  per service and add contract tests that assert the envelope/codes.
- **gRPC contracts:** introduce `.proto` files in `packages/api-contracts` when the first synchronous reader
  lands (profile capability read for marketplace), compiled to Java (+ Kotlin for mobile reuse if needed).

---

## 10. Mobile app architecture direction

**Decision: Android-first with Jetpack Compose now; shared business/data logic in a Kotlin Multiplatform module;
iOS UI later.** Both `apps/mobile-android` and `apps/mobile-kmp` already exist (KMP is in CI).

- **Shared (`mobile-kmp/shared`):** domain models, data/repository layer, API clients (Ktor) against the BFF,
  the **role-based navigation/mode model**, and DTOs mirroring the `*View` contracts. One source of truth for
  product logic, reused by iOS later.
- **Android app:** Jetpack Compose UI consuming the shared module; mirrors the web product logic — Feed,
  Requests, Listings/Explore, Messages, Support, Settings, with **role-based navigation** and **mode switching
  from profile/settings** (no top-of-app mode dropdown), matching [ADR-0001](decisions/0001-role-based-navigation-and-mode-model.md).
- **iOS (later phase):** native SwiftUI (or Compose Multiplatform if the team commits to it) on top of the same
  shared KMP module — minimal duplicated logic.
- **Transport:** mobile talks REST to a BFF (`web-bff` initially, a dedicated `mobile-bff` if mobile-specific
  aggregation grows). Same contracts, same auth (JWT + refresh rotation), same correlation headers.
- **Parity rule:** the three modes (Seeker/Provider/Business), setup gating, and mode-switch-from-settings behave
  identically to web; differences are presentation only.

A dedicated `Docs/Architecture/mobile-architecture.md` should be extracted from this section.

---

## 11. DevOps / deployment direction

- **Local (present):** Docker Compose full stack; one-click VS Code flows; K8s base + Vault for the
  container/orchestration path. Keep these as the developer inner loop.
- **Containers:** per-service Dockerfiles (present). Add image scanning and SBOM in CI; pin base images.
- **Kubernetes:** the `infra/kubernetes/base` Kustomize set + Vault Agent secret injection is the model. Add
  **per-environment overlays** (dev/staging/prod), resource requests/limits, HPA autoscaling, readiness/liveness
  from `/actuator/health`, and PodDisruptionBudgets.
- **CI/CD (gap):** define pipelines — Gradle `check` + Testcontainers + web `build`/`lint`, image build/scan/push,
  environment promotion, DB migration gating (Flyway forward-only).
- **AWS target (gap):** EKS for services; **MSK** (Kafka), **OpenSearch Service**, **RDS Postgres**,
  **ElastiCache/Valkey**, **ECR**, secrets via **Secrets Manager** or self-managed HA Vault (keep the
  "pods get secrets from Vault, not manifests" rule). Managed OTel/X-Ray or self-hosted collector.
- **Serverless-compatible patterns (where they fit):** keep core stateful Spring services on EKS; use serverless
  only for **event-driven glue** (e.g. media post-processing triggered by `media.asset.ready.v1`, notification
  fan-out) where cold-start and statelessness are acceptable. Do not force core domains into Lambda.
- **gRPC ports** `90xx`, HTTP `81xx` (per `service-communication.md`); ClusterIP internal, LoadBalancer only for
  web/admin frontends.

A dedicated `Docs/Development/devops-deployment.md` should be extracted from this section.

---

## 12. Open architectural questions

1. **Capability naming:** standardize on `CUSTOMER` (real) vs `STANDARD` (mock)? (Recommend `CUSTOMER`; fix mock
   contract first.)
2. **gRPC rollout timing:** introduce the first `.proto` now (proactively) or only when marketplace needs the
   synchronous profile-capability read? (Recommend: when the first real reader lands.)
3. **Cassandra fit:** is there a write-heavy, high-fan-out dataset (message timelines, notification feeds, feed
   materialization) that justifies Cassandra's operational cost, or does Postgres (+ partitioning/read models)
   suffice for now? (Recommend: defer; revisit per-domain when volume is proven.)
4. **Asymmetric JWT migration:** when to schedule the HMAC→RS256/JWK cutover, and the dual-validation window?
5. **Serverless scope:** which glue workloads (if any) go serverless on AWS vs staying on EKS?
6. **Mobile BFF:** reuse `web-bff` for mobile, or stand up a dedicated `mobile-bff` for mobile-specific
   aggregation/payload shaping?
7. **Search backing:** confirm `search-service` is built on the already-present OpenSearch (vs a separate engine).
8. **Provider vs Business capability coexistence:** can one account hold both active-switchable (profile-service
   already says business is not a separate account) — confirm the UX/domain rule end to end.

---

## 13. Recommended first backend refactor steps

Small, independently shippable PRs, identity test-suite green at each step:

1. **Kernels-by-default:** update the `viaverse.java-spring-service` convention plugin to apply `web-kernel` +
   `messaging-kernel` + `observability`, so the 5 stub services start conformant.
2. **Identity consolidation:** remove identity's local cross-cutting duplicates in favor of `packages/`
   (envelope, base entity, global error handler, observed-action); one PR per concern.
3. **Docs cleanup (post-approval):** merge `identity-service/coding-standards.md` into `engineering-standards.md`;
   archive `identity-service/plan.md` + `phase-2-plan.md`; reduce the two mock-BFF prototype docs to pointers
   into `Docs/Product/`.
4. **Contract parity fix:** reconcile `STANDARD` → `CUSTOMER` in `mock-web-bff` + `mockAppClient.ts` (and migrate
   the UX-plan references).
5. **Events rename:** apply the `engineering-standards.md` "Messaging → Events" + add the "RPC (gRPC)" subsection
   (doc-only, then code when gRPC lands).
6. **Security epic kickoff:** design the asymmetric-JWT + JWK rotation migration (feature-flagged dual validation)
   and the HMAC request-signing scheme for internal calls — as a dedicated, reviewed epic (§8), not an ad-hoc
   change.
7. **Stub-service skeletons:** for messaging/notification/payment/search/ads-monetization, land conformant empty
   skeletons (kernels + outbox + observability + health) before any domain logic.

---

## Verification (of this planning pass)

- This document exists under `Docs/Architecture/` and is linked from the UX roadmap.
- **No files deleted or rewritten**; the audit (§1) lists proposed changes for separate approval.
- All recommendations are grounded in the actual repo (Java/Spring, `packages/`, infra, stub services) and the
  existing canonical docs (`engineering-standards.md`, `service-communication.md`, `observability.md`).
- Cross-links resolve; `git status` shows only additions under `Docs/`.
