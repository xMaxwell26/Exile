import java.util.*;
import com.koios.game.arpg.*;
import com.koios.game.model.player.Player;
import com.koios.game.model.item.*;
import com.koios.network.*;
/** Mandatory onboarding supplies survive drop attempts and reconnect without duplication. */
public final class ProgressionAuditCheck {
 static int checks;
 static void check(boolean value,String text){checks++;if(!value)throw new AssertionError(text);}
 static void drop(Player p,int slot){Item i=p.getInventory().get(slot);ReceivedPacketExecutor.processPacket(p,new GamePacketBuilder(87).writeShortA(i.getId()).writeShort(3214).writeShortA(slot).toPacket());}
 public static void main(String[] args)throws Exception{
  PrototypeIntegrationCheck.loaded();Player p=PrototypeIntegrationCheck.player("starterloss",47);p.allowSaveRequests=false;Tutorial.initialize(p);Tutorial.tick(p);PrototypeIntegrationCheck.idle(p);
  check(Tutorial.stage(p)==1&&p.getAttribute("starter-claimed",false),"Fixture receives starter");
  Map<Integer,Item> original=new LinkedHashMap<>();for(int slot=0;slot<p.getInventory().getCapacity();slot++){Item i=p.getInventory().get(slot);if(i!=null)original.put(slot,i.copy());}
  for(int slot:original.keySet()){drop(p,slot);check(original.get(slot).equals(p.getInventory().get(slot)),"Tutorial drop keeps exact item identity/count in slot "+slot);}
  check(com.koios.GameService.getGroundItemManager().getPlayerItems(p).isEmpty(),"Blocked drops create no ground supplies");
  MapRuns.cleanup(p);Tutorial.begin(p);Tutorial.tick(p);PrototypeIntegrationCheck.idle(p);PrototypePanel.handle(p,"arpgproto kit");
  check(p.getInventory().size()==original.size(),"Reconnect and repeat kit request cannot duplicate supplies");for(int slot:original.keySet())check(original.get(slot).equals(p.getInventory().get(slot)),"Reconnect retains identity in slot "+slot);
  p.setAttribute("tutorial-stage",11);p.setAttribute("intro-complete",true);int slot=-1;for(int s:original.keySet())if(original.get(s).hasRoll()&&!MapRuns.isGem(original.get(s))&&!MapRuns.isMap(original.get(s))){slot=s;break;}check(slot>=0,"Mandatory rolled gear exists");Item expected=p.getInventory().get(slot).copy();drop(p,slot);check(p.getInventory().get(slot)==null,"Completed character can drop gear");check(com.koios.GameService.getGroundItemManager().getPlayerItems(p).stream().anyMatch(g->g.getItem().equals(expected)),"Normal drop preserves item identity on ground");MapRuns.cleanup(p);
  System.out.println("Passed "+checks+" starter-supply retention and post-tutorial drop checks.");
 }
}
