# profile-service — planning docs

Concise planning set for the next microservice. Read top-to-bottom on first pass; each file is < 1 screen.

| # | Doc | Question it answers |
|---|---|---|
| 01 | [Naming decision](01-naming-decision.md) | `account-service` or `profile-service`? |
| 02 | [Bounded context](02-bounded-context.md) | What it owns + what stays in `identity-service`. |
| 03 | [Capability model](03-capability-model.md) | Customer, individual provider, business — how they coexist. |
| 04 | [Data model](04-data-model.md) | Conceptual entities, persistence shape. |
| 05 | [Key flows](05-key-flows.md) | Capability transitions, business onboarding, public profile. |
| 06 | [Integration](06-integration.md) | How profile-service plugs into identity, BFF, future services. |
| 07 | [Roadmap](07-roadmap.md) | Story breakdown to ship Phase 1. |

Cross-cutting (apply to every backend service, not just profile-service):

| Doc | Why |
|---|---|
| [Shared modules](../shared-modules.md) | Where reusable code lives; reorganization plan for `packages/`. |
| [Engineering standards](../engineering-standards.md) | Layering rules, observability, testing — the bar profile-service must clear. |
| [Service communication](../service-communication.md) | Kafka vs gRPC vs REST — which transport for which call shape. |

No code lands until these are agreed on. The implementation branch will be a separate story spawned from this one.
