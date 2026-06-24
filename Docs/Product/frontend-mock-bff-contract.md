# Frontend / Mock-BFF contract

Status date: **2026-06-24**

The discipline that keeps the Viaverse web app **backend-ready**: the frontend is built as if the real
microservices are already connected, and all mocking lives in the Mock BFF — never in UI components. When the
real services land, swapping them in must require only a wiring change, not a screen rewrite.

See [ADR-0004](../Architecture/decisions/0004-backend-ready-frontend-mock-bff-contract.md). Parent roadmap:
[viaverse-ux-overhaul-roadmap.md](./viaverse-ux-overhaul-roadmap.md). Related:
[`Architecture/web-app-mock-bff-product-prototype.md`](../Architecture/web-app-mock-bff-product-prototype.md).

---

## Current state (already mostly compliant)

- `apps/web-next/lib/mockAppClient.ts` and `lib/authClient.ts` call the real Spring Boot **Mock Web BFF** at
  `http://localhost:8120` (`NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL`). **No `fetch` calls live in components.**
- The Mock BFF (`services/mock-web-bff`) is a real service: Spring Boot + PostgreSQL, ~47 endpoints across 7
  controllers, seed data in `…/app/seed/*.java`, standard `{ success, data }` response envelope, DTOs in
  `AppDtos.java` that mirror the TypeScript `*View` types.
- 14 stubbed microservices exist (identity, profile, content, marketplace, messaging, media, payment,
  notification, search, ads-monetization, trust-gamification, web-bff, admin-bff, mock-web-bff).

The only gap: a few **hardcoded-data leaks** still live in UI components (below).

---

## What data belongs in the Mock BFF

Everything that represents content, configuration, or business state, including:

feed posts · announcements/incidents · events · **listings (provider/business profiles)** · **categories +
subcategories** · **locations + location suggestions** · service requests · offers · matched
jobs/opportunities · messages/conversations · notifications · **onboarding answers** · user settings · profile +
capability/setup status · transactions · support/AI answers · ads/sponsored creative · gamification
scores/badges · moderation status · and UI config such as **feed filters** and **announcement types**.

## What must never be hardcoded in the frontend

No mock arrays or business data inside `app/` or `components/`: no listings, users, categories, requests,
messages, locations, coordinates, ad creative, filter configs, or announcement-type tables.

**Current offenders to migrate (Phase 0):**

| Location | Hardcoded data | Move to |
|---|---|---|
| `components/product/feed/CreatePostModal.tsx` | `ISTANBUL_AREAS` (coords), `typeOptions` | BFF `locations` endpoint; composer config |
| `components/product/feed/feedModel.ts` | `feedFilters`, `announcementTypes`, `locationSuggestions` | BFF `feed-config`, `announcement-types`, `location-suggestions` |

Pure presentational enums (e.g. icon-name → component maps) may stay in the UI; data with labels, coordinates,
tones, or business meaning must come from the BFF.

---

## Proposed API contract groups (extend `mockAppApi`)

- **Session/Identity:** `session`, `setActiveMode`, capability `setupStatus`, `onboardingAnswers` get/submit.
- **Feed/Content:** existing feed/posts/comments/incidents/events + `feed-config`, `announcement-types`.
- **Discovery/Listings:** `providers`, `businesses`, `categories` (with subcategories), `locations`,
  `location-suggestions`, `sponsoredAds(surface)`.
- **Requests/Work:** `myRequests`, `createRequest`, `requestOffers`, `opportunities` (matched jobs),
  `createOffer`, `acceptOffer`.
- **Messaging/Notifications:** existing `conversations`, `messages`, `sendMessage`, `notifications`.
- **Profile/Settings/Provider/Business:** profile, settings, provider profile + setup, business profile +
  setup, catalog, campaigns, appointments, reviews, team.
- **Support/Trust:** `support/ask` (RAG), gamification scores/badges, moderation status.

---

## Service / client layer structure

- Keep the **single typed-client** pattern: components call `mockAppApi.*` (and `authClient.*`); they never
  `fetch` directly.
- Group client methods by domain (above). All response shapes are typed `*View` models — these are the contract
  surface and must stay in parity with the Mock BFF DTOs (`AppDtos.java`) and, later, the real services.
- **Server state vs UI state are separate:** server data comes from `mockAppApi`; ephemeral interaction state
  stays in component state. If caching needs grow, introduce a thin data-fetching hook layer — but the boundary
  (components ↔ typed client ↔ BFF) does not change.

## How role-based navigation reads state

Navigation set, default route, CTAs, and route guards derive from `session.currentUser.activeCapability` plus
each capability's `enabled` / `setupStatus` — all server state. A single nav-config module maps capability →
ordered nav items + default route. No local toggles or hardcoded role branches in components. See
[role-based-navigation-model.md](./role-based-navigation-model.md).

---

## Simulating backend states

The Mock BFF and its seed classes drive every UI state, so the frontend's state handling is exercised before
real services exist:

| State | How the Mock BFF produces it | UI renders |
|---|---|---|
| Loading | response latency | skeletons matching final layout |
| Empty | seeded-empty datasets per surface | empty state: message + primary CTA |
| Error | error envelope `{ success: false }` | standardized error card |
| Unauthorized / role-restricted | capability check → 403 | guard redirect (or "set up to continue") |
| Pending setup | capability `setupStatus = INCOMPLETE` | redirect to the mode's setup wizard |
| Status transitions | request/offer status changes in seed/state | live status badges |
| Message threads | seeded conversations/messages | thread view |

---

## Replacing the Mock BFF with real microservices

Because the frontend depends only on `mockAppApi` + typed `*View` contracts — which already mirror the real
services (identity, profile, content, marketplace, messaging, media, payment, notification, search,
ads-monetization, trust-gamification) — the migration path is:

1. Point the client base URL / BFF wiring at `web-bff` → real services instead of `mock-web-bff`.
2. Keep the `*View` contract shapes stable (TS `*View` ↔ Java DTO parity is the invariant to protect).
3. No screen rewrites required.

Contract parity is the thing to guard in review: any change to a `*View` type must be matched on both sides.

---

## Global acceptance criteria

- No mock arrays or fake business data inside UI components.
- All mock data comes from the Mock BFF.
- UI consumes data through the typed API/service-client abstractions.
- Screens are backend-ready and can be connected to real services with only a wiring change.
- Role-based menus and permissions are driven by account/profile/session state, not hardcoded local toggles.
