from pathlib import Path
import json
rows=[]
def base(id,item,name,weapon,stats): rows.append(dict(id=id,itemId=item,name=name,weapon=weapon,stats=[dict(stat=s,base=v,growth=g) for s,v,g in stats]))
base('rusted_sword',1277,'Rusted Sword',True,[('PHYSICAL_MIN',3,.3333),('PHYSICAL_MAX',7,.5)])
base('carving_dagger',1205,'Carving Dagger',True,[('PHYSICAL_MIN',2,.25),('PHYSICAL_MAX',5,.4),('ATTACK_SPEED',.65,0),('CRITICAL_CHANCE',3,0)])
base('worn_mace',1420,'Worn Mace',True,[('PHYSICAL_MIN',6,.4),('PHYSICAL_MAX',13,.7),('ATTACK_SPEED',-.25,0),('ACCURACY',20,1)])
base('splitting_axe',1351,'Splitting Axe',True,[('PHYSICAL_MIN',4,.3333),('PHYSICAL_MAX',10,.6),('ATTACK_SPEED',-.1,0),('CRITICAL_MULTIPLIER',20,0)])
base('ironward_mail',1115,'Ironward Mail',False,[('ARMOUR',25,3)])
base('trailhide_vest',1129,'Trailhide Vest',False,[('EVASION',40,4)])
base('spiritweave_robe',577,'Spiritweave Robe',False,[('ENERGY_SHIELD',18,2)])
Path('Notorious/Server/data/arpg/bases.json').write_text(json.dumps(rows,indent=2))
p=Path('Notorious/Server/src/com/koios/game/arpg/EquipmentBases.java');p.write_text('''package com.koios.game.arpg;
import java.util.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import com.google.gson.Gson;import com.koios.game.model.item.Item;
public final class EquipmentBases {
 public static final class Property {Stat stat;double base,growth;}
 public static final class Base {public String id,name;public int itemId;public boolean weapon;Property[] stats;}
 private static Base[] bases;
 public static Base[] all(){if(bases==null)try{bases=new Gson().fromJson(new String(Files.readAllBytes(Paths.get("data/arpg/bases.json")),StandardCharsets.UTF_8),Base[].class);}catch(Exception e){throw new IllegalStateException("Cannot load equipment bases",e);}return bases;}
 public static Base select(boolean weapon,Random random){List<Base> pool=new ArrayList<>();for(Base b:all())if(b.weapon==weapon)pool.add(b);return pool.get(random.nextInt(pool.size()));}
 public static Base starter(boolean weapon){for(Base b:all())if(b.id.equals(weapon?"rusted_sword":"ironward_mail"))return b;throw new IllegalStateException("Missing starter base");}
 public static Item create(Base base,int level){List<Modifier> mods=new ArrayList<>();for(Property prop:base.stats){double value=prop.base+prop.growth*level;if(prop.stat==Stat.PHYSICAL_MIN||prop.stat==Stat.PHYSICAL_MAX||prop.stat==Stat.ARMOUR||prop.stat==Stat.EVASION||prop.stat==Stat.ENERGY_SHIELD)value=Math.floor(value);mods.add(new Modifier(prop.stat,Modifier.Operation.FLAT,value));}Item item=new Item(base.itemId);item.setRoll(new ItemRoll(ItemRoll.Rarity.NORMAL,level,Math.max(1,level-3),true,0,Collections.singletonList(new ItemRoll.Affix("base_"+base.id,ItemRoll.Layer.BASE_IMPLICIT,1,mods.toArray(new Modifier[0])))));return item;}
 public static String name(Item item){if(item.hasRoll())for(ItemRoll.Affix affix:item.getRoll().getAffixes())if(affix.getLayer()==ItemRoll.Layer.BASE_IMPLICIT)for(Base b:all())if(affix.getId().equals("base_"+b.id))return b.name;return item.getDefinition().getName();}
}
''')
p=Path('Notorious/Server/src/com/koios/game/arpg/AffixCatalog.java');s=p.read_text();a=s.index(' public static Item starter(');b=s.index('  ItemRoll.Rarity rarity=',a);s=s[:a]+''' public static Item starter(boolean weapon){return EquipmentBases.create(EquipmentBases.starter(weapon),1);}
 public static Item equipment(int level,boolean weapon,Random random){
  Item item=EquipmentBases.create(EquipmentBases.select(weapon,random),level);
'''+s[b:];p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/MapRuns.java');s=p.read_text().replace(':item.getDefinition().getName();',':EquipmentBases.name(item);');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/ItemTooltip.java');s=p.read_text();needle='  for(ItemRoll.Affix a:r.getAffixes()){';idx=s.index(needle);s=s[:idx]+'''  if(item.getDefinition().isWeapon()){lines.add("Attacks per Second: "+number(properties.get(Stat.ATTACK_SPEED)));lines.add("Critical Chance: "+number(properties.get(Stat.CRITICAL_CHANCE))+"% / Multiplier: "+number(properties.get(Stat.CRITICAL_MULTIPLIER))+"%");}
  for(Stat defence:new Stat[]{Stat.ARMOUR,Stat.EVASION,Stat.ENERGY_SHIELD})if(properties.get(defence)>0)lines.add(title(defence.name())+": "+number(properties.get(defence)));
'''+s[idx:];p.write_text(s)
p=Path('Notorious/Server/data/arpg/affixes.json');data=json.loads(p.read_text());
for id,slots,layer,stat,lo,hi in [('of_execution','weapon','SUFFIX','CRITICAL_MULTIPLIER',8,15),('regenerating','armour','PREFIX','LIFE_REGEN',1,2),('of_clarity','armour','SUFFIX','MANA_REGEN',1,2),('of_recharge','armour','SUFFIX','ES_RECHARGE',5,10)]:
 data.append(dict(id=id,slots=slots,layer=layer,stats=[stat],operation='FLAT',tiers=[dict(tier=10-i,level=l,weight=100,low=[round(lo*(1+.22*i),2)],high=[round(hi*(1+.22*i),2)]) for i,l in enumerate([1,5,10,20,30,40,50,60,75,85])]))
p.write_text(json.dumps(data,indent=2))
p=Path('Notorious/Server/src/com/koios/game/arpg/PassiveTree.java');s=p.read_text().replace('"Sword training"','"Weapon training"');p.write_text(s)
