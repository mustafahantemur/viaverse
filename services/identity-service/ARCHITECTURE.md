# Identity Service — Architecture

## Key Decisions

| Decision | Choice |
|---|---|
| Profile scope | Separate `profile-service` — identity owns auth/sessions/consents only |
| Session state | PostgreSQL (device audit) + Valkey (hot cache per request) |
| Rate limiting | Valkey INCR + TTL — no PostgreSQL locks |
| OTP & reg token | Valkey with TTL — removes 2 DB tables |
| Messaging | Spring Cloud Stream + Kafka (broker-agnostic) |
| Mapping | MapStruct |
| Logging pipeline | ECS → Fluent Bit → OpenSearch |
| Tracing | OpenTelemetry auto-instrumentation |
| IP extraction | `ForwardedHeaderFilter` + trusted proxy config — not in controller |

---

## Layer Rules

Strict hexagonal layering. Web/REST is a driving (inbound) adapter; persistence/cache/messaging/OTP are driven (outbound) adapters. There is **no top-level `api/` slice** — controllers live under `infrastructure/adapter/in/web/`.

| Layer | Package | Allowed imports | Forbidden |
|---|---|---|---|
| Domain | `*.domain.*` | JDK only | Spring, JPA, anything external |
| Application | `*.application.*` | Domain + own ports | `*JpaEntity`, `*JpaRepository`, any adapter |
| Inbound adapter | `*.infrastructure.adapter.in.*` | Application ports (`port.in`) + DTOs + Spring web | Domain models in DTOs (use mappers), outbound adapter internals |
| Outbound adapter | `*.infrastructure.adapter.out.*` | Application ports (`port.out`) + Domain models | Other outbound adapters, controllers |
| Infrastructure (tech) | `*.infrastructure.security.*` etc. | Cross-cutting tech primitives only | Business logic |

---

## Package Structure

```
app.viaverse.identity/

  account/
    domain/         Account.java, AccountStatusEnum.java, AccountView.java
    application/
      port/in/      GetCurrentAccountUseCase.java
      port/out/     AccountRepository.java, AccountEventPublisher.java
      usecase/      GetCurrentAccountUseCaseImpl.java
    infrastructure/
      adapter/
        in/web/
          controller/   MeController.java
          mapper/       AccountDtoMapper.java
        out/
          persistence/
            entity/     IdentityAccountJpaEntity.java  (extends BaseJpaEntity)
            mapper/     AccountJpaMapper.java
            adapter/    AccountJpaAdapter.java
            repository/ IdentityAccountJpaRepository.java
          messaging/
            adapter/    AccountKafkaPublisher.java
            event/      AccountCreatedV1KafkaEvent.java, AccountStatusChangedV1KafkaEvent.java

  auth/
    domain/
      model/        AuthLoginFlow.java, OtpChallenge.java, AuthSession.java,
                    RefreshToken.java, IdentityIdentifier.java
      enums/        AuthNextStepEnum.java, IdentifierTypeEnum.java, LoginFlowStatusEnum.java,
                    OtpChallengeStatusEnum.java, RefreshTokenStatusEnum.java, SessionStatusEnum.java,
                    RateLimitScopeEnum.java, OtpDeliveryProviderEnum.java, SocialAuthProviderEnum.java
      value/        NormalizedIdentifier.java, OtpDeliveryRequest.java
      policy/       RegistrationPolicy.java
    application/
      port/in/      StartAuthUseCase.java, VerifyOtpUseCase.java,
                    CompleteRegistrationUseCase.java, RefreshTokenUseCase.java,
                    LogoutUseCase.java, ListSessionsUseCase.java, RevokeSessionUseCase.java
      port/out/     AuthLoginFlowRepository.java, OtpChallengeStore.java,
                    AuthSessionRepository.java, RefreshTokenRepository.java,
                    IdentifierRepository.java, OtpDeliveryPort.java,
                    RegistrationTokenStore.java, RateLimitPort.java, SessionEventPublisher.java
      usecase/      StartAuthUseCaseImpl.java … (one per port/in interface)
      service/      OtpChallengeService.java, AuthSessionService.java,
                    RegistrationTokenService.java, RefreshTokenRotationService.java,
                    AuthAbuseProtectionService.java
    infrastructure/
      adapter/
        in/web/
          controller/   AuthController.java, SessionController.java
          dto/request/  (one per endpoint)
          dto/response/ (one per endpoint; sealed VerifyOtpResponse, SessionView, …)
          mapper/       AuthDtoMapper.java, SessionDtoMapper.java
        out/
          persistence/
            entity/     AuthLoginFlowJpaEntity.java, AuthSessionJpaEntity.java,
                        AuthRefreshTokenJpaEntity.java, IdentityIdentifierJpaEntity.java
            mapper/     (MapStruct — one per entity)
            adapter/    (one per outbound port)
            repository/ (Spring Data — not exposed outside infrastructure)
          cache/        OtpValkeyAdapter.java, RegistrationTokenValkeyAdapter.java,
                        RateLimitValkeyAdapter.java, SessionCacheValkeyAdapter.java,
                        ValkeyKeyScheme.java
          otp/          DebugOtpDeliveryAdapter.java, NetgsmSmsOtpDeliveryAdapter.java,
                        SmtpEmailOtpDeliveryAdapter.java, SmsOtpDeliveryAdapter.java
          social/       GoogleOidcAdapter.java, AppleOidcAdapter.java  (SocialAuthPort lives in application/port/out)
          messaging/
            adapter/    SessionKafkaPublisher.java
            event/      SessionRevokedV1KafkaEvent.java
      security/         JwtAccessTokenService.java, TokenHasher.java, SecureTokenGenerator.java,
                        JwtPrincipal.java, JwtPrincipalResolver.java, IdentityJwtValidator.java,
                        IdentityAuthenticationEntryPoint.java
                        (cross-cutting tech primitives — not adapters)

  consent/
    domain/         ConsentTypeEnum.java, ConsentCategoryEnum.java, ConsentInput.java
    application/
      port/out/     ConsentRecordRepository.java
      ConsentPolicy.java
    infrastructure/adapter/out/persistence/ (entity, mapper, adapter, repository)

  shared/
    audit/          IdentityAuditEventEnum.java, AuditLogJpaEntity.java,
                    AuditLogJpaRepository.java, AuditLogAdapter.java
    error/
      IdentityErrorEnum.java          ← single source of truth for all errors
      ErrorTypeEnum.java
      IdentityException.java
      RateLimitExceededException.java
    normalization/  IdentifierNormalizer.java
    persistence/    BaseJpaEntity.java  ← @MappedSuperclass with @CreatedDate / @LastModifiedDate
    aspect/
      ObservedAction.java, LogParam.java, ObservedActionAspect.java
      AuditEvent.java, AuditEventAspect.java, AuditableResult.java
      RefreshTokenReuseDetectedException.java

  config/
    AuthProperties.java, SecurityConfiguration.java, AuthConfiguration.java,
    ValkeyConfiguration.java, KafkaConfiguration.java,
    GlobalExceptionHandler.java, OpenTelemetryConfiguration.java,
    ForwardedHeaderFilterConfiguration.java
```

