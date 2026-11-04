import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
public final class GemFusionCheck {
 static int checks;static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}
 static Player fixture(int level,int coins){Player p=PrototypeIntegrationCheck.player("gemfusion",45);PrototypeIntegrationCheck.idle(p);p.getInventory().set(0,MapRuns.gem(level,false,0));p.getInventory().set(1,MapRuns.gem(level,false,0));if(coins>0)p.getInventory().set(2,new Item(995,coins));return p;}
 static void unchanged(Player p,int a,int b,String label){String first=ItemRollCodec.encode(p.getInventory().get(0)),second=ItemRollCodec.encode(p.getInventory().get(1));int coins=p.getInventory().getCount(995);GemCrafting.use(p,a,b);check(first.equals(ItemRollCodec.encode(p.getInventory().get(0)))&&second.equals(ItemRollCodec.encode(p.getInventory().get(1)))&&coins==p.getInventory().getCount(995),label);}
 public static void main(String[] args)throws Exception{
  PrototypeIntegrationCheck.loaded();
  for(int level:new int[]{1,7,19}){Player p=fixture(level,25*level+3);Item target=p.getInventory().get(1);String uuid=target.getRoll().getInstanceId();ItemRoll old=target.getRoll();GemCrafting.use(p,0,1);check(p.getInventory().get(0)==null&&p.getInventory().get(1)==target,"Fusion consumes source and retains target slot/object");check(target.getRoll().getGem().level==level+1&&target.getRoll().getInstanceId().equals(uuid),"Fusion upgrades target preserving identity");check(old.getGem().level==level,"Old immutable roll remains unchanged");check(target.getRoll().getRequiredLevel()==(level+1)*4-3&&p.getInventory().getCount(995)==3,"Correct requirement and fee");Item restored=ItemRollCodec.restore(new Item(22411),new String[]{ItemRollCodec.encode(target)},0);check(restored.getRoll().getInstanceId().equals(uuid)&&restored.getRoll().getGem().level==level+1,"Saved output restores level and UUID");GemCrafting.use(p,0,1);check(target.getRoll().getGem().level==level+1&&p.getInventory().getCount(995)==3,"Replayed request cannot upgrade or charge twice");}
  Player p=fixture(1,24);unchanged(p,0,1,"Insufficient funds atomic");p=fixture(1,100);unchanged(p,0,0,"Self fusion atomic");p.getInventory().set(1,p.getInventory().get(0).copy());unchanged(p,0,1,"Duplicate saved identity atomic");p=fixture(1,100);p.getInventory().set(1,MapRuns.gem(2,false));unchanged(p,0,1,"Mismatched level atomic");p=fixture(1,100);p.getInventory().set(1,MapRuns.gem(1,true));unchanged(p,0,1,"Support rejection atomic");p=fixture(20,1000);unchanged(p,0,1,"Maximum level atomic");
  for(int state=0;state<4;state++){p=fixture(1,100);if(state==0)p.setDead(true);if(state==1)p.setIsTrading(true);if(state==2)p.getBankField().setBanking(true);if(state==3)p.setTeleporting(true);unchanged(p,0,1,"Busy state "+state+" atomic");}
  p=fixture(1,100);for(int slot=3;slot<28;slot++)p.getInventory().set(slot,new Item(385));GemCrafting.use(p,0,1);check(p.getInventory().get(0)==null&&p.getInventory().get(1).getRoll().getGem().level==2,"Full inventory succeeds without allocating a slot");
  System.out.println("Passed "+checks+" gem fusion checks.");
 }
}
