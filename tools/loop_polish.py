from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
p=r/'game/arpg/AffixCatalog.java';s=p.read_text().replace('public static Item equipment(int level,boolean weapon,Random random){','public static Item starter(boolean weapon){Item item=equipment(1,weapon,new Random(weapon?1:2));item.setRoll(item.getRoll().changed(ItemRoll.Rarity.NORMAL,true,0,roll(item,ItemRoll.Rarity.NORMAL,new Random(1)),null));return item;}\n public static Item equipment(int level,boolean weapon,Random random){');p.write_text(s)
p=r/'game/arpg/PrototypePanel.java';s=p.read_text().replace('AffixCatalog.equipment(1,true,new Random(1))','AffixCatalog.starter(true)').replace('AffixCatalog.equipment(1,false,new Random(2))','AffixCatalog.starter(false)').replace('"Abandon active map and release its instance"','run.complete?"Close completed map (keep rewards)":"Abandon active map and release its instance"');p.write_text(s)
p=r/'game/arpg/SkillState.java';s=p.read_text().replace('public void activate(){','public void refill(double maximum){mana=maximum;java.util.Arrays.fill(cooldowns,0);actionLock=0;lastPayload=null;}\n public void activate(){');p.write_text(s)
p=r/'game/arpg/MapRuns.java';s=p.read_text().replace('p.getArpgVitals().refill(p.getArpgStats());','p.getArpgVitals().refill(p.getArpgStats());p.getArpgSkills().refill(p.getArpgStats().get(Stat.MANA));');s=s.replace('public static void cleanup(Player p){abandon(p);','public static void cleanup(Player p){Run run=RUNS.remove(p);if(run!=null){for(MapMob mob:run.mobs)remove(mob);run.region.close();}');p.write_text(s)
p=r/'game/arpg/TestArena.java';s=p.read_text().replace('p.getArpgVitals().refill(p.getArpgStats());','p.getArpgVitals().refill(p.getArpgStats());p.getArpgSkills().refill(p.getArpgStats().get(Stat.MANA));');p.write_text(s)
p=r/'game/content/skill/Skill.java';s=p.read_text().replace('if(player!=null)player.getArpgVitals().refill(player.getArpgStats());','if(player!=null){player.getArpgVitals().refill(player.getArpgStats());player.getArpgSkills().refill(player.getArpgStats().get(com.koios.game.arpg.Stat.MANA));}');p.write_text(s)
# Distinct hybrid family instead of duplicate pure physical mods.
import json
p=Path('Notorious/Server/data/arpg/affixes.json');data=json.loads(p.read_text());
for f in data:
 if f['id']=='martial':
  f['stats'].append('ACCURACY')
  for tier in f['tiers']:tier['low'].append(tier['low'][0]);tier['high'].append(tier['high'][0])
p.write_text(json.dumps(data,indent=2))
