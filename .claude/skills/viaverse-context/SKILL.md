---
name: viaverse-context
description: >
  Find relevant context for Viaverse development without loading large Markdown files in full. Use FIRST for
  any Viaverse task (Next.js web app, Spring backend, mock-web-bff, mobile, infra, Docs/). Search-first,
  section-read workflow. Triggers: "where is", "which doc", working on feed/akış, services/hizmetler, explore,
  listings, profile, requests, identity/profile/marketplace/content/media services, web-next, mock-web-bff,
  architecture, or any question that would otherwise mean reading big .md files under Docs/.
---

# Viaverse Context (Claude entry point)

This is the Claude-visible entry to the repo-local `viaverse-context` skill. The canonical reference files and
search script live under `.agents/skills/viaverse-context/` so there is **one source of truth** shared with
Codex; this file just makes the workflow available to Claude.

## Workflow (progressive disclosure — stop as soon as you have enough)

1. **Short domain map** — read `.agents/skills/viaverse-context/references/domain-map.md` (what Viaverse is,
   where frontend/backend/mobile live, key concepts, build/test commands).
2. **Route to the right doc** — read `.agents/skills/viaverse-context/references/docs-routing.md` (active vs
   archive docs + a "read this for that situation" table). Pick the single most relevant doc under `Docs/`.
3. **Search instead of reading whole files** — run the PowerShell search to get path + line + short context:
   ```powershell
   pwsh -File .agents/skills/viaverse-context/scripts/search-docs.ps1 "your query"
   # or: powershell -ExecutionPolicy Bypass -File .agents\skills\viaverse-context\scripts\search-docs.ps1 "query"
   ```
   Excludes `node_modules`, `.git`, `dist`, `build`, `bin`, `obj`, `.next`, `coverage`, `.gradle`; never prints
   whole files.
4. **Open only the relevant section** of the matching doc (by heading or the line number from the search) — not
   the whole file.

## Rules

- Never read a large Markdown file in full just to locate something — search and section-read.
- Prefer the canonical doc named by `docs-routing.md`.
- For Viaverse UI work, the product/design guidance lives in `Docs/Product/*` (UX roadmap, role-based-navigation,
  frontend-mock-bff-contract) and `Docs/Viaverse Design System/*`.

## Maintenance

Edit the canonical files under `.agents/skills/viaverse-context/` (not this entry). Full guide:
`Docs/codex-context-routing.md`.
