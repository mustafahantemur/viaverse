# Identity Service — Implementation Plan

`[~X]` = parallelizable with other tasks sharing the same letter in that phase.  
Phase gates: all tasks in a phase complete before the next phase starts.  
Each task is scoped to be completable in a single Claude session.

## Current checkpoint

As of this branch checkpoint, the production-shaping work through Phase 5 has
been implemented or deliberately revised during the later hardening pass.
The next active product work is **Phase 6**.

Notable revisions from the original checklist:

- Centralized identity errors now use shared `AppErrorCode` values plus `IdentityErrors`
  instead of introducing a separate `IdentityErrorEnum`.
- Trusted client IP handling uses `ClientIpResolver` plus explicit trusted-proxy config
  instead of relying on a broad `ForwardedHeaderFilter`.
- The shared observability stack uses Fluent Bit for ECS stdout log shipping and
  OpenTelemetry Collector for traces/metrics.
- Event publishing and session cache adapters are now live parts of the architecture,
  not future stubs.

---

## Phase 0 — Foundation (Sequential, must finish first) — **done**

- [x] `build.gradle.kts` — add Valkey, Spring Cloud Stream + Kafka, MapStruct, OpenTelemetry, Testcontainers (see CODING_STANDARDS.md)
- [x] Rename all existing enums to `XxxEnum` (e.g. `AccountStatus` → `AccountStatusEnum`) — project-wide rename, update all references
- [x] ~~Create `IdentityErrorEnum` + `ErrorTypeEnum`~~ — superseded: identity errors are constructed via `IdentityErrors` helpers over the shared `AppErrorCode` enum; no per-service error enum exists. `GlobalExceptionHandler` maps `AppException` subclasses to RFC 7807.
- [x] Create target package skeleton (empty packages matching ARCHITECTURE.md tree)
- [x] Migration V2 (`V2__identity_auth_onboarding.sql`) is the live schema: device columns on `auth_session`, registration-token fields on `auth_login_flow`, etc. The "drop bucket/challenge" V5 plan is obsolete — those tables were never created in the current schema; Valkey is the single source for OTP/rate-limit state.
- [x] Add shared `Clock` bean via `TimeConfiguration`; update `application.yml` with Valkey + Kafka connection config

---

## Phase 1 — Domain Model Extraction — **done**

- [x] `[~A]` Extract `AuthLoginFlow`, `OtpChallenge`, `RefreshToken`, `IdentityIdentifier` domain models from JPA entities — pure Java records/classes, no annotations, business state transition methods move here
- [x] `[~B]` Extract `Account` domain model from `IdentityAccountJpaEntity`
- [x] `[~C]` Define all outbound port interfaces in `auth.application.port.out.*` (see ARCHITECTURE.md list)
- [x] `[~C]` Move `OtpDeliveryPort` to `auth.application.port.out`
- [x] `[~C]` Define all inbound port interfaces in `auth.application.port.in.*`
- [x] `[~D]` Strip JPA entities to pure data holders — remove all business methods (`markOtpVerified`, `recordFailure`, `verify`, `rotate`, `revoke`, `expire`, etc.)
- [x] `[~D]` Define `ConsentRecordRepository` outbound port in `consent.application.port.out`

---

## Phase 2 — Infrastructure Adapters — **done**

- [x] `[~A]` MapStruct mappers + JPA adapters: `account`, `auth_login_flow`, `auth_session`, `refresh_token`, `identity_identifier`, `consent_record`
- [x] `[~B]` Valkey adapters: `OtpValkeyAdapter`, `RegistrationTokenValkeyAdapter`, `RateLimitValkeyAdapter` (atomic INCR+EXPIRE via Lua script), `SessionCacheValkeyAdapter`
- [x] `[~C]` Kafka publisher adapters: `AccountKafkaPublisher` + `SessionKafkaPublisher` + event records (`AccountCreatedV1KafkaEvent`, `AccountStatusChangedV1KafkaEvent`, `SessionRevokedV1KafkaEvent`)
- [x] `[~D]` ~~`ForwardedHeaderFilterConfiguration`~~ → replaced by `ClientIpResolver` + `HttpConfiguration` + `HttpProperties` (explicit trusted-proxy list, not blind header trust). `ValkeyConfiguration` and Kafka binding config live in `application.yml`.

---

## Phase 3 — Application Layer Rewrite — **done**

- [x] `[~A]` Rewrite `StartAuthUseCaseImpl`, `VerifyOtpUseCaseImpl` — ports only, `Clock` injected, no JPA imports
- [x] `[~A]` Rewrite `CompleteRegistrationUseCaseImpl`, `RefreshTokenUseCaseImpl`, `LogoutUseCaseImpl`
- [x] `[~A]` New: `ListSessionsUseCaseImpl`, `RevokeSessionUseCaseImpl`
- [x] `[~B]` Rewrite application services (`OtpChallengeService`, `AuthSessionIssuer`, `RegistrationTokenService`, `RefreshTokenRotationService`, `AuthAbuseProtectionService`) — no JPA imports
- [x] `[~C]` `@LogParam` + `ObservedActionAspect`; all `ActionLogContext.put(...)` removed from use cases
- [x] `[~C]` `@AuditEvent` + `AuditEventAspect` + `AuditableResult`; old `IdentityAuditEvents` helper deleted
- [x] `[~C]` `RefreshTokenReuseDetectedException` + `RefreshTokenReuseAspect` records the audit event and rethrows the canonical identity exception
- [x] `[~C]` `@PreAuthorize` for admin endpoints (Spring Security native)
- [x] `[~D]` Rate-limit first-attempt counting fix
- [x] `[~D]` Marketing consent version sourced from `AuthProperties`, not hardcoded
- [x] `[~D]` `@Transactional` on JPA adapters only
- [x] `[~D]` `BaseJpaEntity` with auditing

