[CmdletBinding()]
param(
    [string[]] $Ports = @("3000", "3001", "8101", "8102", "8103", "8104", "8105", "8106", "8107", "8108", "8109", "8110", "8111"),
    [switch] $Quiet
)

$ErrorActionPreference = "Stop"

$normalizedPorts = $Ports |
    ForEach-Object { $_ -split "," } |
    ForEach-Object { $_.Trim() } |
    Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
    ForEach-Object {
        $parsed = 0
        if (-not [int]::TryParse($_, [ref]$parsed) -or $parsed -lt 1 -or $parsed -gt 65535) {
            throw "Invalid port value: $_"
        }
        $parsed
    }

foreach ($port in $normalizedPorts) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    $processIds = @(
        $connections |
            Select-Object -ExpandProperty OwningProcess -Unique |
            Where-Object { $_ -and $_ -gt 0 }
    )

    if (-not $processIds) {
        $netstatPattern = "^\s*TCP\s+\S+:$port\s+\S+\s+LISTENING\s+(\d+)\s*$"
        $processIds = @(
            netstat -ano |
                ForEach-Object {
                    if ($_ -match $netstatPattern) {
                        [int] $Matches[1]
                    }
                } |
                Where-Object { $_ -and $_ -gt 0 } |
                Select-Object -Unique
        )
    }

    if (-not $processIds) {
        if (-not $Quiet) {
            Write-Host "Port $port is free."
        }
        continue
    }

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
