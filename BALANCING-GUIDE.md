# Editing monsters, loot and equipment

This is the authoring entry point for the prototype. The original Desktop project is untouched; edit the files in this POERSPS workspace. Save a copy of a data file before a balance experiment. JSON changes need a server restart; Java changes need a build and restart. Client models/names also need a client restart.

## Where to change a value

| What you want to change | File under `Notorious/Server/data/arpg/` |
| --- | --- |
| Kill XP, drop chance, stack quantity, currency weights, NPC-specific loot | `drops.json` |
| Monster life, damage, attack interval, reach, damage type | `enemies.json` |
| Weapon/armour base stats, level requirements, available bases | `bases.json` |
| Affix tiers, item-level gates, roll ranges, relative weights | `affixes.json` |

`_comment` strings inside drops.json explain nearby settings. They are ignored by the loader. Keep commas and quotes valid; do not add `//` comments to JSON. For quick feedback, run `tools/ValidateContent.ps1` from PowerShell after an initial build. It validates drops, enemies, bases and affix ranges without starting a server or writing saves. Run `dev.ps1 Test` for the broader reward-generation, invalid-data and tutorial-progression checks.

## Strict drop-file validation

Drop files use strict JSON: double-quoted keys, no trailing commas, no `//` or
block comments, and no duplicate keys. Unknown fields fail with their exact path
(for example `drops.json.tables[0].rolls[0].chanceDenominater`), so a misspelled
balance knob cannot silently use its default. Numbers must be exact 32-bit
integers, not quoted strings, fractions or overflowing values; flags are literal
`true`/`false`. Optional fields may be omitted to use the documented defaults,
but explicit `null` is rejected. Any underscore-prefixed **string** field can
hold an author note. Validation and preview never replace the live catalog.

## Reading a drop table

`normalTable`, `eliteTable` and `bossTable` select the default table for each rank. Each table has a unique `id`, `experiencePerTier` and a list of `rolls`.

- XP is `experiencePerTier × map tier`, awarded on death once.
- Each row rolls independently. Two successful rows mean two drops. There is no shared total chance across rows.
- `chanceNumerator: 1, chanceDenominator: 10` means a 10% chance. `1/1` is guaranteed; `0/1` disables a row without deleting it.
- `minQuantity` and `maxQuantity` are inclusive. Set both to the same number for a fixed stack.
- `minimumMapTier` defaults to1. A row below that tier is skipped.
- `excludeIntroduction: true` excludes that row from the tutorial encounter.
- `abundantChanceDenominator` optionally replaces the denominator on an Abundant currency map. `abundantQuantityMultiplier` defaults to1. These settings are per row, not a blanket multiplier for every item.

The current common pool uses relative weights. Weight30 is thirty times as likely as weight1 **within that one pool**. Its chance is `weight ÷ sum of pool weights`. This is separate from the row's chance to roll the pool at all. For example, reducing Exalted weight1 does not increase the number of currency drops; it only changes which material wins.

### Drop types

| `type` | Required setting | Result |
| --- | --- | --- |
| `ITEM` | `itemId` | Ordinary stackable/cache item, such as995 coins or22406 Portal Scrolls |
| `POOL` | `pool` | One weighted item choice from a named pool |
| `EQUIPMENT` | none | A level-eligible random weapon or armour base with a fresh roll UUID |
| `BASE` | `baseId` | One named equipment base, still respecting its unlock level, with a fresh roll UUID |
| `GEM` | none | Active or support gem according to `supportChancePercent` |
| `SUPPORT_GEM` | none | Always a support gem |
| `MAP` | none | An optional normal map at the current tier |

Use EQUIPMENT or BASE for rolled equipment, GEM for gems and MAP for maps. Do not put a custom equipment/gem/map item ID in ITEM or a plain-item pool: that would create a shell without its rolled metadata. Rolled rows require quantity1; the loader rejects larger quantities because each item needs its own identity.

Equipment item level is `tier × equipmentLevelPerTier` (default2); introduction equipment stays level1. `normalWeight`, `magicWeight`, `rareWeight` default to1/2/2:20% normal,40% magic,40% rare. Set `identifiedChancePercent` to control identified drops (default33). Affix eligibility still comes from affixes.json. A named BASE below its own unlock level is skipped, even if you accidentally set its map-tier gate too low.

Gem level is `gemLevelBase + floor(tier / gemLevelTierDivisor)`, clamped1..20. `supportChancePercent: 50` gives an equal active/support split for GEM. These fields do not affect SUPPORT_GEM's type.

## Copy an enemy's loot table

Example: give the Furnace Sentinel (NPC15001) a chance at a Cinderheart Maul.

