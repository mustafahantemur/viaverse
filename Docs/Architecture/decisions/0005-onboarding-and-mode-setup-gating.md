# ADR-0005: Onboarding and mode setup gating

- **Status:** Accepted
- **Date:** 2026-06-24
- **Covers decisions:** D8 and the gating half of D7 (see [roadmap](../../Product/viaverse-ux-overhaul-roadmap.md))
- **Related:** [ADR-0001](./0001-role-based-navigation-and-mode-model.md), [role-based-navigation-model.md](../../Product/role-based-navigation-model.md)

## Context

There is no onboarding today, and the personalization/matching features (feed relevance, provider matching)
need basic signal (intent, location, interests). At the same time, Provider and Business modes require real
setup (categories, work area, business identity, verification) before they can function — activating them empty
would produce broken dashboards. We want a light first-run experience, with deeper setup only when a user opts
into a mode.

## Decision

1. **Short post-register onboarding survey** (`/onboarding`): intent, location, interests, and an optional
   "are you interested in providing services?" question. Answers persist as `onboardingAnswers` (server state)
   and personalize the feed / seed location. It runs only immediately after first registration.
2. **Provider/Business modes require completing a setup wizard before activation:**
   - Provider: **Type → Categories → Profile → Work Area** (ported from the prototype's 4-step flow).
   - Business: **Business identity → Catalog basics → Hours/Location → Verification.**
3. **Gating via capability `setupStatus`** (`NOT_STARTED` / `INCOMPLETE` / `COMPLETE`), surfaced in the session/
   profile contracts:
   - Unset modes are hidden from navigation.
   - Direct URL access to a not-set-up mode redirects to that mode's setup wizard ("set up to continue"),
     not a 404.
4. **Becoming a provider/business is a guided setup**, not an instant switch: a seeker choosing a new mode is
   routed through its wizard, then `setActiveMode` activates it.

## Consequences

- New users reach value quickly; deep forms appear only on opt-in.
- Dashboards never render in a broken empty-capability state.
- Requires `onboardingAnswers` + `setupStatus` contracts (Phase 0) and the two setup wizards (Phases 4–5).
- Onboarding answers become useful input for recommendations/matching later.

## Alternatives considered

- **One big setup form before first use:** rejected — high drop-off, and forces provider/business detail on
  pure seekers.
- **Instant mode switch with no setup:** rejected — produces broken dashboards and empty provider/business
  profiles.
