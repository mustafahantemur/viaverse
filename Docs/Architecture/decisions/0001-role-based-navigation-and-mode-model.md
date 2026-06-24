# ADR-0001: Role-based navigation and mode model

- **Status:** Accepted
- **Date:** 2026-06-24
- **Covers decisions:** D5, D6, D7 (see [roadmap](../../Product/viaverse-ux-overhaul-roadmap.md))
- **Related:** [role-based-navigation-model.md](../../Product/role-based-navigation-model.md), [ADR-0005](./0005-onboarding-and-mode-setup-gating.md)

## Context

The app supports three capabilities — `STANDARD`, `INDIVIDUAL_PROVIDER`, `BUSINESS` (`lib/mockAppClient.ts`).
Today the user changes "mode" via a **top-bar persona dropdown** (`components/product/ProductAppShell.tsx`
lines 155-167; `switchPersona` at 67-71) that swaps the entire session. This is easy to trigger accidentally,
treats a deep identity change as a casual toggle, and the navigation is largely the same across modes even
though the three user types have very different workflows.

A Business is also materially more than a Provider: it needs team/staff, a services/products catalog,
campaigns, appointments/reservations, reviews, hours, gallery, and sponsored promotion — which a single-actor
provider does not.

## Decision

1. **Remove the top-bar persona dropdown.** Mode is identity, not a quick toggle.
2. **Active mode is server state** (`session.currentUser.activeCapability`), changed only via a deliberate
   `setActiveMode(capabilityKey)` call.
3. **Each mode has its own navigation set, default route, CTAs, and empty states**, derived from
   `activeCapability` via a single nav-config module (no ad-hoc role branches in components). Shared items:
   Feed, Messages, Profile, Settings. Per-mode menus and route trees are specified in
   [role-based-navigation-model.md](../../Product/role-based-navigation-model.md).
4. **Switching happens only in Profile → "Profiller ve Modlar".** Active mode is shown elsewhere as a
   read-only chip on the profile pill — never an interactive switch.
5. **Business is a distinct mode** with its own dashboard-first navigation, not a provider sub-type.
6. **Route guards** enforce mode access using `activeCapability` + capability `enabled`/`setupStatus`
   (gating detail in [ADR-0005](./0005-onboarding-and-mode-setup-gating.md)).

## Consequences

- Navigation, dashboards, and available actions diverge cleanly per user type; less cross-mode confusion.
- Mode changes are intentional and survive reload (server-persisted).
- Requires a nav-config module (Phase 0, batch 0.1) and route guards (Phase 1, batch 1.6).
- `switchPersona` and the `personaPicker` UI are deleted; `session.personas` is no longer surfaced as a quick
  switch.
- Business mode earns its own late phase (Phase 5) but is designed-for from day one so nothing is rebuilt.

## Alternatives considered

- **Keep the dropdown:** rejected — accidental, mismatched to how different the workflows are.
- **Business as a provider type (as in the prototype):** rejected for the web app — understates the business
  surface area (team, catalog, appointments) and would force later rework.
- **Client-only mode toggle:** rejected — mode must be server state so guards, dashboards, and contracts stay
  consistent and backend-ready.
