# Web App + Mock Web BFF product prototype

Date: 2026-05-19

## What this phase implemented

This phase adds the first serious post-login Viaverse web application foundation and a standalone mock Web BFF:

- `apps/web-next` now has a navigable authenticated app shell under `/app`
- `services/mock-web-bff` exposes product-shaped REST endpoints under `/api/app/**` and mock identity/auth endpoints under `/api/auth/**`
- the web UI consumes the mock API through `apps/web-next/lib/mockAppClient.ts`
- the landing/auth flow can also target `mock-web-bff`, so local product work is not blocked by the real/proxy Web BFF
- mutable flows persist into the mock service's own H2-backed test database
- the existing real/proxy `services/web-bff` is intentionally untouched

The landing and auth modal still route users to `/app`, but the post-login product area no longer depends on real backend service completeness for this prototype.

## Why backend expansion was paused

Identity, profile, content, and marketplace slices already exist, but the overall backend direction was becoming hard to judge while the user experience was still abstract. This slice makes the product concrete first:

- users can browse a social/info feed for nearby posts, announcements, events, traffic, utility alerts, and useful local information
- users can create social posts, announcements, events, and service/job requests from their relevant product surfaces
- providers can see matching opportunities and submit offers
- accepted offers conceptually transition to messaging
- services discovery distinguishes `Bireysel hizmet veren` and `İşletme`
- profile, settings, payments, notifications, and messages are represented as product surfaces

The goal is not to replace backend domain work. It is to reveal which backend obligations are actually needed.

## Boundary decision

`services/mock-web-bff` is a separate service/module. It is today's mock product API and tomorrow's contract reference for the real Web BFF.

Hard boundary:

- no mock endpoints were added to `services/web-bff`
- the real/proxy Web BFF was not refactored or repurposed
- `apps/web-next` uses `NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL` for this prototype API
- `apps/web-next/lib/authClient.ts` defaults to the Mock Web BFF in this branch so sign-in/sign-up can run against mock identity data
- default local mock API base URL is `http://localhost:8120`

## Seed and mock identity structure

Mock data is split by product concern under `services/mock-web-bff/src/main/java/app/viaverse/mockwebbff/app/seed`:

- `IdentitySeed` for mock login accounts and registration drafts
- `PersonaSeed`, `ProfileSeed`, and `ServiceDiscoverySeed` for current-user, capability, provider, and business data
- `SocialSeed` for feed posts, hashtags, media hints, and comments
- `WorkSeed`, `MessagingSeed`, `FinanceSeed`, and `ActivitySeed` for requests/offers, conversations, payments, and notifications

Seeded mock identity accounts:

- `deniz@viaverse.test` / `Password123!` for the standard user persona
- `ece@viaverse.test` / `Password123!` for the `Bireysel hizmet veren` persona
- `mert@viaverse.test` / `Password123!` for the `İşletme` persona

## Endpoint inventory

Mock identity/auth:

