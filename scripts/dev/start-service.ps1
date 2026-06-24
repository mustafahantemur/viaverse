[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string] $GradlePath,

    [Parameter(Mandatory = $true)]
    [string] $Ports,

    [switch] $SkipInfra,

    [switch] $SkipMigrations
)

$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
$envFile = Join-Path $repoRoot ".env.local"

if (-not $SkipInfra) {
    & "$scriptRoot\start-core-infra.ps1" -SkipDockerDesktopStart
}

if (-not $SkipMigrations) {
    & "$scriptRoot\migrate-local.ps1"
}

& "$scriptRoot\stop-local-app-ports.ps1" -Ports $Ports -Quiet

if (Test-Path $envFile) {
    & "$scriptRoot\load-local-env.ps1" -EnvFilePath $envFile
}

& "$scriptRoot\verify-build-logic.ps1" -RepoRoot $repoRoot

Push-Location $repoRoot
try {
    & "$repoRoot\gradlew.bat" --console=plain "${GradlePath}:bootRun"
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
