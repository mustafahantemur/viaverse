# Content, feed, media, and ads boundaries

Viaverse will eventually show several card types across the product:

- service requests and small jobs
- neighbourhood announcements
- events
- advice / help posts
- organic business promotion posts
- paid business campaigns and video ads

They are **not the same bounded context**, and the product should expose **two distinct main flows**:

1. a local social feed for "what is happening around me?",
2. a work feed for "which requests / offers / jobs are relevant to me?"

## Ownership model

| Concern | Owning service |
|---|---|
| Service requests, offers, accepted jobs, commercial lifecycle | `marketplace-service` |
| Organic posts: announcements, events, advice, local updates, organic business promotion | `content-service` |
| File bytes, upload lifecycle, object keys, transforms, thumbnails, variants | `media-service` |
| Paid campaign setup, spend, targeting, delivery policy, reporting | `ads-monetization-service` |
| Shared policy decisions for unsafe text/image/video | future `moderation-service` |
| Search projections across requests, posts, businesses, and media metadata | `search-service` |
| Personalized ranking over candidate posts / requests | future `recommendation-service` |

## Why the split matters

`marketplace-service` owns a **transactional graph**:

1. a customer creates a request,
2. providers submit offers,
3. one offer is accepted,
4. a job moves through delivery states,
5. later payment/dispute/review flows attach to that job.

Announcements and events do not have that graph. A business promo video may be monetized later, but the video bytes
still belong to `media-service`, and the organic post still belongs to `content-service`.

## Media rule

Domain services store **media ids, never bytes**.

- `media-service` is the system of record for `media_asset`.
- SeaweedFS is the local/object-storage backing store behind `media-service`.
- `marketplace-service`, `profile-service`, future `content-service`, and `ads-monetization-service` only reference
  media ids.
- A future moderation lane receives the text/image/video payload before broad visibility, but the owning domain service
  keeps its own publish decision snapshot.

This keeps photos on service requests, business logos, event covers, ad videos, and feed posts on one reusable
storage foundation without collapsing their business rules together.

## Current first implementation

The first production-oriented cut now exists:

- `content-service` owns organic post records and emits typed lifecycle events,
- `media-service` owns asset/upload-session records and emits `media.asset.ready.v1`,
- browser clients upload bytes directly to SeaweedFS through short-lived presigned URLs,
- domain services still persist only media ids.

## Product consequence

Feeds can later be **projections**, not sources of truth:

- `marketplace-service` emits request/job events,
- `content-service` emits post/event/promotion events,
- `ads-monetization-service` overlays paid delivery decisions,
- `search-service` retrieves candidates,
- a future `recommendation-service` ranks them per user/context.

The local social feed and the work feed may reuse cards and recommendation infrastructure, but they should not be
collapsed into one source or one product lane. A “work request” can still be rendered like a card while preserving the
fact that only the work flow can receive offers, become a job, collect payment, and enter dispute/review flows.
