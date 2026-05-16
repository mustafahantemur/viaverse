# Identity Service Phase 2 Plan

Branch: `identity-refactor-pahase-2.0`

## Why this phase exists

The first refactor moved `identity-service` a long way toward a production-shaped
hexagonal service. This phase is for finishing the transition cleanly, removing
old leftovers, and making the codebase easier to trust before feature work such
as real OTP delivery or social login begins.

## Current state

### Already in good shape

- `auth` is split into domain, application ports/use cases, and infrastructure adapters.
- Auth use cases are focused and mostly orchestration-only.
- Domain models own state transitions instead of JPA entities.
- JWT issuing and validation use Spring Security / Nimbus instead of hand-rolled parsing.
- Rate limiting is separated behind `AuthAbuseProtectionService` and `RateLimitPort`.
- Structured action logging exists through `@ObservedAction`.
- Session management endpoints and session revocation flow exist.
- OTP, refresh token rotation, debug OTP behavior, and consent validation have integration coverage.

### Still incomplete or inconsistent

- Client IP trust is not explicit enough yet. `ForwardedHeaderFilter` exists, but trusted proxy handling must be made explicit so forwarded headers cannot be blindly trusted.
- Documentation mixes the current implementation with the intended end-state.
- Enum naming is inconsistent with the new standards document.
- `LocalTestUserSeeder` still reaches directly into JPA repositories/entities from the application layer.
- Some migration-era leftovers existed at phase start:
  - `IdentityAuditEvents`
  - partially introduced audit AOP that was not yet wired across the main flows
- `CompleteRegistrationUseCaseImpl` still owns several adjacent responsibilities.
- Event publication and session cache ports now exist, but still need stronger delivery guarantees later
  (for example an outbox pattern rather than direct publish).

## Phase 2 goals

1. Make client IP resolution trustworthy and explicit.
2. Align enum naming with the documented standard.
3. Remove dead code and old compatibility helpers that no longer serve a purpose.
4. Finish half-completed architecture moves so the package tree tells the truth.
5. Reconcile documentation with the code that actually exists.
6. Keep tests green while changing structure, not external behavior.

## Working order

### Step 1 - Client IP hardening

- Add an explicit trusted-proxy policy.
- Only honor forwarded headers when the direct peer is trusted.
- Keep controllers free of ad hoc header parsing.
- Add tests that distinguish trusted-proxy and direct-client behavior.

### Step 2 - Naming and consistency

- Rename enums to the chosen project convention.
- Update imports, persistence mappings, tests, and docs consistently.
- Remove naming drift between docs and code.

### Step 3 - Finish the hexagonal move

- Move `LocalTestUserSeeder` out of the application boundary or refactor it to ports.
- Complete account/consent outbound adapters if they are staying in scope.
- Replace partial audit wiring with one coherent model.
- Keep use cases free of infrastructure imports.

### Step 4 - Delete leftovers

- Remove unused helpers, duplicate transition artifacts, and dead adapters that are no longer part of the real design.
- Prefer deletion over keeping speculative code that obscures the active architecture.

### Step 5 - Documentation cleanup

- Keep `ARCHITECTURE.md` as the current architecture, not a wish list.
- Keep deferred future work in `PLAN.md`.
- Use this file as the live phase checklist until the phase closes.

## Deferred on purpose

- Real SMS delivery / Netgsm integration
- Social login implementation
- Mobile UI work
- Other service domains outside `identity-service`

## Progress checklist

- [x] Client IP trust boundary is explicit and tested
- [x] Enum naming is consistent
- [x] Application layer no longer imports JPA infrastructure
- [x] Stale compatibility code removed or promoted into real flows
- [x] Audit/logging path is coherent
- [x] Account/session event publishers are wired
- [x] Session cache is used through a port with invalidation on revoke/expiry
- [x] Docs describe the code that exists
- [ ] Identity-service verification suite passes
