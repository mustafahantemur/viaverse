# Bounded context

## Purpose

`marketplace-service` is the system of record for **commercial intent turning into work**.

## Owns

| Concern | Examples |
|---|---|
| Requests / small-job listings | title, description, category, budget range, locality |
| Offers | provider amount, message, offer state |
| Jobs | accepted offer, agreed amount, execution state |
| Marketplace lifecycle events | request created, offer submitted/accepted, job created/started/completed |

## Does not own

| Concern | Lives in |
|---|---|
| User identity, active mode, capabilities | `profile-service` |
| Authentication | `identity-service` |
| Media bytes / video storage | `media-service` |
| Announcements, events, advice posts, organic business promotion | future `content-service` |
| Paid campaign delivery / ad spend | `ads-monetization-service` |
| Payments, payout, escrow, refunds | `payment-service` |
| Search index | `search-service` |
| Moderation engine | future `moderation-service` |

## Domain decisions

- A **requester** can be any authenticated account. Business owners do not lose customer rights.
- An **offerer** must have a provider-side capability (`INDIVIDUAL_PROVIDER` or approved `BUSINESS`) exposed by
  `profile-service`.
- Active mode is a UI hint, not an authorization boundary. Marketplace validates capabilities, not whichever chip the
  client currently shows.
- Marketplace stores **media ids** only. The same request can later attach photos or videos without marketplace owning
  bytes.
- Marketplace owns the **work feed source**, not the home/social feed. It emits events; search/recommendation layers
  later decide which requests are most relevant to each provider.
- For businesses, “relevant work” should primarily come from the business's declared service catalog / operating area.
  For individual providers, work ranking can use explicit preferences plus learned behaviour.
