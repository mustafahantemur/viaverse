# Key flows

## 1. Create request

1. Authenticated account submits title, description, category, optional budget, optional media ids.
2. Marketplace validates the aggregate and persists `OPEN`.
3. Outbox emits `marketplace.request.created.v1`.

Any authenticated user may do this, including an approved business owner currently using customer mode.

## 2. Submit offer

1. Authenticated account chooses an open request.
2. Marketplace asks `profile-service` whether the account can offer marketplace work.
3. If the account has `INDIVIDUAL_PROVIDER` or approved `BUSINESS`, the offer is persisted as `SUBMITTED`.
4. Outbox emits `marketplace.offer.submitted.v1`.

The initial implementation uses the existing internal HTTP lane; once the first gRPC contract lands, this becomes the
recommended synchronous profile capability read described in `service-communication.md`.

## 3. Accept offer

1. Only the original requester may accept.
2. The chosen offer becomes `ACCEPTED`.
3. Sibling submitted offers become `REJECTED`.
4. The request becomes `MATCHED`.
5. A job is created with frozen agreed terms and `AGREED` status.
6. Outbox emits `marketplace.offer.accepted.v1` and `marketplace.job.created.v1`.

## 4. Withdraw or cancel before matching

1. A provider may withdraw their own still-submitted offer.
2. A requester may cancel their own still-open request.
3. Cancelling an open request rejects any sibling submitted offers so stale bids do not remain actionable.
4. Outbox emits `marketplace.offer.withdrawn.v1` or `marketplace.request.cancelled.v1`.

## 5. Execute job

1. Assigned provider starts the job → `IN_PROGRESS`.
2. Requester confirms completion → `COMPLETED`.
3. Marketplace appends lifecycle entries to the job timeline.
4. Outbox emits start/completion events.

Payment, messaging, dispute, and review integrations attach to this graph in later slices; this first cut intentionally
keeps the commercial lifecycle coherent before adding more services around it.

## 6. Job timeline

1. Accepting an offer creates a `JOB_CREATED` timeline entry.
2. Starting/completing a job appends `JOB_STARTED` / `JOB_COMPLETED`.
3. Requester and assigned provider can both read the timeline.
4. Requester and assigned provider can add short participant notes through `NOTE_ADDED`.
5. Non-participants cannot read or write the timeline.

This is intentionally not a chat replacement. It is the auditable work history that later messaging, payment, dispute,
and review surfaces can reference.

## 7. Work feed

The first cut now exposes both:

- a raw `open requests` list for simple retrieval,
- a first rule-based `work feed` filtered from profile-owned service categories.

Target behaviour:

- business accounts see requests matching the business service catalog / operating area,
- individual providers see requests shaped by declared preferences first, then by behavioural ranking signals,
- the ranking layer is separate from marketplace ownership so personalization can improve without corrupting the core
  request/offer/job model.

Current implementation boundary:

- active `BUSINESS` mode + approved business capability → business service categories,
- active `INDIVIDUAL_PROVIDER` mode → individual-provider service categories,
- a business work feed suppresses non-remote requests outside the business locality,
- a user never sees their own open requests as provider opportunities,
- customer mode or missing categories → no provider opportunities yet.

That is intentionally conservative: an empty catalog should not quietly degrade into “show every job.”
