param([int]$Port = 43594, [string]$HostAddress = '127.0.0.1')
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$server = Join-Path $root 'Notorious\Server'
$client = Join-Path $root 'Notorious\Client'
$java = Get-ChildItem 'C:\Program Files\Java' -Directory | Where-Object { $_.Name -like 'jdk1.8*' } | Sort-Object Name -Descending | Select-Object -First 1
if (!$java) { throw 'Java 8 JDK was not found.' }
$javaExe = Join-Path $java.FullName 'bin\java.exe'
$serverProcess = $null
function Find-ExileServerProcess { Get-CimInstance Win32_Process -ErrorAction SilentlyContinue | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -match 'com\.koios\.Bootstrap' -and $_.CommandLine -match [regex]::Escape($root) } | Select-Object -First 1 }

Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing
$form = New-Object Windows.Forms.Form
$form.Text = 'Exile Server Admin'
$form.Size = New-Object Drawing.Size(650,520)
$form.StartPosition = 'CenterScreen'
$form.BackColor = [Drawing.Color]::FromArgb(24,20,24)
$form.ForeColor = [Drawing.Color]::Gainsboro
$font = New-Object Drawing.Font('Segoe UI',10)

function Button($text,$x,$y,$action) { $b=New-Object Windows.Forms.Button; $b.Text=$text; $b.Location=New-Object Drawing.Point($x,$y); $b.Size=New-Object Drawing.Size(130,34); $b.Add_Click($action); $form.Controls.Add($b); return $b }
$title=New-Object Windows.Forms.Label; $title.Text='EXILE  /  SERVER CONTROL'; $title.Font=New-Object Drawing.Font('Segoe UI Semibold',15); $title.Location=New-Object Drawing.Point(18,16); $title.AutoSize=$true; $form.Controls.Add($title)
$status=New-Object Windows.Forms.Label; $status.Text='Stopped'; $status.Location=New-Object Drawing.Point(20,58); $status.AutoSize=$true; $form.Controls.Add($status)
$logFile=Join-Path $root 'logs\admin-server.log';New-Item (Split-Path $logFile) -ItemType Directory -Force|Out-Null
$log=New-Object Windows.Forms.TextBox; $log.Multiline=$true; $log.ReadOnly=$true; $log.ScrollBars='Vertical'; $log.Location=New-Object Drawing.Point(20,350); $log.Size=New-Object Drawing.Size(600,120); $log.BackColor=[Drawing.Color]::FromArgb(12,12,16); $log.ForeColor=[Drawing.Color]::LightGray; $form.Controls.Add($log)
$hostLabel=New-Object Windows.Forms.Label; $hostLabel.Text='Bind host / client address'; $hostLabel.Location=New-Object Drawing.Point(20,88); $hostLabel.AutoSize=$true; $form.Controls.Add($hostLabel)
$hostBox=New-Object Windows.Forms.TextBox; $hostBox.Text=$HostAddress; $hostBox.Location=New-Object Drawing.Point(185,84); $hostBox.Size=New-Object Drawing.Size(170,24); $form.Controls.Add($hostBox)
$external=New-Object Windows.Forms.CheckBox; $external.Text='External / beta'; $external.Location=New-Object Drawing.Point(365,84); $external.AutoSize=$true; $external.Add_CheckedChanged({ if($external.Checked){$hostBox.Text='0.0.0.0'}else{$hostBox.Text='127.0.0.1'} }); $form.Controls.Add($external)
$online=New-Object Windows.Forms.CheckBox; $online.Text='Online / SQL'; $online.Checked=$false; $online.Location=New-Object Drawing.Point(365,116); $online.AutoSize=$true; $form.Controls.Add($online)
$portLabel=New-Object Windows.Forms.Label; $portLabel.Text='Game port'; $portLabel.Location=New-Object Drawing.Point(20,120); $portLabel.AutoSize=$true; $form.Controls.Add($portLabel)
$portBox=New-Object Windows.Forms.TextBox; $portBox.Text=[string]$Port; $portBox.Location=New-Object Drawing.Point(185,116); $portBox.Size=New-Object Drawing.Size(90,24); $form.Controls.Add($portBox)
$clientLabel=New-Object Windows.Forms.Label; $clientLabel.Text='Test client address'; $clientLabel.Location=New-Object Drawing.Point(20,150); $clientLabel.AutoSize=$true; $form.Controls.Add($clientLabel)
$clientHostBox=New-Object Windows.Forms.TextBox; $clientHostBox.Text='127.0.0.1'; $clientHostBox.Location=New-Object Drawing.Point(185,146); $clientHostBox.Size=New-Object Drawing.Size(170,24); $form.Controls.Add($clientHostBox)
$localTest=New-Object Windows.Forms.CheckBox; $localTest.Text='Local test'; $localTest.Checked=$true; $localTest.Location=New-Object Drawing.Point(365,146); $localTest.AutoSize=$true; $form.Controls.Add($localTest)
$public=New-Object Windows.Forms.Label; $public.Text='External address: checking...'; $public.Location=New-Object Drawing.Point(365,145); $public.AutoSize=$true; $form.Controls.Add($public)
$buildLabel=New-Object Windows.Forms.Label; $buildLabel.Text='Release version'; $buildLabel.Location=New-Object Drawing.Point(20,220); $buildLabel.AutoSize=$true; $form.Controls.Add($buildLabel)
$versionBox=New-Object Windows.Forms.TextBox; $versionBox.Text='0.1.0'; $versionBox.Location=New-Object Drawing.Point(125,216); $versionBox.Size=New-Object Drawing.Size(80,24); $form.Controls.Add($versionBox)
$manifestLabel=New-Object Windows.Forms.Label; $manifestLabel.Text='ZIP download URL'; $manifestLabel.Location=New-Object Drawing.Point(225,220); $manifestLabel.AutoSize=$true; $form.Controls.Add($manifestLabel)
$manifestBox=New-Object Windows.Forms.TextBox; $manifestBox.Text='https://'; $manifestBox.Location=New-Object Drawing.Point(310,216); $manifestBox.Size=New-Object Drawing.Size(310,24); $form.Controls.Add($manifestBox)
$obfuscate=New-Object Windows.Forms.CheckBox; $obfuscate.Text='Obfuscate release'; $obfuscate.Location=New-Object Drawing.Point(20,252); $obfuscate.AutoSize=$true; $form.Controls.Add($obfuscate)
try { $script:externalIp=(Invoke-RestMethod -Uri 'https://api.ipify.org' -TimeoutSec 4).ToString().Trim(); $public.Text="External address: $externalIp" } catch { $script:externalIp=''; $public.Text='External address: unavailable (offline)' }
$existing=Find-ExileServerProcess;if($existing){$status.Text="Already running (PID $($existing.ProcessId))";$log.AppendText("Existing Exile server detected (PID $($existing.ProcessId)); Start will not launch a duplicate.`r`n")}
$start=Button 'Start server' 20 175 { $existing=Find-ExileServerProcess; if($existing){$status.Text="Already running (PID $($existing.ProcessId))";$log.AppendText("Start skipped: Exile server PID $($existing.ProcessId) is already running.`r`n")}else{ $bind=$hostBox.Text.Trim();$gamePort=[int]$portBox.Text;$mode=if($online.Checked){'true'}else{'false'};$errLog=Join-Path $root 'logs\admin-server-error.log';Add-Content $logFile ("--- start $(Get-Date -Format s) host=$bind port=$gamePort online=$mode ---");$args=@('-Duser.home='+$root+'\Notorious','-Xmx1G','-cp','build/classes;lib/*','com.koios.Bootstrap','--host',$bind,'--port',$gamePort,'--onlinemode',$mode);$script:serverProcess=Start-Process -FilePath $javaExe -WorkingDirectory $server -ArgumentList $args -PassThru -WindowStyle Hidden -RedirectStandardOutput $logFile -RedirectStandardError $errLog;$status.Text="Starting (PID $($script:serverProcess.Id))";$log.AppendText("Detached server launched. Panel remains open.`r`n") } }
$stop=Button 'Stop server' 165 175 { $target=if($script:serverProcess -and !$script:serverProcess.HasExited){$script:serverProcess.Id}else{(Find-ExileServerProcess).ProcessId}; if($target){Stop-Process -Id $target -Force; $status.Text='Stopped'; $log.AppendText("Server stopped (PID $target)`r`n")} }
$connect=Button 'Launch client' 310 175 { $connectHost=if($localTest.Checked){'127.0.0.1'}else{$clientHostBox.Text.Trim()}; if(!$connectHost){$connectHost='127.0.0.1'}; $gamePort=[int]$portBox.Text; Start-Process -FilePath $javaExe -WorkingDirectory $client -ArgumentList @('-Duser.home='+$root+'\Notorious','-Darpg.serverHost='+$connectHost,'-Darpg.serverPort='+$gamePort,'-Xmx1G','-cp','build/classes','com.koios.client.Client'); $log.AppendText("Client launched for $connectHost`:$gamePort`r`n") }
$build=Button 'Build client release' 20 285 { $args=@('-ExecutionPolicy','Bypass','-File',(Join-Path $root 'tools\BuildExileRelease.ps1'),'-Version',$versionBox.Text,'-HostAddress',$hostBox.Text,'-Port',[int]$portBox.Text,'-ManifestUrl',$manifestBox.Text,'-LauncherManifestUrl','https://www.dropbox.com/scl/fi/vfq641h4qdl4yo953hj35/manifest.json?rlkey=vaiemhc5ghzhlu13o64b3tg49&st=w9437u0s&dl=1'); if($obfuscate.Checked){$args += '-Obfuscate'}; Start-Process powershell.exe -ArgumentList $args -WorkingDirectory $root -Wait -NoNewWindow; $log.AppendText("Release build finished. Check $root\release`r`n") }
$refresh=Button 'Refresh log' 165 285 { if(Test-Path $logFile){$log.Text=Get-Content $logFile -Raw};$diagFile=Join-Path $root 'logs\network-diagnostics.txt';if(Test-Path $diagFile){$log.AppendText("`r`n"+(Get-Content $diagFile -Raw))} }
$firewall=Button 'Allow game port' 310 285 { $gamePort=[int]$portBox.Text;$fwScript='netsh advfirewall firewall add rule name="Exile RSPS TCP '+$gamePort+'" dir=in action=allow protocol=TCP localport='+$gamePort+'; netsh advfirewall firewall add rule name="Exile RSPS UDP '+$gamePort+'" dir=in action=allow protocol=UDP localport='+$gamePort+'; Add-Content -Path "'+$logFile+'" -Value "Firewall rules requested for '+$gamePort+'"'; Start-Process powershell.exe -Verb RunAs -WindowStyle Hidden -ArgumentList @('-NoProfile','-Command',$fwScript);$log.AppendText("Requested elevated firewall rules; check UAC and refresh the log.`r`n") }
$diagnose=Button 'Network diagnostics' 455 285 { $gamePort=[int]$portBox.Text;$diagFile=Join-Path $root 'logs\network-diagnostics.txt';$diagScript='$o=@();$o+="--- Exile network diagnostics $(Get-Date -Format s) ---";$o+="LAN IPv4: $((ipconfig|Select-String "IPv4 Address"|Select-Object -First 1).ToString().Trim())";$o+="Gateway: $((ipconfig|Select-String "Default Gateway"|Select-Object -First 1).ToString().Trim())";$o+="Public IPv4: '+$externalIp+'";$o+="Listener: $(netstat -ano|Select-String ":'+$gamePort+' .*LISTENING")";$o+="Check router forwarding TCP/UDP '+$gamePort+' to 192.168.0.38";$o|Set-Content "'+$diagFile+'"';Start-Process powershell.exe -WindowStyle Hidden -ArgumentList @('-NoProfile','-Command',$diagScript);$log.AppendText("Network diagnostics started; click Refresh log in a moment.`r`n") }
$timer=New-Object Windows.Forms.Timer;$timer.Interval=1500;$timer.Add_Tick({$found=Find-ExileServerProcess;if($found){$status.Text="Running (PID $($found.ProcessId))"}elseif($script:serverProcess -and $script:serverProcess.HasExited){$status.Text='Stopped (last process exited)'}});$timer.Start()
$form.Add_FormClosing({ if ($script:serverProcess -and !$script:serverProcess.HasExited) { $script:serverProcess.CloseMainWindow() } })
[void]$form.ShowDialog()

