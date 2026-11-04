import java.util.*;
import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/** Mutable inventory must not change the map a displayed card authorizes or lose the return page. */
public final class PanelNavigationCheck {
 private static int checks;
 private static void check(boolean ok,String reason){checks++;if(!ok)throw new AssertionError(reason);}
 private static Player player(String name,int index){Player p=PrototypeIntegrationCheck.player(name,index);p.allowSaveRequests=false;p.setAttribute("tutorial-stage",11);p.setAttribute("intro-complete",true);MapRuns.home(p);PrototypeIntegrationCheck.idle(p);return p;}
 public static void main(String[]args)throws Exception{
  PrototypeIntegrationCheck.loaded();Player p=player("qanavmap",95);
  try{
   Item original=MapRuns.map(1,0),replacement=MapRuns.map(1,7);p.getInventory().set(0,original);PrototypePanel.show(p,"maps");p.getInventory().set(0,replacement);PrototypePanel.handle(p,"arpgproto open 0");
   check(MapRuns.run(p)==null&&p.getInventory().get(0)==replacement,"A stale map card cannot consume a replacement map");
   PrototypePanel.handle(p,"arpgproto open 0");check(MapRuns.run(p)!=null&&p.getInventory().get(0)==null,"Refreshed map card opens its exact replacement normally");
   PrototypePanel.handle(p,"arpgproto open 0");check(MapRuns.run(p)!=null&&p.getInventory().get(0)==null,"Replayed open cannot consume another map");
  }finally{MapRuns.cleanup(p);}
  p=player("qanavcraftmap",96);
  try{
   Item map=MapRuns.map(1,0);String uuid=map.getRoll().getInstanceId();p.getInventory().set(0,map);PrototypePanel.show(p,"maps");map.setRoll(map.getRoll().changed(ItemRoll.Rarity.MAGIC,true,0,Collections.emptyList(),new ItemRoll.MapData(1,1)));PrototypePanel.handle(p,"arpgproto open 0");
   check(MapRuns.run(p)==null&&p.getInventory().get(0)==map&&uuid.equals(map.getRoll().getInstanceId()),"Crafting a map under an open menu requires renewed consent even with the same UUID");
   for(int i=0;i<8;i++)p.getInventory().set(i,MapRuns.map(1,0));PrototypePanel.show(p,"maps");PrototypePanel.handle(p,"arpgproto page 1");Item first=p.getInventory().get(0);PrototypePanel.handle(p,"arpgproto open 0");check(MapRuns.run(p)==null&&p.getInventory().get(0)==first,"An off-page stale card is not a valid current selection");
   PrototypePanel.show(p,"guide");PrototypePanel.handle(p,"arpgproto open 6");check(MapRuns.run(p)==null,"Leaving the map panel revokes its card selections");
  }finally{MapRuns.cleanup(p);}
  p=player("qanavrecovery",98);
  try{
   p.getInventory().set(0,new Item(995,25));PrototypePanel.show(p,"maps");PrototypePanel.handle(p,"arpgproto training");int slot=-1;for(int i=0;i<28;i++)if(MapRuns.isMap(p.getInventory().get(i)))slot=i;
   check(slot>=0&&p.getInventory().getCount(995)==0,"Recovery-map action works without a pre-existing map card and charges once");
   PrototypePanel.handle(p,"arpgproto open "+slot);check(MapRuns.run(p)!=null,"Recovery action refreshes the map list with a valid new card");
  }finally{MapRuns.cleanup(p);}
  p=player("qanavgems",97);
  try{
   for(int i=0;i<13;i++)p.getInventory().set(i,MapRuns.gem(1,i%2==0));PrototypePanel.handle(p,"arpgproto craft");PrototypePanel.handle(p,"arpgproto page 1");PrototypePanel.handle(p,"arpgproto select 6");PrototypePanel.handle(p,"arpgproto reference");PrototypePanel.handle(p,"arpgproto page 3");PrototypePanel.handle(p,"arpgproto craft");PrototypePanel.handle(p,"arpgproto items");
   check(p.getAttribute("prototype-page",-1)==1,"Choose another item restores the original gem page after browsing material rules");
   check(p.getAttribute("craft-slot",0)==-1&&p.getAttribute("craft-uuid","missing").isEmpty(),"Back navigation clears both selected slot and identity");
   Item gem=p.getInventory().get(0);String identity=gem.getRoll().getInstanceId();int owned=p.getArpgSkills().owned(0);PrototypePanel.handle(p,"arpgproto select 0");PrototypePanel.handle(p,"arpgproto learn 2");
   check(p.getInventory().get(0)==gem&&identity.equals(gem.getRoll().getInstanceId())&&p.getArpgSkills().owned(0)==owned,"Obsolete Learn action cannot consume a physical jewel or create abstract support ownership");
   p.setAttribute("craft-slot",4);p.setAttribute("craft-uuid",p.getInventory().get(4).getRoll().getInstanceId());PrototypePanel.openGemCraft(p,0);Item replacement=MapRuns.gem(1,false);p.getInventory().set(0,replacement);PrototypePanel.handle(p,"arpgproto gemmake 0");
   check(p.getInventory().get(0)==replacement&&replacement.getRoll().getGem().kind==-1,"Stale jewel choice never crafts the replacement");
   check(p.getAttribute("prototype-tab","").equals("craft")&&p.getAttribute("gem-craft-slot",0)==-1&&p.getAttribute("craft-slot",0)==-1,"Stale jewel menu refreshes to inventory instead of retaining unusable choices");
  }finally{MapRuns.cleanup(p);}
  System.out.println("Passed "+checks+" stale-map/consent/paging/physical-gem navigation checks.");
 }
}
