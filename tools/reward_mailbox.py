from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
(r/'game/arpg/RewardMailbox.java').write_text('''package com.koios.game.arpg;
import java.util.*;import java.nio.charset.StandardCharsets;import com.google.gson.Gson;
import com.koios.game.model.item.Item;import com.koios.game.model.player.Player;import com.koios.game.model.item.container.bank.Bank;
/** Persistent overflow rewards avoid dropping instance loot when both containers are full. */
public final class RewardMailbox {
 private final List<Item> items=new ArrayList<>();
 public int size(){return items.size();}
 public void add(Item item){items.add(item.copy());}
 public void claim(Player p){Iterator<Item> it=items.iterator();while(it.hasNext()){Item item=it.next();if(p.getInventory().hasRoomFor(item)&&p.getInventory().add(item.copy())||Bank.addToBank(p,item.copy()))it.remove();else break;}}
 public String save(){return Base64.getEncoder().encodeToString(new Gson().toJson(items).getBytes(StandardCharsets.UTF_8));}
 public void load(String value){Item[] restored=new Gson().fromJson(new String(Base64.getDecoder().decode(value),StandardCharsets.UTF_8),Item[].class);List<Item> valid=new ArrayList<>();for(Item item:restored){if(item==null||item.getCount()<1||item.getId()<0)throw new IllegalArgumentException("Invalid reward");if(item.hasRoll()){item.getRoll().validate();if(item.getCount()!=1)throw new IllegalArgumentException("Stacked reward roll");}valid.add(item);}items.clear();items.addAll(valid);}
}
''')
p=r/'game/model/player/Player.java';s=p.read_text().replace('private final com.koios.game.arpg.PassiveTree arpgPassives=', 'private final com.koios.game.arpg.RewardMailbox arpgRewards=new com.koios.game.arpg.RewardMailbox();\n    public com.koios.game.arpg.RewardMailbox getArpgRewards(){return arpgRewards;}\n    private final com.koios.game.arpg.PassiveTree arpgPassives=');p.write_text(s)
p=r/'game/arpg/MapRuns.java';s=p.read_text().replace('if(!Bank.addToBank(p,item))throw new IllegalStateException("Inventory and stash are full");p.sendMessage("Reward sent to your stash.");','if(!Bank.addToBank(p,item)){p.getArpgRewards().add(item);p.sendMessage("Containers full: reward saved in your mailbox (P / Guide).");}else p.sendMessage("Reward sent to your stash.");');p.write_text(s)
p=r/'game/arpg/PrototypePanel.java';s=p.read_text().replace('row(rows,"kit","Claim starter', 'row(rows,"claim","Claim overflow rewards: "+p.getArpgRewards().size());row(rows,"kit","Claim starter');s=s.replace('case "kit":','case "claim":p.getArpgRewards().claim(p);show(p,"guide");return true;\n    case "kit":');p.write_text(s)
p=r/'game/model/player/io/PlayerSaver.java';s=p.read_text().replace('writeValue(bw,"essential-arpg-xp",','writeValue(bw,"essential-arpg-rewards",player.getArpgRewards().save());\n                writeValue(bw,"essential-arpg-xp",');p.write_text(s)
p=r/'game/model/player/io/PlayerLoader.java';s=p.read_text().replace('case "essential-arpg-xp":','case "essential-arpg-rewards":\n            player.getArpgRewards().load(value);break;\n        case "essential-arpg-xp":');p.write_text(s)
