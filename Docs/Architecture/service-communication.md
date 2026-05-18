# Service-to-service communication

Three transports, one job each. Pick by call shape, not by fashion.

## The three lanes

| Transport | When | Why |
|---|---|---|
| **Kafka events** | Fact happened that *other services might care about* — async, fan-out, eventual consistency is fine. | Decouples producer from consumers, replayable, resilient to consumer downtime. |
| **gRPC internal RPC** | One service needs an *answer* from another *now* — synchronous read or command-with-result. | Typed, low-latency, schema-first, native streaming. No public exposure. |
| **REST / JSON over HTTP** | Browser, mobile, or third-party calling a BFF. | Universal client compat. Stops at the BFF boundary. |

## Decision table

| Call shape | Transport | Example |
|---|---|---|
| "Account was created." | Kafka | `account.created.v1` → profile-service provisions a row. |
| "Status changed, suspend everywhere." | Kafka | `account.status.changed.v1` → profile, marketplace, messaging react. |
| "Render this profile card right now." | gRPC | messaging-service asks profile-service for header card on chat open. |
| "Does this user have provider capability?" | gRPC | marketplace-service checks before accepting a service listing. |
| "Approve this business, return the new state." | gRPC | admin-bff → profile-service. Needs the result back. |
| "User refreshed their inbox." | REST → BFF → gRPC fan-out | Web/mobile client never speaks Kafka or gRPC directly. |
| "Search returned 20 users — fetch their cards." | gRPC server-streaming | One round-trip, N profiles. |

## Boundaries

- **Public surface (web, mobile, partners):** REST/JSON via `web-bff` / `mobile-bff` / `admin-bff` only. No gRPC port exposed publicly.
- **Service-to-service writes that change shared state:** Always emit an event after the write. Even if a gRPC call did the write, fire a Kafka event so other consumers can react. **gRPC for the immediate caller, Kafka for everyone else.**
- **Service-to-service reads:** Prefer gRPC. Avoid REST/JSON between services — it's slower, looser-typed, and gives us nothing the gRPC stack doesn't.
- **Critical write path:** **Never** make a synchronous chain three services deep. Two hops max (BFF → service-A → service-B) before you switch to events.

## When NOT to use Kafka

- You need the answer in the response. (Use gRPC.)
- The consumer set is exactly *one* service and the call is request/response shaped. (gRPC is clearer.)
- You're tempted to "wait for the event" in a request handler. That's a synchronous call wearing a costume — write gRPC instead.

## When NOT to use gRPC

- More than one service cares about the same fact. (Use Kafka.)
- Caller doesn't actually need the response. (Kafka — fire and forget.)
- You'd be making the same call hundreds of times from one service to another for the same data. (Project the data via events into the caller's read store.)

## How this fits into our architecture

- **profile-service Phase 1** ships Kafka producers/consumers only. The first gRPC server lands when marketplace-service (Phase 2 epic) needs to read provider capability synchronously.
- gRPC contracts live in `packages/api-contracts` (`.proto` files compiled to Java + Kotlin). Both producer and consumer depend on the package.
- gRPC ports use `90xx` range (`identity-service` → `9101`, `profile-service` → `9102`). The HTTP ports (`81xx`) stay for actuator + BFF traffic.
- Internal calls authenticate with **mTLS** in staging/prod and a shared bearer token in local dev. Same approach as `/internal/*` HTTP endpoints.
- Observability: gRPC interceptors propagate `X-Correlation-Id` (and trace context) exactly like our HTTP filter does. The OpenTelemetry gRPC instrumentation is already on the classpath via `packages/observability`.

## What this means for the existing docs

- [profile-service/06-integration.md](profile-service/06-integration.md) lists "internal" calls as HTTP. Read those as **"internal RPC, gRPC when implemented"**; the HTTP examples there are placeholders until gRPC ships. The Kafka half of that doc is final.
- [engineering-standards.md](engineering-standards.md)'s "Messaging" section becomes "Events" (Kafka) and gets a sibling "RPC" subsection covering gRPC patterns — added when we write the first gRPC service.
- `web-bff` and `admin-bff` keep speaking HTTP to identity-service for now; we migrate per-route when the gRPC server is up. The public REST surface they expose to clients doesn't change.

## Rule of thumb

> If the answer is "tell everyone who cares" → Kafka.
> If the answer is "ask one service and use the reply" → gRPC.
> If a browser or phone is on the other end → REST through a BFF.
