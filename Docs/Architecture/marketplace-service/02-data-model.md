# Data model

## `service_request`

| Field | Meaning |
|---|---|
| `id` | Request id |
| `requester_account_id` | Customer who created it |
| `title`, `description` | User-facing content |
| `category` | Stable service taxonomy |
| `budget_min_amount_minor`, `budget_max_amount_minor`, `currency` | Optional budget range in minor units |
| `remote_allowed`, `district`, `city` | Discovery hints |
| `status` | `OPEN`, `MATCHED`, `CANCELLED`, later `COMPLETED` |
| timestamps/version | standard aggregate metadata |

## `service_request_media`

Ordered references from a request to media ids owned by `media-service`.

Marketplace never stores raw images or video bytes.

## `offer`

| Field | Meaning |
|---|---|
| `id` | Offer id |
| `request_id` | Target request |
| `provider_account_id` | Offer author |
| `amount_minor`, `currency`, `message` | Commercial proposal |
| `status` | `SUBMITTED`, `ACCEPTED`, `REJECTED`, later `WITHDRAWN` |
| timestamps/version | standard aggregate metadata |

## `job`

| Field | Meaning |
|---|---|
| `id` | Job id |
| `request_id`, `accepted_offer_id` | Origin |
| `requester_account_id`, `provider_account_id` | Participants |
| `agreed_amount_minor`, `currency` | Frozen commercial terms |
| `status` | `AGREED`, `IN_PROGRESS`, `COMPLETED`, later `CANCELLED`, `DISPUTED` |
| timestamps/version | standard aggregate metadata |

## Future-facing columns intentionally prepared now

- `service_request_media` exists before upload UI lands so request photos/videos do not force a schema rewrite later.
- Money is stored in **minor units** from day one so payment integration does not need a currency remodel.
- Content moderation is intentionally not hard-coded here yet; the owning service will later persist moderation decisions
  once the shared moderation lane exists.

## Matching inputs intentionally live outside marketplace

`marketplace-service` owns the request itself, but not the provider profile that decides **which** requests should be
shown first.

- business service categories live with `business_profile` in `profile-service`,
- individual-provider service categories live with `individual_provider_profile` in `profile-service`,
- marketplace reads those through the internal profile lane when building the first rule-based work feed.

This keeps “what work exists?” separate from “which work is relevant to this actor?”
