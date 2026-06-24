# Viaverse UX overhaul — master roadmap

Status date: **2026-06-24**

This is the master roadmap for raising the whole Viaverse web app to the quality bar set by the feed
screen. It defines the product decisions, screen inventory, phasing, implementation batches, and design
guidelines for the overhaul. It is a planning artifact — future implementation phases execute against it.

Companion documents:

- [Role-based navigation model](./role-based-navigation-model.md) — IA + per-mode nav + mode switching.
- [Frontend / Mock-BFF contract](./frontend-mock-bff-contract.md) — backend-ready discipline.
- [Repository, Backend, Mobile, and Architecture Plan](../Architecture/repository-backend-mobile-architecture-plan.md) — doc audit, backend/identity refactor, cross-cutting, security, messaging, mobile, DevOps.
- Architecture decisions: [`Docs/Architecture/decisions/`](../Architecture/decisions/) (ADR-0001…0006).

Related existing docs: [`Architecture/web-app-mock-bff-product-prototype.md`](../Architecture/web-app-mock-bff-product-prototype.md),
[`Development/web-app-mock-bff-product-prototype-brief.md`](../Development/web-app-mock-bff-product-prototype-brief.md).

---

## What Viaverse is

A neighborhood-scale platform combining three surfaces:

- a **social / local-info feed** (posts, announcements/incidents, events),
- a **service marketplace** (seekers post requests, providers make offers),
- **business discovery** (provider/business profiles, ads/sponsored placement).

Turkish-language UI, warm cream background with orange primary and green trust accents.

### The three user types

| Type | Capability key | Primary job-to-be-done |
|---|---|---|
| **Service Seeker** | `STANDARD` | Browse feed, discover nearby providers/businesses, post and track service requests, message. |
| **Service Provider** | `INDIVIDUAL_PROVIDER` | Set up a provider profile, receive matched jobs, send offers, manage jobs, build reputation. |
| **Business Profile** | `BUSINESS` | Operate a managed presence: business page, services/products, campaigns, appointments, reviews, team, sponsored promotion. |

## Current implementation state (factual)

- **Landing** (`apps/web-next/app/page.tsx`): marketing page + `AuthModal` (login / register / forgot-password). Exists; to be redesigned with 4 explicit CTAs.
- **App shell** (`apps/web-next/components/product/ProductAppShell.tsx`): top nav (7 fixed items), a **persona dropdown** at lines 155-167 (`switchPersona` at 67-71) — to be removed, a capability-aware sidebar via `sidebarContext(pathname, capability)`, mobile bottom nav. Desktop grid: `264px` sidebar + content; mobile breakpoint 980px.
- **Screens present:** feed (`/app`, reference quality), services (`/app/services`), requests (`/app/requests`), provider (`/app/provider`), messages, payments, activity, profile, settings, marketplace (redirect). All consume the Mock BFF via `mockAppApi`.
- **No onboarding flow** exists yet.
- **Design tokens:** `apps/web-next/app/styles/tokens.css` (`--vv-*`). Light/dark via `lib/theme/ThemeProvider.tsx`.
- **Backend-ready already:** `lib/mockAppClient.ts` and `lib/authClient.ts` both call the Spring Boot Mock BFF at `http://localhost:8120`. No `fetch` calls live in components. See the [frontend/Mock-BFF contract](./frontend-mock-bff-contract.md).
- **Known hardcoded-data leaks to fix:** `ISTANBUL_AREAS`, `typeOptions` in `components/product/feed/CreatePostModal.tsx`; `feedFilters`, `announcementTypes`, `locationSuggestions` in `components/product/feed/feedModel.ts`.

---

## Product decisions

