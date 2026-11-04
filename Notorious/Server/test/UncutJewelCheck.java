import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
/** Player-facing jewel selection and stale-menu ownership regression. */
public final class UncutJewelCheck {
 static int checks;
 static void check(boolean ok,String why){checks++;if(!ok)throw new AssertionError(why);}
 static Player fixture(boolean support,int level){Player p=PrototypeIntegrationCheck.player("jewelcheck",47);PrototypeIntegrationCheck.idle(p);p.getInventory().set(0,MapRuns.gem(level,support));return p;}
 static String saved(Player p){return ItemRollCodec.encode(p.getInventory().get(0));}
 public static void main(String[] args)throws Exception{
  PrototypeIntegrationCheck.loaded();
  for(boolean support:new boolean[]{false,true})for(int level:new int[]{1,7,20})for(int kind=0;kind<(support?3:5);kind++){
   Player p=fixture(support,level);Item item=p.getInventory().get(0);String uuid=item.getRoll().getInstanceId();
   check(item.getRoll().getGem().kind==-1,"Drops are uncut");
   String before=saved(p);ArpgSkills.handle(p,support?"arpgskill socket 0 0 0":"arpgskill socketactive 0 0");check(before.equals(saved(p)),"Uncut cannot socket or disappear");
   PrototypePanel.openGemCraft(p,0);PrototypePanel.handle(p,"arpgproto gemmake "+kind);
   check(p.getInventory().get(0)==item&&item.getRoll().getInstanceId().equals(uuid),"Craft retains item and UUID");
   check(item.getRoll().getGem().kind==kind&&item.getRoll().getGem().level==level&&item.getRoll().getGem().support==support,"Recipe retains level and type");
   String crafted=saved(p);PrototypePanel.handle(p,"arpgproto gemmake "+((kind+1)%(support?3:5)));check(crafted.equals(saved(p)),"Replay cannot recraft");
   Item restored=ItemRollCodec.restore(new Item(item.getId()),new String[]{crafted},0);check(restored.getRoll().getGem().kind==kind&&restored.getRoll().getInstanceId().equals(uuid),"Save restores chosen identity");
  }
  Player p=fixture(true,1);PrototypePanel.openGemCraft(p,0);String before=saved(p);PrototypePanel.handle(p,"arpgproto gemmake 4");check(before.equals(saved(p)),"Active-only index rejected for support");
  p=fixture(false,1);PrototypePanel.openGemCraft(p,0);p.getInventory().set(0,MapRuns.gem(1,true));before=saved(p);PrototypePanel.handle(p,"arpgproto gemmake 0");check(before.equals(saved(p)),"Replaced UUID rejects stale menu");
  for(int busy=0;busy<4;busy++){p=fixture(false,1);PrototypePanel.openGemCraft(p,0);before=saved(p);if(busy==0)p.setDead(true);if(busy==1)p.setIsTrading(true);if(busy==2)p.getBankField().setBanking(true);if(busy==3)p.setTeleporting(true);PrototypePanel.handle(p,"arpgproto gemmake 0");check(before.equals(saved(p)),"Busy state "+busy);}
  System.out.println("Passed "+checks+" uncut jewel checks.");
 }
}
