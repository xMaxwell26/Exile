# Playable prototype

## Current expansion

The map device and gem-upgrade choices now use cards. Prayer's minimap orb is a live blue mana display; click it to open the skill loadout. Material hover tooltips explain what can be crafted and what gets consumed.

Active gems of the same level can be fused by using one on another. Fusion consumes the source gem and 25 times its level in coins, upgrades the target one level, and keeps the target's item identity. Maximum level 20. Support gems remain finite socketable copies and do not use this fusion recipe.

There are 17 equipment bases, including the level-gated Bogsteel Cleaver, Dawnbreaker Blade, Gloomfang Dagger, Bastion Plate and Veilweave Coat. Six later weapons now have distinct ground/worn geometry; the two later armour variants use fitted materials/details. Illustrated expansion art is separate from the original approved atlas.

Foundry and Courtyard use 14 named themed creatures and bosses. Enemy archetypes have different life and individual attack timing, with fragile ranged casters and faster light spiders/hounds. The Foundry has workshop stations, proper doorways, stone/paver/firebrick zones and a foreman area. Skill actions, fire/cold casts and boss impact have world effects. The slam marker fills and counts down; the boss has a recovery pause after impact.

New characters retain their supplies throughout the guided introduction: discarding is blocked until it finishes. This prevents losing mandatory gear after disconnecting from a private instance. Normal post-tutorial drops still go onto the ground.

New characters automatically begin the guided tutorial. Existing prototype characters can use **`::tutorial`** to start or resume it. The tutorial teleports between equipment, crafting, passive, support and combat stations, with an instruction tracker and action-based checkpoints. It provides starter supplies automatically, then waits for the player to equip gear, craft, allocate a passive, attach a support, use both Q/W, defeat the champion, collect the map, read a Portal Scroll, travel home and click the map device. No tutorial selection menu is required.

Checkpoint progress saves. A disconnected combat encounter is recreated when resuming; a closed-instance return checkpoint recovers to the base. The tutorial boss cannot die during the Q/W practice stage. P remains available as the prototype tools panel, and the map device still uses a selection interface for choosing maps.

Use normal click combat, **Q** for Heavy Strike and **W** for Cleave and **E** for Whirlwind. Whirlwind strikes around you and can find a nearby enemy without an existing target. Splash expands its radius; Faster Attacks and Brutality also apply. **K** configures supports. Enter opens chat; skills pause while typing. Escape closes a panel or cancels chat. The former Skills tab shows character progression and combat stats.

Maps show an objective tracker at the top right: defeat the Settlement Champion. It includes the boss direction, distance and remaining enemy count. A hint arrow marks the boss. On boss death, it changes to **MAP COMPLETE**, marks a return portal and announces completion. Collect ground loot before leaving or closing the map.

**Portal Scrolls** (Read in inventory) open a return portal near you and a paired map portal at your private base. Walk up to the return portal and click Return-to-base; use the Map portal at home to re-enter near where you opened it. Creating another portal replaces the old return location. Maps start with an entrance portal, and the boss creates a free exit even if you have no scrolls. Starter kits include five scrolls; existing players can buy five for 25 coins in P / Guide, and enemies drop more. Entry limits from level 10 still apply. Active maps and their portals close on logout or expiry.

After the introduction, return home through P. Close the completed encounter in Maps, then select the rewarded map. Each map item is consumed to create a separate settlement instance with buildings, walls, collision, packs and a champion. The champion guarantees another map; monsters also drop equipment, coins, crafting tokens and upgrade gems. A recovery map costs 25 coins at the device. Map XP comes from kills. The older test arena retains damage XP.

The private base has a map altar, crafting workbench and bank chest. **Craft directly in your inventory:** right-click a material, choose Use, then click the equipment or map. Scroll of Wisdom identifies; Orb of Transmutation makes a normal item magic; Orb of Alchemy makes a normal/magic item rare; Chaos Orb rerolls modifiers; Exalted Orb adds a modifier; Vaal Orb corrupts. These have dedicated item IDs, names and original inventory icons. Their ground shapes currently reuse existing orb/paper cache models; fully custom 3D meshes remain future art work. P / Crafting is a reference and gem-upgrade panel, rather than the normal crafting workflow. P / Guide or Crafting can exchange old rune materials in your inventory for the new currencies.