| # | Decision | ADR |
|---|---|---|
| D1 | **Primary action per type:** Seeker → create a request. Provider → respond to matched jobs. Business → manage presence via dashboard. | — |
| D2 | **Listings/Explore** shows **provider & business profiles only** (image, rating, categories, distance, price band). | [0002](../Architecture/decisions/0002-listings-are-provider-business-profiles.md) |
| D3 | **Service requests** are seeker-authored demand; they live in "My Requests" and surface to providers as matched jobs — never as listings. | [0002](../Architecture/decisions/0002-listings-are-provider-business-profiles.md) |
| D4 | **Providers get work** via a curated **matched-jobs** list (`opportunities()` `matchReason`/`fitScore`); broad browse is post-MVP. | [0003](../Architecture/decisions/0003-provider-work-discovery-matched-jobs.md) |
| D5 | **Business ≠ Provider:** business adds team, catalog, campaigns, appointments, reviews, hours, gallery, sponsored promotion. | [0001](../Architecture/decisions/0001-role-based-navigation-and-mode-model.md) |
| D6 | **Role/mode switching** happens only in **Profile → Profiller ve Modlar**, persisted server-side as `activeCapability`. Persona dropdown removed; active mode shown as a read-only chip. | [0001](../Architecture/decisions/0001-role-based-navigation-and-mode-model.md) |
| D7 | **Setup gating:** Provider/Business modes require completing their setup wizard before activation; unset modes hidden from nav; direct access redirects to setup. | [0001](../Architecture/decisions/0001-role-based-navigation-and-mode-model.md), [0005](../Architecture/decisions/0005-onboarding-and-mode-setup-gating.md) |
| D8 | **Onboarding:** short post-register survey (intent, location, interests, optional role interest) stored as `onboardingAnswers`; personalizes feed, seeds location. | [0005](../Architecture/decisions/0005-onboarding-and-mode-setup-gating.md) |
| D9 | **Ads** only on high-browse surfaces (feed rail + in-feed, explore results, request detail) via `sponsoredAds(surface)`. | [0006](../Architecture/decisions/0006-ads-placement-policy.md) |
| D10 | **AI / moderation / gamification** designed-for now (status/score fields), built in Phase 7. AI assistant = thin chat over a BFF RAG endpoint; moderation server-side, UI renders status only. | — |
| D11 | **MVP = Phases 0–3.** Provider (P4), Business (P5), shared polish (P6), AI/gamification/ads (P7), responsive QA (P8) follow. | — |
| D12 | **Backend-ready discipline:** no mock arrays or business data in components; all data via typed clients; role nav reads `session` state. | [0004](../Architecture/decisions/0004-backend-ready-frontend-mock-bff-contract.md) |

The full role-based navigation model (per-mode menus, switching, gating) is specified in
[role-based-navigation-model.md](./role-based-navigation-model.md).

---

## Screen inventory (by phase)

| Screen | Route | Phase | Status today |
|---|---|---|---|
| Landing (redesign) | `/` | P1 | Exists, redesign |
| Auth (login/register/forgot) | AuthModal | P1 | Exists, polish |
| Onboarding survey | `/onboarding` | P1 | New |
| Profile → Profiller ve Modlar | `/app/profile` | P1 | Extend existing |
| Feed | `/app` | reference | Reference quality |
| Explore (profiles) | `/app/explore` | P3 | Rework of `/app/services` |
| Create Request | `/app/requests/new` | P2 | Split out of `/app/requests` |
| My Requests | `/app/requests` | P2 | Exists, polish |
| Request Detail | `/app/requests/[id]` | P2 | New |
| Provider Dashboard | `/app/provider` | P4 | Exists, rework |
| Provider Setup wizard | `/app/provider/setup` | P4 | New (port prototype 4-step) |
| Matched Jobs + detail | `/app/provider/jobs` | P4 | Partly in `/app/provider` |
| Offers / History / Performance | `/app/provider/*` | P4 | New |
| Business Dashboard | `/app/business` | P5 | New |
| Business Page mgmt | `/app/business/page` | P5 | New |
| Catalog / Campaigns / Appointments / Reviews / Team | `/app/business/*` | P5 | New |
| Messaging | `/app/messages` | P6 | Exists, polish |
| Support / AI Assistant | `/app/support` | P7 | New |
| Settings | `/app/settings` | P6 | Exists, polish |
| Profile (self + public) | `/app/profile`, `/app/u/[id]` | P6 | Self exists; public new |
| Ads/Sponsored placements | cross-surface | P7 | Feed rail exists; expand |

---

## Phased roadmap

### Phase 0 — Audit & product clarification
- **Goal:** Lock decisions (this doc + ADRs), define typed contracts, migrate hardcoded leaks into the Mock BFF.
- **Screens:** none (foundation).
- **Tasks:** finalize role-nav config keyed by `activeCapability`; move `ISTANBUL_AREAS`, `feedFilters`, `announcementTypes`, `locationSuggestions` into Mock BFF endpoints; define `onboardingAnswers`, `setActiveMode`, capability `setupStatus`.
- **Dependencies:** none. **Risks:** scope creep. **Acceptance:** no hardcoded business data in `components/`; nav config reads session.

