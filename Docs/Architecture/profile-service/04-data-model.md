# Data model

Conceptual entities only — column types and exact constraints are decided at implementation time. Every table carries `created_at`, `updated_at`, and `version` (optimistic-lock) by convention; not repeated below.

## `profile` (aggregate root, 1:1 with `identity_account`)

| Field | Notes |
|---|---|
| `account_id` (PK, FK → identity) | Provisioned when consuming `account.created.v1`. |
| `display_name` | Authoritative copy. Mirrored from identity at provision time; profile-service is the writer going forward. |
| `first_name`, `last_name` | Same as above. |
| `avatar_media_id` | Nullable. Points at `media-service`. |
| `headline` | Short tagline, 80 chars. |
| `bio` | Long form, 600 chars. |
| `locale`, `timezone` | UI preference defaults. |
| `active_mode` | `CUSTOMER` \| `INDIVIDUAL_PROVIDER` \| `BUSINESS` — must be in capabilities. |
| `completeness_score` | 0–100, recomputed on write. Cached, not authoritative; the scoring rule is the source of truth. |
| `public_visibility` | `PUBLIC` \| `LIMITED` \| `PRIVATE` |

## `profile_capability` (1:N)

| Field | Notes |
|---|---|
| `account_id`, `capability` | Composite PK. `capability` ∈ `CUSTOMER`, `INDIVIDUAL_PROVIDER`, `BUSINESS`. |
| `status` | `ENABLED`, `PENDING_REVIEW` (business pre-approval), `SUSPENDED` (moderation) |
| `enabled_at`, `disabled_at` | Audit timestamps. |
| `verification_level` | Provider only: `NONE`, `BASIC`, `ENHANCED`. |

## `individual_provider_profile` (1:1 with profile when capability enabled)

| Field | Notes |
|---|---|
| `account_id` (PK, FK) | Exists iff individual-provider capability is at least PENDING. |
| `service_blurb` | "What can you help with?" — 200 chars. |
| `service_areas` | Geo polygons / city list. Phase 2 detail; placeholder column in Phase 1. |
| `availability_summary` | "Weekends only", "Anytime" — free text in Phase 1, structured later. |
| `accepts_remote` | bool. |
| `provider_terms_version_accepted` | Joined against identity-service's consent registry. |

## `business_profile` (0..1)

| Field | Notes |
|---|---|
| `account_id` (PK, FK) | Exactly one business per account in Phase 1. |
| `legal_name`, `trade_name` | Both required. |
| `sector` | Enum (Phase 1: pharmacy, clinic, agency, shop, software, other). |
| `tax_id` | Stored opaquely; validated where TR registry allows. |
| `address_line`, `district`, `city`, `country` | Plain address fields; geocoding is `search-service`'s job. |
| `phone`, `email_public` | Separate from the user's personal identifiers. |
| `logo_media_id` | Points at `media-service`. |
| `opening_hours_json` | Light structured blob. |
| `verification_status` | `DRAFT`, `SUBMITTED`, `APPROVED`, `REJECTED`. |
| `business_terms_version_accepted` | Same consent-registry pattern. |

## `profile_preference`

Generic settings bag — one row per (account, key). Avoids growing the `profile` row with every new toggle.

| Field | Notes |
|---|---|
| `account_id`, `key` | Composite PK. |
| `value_json` | Schemaless; reading code validates by key. |

Examples of keys: `notifications.email.marketing`, `notifications.push.jobs_nearby`, `ui.theme`, `ui.language`.

## `profile_block`

| Field | Notes |
|---|---|
| `blocker_account_id` | Whose list this is. |
| `blocked_account_id` | Composite PK with blocker. |
| `reason` | Free-text, 200 chars; not shown to the blocked user. |

Consumed by messaging-service and search-service; profile-service is the source of truth.

## `profile_event_outbox`

Standard outbox row, identical pattern to `identity-service` (see [shared-modules.md](../shared-modules.md) on extraction).

## Events emitted

| Event | When | Consumers |
|---|---|---|
| `profile.created.v1` | After consuming `account.created.v1` | search-service (build index entry) |
| `profile.updated.v1` | On any display field change | search-service, messaging-service (header card cache) |
| `profile.capability.enabled.v1` | Capability flipped on | search-service (provider toggle), notification-service |
| `profile.capability.disabled.v1` | Capability flipped off | same |
| `profile.business.submitted.v1` | Business onboarding completed | admin-bff (approval queue) |
| `profile.business.approved.v1` | Ops approves | notification-service, search-service |
| `profile.blocked.v1` | New block | messaging-service, search-service |

All payloads carry `eventId` (UUID), `occurredAt`, `version`, and the relevant aggregate id. Same envelope shape as `AccountCreatedV1KafkaEvent`.
