[CmdletBinding()]
param(
    [int[]] $Ports = @(3000, 3001, 8101, 8102, 8103, 8104, 8105, 8106, 8107, 8108, 8109, 8110),
    [switch] $Quiet
)

$ErrorActionPreference = "Stop"

foreach ($port in $Ports) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if (-not $connections) {
        if (-not $Quiet) {
            Write-Host "Port $port is free."
        }
        continue
    }

    $processIds = $connections |
        Select-Object -ExpandProperty OwningProcess -Unique |
        Where-Object { $_ -and $_ -gt 0 }

    foreach ($processId in $processIds) {
        $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
        if (-not $process) {
            continue
        }

        if (-not $Quiet) {
            Write-Host "Stopping process $($process.ProcessName) ($processId) on port $port..."
        }

        Stop-Process -Id $processId -Force
    }
}
