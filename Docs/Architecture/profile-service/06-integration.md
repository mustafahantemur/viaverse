# Integration

How profile-service plugs into the rest of the system today and where it grows.

## Talking to identity-service

profile-service should **never call identity-service in the hot path**. Two integration shapes only:

| Need | Shape | Why |
|---|---|---|
| Provision profile on signup | **Consume** `account.created.v1` Kafka event | Already emitted today; this is the trigger to insert the profile row. |
| Mirror status changes (suspend, reactivate) | **Consume** `account.status.changed.v1` Kafka event | Profile can disable capabilities when the account is suspended, restore on reactivation. |
| Identifier presence check (does user have a verified email or phone for provider-enable?) | Current implementation calls `GET /api/v1/internal/accounts/{id}/provider-readiness`; later we can replace that read with replicated facts if the flow gets hot. | Keeps account standing + verified-identifier truth inside identity while preserving a small read-only internal contract. |
| Consent registry (provider terms / business terms versions) | Identity remains the consent registry. profile-service reads `GET /api/v1/internal/consent-policy` and records accepted versions through `POST /api/v1/internal/accounts/{id}/consents`. | Single source of truth for legal documents and consent history. |
| Trust badge projection | **Consume** `trust.score.updated.v1` from `trust-gamification-service` | Keeps trust ownership outside profile-service while making public/profile reads cheap. |

`/internal/*` endpoints are shared-secret-protected today through `X-Internal-Token` and are not part of the public BFF surface. The config key is shared as `VIAVERSE_INTERNAL_API_TOKEN`; mTLS can replace the header without changing the domain ports.

## Talking to BFFs

`web-bff` and (eventually) `mobile-bff` proxy `/api/profile/**`, `/api/me/profile`, `/api/me/preferences`, `/api/me/blocks`, `/api/me/capabilities/**`, `/api/me/individual-provider-profile`, `/api/me/business/**`, and `/api/me/active-mode`. Same pass-through pattern `web-bff` already uses for identity. No business logic in the BFF beyond:

- Adding the `Authorization` header from the session
- Translating the public `/api/profile` surface to internal `GET /profiles/{accountId}` calls
- Cache headers for unauthenticated public-profile reads

Onboarding UIs also read the current capability legal texts through identity's public
`GET /api/v1/auth/capability-terms` surface (proxied as `GET /api/auth/capability-terms` by `web-bff`).
The client displays the returned URL/version and posts that returned version back when enabling provider
or submitting business onboarding; versions are never hardcoded in the UI.

## Talking to future services

| Consumer | What it needs |
|---|---|
| `search-service` | `profile.created`, `profile.updated`, `profile.capability.enabled/disabled`, `profile.business.approved`, `profile.blocked` — to project a searchable index. |
| `marketplace-service` | Synchronous read of `GET /internal/profiles/{accountId}` to validate that a listing author has the `INDIVIDUAL_PROVIDER` or `BUSINESS` capability before accepting a service-side listing. |
| `messaging-service` | `profile.blocked` events to refuse delivery between blocked pairs; sync `/internal/profiles/{accountId}/header-card` for chat thread headers (cached). |
| `notification-service` | `profile.created`, `profile.business.approved` to send welcome / approval emails. |
| `trust-gamification-service` | Consumes `profile.created.v1`, stores trust state, and emits `trust.score.updated.v1`; later it will also evaluate richer verification and reputation signals. |
| `payment-service` | Reads provider capability state to know who can receive payouts; emits a `payment.provider.payout_ready.v1` event that profile-service uses to gate the "Hizmet veriyor" badge on whether the user can actually be paid. |
| `admin-bff` | Approval queue for business onboarding; internal write endpoints under `/internal/admin/profiles/**`. |

`admin-bff` now fronts the first moderation slice through:

- `GET /api/admin/business-profiles/submissions`
- `POST /api/admin/business-profiles/{accountId}/approve`
- `POST /api/admin/business-profiles/{accountId}/reject`

Those routes only proxy profile-service internal endpoints; the approval transition itself stays in profile-service.

## Compatibility with the existing `/me` surface

identity-service today serves `GET /me`, `POST /me/password`, `POST /me/2fa/*`. We keep those untouched. profile-service adds **new** paths under `/me/profile`, `/me/preferences`, `/me/blocks`, `/me/capabilities`. The web-bff / mobile-bff route by prefix.

Long-term, `GET /me` shrinks to an auth-state read-out (status, roles, has-2fa) and the rich user view comes from `GET /me/profile`. We can ship that refactor in a later story.

## Migration of `display_name / first_name / last_name`

Current state after Phase 4:

1. profile-service is the authoritative writer for display fields after provisioning.
2. identity-service consumes `profile.updated.v1` and keeps its own columns as a **read mirror** for the legacy `/me`
   contract and first-load UX.
3. The physical column drop from `identity_account` is intentionally deferred until:
   - the mirror has proven stable under replay / out-of-order delivery,
   - clients no longer rely on `/me` for display values,
   - and the onboarding path has a deliberate replacement for the synchronous first-read guarantee identity gives today.

No big-bang migration. Each phase ships independently.

## Observability + audit

Same patterns identity-service uses today:

- `@ObservedAction("profile.capability.enable")` on each use case method.
- `@AuditEvent(ProfileAuditEventEnum.PROVIDER_ENABLED)` for business-meaningful actions (capability changes, blocks, business submission/approval).
- `CorrelationIdFilter` from `packages/observability` already gives request-scoped MDC.
- Outbox dispatcher pattern same as identity (and a candidate for [shared-modules.md](../shared-modules.md) extraction).

## Service config and ports

- Service port: `8111` (next free in the current `81xx` allocation; `8102` is already occupied by the existing marketplace-service scaffold).
- Database: `viaverse_profile`.
- Kafka topics:
  - outbound `viaverse.profile.events.v1`
  - inbound `viaverse.identity.account-events`
  - inbound `viaverse.trust.score-events.v1`
- Trace + log shipping: inherit from `packages/observability` and OTel config in `application.yml`, same as identity-service.
