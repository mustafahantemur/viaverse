# ADR-0004: Backend-ready frontend / Mock-BFF contract

- **Status:** Accepted
- **Date:** 2026-06-24
- **Covers decisions:** D12 (see [roadmap](../../Product/viaverse-ux-overhaul-roadmap.md))
- **Related:** [frontend-mock-bff-contract.md](../../Product/frontend-mock-bff-contract.md)

## Context

The frontend must become production-ready without a rewrite when the real microservices are connected. The app
is already ~90% there: `lib/mockAppClient.ts` and `lib/authClient.ts` call the real Spring Boot Mock BFF
(`http://localhost:8120`), no `fetch` lives in components, and typed `*View` models mirror the Mock BFF DTOs
(`AppDtos.java`). The remaining risk is **mock/business data leaking into UI components**
(`ISTANBUL_AREAS`/`typeOptions` in `CreatePostModal.tsx`; `feedFilters`/`announcementTypes`/`locationSuggestions`
in `feedModel.ts`).

## Decision

1. **No mock arrays or business data in UI components.** Listings, users, categories, requests, messages,
   locations, coordinates, ad creative, filter configs, and announcement-type tables all come from the Mock BFF.
2. **All data flows through the typed client** (`mockAppApi` / `authClient`); components never `fetch`.
3. **Mocking lives only in the Mock BFF** (seed classes), which behaves like a lightweight version of the future
   services — realistic contracts, realistic states (loading/empty/error/unauthorized/pending-setup).
4. **`*View` ↔ Java DTO parity is the protected invariant.** Any change to a contract type is matched on both
   sides in review.
5. **Migration = wiring change:** point the client at `web-bff` → real services; keep contract shapes stable; no
   screen rewrites.
6. **Phase 0 migrates the current leaks** into BFF endpoints (`locations`, `location-suggestions`, `feed-config`,
   `announcement-types`) and adds `setActiveMode` / `onboardingAnswers` / capability `setupStatus`.

## Consequences

- Screens are backend-ready; real services connect without UI rewrites.
- Slightly more upfront work (BFF endpoints for config/data that was previously inline).
- Contract parity must be enforced in review; a `*View` change without a DTO change is a defect.
- Role-based navigation and permissions are driven by session/profile state, not local toggles
  (see [ADR-0001](./0001-role-based-navigation-and-mode-model.md)).

## Alternatives considered

- **Keep small config arrays inline "for convenience":** rejected — they are exactly the prototype-only shortcuts
  that require later removal and erode the backend-ready guarantee.
- **Introduce a heavy client cache/state library now:** deferred — the component ↔ typed-client ↔ BFF boundary is
  what matters; a hook/cache layer can be added later without changing it.
