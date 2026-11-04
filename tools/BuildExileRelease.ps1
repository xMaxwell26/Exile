param([string]$Version='0.1.0',[string]$HostAddress='127.0.0.1',[int]$Port=43594,[string]$Output='release')
$ErrorActionPreference='Stop';$root=Split-Path -Parent $PSScriptRoot;$client=Join-Path $root 'Notorious\Client';$out=Join-Path $root $Output;$release=Join-Path $out $Version
New-Item -ItemType Directory -Force $release | Out-Null
& (Join-Path $root 'dev.ps1') Build -JdkPath 'C:\Program Files\Java\jdk1.8.0_261' -ServerJdkPath 'C:\Program Files\Java\jdk-21'
$jar=Join-Path $release 'client.jar';if(Test-Path $jar){Remove-Item $jar -Force};& jar cf $jar -C (Join-Path $client 'build\classes') .
$sha=(Get-FileHash $jar -Algorithm SHA256).Hash.ToLowerInvariant();$zip=Join-Path $out ("exile-"+$Version+".zip");if(Test-Path $zip){Remove-Item $zip -Force};& tar -a -c -f $zip -C $release client.jar
$manifest=@{version=$Version;url='REPLACE_WITH_HOSTED_ZIP_URL';sha256=$sha;host=$HostAddress;port=$Port}|ConvertTo-Json -Compress;Set-Content (Join-Path $out 'manifest.json') $manifest
$launcherOut=Join-Path $out 'ExileLauncher.jar';$launcherClasses=Join-Path $out 'launcher-classes';New-Item -ItemType Directory -Force $launcherClasses|Out-Null;$sources=Get-ChildItem (Join-Path $root 'Launcher\src') -Recurse -Filter *.java|ForEach-Object FullName;& 'C:\Program Files\Java\jdk1.8.0_261\bin\javac.exe' -source 8 -target 8 -d $launcherClasses $sources;& 'C:\Program Files\Java\jdk1.8.0_261\bin\jar.exe' cfe $launcherOut com.exile.launcher.ExileLauncher -C $launcherClasses .
if(Test-Path (Join-Path $PSScriptRoot 'proguard.jar')){Write-Host 'Optional ProGuard detected; use tools\proguard-release.pro for final obfuscation.'}else{Write-Warning 'Packaged and hashed release. Add tools\proguard.jar to enable optional obfuscation.'}
Write-Host "Release: $zip`nSHA256: $sha`nUpload the ZIP and manifest.json, then replace REPLACE_WITH_HOSTED_ZIP_URL."
