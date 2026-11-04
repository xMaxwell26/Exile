param([string]$JdkPath = 'C:\Program Files\Java\jdk-21')
$ErrorActionPreference = 'Stop'
$workspace = Split-Path $PSScriptRoot -Parent
$serverDirectory = Join-Path $workspace 'Notorious\Server'
$compiledServer = Join-Path $serverDirectory 'build\classes'
if (!(Test-Path (Join-Path $compiledServer 'com\koios\game\arpg\MapDropTables.class'))) {
    throw 'Run .\dev.ps1 Build first. The validator uses the current compiled content loaders.'
}
# Compile into a separate tool directory: validation never overwrites the running game classes.
$toolClasses = Join-Path $serverDirectory 'build\content-validator'
New-Item -ItemType Directory -Force $toolClasses | Out-Null
Push-Location $serverDirectory
try {
    & (Join-Path $JdkPath 'bin\javac.exe') --release 8 -cp 'build/classes;lib/*' -d $toolClasses 'test/ValidateContent.java'
    if ($LASTEXITCODE -ne 0) { throw 'Content validator compilation failed.' }
    & (Join-Path $JdkPath 'bin\java.exe') -cp "$toolClasses;build/classes;lib/*" ValidateContent
    if ($LASTEXITCODE -ne 0) { throw 'Content validation failed. Fix the reported data before restarting.' }
} finally {
    Pop-Location
}
