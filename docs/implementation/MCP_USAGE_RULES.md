# MCP Usage Rules

Use the Viaverse MCP before implementation tasks.

## Required Calls

1. `resolve_task_context`
2. `get_context_bundle`
3. `pre_coding_brief`

If the endpoint is unreachable, stop and report the connection issue. Do not generate files.

## HTTP Fallback

If the agent runtime does not expose MCP tools directly, but the local Viaverse MCP endpoint is running, use the committed HTTP client:

```powershell
./scripts/dev/invoke-viaverse-mcp.ps1 -ToolName resolve_task_context -ArgumentsJson '{"task":"Update local infrastructure"}'
./scripts/dev/invoke-viaverse-mcp.ps1 -ToolName get_context_bundle -ArgumentsJson '{"boundedContext":"foundation","task":"Update local infrastructure","limit":5}'
./scripts/dev/invoke-viaverse-mcp.ps1 -ToolName pre_coding_brief -ArgumentsJson '{"boundedContext":"foundation","task":"Update local infrastructure"}'
```

The script targets `http://localhost:6275/mcp`, initializes JSON-RPC MCP, and sends `Accept: application/json, text/event-stream`. This matches the local endpoint behavior and avoids false "MCP unreachable" reports when only the direct tool bridge is missing.

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
