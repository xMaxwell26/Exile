package com.koios.game.arpg;
import java.util.*;
import java.nio.charset.StandardCharsets;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;
/** Slot-specific display data; never modifies shared item definitions. */
public final class ItemTooltip {
 public static final int CHANNEL=65534;
 public static boolean supports(Item item){return item!=null&&(item.hasRoll()||CraftingCurrency.index(item.getId())>=0||item.getId()==MapRuns.PORTAL_SCROLL);}
 public static void clear(Player p,int widget){p.getSession().send(new SendStringPacket("clear|"+widget,CHANNEL));}
 public static void send(Player p,int widget,int slot,Item item){
  if(!supports(item)){p.getSession().send(new SendStringPacket("remove|"+widget+"|"+slot,CHANNEL));return;}
  p.getSession().send(new SendStringPacket(payload(widget,slot,item),CHANNEL));
 }
 public static String payload(int widget,int slot,Item item){
  ItemRoll r=item.getRoll();
  int color=r==null?0xD4B984:r.getRarity()==ItemRoll.Rarity.RARE?0xF0D578:r.getRarity()==ItemRoll.Rarity.MAGIC?0x8888FF:r.getRarity()==ItemRoll.Rarity.UNIQUE?0xC87539:0xD8D8D8;
  return "item|"+widget+"|"+slot+"|"+item.getId()+"|"+color+"|"+encode(lines(item,false))+"|"+encode(lines(item,true));
 }
 private static String encode(List<String> lines){return Base64.getEncoder().encodeToString(String.join("\n",lines).getBytes(StandardCharsets.UTF_8));}
 public static List<String> lines(Item item,boolean advanced){
  ItemRoll r=item.getRoll();List<String> lines=new ArrayList<>();
  if(!item.hasRoll()){
   lines.add(item.getDefinition().getName());int material=CraftingCurrency.index(item.getId());
   if(material>=0){lines.add(CraftingCurrency.description(material));lines.add("Right-click Use, then select equipment or a map.");if(material==4)lines.add("Corrupted items cannot use other crafting materials.");else if(material!=5)lines.add("The target must be identified and uncorrupted.");lines.add("One material is consumed after a successful craft.");}
   else if(item.getId()==MapRuns.PORTAL_SCROLL){lines.add("Read inside a map to open a return portal.");lines.add("Enter the portal to return to your private base.");lines.add("Your base portal lets you resume the same map.");lines.add("Maps close on logout or when abandoned.");}return lines;
  }
  lines.add(MapRuns.name(item));
  if(MapRuns.isGem(item)){lines.add("Requires character level "+r.getRequiredLevel());if(r.getGem().kind<0){lines.add("Right-click Craft to choose its permanent identity.");lines.add(r.getGem().support?"Choose a support, then socket it beside an active skill.":"Filter by weapon, choose a skill, then socket it in K.");lines.add("Crafting preserves this jewel's level.");return lines;}lines.add(r.getGem().support?"Drag into a support socket beside an active skill.":"Drag into one of the three active slots in K.");lines.add("Drag it back to inventory to remove it.");lines.add("The gem remains an item while configuring your loadout.");if(!r.getGem().support){lines.add("Use on another matching active gem to fuse.");lines.add("Fusion costs 25 x gem level coins; target gains one level.");lines.add("Maximum gem level: 20.");}return lines;}
  if(MapRuns.isMap(item)){ItemRoll.MapData map=r.getMap();lines.add("Map Tier: "+map.tier);lines.add("Open at your private map device");if((map.mods&1)!=0)lines.add("Monsters deal 30% more damage");if((map.mods&2)!=0)lines.add("Monsters have 100% more life");if((map.mods&4)!=0)lines.add("Abundant currency and equipment");if(map.mods==0)lines.add("No map modifiers");if(r.getCorruption()>0)lines.add("Corrupted");return lines;}
  lines.add(title(r.getRarity().name())+" / Requires level "+r.getRequiredLevel());
  if(advanced)lines.add("Item level: "+r.getItemLevel());
  if(!r.isIdentified())lines.add("Unidentified");
  Map<Stat,Double> properties=ArpgStats.equipment(new Item[]{item});
  if(properties.get(Stat.PHYSICAL_MAX)>0)lines.add("Physical Damage: "+damageRange(properties));
  if(item.getDefinition().isWeapon()){lines.add("Attacks per Second: "+number(properties.get(Stat.ATTACK_SPEED)));lines.add("Critical Chance: "+number(properties.get(Stat.CRITICAL_CHANCE))+"% / Multiplier: "+number(properties.get(Stat.CRITICAL_MULTIPLIER))+"%");}
  for(Stat defence:new Stat[]{Stat.ARMOUR,Stat.EVASION,Stat.ENERGY_SHIELD})if(properties.get(defence)>0)lines.add(title(defence.name())+": "+number(properties.get(defence)));
  Map<Stat,Double> baseProperties=baseProperties(item);
  for(ItemRoll.Affix a:r.getAffixes()){
   if(a.getLayer()==ItemRoll.Layer.BASE_IMPLICIT){appendBaseAffix(lines,a,baseProperties,advanced);continue;}
   if(!r.isIdentified() && a.getLayer()!=ItemRoll.Layer.BASE_IMPLICIT)continue;
   if(advanced)lines.add(title(a.getLayer().name())+" / "+title(a.getId())+((a.getLayer()==ItemRoll.Layer.PREFIX||a.getLayer()==ItemRoll.Layer.SUFFIX)?" / Tier "+a.getTier():""));
   Modifier low=null,high=null;
   for(Modifier m:a.getModifiers()){
    if(m.getStat()==Stat.PHYSICAL_MIN)low=m;
    if(m.getStat()==Stat.PHYSICAL_MAX)high=m;
   }
   boolean paired=low!=null && high!=null && low.getOperation()==high.getOperation();
   if(paired){
    if(low.getOperation()==Modifier.Operation.FLAT){
     if(a.getLayer()!=ItemRoll.Layer.BASE_IMPLICIT)lines.add("Adds "+number(low.getValue())+" to "+number(high.getValue())+" Physical Damage");
     else if(advanced)lines.add("Base Physical Damage: "+number(low.getValue())+"-"+number(high.getValue()));
    }else if(low.getValue()==high.getValue())lines.add(formatPhysicalPercent(low));
    else lines.add("Physical Damage modifier: "+number(low.getValue())+"%-"+number(high.getValue())+"%");
   }
   for(Modifier m:a.getModifiers())if(!paired || (m.getStat()!=Stat.PHYSICAL_MIN && m.getStat()!=Stat.PHYSICAL_MAX))lines.add(format(m));
  }
  if(r.getCorruption()>0)lines.add(r.getCorruption()==2?"Twice corrupted":"Corrupted");
  if(!advanced)lines.add("Hold Ctrl for affix details");
  return lines;
 }
 /** Calculate the base item alone, without explicit prefixes or suffixes. */
 private static Map<Stat,Double> baseProperties(Item item){
  ItemRoll roll=item.getRoll();List<ItemRoll.Affix> bases=new ArrayList<>();
  for(ItemRoll.Affix affix:roll.getAffixes())if(affix.getLayer()==ItemRoll.Layer.BASE_IMPLICIT)bases.add(affix);
  Item snapshot=new Item(item.getId());
  snapshot.setRoll(roll.changed(roll.getRarity(),true,roll.getCorruption(),bases,null));
  return ArpgStats.equipment(new Item[]{snapshot});
 }
 /** Combat rounds stochastic physical hits to these attainable integer bounds. */
 private static String damageRange(Map<Stat,Double> properties){double low=properties.get(Stat.PHYSICAL_MIN),high=Math.max(low,properties.get(Stat.PHYSICAL_MAX));return number(Math.floor(low))+"-"+number(Math.ceil(high));}
 private static boolean displayedProperty(Stat stat){switch(stat){case PHYSICAL_MIN:case PHYSICAL_MAX:case ATTACK_SPEED:case CRITICAL_CHANCE:case CRITICAL_MULTIPLIER:case ARMOUR:case EVASION:case ENERGY_SHIELD:return true;default:return false;}}
 /** Hide internal base offsets in normal view; advanced view shows actual final base values. */
 private static void appendBaseAffix(List<String> lines,ItemRoll.Affix affix,Map<Stat,Double> base,boolean advanced){
  boolean visible=advanced;for(Modifier modifier:affix.getModifiers())if(!displayedProperty(modifier.getStat()))visible=true;if(!visible)return;
  if(advanced)lines.add("Base implicit / "+title(affix.getId()));Set<Stat> printed=EnumSet.noneOf(Stat.class);
  for(Modifier modifier:affix.getModifiers()){
   Stat stat=modifier.getStat();if(!displayedProperty(stat)){lines.add(format(modifier));continue;}if(!advanced||!printed.add(stat))continue;
   if(stat==Stat.PHYSICAL_MIN||stat==Stat.PHYSICAL_MAX){if(printed.contains(stat==Stat.PHYSICAL_MIN?Stat.PHYSICAL_MAX:Stat.PHYSICAL_MIN))continue;lines.add("Base Physical Damage: "+damageRange(base));}
   else if(stat==Stat.ATTACK_SPEED)lines.add("Base Attacks per Second: "+number(base.get(stat)));
   else lines.add("Base "+title(stat.name())+": "+number(base.get(stat))+(stat==Stat.CRITICAL_CHANCE||stat==Stat.CRITICAL_MULTIPLIER?"%":""));
  }
 }
 private static String title(String s){String t=s.toLowerCase(Locale.ROOT).replace('_',' ');return Character.toUpperCase(t.charAt(0))+t.substring(1);}
 private static String number(double v){return String.format(Locale.ROOT,v==Math.rint(v)?"%.0f":"%.2f",v);}
 private static String formatPhysicalPercent(Modifier m){
  double v=m.getValue();return number(Math.abs(v))+"% "+(m.getOperation()==Modifier.Operation.MORE?(v<0?"less":"more"):(v<0?"reduced":"increased"))+" Physical Damage";
 }
 private static String format(Modifier m){
  double v=m.getValue();String stat=title(m.getStat().name());
  if(m.getOperation()==Modifier.Operation.FLAT)return (v>=0?"+":"")+number(v)+(m.getStat().name().endsWith("RESISTANCE")||m.getStat()==Stat.CRITICAL_CHANCE||m.getStat()==Stat.BLOCK_CHANCE?"% ":" ")+stat;
  return number(Math.abs(v))+"% "+(m.getOperation()==Modifier.Operation.MORE?(v<0?"less ":"more "):(v<0?"reduced ":"increased "))+stat;
 }
}