- `GET /api/auth/required-consents`
- `GET /api/auth/capability-terms`
- `POST /api/auth/password-login`
- `POST /api/auth/register/start`
- `POST /api/auth/register/verify-email`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/forgot-password/start`
- `POST /api/auth/forgot-password/verify-otp`
- `POST /api/auth/forgot-password/complete`
- `GET /api/me`

Session/current user:

- `GET /api/app/me`
- `POST /api/app/session/persona`
- `POST /api/app/dev/reset`

Feed/content:

- `GET /api/app/feed?type=SOCIAL|ALL|POST|ANNOUNCEMENT|EVENT|ALERT|INFO`
- `POST /api/app/posts`
- `GET /api/app/feed/hashtags?query={query}`
- `GET /api/app/media/mock-photos?query={query}`
- `GET /api/app/ads?surface={surface}`
- `POST /api/app/posts/{postId}/like`
- `POST /api/app/posts/{postId}/share`
- `GET /api/app/posts/{postId}/comments`
- `POST /api/app/posts/{postId}/comments`
- `GET /api/app/events`
- `GET /api/app/announcements`

Services:

- `GET /api/app/services/categories`
- `GET /api/app/providers`
- `GET /api/app/providers/{id}`
- `GET /api/app/businesses`
- `GET /api/app/businesses/{id}`
- `GET /api/app/searches/saved?surface={surface}`
- `POST /api/app/searches/saved`

Requests/jobs/offers:

- `GET /api/app/requests/mine`
- `POST /api/app/requests`
- `GET /api/app/opportunities`
- `POST /api/app/offers`
- `GET /api/app/offers/mine`
- `GET /api/app/requests/{requestId}/offers`
- `POST /api/app/offers/{offerId}/accept`

Messaging:

- `GET /api/app/conversations`
- `GET /api/app/conversations/{conversationId}/messages`
- `POST /api/app/conversations/{conversationId}/messages`

Profile/settings:

- `GET /api/app/profile`
- `PATCH /api/app/profile`
- `GET /api/app/settings`
- `PATCH /api/app/settings`

Payments/activity:

- `GET /api/app/payments/transactions`
- `POST /api/app/payments/mock-intents`
- `PATCH /api/app/payments/mock-intents/{transactionId}`
- `GET /api/app/notifications`

## Persisted flows

The mock service stores a versioned JSON document in an H2 file database at the service build output path. This keeps the prototype stateful without creating production-like schemas too early.

Persisted today:

- created feed posts, information cards, announcements, and events
- feed likes, shares, comments, hashtags, and comment counts
- created service/job requests
- saved feed and services searches
- submitted offers
- accepted-offer status, matched request state, and generated conversation/message state
- sent conversation messages
- profile patches and active capability changes
- settings patches
- mock payment intents and completed/failed/cancelled states
- mock registration drafts and newly verified mock identity accounts

Reset:

- `POST /api/app/dev/reset` reseeds the mock state

## Intentionally simplified

- mock identity/auth exists for product development, but there is no production-grade authentication or authorization enforcement in `mock-web-bff`
- no real marketplace matching engine
- no real media upload path in the prototype app
- no real-time messaging transport
- no real payment provider integration
- no production schema or Flyway migrations for the mock state
- no final moderation, notification fan-out, or ranking implementation

These are product-shaping mocks, not production backend decisions.

## Evolution into the real Web BFF

When backend services are ready, the real Web BFF should align with the useful parts of this contract and replace mock reads/writes with aggregation/proxy calls:

- session/profile data from identity/profile services
- feed and content cards from content/feed services
- provider and business discovery from profile, marketplace, search, and trust signals
- requests/offers/jobs from marketplace-service
- accepted-offer messaging handoff into messaging-service
- payment intents and transaction history from payment-service
- notifications/activity from notification-service

The frontend should only need API base/configuration and contract alignment changes, not page-level data rewrites.

## Backend questions exposed

Account/profile:

- Which profile fields are app-shell-critical versus editable profile details?
- Should active capability switching live only in profile-service, or should Web BFF own a richer session projection?
- How should business capability and individual provider capability appear together without implying separate accounts?

Marketplace:

- What is the minimum request DTO needed for matching without over-modeling jobs too early?
- How should nearby small jobs remain a request/service lane under `Bireysel hizmet veren`?
- What statuses are needed before accepted offer becomes job/payment/messaging?
- What exact event should trigger the accepted-offer to messaging transition?

Feed/community:

- Are posts, announcements, events, alerts, and request-derived provider opportunities one content model with type-specific invariants, or separate read models aggregated by the BFF?
- Which feed cards come from content-service versus marketplace-service versus business profile events?
- Where should ranking/filtering for `Tümü`, `Duyurular`, `Etkinlikler`, traffic, utility alerts, and useful local information live?

Messaging:

- Does messaging attach to request/offer/job entities directly, or through a conversation context record?
- Which system messages should be created by marketplace events?
- What read/unread model is needed across standard, provider, and business capabilities?

Payments:

- When should a payment intent be created: before offer acceptance, after acceptance, or before job start?
- Which transaction states must be visible in Web BFF without leaking provider-specific payment internals?
- How should mock wallet/history evolve into payment-service contracts?

Notifications:

- Which events should become activity items versus push/email notifications?
- Should activity be queried as a BFF aggregation over multiple event sources?
