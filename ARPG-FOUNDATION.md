# ARPG item and stat foundation

Implemented: immutable per-item rolls with UUID identity; item level and required level; normal/magic/rare/unique rarity; identification; separate corruption state (0/1/2); typed prefix, suffix, implicit and enchant layers; affix tiers and numerical modifiers. Magic allows one prefix/suffix, rare allows three each. Implicit layers reserve future systems; corruption crafting and influence compatibility are not implemented yet.

Item.copy and BankItem.copy retain identity. Rolled items have quantity one, never stack, and bank withdrawal uses the selected UI slot (including tab offsets and search results) for Withdraw-1/5/10/All. ID-only custom amount/all-but-one packets cannot identify duplicate rolls and reject rolled bases until the client protocol is extended. Rolled items cannot be noted.

Saves append optional base64 JSON metadata before the existing item-name comment. Old saves still load. Invalid metadata fails loading instead of silently deleting rolls. Updated saves must not be opened with an old server build, which would discard metadata on its next save.

Player.getArpgStats provides a recomputed equipment snapshot. Calculation: (base + flat) * (1 + summed increased / 100) * product(1 + more / 100). Baseline resources and caps are prototype values. Resistance: -200 to 75; block: 0 to 75; crit chance: 0 to 100. No attack-speed cap. This snapshot is not yet wired into legacy combat damage, hitpoints, mana consumption, mitigation or the client HUD. Local weapon/global modifier scoping and data-driven base definitions remain for the combat integration milestone.

## Try it after restarting the server

- `::arpgsword` creates one rare bronze sword with rolled life, critical chance and attack speed.
- Run twice for two independently rolled swords.
- `::arpgitem 1` inspects inventory slot 1 (slots are 1-28).
- `::arpgstats` shows equipped stat totals. Equip/swap/remove the swords and compare.
- Deposit both, withdraw the second using Withdraw-1, inspect, bank again and relog. The UUID and rolls should remain unchanged.

Commands are available only with online mode disabled. This is developer test content, not the final drop generator or item GUI. Legacy shop selling, trading and familiar deposits reject rolled items. Legacy item option 1/3/4 handlers are temporarily disabled while carrying rolled items. Death retains equipment and inventory while carrying/equipping rolled items until ARPG death handling replaces legacy ground drops. Other old content is not yet audited for rolled gear: use the prototype swords in controlled inventory/equipment/bank tests.

## Verification

Run `./dev.ps1 Build`, then `./dev.ps1 Test`. The checks exercise container transfer/failure, bank identity and selected-slot withdrawal, legacy stacking, metadata encoding, real character save writer/loader round trip, modifier order/caps and affix limits. No automated client clicks are required. Live client bank UI testing remains manual.

## Inventory hover tooltips

Hover rolled equipment in the inventory to see its rarity-colored name, required level and modifiers. Hold Ctrl for item level, affix names, layers and tiers. The same slot-aware metadata is sent for equipment and bank interfaces. Fixed-mode tooltips appear to the left of the inventory inside the game viewport. Tooltips hide during dragging and menus; releasing focus resets Ctrl.

The protocol uses reserved string-message widget 65534 for slot-specific tooltip data. Full container updates clear metadata before repopulation; single updates replace or clear that slot. Logout clears the client cache. Unidentified items never send hidden explicit modifiers in either tooltip view. Updated server and client must be run together.

Verification: 41 foundation/protocol checks pass. TooltipRenderCheck renders both views with the cache's actual p12_full font for visual QA. This verifies the renderer and wire format; full in-game hover behavior still needs the user's login to check.

## Rolled melee combat

Equipping a rolled physical weapon activates the ARPG melee path against ordinary NPCs. Damage uses the equipped PHYSICAL_MIN/PHYSICAL_MAX snapshot, critical chance and critical multiplier. Skills can reuse MeleeRolls with a damage-effectiveness multiplier. Existing NPC accuracy, immunity and melee damage/death/XP handling still apply. Rolled weapons cannot use this path against players or familiars. Legacy weapons retain the legacy combat path.

