[CmdletBinding()]
param(
    [string]$EnvFilePath
)

$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
$defaultEnvFile = Join-Path $repoRoot ".env.local"

if (-not $EnvFilePath) {
    $EnvFilePath = $defaultEnvFile
}

if (-not (Test-Path $EnvFilePath)) {
    throw "Local environment file not found: $EnvFilePath"
}

& "$scriptRoot\load-local-env.ps1" -EnvFilePath $EnvFilePath
if (-not $?) {
    throw "Failed to load local environment file: $EnvFilePath"
}

Push-Location $repoRoot
try {
    Write-Host "Executing Gradle migrateLocal with loaded local environment"
    & "$repoRoot\gradlew.bat" migrateLocal
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle migrateLocal failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}
