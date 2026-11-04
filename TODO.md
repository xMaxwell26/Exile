# Project TODO

## Headless quality pass

- [x] All seventeen equipment bases have tooltip property, duplicate-display and unidentified-privacy coverage.
- [x] Strict drop JSON rejects coercion, misspelled fields, duplicate keys and lenient syntax with author-facing paths.
- [x] Courtyard garden passages are two tiles wide, with matching worn terrain and real-cache scene/collision checks.
- [x] Fitted armour projects onto integer renderer coordinates; all 600 covered added vertices clear the torso surface.
- [x] Fullscreen layout contracts run headlessly at 16 sizes; physical monitor transitions remain local acceptance.
- [x] All eleven crafting recipes have a paged in-game reference, with complete descriptions measured using the real cache font.
- [x] Passive and repurposed quest/prayer/magic destinations use shared Exile chrome without adding legacy gameplay.
- [ ] Continue complete-outfit animation/triangle-interior clipping review and physical controls/fullscreen acceptance locally.

## Current integration work

- [x] Expand to 17 gated bases, six distinct higher-weapon meshes, two fitted armour variants and nine original ground consumable models.
- [x] Add themed encounter identities, individual enemy attack clocks, readable boss windup/recovery and native effect previews.
- [x] Add gem fusion, crafting-material tooltips, transaction/identity checks and tutorial supply retention.
- [x] Add live mana orb, map/gem card layouts and account-specific controls model/editor.
- [x] Add private-base Quartermaster/Gemcutter and owner-private ground labels with exact-identity pickup authority.
- [ ] Finish the next combined runtime gate for controls, services and clickable loot, then repeat live tutorial/map checks.
- [ ] Improve bespoke character/NPC geometry, outfit animation coverage and environmental composition beyond the current cache rig foundation.

## Skill and input interface redesign

- Replace the temporary skills/support GUI with the final interface.
- Let players bind each active skill to keyboard keys and supported mouse buttons, including middle click (M3), side buttons (M4/M5 and additional buttons exposed by the device), and right click.
- Support modifier combinations where practical, and verify extra-button support in the client input backend rather than assuming every mouse exposes the same buttons.
- Detect duplicate/conflicting bindings and let players clear, replace, save and reset bindings.
- Keep gameplay bindings inactive while entering chat or other text prompts.
- Define how rebound mouse buttons interact with targeting, movement, dragging and the right-click context menu. Preserve normal behavior for unbound buttons.
- Display the chosen bindings on the skill bar and skill tooltips; remove hardcoded Q/W/K assumptions from gameplay and UI.
- Persist each player's binding preferences across sessions.

## Deferred UI work

- Redesign the temporary ARPG panels and HUD after the gameplay loop is proven.
- [x] Replace the prayer orb with a mana orb.

## Affix generation and balance

- Use PoE 1-style explicit tier ordering: T1 is strongest; early tiers typically start around T10-T8, with the actual number of tiers defined per affix family and equipment base.
- Expand the implemented JSON tier catalog with more bases and affix families, and tune weights/ranges. The initial catalog has item-level unlocks and T10 through T1; values are prototype balance, not verified PoE values.
- Fire, cold, lightning and chaos resistance each consume their own suffix. Exclude combined/all-resistance explicit affixes from our item pool, even where reference games offer exceptions.
- Enforce the rare-item maximum of three suffixes; an ordinary rare cannot roll all four resistance suffixes at once.
- Keep base properties separate from explicit affix tier labels.
- Define an explicit migration policy for old prototype items before beta. Preserve saved test gear until that migration is implemented.

## Character XP rewards

- Maps award character XP on kills. Remove damage XP from legacy encounters before beta; the test arena currently keeps it for testing.
- Tune monster XP, level scaling and later party reward rules.

## Custom map authoring

- Integrate the Desktop RSPSi 1.16.1 editor after verifying a matching cache plugin through an unchanged-region export round trip (see MAP-PIPELINE.md).
- Author original map and house-base layouts with terrain, walls, buildings, lanes and encounter rooms; import matching client assets and server collision data.
- Add instance-aware door and scenery interactions. Settlement uses a copied full layout; Foundry and Courtyard have authored terrain/scenery/collision layouts.

## Item art

- Six crafting currencies now have dedicated IDs, names and original inventory icons. Replace reused ground orb/paper cache meshes with authored 3D currency models; preserve a readable silhouette and color for each currency.
- Expand the connected passive graph with additional clusters and cross-connections as the tree grows; larger trees will need panning and zooming.

## Expansion play checks

- Check E Whirlwind with and without Splash/Faster Attacks/Brutality, including typing E in chat.
- Compare all seventeen bases with `::arpgbases`; equip/unequip, craft, bank, and reconnect to confirm stats/metadata.
- Test normal-map pack density, caster reach, elite reward pacing and champion slam visibility across camera angles. Balance values remain provisional.
- Replace shared cache silhouettes/animations with authored weapon and encounter art after combat readability is proven.

## Skill loadout play checks

- K now supports five browsable skills, optional empty hotbar slots, and finite dropped-gem support copies with attach/move/remove by drag-and-drop. Test held drags, quick drags, off-panel cancellation, focus loss and resizable coordinates.
- Check that Q/W/E, clickable hotbar labels, cooldowns, tutorial prompts and saved order agree after swaps and reconnects.
- [x] Keyboard/modifier/mouse bindings with account persistence, conflict handling and game-thread dispatch. Synthetic AWT/live network checks passed; physical extra-button delivery still needs a working desktop helper.

## Authored weapon geometry

- Ten weapon meshes now replace the source geometry in ground and male/female equipment rendering. Validate hand grip and clipping during idle, movement, Q/W/E casts and normal attacks.
- Native mesh rendering and weapon-hand group transforms are covered by WeaponMeshCheck. Inventory art and skills/loadout UI stay unchanged.
- Expand bespoke armour silhouettes; nine currency/gem/scroll ground models now use authored geometry.

- [x] Repurpose legacy quest, prayer and magic tabs as passive-tree, support and skill destinations. No prayers, spellbook teleports or legacy magic progression; active abilities and supports live in the approved skills/loadout panel and travel uses maps/portals.
- Fullscreen: automatic canvas/input scaling, tracker below minimap, skill bar clear of chat; verify on the actual desktop after restart.

## Background QA

- `dev.ps1 Test` runs gameplay, persistence, packet fragmentation and native offscreen rendering without desktop input.
- App-owned LOCAL QA frame/state/action hooks support background client runs without desktop input. They require the actual logged-in fixture account and are disabled for ordinary client launches.
- Windows capture/input helper remains unreliable; do not claim physical mouse-button delivery from app-owned renders or synthetic AWT event checks.
- Secondary-monitor fullscreen, card layouts, equipment, Foundry navigation, boss effects and gem fusion have actual running-client evidence. Controls/services/loot have a later live pass; current Exile branding, authored NPCs and expanded crafting await the next gate.

## Remaining prototype quality limits

- Replace remaining legacy minimap chrome and any old NPC-dialogue surfaces with the shared palette.
- Worn equipment uses the legacy rig with original weapon geometry and fitted accents; it still needs bespoke character/outfit art for release quality.
- The new authored arenas reuse terrain/object assets; full RSPSi terrain exports remain future work.
- Support ownership is finite, but learned levels are shared per support type; assess per-copy level/socket identity before beta.
- Active skills beyond the three chosen slots are browsable; fully data-driven skill unlock/quest progression and final balance remain future content.
