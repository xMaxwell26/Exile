package com.koios.game.arpg;
import java.util.*;import com.koios.game.model.*;import com.koios.game.model.item.Item;import com.koios.game.model.player.Player;import com.koios.network.out.impl.SendStringPacket;
/** Action-driven, saved checkpoints. Menus are only the actual skill/passive interfaces. */
public final class Tutorial {
 public static int stage(Player p){return p.getAttribute("tutorial-stage",0);}
 public static boolean active(Player p){return stage(p)>0&&stage(p)<11;}
 public static void initialize(Player p){releaseLegacyRestrictions(p);if(stage(p)==0){p.setAttribute("tutorial-stage",1);p.setAttribute("tutorial-setup",0);}}
 public static void begin(Player p){releaseLegacyRestrictions(p);if(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting())throw new IllegalArgumentException("Finish your current action first");if(stage(p)==11){p.sendMessage("Tutorial already completed.");return;}if(stage(p)==0&&MapRuns.run(p)!=null)throw new IllegalArgumentException("Close your active map at the map device before starting the tutorial");if(stage(p)==0)p.setAttribute("tutorial-stage",1);p.setAttribute("tutorial-setup",0);}
 private static void advance(Player p){p.setAttribute("tutorial-stage",stage(p)+1);p.setAttribute("tutorial-setup",0);}
 public static void action(Player p,String action){int stage=stage(p);if(stage==2&&action.equals("craft")||stage==3&&action.equals("passive")||stage==4&&action.equals("support")||stage==8&&action.equals("scroll"))advance(p);else if(stage==5&&(action.equals("q")||action.equals("w"))){int mask=p.getAttribute("tutorial-casts",0)|(action.equals("q")?1:2);p.setAttribute("tutorial-casts",mask);if(mask==3){p.getArpgVitals().refill(p.getArpgStats());p.getArpgSkills().refill(p.getArpgStats().get(Stat.MANA));advance(p);}}}
 public static String text(Player p){int s=stage(p);if(!active(p))return "";String[] lines={"","Equip the starter sword and body armour|Open inventory and wear both items.","Craft an item with a material|Use Wisdom on the unidentified practice sword.","Allocate your first passive|Click an available node in the passive tree.","Socket a support on Heavy Strike|Take the gem, Learn it, then drag it in K.","Practice your active skills|Click an enemy, then use both Q and W.","Defeat the Settlement Champion|Follow the boss arrow and defeat the champion.","Pick up the dropped map|Take the map from the champion's ground loot.","Create your own return portal|Read a Portal Scroll in your inventory.","Travel back through your portal|Walk up to Return portal and click it.","Visit the map device|Click the map altar at your base."};String[] detail=lines[s].split("\\|");if(s==5)detail[1]=bindingKey(p,0).equals("?")||bindingKey(p,1).equals("?")?"Assign Heavy Strike and Cleave in K, then use both.":"Use "+bindingKey(p,0)+" Heavy Strike and "+bindingKey(p,1)+" Cleave.";return "TUTORIAL "+s+" / 10\n"+detail[0]+"\n"+detail[1];}
 public static boolean showing(Player p){return active(p)||p.getAttribute("tutorial-complete-until",0L)>System.currentTimeMillis();}
 public static String bindingKey(Player p,int skill){String legacy=p.getArpgSkills().keyFor(skill);int slot="QWE".indexOf(legacy);if(slot<0||legacy.length()!=1)return "?";String[] labels=p.getAttribute("arpg-binding-labels",new String[]{"Q","W","E"});return slot<labels.length&&!labels[slot].equals("Unbound")?labels[slot]:"?";}
 public static String tracker(Player p){if(active(p)&&p.getAttribute("tutorial-await-return",false)&&MapRuns.isHome(p))return "TUTORIAL "+stage(p)+" / 10 - RETRY\nYou recovered at your private base.\nEnter the map portal to resume the encounter.";return active(p)?text(p):"TUTORIAL COMPLETE\nYour map device is ready\nOpen a dropped map to begin your next run";}
 public static void releaseLegacyRestrictions(Player p){if(p.getAttribute("tutorial",false))p.setAttribute("tutorial",false);}
 public static void tick(Player p){
  releaseLegacyRestrictions(p);
  if(p.isDead()||p.isTeleporting()||p.isTrading()||p.getBankField().isBanking())return;
  if(stage(p)==0&&MapRuns.run(p)==null&&!p.getAttribute("starter-claimed",false)&&!p.getAttribute("intro-complete",false))begin(p);
  if(!active(p)||p.isDead()||p.isTeleporting()||p.isTrading()||p.getBankField().isBanking())return;
  int s=stage(p);
  if(p.getAttribute("tutorial-await-return",false)&&MapRuns.isHome(p))return;
  if(s==9&&MapRuns.run(p)==null&&!MapRuns.isHome(p)){MapRuns.home(p);advance(p);return;}
  if(s>=5&&s<=8&&!MapRuns.inside(p))p.setAttribute("tutorial-setup",0);
  if(p.getAttribute("tutorial-setup",0)!=s){
   if(s<=4||s==10){if(!MapRuns.isHome(p))MapRuns.home(p);if(s==2){if(!p.getAttribute("tutorial-practice",false)){if(p.getInventory().freeSlots()<2){p.sendMessage("Make two inventory spaces for tutorial practice supplies.");return;}Item practice=AffixCatalog.starter(true);practice.setRoll(practice.getRoll().changed(practice.getRoll().getRarity(),false,0,practice.getRoll().getAffixes(),null));p.getInventory().add(practice);p.getInventory().add(new Item(22405));p.setAttribute("tutorial-practice",true);}}MapRuns.tutorialStation(p,s==2?1:s==10?0:s==3?2:0);}
   if(s==1&&!p.getAttribute("starter-claimed",false)){if(p.getInventory().freeSlots()<10){p.sendMessage("Make ten inventory spaces for starter supplies.");return;}PrototypePanel.starter(p);}
   if(s==4){boolean hasSupport=false;for(int support=0;support<3;support++)if(p.getArpgSkills().owned(support)>0)hasSupport=true;for(Item item:p.getInventory().getValidItems())if(MapRuns.isGem(item)&&item.getRoll().getGem().support&&item.getRoll().getRequiredLevel()<=p.getArpgProgress().level())hasSupport=true;for(com.koios.game.model.item.GroundItemEntity ground:com.koios.GameService.getGroundItemManager().getPlayerItems(p))if(MapRuns.isGem(ground.getItem())&&ground.getItem().getRoll().getGem().support&&ground.getItem().getRoll().getRequiredLevel()<=p.getArpgProgress().level())hasSupport=true;if(!hasSupport)com.koios.game.model.item.GroundItemManager.spawnPrivate(p,MapRuns.gem(1,true),p.getLocation().copy());}
   if(s==3&&p.getArpgProgress().points()<=p.getArpgPassives().spent())p.getArpgProgress().award(p.getArpgProgress().next()-p.getArpgProgress().xp());
   if(s>=5&&s<=8&&!MapRuns.inside(p)){if(!MapRuns.isHome(p))MapRuns.home(p);if(p.isTeleporting())return;if(MapRuns.run(p)!=null)MapRuns.enter(p);else{if(s>6){p.setAttribute("tutorial-stage",5);p.setAttribute("tutorial-casts",0);s=5;}MapRuns.start(p,-1,true);}}
   if(s==8&&p.getInventory().getCount(MapRuns.PORTAL_SCROLL)==0){if(p.getInventory().freeSlots()<1){p.sendMessage("Make one inventory space for a Portal Scroll.");return;}p.getInventory().add(new Item(MapRuns.PORTAL_SCROLL));}
   p.getSession().send(new com.koios.network.out.impl.SendRemoveInterfacePacket());p.getSession().send(new SendStringPacket("close",65528));if(s==3)PrototypePanel.show(p,"tree");p.setAttribute("tutorial-setup",s);
  }
  if(s==1&&p.getEquipment().get(3)!=null&&p.getEquipment().get(3).hasRoll()&&p.getEquipment().get(4)!=null&&p.getEquipment().get(4).hasRoll())advance(p);
  if(s==6&&MapRuns.run(p)!=null&&MapRuns.run(p).complete)advance(p);
  if(s==7&&p.getInventory().contains(i->MapRuns.isMap(i)))advance(p);
  if(s==9&&MapRuns.isHome(p))advance(p);
 }
 public static void device(Player p){if(stage(p)==10){advance(p);p.setAttribute("tutorial-complete-until",System.currentTimeMillis()+10000L);MapRuns.objective(p);}}
}


