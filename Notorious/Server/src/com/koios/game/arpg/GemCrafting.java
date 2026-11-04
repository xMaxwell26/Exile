package com.koios.game.arpg;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
/** Inventory-local gem fusion: validate everything before committing either input or coins. */
public final class GemCrafting {
 private GemCrafting(){}
 public static boolean use(Player p,int source,int target){
  if(source<0||target<0||source>=p.getInventory().getCapacity()||target>=p.getInventory().getCapacity())return false;
  Item a=p.getInventory().get(source),b=p.getInventory().get(target);
  if(!MapRuns.isGem(a)||!MapRuns.isGem(b))return false;
  if(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting())return true;
  try{
   if(source==target||a==b||a.getRoll().getInstanceId().equals(b.getRoll().getInstanceId()))throw new IllegalArgumentException("Choose two distinct gems.");
   if(a.getCount()!=1||b.getCount()!=1)throw new IllegalArgumentException("Choose individual gems.");
   ItemRoll.GemData first=a.getRoll().getGem(),second=b.getRoll().getGem();
   if(first.support||second.support||first.kind<0||second.kind<0)throw new IllegalArgumentException("Craft active skill gems before fusing them.");
   if(first.kind!=second.kind)throw new IllegalArgumentException("Both gems must grant the same skill.");
   if(first.level!=second.level)throw new IllegalArgumentException("Both active gems must have the same level.");
   if(first.level>=20)throw new IllegalArgumentException("This gem is already level 20.");
   int fee=25*first.level;
   if(p.getInventory().getCount(995)<fee)throw new IllegalArgumentException("Fusion requires "+fee+" coins.");
   ItemRoll next=b.getRoll().withGemLevel(first.level+1);
   p.getInventory().remove(995,fee);p.getInventory().set(source,null);b.setRoll(next);p.getInventory().fireItemChanged(target);
   p.sendMessage("Fused skill gem: level "+next.getGem().level+" ("+fee+" coins).");
  }catch(IllegalArgumentException ex){p.sendMessage(ex.getMessage());}
  return true;
 }
}
