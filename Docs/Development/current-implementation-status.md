# Current implementation status

Status date: **2026-05-18**

## What is implemented now

The repository is no longer only a shell. The active product slice is:

- identity/auth foundation
- profile-service through **Phase 3**
- shared backend extractions for reusable cross-service infrastructure
- first usable web and admin surfaces for the profile/business flow

### Backend foundation

Completed:

- Gradle multi-module structure and Spring service conventions
- shared packages:
  - `web-kernel`
  - `messaging-kernel`
  - `security-kernel`
  - `shared-kernel`
  - `observability`
- local Docker infrastructure
- Flyway migration flow
- outbox/eventing baseline
- local observability stack

### identity-service

Implemented:

- auth bootstrap, OTP/password login, refresh/logout
- account registration and consent handling
- admin invitation support
- capability-term discovery for downstream onboarding UIs
- internal read APIs used by profile-service

### profile-service

Implemented through Phase 3:

- profile provisioning from `account.created.v1`
- self profile read/write
- public profile reads with visibility and block rules
- preferences
- blocks
- customer / individual-provider capabilities
- active-mode switching
- business draft, submission, approval, rejection, and public preview
- event emission through the outbox

Important domain decision now enforced:

- A business profile is **not** a separate account.
- `CUSTOMER` stays enabled even after business approval.
- The same person can switch from `BUSINESS` back to `CUSTOMER` and continue normal-user flows.

### Client follow-through already present

- Web:
  - authenticated `/app`
  - `/app/profile`
  - profile editing
  - provider enablement
  - business draft / submission
  - active-mode switching
- Admin web:
  - first business approval queue
- Mobile:
  - home shell reads the real active mode instead of showing a placeholder

## What is still intentionally incomplete

### Next profile-service boundary

Phase 4 is still open:

1. trust read-through
2. migration of display-field ownership from identity-service to profile-service
3. identity-service slim-down after the mirror has burned in

That work depends on the trust service and on a deliberate migration plan, so it should not be faked inside Phase 3.

### Trust, verification, and moderation

The product direction is documented, but the automation is not implemented yet:

- automatic business verification
- automatic identity / liveness verification
- automatic moderation for future posts, listings, jobs, and events

The current architecture direction is captured in `Docs/Architecture/trust-and-moderation.md`.

### Other services

These services are still mostly technical shells:

- marketplace-service
- payment-service
- messaging-service
- media-service
- notification-service
- search-service
- trust-gamification-service
- ads-monetization-service

They compile, migrate, and expose health, but they do not yet contain their future business feature sets.

### UI gaps

Still missing or intentionally thin:

- full mobile profile/business management screens
- richer admin moderation tooling
- real marketplace/feed/product flows around the new profile capability model

## Local-development status after this pass

The local stack is materially healthier than before:

- `profile-service` databases are now created by `start-core-infra.ps1`
- VS Code task wiring no longer reuses identity-service port cleanup as a dependency for profile/web-bff startup
- `identity-service`, `profile-service`, and `web-bff` VS Code configs now launch directly instead of depending on suspended attach targets
- `debug profile-service` exists in `.vscode/launch.json`
- README and the initial start guide now describe the real startup order and health endpoints

## Known sharp edges

- `identity-service` exposes `/actuator/health`, not `/health`.
- After shared-module moves, VS Code may show stale red imports even when Gradle is green. Clean the Java language-server workspace before treating that as a source-code error.
- `debug backend: all services` is useful for topology checks, but most day-to-day profile work only needs identity + profile + the two BFF/client surfaces.

## Recommended next engineering step

Do **not** jump straight into more business domains yet.

The next clean boundary is:

1. keep the just-finished Phase 3 profile flow stable,
2. design the trust-gamification read model and moderation contracts,
3. then begin Phase 4 with a real event contract instead of ad-hoc placeholders.
