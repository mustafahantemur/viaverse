# AGENTS.md — Viaverse

Guidance for AI coding agents (Codex and others) working in this repository.

## Context routing (read this first)

This repo has many large Markdown files (architecture notes, product plans, design system). Reading them in
full bloats context and slows tasks. Instead:

1. **Use the repo-specific `viaverse-context` skill first.** It lives at
   [`.agents/skills/viaverse-context/`](.agents/skills/viaverse-context/) and routes you to the right place
   without loading whole documents.
2. **Do not read large Markdown files in full.** First inspect file lists and headings, then open **only the
   section** directly relevant to the current task.
3. **Search before reading.** Use the skill's search script
   (`.agents/skills/viaverse-context/scripts/search-docs.ps1`) to find matching files, line numbers, and short
   surrounding context — never dump entire files.

See [`Docs/codex-context-routing.md`](Docs/codex-context-routing.md) for the full explanation, setup, and
maintenance workflow.

## Quick orientation

- **Short project/domain map:** [`.agents/skills/viaverse-context/references/domain-map.md`](.agents/skills/viaverse-context/references/domain-map.md)
- **Which docs to read when:** [`.agents/skills/viaverse-context/references/docs-routing.md`](.agents/skills/viaverse-context/references/docs-routing.md)
- **Repo map + run/build commands:** [`README.md`](README.md)

## Working rules

- Do not delete or move existing files unless the task explicitly asks for it.
- Keep changes small and safe; match existing structure and conventions.
- Backend is **Java 25 + Spring Boot** (hexagonal). Frontend is **Next.js** (`apps/web-next`, `apps/admin-next`).
  Mobile is **Kotlin** (`apps/mobile-android`, `apps/mobile-kmp`).
- Prefer the canonical docs over guessing; the routing skill tells you which one is canonical.
