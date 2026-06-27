# ADR-0006: Ads placement policy

- **Status:** Accepted
- **Date:** 2026-06-24
- **Covers decisions:** D9 (see [roadmap](../../Product/viaverse-ux-overhaul-roadmap.md))

## Context

The product should include natural sponsored/ad areas on web screens where they make sense, without harming the
experience. The Mock BFF already serves `sponsoredAds(surface)`, and an `ads-monetization-service` is stubbed.
We need a clear policy for where ads are appropriate and where they must be avoided, so placements are
consistent and never inserted into high-focus or transactional flows.

## Decision

1. **Allowed surfaces** (high browse intent): feed right-rail, in-feed every N cards, explore/listings results
   (as labeled sponsored profiles), and request-detail sidebar.
2. **Forbidden surfaces** (focus/transaction/setup): messaging threads, payments/checkout, onboarding, settings,
   and provider offer-submission.
3. **Always served via `sponsoredAds(surface)`** with a `surface` key per placement — never hardcoded ad
   creative in components (consistent with [ADR-0004](./0004-backend-ready-frontend-mock-bff-contract.md)).
4. **Always clearly labeled "Sponsorlu"** and visually distinct from organic content.

## Consequences

- Monetization surfaces exist from the start without degrading core flows.
- Each placement needs a `surface` key and seeded ad data in the Mock BFF.
- Ad rendering is data-driven and swappable for the real `ads-monetization-service` with no UI change.
- Most ad work is Phase 7, but the feed rail (existing) already follows this policy.

## Alternatives considered

- **Ads everywhere for maximum inventory:** rejected — harms messaging/checkout/onboarding and erodes trust.
- **No ads until late:** rejected — the policy and the `surface` plumbing should exist early so placements are
  consistent and the design accounts for them.
