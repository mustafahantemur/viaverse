$ErrorActionPreference = "Stop"

$repoRoot = Join-Path $PSScriptRoot "..\.."
$repoRoot = (Resolve-Path $repoRoot).Path
$gradlew = Join-Path $repoRoot "gradlew.bat"

Push-Location $repoRoot
try {
    & $gradlew ":apps:mobile-kmp:desktopRun"
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Mobile desktop run failed."
        exit 1
    }
}
finally {
    Pop-Location
}
