# Web App + Mock Web BFF product prototype brief

Date: 2026-05-19
Branch: `feature/web-app-mock-bff-product-prototype`

## Pre-coding decision

This slice pauses backend domain expansion and makes the Viaverse post-login product visible through a coherent web application backed by a dedicated mock REST API.

The mock API will be implemented as a new standalone service:

- `services/mock-web-bff`

The existing real/proxy BFF remains outside the implementation target:

- no mock endpoints are added to `services/web-bff`
- no refactor or repurposing of `services/web-bff`
- the web client will point at `mock-web-bff` through separate app/auth API clients

## References checked

- `README.md`
- `Docs/Development/current-implementation-status.md`
- `Docs/Architecture/engineering-standards.md`
- `Docs/Viaverse Design System/README.md`
- `Docs/Viaverse Design System/ui_kits/web/README.md`
- `Docs/ViaverseUIPrototype/README.md`
- existing `apps/web-next` structure and app/auth/client conventions

`AGENTS.md` and root `CODING_RULES.md` were requested but are not present in the repository search results. Existing repo standards and the user-provided constraints govern this slice.

## Product framing

The app should express Viaverse as a local opportunity and services ecosystem. Visible copy should use concrete location, distance, nearby, service, request, and activity language instead of naming a separate product concept for the user's surrounding area.

Provider capabilities remain:

- `Bireysel hizmet veren`
- `İşletme`

Small nearby jobs are treated as request/service lanes under individual provider capability, not as a third top-level provider type.

## Planned architecture

- `mock-web-bff` exposes `/api/app/**` REST endpoints with product-shaped DTOs.
- `mock-web-bff` also mocks identity-oriented `/api/auth/**` endpoints so product work does not depend on the real/proxy Web BFF.
- Seed data is split by concern inside the mock service: identity, personas, profiles, social feed, service discovery, work/offers, messaging, finance, and activity.
- Seed data is written into its own lightweight H2 test database on first run.
- Mutable flows persist in that mock database: posts, likes, shares, comments, registration drafts/accounts, requests, offers, messages, profile/settings patches, and mock payments.
- `apps/web-next` uses a new centralized API client/hooks layer for the prototype app. UI pages do not import static mock data.
- The endpoint shape is intentionally BFF-like so it can later become a contract reference for the real Web BFF proxy/aggregation layer.
