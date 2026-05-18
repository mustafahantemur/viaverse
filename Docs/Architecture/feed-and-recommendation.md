# Feed and recommendation architecture

Viaverse has **two different user-facing feeds**. They should not be collapsed just because both can appear as cards in
the app.

## 1. Local social feed

Purpose: answer **“çevremde neler oluyor?”**

Examples:

- neighbourhood announcements
- events
- power outages
- traffic notes
- lost-pet / lost-item posts
- advice / local-help posts
- organic business promotion posts and videos

Owning domain: future `content-service`.

Primary ranking inputs:

- distance and locality
- recency / urgency
- follows and social graph
- explicit interests
- prior engagement: opens, likes, saves, comments, shares, hides
- dwell time / video watch behaviour
- safety and quality signals

## 2. Work feed

Purpose: answer **“hangi işler bana uygun?”**

Examples:

- open customer requests
- bids I submitted
- requests that match what I offer
- accepted jobs and execution states

Owning domain: `marketplace-service`.

Primary ranking inputs:

- declared provider/service categories
- service area, radius, and remote preference
- business service catalog / sector fit
- provider availability
- prior accepted / completed work
- explicit user preferences
- later: response rate, price fit, trust score, conversion likelihood

Important distinction:

- an approved **business** should mainly see work that matches what that business actually offers,
- an **individual provider** feed can be broader and become more personalized from explicit preferences plus behaviour.

Active mode is still a UI hint, not an authorization boundary. The work feed may use active mode to choose the most
helpful default presentation, but eligibility remains capability-based.

## Retrieval vs ranking

Do not make one service responsible for everything.

| Layer | Responsibility |
|---|---|
| Owning service | writes the source-of-truth content/job aggregate |
| `search-service` | retrieves candidate posts / requests efficiently |
| future `recommendation-service` | ranks candidates per user/context |
| `ads-monetization-service` | selects eligible paid campaigns and bids them into the final ranked surface |
| future `moderation-service` | blocks or limits unsafe content before it becomes broadly visible |

The “Instagram-like” behaviour belongs to this ranking layer, not to the source service itself.

## Signals to capture

Recommendation quality will eventually depend on an event stream of user behaviour:

- impression
- open / detail view
- dwell time
- like / comment / save / share
- hide / report / not interested
- search query and category clicks
- video start / quartiles / completion
- offer view / offer submit / accept / complete on the work side

Those events can feed feature stores and ranking models later. Before ML exists, the same contract still supports a
rule-based ranker.

## Ads

Ads should be personalized through the **same contextual understanding**, but kept commercially and legally separate.

- Organic content stays owned by `content-service`.
- Campaign budget, delivery, targeting, pacing, and reporting stay in `ads-monetization-service`.
- Final feed assembly can interleave sponsored items after safety checks and eligibility checks.
- Users should still have transparency controls such as “neden bu reklamı görüyorum?” and preference management.

## Current first implementation

The codebase now exposes two different reads on purpose:

- `/requests/open` remains the raw bootstrap retrieval,
- `/feed/work` is the first rule-based work feed, shaped by the active mode plus profile-owned service categories.
- `/feed/social` is the first rule-based social feed, scored by locality, recency, and near-term event relevance.

The social lane also records typed behaviour signals through `/posts/{postId}/interactions` and emits
`content.interaction.recorded.v1`. That contract is intentionally useful before any ML exists: the current rule-based
feed can improve incrementally, and a later `recommendation-service` can consume the same signal stream without forcing
the source-of-truth content model to change.

This is still only the first step toward the later recommendation layer, but it prevents the product from silently
turning either “all open jobs” or “all published posts” into the long-term UX model.
