param([ValidateSet('Build','Test','Server','Client')][string]$Action = 'Build', [string]$JdkPath = $env:JAVA_HOME, [string]$ServerJdkPath = 'C:\Program Files\Java\jdk-21')
$ErrorActionPreference = 'Stop'
$root = Join-Path $PSScriptRoot 'Notorious'
if (!$JdkPath) {
    $JdkPath = Get-ChildItem 'C:\Program Files\Java' -Directory | Where-Object { $_.Name -like 'jdk1.8*' } | Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName
}
if (!$JdkPath -or !(Test-Path (Join-Path $JdkPath 'bin\javac.exe'))) { throw 'Set JAVA_HOME to a Java 8 JDK, or pass -JdkPath.' }
if ($Action -eq 'Build') {
    foreach ($project in @('Server','Client')) {
        $dir = Join-Path $root $project
        $out = Join-Path $dir 'build\classes'
        New-Item -ItemType Directory -Force $out | Out-Null
        $sources = Join-Path $dir 'build\sources.txt'
        Get-ChildItem (Join-Path $dir 'src') -Recurse -Filter *.java | ForEach-Object { '"' + $_.FullName.Replace('\','/') + '"' } | Set-Content -Encoding ASCII $sources
        $compileArgs = @('-encoding','windows-1252','-d',$out)
        if ($project -eq 'Server') { $compileArgs += @('-cp',(Join-Path $dir 'lib\*')) }
        $compilerJdk = $JdkPath
        if ($project -eq 'Server') { $compilerJdk = $ServerJdkPath; $compileArgs += @('--release','8') }
        & (Join-Path $compilerJdk 'bin\javac.exe') @compileArgs "@$sources"
        if ($LASTEXITCODE -ne 0) { throw "$project compilation failed." }
        Write-Host "$project built successfully."
    }
} elseif ($Action -eq 'Test') {
    $dir = Join-Path $root 'Server'
    Push-Location $dir
    try {
        New-Item -ItemType Directory -Force 'build/test-classes' | Out-Null
        & (Join-Path $ServerJdkPath 'bin/javac.exe') --release 8 -cp 'build/classes;lib/*;../Client/build/classes' -d build/test-classes test/ArpgFoundationTest.java test/PrototypeIntegrationCheck.java test/CustomItemVisualCheck.java test/ItemInventoryArtCheck.java test/WeaponMeshCheck.java test/ArmourMeshCheck.java test/FullCharacterVisualCheck.java test/ChatWrapCheck.java test/ViewportInputCheck.java test/PacketFragmentationCheck.java test/ExpansionCheck.java test/HighDpiArtCheck.java test/MaceSilhouetteCheck.java test/SkillGlyphPreview.java test/ArenaTerrainCheck.java test/SceneryVisualCheck.java test/GameplayEdgeCheck.java test/BattleEffectsCheck.java test/ManaOrbCheck.java test/ProgressionAuditCheck.java test/NativeEncounterNpcCheck.java test/CraftingAuditCheck.java test/FoundryEnvironmentCheck.java test/CombatPresentationCheck.java test/ConsumableMeshCheck.java test/HigherBaseWeaponCheck.java test/GemFusionCheck.java test/ExpansionIconsCheck.java test/BaseBalanceCheck.java test/PanelCardsCheck.java test/ArmourVariantCheck.java test/QaFixtureCheck.java test/InputBindingsCheck.java test/ControlRoutingCheck.java test/TooltipBoundsCheck.java test/HubTemplateCheck.java test/GroundLootLabelsCheck.java test/ExactLootPickupCheck.java test/GroundLootMetadataCheck.java test/HubServicesCheck.java test/EncounterBalanceCheck.java test/BiomeMapArtCheck.java test/WorldBackdropCheck.java test/SkillAnimationProbe.java test/SkillAnimationsCheck.java test/MapDeathCheck.java test/NpcHealthPacketCheck.java test/CraftingPanelPagingCheck.java test/CurrencyExpansionCheck.java test/CurrencyArtCheck.java test/CurrencyLabelAuditCheck.java test/HubPlacementCheck.java test/DropTableCheck.java test/ConstructProfileCheck.java test/ConstructWindupCheck.java test/CustomNpcMeshCheck.java test/CustomBriarMeshCheck.java test/CustomNpcRigCheck.java test/ExileBrandingCheck.java test/ExileThemeCheck.java test/ExileLoginCheck.java test/TooltipContentCheck.java test/ArmourSurfaceCheck.java test/HeadlessLayoutCheck.java test/CourtyardEnvironmentCheck.java test/CraftingReferenceCheck.java test/TooltipRenderCheck.java
        if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
        & (Join-Path $ServerJdkPath 'bin/javac.exe') --release 8 -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' -d build/test-classes test/TutorialGemInstructionCheck.java test/UncutJewelCheck.java test/GemcraftCardsCheck.java
        if ($LASTEXITCODE -ne 0) { throw 'Jewel crafting test compilation failed.' }
        & (Join-Path $JdkPath 'bin/java.exe') -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' ArpgFoundationTest
        if ($LASTEXITCODE -ne 0) { throw 'ARPG checks failed.' }
        & (Join-Path $JdkPath 'bin/java.exe') -Xmx1G -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' PrototypeIntegrationCheck
        if ($LASTEXITCODE -ne 0) { throw 'Prototype integration checks failed.' }
        & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=true' -Xmx1G -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' ExpansionCheck
        if ($LASTEXITCODE -ne 0) { throw 'Expansion checks failed.' }
        & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=true' -Xmx1G -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' GameplayEdgeCheck
        if ($LASTEXITCODE -ne 0) { throw 'Gameplay edge checks failed.' }
        foreach ($visualCheck in @('TooltipContentCheck','ArmourSurfaceCheck','HeadlessLayoutCheck','CourtyardEnvironmentCheck','CraftingReferenceCheck','TooltipRenderCheck','FullCharacterVisualCheck','ItemInventoryArtCheck','ChatWrapCheck','PacketFragmentationCheck','HighDpiArtCheck','MaceSilhouetteCheck','SkillGlyphPreview','ArenaTerrainCheck','SceneryVisualCheck','BattleEffectsCheck','ManaOrbCheck','ProgressionAuditCheck','NativeEncounterNpcCheck','CraftingAuditCheck','CraftingPanelPagingCheck','FoundryEnvironmentCheck','CombatPresentationCheck','MapDeathCheck','NpcHealthPacketCheck','SkillAnimationsCheck','EncounterBalanceCheck','ConsumableMeshCheck','HigherBaseWeaponCheck','GemFusionCheck','ExpansionIconsCheck','BaseBalanceCheck','PanelCardsCheck','ArmourVariantCheck','QaFixtureCheck','InputBindingsCheck','TooltipBoundsCheck','HubServicesCheck','GroundLootMetadataCheck','ExactLootPickupCheck','GroundLootLabelsCheck','HubTemplateCheck','BiomeMapArtCheck','WorldBackdropCheck','CurrencyExpansionCheck','CurrencyArtCheck','CurrencyLabelAuditCheck','HubPlacementCheck','DropTableCheck','ConstructProfileCheck','ConstructWindupCheck','CustomNpcMeshCheck','CustomBriarMeshCheck','ExileBrandingCheck','ExileThemeCheck','ExileLoginCheck')) {
            & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=true' -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' $visualCheck
            if ($LASTEXITCODE -ne 0) { throw "$visualCheck failed." }
        }
        foreach ($jewelCheck in @('TutorialGemInstructionCheck','UncutJewelCheck','GemcraftCardsCheck')) {
            & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=true' -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' $jewelCheck
            if ($LASTEXITCODE -ne 0) { throw "$jewelCheck failed." }
        }
        # Exercise each original mesh through its actual configured server attack profile.
        foreach ($npcId in @(15001,15007)) {
            & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=true' -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' CustomNpcRigCheck $npcId
            if ($LASTEXITCODE -ne 0) { throw "CustomNpcRigCheck $npcId failed." }
        }
        & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=false' -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' ControlRoutingCheck
        if ($LASTEXITCODE -ne 0) { throw 'Control routing checks failed.' }
        & (Join-Path $JdkPath 'bin/java.exe') '-Djava.awt.headless=false' -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' ViewportInputCheck
        if ($LASTEXITCODE -ne 0) { throw 'Viewport input checks failed.' }
    } finally { Pop-Location }
} else {
    $dir = Join-Path $root $Action
    Push-Location $dir
    try {
        $javaArgs = @("-Duser.home=$root",'-Xmx1G','-cp')
        if ($Action -eq 'Server') { $javaArgs += @('build/classes;lib/*','com.koios.Bootstrap','--host','127.0.0.1','--port','43594','--onlinemode','false') }
        else { $javaArgs += @('build/classes','com.koios.client.Client') }
        & (Join-Path $JdkPath 'bin\java.exe') @javaArgs
        if ($LASTEXITCODE -ne 0) { throw "$Action exited with code $LASTEXITCODE." }
    } finally { Pop-Location }
}