Monster rewards land at the enemy's death tile and must be picked up. Equipment retains its exact identity and rolls, and full inventories leave loot on the ground. Private map loot remains until the instance closes, expires, or the player logs out. Uncollected loot is cleared with the instance. Starter kits and purchased recovery maps still go directly to inventory; those are grants rather than monster drops.

Corruption currently adds life implicits to equipment, up to two, and locks ordinary crafting. Advanced influence, enchants and broader corruption outcomes remain future content.

Spend earned passive points in P / Passives. The connected graph branches from a central start: hover nodes for upgrades; bright outlines show available paths and gold marks allocated nodes. Nodes require their parent. Ctrl-click refunds leaf nodes for 100 coins; the footer toggles free testing refunds in offline mode. The three branches cover sword damage, survival and energy resources. Upgrade dropped skill/support gems through Crafting: choose the skill or support to receive the gem's level. Higher gems require character levels.

Before level 10, map entry and death are forgiving. From level 10, normal maps have six entries and death removes 10% of progress toward the next level, without losing a level. Prototype map deaths retain gear. Non-loot grants can overflow into the bank, then a saved mailbox claimed from Guide.

Character XP, passive allocations, item identities/rolls, gem upgrades, starter/introduction flags and mailbox rewards save. Active maps and private base instances are temporary: logout or server restart closes them. Consumed maps are not refunded on restart. Returning players whose saved position was in a closed instance recover to the normal world and can reopen their base.

## Current limits

Solo only; party ownership is future work. Settlement and house templates copy existing cache regions; original RSPSi layouts require the compatibility work in MAP-PIPELINE.md. Scenery is decorative; instance-aware doors and building interactions remain to be added. The private base is a house-layout prototype rather than the full RuneScape Construction system.

The P panels and HUD are functional test interfaces. Final interface design, mana orb, fully rebindable keyboard/mouse controls, additional skills and weapon bases, economy balance, authored maps, and beta hardening remain in scope for later development. Live client map rendering and interactive combat need in-game validation; automated checks cover the server lifecycle and packet generation, not a logged-in play session.

## Verification

Run `./dev.ps1 Build`, then `./dev.ps1 Test`. The test action includes foundation checks plus cache-backed integration checks for translated map regions, isolated collision, map entry/consumption/cleanup, introduction rewards, kill XP, crafting identity and passive effects. `TooltipRenderCheck.java` generates panel previews using the actual client font cache.


## Melee and encounter expansion

New drops use seven data-driven equipment bases: Rusted Sword (balanced), Carving Dagger (fast, higher crit), Worn Mace (slow, heavy damage/accuracy), Splitting Axe (critical multiplier), Ironward Mail (armour), Trailhide Vest (evasion), and Spiritweave Robe (energy shield). The tooltip shows physical damage, attacks per second, critical chance and defensive properties. Base names/stats survive crafting. Existing saved prototype items keep their original bases and rolls.

Use `::arpgbases` in the offline prototype to receive all seven normal, identified level-one bases for comparison; overflow uses the normal stash/mailbox grant handling. These are test samples, not a production reward.

Normal maps have three packs, each with an empowered elite, and a champion placed farther from the entrance. Zombies/skeletons use physical attacks, casters attack with fire from range, and spiders apply chaos damage. Elites have more life/damage and guarantee equipment plus extra crafting currency and kill XP. The introduction stays simple.

The normal-map champion periodically targets the player's current tile for a physical slam. The orange tile outlines and objective countdown give 1.8 seconds to leave the marked area. The attack hits the original area, so movement avoids it. Returning home, dying, killing the boss or closing the map clears pending hazards. The client marker still needs a live play check for camera/terrain readability.

Whirlwind uses 24 base mana, a five-second base cooldown and 115% weapon damage. Higher skill gems can upgrade it through Crafting. It has its own supports, cooldown and saved gem level. Existing five-value gem saves load with a level-one Whirlwind; old Q/W support assignments retain their meaning.


## Drag-and-drop skills

Press **K** for the skill loadout. Click a skill card to select it. Drag a skill card (or an existing hotbar tile) onto Q, W or E to swap assignments. The keyboard and clickable gameplay bar execute the skill assigned to that slot; labels and cooldowns follow the assignment. Hotbar order saves with the character.