### Phase 1 — Landing, auth, onboarding, role model
- **Goal:** Public entry + deliberate role model; remove persona dropdown.
- **Screens:** Landing (redesign), Auth, Onboarding, Profile→Profiller ve Modlar.
- **Tasks:** redesign `app/page.tsx` with 4 CTAs; post-register `/onboarding` survey; remove dropdown (`ProductAppShell.tsx:155-167`, `switchPersona` 67-71); add `setActiveMode`; mode-management section in Profile; route guards.
- **Dependencies:** P0. **Risks:** auth regressions; guard loops. **Acceptance:** register → onboard → seeker feed; modes switch only from Profile and persist on reload; provider/business routes redirect to setup when unset; no top-bar dropdown.

### Phase 2 — Service-seeker core flow
- **Goal:** Make the seeker's primary loop excellent.
- **Screens:** Create Request, My Requests, Request Detail.
- **Tasks:** split Create Request into its own route; richer My Requests states; Request Detail with offers + accept; seeker nav set.
- **Dependencies:** P1. **Risks:** request/offer model gaps. **Acceptance:** seeker creates a request, sees offers, accepts one, lands in a conversation — all via `mockAppApi`.

### Phase 3 — Listings/Explore redesign (MVP boundary)
- **Goal:** Replace icon tiles with image-rich provider/business discovery.
- **Screens:** Explore + profile detail.
- **Tasks:** new explore grid (real images via `mockPhotos`/media-service); redesigned filters (search, location, price, availability, rating, distance, category+subcategory); richer category taxonomy (port from prototype); replace listings icon; sponsored-profile slot.
- **Dependencies:** P2; category data in BFF. **Risks:** image licensing (use `media-service`/seeded `MockPhotoView`). **Acceptance:** Explore shows profile cards with images + working filters; nothing hardcoded.

### Phase 4 — Service-provider flow
- **Goal:** Provider can set up and win work.
- **Screens:** Provider Setup wizard, Dashboard, Matched Jobs (+detail), Offers, History, Performance, Provider Profile.
- **Tasks:** port prototype 4-step setup; dashboard KPIs + matched jobs (`opportunities()`); offer submission sheet; provider nav set + guards.
- **Dependencies:** P1 (mode model), P2 (requests exist). **Risks:** matching quality (mock curated list ok). **Acceptance:** seeker switches to provider via setup, sees matched jobs, submits an offer that appears in the seeker's request detail.

### Phase 5 — Business-profile flow
- **Goal:** Business presence & ops.
- **Screens:** Business Setup, Dashboard, Incoming Requests, Catalog, Campaigns, Appointments, Reviews, Business Page, Team.
- **Tasks:** business setup wizard; dashboard; catalog & campaign management; appointments flow; reviews; team mgmt; business nav set + guards.
- **Dependencies:** P4 patterns. **Risks:** largest surface — keep batches small. **Acceptance:** a business can be set up, manage a catalog/campaign, receive a request, and book an appointment — all data-driven.

### Phase 6 — Messaging / support / settings / profile polish
- **Goal:** Raise shared surfaces to feed quality.
- **Screens:** Messages, Settings, Profile (self + public).
- **Tasks:** messaging polish; settings IA cleanup; public profile screen; consistent empty/loading states.
- **Dependencies:** P1–P5. **Acceptance:** shared screens match design guidelines; public profile reachable from Explore.

### Phase 7 — AI support, moderation, gamification, ads
- **Goal:** Trust, assistance, monetization.
- **Screens:** Support/AI Assistant; gamification on profile/dashboard; expanded ad placements.
- **Tasks:** BFF RAG endpoint + thin chat UI; moderation status rendering on posts/requests/listings; trust score / badges / activity score / completed-jobs / achievement levels (via `trust-gamification-service`); ad surfaces via `sponsoredAds(surface)`.
- **Dependencies:** core loops done. **Acceptance:** assistant answers from BFF; flagged content shows status; gamification visible; ads only on approved surfaces.

### Phase 8 — Responsive QA & final polish
- **Goal:** Production-ready across breakpoints and states.
- **Tasks:** audit every screen at 375/768/1024/1440px; verify empty/loading/error/unauthorized/pending-setup states; accessibility pass; performance.
- **Acceptance:** all screens pass the responsive + state matrix; no console errors; a11y/perf targets met.

---

## Implementation batches

Each batch is independently reviewable/testable.

