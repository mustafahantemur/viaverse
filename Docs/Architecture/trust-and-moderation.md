# Trust, verification, and moderation

Cross-cutting product direction for keeping Viaverse safe without turning the admin team into the primary
decision engine.

## Product stance

- Every human account should accumulate **trust signals** over time; email/phone verification is only the floor.
- Business onboarding should be **automation-first, human-exception-second**. Manual approval remains available,
  but the steady-state path is automatic verification plus risk routing.
- User-generated content (posts, events, listings, job requests, media) should pass through a **shared moderation
  pipeline** before it becomes broadly visible.
- Profile mode is not identity. A verified business owner can still act as a normal customer from the same account.

## Responsibility split

| Concern | Owning service |
|---|---|
| Face/liveness, document, device, reputation, and account-trust signals | `trust-gamification-service` at first; split into a dedicated `verification-service` later if volume/regulation demands it |
| Business verification workflow + risk score | `trust-gamification-service` / future `moderation-service` |
| Persisting business profile fields and capability state | `profile-service` |
| Content safety decisions for text/image/video before publish | future `moderation-service` |
| Admin exception queue | `admin-bff` over moderation/trust internal APIs |

`profile-service` should surface trust state and consume decisions; it should not become the place where biometric
or content-classification logic lives.

## Business approval target flow

1. `profile-service` receives a completed business submission.
2. It emits `profile.business.submitted.v1`.
3. Verification pipeline evaluates deterministic checks:
   - required fields present and normalized
   - registry/tax lookup where available
   - duplicate business/account/device signals
   - contact-channel reachability
   - anomaly/risk score
4. Decision fan-out:
   - `AUTO_APPROVE` → profile-service enables `BUSINESS`
   - `NEEDS_REVIEW` → admin queue
   - `AUTO_REJECT` → rejection with machine-readable reason + appeal path

Until registry integrations and risk signals exist, the current admin approval queue is the safe fallback. We should
not auto-approve every filled form just to reduce queue size; that only moves the workload into abuse cleanup later.

## Human authenticity target flow

Progressive trust, not one giant onboarding wall:

| Level | Typical signals |
|---|---|
| `BASIC` | verified email/phone, device hygiene, no abuse flags |
| `VERIFIED_HUMAN` | liveness / face verification, optional document match where policy requires it |
| `ENHANCED` | payout/KYC, category-specific checks, business registry proof |

Important privacy rule: keep raw biometric media out of general service databases. Store only provider references,
result metadata, timestamps, confidence/risk summaries, and user-visible verification state unless a legal retention
policy explicitly requires more.

## Content moderation target flow

Every publish-capable service calls the same moderation lane:

1. Client submits draft content.
2. Owning service requests a moderation decision before broad visibility.
3. Classifiers/rules inspect text, image, video, links, and account context.
4. Decision is one of:
   - `ALLOW`
   - `ALLOW_WITH_LIMITS`
   - `NEEDS_REVIEW`
   - `REJECT`
5. Owning service persists the decision and emits an event so search/feed/notification projections stay consistent.

This applies to requests, listings, events, posts, images, and later comments/messages where policy requires it.
The policy engine must be centralized enough that "sakıncalı içerik" means the same thing everywhere.

## Why this matters for the current profile work

- The current manual business queue is an **interim fallback**, not the final operating model.
- Phase 4 trust read-through now exposes a badge/state that can later represent `VERIFIED_HUMAN` and business
  verification results without remodeling profile-service again.
- Future business capability transitions should accept machine decisions from the trust/moderation boundary in
  addition to the existing admin internal endpoint.
