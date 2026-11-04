package com.koios.game.arpg;
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
 public static Item starter(boolean weapon){return EquipmentBases.create(EquipmentBases.starter(weapon),1);}
 public static Item equipment(int level,boolean weapon,Random random){
  Item item=EquipmentBases.create(EquipmentBases.select(weapon,random,level),level);
  ItemRoll.Rarity rarity=random.nextInt(5)==0?ItemRoll.Rarity.NORMAL:random.nextBoolean()?ItemRoll.Rarity.MAGIC:ItemRoll.Rarity.RARE;
  item.setRoll(item.getRoll().changed(rarity,random.nextInt(3)==0,0,roll(item,rarity,random),null));AffixRules.validateGenerated(item.getRoll());return item;
 }
}