AttackClock accumulates fractional attacks each 600 ms simulation step, with an immediate initial attack. At 150 APS, subsequent ticks resolve 90 attacks; at 1000 APS, 600. Each batch emits one damage application and animation. This improves rate scaling but is not sub-tick movement or animation. The first 4096 attacks in a batch are sampled directly; larger batches use a bounded representative sample scaled to the logical count. It preserves expected damage, not exact per-attack/on-hit event behavior at extreme rates. Target/weapon changes reset credit. Damage saturates at the integer limit and is clamped to target health by the existing applier.

NPC hit packets now carry a 32-bit damage amount and critical marker; both client and server must be updated together. Monster health display remains the legacy percentage representation for larger health pools.

`::arpgcombat` toggles prototype batch logging: attack count, pre-immunity/pre-health-cap damage, critical presence and APS. New `::arpgsword` items also roll a physical damage prefix; existing swords retain their original rolls. Life, mana, ES and defensive affixes are still stat-only pending resource/defence integration. Legacy special attacks are bypassed for rolled weapons until ARPG skills replace them.

Build and 63 checks pass, including fractional timing, 150/1000 APS, crit multiplier, miss handling, damage overflow, weapon gating, actual NPC health removal, critical masks, 32-bit packet encoding and the integrated attack cooldown path. Live in-game balance and sustained high-rate profiling remain to be tested.

Tooltip physical damage now displays a single computed weapon range (Physical Damage: X-Y). Paired flat physical affixes display Adds X to Y Physical Damage; Ctrl reveals the base range and affix tier. Existing rolls and combat values are unchanged. Build and 67 checks pass; both updated tooltip views were rendered with the real client font and inspected.

## Explicit chat and prototype skills

Enter or clicking the chat input activates typing. Enter submits (or closes empty chat); Escape cancels. World clicks and lost focus release chat. Existing private-message and amount/name prompts retain text input. Enter and panel-toggle repeat are debounced. Gameplay letters do not type into chat without activation.

Q casts Heavy Strike (180% weapon damage, 12 mana, 3s base cooldown). W casts frontal Cleave (90%, 18 mana, 3.6s). Both require a selected ordinary attackable NPC in melee range and a rolled physical weapon. K opens a separate skills/support panel. Click the skill, then a support row to toggle Splash, Faster Attacks or Brutality on that skill. Support selections persist in the character's ESSENTIALS fields; older characters default to no supports. Mana starts at 50 on login, regenerates at the MANA_REGEN stat (base 5 per second) and is capped by MANA. Mana and cooldowns are session state for the prototype. The viewport HUD shows mana and both cooldowns.

Splash adds nearby targets (Heavy Strike secondary hits use 50% of its supported damage). Cleave hits a short frontal area. Faster Attacks increases use speed and cooldown recovery by 25%; Brutality adds 30% more damage and 40% mana cost. Splash adds 20% mana cost. These are prototype supports, not full PoE mechanics or final balance. Active skills use a shared action lock and cannot spend mana while on cooldown. Invalid targets do not spend mana. Cast requests are server-validated for attackability, same plane, range, path, ownership and single-combat aggressors. Players/familiars and legacy melee-immune flying NPCs are excluded.

Protocol: arpgskill commands use the existing command packet and are intercepted before general chat command processing. Reserved string widget 65533 carries server resource/cooldown/support state. 90 automated checks pass, including chat routing, skill spend/recovery, support separation, repeat rejection, target validation and an integrated two-enemy Cleave. Panel layout was rendered with the actual client font and visually inspected. Live keyboard/mouse behavior remains for in-game verification after login. Skill bindings are fixed for this prototype; skill drops/levels, configurable bindings and larger skill libraries remain future work.

## Character progression, defences and test arena

The Skills sidebar now renders a character sheet. It replaces the old RuneScape skill icons, skill list and skill click menus in that tab. It lists character level/XP, earned passive points, resources, weapon damage, APS, crit, accuracy, armour/evasion/block and four resistances. Noncombat skills remain in the server for future integration but are not listed in this tab. The old prayer/spellbook tabs have not been redesigned in this milestone.

Attack, Strength, Defence, Hitpoints, Ranged, Prayer, Magic and Summoning XP gains are disabled. Combat damage awards prototype unified character XP (5 XP per damage), with level thresholds 100*(level-1)^2, maximum level 100. Each level above 1 earns one future passive point; allocation is not yet implemented. Combat labels and legacy combat equipment requirements use character level. Rolled equipment uses its saved required level. Noncombat requirements remain. NPC accuracy against players is resolved once by the new defence system; player accuracy against NPCs uses the character's Accuracy stat and an NPC evasion proxy based on defence. Existing combat skill XP is retained in saves for compatibility but does not advance.

