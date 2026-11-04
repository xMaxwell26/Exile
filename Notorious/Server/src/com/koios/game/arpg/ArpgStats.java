package com.koios.game.arpg;
import java.util.*;
import com.koios.game.model.item.Item;
/** Recomputed snapshot: avoids stale equipment caches during loading and banking. */
public final class ArpgStats {
 public static Map<Stat,Double> calculate(Map<Stat,Double> bases,Iterable<Modifier> modifiers){
  EnumMap<Stat,Double> flat=new EnumMap<>(Stat.class),inc=new EnumMap<>(Stat.class),more=new EnumMap<>(Stat.class),result=new EnumMap<>(Stat.class);
  for(Modifier m:modifiers){m.validate();Stat s=m.getStat();double v=m.getValue();
   switch(m.getOperation()){
    case FLAT: flat.put(s,flat.getOrDefault(s,0d)+v);break;
    case INCREASED: inc.put(s,inc.getOrDefault(s,0d)+v);break;
    case MORE: more.put(s,more.getOrDefault(s,1d)*(1+v/100));break;
   }
  }
  for(Stat s:Stat.values()){
   double v=(bases.getOrDefault(s,0d)+flat.getOrDefault(s,0d))*Math.max(0,1+inc.getOrDefault(s,0d)/100)*more.getOrDefault(s,1d);
   if(!Double.isFinite(v))throw new IllegalArgumentException("Stat overflow");
   if(s.name().endsWith("RESISTANCE"))v=Math.max(-200,Math.min(75,v));
   else if(s==Stat.BLOCK_CHANCE)v=Math.max(0,Math.min(75,v));
   else if(s==Stat.CRITICAL_CHANCE)v=Math.max(0,Math.min(100,v));
   else v=Math.max(0,v);
   result.put(s,v);
  }
  return Collections.unmodifiableMap(result);
 }
 public static Map<Stat,Double> equipment(Item[] equipment){return equipment(equipment,1);}
 public static Map<Stat,Double> equipment(Item[] equipment,int level){return equipment(equipment,level,Collections.emptyList());}
 public static Map<Stat,Double> equipment(Item[] equipment,int level,Iterable<Modifier> extra){
  List<Modifier> mods=new ArrayList<>();Map<Stat,Double> bases=new EnumMap<>(Stat.class);
  bases.put(Stat.MANA_REGEN,5d);bases.put(Stat.ACCURACY,100d+10*(level-1));bases.put(Stat.LIFE_REGEN,1d);bases.put(Stat.LIFE,100d+10*(level-1));bases.put(Stat.MANA,50d);bases.put(Stat.ATTACK_SPEED,1d);bases.put(Stat.CRITICAL_CHANCE,5d);bases.put(Stat.CRITICAL_MULTIPLIER,150d);
  for(Item item:equipment)if(item!=null && item.hasRoll())for(ItemRoll.Affix a:item.getRoll().getAffixes())
   if(item.getRoll().isIdentified() || a.getLayer()==ItemRoll.Layer.BASE_IMPLICIT)mods.addAll(a.getModifiers());
  for(Modifier m:extra)mods.add(m);
  return calculate(bases,mods);
 }
}
