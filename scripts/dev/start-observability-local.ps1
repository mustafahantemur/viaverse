[CmdletBinding()]
param(
    [switch] $SkipDockerDesktopStart
)

$ErrorActionPreference = "Stop"

function Fail($message) {
    Write-Error $message
    exit 1
}

function Ensure-Command($name, $hint) {
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        Fail "$name was not found. $hint"
    }
}

function Test-DockerReady {
    docker info *> $null
    return $LASTEXITCODE -eq 0
}

function Start-DockerDesktopIfPossible {
    if ($SkipDockerDesktopStart) {
        return
    }

    $dockerDesktop = Join-Path $env:ProgramFiles "Docker\Docker\Docker Desktop.exe"
    if (Test-Path $dockerDesktop) {
        Write-Host "Starting Docker Desktop..."
        Start-Process -FilePath $dockerDesktop -WindowStyle Hidden | Out-Null
    }
}

function Wait-DockerReady {
    if (Test-DockerReady) {
        return
    }

    Start-DockerDesktopIfPossible

    $deadline = (Get-Date).AddSeconds(150)
    do {
        if (Test-DockerReady) {
            Write-Host "Docker daemon is ready."
            return
        }

        Write-Host "Waiting for Docker daemon..."
        Start-Sleep -Seconds 3
    } while ((Get-Date) -lt $deadline)

    Fail "Docker daemon is not running. Start Docker Desktop and try again."
}

function Wait-HttpOk($name, $url, $timeoutSeconds) {
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)
    do {
        try {
            $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                Write-Host "$name is reachable at $url."
                return
            }
        }
        catch {
            Write-Host "Waiting for $name..."
            Start-Sleep -Seconds 3
        }
    } while ((Get-Date) -lt $deadline)

    Fail "Timed out waiting for $name at $url."
}

function Read-EnvValue($path, $name, $fallback) {
    if (-not (Test-Path $path)) {
        return $fallback
    }

    $line = Get-Content -LiteralPath $path |
        Where-Object { $_ -match "^\s*$([regex]::Escape($name))=" } |
        Select-Object -First 1
    if ($line) {
        return ($line -replace "^\s*$([regex]::Escape($name))=", "").Trim()
    }

    return $fallback
}

function Invoke-OpenSearchJson($method, $url, $bodyPath) {
    $body = Get-Content -LiteralPath $bodyPath -Raw
    Invoke-RestMethod -Method $method -Uri $url -ContentType "application/json" -Body $body | Out-Null
}

Ensure-Command "docker" "Install Docker Desktop with Compose v2."
Wait-DockerReady

$composeDir = Join-Path $PSScriptRoot "..\..\infra\docker-compose"
$composeDir = (Resolve-Path $composeDir).Path

Push-Location $composeDir
try {
    $rootEnvFile = Join-Path $composeDir "..\..\.env.local"
    if (Test-Path $rootEnvFile) {
        Write-Host "Using local environment file: $rootEnvFile"
        $envFile = $rootEnvFile
    }
    else {
        $envFile = Join-Path $composeDir ".env.example"
        Write-Host "Using example env file: $envFile"
    }

    docker compose --env-file $envFile --profile observability up -d opensearch opensearch-dashboards fluent-bit otel-collector
    if ($LASTEXITCODE -ne 0) {
        Fail "Docker Compose could not start the local observability stack."
    }

    $opensearchPort = Read-EnvValue $envFile "OPENSEARCH_PORT" "9200"
    $dashboardsPort = Read-EnvValue $envFile "OPENSEARCH_DASHBOARDS_PORT" "5601"
    Wait-HttpOk "OpenSearch" "http://localhost:$opensearchPort" 120
    Wait-HttpOk "OpenSearch Dashboards" "http://localhost:$dashboardsPort" 180

    $opensearchConfigDir = Join-Path $composeDir "opensearch"
    Invoke-OpenSearchJson `
        "PUT" `
        "http://localhost:$opensearchPort/_plugins/_ism/policies/viaverse-logs-retention" `
        (Join-Path $opensearchConfigDir "viaverse-logs-retention-policy.json")
    Invoke-OpenSearchJson `
        "PUT" `
        "http://localhost:$opensearchPort/_index_template/viaverse-logs" `
        (Join-Path $opensearchConfigDir "viaverse-logs-template.json")

    Write-Host "Local observability stack is ready."
    Write-Host "OpenSearch: http://localhost:$opensearchPort"
    Write-Host "OpenSearch Dashboards: http://localhost:$dashboardsPort"
    Write-Host "OTLP gRPC: localhost:4317"
    Write-Host "OTLP HTTP: http://localhost:4318"
}
finally {
    Pop-Location
}
