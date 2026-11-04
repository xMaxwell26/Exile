package com.koios.game.arpg;
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
