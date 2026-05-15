# Identity Service — Implementation Plan

`[~X]` = parallelizable with other tasks sharing the same letter in that phase.  
Phase gates: all tasks in a phase complete before the next phase starts.  
Each task is scoped to be completable in a single Claude session.

---

## Phase 0 — Foundation (Sequential, must finish first)

- [ ] `build.gradle.kts` — add Valkey, Spring Cloud Stream + Kafka, MapStruct, OpenTelemetry, Testcontainers (see CODING_STANDARDS.md)
- [ ] Rename all existing enums to `XxxEnum` (e.g. `AccountStatus` → `AccountStatusEnum`) — project-wide rename, update all references
- [ ] Create `IdentityErrorEnum` + `ErrorTypeEnum`; rewrite `IdentityException` to wrap enum; delete `IdentityErrors.java`; update `GlobalExceptionHandler`
- [ ] Create target package skeleton (empty packages matching ARCHITECTURE.md tree)
- [ ] Migration V5: drop `auth_otp_challenge` table, drop `auth_rate_limit_bucket` table, drop `registration_token_hash` + `registration_expires_at` from `auth_login_flow`, add device columns to `auth_session`
- [ ] Add `Clock` bean to `AuthConfiguration`; update `application.yml` with Valkey + Kafka connection config

---

## Phase 1 — Domain Model Extraction [parallel]

- [ ] `[~A]` Extract `AuthLoginFlow`, `OtpChallenge`, `RefreshToken`, `IdentityIdentifier` domain models from JPA entities — pure Java records/classes, no annotations, business state transition methods move here
- [ ] `[~B]` Extract `Account` domain model from `IdentityAccountJpaEntity`
- [ ] `[~C]` Define all outbound port interfaces in `auth.application.port.out.*` (see ARCHITECTURE.md list)
- [ ] `[~C]` Move `OtpDeliveryPort` to `auth.application.port.out`
- [ ] `[~C]` Define all inbound port interfaces in `auth.application.port.in.*`
- [ ] `[~D]` Strip JPA entities to pure data holders — remove all business methods (`markOtpVerified`, `recordFailure`, `verify`, `rotate`, `revoke`, `expire`, etc.)
- [ ] `[~D]` Define `ConsentRecordRepository` outbound port in `consent.application.port.out`

---

## Phase 2 — Infrastructure Adapters [parallel]

- [ ] `[~A]` MapStruct mappers + JPA adapters: `account`, `auth_login_flow`, `auth_session`, `refresh_token`, `identity_identifier`, `consent_record`
- [ ] `[~B]` Valkey adapters: `OtpValkeyAdapter`, `RegistrationTokenValkeyAdapter`, `RateLimitValkeyAdapter` (atomic INCR+EXPIRE via Lua script), `SessionCacheValkeyAdapter`
- [ ] `[~C]` Kafka publisher adapters: `AccountKafkaPublisher` + `SessionKafkaPublisher` + define all event records (`AccountCreatedV1KafkaEvent`, etc.)
- [ ] `[~D]` `ValkeyConfiguration`, `KafkaConfiguration`, `ForwardedHeaderFilterConfiguration` beans

---

## Phase 3 — Application Layer Rewrite [parallel]

