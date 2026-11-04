package com.koios.game.arpg;
import java.util.*;
/** Generation rules are separate from legacy loading so old test saves stay readable. */
public final class AffixRules {
 private static boolean resistance(Stat stat){return stat==Stat.FIRE_RESISTANCE||stat==Stat.COLD_RESISTANCE||stat==Stat.LIGHTNING_RESISTANCE||stat==Stat.CHAOS_RESISTANCE;}
 public static void validateGenerated(ItemRoll roll){
  roll.validate();Set<Stat> groups=EnumSet.noneOf(Stat.class);
  for(ItemRoll.Affix affix:roll.getAffixes()){
   if(affix.getLayer()!=ItemRoll.Layer.PREFIX&&affix.getLayer()!=ItemRoll.Layer.SUFFIX)continue;
   Set<Stat> resistances=EnumSet.noneOf(Stat.class);
   for(Modifier m:affix.getModifiers())if(resistance(m.getStat()))resistances.add(m.getStat());
   if(!resistances.isEmpty()&&affix.getLayer()!=ItemRoll.Layer.SUFFIX)throw new IllegalArgumentException("Explicit resistance must be a suffix");
   if(resistances.size()>1)throw new IllegalArgumentException("Each resistance needs its own suffix");
   for(Stat stat:resistances)if(!groups.add(stat))throw new IllegalArgumentException("Duplicate resistance affix group");
  }
 }
}
