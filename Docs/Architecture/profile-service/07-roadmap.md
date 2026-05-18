# Roadmap

Story breakdown for shipping profile-service. Each row is a separate branch / PR / story. Order is dependency-driven; ship top-to-bottom.

## Phase 0 — preconditions (do before profile-service starts)

| # | Story | Outcome |
|---|---|---|
| P0.1 | Extract `packages/web-kernel` + `packages/messaging-kernel` per [shared-modules.md](../shared-modules.md) | identity-service consumes them; build still green. |
| P0.2 | `Docs/Architecture/engineering-standards.md` adopted as the bar | Same checklist applies to profile-service from day one. |

Without P0.1, profile-service will copy outbox/audit/api-response code verbatim from identity-service. That regret compounds across every future service.

## Phase 1 — profile-service v0 (the minimum to be useful)

| # | Story | Outcome |
|---|---|---|
| 1.1 | Module scaffold | `services/profile-service` with Spring Boot 4.0.6 / Java 25 / Flyway / Postgres / Kafka wired exactly like identity-service. Empty `/health` returns 200. |
| 1.2 | Domain + persistence: `profile` aggregate | Tables `profile`, `profile_preference`, `profile_block`. JPA adapters. `Profile` domain model + `ProfilePolicy.computeCompleteness`. |
| 1.3 | Consume `account.created.v1` → provision profile | `profile.created.v1` emitted via outbox. Idempotent. |
| 1.4 | `/me/profile` read + write surface | `GET`, `PATCH display_name/first_name/last_name/headline/bio/avatar_media_id/locale/timezone/public_visibility`. Validation policy. |
| 1.5 | Public profile read | `GET /profiles/{accountId}` with visibility + block rules. BFF route. |
| 1.6 | Preferences | `GET /me/preferences`, `PUT /me/preferences/{key}`. |
| 1.7 | Blocks | `POST /me/blocks`, `DELETE /me/blocks/{id}`, `GET /me/blocks`. Events. |

## Phase 2 — capabilities

| # | Story | Outcome |
|---|---|---|
| 2.1 | `profile_capability` table + read on `/me/profile` | Customer auto-enabled. |
| 2.2 | Enable / disable INDIVIDUAL_PROVIDER | Consent stamping against identity. `provider_terms` consent type added. Event emitted. |
| 2.3 | `individual_provider_profile` aggregate | Service blurb, availability, accepts_remote. |
| 2.4 | `active_mode` switching | Mode pill in mobile + web header reflects state; `PATCH /me/active-mode`. |

## Phase 3 — business onboarding

| # | Story | Outcome |
|---|---|---|
| 3.1 | `business_profile` draft | `POST /start` + `PATCH /draft`. |
| 3.2 | Submission + ops queue | `POST /submit` → admin-bff queue surface. Approval / rejection internal endpoints + events. |
| 3.3 | Public business preview | Business badge + business profile fields on `GET /profiles/{accountId}` once approved. |

## Phase 4 — trust + identity slimming

| # | Story | Outcome |
|---|---|---|
| 4.1 | Trust read-through | Consume `trust.score.updated.v1` (when trust-gamification-service exists); surface badge field on profile read. |
| 4.2 | Display fields migration | Move `display_name / first_name / last_name` writes from identity-service to profile-service; identity becomes a read-mirror via event. |
| 4.3 | identity-service slim-down | Drop the duplicated columns once the mirror has burned in. |

## What ships in the first PR after this planning branch

Just **P0.1 + P0.2 + Story 1.1**. That's enough to merge a working `profile-service` skeleton without yet committing to any data model. The remaining stories land one at a time behind that scaffold.

## Current implementation status

The active implementation branch has now advanced through:

- Phase 0 (`web-kernel`, `messaging-kernel`, engineering standards)
- Story 1.1 through Story 1.7
- Story 2.1 through Story 2.4
- Story 3.1 through Story 3.3
- an extra cross-cutting extraction: `security-kernel`, because `profile-service` became the second JWT consumer earlier than originally expected
- client follow-through for the shipped slice: web profile/business workspace, first admin business queue screen,
  and a real active-mode badge on mobile home

Phase 4 (trust read-through + identity slimming) is the next dependency boundary.

## Definition of done (every story)

- Code follows [engineering-standards.md](../engineering-standards.md).
- Use cases have observed actions + audit annotations where relevant.
- New events flow through the outbox pattern (no direct Kafka publish from a transaction).
- Integration test covers at least one happy path + one failure path.
- Flyway migration is forward-only and idempotent on a re-applied baseline.
- No new "shared" code lands in `services/profile-service/shared/`; it goes to `packages/` or it doesn't ship.