---

## Phase 4 — API Layer — **done**

- [x] `[~A]` `ApiResponse<T>` wrapper on every controller; `AuthDtoMapper`, `SessionDtoMapper` for DTO mapping. (Sealed envelope renamed to `AuthCompletionResponse` so it can be reused by social sign-in.)
- [x] `[~B]` `SessionController` — `GET /api/v1/me/sessions`, `DELETE /api/v1/me/sessions/{sessionId}`, `DELETE /api/v1/me/sessions`
- [x] `[~C]` Controllers no longer do ad hoc IP parsing — `ClientIpResolver` resolves trusted-proxy chains
- [x] `[~C]` Rate limiting on `/refresh` and `/logout`
- [x] `[~C]` Logout with no JWT and no refresh token → 400
- [x] `[~D]` `SecurityConfiguration` covers `/api/v1/me/**`

---

## Phase 5 — Observability — **done**

- [x] `[~A]` `OpenTelemetryConfiguration` + Micrometer Tracing → OTLP exporter
- [x] `[~B]` `docker-compose.yml` — PostgreSQL, Valkey, Kafka (KRaft), Jaeger, Kafka UI, OpenSearch
- [x] `[~C]` `fluent-bit.conf` — ECS stdout log shipping into OpenSearch; OpenTelemetry Collector remains trace/metric transport.
- [x] `[~D]` Integration tests use Testcontainers for PostgreSQL + Valkey + Kafka

---

## Phase 6 — Missing Features (active)

**Scaffolded + hardened on `story/identity-refactor` (awaiting real provider creds / step 8 cutover):**

- [x] `[~A]` Google Sign-In: `SocialAuthPort` + `GoogleOidcAdapter` verifies the Google ID token via JWK URI, validates issuer + audience + nonce + subject, links/creates account. Gated by `viaverse.auth.social.google.enabled`.
- [x] `[~B]` Apple Sign-In: `AppleOidcAdapter` extends the same OIDC base. Gated by `viaverse.auth.social.apple.enabled`.
- [x] `[~C]` NetGSM SMS: `NetgsmSmsOtpDeliveryAdapter` selected by `OtpChallengeService` via `OtpDeliveryPort.supports()`. Built on a dedicated `RestClient` (3s connect / 5s read; no auto-instrumentation so credentials in the URL can't leak to spans). `AuthConfiguration.validate` fails fast on incomplete NetGSM config and on a message template missing `%s`.

**Not started:**

- [ ] `[~D]` SMTP email OTP: `SmtpEmailOtpDeliveryAdapter` implements `OtpDeliveryPort` (supports `EMAIL`); slots into the same multi-adapter dispatch.
- [x] `[~E]` Admin invitation flow: invite-token issuance, admin-only registration endpoint, `roles: ["ADMIN"]` claim in JWT, role-aware `@PreAuthorize`.

**Step 8 cutover work for the already-scaffolded items:**

- [ ] Switch NetGSM adapter to the POST endpoint and re-attach observation with a URL-sanitising convention. Wire real credentials via secret store.
- [ ] Wire real Google + Apple OAuth client IDs; verify the end-to-end ID-token flow against staging providers; remove the conditional defaults.

---

## Known Bugs to Fix (status)

| Bug | Status |
|---|---|
| Rate limit: first attempt to any bucket is uncounted (free pass) | **fixed** (Phase 3D, commit `0004ff0`) |
| IP spoofing via `X-Forwarded-For` in `AuthController.clientIp()` | **fixed** (`ClientIpResolver` with trusted-proxy list) |
| Logout with null JWT + null refresh token silently succeeds (204) | **fixed** (`refreshTokenRequired()` 400) |
| `normalizeOptional()` duplicated in two classes | **fixed** (centralised in `IdentifierNormalizer`) |
| Marketing consent version hardcoded `"v1"` | **fixed** (`AuthProperties.Consent.marketingVersion`) |
| `AppRunner` config validation runs too late (post-startup) | **fixed** — `AuthConfiguration` now validates in `@PostConstruct`, before the service starts accepting traffic. |
| Pre-existing: device-fingerprint rate-limit bucket reuses `getIpMaxAttempts()` instead of a dedicated `deviceMaxAttempts` knob | **fixed** — `auth-start.device-window-seconds` and `auth-start.device-max-attempts` now configure the device bucket independently. |
| JWT signing secret has no rotation strategy — single `viaverse.auth.jwt.secret` used indefinitely | **fixed** — `viaverse.auth.jwt.previous-secrets` is accepted during the rotation window through `RotatingJwtDecoder`; new tokens are still issued only with the current secret. |

---

## Production-readiness gate — Step 6 status

- [x] 6.1 Cache strategy + TTL audit
- [x] 6.2 Security headers / CORS / JWT posture
- [x] 6.3 Kafka transactional outbox + producer hardening
- [x] 6.4 OTel/logging hardening: request/correlation filter restored, use-case spans emitted from `@ObservedAction`, trace/span ids present in action logs, OpenSearch template + ISM retention payloads added
- [x] 6.5 Health/readiness review: liveness is process-only, readiness depends on database + Valkey, Kafka remains decoupled through the outbox, and an `outbox` health contributor surfaces backlog/terminal failures for operators
