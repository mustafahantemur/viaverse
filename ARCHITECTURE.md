# Architecture

This document is the primary architecture reference for Viaverse until more detailed service docs exist.

## Direction

Viaverse is a greenfield, Gradle-first platform with a Java Spring Boot backend and Kotlin Multiplatform Compose mobile client. The repository starts with documentation, operating rules, build orchestration, and technical shells before business source code.

React templates and prototype screenshots are reference material only. They must not drive architecture or be ported into the production implementation.

## Product Boundary

Viaverse must preserve two future product surfaces:

- Dinamik Çevre: context-aware discovery and local environment experiences.
- Hizmet Al: intentional service request and fulfillment experiences.

The bootstrap does not implement either flow.

## Repository Shape

The current shape is:

```text
services/
  identity-service/
  marketplace-service/
  payment-service/
  messaging-service/
  media-service/
  notification-service/
  search-service/
  trust-gamification-service/
  ads-monetization-service/
  admin-bff/
apps/
  mobile-kmp/
  web-next/
  admin-next/
packages/
  api-contracts/
  shared-kernel/
  observability/
docs/
  decisions/
  implementation/
```

Backend service modules currently contain technical Spring Boot shells only.

## Backend Baseline

- Language: Java.
- Framework: Spring Boot.
- Architecture style: service-oriented with hexagonal module boundaries.
- API style: REST first.
- GraphQL: allowed later only for complex read aggregation through a BFF.
- Messaging: Kafka through Spring Cloud Stream later.
- Database: PostgreSQL as the transactional source of truth.
- Migrations: Flyway.
- ORM validation: Hibernate `validate`; no runtime schema generation.

## Mobile Baseline

- Kotlin Multiplatform for shared code.
- Compose Multiplatform for UI when UI work begins.
- Gradle Kotlin DSL for all mobile build definitions.
- No React code porting.

## Observability And Operations

- Structured JSON logging is the default.
- Correlation IDs are required for request tracing.
- Audit logging is required for sensitive business and security events once those flows exist.
- Exception handling must be consistent, typed, and safe to expose.

## Out Of Scope For Bootstrap

Auth, profile, marketplace logic, payment, chat, media upload logic, social feed, provider panel, UI screens, and detailed unit tests are intentionally not implemented here.
