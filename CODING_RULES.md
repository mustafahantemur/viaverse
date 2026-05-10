# Coding Rules

These rules apply once source code is introduced.

## Build

- Use Gradle Kotlin DSL for root, backend, and mobile builds.
- Keep root orchestration Gradle-first.
- Do not add Maven files.
- Centralize versions through Gradle version catalogs when build files are introduced.

## Backend

- Use Java and Spring Boot.
- Keep service code modular and service-oriented.
- Start with hexagonal boundaries: domain, application, adapters, and infrastructure.
- Use PostgreSQL for transactional persistence.
- Use Flyway for schema migrations.
- Configure Hibernate for schema validation only.
- Expose REST APIs first.

## Mobile

- Use Kotlin Multiplatform and Compose Multiplatform.
- Keep shared client code separate from platform-specific integrations.
- Do not port React implementation code.

## Observability

- Emit structured JSON logs.
- Carry correlation IDs across inbound and outbound calls.
- Add audit logging for security, identity, money, provider, and moderation-sensitive actions when those areas exist.
- Never log secrets, tokens, passwords, or sensitive documents.

## Error Handling

- Prefer typed application errors over stringly typed failures.
- Map validation and domain failures to explicit API responses.
- Keep exception responses consistent across services.

## Scope Control

- No business logic belongs in repository bootstrap tasks.
- No UI screens belong in foundation documentation tasks.
- Add tests in proportion to implementation risk after code exists.

