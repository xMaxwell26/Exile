package com.koios.game.arpg;
import java.util.*;import java.nio.charset.StandardCharsets;
import com.koios.GameService;import com.koios.game.model.player.Player;import com.koios.game.model.item.Item;import com.koios.network.out.impl.SendStringPacket;
public final class PrototypePanel {
 public static void show(Player p,String tab){
  List<String> rows=new ArrayList<>();
  if(tab.equals("tree")){
   rows.add("tree|"+Math.max(0,p.getArpgProgress().points()-p.getArpgPassives().spent())+"|"+p.getAttribute("free-refunds",false));
   for(PassiveTree.Node node:PassiveTree.NODES.values())rows.add(node.id+"|"+node.parent+"|"+p.getArpgPassives().has(node.id)+"|"+node.name+"|"+node.description());
  }else{
   rows.add(tab);
   if(tab.equals("guide")){
    row(rows,"exchange","Exchange old rune materials for crafting currency");row(rows,"scrolls","Buy 5 Portal Scrolls - 25 coins");row(rows,"claim","Claim overflow rewards: "+p.getArpgRewards().size());row(rows,"kit","Claim starter sword, armour and crafting supplies (once)");row(rows,"home","Enter your private house base");row(rows,"intro","Start or resume the guided tutorial");row(rows,"tree","Spend passive points: click a node; Ctrl-click to refund");row(rows,"craft","Crafting: use a material directly on an inventory item");row(rows,"maps","Map device: consume a dropped map to open an instance");row(rows,"","K: skills, loadout and controls / P: field guide");row(rows,"","Enter opens chat. All keyboard skills pause while typing.");row(rows,"","After level 10: limited map entries and death XP penalties.");row(rows,"","Monster loot drops on the ground. Pick it up before closing a map.");
   }else if(tab.equals("reference")){
    craftingReference(p,rows);
   }else if(tab.equals("craft")){
    row(rows,"reference","Crafting reference: all eleven materials and their rules");
    int slot=p.getAttribute("craft-slot",-1);Item selected=p.getInventory().get(slot);
    if(selected!=null&&selected.hasRoll()&&selected.getRoll().getInstanceId().equals(p.getAttribute("craft-uuid",""))){
     row(rows,"items","Selected: "+MapRuns.name(selected)+" / "+selected.getRoll().getRarity());
     if(MapRuns.isGem(selected)){
      boolean support=selected.getRoll().getGem().support;row(rows,"",support?"Support gems modify one socketed active skill.":"Active gems occupy one of three loadout slots.");row(rows,"","Open K, then drag this gem into an open socket.");row(rows,"","Drag a socketed gem back to inventory to remove it.");row(rows,"items","Choose a different item");
     }else{row(rows,"","Close this panel; use a crafting material on the item in inventory.");row(rows,"","Wisdom: identify / Transmutation: normal to magic");row(rows,"","Alchemy: normal to rare / Chaos: reroll");row(rows,"","Exalted: add an affix / Vaal: corrupt");row(rows,"items","Choose a different item");}
    }else{
     row(rows,"","Use a crafting material on equipment or a map in your inventory.");
     row(rows,"","Scroll of Wisdom: identify / Transmutation: normal to magic");
     row(rows,"","Alchemy: normal to rare / Chaos: reroll modifiers");
     row(rows,"","Exalted: add a modifier / Vaal: corrupt (up to twice)");

     row(rows,"","Active and support gems drag directly into the K loadout.");
     gemInventory(p,rows);
    }
   }else if(tab.equals("maps")){
    MapRuns.Run run=MapRuns.run(p);row(rows,"home","Return to private house base");row(rows,"training","Create a basic training map - 25 coins");
    if(run!=null){row(rows,"resume","Re-enter active map / portals: "+run.portals+(run.complete?" / champion defeated":""));row(rows,"abandon",run.complete?"Close completed map (keep rewards)":"Abandon active map and release its instance");}
    else{row(rows,"intro","Start or resume guided tutorial");inventory(p,rows,true);}
   }
  }
  p.setAttribute("prototype-tab",tab);send(p,String.join("\n",rows));
 }
 /**
  * Browse recipes without spending currency or changing the selected gem.
  * Three recipes per page leave room for full descriptions, restrictions and
  * navigation on the fixed 512x334 canvas. Names/descriptions use the same
  * catalog as item tooltips; adding a currency automatically extends the guide.
  */
 private static void craftingReference(Player p,List<String> rows){
  int perPage=3,last=(CraftingCurrency.IDS.length-1)/perPage;
  int page=Math.max(0,Math.min(last,p.getAttribute("prototype-page",0)));
  p.setAttribute("prototype-page",page);
  row(rows,"craft","Back to crafting and gems");
  row(rows,"","MATERIAL REFERENCE / "+(page+1)+" of "+(last+1));
  for(int i=page*perPage;i<Math.min(CraftingCurrency.IDS.length,(page+1)*perPage);i++){
   row(rows,"",new Item(CraftingCurrency.IDS[i]).getDefinition().getName());
   // Word-wrap instead of allowing the legacy row renderer to truncate rules.
   String line="";
   for(String word:CraftingCurrency.description(i).split(" ")){
    if(line.length()+word.length()+1>66){row(rows,"",line);line="";}
    line+=(line.isEmpty()?"":" ")+word;
   }
   row(rows,"",line);
  }
  row(rows,"","Use a material on equipment or a map; gems cannot be crafted.");
  row(rows,"","Corrupted: only Wisdom or another Vaal (maximum twice).");
  row(rows,"","One material per success. Rejected crafts consume nothing.");
  if(page>0)row(rows,"page "+(page-1),"Previous materials");
  if(page<last)row(rows,"page "+(page+1),"Next materials");
 }
 private static void inventory(Player p,List<String> rows,boolean maps){
  List<Integer> slots=new ArrayList<>();for(int i=0;i<p.getInventory().getCapacity();i++){Item item=p.getInventory().get(i);if(item!=null&&item.hasRoll()&&(!maps||MapRuns.isMap(item)))slots.add(i);}
  int last=Math.max(0,(slots.size()-1)/6),page=Math.max(0,Math.min(last,p.getAttribute("prototype-page",0)));p.setAttribute("prototype-page",page);
  for(int offset=page*6;offset<Math.min(slots.size(),page*6+6);offset++){int slot=slots.get(offset);Item item=p.getInventory().get(slot);String metadata="";if(maps){int count=Integer.bitCount(item.getRoll().getMap().mods);metadata="\t"+item.getRoll().getRarity().name()+" / "+(count==0?"No modifiers":count+" modifier"+(count==1?"":"s"));}rows.add((maps?"open ":"select ")+slot+"\t"+MapRuns.name(item)+metadata);}
  if(page>0)row(rows,"page "+(page-1),"Previous page");if(page<last)row(rows,"page "+(page+1),"Next page");
 }
 private static void gemInventory(Player p,List<String> rows){List<Integer> slots=new ArrayList<>();for(int i=0;i<p.getInventory().getCapacity();i++)if(MapRuns.isGem(p.getInventory().get(i)))slots.add(i);int last=Math.max(0,(slots.size()-1)/6),page=Math.max(0,Math.min(last,p.getAttribute("prototype-page",0)));p.setAttribute("prototype-page",page);for(int at=page*6;at<Math.min(slots.size(),page*6+6);at++){int slot=slots.get(at);row(rows,"select "+slot,MapRuns.name(p.getInventory().get(slot)));}if(page>0)row(rows,"page "+(page-1),"Previous page");if(page<last)row(rows,"page "+(page+1),"Next page");}
 private static void row(List<String> rows,String action,String label){rows.add(action+"\t"+label);}
 private static void send(Player p,String text){p.getSession().send(new SendStringPacket(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)),65531));}
 public static boolean handle(Player p,String input){
  if(!input.startsWith("arpgproto "))return false;if(p.isDead()||p.isTrading()||p.getBankField().isBanking())return true;
  String action=input.substring(10);String[] args=action.split(" ");
  try{
   switch(args[0]){
    case "guide":case "tree":case "craft":case "maps":case "reference":p.setAttribute("prototype-page",0);if(args.length==1){show(p,args[0]);return true;}if(args[0].equals("craft"))craft(p,args[1]);break;
    case "exchange":CraftingCurrency.exchange(p);show(p,"guide");return true;
    case "items":p.setAttribute("craft-slot",-1);show(p,"craft");return true;
    case "page":p.setAttribute("prototype-page",Math.max(0,Math.min(10,Integer.parseInt(args[1]))));show(p,p.getAttribute("prototype-tab","craft"));return true;
    case "home":MapRuns.home(p);show(p,"maps");return true;
    case "intro":Tutorial.begin(p);return true;
    case "open":MapRuns.start(p,Integer.parseInt(args[1]),false);return true;
    case "scrolls":if(p.getInventory().getCount(995)<25)throw new IllegalArgumentException("Requires 25 coins");if(!p.getInventory().hasRoomFor(new Item(MapRuns.PORTAL_SCROLL,5)))throw new IllegalArgumentException("Make inventory space first");p.getInventory().remove(995,25);p.getInventory().add(new Item(MapRuns.PORTAL_SCROLL,5));show(p,"guide");return true;
    case "training":if(!MapRuns.isHome(p))throw new IllegalArgumentException("Use your map device at home");if(p.getInventory().getCount(995)<25)throw new IllegalArgumentException("Requires 25 coins");MapRuns.grant(p,MapRuns.map(1,0));p.getInventory().remove(995,25);show(p,"maps");return true;
    case "resume":MapRuns.enter(p);return true;
    case "abandon":MapRuns.abandon(p);show(p,"maps");return true;
    case "select":{int slot=Integer.parseInt(args[1]);Item item=p.getInventory().get(slot);if(item==null||!item.hasRoll())throw new IllegalArgumentException("Select rolled equipment or a map");p.setAttribute("craft-slot",slot);p.setAttribute("craft-uuid",item.getRoll().getInstanceId());show(p,"craft");return true;}
    case "learn":{if(p.isTeleporting())throw new IllegalArgumentException("Wait until you finish travelling.");int slot=p.getAttribute("craft-slot",-1);Item gem=p.getInventory().get(slot);if(!MapRuns.isGem(gem)||!gem.getRoll().getInstanceId().equals(p.getAttribute("craft-uuid","")))throw new IllegalArgumentException("Select the gem again");int id=Integer.parseInt(args[1]);if(gem.getRoll().getGem().support?(id<2||id>4):(id!=0&&id!=1&&id!=5&&id!=6&&id!=7))throw new IllegalArgumentException("Wrong gem type");if(gem.getRoll().getGem().support)p.getArpgSkills().learnSupport(id-2,gem.getRoll().getGem().level,p.getArpgProgress().level());else p.getArpgSkills().upgrade(id,gem.getRoll().getGem().level,p.getArpgProgress().level());p.getInventory().set(slot,null);p.setAttribute("craft-slot",-1);show(p,"craft");return true;}
    case "allocate":p.getArpgPassives().allocate(Integer.parseInt(args[1]),p.getArpgProgress().points());Tutorial.action(p,"passive");show(p,"tree");return true;
    case "refund":{int id=Integer.parseInt(args[1]);if(!p.getAttribute("free-refunds",false)&&p.getInventory().getCount(995)<100)throw new IllegalArgumentException("Refund needs 100 coins");p.getArpgPassives().refund(id);if(!p.getAttribute("free-refunds",false))p.getInventory().remove(995,100);show(p,"tree");return true;}
    case "free":if(!GameService.isOnlineMode()){p.setAttribute("free-refunds",!p.getAttribute("free-refunds",false));show(p,"tree");}return true;
    case "claim":p.getArpgRewards().claim(p);show(p,"guide");return true;
    case "kit":if(p.getAttribute("starter-claimed",false))throw new IllegalArgumentException("Starter kit already claimed");if(p.getInventory().freeSlots()<13)throw new IllegalArgumentException("Make thirteen inventory spaces first");
     starter(p);show(p,"guide");return true;
   }
   show(p,p.getAttribute("prototype-tab","craft"));
  }catch(RuntimeException e){p.sendMessage(e.getMessage()==null?"Prototype action failed":e.getMessage());}
  return true;
 }
 public static void starter(Player p){MapRuns.grant(p,AffixCatalog.starter(true));MapRuns.grant(p,AffixCatalog.starter(false));MapRuns.grant(p,MapRuns.gem(1,false,0));MapRuns.grant(p,MapRuns.gem(1,false,1));MapRuns.grant(p,MapRuns.gem(1,false,2));MapRuns.grant(p,new Item(995,500));MapRuns.grant(p,new Item(MapRuns.PORTAL_SCROLL,5));for(int i=0;i<CraftingCurrency.LEGACY.length;i++)MapRuns.grant(p,new Item(CraftingCurrency.IDS[i],5));p.setAttribute("starter-claimed",true);}
 private static void craft(Player p,String action){
  if(!MapRuns.isHome(p))throw new IllegalArgumentException("Craft at your private base");int slot=p.getAttribute("craft-slot",-1);Item item=p.getInventory().get(slot);
  if(item==null||!item.hasRoll()||!item.getRoll().getInstanceId().equals(p.getAttribute("craft-uuid","")))throw new IllegalArgumentException("Choose the item again");
  if(MapRuns.isGem(item))throw new IllegalArgumentException("Choose a skill or support upgrade for this gem");
  craft(p,action,slot,-1);
 }
 /**
  * Inventory transaction boundary. Recipes prepare a roll without mutation; all
  * source/currency checks complete before the single material and target commit.
  * sourceSlot=-1 is the selected-item panel path; nonnegative slots come from Use.
  */
 public static void craft(Player p, String action, int slot, int sourceSlot) {
  if (p.isDead() || p.isTrading() || p.getBankField().isBanking() || p.isTeleporting()) {
   throw new IllegalArgumentException("Cannot craft right now");
  }
  Item item = p.getInventory().get(slot);
  if (item == null || !item.hasRoll() || MapRuns.isGem(item)) {
   throw new IllegalArgumentException("Use a crafting material on equipment or a map");
  }
  int currency = -1;
  for (int index = 0; index < CraftingCurrency.ACTIONS.length; index++) {
   if (CraftingCurrency.ACTIONS[index].equals(action)) currency = CraftingCurrency.IDS[index];
  }
  if (currency < 0) throw new IllegalArgumentException("Unknown crafting action");

  ItemRoll next = CurrencyCrafting.apply(item, action, new Random());
  AffixRules.validateGenerated(next);
  if (sourceSlot >= 0 && (p.getInventory().get(sourceSlot) == null
          || p.getInventory().get(sourceSlot).getId() != currency)) {
   throw new IllegalArgumentException("Choose the material again");
  }
  if (p.getInventory().getCount(currency) < 1) {
   throw new IllegalArgumentException("You need the listed crafting currency");
  }

  // No failure-prone generation remains below this point. Preserve the Item and
  // roll UUID so equipment, inventory hover and saved identity stay consistent.
  p.getInventory().remove(currency, 1);
  item.setRoll(next);
  p.getInventory().fireItemChanged(slot);
  Tutorial.action(p, "craft");
  p.sendMessage("Crafted " + MapRuns.name(item));
 }
}
