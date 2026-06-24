# ADR-0002: Listings are provider & business profiles

- **Status:** Accepted
- **Date:** 2026-06-24
- **Covers decisions:** D2, D3 (see [roadmap](../../Product/viaverse-ux-overhaul-roadmap.md))

## Context

The Explore/Listings surface (today `/app/services`) was ambiguous: it could plausibly show provider profiles,
concrete service offers, open customer requests, sponsored placements, or a mix. Mixing "people to hire" with
"jobs to be hired for" on one surface creates a core confusion — *am I hiring or being hired?* — and bloats the
mental model. Listings also currently use icon tiles rather than real imagery.

## Decision

1. **Explore/Listings shows provider & business profiles only** — image, rating, categories, distance, price
   band — backed by `providers()` / `businesses()`.
2. **Service requests are never listings.** Seeker-authored requests (`ServiceRequestView`) live in
   "My Requests" and surface to providers as **matched jobs** (see
   [ADR-0003](./0003-provider-work-discovery-matched-jobs.md)).
3. **Cards use real images** via `media-service` / `mockPhotos()` (`MockPhotoView`), replacing icon tiles. The
   listings nav icon is replaced with a more appropriate one.
4. **Sponsored profiles** may be interleaved as a distinct, labeled slot (see
   [ADR-0006](./0006-ads-placement-policy.md)).

## Consequences

- One surface = one intent: Explore is unambiguously "find a person/business."
- Requires richer category/subcategory data and image plumbing (Phase 3).
- Keeps requests and profiles as separate models in the UI, matching the backend (`ServiceRequestView` vs
  `ProviderView`/`BusinessView`).
- A future "browse open requests" surface for providers, if ever added, is separate from Explore.

## Alternatives considered

- **Profiles + service offers:** deferred — richer, but needs a service-catalog/offer model not yet justified
  for MVP.
- **Unified everything (profiles + offers + requests + sponsored):** rejected — maximum flexibility but exactly
  the "overloaded/confusing" feel we want to avoid.
