---
name: viaverse-context
description: >
  Find relevant context for Viaverse development tasks without loading large Markdown files into context.
  Use FIRST for any Viaverse task (backend Spring services, Next.js web app, Kotlin mobile, infra, docs).
  Index/search-first workflow: read a short domain map, route via a docs index, search with a script, then
  open only the matching document section. Triggers: "where is", "which doc", "how does Viaverse", working on
  identity/profile/marketplace/content/media/trust services, web-next, mock-web-bff, architecture, or any
  question that would otherwise mean reading big .md files.
---

# Viaverse Context

Lightweight, search-first context routing for the Viaverse monorepo. The goal is to **avoid loading huge
Markdown files into context**. Follow progressive disclosure: stop as soon as you have what the task needs.

## When to use

Use this skill at the **start of any Viaverse development task** before opening architecture/product docs, and
whenever you would otherwise read a large `.md` file to find something.

## Workflow (progressive disclosure)

Do these steps in order. Stop at the first step that gives you enough to proceed.

1. **Trigger from this description.** If the task is clearly scoped to code you already know the location of,
   skip docs entirely and go straight to the code.

2. **Read the short domain map.** Open [`references/domain-map.md`](references/domain-map.md) — a one-screen map
   of what Viaverse is, where frontend/backend/mobile live, key domain concepts, and build/test commands.

3. **Route to the right doc.** Open [`references/docs-routing.md`](references/docs-routing.md) — it lists active
   vs archive docs and a "read this for that situation" table. Identify the single most relevant doc.

4. **Search instead of reading whole files.** Run the search script to find matching files, line numbers, and a
   few lines of surrounding context:

   ```powershell
   pwsh -File .agents/skills/viaverse-context/scripts/search-docs.ps1 "your query"
   # or, if pwsh is unavailable:
   powershell -ExecutionPolicy Bypass -File .agents\skills\viaverse-context\scripts\search-docs.ps1 "your query"
   ```

   The script excludes `node_modules`, `.git`, `dist`, `build`, `bin`, `obj`, `.next`, `coverage` and never
   prints entire files.

5. **Open only the relevant section.** Using the file path + line number from the search (or a heading), read
   **only** that section of the matching document — not the whole file. For large files, navigate by headings
   first.

## Rules

- Never read a large Markdown file in full just to summarize or locate something — search and section-read.
- Prefer the canonical doc named by `docs-routing.md` over similar-looking older notes.
- If nothing matches, broaden the query or check `domain-map.md` for the right module, then search again.

## Maintenance

Keep [`references/domain-map.md`](references/domain-map.md) and
[`references/docs-routing.md`](references/docs-routing.md) short and current when modules or canonical docs
change. See [`Docs/codex-context-routing.md`](../../../Docs/codex-context-routing.md) for the full maintenance
and setup guide.
