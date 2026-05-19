# Current implementation status

Status date: **2026-05-19**

## What is implemented now

The repository is no longer only a shell. The active product slice is:

- identity/auth foundation
- profile-service through the **functional Phase 4 boundary**
- first organic content + media vertical slice
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

### content-service

Implemented now:

- organic social posts for local updates, announcements, advice, events, and business promotion
- author-mode snapshotting so an account can publish as customer / provider / business without splitting accounts
- event-specific invariants and publish events through the outbox
- current-user and published-post reads
- first rule-based `/feed/social`
- typed interaction capture for impressions, opens, dwell, engagement, hide/report, and video milestones

### media-service

Implemented now:

- media asset records and upload-session lifecycle
- direct object-storage uploads through presigned URLs
- SeaweedFS-backed S3-compatible local storage integration
- completion verification through object metadata before an asset becomes `READY`
- typed `media.asset.ready.v1` outbox event

### Client follow-through already present

- Web:
  - authenticated `/app`
  - `/app/profile`
  - `/app/feed`
  - organic post creation with direct media upload path
  - profile editing
  - provider enablement
  - business draft / submission
  - active-mode switching
  - header mode switcher that lets enabled users move between customer, provider, and business modes
  - `/app/marketplace` request/offer/job screens backed by the work-feed APIs
- Admin web:
  - first business approval queue
- Mobile:
  - authenticated four-tab app shell
  - home shell reads the real active mode
  - social feed read/create surface
  - marketplace work-feed, request creation, offer, cancellation, withdrawal, and job actions
  - profile screen for active-mode switching and individual-provider enablement

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

### marketplace-service

Implemented now:

- request creation and customer request listing
- offer submission, withdrawal, current-provider offer listing, and acceptance
- open-request cancellation with pending-offer cleanup
- accepted-offer → job lifecycle (`AGREED` → `IN_PROGRESS` → `COMPLETED`)
- transactional outbox events for the lifecycle
- first rule-based **work feed** using profile-owned service categories plus own-request suppression and business locality
- `/app/marketplace` web flow for requests, relevant jobs, provider offers, and active jobs

Important product distinction now reflected in code:

- the marketplace work feed is **not** the local/social home feed,
- approved businesses use their business service categories,
- individual providers use their own declared service categories,
- customer mode does not silently degrade into a generic provider-opportunity feed.

### Other services

These services are still mostly technical shells:

- payment-service
- messaging-service
- notification-service
- search-service
- ads-monetization-service

They compile, migrate, and expose health, but they do not yet contain their future business feature sets.

### UI gaps

Still missing or intentionally thin:

- full mobile business onboarding/management screens
- richer admin moderation tooling
- ML-backed recommendation behaviour over the social feed

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

The active lane is now **marketplace transaction depth**.

The next meaningful work is to move from request/offer/job skeleton into the real service-delivery loop:

1. job-scoped messaging and status timeline,
2. payment authorization / escrow-style hold before job start,
3. dispute/cancellation rules around in-progress jobs,
4. search over requests, providers, businesses, and local posts.

See:

- `Docs/Architecture/marketplace-service/`
- `Docs/Architecture/content-and-media-boundaries.md`
- `Docs/Architecture/feed-and-recommendation.md`
