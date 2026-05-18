# Bounded context

## Purpose

profile-service is the system of record for **how a user appears, what they can do, and what they prefer**. It is read by every other service that needs to render or check a user (search, marketplace, messaging, trust). It is written by the user (and by admin tools for moderation actions).

## Responsibilities

Profile-service **owns**:

| Concern | Examples |
|---|---|
| **Display identity** | `display_name`, `first_name`, `last_name`, `avatar_url`, short bio, headline, locale, timezone |
| **Capabilities** | which of `CUSTOMER`, `INDIVIDUAL_PROVIDER`, `BUSINESS` the account has enabled |
| **Active mode** | which capability the app is currently presenting (UI hint, not a security boundary) |
| **Public preview** | computed view of "how my profile appears to a stranger" |
| **Preferences** | notification channels, language, theme, search defaults — anything the user toggles in *Settings* |
| **Blocks** | per-account "I don't want to see this user" list (also enforced by search/messaging) |
| **Trust/completeness scaffolding** | profile completeness %, verified-badge state |
| **Business profile** | legal name, trade name, sector, tax id (KVKK-safe placement TBD), opening hours, location, logo |
| **Support entry-points** | "help with my account" prompts, but **not** the ticketing/CRM itself |

Profile-service **does NOT own**:

| Concern | Lives in |
|---|---|
| Login credentials, sessions, identifiers (email/phone), OTP flows | `identity-service` |
| Listings, requests, offers | `marketplace-service` |
| Payouts, billing, KYC docs (financial) | `payment-service` |
| Reviews, ratings, disputes | `trust-gamification-service` (consumer) — profile-service exposes the aggregated badge/score read-through |
| Conversations | `messaging-service` (blocks are *consulted* by messaging) |
| Search index | `search-service` (profile-service publishes events; search projects them) |
| Avatars / images at rest | `media-service` (profile-service stores `avatar_media_id`, not bytes) |

## Cuts that are intentional

- **Capabilities are flags, not separate accounts.** A user is one account that can be `CUSTOMER + INDIVIDUAL_PROVIDER + BUSINESS` simultaneously. We do not model "provider account" as a sibling row.
- **Business profile is a 1-to-1 attached aggregate**, not a parallel account. The same login owns it. Multi-business-per-user is a Phase 2+ consideration; until then, enforce one business profile per account.
- **No payment/KYC fields here.** When a provider wants to receive payouts, `payment-service` collects what it needs; profile-service just knows "provider capability is enabled" + "payments side has approved this user" (a flag mirrored from a payment-service event).
- **No moderation actions here.** Admin tooling lives in `admin-bff` and writes to profile-service via internal endpoints; the moderation queue itself is owned by `trust-gamification-service` (or a future `moderation-service`).

## What stays in identity-service

Identity continues to own:

- `identity_account` row (status, roles, password, 2FA)
- `identity_identifier` (verified email/phone)
- `auth_login_flow`, `auth_session`, refresh tokens, OTPs
- The `account.created.v1` event (profile-service consumes it to provision a blank profile)

Eventually `display_name / first_name / last_name / profile_completed` migrate out of `identity_account`. The transition plan is in [06-integration.md](06-integration.md).
