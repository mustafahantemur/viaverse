[CmdletBinding()]
param(
    [string] $Endpoint = "http://localhost:6275/mcp",
    [Parameter(Mandatory = $true)]
    [string] $ToolName,
    [string] $ArgumentsJson = "{}"
)

$ErrorActionPreference = "Stop"

function ConvertFrom-McpEvent($content) {
    $dataLines = @()
    foreach ($line in ($content -split "`n")) {
        $trimmed = $line.Trim()
        if ($trimmed.StartsWith("data: ")) {
            $dataLines += $trimmed.Substring(6)
        }
    }

    if ($dataLines.Count -eq 0) {
        return $content | ConvertFrom-Json
    }

    return ($dataLines -join "`n") | ConvertFrom-Json
}

function Invoke-McpJsonRpc($endpoint, $body) {
    $headers = @{
        Accept = "application/json, text/event-stream"
    }

    $response = Invoke-WebRequest `
        -Uri $endpoint `
        -Method POST `
        -Headers $headers `
        -ContentType "application/json" `
        -Body ($body | ConvertTo-Json -Depth 50) `
        -UseBasicParsing

    return ConvertFrom-McpEvent $response.Content
}

$null = Invoke-McpJsonRpc $Endpoint @{
    jsonrpc = "2.0"
    id = 1
    method = "initialize"
    params = @{
        protocolVersion = "2024-11-05"
        capabilities = @{}
        clientInfo = @{
            name = "viaverse-repo-script"
            version = "1.0.0"
        }
    }
}

$arguments = $ArgumentsJson | ConvertFrom-Json -AsHashtable
$result = Invoke-McpJsonRpc $Endpoint @{
    jsonrpc = "2.0"
    id = 2
    method = "tools/call"
    params = @{
        name = $ToolName
        arguments = $arguments
    }
}

if ($result.error) {
    Write-Error ($result.error | ConvertTo-Json -Depth 20)
    exit 1
}

$result.result.content | ForEach-Object {
    if ($_.type -eq "text") {
        $_.text
    }
    else {
        $_ | ConvertTo-Json -Depth 20
    }
}
