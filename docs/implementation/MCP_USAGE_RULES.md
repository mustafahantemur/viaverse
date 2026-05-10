# MCP Usage Rules

Use the Viaverse MCP before implementation tasks.

## Required Calls

1. `resolve_task_context`
2. `get_context_bundle`
3. `pre_coding_brief`

If the endpoint is unreachable, stop and report the connection issue. Do not generate files.

## Compact Mode

- Use at most 2 bounded contexts.
- Use at most 3 canonical docs.
- Avoid long markdown dumps.
- Require a filled pre-coding brief.
- Do not use empty placeholders.

## Scope Rules

- Split broad tasks before coding.
- Do not load marketplace, payment, chat, social, provider panel, profile, auth, media, or UI context unless the task explicitly needs it.
- For foundation tasks, prefer architecture and repository policy context only.

## Bootstrap Result

For this bootstrap, MCP was reachable. The generated files remain repository-level documentation and metadata only.

