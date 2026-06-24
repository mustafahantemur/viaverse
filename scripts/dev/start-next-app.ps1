[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("web-next", "admin-next")]
    [string] $App,

    [switch] $MockApi
)

$ErrorActionPreference = "Stop"

function Fail($message) {
    Write-Error $message
    exit 1
}

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    Fail "npm was not found. Install Node.js 22 or newer, then reopen VS Code."
}

$appDir = Join-Path $PSScriptRoot "..\..\apps\$App"
$appDir = (Resolve-Path $appDir).Path
$port = if ($App -eq "web-next") { 3000 } else { 3001 }

& "$PSScriptRoot\stop-local-app-ports.ps1" -Ports @($port)

Push-Location $appDir
try {
    $nodeModules = Join-Path $appDir "node_modules"
    $installedLock = Join-Path $nodeModules ".package-lock.json"
    $packageLock = Join-Path $appDir "package-lock.json"

    $needsInstall = -not (Test-Path $nodeModules)
    if (-not $needsInstall -and (Test-Path $packageLock) -and (Test-Path $installedLock)) {
        $needsInstall = (Get-Item $packageLock).LastWriteTimeUtc -gt (Get-Item $installedLock).LastWriteTimeUtc
    }

    if ($needsInstall) {
        Write-Host "Installing npm dependencies for $App..."
        npm install
        if ($LASTEXITCODE -ne 0) {
            Fail "npm install failed for $App."
        }
    }
    else {
        Write-Host "npm dependencies for $App are already installed."
    }

    if ($MockApi) {
        $env:NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL = "http://localhost:8120"
        $env:NEXT_PUBLIC_BFF_BASE_URL = "http://localhost:8120"
        Write-Host "$App is using Mock Web BFF at http://localhost:8120"
    }

    npm run dev
    if ($LASTEXITCODE -ne 0) {
        Fail "npm run dev failed for $App."
    }
}
finally {
    Pop-Location
}
