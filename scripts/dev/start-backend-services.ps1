[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
$envFile = Join-Path $repoRoot ".env.local"

& "$scriptRoot\start-core-infra.ps1" -SkipDockerDesktopStart
& "$scriptRoot\migrate-local.ps1"
& "$scriptRoot\stop-local-app-ports.ps1" -Ports "8001,8101,8102,8103,8104,8105,8106,8107,8108,8109,8110,8111,8112" -Quiet

if (Test-Path $envFile) {
    & "$scriptRoot\load-local-env.ps1" -EnvFilePath $envFile
}

& "$scriptRoot\verify-build-logic.ps1" -RepoRoot $repoRoot

$bootRunTasks = @(
    ":services:identity-service:bootRun",
    ":services:profile-service:bootRun",
    ":services:content-service:bootRun",
    ":services:marketplace-service:bootRun",
    ":services:payment-service:bootRun",
    ":services:messaging-service:bootRun",
    ":services:media-service:bootRun",
    ":services:notification-service:bootRun",
    ":services:search-service:bootRun",
    ":services:trust-gamification-service:bootRun",
    ":services:ads-monetization-service:bootRun",
    ":services:admin-bff:bootRun",
    ":services:web-bff:bootRun"
)

Push-Location $repoRoot
try {
    & "$repoRoot\gradlew.bat" --console=plain --parallel @bootRunTasks
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
