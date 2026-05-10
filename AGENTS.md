# AGENTS

This file defines repository rules for AI and human contributors.

## Required Before Coding

Use the Viaverse MCP endpoint before implementation work:

1. `resolve_task_context`
2. `get_context_bundle`
3. `pre_coding_brief`

If the MCP endpoint is unreachable, stop and report the connection issue. Do not generate files.

Use compact mode:

- at most 2 bounded contexts
- at most 3 canonical docs
- no long markdown dumps
- filled pre-coding brief
- no empty placeholders

## Product Guardrail

Viaverse is not only a job marketplace. Preserve the future distinction between Dinamik Çevre and Hizmet Al in architecture and product language.

## Architecture Guardrails

- Treat this repository as a greenfield implementation.
- Use [ARCHITECTURE.md](ARCHITECTURE.md) as the primary architecture reference.
- Do not port React prototype code.
- Use prototypes and screenshots only as visual/product references.
- Keep REST as the first API style.
- Allow GraphQL BFF later only for complex read aggregation.
- Use Gradle Kotlin DSL; do not introduce Maven.
- Add business logic only when a task explicitly asks for it.

## Implementation Guardrails

- Keep tasks scoped to the requested bounded contexts.
- Split broad tasks before implementation.
- Avoid hardcoded business role, status, type, capability, payment, and request strings.
- Prefer typed enums, value objects, and explicit contracts when domain code begins.
- Do not introduce auth, profile, marketplace, payment, chat, media, social, provider panel, or UI flows during foundation-only work.

## Validation Posture

Do not add detailed unit tests during the bootstrap. Document future validation commands until modules exist.