Drag Splash, Faster Attacks or Brutality from the support library onto a skill card to attach it. Drag a filled support socket onto another skill card to move it, or back into the library to remove it. Dropping outside a valid target cancels; Escape, closing the panel or losing focus cancels an active drag. Repeated support drops are idempotent. Support levels stay with the learned support.

Skill cards display gem level, supported damage effectiveness, mana cost and cooldown. The guided tutorial now asks for a support drag and uses the actual assigned keys when explaining Heavy Strike and Cleave. The existing keyboard and mouse rebinding TODO remains separate from assigning skills to Q/W/E.

Also fixed Whirlwind's cooldown ticking: it now counts down and permits repeated casts.


## Illustrated item art

Equipment, map items, skill/support gems and crafting currencies now have illustrated inventory icons with consistent custom names and separate IDs. Existing saved prototype gear adopts the new IDs on load, preserving rolls and item identity. Old rune crafting materials can be exchanged under P / Guide. Ordinary RuneScape items keep their normal definitions. The skills/loadout interface is unchanged.

Use `::arpgbases` for the seventeen equipment bases. Full art assets, generation prompts and runtime import details are recorded in ITEM-ART.md. Ground currencies/maps and ten weapons have authored geometry. Armour uses fitted legacy rigs with authored accents; it still needs further character-art polish. Original Furnace Sentinel and Briar Warden creature meshes use verified cache skeletons. See NPC-AUTHORING.md and BRIAR-AUTHORING.md.


The four melee weapons now have authored in-world meshes matching their item identities: chipped sword, hooked dagger, flanged mace and crescent axe. Inventory art and skills/loadout controls remain as approved. Use `::arpgbases` to get comparison samples, then equip each weapon and check walking, attacks and ground drops. Male/female weapon-hand binding and native rendering are tested; armour still uses recolored cache meshes.

The old quest/prayer/magic slots now open passive-tree, supports and active-skill destinations. The existing K drag/drop loadout remains the editor. Tutorial narration stays in the tracker; system chat wraps to its usable width. Fullscreen/resizable modes scale the logical canvas and pointer coordinates together, put objectives below the minimap, and position the skill bar clear of chat. The original RSPS onboarding packet lock and save restriction are retired.

Dying in a map returns you to your private house with restored resources. During the tutorial, the tracker asks you to enter the map portal to retry the same encounter; the checkpoint and gear remain intact.

## Expansion test pass

- K: five melee skills across two pages. Ground Slam hits around the selected enemy; Execute deals extra damage below 35% enemy life. Three hotbar slots remain; drag a hotbar skill away to leave it empty. Empty slots save across reconnects.
- Supports require physical dropped Uncut Support Gems. Take a gem, click Learn, choose a support, then drag the available copy in K. One gem supplies one copy, which can be moved between skills or returned to the library. Old automatic supports are removed rather than converted into free gems. The tutorial supplies the first level1 support on the ground.
- Twelve equipment bases now exist. Warden Longblade, Nightglass Shiv, Wayfarer Brigandine require level4; Cinderheart Maul and Emberweave Mantle require level6. They reuse the existing visual families, with distinct base stats. `::arpgbases` gives samples for local testing.
- `::arpgmaps` gives the settlement, Ashen Foundry and Thornwild Courtyard map items for local testing after the tutorial. Foundry and courtyard have authored scenery/collision layouts over reused clear terrain chunks, not final RSPSi terrain exports.
- Normal map packs now also include hellhounds and cold-damage ghosts. Large-NPC footprints are validated against walls and other spawns.

Run `.\tools\PlaytestClients.ps1` to launch the two isolated local QA clients on the secondary monitor. They use dedicated test characters and fixture credentials, never the normal player account. Evidence and unresolved visual issues are listed in `logs/README.md`.


## Exile authoring update

The product is now Exile. See README.md for current entry points and BALANCING-GUIDE.md for editable drops, enemy profiles, bases and affix tiers. Expanded crafting adds Alteration, Augmentation, Regal, Scouring and Annulment. Alchemy requires normal items, Chaos/Exalted require rare items, and all crafting uses material-on-item interactions. New source batches require a fresh build/restart; logs/overnight-integration.md distinguishes staged, isolated and actual live checks.
