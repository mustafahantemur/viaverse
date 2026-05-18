# Capability model

## The three capabilities

| Capability | Meaning | Who has it by default |
|---|---|---|
| `CUSTOMER` | Browse, post requests, hire | Every account, on creation |
| `INDIVIDUAL_PROVIDER` | Discover open jobs, apply, complete small/local/flexible work — student, freelancer, repairperson, courier, designer, casual helper. | Off; enabled by the user |
| `BUSINESS` | Operate as a registered business (pharmacy, agency, clinic, shop) — invoicing, business profile, branded presence | Off; enabled through a separate onboarding |

These are **non-exclusive flags on one account**. A user can be all three. The product framing matters:

- The product is not "marketplace for pros". It is **lowering the bar to earning** for ordinary people. INDIVIDUAL_PROVIDER must be cheap to turn on — no portfolio, no certifications required at first. The app reveals more depth as the user takes more jobs.
- BUSINESS is heavier — verified entity, legal name, tax/registration, possibly approved by ops. It must feel different from individual provider.

## Active mode

`active_mode` is a UI hint stored on the profile: which face the app shows on launch. It can be one of the enabled capabilities. Switching modes is one tap. The capabilities are still all enabled in the background.

| Mode | Defaults to |
|---|---|
| New account | `CUSTOMER` |
| Account that enabled provider | Still `CUSTOMER` until first explicit switch — opt-in |
| Account that enabled business | `BUSINESS` after onboarding completes |

**Security note:** `active_mode` does not grant any new permission server-side. Authorization comes from the capability flags themselves. The mode only affects which UI surfaces the BFF/clients render.

## Enabling INDIVIDUAL_PROVIDER (the headline flow)

The transition from "I'm a customer" → "I also want to provide services" must feel like flipping a switch, not signing up again. Minimum requirements to enable:

1. Account is in good standing (not suspended).
2. At least one verified identifier (email **or** phone) — already true post-signup.
3. Accept the provider terms (a new consent type, stamped by `identity-service`'s consent registry).
4. *Optional* in Phase 1: a one-line "what kind of help can you offer?" hint. Skippable.

That's the bar to ship. Stricter checks (ID verification, references, deposit) get layered on per category as policy evolves; profile-service models these as `provider_verification_level` (`NONE`, `BASIC`, `ENHANCED`) so the platform can require a level *per job category* without re-engineering the profile.

## Enabling BUSINESS

Higher bar — handled as a discrete onboarding flow:

1. Legal name, trade name, sector
2. Tax/registration number (validated against TR registry where possible; otherwise stored opaquely until backoffice approves)
3. Address, contact channels, opening hours, logo
4. Accept business terms (separate consent)
5. Ops approval before the business badge becomes visible publicly

Until step 5, `BUSINESS` is "enabled but pending"; the public preview shows a "verifying" indicator instead of the business badge.

## What this means for the API

profile-service exposes:

- `POST /me/capabilities/individual-provider/enable` — flips `INDIVIDUAL_PROVIDER`, stamps consent.
- `POST /me/capabilities/individual-provider/disable` — soft-disables (history kept).
- `POST /me/capabilities/business/start` → `POST /me/capabilities/business/submit` → ops-approval webhook.
- `PATCH /me/active-mode` — switch UI mode among enabled capabilities.
- `GET /me/profile` — full self-view, including capability state and what the user can/should fill in next.
- `GET /profiles/{accountId}` — **public** view, scoped by visibility settings + viewer's relation (blocked, etc).