1. Copy the complete `map_normal` object in `tables`, including all of its rolls. Rename the copied `id` to `furnace_sentinel`. This preserves the ordinary rewards while you experiment.
2. Add the following row to the copied table's rolls. `siege_maul` is the base's internal id from bases.json; its display name is Cinderheart Maul.

```json
{
  "label": "Signature Cinderheart Maul",
  "type": "BASE",
  "baseId": "siege_maul",
  "minimumMapTier": 3,
  "chanceNumerator": 1,
  "chanceDenominator": 20,
  "equipmentLevelPerTier": 2,
  "normalWeight": 0,
  "magicWeight": 3,
  "rareWeight": 1,
  "identifiedChancePercent": 50
}
```

3. Change the top-level `npcOverrides` object to:

```json
"npcOverrides": {
  "15001": "furnace_sentinel"
}
```

4. Run checks, restart the server, and open a fresh Foundry map. The row is5% per eligible kill, and successful drops are75% magic/25% rare. Below tier3 it cannot drop. Every copy has independent affixes and a unique identity.

An override replaces the entire rank table for that NPC, including its elite variant. If you want elite-specific rewards too, make that policy explicit in code rather than assuming the normal table is merged automatically. Introduction encounters ignore NPC overrides so experimental encounter tables cannot change their scripted checkpoints.

The champion's next-map reward and return portal are guaranteed separately in MapRuns.killed. The tutorial's first Foundry map is also separate. Those are progression mechanics, not optional random loot; removing a MAP row here will not remove those guarantees. Loot goes onto the private instance floor, never straight into a player's inventory.

## Monster balance

Each enemies.json role defines `healthMultiplier`, `damageBase`, `damageGrowth`, `attackTicks`, `reach` and `damageType`. One server tick is0.6seconds. Larger `attackTicks` means slower attacks.

- Ordinary base life: `(22 + 8 × tier) × healthMultiplier`, rounded.
- Champion base life: `(120 + 35 × tier) × healthMultiplier`, rounded.
- Base hit: `ceil(damageBase + damageGrowth × tier)`.
- Elites and map modifiers can then multiply those values; see the commented encounter code and generated balance logs before comparing final difficulty.
- Types are PHYSICAL, FIRE, COLD, LIGHTNING or CHAOS. Damage then goes through the corresponding character defences.

The seven base roles are shared by matching Foundry/Courtyard archetypes. A supported `npcId` override changes one named custom creature while keeping the shared role intact. Do not change a creature's footprint without checking client mesh dimensions, pathfinding, door clearance and reach together.

`attackAnimation: -1` uses the cache NPC's default. The authored Furnace Sentinel15001 and Briar Warden15007 use verified skeleton punch5487. `windupTicks: 1` delays impact0.6seconds after the visible attack starts; leaving range or line of sight dodges it. `attackTicks` measures the full start-to-start interval including windup. Do not choose an arbitrary animation ID: a valid sequence can still distort a different rig. Run the native all-frame NPC checks and inspect the actual model before changing it. Windup must be shorter than the attack interval.

## Bases and affixes

A base's `id` is a stable internal name; `itemId` is its dedicated custom ID; `templateId` supplies legacy equip/rig behavior. `minLevel` gates selection and wearing. Each stat entry has `base` and `growth`; its value is `base + growth × item level`. Physical bounds, armour, evasion and ES are floored to integers. Creating a base by copying an entry still requires matching client art/model/name wiring.

Affix `tier` labels run strongestT1 downward. `level` is the item-level unlock, not the player's RS skill. `weight` is relative among eligible tiers within that affix family. `low`/`high` arrays match the listed stats in order. Keep prefix/suffix budgets and separate elemental/chaos resistance suffixes. The validator rejects illegal generated rolls.

## Code map

- `MapDropTables.java`: commented loader, validation, rank/NPC lookup, independent rolls, weighted pool selection and fresh rolled-item creation.
- `MapRuns.killed`: once-per-death XP/ground-drop commit and guaranteed mission progression.
- `EncounterProfiles.java`: monster formula data and NPC-specific overrides.
- `EncounterNpcs.java` and client `CustomNpcs.java`: dedicated NPC identity, names and cache rig aliases.
- Client `CustomNpcModels.java`: original geometry and bone binding. Follow its model-authoring notes before copying a creature.
- `CurrencyCrafting.java`: validated immutable crafting outcomes; `PrototypePanel.craft` validates and commits the currency transaction.

Useful evidence is under logs/ and Server/build/. Assertion counts establish specific contracts; they do not prove final balance or visual quality. Keep small changes, test one factor at a time, and compare the resulting encounter in the actual client.
