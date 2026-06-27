#requires -Version 5.1
<#
.SYNOPSIS
    Search Markdown files in the Viaverse repo and print only matching lines with short context.

.DESCRIPTION
    Part of the `viaverse-context` skill. Finds matches across .md files and prints the file path,
    line number, and a few lines of surrounding context. It NEVER prints entire files, and it
    excludes heavy/generated folders.

.PARAMETER Query
    Text or regex to search for. Use -Literal for a plain (non-regex) match.

.PARAMETER Context
    Number of lines of context to show before and after each match. Default 2.

.PARAMETER Path
    Root folder to search. Defaults to the repository root (two levels above this script).

.PARAMETER MaxResults
    Maximum number of matches to print. Default 50.

.PARAMETER Literal
    Treat Query as a literal string instead of a regular expression.

.EXAMPLE
    pwsh -File .agents/skills/viaverse-context/scripts/search-docs.ps1 "outbox"

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .agents\skills\viaverse-context\scripts\search-docs.ps1 "role-based" -Context 3
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string]$Query,

    [int]$Context = 2,

    [string]$Path,

    [int]$MaxResults = 50,

    [switch]$Literal
)

$ErrorActionPreference = 'Stop'

# Default search root = repo root (this script lives in .agents/skills/viaverse-context/scripts).
if ([string]::IsNullOrWhiteSpace($Path)) {
    $Path = Join-Path $PSScriptRoot '..\..\..\..'
}
$root = (Resolve-Path -LiteralPath $Path).Path

$excludeDirs = @('node_modules', '.git', 'dist', 'build', 'bin', 'obj', '.next', 'coverage', '.gradle')

function Format-Line {
    param([string]$Text, [int]$Max = 200)
    if ($null -eq $Text) { return '' }
    $t = $Text.TrimEnd()
    if ($t.Length -gt $Max) { return $t.Substring(0, $Max) + ' ...' }
    return $t
}

# Collect markdown files, skipping excluded folders.
$files = Get-ChildItem -LiteralPath $root -Recurse -File -Filter '*.md' -ErrorAction SilentlyContinue |
    Where-Object {
        $full = $_.FullName
        $skip = $false
        foreach ($d in $excludeDirs) {
            if ($full -match ('[\\/]' + [regex]::Escape($d) + '[\\/]')) { $skip = $true; break }
        }
        -not $skip
    }

if (-not $files) {
    Write-Host "No Markdown files found under $root."
    return
}

Write-Host ""
Write-Host "Searching $($files.Count) Markdown file(s) for: $Query" -ForegroundColor Yellow
Write-Host ("-" * 60)

# Search with context. Select-String uses regex unless -SimpleMatch is set.
if ($Literal) {
    $results = $files | Select-String -Pattern $Query -SimpleMatch -Context $Context, $Context -ErrorAction SilentlyContinue
} else {
    $results = $files | Select-String -Pattern $Query -Context $Context, $Context -ErrorAction SilentlyContinue
}

if (-not $results) {
    Write-Host "No matches. Try a broader query or check references/domain-map.md for the right module."
    return
}

$shown = 0
foreach ($m in $results) {
    if ($shown -ge $MaxResults) { break }

    $rel = $m.Path
    if ($rel.StartsWith($root, [System.StringComparison]::OrdinalIgnoreCase)) {
        $rel = $rel.Substring($root.Length).TrimStart('\', '/')
    }

    Write-Host ""
    Write-Host ("{0}:{1}" -f $rel, $m.LineNumber) -ForegroundColor Cyan
    foreach ($pre in $m.Context.PreContext)  { Write-Host ("    " + (Format-Line $pre)) -ForegroundColor DarkGray }
    Write-Host ("  > " + (Format-Line $m.Line))
    foreach ($post in $m.Context.PostContext) { Write-Host ("    " + (Format-Line $post)) -ForegroundColor DarkGray }

    $shown++
}

Write-Host ""
Write-Host ("-" * 60)
$total = @($results).Count
if ($total -gt $shown) {
    Write-Host "Showing $shown of $total matches (raise -MaxResults to see more). Open only the relevant section."
} else {
    Write-Host "$shown match(es). Open only the relevant section of a file above."
}
