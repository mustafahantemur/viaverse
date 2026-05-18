# Integration

How profile-service plugs into the rest of the system today and where it grows.

## Talking to identity-service

profile-service should **never call identity-service in the hot path**. Two integration shapes only:

| Need | Shape | Why |
|---|---|---|
| Provision profile on signup | **Consume** `account.created.v1` Kafka event | Already emitted today; this is the trigger to insert the profile row. |
| Mirror status changes (suspend, reactivate) | **Consume** `account.status.changed.v1` Kafka event | Profile can disable capabilities when the account is suspended, restore on reactivation. |
| Identifier presence check (does user have a verified phone for provider-enable?) | **Replicate the relevant facts via events**, or call `identity-service`'s `GET /internal/accounts/{id}/identifiers` from a domain service. Prefer events. | We don't want a synchronous internal RPC every time a user enables provider mode. |
| Consent registry (provider terms / business terms versions) | Identity remains the consent registry. profile-service **calls** identity's `GET /internal/consent-policy` once at boot (or on cache miss) and stamps versions on its capability rows. | Single source of truth for legal documents. |

`/internal/*` endpoints are mTLS- or shared-secret-protected, not part of the public BFF surface.

## Talking to BFFs

`web-bff` and (eventually) `mobile-bff` proxy `/api/profile/**` and `/api/me/profile`, `/api/me/preferences`, `/api/me/blocks`, `/api/me/capabilities/**`. Same pass-through pattern `web-bff` already uses for identity. No business logic in the BFF beyond:

- Adding the `Authorization` header from the session
- Translating the public `/api/profile` surface to internal `GET /profiles/{accountId}` calls
- Cache headers for unauthenticated public-profile reads

## Talking to future services

| Consumer | What it needs |
|---|---|
| `search-service` | `profile.created`, `profile.updated`, `profile.capability.enabled/disabled`, `profile.business.approved`, `profile.blocked` — to project a searchable index. |
| `marketplace-service` | Synchronous read of `GET /internal/profiles/{accountId}` to validate that a listing author has the `INDIVIDUAL_PROVIDER` or `BUSINESS` capability before accepting a service-side listing. |
| `messaging-service` | `profile.blocked` events to refuse delivery between blocked pairs; sync `/internal/profiles/{accountId}/header-card` for chat thread headers (cached). |
| `notification-service` | `profile.created`, `profile.business.approved` to send welcome / approval emails. |
| `trust-gamification-service` | Reads profile state to compute trust score; emits trust events that profile-service may surface as a badge. |
| `payment-service` | Reads provider capability state to know who can receive payouts; emits a `payment.provider.payout_ready.v1` event that profile-service uses to gate the "Hizmet veriyor" badge on whether the user can actually be paid. |
| `admin-bff` | Approval queue for business onboarding; internal write endpoints under `/internal/admin/profiles/**`. |

## Compatibility with the existing `/me` surface

identity-service today serves `GET /me`, `POST /me/password`, `POST /me/2fa/*`. We keep those untouched. profile-service adds **new** paths under `/me/profile`, `/me/preferences`, `/me/blocks`, `/me/capabilities`. The web-bff / mobile-bff route by prefix.

Long-term, `GET /me` shrinks to an auth-state read-out (status, roles, has-2fa) and the rich user view comes from `GET /me/profile`. We can ship that refactor in a later story.

## Migration of `display_name / first_name / last_name`

Today these live on `identity_account`. Strategy:

1. **Phase 1 (this slice)** — profile-service mirrors them from `account.created.v1`, writes its own copy. identity-service still owns the source row. Reads can come from either; we let profile-service be the writer going forward via a new identity-service event `account.display_changed.v1` that profile-service consumes if identity ever updates them.
2. **Phase 2** — admin tool + UI both call profile-service for writes. identity-service's columns become read-only mirrors via an event subscription from profile-service.
3. **Phase 3** — drop the columns from `identity_account`. `identity-service`'s `AccountView` shrinks to credential state only.

No big-bang migration. Each phase ships independently.

## Observability + audit

Same patterns identity-service uses today:

- `@ObservedAction("profile.capability.enable")` on each use case method.
- `@AuditEvent(ProfileAuditEventEnum.PROVIDER_ENABLED)` for business-meaningful actions (capability changes, blocks, business submission/approval).
- `CorrelationIdFilter` from `packages/observability` already gives request-scoped MDC.
- Outbox dispatcher pattern same as identity (and a candidate for [shared-modules.md](../shared-modules.md) extraction).

## Service config and ports

- Service port: `8102` (next free in the `81xx` range; matches the convention).
- Database: `viaverse_profile`.
- Kafka topics: `viaverse.profile.events.v1` for outbound, subscribes to `viaverse.identity.events.v1`.
- Trace + log shipping: inherit from `packages/observability` and OTel config in `application.yml`, same as identity-service.
