[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

function Fail($message) {
    Write-Error $message
    exit 1
}

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    Fail "kubectl was not found. Install kubectl and try again."
}

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")

Push-Location $repoRoot
try {
    kubectl delete -k infra/kubernetes/base --ignore-not-found
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not delete Kubernetes base manifests."
    }

    Write-Host "Local Kubernetes resources are stopped."
}
finally {
    Pop-Location
}
