# Docs routing

How to find the right document without reading large files in full. Identify the situation, open the **named
doc**, and read only the relevant section (navigate by headings; use `scripts/search-docs.ps1` to jump to a
line).

## Active development references

These are current and canonical — prefer them.

| Folder / file | Use for |
|---|---|
| `Docs/Product/viaverse-ux-overhaul-roadmap.md` | Product vision, UX phases, screen inventory, roadmap |
| `Docs/Product/role-based-navigation-model.md` | Role-based nav, per-mode menus, mode switching |
| `Docs/Product/frontend-mock-bff-contract.md` | Frontend ↔ Mock BFF rules; no mock data in UI |
| `Docs/Architecture/engineering-standards.md` | **Canonical** backend PR-gate: hexagonal, web layer, persistence, security, testing |
| `Docs/Architecture/service-communication.md` | Kafka vs gRPC vs REST decision guide |
| `Docs/Architecture/shared-modules.md` | `packages/*` shared-library boundaries |
| `Docs/Architecture/feed-and-recommendation.md` | Feed + recommendation direction |
| `Docs/Architecture/content-and-media-boundaries.md` | Content/media bounded-context split |
| `Docs/Architecture/trust-and-moderation.md` | Trust, verification, moderation direction |
| `Docs/Architecture/repository-backend-mobile-architecture-plan.md` | Repo/doc audit, backend/identity refactor, cross-cutting, security, messaging, mobile, DevOps |
| `Docs/Architecture/decisions/0001..0006-*.md` | Architecture Decision Records (ADRs) |
| `Docs/Architecture/identity-service/architecture.md` | identity-service reference architecture |
| `Docs/Architecture/profile-service/*`, `marketplace-service/*` | Per-service bounded context, data model, flows, roadmap |
| `Docs/Development/current-implementation-status.md` | What is implemented now + known gaps |
| `Docs/Development/initial-development-start-guide.md` | Fresh clone / first local run |
| `Docs/Development/observability.md` | Logging/tracing/metrics runbook |

## Reference assets (large; do not read in full)

Open only specific parts via search/headings.

| Folder | Notes |
|---|---|
| `Docs/Viaverse Design System/` | Design system, UI kits, assets — large; reference, not living docs |
| `Docs/ViaverseUIPrototype/` | Vendored React prototype source for UI/product inspiration |

## Archive / older notes (historical)

| File | Notes |
|---|---|
| `Docs/Architecture/identity-service/plan.md` | Historical planning; superseded by `architecture.md` + `engineering-standards.md` |
| `Docs/Architecture/identity-service/phase-2-plan.md` | Historical phase plan |
| `Docs/Architecture/identity-service/coding-standards.md` | Largely overlaps `engineering-standards.md` (canonical) |

## Read-this-for-that

| Situation | Start with |
|---|---|
| Set up / run locally | `README.md` → `Docs/Development/initial-development-start-guide.md` |
| What works today / gaps | `Docs/Development/current-implementation-status.md` |
| Backend conventions / PR gate | `Docs/Architecture/engineering-standards.md` |
| Service-to-service comms | `Docs/Architecture/service-communication.md` |
| Observability | `Docs/Development/observability.md` |
| A specific service's domain | `Docs/Architecture/<service>/*` |
| Product / UX direction | `Docs/Product/*` |
| Why a decision was made | `Docs/Architecture/decisions/*` |
| Backend / mobile / security / DevOps plan | `Docs/Architecture/repository-backend-mobile-architecture-plan.md` |
| Frontend data / mock rules | `Docs/Product/frontend-mock-bff-contract.md` |

## Handling large Markdown files

1. List headings first (search for `^#` lines) instead of opening the whole file.
2. Use `scripts/search-docs.ps1 "<query>"` to get file path + line number + a few lines of context.
3. Open only that section/line range.
