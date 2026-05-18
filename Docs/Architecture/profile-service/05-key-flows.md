# Key flows

Sequence-style summaries. Treat each step as one Spring use case in the future implementation.

## 1. Provisioning on signup

```
identity-service emits  account.created.v1  ─►  profile-service
                                                  ├─ insert profile (mirror display_name, first_name, last_name)
                                                  ├─ insert profile_capability(CUSTOMER, ENABLED)
                                                  └─ emit profile.created.v1
```

The profile row exists *before* the user's first authenticated request, so `GET /me/profile` never has a "not yet provisioned" state. The consumer is idempotent — replays don't duplicate the row.

## 2. Customer → Individual provider transition

UI shows a single CTA in profile: *"Hizmet vermeye başla"*.

1. Client → BFF → `POST /me/capabilities/individual-provider/enable` with the accepted provider-terms version + optional `service_blurb`.
2. profile-service:
   - validates: account active, identifier verified (reads from identity), terms version current (reads from identity's consent registry, see [06-integration.md](06-integration.md)).
   - inserts/updates `profile_capability(INDIVIDUAL_PROVIDER, ENABLED, verification_level=NONE)`.
   - inserts `individual_provider_profile` row with the blurb.
   - emits `profile.capability.enabled.v1`.
3. Client refreshes `/me/profile`; UI now exposes provider screens.

No new auth round-trip, no separate "provider account". The mobile app's bottom nav can grow a "Jobs" tab the moment the capability is on.

## 3. Disabling provider mode

`POST /me/capabilities/individual-provider/disable` — flips status to `DISABLED`, keeps the `individual_provider_profile` row for history. If the user re-enables later, their old service blurb is restored.

## 4. Business onboarding

Multi-step; profile-service stores the draft so the user can resume.

```
1. POST /me/capabilities/business/start
     └─ creates business_profile in DRAFT, capability=PENDING_REVIEW
2. PATCH /me/business/draft  (repeated)
     └─ user fills in legal_name, sector, tax_id, address, hours, logo
3. POST /me/capabilities/business/submit
     └─ profile-service:
         - validates required fields
         - stamps business_terms acceptance
         - flips verification_status to SUBMITTED
         - emits profile.business.submitted.v1
4. admin-bff approval queue → moderator approves
5. admin-bff calls internal endpoint
     POST /internal/business/{accountId}/approve
     └─ profile-service:
         - flips capability to ENABLED, verification_status=APPROVED
         - emits profile.business.approved.v1
```

Rejection mirrors the same shape with `REJECTED` status and a reason event.

## 5. Public profile view

`GET /profiles/{accountId}` from anywhere, *including unauthenticated callers* via BFF cache.

Output is computed each request from:
- `profile` (display fields, headline, bio, avatar)
- `profile_capability` (badges: "Hizmet veriyor", "İşletme")
- `business_profile` (only if capability is APPROVED — otherwise hidden)
- Aggregated trust signals (read-through to `trust-gamification-service` later; placeholder field in Phase 1)

Visibility rules:
- `PUBLIC` — everything below the redaction line is visible to everyone.
- `LIMITED` — only display fields + capabilities; bio, business details require the viewer to be authenticated.
- `PRIVATE` — only display name + avatar, no badges, no bio. Some surfaces (marketplace listings) override this because operating publicly implies a baseline visibility.

Blocks: if the viewer is in the target's block list, the response is the same as `PRIVATE` to that viewer.

## 6. Profile completeness

`completeness_score` is computed by a pure-domain `CompletenessPolicy` (mirrors `ConsentPolicy` shape in identity-service). Inputs: which fields are filled, which capabilities are on, whether avatar exists, whether identifier set has both email and phone (queried from identity).

Re-computed on every write. Exposed on `GET /me/profile` so the client can show "your profile is 70% complete — add a photo".

## 7. Blocks

`POST /me/blocks` with `{ blockedAccountId, reason }`. Profile-service writes the row and emits `profile.blocked.v1`. Messaging-service consumes the event and refuses to deliver new messages between the two accounts; search-service drops the blocked user from the blocker's search results.

Unblock: `DELETE /me/blocks/{blockedAccountId}` emits `profile.unblocked.v1`.

## 8. Preferences

`GET /me/preferences`, `PUT /me/preferences/{key}`. No event emission — preferences are profile-internal. Client-side language/theme reads from this endpoint after login and falls back to the cookie/local choice for first paint.
