from pathlib import Path
r=Path('Notorious/Server/src/com/koios');c=Path('Notorious/Client/src/com/koios/client')
def put(p,s):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a[:40]);p.write_text(s.replace(a,b))
put(r/'game/arpg/PrototypePanel.java','''package com.koios.game.arpg;
import java.util.*;import java.nio.charset.StandardCharsets;
import com.koios.GameService;import com.koios.game.model.player.Player;import com.koios.game.model.item.Item;import com.koios.network.out.impl.SendStringPacket;
public final class PrototypePanel {
 public static void show(Player p,String tab){
  List<String> rows=new ArrayList<>();
  if(tab.equals("tree")){
   rows.add("tree|"+Math.max(0,p.getArpgProgress().points()-p.getArpgPassives().spent())+"|"+p.getAttribute("free-refunds",false));
   for(PassiveTree.Node node:PassiveTree.NODES.values())rows.add(node.id+"|"+node.parent+"|"+p.getArpgPassives().has(node.id)+"|"+node.name+"|"+node.modifiers);
  }else{
   rows.add(tab);
   if(tab.equals("guide")){
    row(rows,"kit","Claim starter sword, armour and crafting supplies (once)");row(rows,"home","Enter your private house base");row(rows,"intro","Begin the introductory encounter from your base");row(rows,"tree","Spend passive points: click a node; Ctrl-click to refund");row(rows,"craft","Crafting bench: choose an item and a currency action");row(rows,"maps","Map device: consume a dropped map to open an instance");row(rows,"","Q Heavy Strike / W Cleave / K Supports / P Prototype hub");row(rows,"","Enter opens chat. All keyboard skills pause while typing.");row(rows,"","After level 10: limited map entries and death XP penalties.");row(rows,"","Map loot goes to inventory, overflowing into your stash.");
   }else if(tab.equals("craft")){
    int slot=p.getAttribute("craft-slot",-1);Item selected=p.getInventory().get(slot);
    if(selected!=null&&selected.hasRoll()&&selected.getRoll().getInstanceId().equals(p.getAttribute("craft-uuid",""))){
     row(rows,"items","Selected: "+MapRuns.name(selected)+" / "+selected.getRoll().getRarity());
     row(rows,"craft identify","Identify - 1 Water rune (Wisdom token)");row(rows,"craft magic","Normal to Magic - 1 Cosmic rune (Transmutation)");row(rows,"craft rare","Normal/Magic to Rare - 1 Nature rune (Alchemy)");row(rows,"craft reroll","Reroll explicit affixes / map mods - 1 Chaos rune");row(rows,"craft add","Add one affix / map mod - 1 Law rune");row(rows,"craft corrupt","Corrupt / twice corrupt - 1 Fire rune");row(rows,"items","Choose a different item");
    }else inventory(p,rows,false);
   }else if(tab.equals("maps")){
    MapRuns.Run run=MapRuns.run(p);row(rows,"home","Return to private house base");
    if(run!=null){row(rows,"resume","Re-enter active map / portals: "+run.portals+(run.complete?" / champion defeated":""));row(rows,"abandon","Abandon active map and release its instance");}
    else{row(rows,"intro","Run introduction (unlocks maps)");inventory(p,rows,true);}
   }
  }
  p.setAttribute("prototype-tab",tab);send(p,String.join("\\n",rows));
 }
 private static void inventory(Player p,List<String> rows,boolean maps){int page=p.getAttribute("prototype-page",0),shown=0,skipped=0;for(int i=0;i<p.getInventory().getCapacity();i++){
  Item item=p.getInventory().get(i);if(item==null||!item.hasRoll()||maps&&!MapRuns.isMap(item))continue;if(skipped++<page*12)continue;if(shown++>=12)break;
  row(rows,(maps?"open ":"select ")+i,"Slot "+(i+1)+": "+MapRuns.name(item));
 }row(rows,"page "+Math.max(0,page-1),"Previous page");row(rows,"page "+(page+1),"Next page");}
 private static void row(List<String> rows,String action,String label){rows.add(action+"\\t"+label);}
 private static void send(Player p,String text){p.getSession().send(new SendStringPacket(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)),65531));}
 public static boolean handle(Player p,String input){
  if(!input.startsWith("arpgproto "))return false;if(p.isDead()||p.isTrading()||p.getBankField().isBanking())return true;
  String action=input.substring(10);String[] args=action.split(" ");
  try{
   switch(args[0]){
    case "guide":case "tree":case "craft":case "maps":p.setAttribute("prototype-page",0);if(args.length==1){show(p,args[0]);return true;}if(args[0].equals("craft"))craft(p,args[1]);break;
    case "items":p.setAttribute("craft-slot",-1);show(p,"craft");return true;
    case "page":p.setAttribute("prototype-page",Math.max(0,Math.min(10,Integer.parseInt(args[1]))));show(p,p.getAttribute("prototype-tab","craft"));return true;
    case "home":MapRuns.home(p);show(p,"maps");return true;
    case "intro":MapRuns.start(p,-1,true);return true;
    case "open":MapRuns.start(p,Integer.parseInt(args[1]),false);return true;
    case "resume":MapRuns.enter(p);return true;
    case "abandon":MapRuns.abandon(p);show(p,"maps");return true;
    case "select":{int slot=Integer.parseInt(args[1]);Item item=p.getInventory().get(slot);if(item==null||!item.hasRoll())throw new IllegalArgumentException("Select rolled equipment or a map");p.setAttribute("craft-slot",slot);p.setAttribute("craft-uuid",item.getRoll().getInstanceId());show(p,"craft");return true;}
    case "allocate":p.getArpgPassives().allocate(Integer.parseInt(args[1]),p.getArpgProgress().points());show(p,"tree");return true;
    case "refund":{int id=Integer.parseInt(args[1]);if(!p.getAttribute("free-refunds",false)&&p.getInventory().getCount(995)<100)throw new IllegalArgumentException("Refund needs 100 coins");p.getArpgPassives().refund(id);if(!p.getAttribute("free-refunds",false))p.getInventory().remove(995,100);show(p,"tree");return true;}
    case "free":if(!GameService.isOnlineMode()){p.setAttribute("free-refunds",!p.getAttribute("free-refunds",false));show(p,"tree");}return true;
    case "kit":if(p.getAttribute("starter-claimed",false))throw new IllegalArgumentException("Starter kit already claimed");if(p.getInventory().freeSlots()<8)throw new IllegalArgumentException("Make eight inventory spaces first");
     MapRuns.grant(p,AffixCatalog.equipment(1,true,new Random(1)));MapRuns.grant(p,AffixCatalog.equipment(1,false,new Random(2)));MapRuns.grant(p,new Item(995,500));for(int id:new int[]{561,562,563,564,554,555})MapRuns.grant(p,new Item(id,5));p.setAttribute("starter-claimed",true);show(p,"guide");return true;
   }
   show(p,p.getAttribute("prototype-tab","craft"));
  }catch(RuntimeException e){p.sendMessage(e.getMessage()==null?"Prototype action failed":e.getMessage());}
  return true;
 }
 private static void craft(Player p,String action){
  if(!MapRuns.isHome(p))throw new IllegalArgumentException("Craft at your private base");int slot=p.getAttribute("craft-slot",-1);Item item=p.getInventory().get(slot);
  if(item==null||!item.hasRoll()||!item.getRoll().getInstanceId().equals(p.getAttribute("craft-uuid","")))throw new IllegalArgumentException("Choose the item again");
  ItemRoll old=item.getRoll(),next;Random random=new Random();int currency;
  if(old.getCorruption()>0&&!action.equals("identify")&&!action.equals("corrupt"))throw new IllegalArgumentException("Corrupted items cannot be crafted");
  if(!old.isIdentified()&&!action.equals("identify"))throw new IllegalArgumentException("Identify this item first");
  switch(action){
   case "identify":if(old.isIdentified())throw new IllegalArgumentException("Already identified");currency=555;next=old.changed(old.getRarity(),true,old.getCorruption(),old.getAffixes(),old.getMap());break;
   case "magic":if(old.getRarity()!=ItemRoll.Rarity.NORMAL)throw new IllegalArgumentException("Requires a normal item");currency=564;next=rolled(item,ItemRoll.Rarity.MAGIC,random);break;
   case "rare":if(old.getRarity()==ItemRoll.Rarity.RARE)throw new IllegalArgumentException("Already rare");currency=561;next=rolled(item,ItemRoll.Rarity.RARE,random);break;
   case "reroll":if(old.getRarity()==ItemRoll.Rarity.NORMAL)throw new IllegalArgumentException("Upgrade rarity first");currency=562;next=rolled(item,old.getRarity(),random);break;
   case "add":currency=563;
    if(MapRuns.isMap(item)){int available=(~old.getMap().mods)&7;if(available==0)throw new IllegalArgumentException("Map already has all modifiers");int bit=Integer.lowestOneBit(available);next=old.changed(ItemRoll.Rarity.RARE,true,0,old.getAffixes(),new ItemRoll.MapData(old.getMap().tier,old.getMap().mods|bit));}
    else{List<ItemRoll.Affix> affixes=new ArrayList<>(old.getAffixes());int limit=old.getRarity()==ItemRoll.Rarity.MAGIC?1:old.getRarity()==ItemRoll.Rarity.RARE?3:0;int pre=0,suf=0;Set<String> ids=new HashSet<>();for(ItemRoll.Affix a:affixes){ids.add(a.getId());if(a.getLayer()==ItemRoll.Layer.PREFIX)pre++;if(a.getLayer()==ItemRoll.Layer.SUFFIX)suf++;}
     ItemRoll.Affix chosen=null;for(int tries=0;tries<20&&chosen==null;tries++)for(ItemRoll.Affix a:AffixCatalog.roll(item,ItemRoll.Rarity.RARE,random))if(!ids.contains(a.getId())&&(a.getLayer()==ItemRoll.Layer.PREFIX&&pre<limit||a.getLayer()==ItemRoll.Layer.SUFFIX&&suf<limit)){chosen=a;break;}
     if(chosen==null)throw new IllegalArgumentException("No affix slot available");affixes.add(chosen);next=old.changed(old.getRarity(),true,0,affixes,null);}
    break;
   case "corrupt":currency=554;if(old.getCorruption()>=2)throw new IllegalArgumentException("Already twice corrupted");List<ItemRoll.Affix> affixes=new ArrayList<>(old.getAffixes());if(!MapRuns.isMap(item))affixes.add(new ItemRoll.Affix("corruption_"+(old.getCorruption()+1),ItemRoll.Layer.CORRUPTION_IMPLICIT,1,new Modifier(Stat.LIFE,Modifier.Operation.FLAT,5+random.nextInt(6))));next=old.changed(old.getRarity(),true,old.getCorruption()+1,affixes,old.getMap());break;
   default:throw new IllegalArgumentException("Unknown crafting action");
  }
  AffixRules.validateGenerated(next);if(p.getInventory().getCount(currency)<1)throw new IllegalArgumentException("You need the listed crafting currency");p.getInventory().remove(currency,1);item.setRoll(next);p.getInventory().fireItemChanged(slot);p.sendMessage("Crafted "+MapRuns.name(item));
 }
 private static ItemRoll rolled(Item item,ItemRoll.Rarity rarity,Random random){ItemRoll old=item.getRoll();if(MapRuns.isMap(item))return old.changed(rarity,true,0,old.getAffixes(),new ItemRoll.MapData(old.getMap().tier,1+random.nextInt(7)));return old.changed(rarity,true,0,AffixCatalog.roll(item,rarity,random),null);}
}
''')
# Integration points
p=r/'network/in/impl/PacketCommand.java';edit(p,'if(com.koios.game.arpg.ArpgSkills.handle(player,input))return;','if(com.koios.game.arpg.PrototypePanel.handle(player,input))return;\n        if(com.koios.game.arpg.ArpgSkills.handle(player,input))return;')
p=r/'game/content/command/impl/PlayerCommands.java';edit(p,'case "arena":','case "prototype":\n         com.koios.game.arpg.PrototypePanel.show(player,"guide");break;\n        case "passivefree":\n         com.koios.game.arpg.PrototypePanel.handle(player,"arpgproto free");break;\n        case "base":\n         com.koios.game.arpg.MapRuns.home(player);break;\n        case "arena":')
p=r/'game/arpg/ArpgSkills.java';edit(p,'TestArena.tick(p);','TestArena.tick(p);MapRuns.tick(p);')
p=r/'game/model/EntityCharacter.java';edit(p,'if(com.koios.game.arpg.TestArena.inside(this))return true;','if(com.koios.game.arpg.TestArena.inside(this)||isPlayer()&&com.koios.game.arpg.MapRuns.inside(toPlayer())||this instanceof com.koios.game.arpg.MapRuns.MapMob)return true;')
p=r/'game/model/World.java';edit(p,'com.koios.game.arpg.TestArena.cleanup(player);','com.koios.game.arpg.TestArena.cleanup(player);com.koios.game.arpg.MapRuns.cleanup(player);')
p=r/'network/in/impl/PacketRegionChanged.java';edit(p,'player.allowSaveRequests = true;','player.allowSaveRequests = true;com.koios.game.arpg.MapRuns.fixtures(player);')
p=r/'network/in/impl/PacketObjectOption.java';edit(p,'player.objectDistance = 1;','if(com.koios.game.arpg.MapRuns.object(player,player.objectId,player.objectX,player.objectY))return;\n            player.objectDistance = 1;')
p=r/'task/impl/PlayerDeathTask.java';edit(p,'this.player = player;','this.player = player;com.koios.game.arpg.MapRuns.death(player);');edit(p,'com.koios.game.arpg.TestArena.inside(player) ||','com.koios.game.arpg.TestArena.inside(player) || com.koios.game.arpg.MapRuns.inside(player) ||')
p=r/'game/content/combat/CombatExtras.java';edit(p,'if(p.getArpgProgress()!=null){p.getArpgProgress().gain(damage);return;}','if(p.getArpgProgress()!=null){if(!com.koios.game.arpg.MapRuns.inside(p))p.getArpgProgress().gain(damage);return;}')
p=r/'game/arpg/ItemTooltip.java';edit(p,'lines.add(item.getDefinition().getName());','lines.add(MapRuns.name(item));\n  if(MapRuns.isMap(item)){ItemRoll.MapData map=r.getMap();lines.add("Map Tier: "+map.tier);lines.add("Open at your private map device");if((map.mods&1)!=0)lines.add("Monsters deal 30% more damage");if((map.mods&2)!=0)lines.add("Monsters have 100% more life");if((map.mods&4)!=0)lines.add("Abundant currency and equipment");if(map.mods==0)lines.add("No map modifiers");if(r.getCorruption()>0)lines.add("Corrupted");return lines;}')
p=r/'game/model/player/io/PlayerSaver.java';edit(p,'writeValue(bw,"essential-arpg-xp",','writeValue(bw,"essential-starter-claimed",player.getAttribute("starter-claimed",false));\n                writeValue(bw,"essential-intro-complete",player.getAttribute("intro-complete",false));\n                writeValue(bw,"essential-arpg-xp",')
p=r/'game/model/player/io/PlayerLoader.java';edit(p,'case "essential-arpg-xp":','case "essential-starter-claimed":\n            player.setAttribute("starter-claimed",Boolean.parseBoolean(value));break;\n        case "essential-intro-complete":\n            player.setAttribute("intro-complete",Boolean.parseBoolean(value));break;\n        case "essential-arpg-xp":')
p=r/'game/arpg/MapRuns.java';s=p.read_text().replace('remove(this);','MapRuns.remove(this);').replace('if(!intro&&!isMap(item))','if(!intro&&!p.getAttribute("intro-complete",false))throw new IllegalArgumentException("Finish the introduction first");if(!intro&&!isMap(item))').replace('new Item(995,mob.boss?300:30)','new Item(995,(mob.boss?300:30)*((run.mods&4)!=0?2:1))').replace('random.nextInt(3)==0||mob.boss','random.nextInt((run.mods&4)!=0?2:3)==0||mob.boss');p.write_text(s)
p=r/'game/model/mob/Walking.java';s=p.read_text();a=s.index('public static MapRegion getRegion(int x, int y) {');b=s.index('\n\t}',a);s=s[:a]+'''public static MapRegion getRegion(int x, int y) {return MapRegion.getRegion(x,y);'''+s[b:];p.write_text(s)