- [ ] `[~A]` Rewrite `StartAuthUseCaseImpl`, `VerifyOtpUseCaseImpl` — ports only, `Clock` injected, no JPA imports
- [ ] `[~A]` Rewrite `CompleteRegistrationUseCaseImpl`, `RefreshTokenUseCaseImpl`, `LogoutUseCaseImpl`
- [ ] `[~A]` New: `ListSessionsUseCaseImpl`, `RevokeSessionUseCaseImpl`
- [ ] `[~B]` Rewrite application services (`OtpChallengeService`, `AuthSessionService`, `RegistrationTokenService`, `RefreshTokenRotationService`, `AuthAbuseProtectionService`) — no JPA imports
- [ ] `[~C]` Implement `@LogParam` annotation + update `ObservedActionAspect` to extract annotated params — delete all `ActionLogContext.put(...)` from use cases
- [ ] `[~C]` Implement `@AuditEvent` annotation + `AuditEventAspect` (after-returning) + `AuditableResult` interface — delete all `IdentityAuditEvents.*` / `auditLogger.*` calls from use cases; remove `AuditLogger` from use case constructors
- [ ] `[~C]` Implement `RefreshTokenReuseDetectedException` (internal); update `RefreshTokenRotationService` to throw it instead of logging manually — aspect records audit and rethrows as `INVALID_REFRESH_TOKEN`
- [ ] `[~C]` Add `@PreAuthorize` annotations (Spring Security native) for admin endpoints — no custom authorization aspect needed
- [ ] `[~D]` Fix rate limit first-attempt bug (count attempt on bucket creation, not after)
- [ ] `[~D]` Fix marketing consent version (inject from `AuthProperties`, not hardcoded `"v1"`)
- [ ] `[~D]` Add `@Transactional` to JPA adapters; remove from use cases
- [ ] `[~D]` Create `BaseJpaEntity` (`@MappedSuperclass` + `@EnableJpaAuditing`) — remove manual `createdAt`/`updatedAt` from all entity constructors

---

## Phase 4 — API Layer [parallel]

- [ ] `[~A]` `ApiResponse<T>` wrapper + update all controller methods + MapStruct DTO mappers (`AuthDtoMapper`, `SessionDtoMapper`)
- [ ] `[~B]` `SessionController` — `GET /api/v1/me/sessions`, `DELETE /api/v1/me/sessions/{sessionId}`, `DELETE /api/v1/me/sessions`
- [ ] `[~C]` Remove `clientIp()` from `AuthController`; rely on `ForwardedHeaderFilter`
- [ ] `[~C]` Add rate limiting to `/refresh` and `/logout` endpoints
- [ ] `[~C]` Validate logout: reject (400) if both JWT principal and refresh token are absent
- [ ] `[~D]` Update `SecurityConfiguration` to permit session endpoints (authenticated) and apply correct rules

---

## Phase 5 — Observability [parallel]

- [ ] `[~A]` `OpenTelemetryConfiguration` + auto-instrumentation wiring
- [ ] `[~B]` `docker-compose.yml` — PostgreSQL, Valkey, Kafka (KRaft mode), Fluent Bit, OpenSearch
- [ ] `[~C]` `fluent-bit.conf` — tail ECS logs → OpenSearch; parse multiline if needed
- [ ] `[~D]` Update integration tests to use Testcontainers for Valkey and Kafka alongside PostgreSQL

---

## Phase 6 — Missing Features [parallel]

- [ ] `[~A]` Google Sign-In: `SocialAuthPort` + `GoogleOidcAdapter` (verify ID token, link/create account)
- [ ] `[~B]` Apple Sign-In: `AppleOidcAdapter`
- [ ] `[~C]` NetGSM SMS: `NetgsmSmsOtpDeliveryAdapter` implements `OtpDeliveryPort`
- [ ] `[~D]` SMTP email OTP: `SmtpEmailOtpDeliveryAdapter` implements `OtpDeliveryPort`
- [ ] `[~E]` Admin registration: role claim (`roles: ["ADMIN"]`) in JWT, admin-only registration endpoint with pre-authorized invite token

---

## Known Bugs to Fix (tie into whichever phase touches that code)

| Bug | Phase |
|---|---|
| Rate limit: first attempt to any bucket is uncounted (free pass) | 3D |
| IP spoofing via `X-Forwarded-For` in `AuthController.clientIp()` | 4C |
| Logout with null JWT + null refresh token silently succeeds (204) | 4C |
| `normalizeOptional()` duplicated in two classes | 3B |
| Marketing consent version hardcoded `"v1"` | 3D |
| `AppRunner` config validation runs too late (post-startup) → move to `@PostConstruct` | 0 |
