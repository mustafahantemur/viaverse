# ADR-0004: Observability, Audit, And Exception Baseline

## Status

Accepted

## Context

Viaverse will eventually handle identity, service requests, provider workflows, payment-sensitive actions, and moderation-sensitive events.

## Decision

Adopt structured JSON logging, correlation IDs, audit logging, and consistent exception handling as platform baselines.

## Consequences

- Logs must be machine-readable and safe.
- Correlation IDs must travel through request handling and outbound calls.
- Sensitive events must produce audit records once those flows exist.
- Public errors must be consistent and must not expose secrets or internals.

