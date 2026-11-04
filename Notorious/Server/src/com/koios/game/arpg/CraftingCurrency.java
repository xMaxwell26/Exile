package com.koios.game.arpg;
import com.koios.game.model.item.Item;import com.koios.game.model.player.Player;
public final class CraftingCurrency {
 public static final int[] IDS={22400,22401,22402,22403,22404,22405,22407,22408,22409,22415,22416};
 public static final int[] LEGACY={561,562,563,564,554,555};
 public static final String[] ACTIONS={"rare","reroll","add","magic","corrupt","identify","alteration","augmentation","regal","scour","annul"};
 public static String description(int index){return new String[]{"Upgrade normal equipment or a map to rare.","Reroll the explicit modifiers on rare equipment or a map.","Add one explicit modifier to a rare item with room.","Upgrade a normal item to magic.","Corrupt up to twice; equipment gains a permanent implicit.","Identify an unidentified item and reveal its modifiers.","Reroll the explicit modifiers on a magic item.","Add one explicit modifier to a magic item with room.","Upgrade magic to rare, retaining modifiers and adding one.","Remove explicit modifiers, returning an item to normal.","Remove one random explicit modifier; rarity is retained."}[index];}
 public static int index(int id){for(int i=0;i<IDS.length;i++)if(IDS[i]==id)return i;return -1;}
 public static boolean use(Player p,int first,int second){
  Item a=p.getInventory().get(first),b=p.getInventory().get(second);if(a==null||b==null)return false;
  int index=index(a.getId()),target=second,source=first;if(index<0){index=index(b.getId());target=first;source=second;}if(index<0)return false;
  if(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting())return true;
  if(source==target||!p.getInventory().get(target).hasRoll()){p.sendMessage("Use this material on equipment or a map.");return true;}
  try{PrototypePanel.craft(p,ACTIONS[index],target,source);}catch(IllegalArgumentException ex){p.sendMessage(ex.getMessage());}return true;
 }
 public static void migrateLegacyInventory(Player p){if(!p.getAttribute("starter-claimed",false)||p.getAttribute("currency-art-migrated",false)||p.getAttribute("currency-migration-tried",false))return;p.setAttribute("currency-migration-tried",true);exchange(p);for(int id:LEGACY)if(p.getInventory().getCount(id)>0)return;p.setAttribute("currency-art-migrated",true);}
 public static void exchange(Player p){for(int i=0;i<LEGACY.length;i++){int amount=p.getInventory().getCount(LEGACY[i]);if(amount>0){Item next=new Item(IDS[i],amount);p.getInventory().remove(LEGACY[i],amount);if(!p.getInventory().add(next)){p.getInventory().add(new Item(LEGACY[i],amount));p.sendMessage("Make inventory space for currency exchange.");}}}}
}
