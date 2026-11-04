param([ValidateRange(1,2)][int]$Count = 2, [ValidatePattern('^qa[a-z0-9]{3,9}$')][string[]]$Characters = @('qaember','qathorn'), [switch]$Maps, [switch]$Fullscreen, [switch]$Capture)
$ErrorActionPreference = 'Stop'
$workspace = Split-Path $PSScriptRoot -Parent
$gameRoot = Join-Path $workspace 'Notorious'
$clientDirectory = Join-Path $gameRoot 'Client'
$javaExecutable = 'C:\Program Files\Java\jdk1.8.0_261\bin\java.exe'
$logDirectory = Join-Path $clientDirectory 'build\qa'
New-Item -ItemType Directory -Force $logDirectory | Out-Null
for ($clientIndex = 0; $clientIndex -lt [Math]::Min($Count,$Characters.Count); $clientIndex++) {
    $arguments = @(
        '"-Duser.home=' + $gameRoot + '"',
        '-Darpg.qaCharacter=' + $characters[$clientIndex],
        '-Darpg.qaOffset=' + ($clientIndex * 790),
        '-Xmx1G',
        '-cp', 'build/classes', 'com.koios.client.Client'
    )
    if ($Maps -and $characters[$clientIndex] -eq 'qathorn') { $arguments = @('-Darpg.qaMaps=true') + $arguments }
    if ($Fullscreen -and $characters[$clientIndex] -eq 'qathorn') { $arguments = @('-Darpg.qaFullscreen=true') + $arguments }
    if ($Capture) { $arguments = @('-Darpg.qaCapture=true') + $arguments }
    $stdout = Join-Path $logDirectory ($characters[$clientIndex] + '.stdout.log')
    $stderr = Join-Path $logDirectory ($characters[$clientIndex] + '.stderr.log')
    $process = Start-Process -FilePath $javaExecutable -WorkingDirectory $clientDirectory -ArgumentList $arguments -WindowStyle Hidden -RedirectStandardOutput $stdout -RedirectStandardError $stderr -PassThru
    Write-Output ($characters[$clientIndex] + ' PID ' + $process.Id)
    if ($clientIndex + 1 -lt $Count) { Start-Sleep -Seconds 2 }
}
