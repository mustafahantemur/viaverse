# Coding Standards — Viaverse Backend Services

## Naming

| Type | Convention | Example |
|---|---|---|
| Domain model | No suffix | `AuthLoginFlow`, `Account` |
| JPA entity | `JpaEntity` | `AuthLoginFlowJpaEntity` |
| Spring Data repo | `JpaRepository` | `AuthLoginFlowJpaRepository` |
| JPA adapter | `JpaAdapter` | `AuthLoginFlowJpaAdapter` |
| Valkey adapter | `ValkeyAdapter` | `RateLimitValkeyAdapter` |
| Inbound port | `UseCase` (interface) | `StartAuthUseCase` |
| Use case impl | `UseCaseImpl` | `StartAuthUseCaseImpl` |
| Outbound port | `Repository` / `Port` / `Store` | `OtpDeliveryPort`, `RateLimitPort` |
| **Enum** | **`XxxEnum`** | `AccountStatusEnum`, `LoginFlowStatusEnum` |
| Request DTO | `Request` | `StartAuthRequest`, `SocialSignInRequest` |
| Response DTO | `Response` | `AuthResponse`, `AuthCompletionResponse` |
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
| Entity timestamps | Prefer `@EnableJpaAuditing` + `@CreatedDate` / `@LastModifiedDate` on `BaseJpaEntity` for new entities | Ad hoc timestamp writes spread across use cases |
| JWT validation | Spring Security OAuth2 Resource Server (`JwtDecoder`) | Custom JWT parsing |
| Bean validation | `@Valid`, `@NotBlank`, `@Pattern` | Custom validation aspect |

`BaseJpaEntity` is available for shared auditing concerns. Existing identity persistence entities still keep explicit timestamp fields until that migration is completed deliberately.

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

Return types implement `AuditableResult { UUID accountId(); }`.

**Security-alert path** (e.g. token reuse): throw `RefreshTokenReuseDetectedException` (internal) →
`RefreshTokenReuseAspect` records the typed audit event and rethrows the canonical identity exception.

`IdentityAuditEvents.java` is removed. `IdentityErrors.java` remains the centralized helper for public identity errors.

---

## Errors

- Use shared `AppErrorCode` values for stable machine-readable codes.
- Use `IdentityErrors` to construct identity-specific public exceptions in one place.
- `GlobalExceptionHandler` maps those exceptions to RFC 7807 responses with `identityCode`.
- Keep field validation structured; do not scatter user-facing literals through use cases.

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

## build.gradle.kts additions (identity-service)

```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-redis")
implementation(libs.spring.cloud.stream)
implementation(libs.spring.cloud.stream.binder.kafka)
implementation("org.mapstruct:mapstruct:1.6.3")
annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
testImplementation("org.testcontainers:testcontainers:2.0.4")
testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.4")
testImplementation("org.testcontainers:testcontainers-postgresql:2.0.4")
testImplementation("org.testcontainers:testcontainers-kafka:2.0.4")
```
