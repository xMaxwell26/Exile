from pathlib import Path
import json
p=Path('Notorious/Server/data/arpg/bases.json');data=json.loads(p.read_text());
for base in data:
 for prop in base['stats']:
  if prop['growth']==.3333:prop['growth']=1/3
p.write_text(json.dumps(data,indent=2))
p=Path('Notorious/Server/test/TooltipRenderCheck.java');s=p.read_text().replace('SkillPanel.draw(font,0,new String[]{"38","50","1.8","0.0","5","0"});','SkillPanel.draw(font,2,new String[]{"38","50","1.8","0.0","5","0","1","1","1","1","1","2.4","3","5"});');p.write_text(s)
p=Path('PROTOTYPE-GUIDE.md');s=p.read_text().replace('**W** for Cleave.','**W** for Cleave and **E** for Whirlwind. Whirlwind strikes around you and can find a nearby enemy without an existing target. Splash expands its radius; Faster Attacks and Brutality also apply.');s+='''

## Melee and encounter expansion

New drops use seven data-driven equipment bases: Rusted Sword (balanced), Carving Dagger (fast, higher crit), Worn Mace (slow, heavy damage/accuracy), Splitting Axe (critical multiplier), Ironward Mail (armour), Trailhide Vest (evasion), and Spiritweave Robe (energy shield). The tooltip shows physical damage, attacks per second, critical chance and defensive properties. Base names/stats survive crafting. Existing saved prototype items keep their original bases and rolls.

Use `::arpgbases` in the offline prototype to receive all seven normal, identified level-one bases for comparison; overflow uses the normal stash/mailbox grant handling. These are test samples, not a production reward.

Normal maps have three packs, each with an empowered elite, and a champion placed farther from the entrance. Zombies/skeletons use physical attacks, casters attack with fire from range, and spiders apply chaos damage. Elites have more life/damage and guarantee equipment plus extra crafting currency and kill XP. The introduction stays simple.

The normal-map champion periodically targets the player's current tile for a physical slam. The orange tile outlines and objective countdown give 1.8 seconds to leave the marked area. The attack hits the original area, so movement avoids it. Returning home, dying, killing the boss or closing the map clears pending hazards. The client marker still needs a live play check for camera/terrain readability.

Whirlwind uses 24 base mana, a five-second base cooldown and 115% weapon damage. Higher skill gems can upgrade it through Crafting. It has its own supports, cooldown and saved gem level. Existing five-value gem saves load with a level-one Whirlwind; old Q/W support assignments retain their meaning.
''';p.write_text(s)
p=Path('TODO.md');s=p.read_text();s+='''
## Expansion play checks

- Check E Whirlwind with and without Splash/Faster Attacks/Brutality, including typing E in chat.
- Compare all seven bases with `::arpgbases`; equip/unequip, craft, bank, and reconnect to confirm stats/metadata.
- Test normal-map pack density, caster reach, elite reward pacing and champion slam visibility across camera angles. Balance values remain provisional.
- Replace shared cache silhouettes/animations with authored weapon and encounter art after combat readability is proven.
''';p.write_text(s)