Real life starts at 100 plus 10 per character level above 1, before gear; base life regeneration is 1/sec as a prototype tuning choice. Gear does not refill resources: removing gear clamps maximum pools, re-equipping does not heal. Life and ES save separately from legacy skill levels, and restore-all/respawn restores the ARPG pools. The HUD and health orb use real life values. Mana orb conversion remains deferred.

Typed incoming damage supports Physical, Fire, Cold, Lightning and Chaos. Physical attacks may be evaded or blocked, physical hits are mitigated by armour, elemental/chaos damage uses its matching resistance, ES absorbs remaining non-chaos damage and chaos bypasses ES. Poison is currently treated as chaos damage over time. Untagged legacy magic hits default to fire; a complete spell-by-spell elemental audit remains future work. Existing legacy prayer/special reductions can apply before this layer. Physical DoT bypasses armour. Player hit packets now carry 32-bit damage/life/max-life values; updated server and client must be deployed together.

The prototype uses PoE 1 armour reduction A/(A+5*hit), capped at 90%, and ES recharge after 2s without damage at 33.3% of max ES/sec. Sources: https://www.poewiki.net/wiki/Armour and https://www.poewiki.net/wiki/Energy_shield . Evasion uses a simpler explicitly prototype accuracy/(accuracy+evasion) model with a minimum 5% hit chance. This is not a full PoE damage engine: conversion, penetration, ailments, suppression and mixed-component hits remain future work.

Offline developer commands:

- `::arena` or `::arena reset`: enter/rebuild a private seven-target test area on a verified clear Falador floor. Six pack targets have 200 life, a champion has 1200. The pack cycles physical and elemental/chaos attack types, and speaks labels overhead. Typed attacks occur every 3s within eight tiles. Arena targets are stationary, give no loot and do not respawn until reset.
- `::arena pause`: toggle incoming arena attacks for damage-only or survivability testing.
- `::arena leave`: return to the entry location (or Edgeville if the server restarted and the temporary run no longer exists).
- `::arpgarmour armour`, `evasion`, `es` or `hybrid`: generate test chest equipment with life and resistances plus the chosen defensive base. Equip it, open the character tab and compare incoming damage/ES recovery.

The arena is multi-combat, private by server height and cleaned up on exit/logout/death. Arena death keeps items; death XP/portal penalties are still future progression work. Arena positions/runs are temporary developer state; use leave after a server restart if logged into an empty arena copy. Current development map collision data is shared with the underlying map, so this is a testing instance rather than the final map-instance architecture.

114 checks pass, covering prior item/combat/skill behavior plus character XP/level restore, no Attack XP gain, armour vs hit size, elemental/negative resistances, ES absorption/overflow/chaos bypass, recharge delay, life regeneration, gear-swap limits, real player damage, respawn and resource save loading. ArenaLayoutCheck loads actual map collision data and verifies the chosen floor. CharacterPanel was rendered with the actual cache font and visually checked. Live balance and death/arena client interaction remain manual verification.

## Starter affix rules correction

New prototype explicit affixes use T10-T8 labels (physical damage T10, life T9, crit/speed T8) rather than calling every roll T1. T1 is reserved for the strongest explicit tier. Base properties no longer display explicit tier labels. These are provisional tiers pending a full affix catalog with item-level gates and tier-specific ranges.

New test chest items have three individual resistance suffixes instead of a combined elemental affix plus chaos. Armour: fire/cold/lightning; evasion: lightning/cold/chaos; ES: fire/cold/chaos; hybrid: fire/lightning/chaos. Each resistance consumes one suffix. Generation validation rejects combined explicit resistance affixes, resistance prefixes and duplicate explicit resistance groups. Rare items retain their three-suffix maximum. Existing saved test items are accepted without modification; generate fresh gear to see the new rules.

136 checks pass. The final XP design is kill-based; damage-based XP remains a prototype-only mechanism until kill rewards are implemented (tracked in TODO.md).
