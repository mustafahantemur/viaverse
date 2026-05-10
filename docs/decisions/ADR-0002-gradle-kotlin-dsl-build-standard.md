# ADR-0002: Gradle Kotlin DSL Build Standard

## Status

Accepted

## Context

Viaverse will include backend and mobile projects. A single build standard is needed before project modules are created.

## Decision

Use Gradle Kotlin DSL for backend, mobile, and root orchestration. Do not introduce Maven.

## Consequences

- Root orchestration remains Gradle-first.
- Future backend and mobile modules can share version catalogs and build conventions.
- Build files should be added only when the first implementation slice needs them.

