# ADR-0003: Provider work discovery via matched jobs

- **Status:** Accepted
- **Date:** 2026-06-24
- **Covers decisions:** D4 (see [roadmap](../../Product/viaverse-ux-overhaul-roadmap.md))

## Context

Providers need to find work in the simplest, most useful way. Forcing a provider to scroll and filter through
every open request in their area is high-effort and low-signal, especially at the start when volume is
uncertain. The Mock BFF already exposes `opportunities()` returning requests with `matchReason` and `fitScore`,
and a `search-service` is stubbed to back matching later.

## Decision

1. **Providers find work via a curated "Bana Uygun İşler" (matched jobs) list** — requests matched to their
   categories, work area, and availability — surfaced via `opportunities()` (`matchReason` / `fitScore`).
2. The provider dashboard leads with status, a few KPIs, and this matched-jobs list; tapping a job opens its
   detail where the provider submits an offer (reusing the prototype's bottom-sheet offer pattern).
3. **Broad browse of all open requests is out of MVP scope** and can be added later as a separate surface if
   demand warrants.

## Consequences

- Simplest provider UX; relies on match quality. In the mock phase a deterministic curated list is sufficient;
  real matching arrives with `search-service`.
- Keeps provider home operational (dashboard-first), consistent with [ADR-0001](./0001-role-based-navigation-and-mode-model.md).
- Offers created here appear on the seeker's Request Detail, closing the two-sided loop.

## Alternatives considered

- **Browse + recommended:** more discovery, but more UI and scroll burden; deferred to post-MVP.
- **Browse-only (no matching):** simplest to build now, but worst day-one provider experience; rejected.
