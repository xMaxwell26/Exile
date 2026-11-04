package com.koios.game.arpg;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import com.google.gson.Gson;
import com.koios.game.model.item.Item;
public final class ItemRollCodec {
 private static final Gson GSON=new Gson();
 public static String encode(Item item){return item.hasRoll()?Base64.getEncoder().encodeToString(GSON.toJson(item.getRoll()).getBytes(StandardCharsets.UTF_8)):"-";}
 public static <T extends Item> T restore(T item,String[] fields,int index){
  if(fields.length>index && !fields[index].trim().startsWith("#") && !fields[index].equals("-")){
   ItemRoll roll=GSON.fromJson(new String(Base64.getDecoder().decode(fields[index]),StandardCharsets.UTF_8),ItemRoll.class);
   roll.validate();item.setRoll(roll);EquipmentBases.migrate(item);
  }
  return item;
 }
}
