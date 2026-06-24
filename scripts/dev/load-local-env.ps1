[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)]
    [string]$EnvFilePath
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $EnvFilePath)) {
    throw "Environment file not found: $EnvFilePath"
}

Write-Host "Loading local environment from $EnvFilePath"
Get-Content $EnvFilePath | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) {
        return
    }

    if ($line -notmatch "^([^=]+)=(.*)$") {
        return
    }

    $name = $matches[1].Trim()
    $value = $matches[2].Trim()

    if ($value.StartsWith('"') -and $value.EndsWith('"')) {
        $value = $value.Substring(1, $value.Length - 2)
    }
    elseif ($value.StartsWith("'") -and $value.EndsWith("'")) {
        $value = $value.Substring(1, $value.Length - 2)
    }

    Set-Item -Path "Env:\$name" -Value $value
}
