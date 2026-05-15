# Coding Standards — Viaverse Backend Services

## Naming

| Type | Convention | Example |
|---|---|---|
| Domain model | No suffix | `AuthLoginFlow`, `Account` |
| JPA entity | `JpaEntity` | `AuthLoginFlowJpaEntity` |
| Spring Data repo | `JpaRepository` | `AuthLoginFlowJpaRepository` |
| JPA adapter | `JpaAdapter` | `AuthLoginFlowJpaAdapter` |
| Valkey adapter | `ValkeyAdapter` | `RateLimitValkeyAdapter` |
| Kafka publisher | `KafkaPublisher` | `AccountKafkaPublisher` |
| Kafka event | `V{n}KafkaEvent` | `AccountCreatedV1KafkaEvent` |
| Inbound port | `UseCase` (interface) | `StartAuthUseCase` |
| Use case impl | `UseCaseImpl` | `StartAuthUseCaseImpl` |
| Outbound port | `Repository` / `Port` / `Store` | `OtpDeliveryPort`, `RateLimitPort` |
| **Enum** | **`XxxEnum`** | `AccountStatusEnum`, `IdentityErrorEnum` |
| Request DTO | `RequestDto` | `StartAuthRequestDto` |
| Response DTO | `ResponseDto` | `AuthResponseDto` |
| MapStruct mapper | `Mapper` | `AccountJpaMapper`, `AuthDtoMapper` |
| Spring config | `Configuration` | `ValkeyConfiguration` |
| Properties | `Properties` | `AuthProperties` |

---

## Layer Rules

- Domain: zero Spring/JPA imports
- Application use cases: no `@Transactional`, no JPA imports, no `Instant.now()` — inject `Clock`
- `@Transactional` → JPA adapters only
- No `Logger`, `ActionLogContext.put()`, or `auditLogger.*` in domain or application code
- No `try-catch` in use cases — propagate to `GlobalExceptionHandler`

---

## Use Spring — Don't Reinvent

| Concern | Use Spring's built-in | Do NOT write |
|---|---|---|
| Method authorization | `@PreAuthorize("hasRole('ADMIN')")` | Custom `RequiresRole` + aspect |
| Caching | `@Cacheable` / `@CacheEvict` (Spring Cache + Valkey) | Custom caching aspect |
| Entity timestamps | `@EnableJpaAuditing` + `@CreatedDate` / `@LastModifiedDate` on `BaseJpaEntity` | Manual `createdAt = now` in constructors |
| JWT validation | Spring Security OAuth2 Resource Server (`JwtDecoder`) | Custom JWT parsing |
| Bean validation | `@Valid`, `@NotBlank`, `@Pattern` | Custom validation aspect |

`BaseJpaEntity` (`@MappedSuperclass` + `@EntityListeners(AuditingEntityListener.class)`) — all JPA entities extend it.

---

## AOP — What We Do Write

Only where Spring has no equivalent:

| Concern | Annotation | Aspect | Note |
|---|---|---|---|
| Structured logging | `@ObservedAction` + `@LogParam` | `ObservedActionAspect` | Reads `@LogParam` from params via reflection |
| Audit trail | `@AuditEvent` | `AuditEventAspect` | after-returning; subject from `AuditableResult` |

```java
@ObservedAction("auth.register")
@AuditEvent(IdentityAuditEventEnum.ACCOUNT_CREATED)
public AuthResult complete(@LogParam("auth.identifier") String identifier, ...) {
    // no logging, no auditing here
}
```

Return types implement `AuditableResult { UUID auditSubjectId(); }`.

**Security-alert path** (e.g. token reuse): throw `RefreshTokenReuseDetectedException` (internal) →
`AuditEventAspect` catches → records `REFRESH_TOKEN_REUSED` → rethrows as `IdentityErrorEnum.INVALID_REFRESH_TOKEN`.

`IdentityAuditEvents.java` and `IdentityErrors.java` are **deleted**.

---

## Error Enum

Single source of truth — code + HTTP status + type + message in one place:

```java
public enum IdentityErrorEnum {
    INVALID_OTP         ("IDENTITY_1010", HttpStatus.UNPROCESSABLE_ENTITY, ErrorTypeEnum.AUTHENTICATION, "The provided OTP is incorrect."),
    OTP_EXPIRED         ("IDENTITY_1011", HttpStatus.GONE,                 ErrorTypeEnum.AUTHENTICATION, "OTP has expired."),
    RATE_LIMIT_EXCEEDED ("IDENTITY_5001", HttpStatus.TOO_MANY_REQUESTS,    ErrorTypeEnum.RATE_LIMIT,     "Too many attempts. Try again later."),
    // ...all errors here
    ;
    // code, status, type, message fields + toException() / toException(fieldErrors)
}

public enum ErrorTypeEnum { AUTHENTICATION, AUTHORIZATION, VALIDATION, RATE_LIMIT, TECHNICAL, CONFIGURATION }
```

Usage: `throw IdentityErrorEnum.INVALID_OTP.toException();`

`GlobalExceptionHandler` reads `error.getCode()`, `error.getType()`, `error.getMessage()` from the exception.

> i18n: replace `message` with a message key, resolve via `MessageSource` at response time.

---

## Generic API Response

```java
public record ApiResponse<T>(String requestId, Instant timestamp, T data) {}
```

All endpoints return `ApiResponse<T>` except 204. Errors use RFC 7807 `ProblemDetail` + `identityCode` extension.

---

## Clock

```java
@Bean Clock clock() { return Clock.systemUTC(); }
// In use case: Instant now = Instant.now(clock);
```

---

## Kafka Events

```java
public record AccountCreatedV1KafkaEvent(
    UUID eventId, Instant occurredAt, String version,  // "v1"
    UUID accountId, ...
) implements ViaverseEvent {}
```

Kafka key = `accountId.toString()`. Spring Cloud Stream `StreamBridge` — not Kafka API directly.

---

## build.gradle.kts additions (identity-service)

```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-redis")
implementation("org.springframework.cloud:spring-cloud-stream")
implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
implementation("org.mapstruct:mapstruct:1.6.3")
annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
testImplementation("org.testcontainers:postgresql")
testImplementation("org.testcontainers:kafka")
testImplementation("org.testcontainers:testcontainers")
```
