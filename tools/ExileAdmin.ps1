param([int]$Port = 43594, [string]$HostAddress = '127.0.0.1')
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$server = Join-Path $root 'Notorious\Server'
$client = Join-Path $root 'Notorious\Client'
$java = Get-ChildItem 'C:\Program Files\Java' -Directory | Where-Object { $_.Name -like 'jdk1.8*' } | Sort-Object Name -Descending | Select-Object -First 1
if (!$java) { throw 'Java 8 JDK was not found.' }
$javaExe = Join-Path $java.FullName 'bin\java.exe'
$serverProcess = $null

Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing
$form = New-Object Windows.Forms.Form
$form.Text = 'Exile Server Admin'
$form.Size = New-Object Drawing.Size(520,390)
$form.StartPosition = 'CenterScreen'
$form.BackColor = [Drawing.Color]::FromArgb(24,20,24)
$form.ForeColor = [Drawing.Color]::Gainsboro
$font = New-Object Drawing.Font('Segoe UI',10)

function Button($text,$x,$y,$action) { $b=New-Object Windows.Forms.Button; $b.Text=$text; $b.Location=New-Object Drawing.Point($x,$y); $b.Size=New-Object Drawing.Size(130,34); $b.Add_Click($action); $form.Controls.Add($b); return $b }
$title=New-Object Windows.Forms.Label; $title.Text='EXILE  /  SERVER CONTROL'; $title.Font=New-Object Drawing.Font('Segoe UI Semibold',15); $title.Location=New-Object Drawing.Point(18,16); $title.AutoSize=$true; $form.Controls.Add($title)
$status=New-Object Windows.Forms.Label; $status.Text='Stopped'; $status.Location=New-Object Drawing.Point(20,58); $status.AutoSize=$true; $form.Controls.Add($status)
$log=New-Object Windows.Forms.TextBox; $log.Multiline=$true; $log.ReadOnly=$true; $log.ScrollBars='Vertical'; $log.Location=New-Object Drawing.Point(20,210); $log.Size=New-Object Drawing.Size(465,120); $log.BackColor=[Drawing.Color]::FromArgb(12,12,16); $log.ForeColor=[Drawing.Color]::LightGray; $form.Controls.Add($log)
$hostLabel=New-Object Windows.Forms.Label; $hostLabel.Text='Bind host / client address'; $hostLabel.Location=New-Object Drawing.Point(20,88); $hostLabel.AutoSize=$true; $form.Controls.Add($hostLabel)
$hostBox=New-Object Windows.Forms.TextBox; $hostBox.Text=$HostAddress; $hostBox.Location=New-Object Drawing.Point(185,84); $hostBox.Size=New-Object Drawing.Size(170,24); $form.Controls.Add($hostBox)
$external=New-Object Windows.Forms.CheckBox; $external.Text='External / beta'; $external.Location=New-Object Drawing.Point(365,84); $external.AutoSize=$true; $external.Add_CheckedChanged({ if($external.Checked){$hostBox.Text='0.0.0.0'}else{$hostBox.Text='127.0.0.1'} }); $form.Controls.Add($external)
$online=New-Object Windows.Forms.CheckBox; $online.Text='Online / SQL'; $online.Location=New-Object Drawing.Point(365,116); $online.AutoSize=$true; $form.Controls.Add($online)
$portLabel=New-Object Windows.Forms.Label; $portLabel.Text='Game port'; $portLabel.Location=New-Object Drawing.Point(20,120); $portLabel.AutoSize=$true; $form.Controls.Add($portLabel)
$portBox=New-Object Windows.Forms.TextBox; $portBox.Text=[string]$Port; $portBox.Location=New-Object Drawing.Point(185,116); $portBox.Size=New-Object Drawing.Size(90,24); $form.Controls.Add($portBox)
$public=New-Object Windows.Forms.Label; $public.Text='External address: checking...'; $public.Location=New-Object Drawing.Point(20,145); $public.AutoSize=$true; $form.Controls.Add($public)
try { $ip=(Invoke-RestMethod -Uri 'https://api.ipify.org' -TimeoutSec 4); $public.Text="External address: $ip" } catch { $public.Text='External address: unavailable (offline)' }
$start=Button 'Start server' 20 175 { if (!$script:serverProcess -or $script:serverProcess.HasExited) { $bind=$hostBox.Text.Trim(); $gamePort=[int]$portBox.Text; $mode=if($online.Checked){'true'}else{'false'}; $script:serverProcess=Start-Process -FilePath $javaExe -WorkingDirectory $server -ArgumentList @('-Duser.home='+$root+'\Notorious','-Xmx1G','-cp','build/classes;lib/*','com.koios.Bootstrap','--host',$bind,'--port',$gamePort,'--onlinemode',$mode) -PassThru; $status.Text="Running (PID $($script:serverProcess.Id))"; $log.AppendText("Server started at $bind`:$gamePort (online=$mode)`r`n") } }
$stop=Button 'Stop server' 165 175 { if ($script:serverProcess -and !$script:serverProcess.HasExited) { $script:serverProcess.CloseMainWindow(); if (!$script:serverProcess.WaitForExit(3000)) { $script:serverProcess.Kill() }; $status.Text='Stopped'; $log.AppendText("Server stopped`r`n") } }
$connect=Button 'Launch client' 310 175 { $connectHost=$hostBox.Text.Trim(); if($connectHost -eq '0.0.0.0'){$connectHost='127.0.0.1'}; $gamePort=[int]$portBox.Text; Start-Process -FilePath $javaExe -WorkingDirectory $client -ArgumentList @('-Duser.home='+$root+'\Notorious','-Darpg.serverHost='+$connectHost,'-Darpg.serverPort='+$gamePort,'-Xmx1G','-cp','build/classes','com.koios.client.Client'); $log.AppendText("Client launched for $connectHost`:$gamePort`r`n") }
$form.Add_FormClosing({ if ($script:serverProcess -and !$script:serverProcess.HasExited) { $script:serverProcess.CloseMainWindow() } })
[void]$form.ShowDialog()
