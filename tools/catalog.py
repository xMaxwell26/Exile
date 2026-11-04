from pathlib import Path
import json
r=Path('Notorious/Server/src/com/koios')
def put(p,s):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a[:40]);p.write_text(s.replace(a,b))
families=[]
def fam(id,layer,slots,stats,op,low,high):
 tiers=[]
 for i,lvl in enumerate([1,5,10,20,30,40,50,60,75,85]):tiers.append(dict(tier=10-i,level=lvl,weight=100,low=[round(v*(1+i*.6),2) for v in low],high=[round(v*(1+i*.6),2) for v in high]))
 families.append(dict(id=id,layer=layer,slots=slots,stats=stats,operation=op,tiers=tiers))
fam('sharpened','PREFIX','weapon',['PHYSICAL_MIN','PHYSICAL_MAX'],'FLAT',[1,3],[3,7])
fam('tempered','PREFIX','weapon',['PHYSICAL_MIN','PHYSICAL_MAX'],'INCREASED',[10,10],[20,20])
fam('martial','PREFIX','weapon',['PHYSICAL_MIN','PHYSICAL_MAX'],'INCREASED',[5,5],[10,10])
for id,stat,lo,hi in [('healthy','LIFE',10,25),('sheltering','ENERGY_SHIELD',10,20),('plated','ARMOUR',20,50),('nimble','EVASION',20,50),('reservoir','MANA',5,15)]:fam(id,'PREFIX','armour',[stat],'FLAT',[lo],[hi])
for id,stat,lo,hi in [('keen','CRITICAL_CHANCE',5,12),('quick','ATTACK_SPEED',3,7),('precise','ACCURACY',10,25)]:fam(id,'SUFFIX','weapon',[stat],'INCREASED' if id!='precise' else 'FLAT',[lo],[hi])
for stat in ['FIRE_RESISTANCE','COLD_RESISTANCE','LIGHTNING_RESISTANCE','CHAOS_RESISTANCE']:fam(stat.lower(),'SUFFIX','armour',[stat],'FLAT',[6],[12])
p=Path('Notorious/Server/data/arpg/affixes.json');p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(families,indent=2))
put(r/'game/arpg/AffixCatalog.java','''package com.koios.game.arpg;
import java.util.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;import com.koios.game.model.item.Item;
public final class AffixCatalog {
 public static final class Tier {int tier,level,weight;double[] low,high;}
 public static final class Family {String id,slots;ItemRoll.Layer layer;Stat[] stats;Modifier.Operation operation;Tier[] tiers;}
 private static Family[] catalog;
 private static Family[] definitions(){if(catalog==null)try{catalog=new Gson().fromJson(new String(Files.readAllBytes(Paths.get("data/arpg/affixes.json")),StandardCharsets.UTF_8),Family[].class);}catch(Exception e){throw new IllegalStateException("Cannot load affix catalog",e);}return catalog;}
 public static List<ItemRoll.Affix> roll(Item item,ItemRoll.Rarity rarity,Random random){
  ItemRoll old=item.getRoll();List<ItemRoll.Affix> out=new ArrayList<>();for(ItemRoll.Affix a:old.getAffixes())if(a.getLayer()!=ItemRoll.Layer.PREFIX&&a.getLayer()!=ItemRoll.Layer.SUFFIX)out.add(a);
  int count=rarity==ItemRoll.Rarity.NORMAL?0:rarity==ItemRoll.Rarity.MAGIC?1+random.nextInt(2):4+random.nextInt(3);
  List<Family> available=new ArrayList<>();boolean weapon=item.getDefinition().isWeapon();for(Family family:definitions())if(family.slots.equals(weapon?"weapon":"armour"))available.add(family);Collections.shuffle(available,random);
  int prefixes=0,suffixes=0,limit=rarity==ItemRoll.Rarity.MAGIC?1:3;
  for(Family f:available){if(count==0)break;if(f.layer==ItemRoll.Layer.PREFIX&&prefixes>=limit||f.layer==ItemRoll.Layer.SUFFIX&&suffixes>=limit)continue;
   List<Tier> eligible=new ArrayList<>();int weight=0;for(Tier t:f.tiers)if(t.level<=old.getItemLevel()){eligible.add(t);weight+=t.weight;}
   if(weight==0)continue;int ticket=random.nextInt(weight);Tier tier=eligible.get(0);for(Tier t:eligible){ticket-=t.weight;if(ticket<0){tier=t;break;}}
   Modifier[] mods=new Modifier[f.stats.length];double fraction=random.nextDouble();for(int i=0;i<mods.length;i++){double v=Math.round(tier.low[i]+fraction*(tier.high[i]-tier.low[i]));mods[i]=new Modifier(f.stats[i],f.operation,v);}
   out.add(new ItemRoll.Affix(f.id,f.layer,tier.tier,mods));if(f.layer==ItemRoll.Layer.PREFIX)prefixes++;else suffixes++;count--;
  }
  return out;
 }
 public static Item equipment(int level,boolean weapon,Random random){
  Item item=new Item(weapon?1277:1115);List<ItemRoll.Affix> base=Arrays.asList(weapon?new ItemRoll.Affix("sword_base",ItemRoll.Layer.BASE_IMPLICIT,1,new Modifier(Stat.PHYSICAL_MIN,Modifier.Operation.FLAT,3+level/3),new Modifier(Stat.PHYSICAL_MAX,Modifier.Operation.FLAT,7+level/2)):new ItemRoll.Affix("armour_base",ItemRoll.Layer.BASE_IMPLICIT,1,new Modifier(Stat.ARMOUR,Modifier.Operation.FLAT,25+level*3)));
  item.setRoll(new ItemRoll(ItemRoll.Rarity.NORMAL,level,Math.max(1,level-3),true,0,base));
  ItemRoll.Rarity rarity=random.nextInt(5)==0?ItemRoll.Rarity.NORMAL:random.nextBoolean()?ItemRoll.Rarity.MAGIC:ItemRoll.Rarity.RARE;
  item.setRoll(item.getRoll().changed(rarity,random.nextInt(3)==0,0,roll(item,rarity,random),null));AffixRules.validateGenerated(item.getRoll());return item;
 }
}
''')
p=r/'game/arpg/RegionInstance.java';s=p.read_text().replace('p.getCurrentRegion().setAs(p.getLocation());','p.getCurrentRegion().setAs(new Location(centreX,centreY,0));').replace('writeBits(1,true)','writeBits(true)');p.write_text(s)
