# Current implementation status

Status date: **2026-05-18**

## What is implemented now

The repository is no longer only a shell. The active product slice is:

- identity/auth foundation
- profile-service through the **functional Phase 4 boundary**
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
- `profile.updated.v1` consumption so display fields are mirrored from profile-service after onboarding

### profile-service

Implemented through the functional Phase 4 boundary:

- profile provisioning from `account.created.v1`
- self profile read/write
- public profile reads with visibility and block rules
- preferences
- blocks
- customer / individual-provider capabilities
- active-mode switching
- business draft, submission, approval, rejection, and public preview
- trust read-through from `trust.score.updated.v1`
- event emission through the outbox

Important domain decision now enforced:

- A business profile is **not** a separate account.
- `CUSTOMER` stays enabled even after business approval.
- The same person can switch from `BUSINESS` back to `CUSTOMER` and continue normal-user flows.

### trust-gamification-service

Implemented now:

- consumes `profile.created.v1`
- creates a baseline `BASIC` trust state
- emits `trust.score.updated.v1`

This is the first real trust foundation; automatic verification and moderation are still future work.

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

### Remaining identity slimming gate

The dangerous part of Phase 4 is intentionally not collapsed into the same cutover:

1. display fields are already mirrored from profile-service into identity-service,
2. but the physical `identity_account` column drop is still deferred until the mirror has burned in and clients have
   stopped using `/me` for display data.

That is a migration gate, not missing product behavior.

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

The next clean boundary is no longer more profile work. It is deciding the next product lane:

1. marketplace / listings / offer lifecycle,
2. richer trust automation (business auto-review, human verification, moderation contracts),
3. or the search/payment spine those flows will depend on.