---

## Valkey Key Schema

| Key | Value | TTL | Removes |
|---|---|---|---|
| `otp:{flowId}` | `{otpHash, attempts, maxAttempts, status}` | OTP TTL | `auth_otp_challenge` table |
| `reg:{tokenHash}` | `{flowId}` | Registration TTL | `registration_token_hash` col |
| `rl:{scope}:{keyHash}` | integer | Window seconds | `auth_rate_limit_bucket` table |
| `session:{sessionId}` | `{accountId, status, expiresAt}` | Access token TTL | DB hit per request |

---

## Kafka Events

Topic: `viaverse.identity.{aggregate}-events` — key = `accountId`.

| Topic | Event | Trigger |
|---|---|---|
| `viaverse.identity.account-events` | `AccountCreatedV1` | Registration complete |
| `viaverse.identity.account-events` | `AccountStatusChangedV1` | Suspended / reactivated |
| `viaverse.identity.session-events` | `SessionRevokedV1` | Session revoked |
| `viaverse.identity.consent-events` | `ConsentUpdatedV1` | Consent recorded |

---

## Session Device Tracking

New columns on `auth_session`: `device_id VARCHAR(255)`, `device_name VARCHAR(100)`,
`platform VARCHAR(20)` (WEB/MOBILE/DESKTOP/UNKNOWN), `last_ip VARCHAR(45)`.

```
GET    /api/v1/me/sessions              ← list active sessions
DELETE /api/v1/me/sessions/{sessionId} ← revoke one
DELETE /api/v1/me/sessions             ← revoke all except current
```

---

## JWT Contract (for other services)

```
iss: "viaverse-identity"   sub: "{accountId}"  sid: "{sessionId}"   iat/exp: Unix epoch
```

Other services validate via shared HMAC secret. Session invalidation propagates via `SessionRevokedV1`.

---

## Profile Service Boundary

| identity-service | profile-service (future) |
|---|---|
| Account, identifiers, sessions, consents, JWT | Avatar, bio, preferences, categories, ratings |

profile-service creates its record on `AccountCreatedV1`, linked by `accountId`.