**Phase 0** — 0.1 nav-config module keyed by `activeCapability` · 0.2 BFF `locations` + `location-suggestions`; remove from UI · 0.3 BFF `feed-config` + `announcement-types`; remove from `feedModel.ts` · 0.4 BFF `onboardingAnswers` / `setActiveMode` / capability `setupStatus`.

**Phase 1** — 1.1 landing hero + role value props · 1.2 landing 4 CTAs wired · 1.3 onboarding survey + persistence · 1.4 remove persona dropdown, render mode chip · 1.5 Profile → "Profiller ve Modlar" · 1.6 route guards + redirect-to-setup.

**Phase 2** — 2.1 Create Request standalone · 2.2 My Requests states · 2.3 Request Detail + offers/accept.

**Phase 3** — 3.1 Explore grid + image cards · 3.2 filter rework · 3.3 category taxonomy from BFF · 3.4 sponsored-profile slot.

**Phase 4** — 4.1 provider setup wizard · 4.2 dashboard + KPIs · 4.3 matched jobs + detail · 4.4 offer sheet · 4.5 offers/history/performance · 4.6 provider profile.

**Phase 5** — 5.1 business setup · 5.2 dashboard · 5.3 catalog · 5.4 campaigns · 5.5 appointments · 5.6 reviews · 5.7 business page · 5.8 team.

**Phase 6** — 6.1 messaging · 6.2 settings · 6.3 public profile · 6.4 shared states.
**Phase 7** — 7.1 AI assistant · 7.2 moderation status · 7.3 gamification · 7.4 ads expansion.
**Phase 8** — 8.1 responsive audit · 8.2 state matrix · 8.3 a11y/perf.

---

## Design guidelines

These extend the existing `--vv-*` token system and the feed screen's patterns. They were cross-checked
against the `ui-ux-pro-max` design-intelligence skill (community/marketplace landing patterns, trust-and-authority
style, and the critical UX ruleset). **Keep Viaverse's existing brand tokens and fonts** (Plus Jakarta Sans +
Open Sans) — do not adopt the skill's generic palette/font suggestions.

### Layout principles
- Reuse the app shell + `--vv-*` tokens (`app/styles/tokens.css`). Consistent content max-width with the feed.
- Sidebar nav driven by active mode (see role-nav doc). One active nav item, clearly indicated (color + left/bottom accent).
- Define a **z-index scale** (10 / 20 / 30 / 50) for dropdowns, sticky bars, overlays, modals.
- No content hidden behind fixed bars; reserve space for async content (avoid layout shift).

### Card styles
- Match the feed card (`.socialPost`): `--vv-radius-md`, `--vv-shadow-sm`, subtle border; image with `object-fit: cover`; ellipsis-truncated meta.
- Equal-height cards in a grid row (`align-items: stretch`); expandable content must not stretch siblings.
- All clickable cards get `cursor: pointer` and a hover state (color/shadow/border) — **never** a scale transform that shifts layout. Transitions 150–300ms.

### Form & wizard patterns
- Reuse `ProductControls.tsx` (`TextField`, etc.). Every input has an associated `<label>`.
- Multi-step wizards (provider/business setup, onboarding) show "Adım x/N", allow back, validate inline, and disable the submit button during async with a busy label.
- Primary/outline button hierarchy; one primary action per view.

### Filter patterns
- One shared filter pattern (chips + autocomplete + slider) across feed and explore. Dynamic active-filter count; no redundant section headers.
- Filters update the URL (deep-linking) so a filtered view is shareable and survives reload.

### Empty / loading / error states
- **Empty:** icon + one-line explanation + a single primary CTA (e.g. "Henüz talebin yok → Talep oluştur"). Never a blank screen.
- **Loading:** skeletons matching the final layout; no spinners-on-blank where a skeleton fits; never layout-shift.
- **Error / unauthorized / pending-setup:** standardized cards; pending-setup routes to the relevant wizard.

