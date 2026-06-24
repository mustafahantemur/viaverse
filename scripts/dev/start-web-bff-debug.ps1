[CmdletBinding()]
param(
    [int] $DebugPort = 5006
)

$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
$envFile = Join-Path $repoRoot ".env.local"

if (Test-Path $envFile) {
    & "$scriptRoot\load-local-env.ps1" -EnvFilePath $envFile
}

& "$scriptRoot\stop-local-app-ports.ps1" -Ports $DebugPort -Quiet

# Self-heal: the build-logic plugin jar occasionally caches as empty
# (incomplete interrupted build), which then breaks every dependent
# subproject. The preflight cleans build-logic/build when that's the case.
& "$scriptRoot\verify-build-logic.ps1" -RepoRoot $repoRoot

Push-Location $repoRoot
try {
    & "$repoRoot\gradlew.bat" --console=plain :services:web-bff:bootRunDebug "-PdebugPort=$DebugPort"
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
