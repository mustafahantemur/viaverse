# Marketplace service

The next active product lane after profile/trust.

`marketplace-service` owns the commercial lifecycle:

1. customer request / small-job listing,
2. provider offer,
3. accepted offer,
4. job execution states.

It is deliberately **not** the owner of general social posting. Events, announcements, advice posts, and organic
business promotion belong to a future `content-service`; paid delivery belongs to `ads-monetization-service`; uploaded
files belong to `media-service`.

## Documents

- [01-bounded-context.md](01-bounded-context.md)
- [02-data-model.md](02-data-model.md)
- [03-key-flows.md](03-key-flows.md)
- [04-roadmap.md](04-roadmap.md)

Related:

- [../content-and-media-boundaries.md](../content-and-media-boundaries.md)
- [../feed-and-recommendation.md](../feed-and-recommendation.md)
- [../trust-and-moderation.md](../trust-and-moderation.md)
