# Roadmap

## Phase 1 — first working vertical slice

| Item | Outcome |
|---|---|
| Request creation/listing | Customer can create and review requests |
| Open request discovery | Provider can browse open work |
| Offer submission | Provider/business capability is checked before bidding |
| Offer acceptance | Requester chooses one offer; sibling offers are rejected |
| Job creation/execution | Accepted offer becomes a job that can start and complete |
| First work feed | `/feed/work` filters jobs by active mode + profile-owned service categories |
| Web UI | `/app/marketplace` supports the whole flow |
| Events | Marketplace lifecycle emits through the transactional outbox |

## Phase 2 — commercial hardening

- gRPC profile eligibility read
- locality-aware work feed refinement
- behavioural ranking inputs + recommendation-service handoff
- cancellation / withdrawal
- expiry and reopen policies
- search projections and locality filters
- messaging thread creation
- richer provider/job summary cards

## Phase 3 — money and safety

- payment intent / escrow / payout integration
- moderation decisions before broad visibility
- disputes and review hooks
- fraud/risk signals into trust

## Adjacent but separate lanes

- `media-service`: upload sessions, SeaweedFS-backed assets, transforms, thumbnails, video metadata
- future `content-service`: announcements, events, advice posts, organic business promotion
- future `recommendation-service`: ranking for the local social feed and the work feed from behaviour signals
- `ads-monetization-service`: paid campaign delivery over content/media, not ownership of organic posts
