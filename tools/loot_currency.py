from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
p=r/'game/model/item/GroundItemEntity.java';s=p.read_text();s=s.replace('private final int amount;', 'private final int amount;\n private Item payload;');s=s.replace('public int getAmount() {','public GroundItemEntity(Item item, int x,int y,int z,int hideTicks,long owner){this(item.getId(),x,y,z,item.getCount(),hideTicks,owner);payload=item.copy();}\n public Item getItem(){return payload==null?new Item(id,amount):payload.copy();}\n\n public int getAmount() {');p.write_text(s)
p=r/'game/model/item/GroundItemManager.java';s=p.read_text();pos=s.index('\tpublic static void spawnNew(Player player, Item item)');s=s[:pos]+''' public static void spawnPrivate(Player player, Item item, Location at){
  GroundItemEntity entity=new GroundItemEntity(item,at.getX(),at.getY(),at.getZ(),Integer.MAX_VALUE,player.getUsernameHash());
  groundItems.add(entity);player.getItems().createGroundItem(item,at);
 }
 public static void clearArea(int x,int y,int size){groundItems.removeIf(item->item.getX()>=x&&item.getX()<x+size&&item.getY()>=y&&item.getY()<y+size);}
'''+s[pos:];s=s.replace('new Item(item.getId(), item.getAmount())','item.getItem()');p.write_text(s)
p=r/'game/arpg/RegionInstance.java';s=p.read_text().replace('closed=true;for(int id:regions)', 'closed=true;com.koios.game.model.item.GroundItemManager.clearArea(baseX,baseY,104);for(int id:regions)');p.write_text(s)
p=r/'game/arpg/MapRuns.java';s=p.read_text();a=s.index(' private static void killed(');b=s.index(' private static Location findSpawn',a);part=s[a:b].replace('grant(p,','drop(p,mob,').replace('new int[]{561,562,563,564,554,555}','CraftingCurrency.IDS').replace('Rewards collected.','Loot dropped at the champion.');s=s[:a]+part+s[b:];s=s.replace(' private static void killed(', ' private static void drop(Player p,MapMob mob,Item item){com.koios.game.model.item.GroundItemManager.spawnPrivate(p,item,mob.getLocation().copy());}\n private static void killed(');p.write_text(s)
# dedicated currency definitions
p=r/'game/def/ItemDefinition.java';s=p.read_text().replace('new ItemDefinition[root.size()]','new ItemDefinition[Math.max(root.size(),22500)]');needle='\t\tSystem.out.println("Finished reading';idx=s.index(needle);s=s[:idx]+'''  String[] currencyNames={"Orb of Alchemy","Chaos Orb","Exalted Orb","Orb of Transmutation","Vaal Orb","Scroll of Wisdom"};
  for(int n=0;n<6;n++){ItemDefinition d=definitions[22400+n];d.name=currencyNames[n];d.description="Use on equipment or a map to craft it.";d.stackable=true;d.untradeable=false;d.noteId=-1;d.lendId=-1;d.equipmentType=definitions[561].equipmentType;d.value=1;}
'''+s[idx:];p.write_text(s)
p=r/'game/arpg/CraftingCurrency.java';p.write_text('''package com.koios.game.arpg;
import com.koios.game.model.item.Item;import com.koios.game.model.player.Player;
public final class CraftingCurrency {
 public static final int[] IDS={22400,22401,22402,22403,22404,22405};
 public static final int[] LEGACY={561,562,563,564,554,555};
 public static final String[] ACTIONS={"rare","reroll","add","magic","corrupt","identify"};
 public static int index(int id){for(int i=0;i<IDS.length;i++)if(IDS[i]==id)return i;return -1;}
 public static boolean use(Player p,int first,int second){
  Item a=p.getInventory().get(first),b=p.getInventory().get(second);if(a==null||b==null)return false;
  int index=index(a.getId()),target=second,source=first;if(index<0){index=index(b.getId());target=first;source=second;}if(index<0)return false;
  if(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting())return true;
  if(source==target||!p.getInventory().get(target).hasRoll()){p.sendMessage("Use this material on equipment or a map.");return true;}
  try{PrototypePanel.craft(p,ACTIONS[index],target,source);}catch(IllegalArgumentException ex){p.sendMessage(ex.getMessage());}return true;
 }
 public static void exchange(Player p){for(int i=0;i<IDS.length;i++){int amount=p.getInventory().getCount(LEGACY[i]);if(amount>0){Item next=new Item(IDS[i],amount);p.getInventory().remove(LEGACY[i],amount);if(!p.getInventory().add(next)){p.getInventory().add(new Item(LEGACY[i],amount));p.sendMessage("Make inventory space for currency exchange.");}}}}
}
''')
p=r/'network/in/impl/PacketItemWithItem.java';s=p.read_text().replace('\t\tRandomEvent.tryRandom(player);','\t\tif(com.koios.game.arpg.CraftingCurrency.use(player,itemUsedSlot,usedWithSlot))return;\n        if(player.getInventory().get(itemUsedSlot).hasRoll()||player.getInventory().get(usedWithSlot).hasRoll()){player.sendMessage("Use a crafting material on this item.");return;}\n\t\tRandomEvent.tryRandom(player);');p.write_text(s)
p=r/'game/arpg/PrototypePanel.java';s=p.read_text();s=s.replace('for(int id:new int[]{561,562,563,564,554,555})','for(int id:CraftingCurrency.IDS)');s=s.replace('row(rows,"claim","Claim overflow rewards:', 'row(rows,"exchange","Exchange old rune materials for crafting currency");row(rows,"claim","Claim overflow rewards:');s=s.replace('row(rows,"","Map loot goes to inventory, overflowing into your stash.");','row(rows,"","Monster loot drops on the ground. Pick it up before closing a map.");');s=s.replace('case "items":','case "exchange":CraftingCurrency.exchange(p);show(p,"guide");return true;\n    case "items":');
# retain internal method for old protocol test but direct craft accepts exact source slot
needle='  ItemRoll old=item.getRoll(),next;Random random=new Random();int currency;'
s=s.replace(needle,'''  craft(p,action,slot,-1);
 }
 public static void craft(Player p,String action,int slot,int sourceSlot){
  if(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting())throw new IllegalArgumentException("Cannot craft right now");
  Item item=p.getInventory().get(slot);if(item==null||!item.hasRoll()||MapRuns.isGem(item))throw new IllegalArgumentException("Use a crafting material on equipment or a map");
  ItemRoll old=item.getRoll(),next;Random random=new Random();int currency;''');
for old,new in [(561,22400),(562,22401),(563,22402),(564,22403),(554,22404),(555,22405)]:s=s.replace('currency='+str(old)+';', 'currency='+str(new)+';')
s=s.replace('AffixRules.validateGenerated(next);if(p.getInventory().getCount(currency)<1)', 'AffixRules.validateGenerated(next);if(sourceSlot>=0&&(p.getInventory().get(sourceSlot)==null||p.getInventory().get(sourceSlot).getId()!=currency))throw new IllegalArgumentException("Choose the material again");if(p.getInventory().getCount(currency)<1)')
# replace craft actions rows with simple help, gem choices remain
start=s.index('}else{row(rows,"craft identify"');end=s.index('\n    }else inventory',start)
s=s[:start]+'''}else{row(rows,"","Close this panel; use a crafting material on the item in inventory.");row(rows,"","Wisdom: identify / Transmutation: normal to magic");row(rows,"","Alchemy: normal or magic to rare / Chaos: reroll");row(rows,"","Exalted: add an affix / Vaal: corrupt");row(rows,"items","Choose a different item");}'''+s[end:]
p.write_text(s)
