[CmdletBinding()]
param(
    [string] $AvdName = ""
)

$ErrorActionPreference = "Stop"

function Fail($message) {
    Write-Error $message
    exit 1
}

function Find-AndroidTool($relativePath) {
    $roots = @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
    foreach ($root in $roots) {
        $candidate = Join-Path $root $relativePath
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    return $null
}

function Wait-AndroidDevice($adb) {
    $deadline = (Get-Date).AddSeconds(150)
    do {
        $devices = & $adb devices
        $ready = $devices | Where-Object { $_ -match "\sdevice$" -and $_ -notmatch "^List of devices" }
        if ($ready) {
            Write-Host "Android device is ready."
            return
        }

        Write-Host "Waiting for Android device..."
        Start-Sleep -Seconds 3
    } while ((Get-Date) -lt $deadline)

    Fail "No Android emulator/device became ready in time."
}

$adb = Find-AndroidTool "platform-tools\adb.exe"
$emulator = Find-AndroidTool "emulator\emulator.exe"

if (-not $adb) {
    Fail "adb.exe was not found. Install Android Studio, install Android SDK Platform Tools, and set ANDROID_HOME or ANDROID_SDK_ROOT."
}

$devices = & $adb devices
$hasDevice = $devices | Where-Object { $_ -match "\sdevice$" -and $_ -notmatch "^List of devices" }

if (-not $hasDevice) {
    if (-not $emulator) {
        Fail "No Android device is connected and emulator.exe was not found. Start an emulator from Android Studio."
    }

    $avds = & $emulator -list-avds
    if (-not $avds) {
        Fail "No Android Virtual Device exists. Create one in Android Studio Device Manager."
    }

    if ([string]::IsNullOrWhiteSpace($AvdName)) {
        $AvdName = ($avds | Select-Object -First 1).Trim()
    }

    Write-Host "Starting Android emulator $AvdName..."
    Start-Process -FilePath $emulator -ArgumentList @("-avd", $AvdName) | Out-Null
}

Wait-AndroidDevice $adb

$repoRoot = Join-Path $PSScriptRoot "..\.."
$repoRoot = (Resolve-Path $repoRoot).Path
$gradlew = Join-Path $repoRoot "gradlew.bat"

Push-Location $repoRoot
try {
    $tasks = & $gradlew ":apps:mobile-android:tasks" "--all" "--quiet"
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not read mobile Gradle tasks."
    }

    if (-not ($tasks | Select-String -Pattern "installDebug")) {
        Fail "The Android app module does not expose installDebug."
    }

    & $gradlew ":apps:mobile-android:installDebug"
    if ($LASTEXITCODE -ne 0) {
        Fail "Android APK install failed."
    }

    & $adb shell am start -n "app.viaverse.mobile/.MainActivity"
    if ($LASTEXITCODE -ne 0) {
        Fail "Android app launch failed."
    }
}
finally {
    Pop-Location
}
