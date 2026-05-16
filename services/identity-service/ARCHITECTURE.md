# Identity Service — Architecture

## Key Decisions

| Decision | Choice |
|---|---|
| Profile scope | Separate `profile-service` — identity owns auth/sessions/consents only |
| Session state | PostgreSQL |
| Rate limiting | Valkey INCR + TTL — no PostgreSQL locks |
| OTP & reg token | Valkey with TTL |
| Messaging | Spring Cloud Stream / Kafka publishers for account and session events |
| Mapping | MapStruct |
| Logging pipeline | ECS stdout → OpenTelemetry Collector → OpenSearch |
| Tracing | OpenTelemetry auto-instrumentation |
| IP extraction | `ClientIpResolver` + explicit trusted proxy config |

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
          messaging/   AccountKafkaPublisher.java
            event/     AccountCreatedV1KafkaEvent.java, AccountStatusChangedV1KafkaEvent.java
          persistence/
            entity/     IdentityAccountJpaEntity.java
            mapper/     AccountJpaMapper.java
            adapter/    AccountJpaAdapter.java
            repository/ IdentityAccountJpaRepository.java

  auth/
    domain/
      model/        AuthLoginFlow.java, OtpChallenge.java, AuthSession.java,
                    RefreshToken.java, IdentityIdentifier.java
      enums/        AuthNextStepEnum.java, IdentifierTypeEnum.java, LoginFlowStatusEnum.java
                    (OTP_REQUIRED, OTP_VERIFIED, EXTERNAL_VERIFIED, REGISTRATION_REQUIRED,
                     COMPLETED, EXPIRED, FAILED),
                    OtpChallengeStatusEnum.java, RefreshTokenStatusEnum.java, SessionStatusEnum.java,
                    RateLimitScopeEnum.java, OtpDeliveryProviderEnum.java, SmsProviderEnum.java,
                    SocialAuthProviderEnum.java (GOOGLE, APPLE)
      value/        NormalizedIdentifier.java, OtpDeliveryRequest.java, SocialIdentity.java
      policy/       RegistrationPolicy.java
    application/
      port/in/      StartAuthUseCase.java, VerifyOtpUseCase.java, SocialSignInUseCase.java,
                    CompleteRegistrationUseCase.java, RefreshTokenUseCase.java,
                    LogoutUseCase.java, ListSessionsUseCase.java, RevokeSessionUseCase.java
      port/out/     AuthLoginFlowRepository.java, OtpChallengeStore.java,
                    AuthSessionRepository.java, RefreshTokenRepository.java,
                    IdentifierRepository.java, OtpDeliveryPort.java, SocialAuthPort.java,
                    RegistrationTokenStore.java, RateLimitPort.java,
                    SessionCachePort.java, SessionEventPublisher.java
      usecase/      StartAuthUseCaseImpl.java … (one per port/in interface)
      service/      OtpChallengeService.java, AuthSessionIssuer.java,
                    RegistrationTokenService.java, RefreshTokenRotationService.java,
                    AuthAbuseProtectionService.java
    infrastructure/
      adapter/
        in/web/
          controller/   AuthController.java, SessionController.java
          dto/request/  (one per endpoint)
          dto/response/ (one per endpoint; sealed AuthCompletionResponse, SessionView, …)
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
          messaging/    SessionKafkaPublisher.java
            event/      SessionRevokedV1KafkaEvent.java
          otp/          DebugOtpDeliveryAdapter.java, NetgsmSmsOtpDeliveryAdapter.java
                        (selected by OtpChallengeService via OtpDeliveryPort.supports())
          social/       AbstractOidcSocialAuthAdapter.java,
                        GoogleOidcAdapter.java, AppleOidcAdapter.java
                        (each gated by @ConditionalOnProperty on viaverse.auth.social.*.enabled)
          seed/         LocalTestUserSeeder.java
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
    error/          IdentityErrors.java, IdentityException.java,
                    RateLimitExceededException.java, RefreshTokenReuseDetectedException.java
    normalization/  IdentifierNormalizer.java
    persistence/    BaseJpaEntity.java
    security/       ClientIpResolver.java
                    (RFC 7239 Forwarded + legacy X-Forwarded-For parser; trust list from HttpProperties)
    aspect/
      ObservedAction.java, LogParam.java, ObservedActionAspect.java
      AuditEvent.java, AuditEventAspect.java, AuditableResult.java,
      RefreshTokenReuseAspect.java

  config/
    AuthProperties.java, SecurityConfiguration.java, AuthConfiguration.java,
    HttpConfiguration.java, HttpProperties.java, ValkeyConfiguration.java,
    OtpDeliveryConfiguration.java, OpenTelemetryConfiguration.java,
    GlobalExceptionHandler.java
```

---

## Valkey Key Schema

| Key | Value | TTL | Removes |
|---|---|---|---|
| `otp:{flowId}` | `{otpHash, attempts, maxAttempts, status}` | OTP TTL | Active OTP state |
| `reg:{tokenHash}` | `{flowId}` | Registration TTL | Registration token lookup |
| `rl:{scope}:{keyHash}` | integer | Window seconds | Abuse protection counters |
| `session:{sessionId}` | `{accountId, status, expiresAt}` | Session expiry | Session read-through cache |

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

Other services validate via the shared HMAC secret. Session revocation is also published through
`viaverse.identity.session-events` so future consumers can react without coupling to the identity database.

---

## Profile Service Boundary

| identity-service | profile-service (future) |
|---|---|
| Account, identifiers, sessions, consents, JWT | Avatar, bio, preferences, categories, ratings |

Account lifecycle publication exists through `viaverse.identity.account-events`; profile creation remains
deferred until a profile-service consumer is introduced.
