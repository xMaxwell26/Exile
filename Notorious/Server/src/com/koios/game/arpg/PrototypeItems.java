package com.koios.game.arpg;
import java.util.*;
import com.koios.game.model.item.Item;
public final class PrototypeItems {
 private static ItemRoll.Affix resistance(Stat stat){
  return new ItemRoll.Affix(stat.name().toLowerCase(java.util.Locale.ROOT)+"_ward",ItemRoll.Layer.SUFFIX,stat==Stat.CHAOS_RESISTANCE?9:8,new Modifier(stat,Modifier.Operation.FLAT,stat==Stat.CHAOS_RESISTANCE?20:25));
 }
 public static Item armour(String profile){
  List<Modifier> base=new ArrayList<>();
  if(profile.equals("armour")||profile.equals("hybrid"))base.add(new Modifier(Stat.ARMOUR,Modifier.Operation.FLAT,profile.equals("hybrid")?100:250));
  if(profile.equals("evasion")||profile.equals("hybrid"))base.add(new Modifier(Stat.EVASION,Modifier.Operation.FLAT,profile.equals("hybrid")?100:250));
  if(profile.equals("es")||profile.equals("hybrid"))base.add(new Modifier(Stat.ENERGY_SHIELD,Modifier.Operation.FLAT,profile.equals("hybrid")?30:80));
  if(base.isEmpty())throw new IllegalArgumentException("Use armour, evasion, es or hybrid");
  Item item=new Item(profile.equals("evasion")?22425:profile.equals("es")?22426:22424);item.setRoll(new ItemRoll(ItemRoll.Rarity.RARE,1,1,true,0,Arrays.asList(
   new ItemRoll.Affix(profile+"_base",ItemRoll.Layer.BASE_IMPLICIT,1,base.toArray(new Modifier[0])),
   new ItemRoll.Affix("healthy",ItemRoll.Layer.PREFIX,9,new Modifier(Stat.LIFE,Modifier.Operation.FLAT,30)),
   resistance(profile.equals("evasion")?Stat.LIGHTNING_RESISTANCE:Stat.FIRE_RESISTANCE),
   resistance(profile.equals("hybrid")?Stat.LIGHTNING_RESISTANCE:Stat.COLD_RESISTANCE),
   resistance(profile.equals("armour")?Stat.LIGHTNING_RESISTANCE:Stat.CHAOS_RESISTANCE)
  )));AffixRules.validateGenerated(item.getRoll());return item;
 }
 public static Item sword(Random random){
  Item item=new Item(22420);
  item.setRoll(new ItemRoll(ItemRoll.Rarity.RARE,1,1,true,0,Arrays.asList(
   new ItemRoll.Affix("sword_base",ItemRoll.Layer.BASE_IMPLICIT,1,new Modifier(Stat.PHYSICAL_MIN,Modifier.Operation.FLAT,3),new Modifier(Stat.PHYSICAL_MAX,Modifier.Operation.FLAT,7)),
   new ItemRoll.Affix("sharpened",ItemRoll.Layer.PREFIX,10,new Modifier(Stat.PHYSICAL_MIN,Modifier.Operation.FLAT,1+random.nextInt(4)),new Modifier(Stat.PHYSICAL_MAX,Modifier.Operation.FLAT,3+random.nextInt(6))),
   new ItemRoll.Affix("healthy",ItemRoll.Layer.PREFIX,9,new Modifier(Stat.LIFE,Modifier.Operation.FLAT,10+random.nextInt(31))),
   new ItemRoll.Affix("keen",ItemRoll.Layer.SUFFIX,8,new Modifier(Stat.CRITICAL_CHANCE,Modifier.Operation.INCREASED,10+random.nextInt(41))),
   new ItemRoll.Affix("quick",ItemRoll.Layer.SUFFIX,8,new Modifier(Stat.ATTACK_SPEED,Modifier.Operation.INCREASED,5+random.nextInt(16)))
  )));AffixRules.validateGenerated(item.getRoll());return item;
 }
}
