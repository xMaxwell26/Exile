# Exile

A classless PvE ARPG prototype built on an OSRS/pre-EoC RSPS foundation. Combat builds come from equipment, affixes, passive choices, active skills and dropped support gems. Progression leads from a guided introduction and private base into repeatable instanced maps.

## Build and run

Run these commands from this workspace in PowerShell:

```powershell
.\dev.ps1 Build
.\dev.ps1 Test
.\dev.ps1 Server
```

Open another PowerShell window for the client:

```powershell
.\dev.ps1 Client
```

The server runs locally on127.0.0.1:43594 with online/database mode disabled. The scripts select the installed Java8 runtime and Java21 server compiler. Keep the workspace cache and character store together when backing up the prototype.

## Authoring and balance

- [BALANCING-GUIDE.md](BALANCING-GUIDE.md): editable drop tables, monster stats, bases, affix tiers and a worked signature-weapon drop recipe.
- [NPC-AUTHORING.md](NPC-AUTHORING.md): original Furnace Sentinel geometry, named parts, rig constraints and native animation verification.
- [BRIAR-AUTHORING.md](BRIAR-AUTHORING.md): original Briar Warden branch geometry and editable body recipe.
- [ITEM-ART.md](ITEM-ART.md): inventory/ground/worn art pipeline.
- [MAP-PIPELINE.md](MAP-PIPELINE.md): private maps, scenery/collision and future RSPSi import work.
- [PROTOTYPE-GUIDE.md](PROTOTYPE-GUIDE.md): play controls and prototype commands.
- [logs/README.md](logs/README.md): implementation evidence, independent criticism, failures and live verification.

The supported development entry points are dev.ps1, dev.sh (Linux headless build/test), tools/ValidateContent.ps1 and tools/PlaytestClients.ps1. The older Python files under tools/ are historical one-off source generators; rerunning them can overwrite newer work. Edit the current Java/data files using the guides above.

## Compatibility and naming

The product name is **Exile**. Historical Java package names, workspace folder names, cache filenames and character-save namespaces remain stable to preserve saved characters, controls and tool compatibility. They are implementation identifiers, not player-facing branding. No public Exile website or store is configured; old server-domain actions must not open unrelated sites.

This remains a prototype. Original enemy meshes use verified cache animation rigs; native views and actual client checks both matter. Balance data is intentionally editable and provisional. Multiplayer parties, a larger passive tree, more skill families, RSPSi imports and release-quality character art remain future work.