### Mobile behavior
- Bottom nav per mode (≤980px); wizards full-screen; sheets slide from the bottom (port the prototype's `motion` sheet pattern).
- Minimum 44×44px touch targets; minimum 16px body text; no horizontal scroll; respect `prefers-reduced-motion`.
- Verify at 375 / 768 / 1024 / 1440px.

### Ad placement rules (see [ADR-0006](../Architecture/decisions/0006-ads-placement-policy.md))
- **Allowed:** feed right-rail, in-feed every N cards, explore results, request detail.
- **Forbidden:** messaging, payments/checkout, onboarding, settings, offer submission.
- Always labeled "Sponsorlu"; always served via `sponsoredAds(surface)`.

### Image usage
- All imagery via `media-service` / `mockPhotos()` (`MockPhotoView` carries `sourceLabel`/`sourceUrl`). Never hot-link arbitrary web images in components.
- Use WebP + `loading="lazy"` + responsive `srcset` for below-the-fold images; always provide descriptive `alt` (decorative images get empty `alt`).

### Accessibility & polish (critical)
- Color contrast ≥ 4.5:1 for body text; color is never the only signal.
- Visible focus rings; tab order matches visual order; `aria-label` on icon-only buttons.
- SVG icons (Lucide) only — no emoji icons. Consistent 24×24 viewBox.

---

## Open questions before implementation

1. **Category taxonomy source of truth:** port the prototype's 10×(30–50) Turkish taxonomy into the Mock BFF `categories` seed as canonical? (Assumed yes.)
2. **Provider vs Business overlap:** can one account hold both capabilities active-switchable, or is Business an upgrade of Provider?
3. **Appointments scope (P5):** simple request-an-appointment, or full calendar/availability with conflict handling?
4. **AI assistant data sources (P7):** which docs/rules feed the RAG (platform rules, help center, dynamic data)?
5. **Payments in MVP:** is `/app/payments` in the seeker MVP loop (pay on accept) or display-only until later?

---

## First recommended next step

Execute **Phase 0 + Phase 1.1–1.4** in order:

1. **P0.1** nav-config module keyed by `activeCapability` (pure data; unblocks every later screen).
2. **P0.2–0.4** migrate hardcoded leaks into Mock BFF endpoints; add `setActiveMode` / `onboardingAnswers` / setup-status to contracts.
3. **P1.4** remove the persona dropdown; render the read-only mode chip.
4. **P1.1–1.2** landing redesign + 4 CTAs.

This delivers visible progress (landing + dropdown removal), establishes the role-based-nav backbone, and
enforces the backend-ready discipline before any large screens are built.

---

## Appendix — multi-agent rationale (why the decisions landed here)

A condensed design-review between four roles resolved the contested points:

- **Seeker flow:** primary action is *post a request*; the feed is engagement, not conversion. → Seeker nav prioritizes Feed + Explore + Create Request + My Requests. (D1)
- **Provider flow:** providers don't want to scroll hundreds of requests — show matched jobs. → matched-jobs-only in MVP. (D4, [ADR-0003](../Architecture/decisions/0003-provider-work-discovery-matched-jobs.md))
- **Business flow:** a business is a *managed presence* (team, catalog, appointments) — genuinely more surface than a single provider, so it earns its own nav and a dedicated late phase. (D5, Phase 5)
- **Listings vs requests:** one surface = one intent. Explore = profiles; requests stay separate to avoid "am I hiring or being hired?" confusion. (D2/D3, [ADR-0002](../Architecture/decisions/0002-listings-are-provider-business-profiles.md))
- **Dropdown removal:** mode is identity, not a quick toggle. Switch deliberately from Profile; show mode as a read-only chip. Active mode must be server state, with route guards. (D6, [ADR-0001](../Architecture/decisions/0001-role-based-navigation-and-mode-model.md))
- **Onboarding:** short survey first; deep provider/business setup only on opt-in. (D8, [ADR-0005](../Architecture/decisions/0005-onboarding-and-mode-setup-gating.md))
- **Ads / AI / gamification:** ads only where browse intent is high; AI/moderation/gamification map to real stubbed services and are designed-for now, built in Phase 7. (D9/D10, [ADR-0006](../Architecture/decisions/0006-ads-placement-policy.md))

---

## Verification (per phase)

- Run `apps/web-next` against the Mock BFF (`services:mock-web-bff`, port 8120).
- **P1:** register → onboarding → seeker feed; switch modes only via Profile, persists on reload; provider/business URLs redirect to setup when unset; no top-bar dropdown.
- **P2:** create request → receive offer → accept → conversation, all via `mockAppApi`.
- **P3:** Explore shows image-rich profile cards + working filters; zero hardcoded data (grep `components/` for inline arrays).
- **P4:** seeker→provider setup → matched jobs → submit offer appears on seeker's request.
- **P5:** business setup → catalog/campaign → incoming request → appointment.
- **Cross-cutting:** every touched screen verified across 375/768/1024/1440px and the state matrix (loading/empty/error/unauthorized/pending-setup); no mock arrays in UI; ads only on approved surfaces.
