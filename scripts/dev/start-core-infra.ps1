[CmdletBinding()]
param(
    [switch] $SkipDockerDesktopStart
)

$ErrorActionPreference = "Stop"

$serviceDatabases = @(
    "viaverse_identity",
    "viaverse_profile",
    "viaverse_content",
    "viaverse_marketplace",
    "viaverse_payment",
    "viaverse_messaging",
    "viaverse_media",
    "viaverse_notification",
    "viaverse_search",
    "viaverse_trust_gamification",
    "viaverse_ads_monetization",
    "viaverse_admin_bff",
    "viaverse_web_bff",
    "viaverse_identity_test",
    "viaverse_profile_test",
    "viaverse_content_test",
    "viaverse_marketplace_test",
    "viaverse_payment_test",
    "viaverse_messaging_test",
    "viaverse_media_test",
    "viaverse_notification_test",
    "viaverse_search_test",
    "viaverse_trust_gamification_test",
    "viaverse_ads_monetization_test",
    "viaverse_admin_bff_test",
    "viaverse_web_bff_test"
)

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
        Start-Process -FilePath $dockerDesktop | Out-Null
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

function Wait-ContainerHealthy($containerName, $timeoutSeconds) {
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)
    do {
        $status = docker inspect --format="{{.State.Health.Status}}" $containerName 2>$null
        if ($status -eq "healthy") {
            Write-Host "$containerName is healthy."
            return
        }

        Write-Host "Waiting for $containerName health check..."
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)

    Fail "Timed out waiting for $containerName to become healthy."
}

function Wait-ContainerCompleted($containerName, $timeoutSeconds) {
    $deadline = (Get-Date).AddSeconds($timeoutSeconds)
    do {
        $status = docker inspect --format="{{.State.Status}}" $containerName 2>$null
        $exitCode = docker inspect --format="{{.State.ExitCode}}" $containerName 2>$null
        if ($status -eq "exited" -and $exitCode -eq "0") {
            Write-Host "$containerName completed successfully."
            return
        }

        if ($status -eq "exited" -and $exitCode -ne "0") {
            docker logs $containerName
            Fail "$containerName failed with exit code $exitCode."
        }

        Write-Host "Waiting for $containerName to complete..."
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)

    Fail "Timed out waiting for $containerName to complete."
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

function Get-ContainerEnv($containerName, $name, $fallback) {
    $value = docker exec $containerName printenv $name 2>$null
    if ($LASTEXITCODE -eq 0 -and -not [string]::IsNullOrWhiteSpace($value)) {
        return $value.Trim()
    }

    return $fallback
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

function Invoke-OpenSearchJson($method, $url, $bodyPath, [switch] $AllowConflict) {
    $body = Get-Content -LiteralPath $bodyPath -Raw
    try {
        Invoke-RestMethod -Method $method -Uri $url -ContentType "application/json" -Body $body | Out-Null
    }
    catch {
        $response = $_.Exception.Response
        if ($AllowConflict -and $null -ne $response -and [int]$response.StatusCode -eq 409) {
            Write-Host "OpenSearch resource already exists at $url; keeping existing definition."
            return
        }

        throw
    }
}

function Ensure-DashboardsDataView($dashboardsPort) {
    $findUrl = "http://localhost:$dashboardsPort/api/saved_objects/_find?type=index-pattern&search_fields=title&search=viaverse-logs-*"
    $headers = @{ "osd-xsrf" = "true" }
    $existing = Invoke-RestMethod -Method "GET" -Uri $findUrl -Headers $headers
    if ($existing.total -gt 0) {
        Write-Host "OpenSearch Dashboards data view viaverse-logs-* already exists."
        return
    }

    $body = @{
        attributes = @{
            title = "viaverse-logs-*"
            timeFieldName = "@timestamp"
        }
    } | ConvertTo-Json -Depth 5

    Invoke-RestMethod `
        -Method "POST" `
        -Uri "http://localhost:$dashboardsPort/api/saved_objects/index-pattern/viaverse-logs" `
        -Headers $headers `
        -ContentType "application/json" `
        -Body $body | Out-Null
    Write-Host "Created OpenSearch Dashboards data view viaverse-logs-*."
}

function Ensure-PostgresDatabases {
    $containerName = "viaverse-postgres"
    $postgresUser = Get-ContainerEnv $containerName "POSTGRES_USER" "viaverse"
    $postgresDb = Get-ContainerEnv $containerName "POSTGRES_DB" "viaverse"

    $existing = docker exec $containerName psql -U $postgresUser -d $postgresDb -Atc "SELECT datname FROM pg_database;"
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not read PostgreSQL database list from $containerName."
    }

    foreach ($database in $serviceDatabases) {
        if ($existing -contains $database) {
            continue
        }

        Write-Host "Creating PostgreSQL database $database..."
        docker exec $containerName createdb -U $postgresUser $database
        if ($LASTEXITCODE -ne 0) {
            $check = docker exec $containerName psql -U $postgresUser -d $postgresDb -Atc "SELECT 1 FROM pg_database WHERE datname = '$database';"
            if (($check | Select-Object -First 1) -ne "1") {
                Fail "Could not create PostgreSQL database $database."
            }
        }
    }
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

    docker compose --env-file $envFile --profile observability up -d `
        postgres `
        valkey `
        kafka `
        mailpit `
        seaweedfs `
        seaweedfs-bucket-init `
        opensearch `
        opensearch-dashboards `
        fluent-bit `
        otel-collector `
        prometheus `
        jaeger `
        kafka-ui
    if ($LASTEXITCODE -ne 0) {
        Fail "Docker Compose could not start the core local infrastructure."
    }

    Wait-ContainerHealthy "viaverse-postgres" 120
    Wait-ContainerHealthy "viaverse-seaweedfs" 120
    Wait-ContainerCompleted "viaverse-seaweedfs-bucket-init" 120
    Ensure-PostgresDatabases

    $opensearchPort = Read-EnvValue $envFile "OPENSEARCH_PORT" "9200"
    $dashboardsPort = Read-EnvValue $envFile "OPENSEARCH_DASHBOARDS_PORT" "5601"
    Wait-HttpOk "OpenSearch" "http://localhost:$opensearchPort" 120
    Wait-HttpOk "OpenSearch Dashboards" "http://localhost:$dashboardsPort" 180

    $opensearchConfigDir = Join-Path $composeDir "opensearch"
    Invoke-OpenSearchJson `
        "PUT" `
        "http://localhost:$opensearchPort/_plugins/_ism/policies/viaverse-logs-retention" `
        (Join-Path $opensearchConfigDir "viaverse-logs-retention-policy.json") `
        -AllowConflict
    Invoke-OpenSearchJson `
        "PUT" `
        "http://localhost:$opensearchPort/_index_template/viaverse-logs" `
        (Join-Path $opensearchConfigDir "viaverse-logs-template.json")
    Ensure-DashboardsDataView $dashboardsPort

    Write-Host "Local infrastructure is ready."
    Write-Host "OpenSearch: http://localhost:$opensearchPort"
    Write-Host "OpenSearch Dashboards: http://localhost:$dashboardsPort"
    Write-Host "Prometheus: http://localhost:9090"
    Write-Host "Jaeger: http://localhost:16686"
    Write-Host "OTLP gRPC: localhost:4317"
    Write-Host "OTLP HTTP: http://localhost:4318"
}
finally {
    Pop-Location
}
