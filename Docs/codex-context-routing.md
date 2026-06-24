# Codex Context Routing

A lightweight, repo-local system that keeps AI coding agents (Codex and others) from loading huge Markdown
files into context during everyday development.

## What it is

A small "guidance + search" layer made of:

- **`AGENTS.md`** (repo root) — tells agents to route context through the skill and read only relevant sections.
- **The `viaverse-context` skill** at [`.agents/skills/viaverse-context/`](../.agents/skills/viaverse-context/):
  - `SKILL.md` — the progressive-disclosure workflow (trigger → domain map → docs routing → search → section read).
  - `references/domain-map.md` — a one-screen map of the project, modules, locations, and build/test commands.
  - `references/docs-routing.md` — which docs are active vs archive, and "read this for that situation".
  - `scripts/search-docs.ps1` — a PowerShell search that returns file path + line number + short context only.

## Why it exists

The repo has many large Markdown files (architecture notes, product/UX plans, the design system, the vendored
UI prototype). Reading them in full wastes context, slows tasks, and buries the few lines that actually matter.
This system makes the default behavior **search-first, section-read-second**.

## How Codex should use it

1. Start any Viaverse task by triggering the `viaverse-context` skill (it is described for auto-discovery).
2. Read the short `references/domain-map.md` to orient.
3. Use `references/docs-routing.md` to pick the single most relevant doc.
4. Run `scripts/search-docs.ps1 "<query>"` to find the exact file + line, with a few lines of context.
5. Open **only** that section — never the whole large file.

`AGENTS.md` encodes these rules so agents pick them up automatically.

## Setup

Repo-specific skills are stored in **`.agents/skills/viaverse-context/`** and are versioned with the repo, so
no global installation is required — **repo-local is the preferred setup for Viaverse.**

- Codex discovers repo skills from **`.agents/skills`**. After cloning or pulling the repo, the skill is
  available as soon as Codex indexes the workspace.
- If your local Codex does not immediately see the skill, **restart or refresh Codex** (reload the workspace)
  after pulling, so it re-scans `.agents/skills`.
- **Global installation is optional** and not required here. If you ever install agent skills globally, keep
  this one repo-local — it is repo-specific and should travel with the codebase.

### Running the search script manually

From the repo root (Windows PowerShell):

```powershell
pwsh -File .agents/skills/viaverse-context/scripts/search-docs.ps1 "outbox"
# or, if pwsh is unavailable:
powershell -ExecutionPolicy Bypass -File .agents\skills\viaverse-context\scripts\search-docs.ps1 "role-based"
```

Useful options:

- `-Context <n>` — lines of surrounding context (default 2).
- `-MaxResults <n>` — cap the number of matches printed (default 50).
- `-Literal` — treat the query as plain text instead of a regex.
- `-Path <dir>` — limit the search to a subfolder (e.g. `Docs/Architecture`).

The script excludes `node_modules`, `.git`, `dist`, `build`, `bin`, `obj`, `.next`, `coverage`, and `.gradle`,
and never prints whole files.

## Maintenance

Keep the two reference files short and current:

- **`references/domain-map.md`** — update when modules move, services are added, or build/test commands change.
- **`references/docs-routing.md`** — update when a doc becomes canonical, is archived, or moves; keep the
  "read this for that situation" table accurate.

Guidelines: one screen each, link don't duplicate, and point to the **canonical** doc for each topic. When a
large doc is added, add a routing entry rather than expecting agents to read it in full.
