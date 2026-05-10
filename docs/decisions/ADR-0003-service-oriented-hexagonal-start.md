# ADR-0003: Service-Oriented Hexagonal Start

## Status

Accepted

## Context

The platform needs room for transactional services, mobile clients, and later integration points without coupling business rules to framework code.

## Decision

Start backend services with service-oriented boundaries and hexagonal structure. Keep domain logic independent from Spring, persistence, messaging, and transport adapters.

## Consequences

- REST controllers, persistence, messaging, and external clients remain adapters.
- Application services coordinate use cases when business logic begins.
- Domain rules can be tested without framework bootstrapping once code exists.

