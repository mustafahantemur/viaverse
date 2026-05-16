[CmdletBinding()]
param(
    [int] $DebugPort = 5005
)

$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
$envFile = Join-Path $repoRoot ".env.local"

if (Test-Path $envFile) {
    & "$scriptRoot\load-local-env.ps1" -EnvFilePath $envFile
}

& "$scriptRoot\stop-local-app-ports.ps1" -Ports $DebugPort -Quiet

Push-Location $repoRoot
try {
    & "$repoRoot\gradlew.bat" --console=plain :services:identity-service:bootRunDebug "-PdebugPort=$DebugPort"
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
