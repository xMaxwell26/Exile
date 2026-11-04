#!/usr/bin/env bash
# Linux/cloud gate. Requires a Java 8 JDK for the client and runtime, Java 21
# for the server compiler. No desktop, X server or native client is launched.
set -euo pipefail
cd "$(dirname "$0")"
: "${EXILE_JAVA8_HOME:?Set EXILE_JAVA8_HOME to a Java 8 JDK}"
EXILE_JAVA21_HOME="${EXILE_JAVA21_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}"
action="${1:-test}"
case "$action" in build|test) ;; *) echo 'Usage: ./dev.sh [build|test]' >&2; exit 2;; esac
"$EXILE_JAVA8_HOME/bin/java" -version 2>&1 | head -1 | grep -q '"1.8.' || { echo 'Java 8 runtime required' >&2; exit 2; }
for project in Server Client; do
    directory="Notorious/$project"
    mkdir -p "$directory/build/classes"
    find "$directory/src" -name '*.java' -print | LC_ALL=C sort > "$directory/build/sources.txt"
    if [[ "$project" == Server ]]; then
        "$EXILE_JAVA21_HOME/bin/javac" --release 8 -encoding windows-1252 -cp "$directory/lib/*" -d "$directory/build/classes" @"$directory/build/sources.txt"
    else
        "$EXILE_JAVA8_HOME/bin/javac" -encoding windows-1252 -d "$directory/build/classes" @"$directory/build/sources.txt"
    fi
done
[[ "$action" == test ]] || exit 0
mkdir -p logs
cd Notorious/Server
mkdir -p build/test-classes
classpath='build/test-classes:build/classes:lib/*:../Client/build/classes'
find test -name '*.java' -print | LC_ALL=C sort > build/test-sources.txt
"$EXILE_JAVA21_HOME/bin/javac" --release 8 -cp "$classpath" -d build/test-classes @build/test-sources.txt
# Match the supported PowerShell gate except for two AWT/Applet input checks,
# which explicitly require a desktop. Headless layout contracts run separately.
checks=(TooltipContentCheck ArmourSurfaceCheck HeadlessLayoutCheck CourtyardEnvironmentCheck CraftingReferenceCheck TooltipRenderCheck ArpgFoundationTest PrototypeIntegrationCheck ExpansionCheck GameplayEdgeCheck
    FullCharacterVisualCheck ItemInventoryArtCheck ChatWrapCheck PacketFragmentationCheck HighDpiArtCheck MaceSilhouetteCheck
    SkillGlyphPreview ArenaTerrainCheck SceneryVisualCheck BattleEffectsCheck ManaOrbCheck ProgressionAuditCheck
    NativeEncounterNpcCheck CraftingAuditCheck CraftingPanelPagingCheck FoundryEnvironmentCheck CombatPresentationCheck MapDeathCheck
    NpcHealthPacketCheck SkillAnimationsCheck EncounterBalanceCheck ConsumableMeshCheck HigherBaseWeaponCheck GemFusionCheck
    ExpansionIconsCheck BaseBalanceCheck PanelCardsCheck ArmourVariantCheck QaFixtureCheck InputBindingsCheck
    TooltipBoundsCheck HubServicesCheck GroundLootMetadataCheck ExactLootPickupCheck GroundLootLabelsCheck HubTemplateCheck
    BiomeMapArtCheck WorldBackdropCheck CurrencyExpansionCheck CurrencyArtCheck CurrencyLabelAuditCheck HubPlacementCheck
    DropTableCheck ConstructProfileCheck ConstructWindupCheck CustomNpcMeshCheck CustomBriarMeshCheck ExileBrandingCheck
    ExileThemeCheck ExileLoginCheck
)
for check in "${checks[@]}"; do
    echo "Running $check"
    "$EXILE_JAVA8_HOME/bin/java" -Djava.awt.headless=true -Xmx1G -cp "$classpath" "$check" > "build/$check.log" 2>&1 || { cat "build/$check.log"; exit 1; }
    tail -1 "build/$check.log"
done
for npc in 15001 15007; do
    "$EXILE_JAVA8_HOME/bin/java" -Djava.awt.headless=true -Xmx1G -cp "$classpath" CustomNpcRigCheck "$npc" > "build/CustomNpcRigCheck-$npc.log" 2>&1 || { cat "build/CustomNpcRigCheck-$npc.log"; exit 1; }
    tail -1 "build/CustomNpcRigCheck-$npc.log"
done
echo 'Headless gate passed. ControlRoutingCheck/ViewportInputCheck and physical fullscreen acceptance require a local desktop.'
