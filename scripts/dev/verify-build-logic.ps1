<#
    Preflight check for the build-logic precompiled-script-plugins jar.

    Symptom this fixes: every now and then a previously interrupted Gradle
    build leaves an empty/incomplete `viaverse-build-logic.jar` (no
    Viaverse_*Plugin classes) on disk. Gradle's plugin-classpath transform
    then caches that broken jar by its content hash, and any later
    `bootRun(Debug)` evaluation fails with:

        Could not find implementation class 'Viaverse_<something>Plugin'

    The cure is to delete `build-logic/build` so the jar gets rebuilt
    fresh, which produces a new content hash and forces Gradle to redo
    the transform.

    This script inspects the most recent jar and, if any of the
    expected plugin descriptors point at a missing implementation
    class, stops the daemons and wipes `build-logic/build` so the next
    build starts from a clean slate.
#>

[CmdletBinding()]
param(
    [string] $RepoRoot
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
    $RepoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
}

$buildDir = Join-Path $RepoRoot "build-logic\build"
$jarPath = Join-Path $buildDir "libs\viaverse-build-logic.jar"

# Plugin descriptors we expect inside the jar; each maps to its
# implementation class which Gradle's Java plugin loader will resolve.
$expectedPlugins = @{
    "viaverse.code-quality"             = "Viaverse_codeQualityPlugin.class"
    "viaverse.frontend-shell"           = "Viaverse_frontendShellPlugin.class"
    "viaverse.java-library"             = "Viaverse_javaLibraryPlugin.class"
    "viaverse.java-spring-service"      = "Viaverse_javaSpringServicePlugin.class"
    "viaverse.kotlin-multiplatform-app" = "Viaverse_kotlinMultiplatformAppPlugin.class"
}

function Test-Jar {
    param([string] $Path)
    if (-not (Test-Path $Path)) {
        return $false
    }
    try {
        Add-Type -AssemblyName "System.IO.Compression.FileSystem" -ErrorAction Stop
        $zip = [System.IO.Compression.ZipFile]::OpenRead($Path)
        try {
            $names = @($zip.Entries | ForEach-Object { $_.FullName })
        }
        finally {
            $zip.Dispose()
        }
    }
    catch {
        Write-Host "verify-build-logic: cannot read $Path ($($_.Exception.Message))"
        return $false
    }
    foreach ($impl in $expectedPlugins.Values) {
        if (-not ($names -contains $impl)) {
            Write-Host "verify-build-logic: implementation $impl missing from jar"
            return $false
        }
    }
    return $true
}

if (Test-Jar -Path $jarPath) {
    Write-Host "verify-build-logic: jar is healthy at $jarPath"
    return
}

if (-not (Test-Path $jarPath)) {
    Write-Host "verify-build-logic: jar not yet built; will be produced by the upcoming Gradle run."
    return
}

Write-Host "verify-build-logic: jar at $jarPath is missing one or more plugin classes; cleaning build-logic\build to force a rebuild."

# Stop daemons so files aren't held open by another JVM.
& "$RepoRoot\gradlew.bat" --stop | Out-Null

if (Test-Path $buildDir) {
    Remove-Item -Recurse -Force -ErrorAction SilentlyContinue $buildDir
}
