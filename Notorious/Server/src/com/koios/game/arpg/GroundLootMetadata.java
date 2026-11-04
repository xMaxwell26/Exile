package com.koios.game.arpg;
import java.util.*;import java.lang.ref.WeakReference;import java.nio.charset.StandardCharsets;import com.koios.game.model.item.*;import com.koios.game.model.player.Player;import com.koios.network.out.impl.SendStringPacket;
/** Presentation only: identity and pickup authority remain with GroundItemManager. */
public final class GroundLootMetadata {
 private static final class Record {final String key;WeakReference<Player> owner;Record(String key,Player p){this.key=key;owner=new WeakReference<>(p);}}
 private static final IdentityHashMap<GroundItemEntity,Record> RECORDS=new IdentityHashMap<>();private static long sequence;
 public static boolean eligible(Item item){return item.hasRoll()||CraftingCurrency.index(item.getId())>=0||item.getId()==MapRuns.PORTAL_SCROLL||item.getId()==995;}
 private static boolean visible(Player p,GroundItemEntity ground){return ground.getOwner()==p.getUsernameHash()&&ground.getZ()==p.getZ()&&p.distanceToPoint(ground.getX(),ground.getY())<=60&&MapRuns.metadataContains(p,ground.getLocation());}
 private static Record record(Player p,GroundItemEntity ground){Record r=RECORDS.get(ground);if(r==null){r=new Record("g"+(++sequence),p);RECORDS.put(ground,r);}else r.owner=new WeakReference<>(p);return r;}
 private static int color(Item item){if(item.hasRoll()){if(item.getRoll().getGem()!=null)return item.getRoll().getGem().support?0xE3BA72:0x7CBFE7;switch(item.getRoll().getRarity()){case MAGIC:return 0x8888FF;case RARE:return 0xFFFF77;case UNIQUE:return 0xAF6025;default:return 0xD5D5D5;}}return item.getId()==995?0xD6B75E:0xAA9E82;}
 private static String name(Item item){if(item.hasRoll())return (!item.getRoll().isIdentified()?"Unidentified ":"")+MapRuns.name(item);return item.getDefinition().getName();}
 public static void added(Player p,GroundItemEntity ground){Item item=ground.getItem();if(!eligible(item)||ground.getOwner()!=p.getUsernameHash())return;Record r=record(p,ground);if(visible(p,ground))send(p,"add|"+r.key+"|"+ground.getX()+"|"+ground.getY()+"|"+ground.getZ()+"|"+item.getId()+"|"+(item.hasRoll()?item.getRoll().getInstanceId():"")+"|"+color(item)+"|"+item.getCount()+"|"+Base64.getEncoder().encodeToString(name(item).getBytes(StandardCharsets.UTF_8)));}
 public static void removed(GroundItemEntity ground){Record r=RECORDS.remove(ground);if(r==null)return;Player p=r.owner.get();if(p!=null&&p.getUsernameHash()==ground.getOwner())send(p,"remove|"+r.key);}
 public static void snapshot(Player p,Iterable<GroundItemEntity> items){send(p,"clear");for(GroundItemEntity item:items)if(visible(p,item))added(p,item);}
 private static final Map<Player,Long> REQUESTS=new WeakHashMap<>();private static long requestSequence;
 private static GroundItemEntity resolve(Player p,String key){for(Map.Entry<GroundItemEntity,Record> entry:RECORDS.entrySet())if(entry.getValue().key.equals(key)&&visible(p,entry.getKey()))return entry.getKey();return null;}
 private static boolean ready(Player p,GroundItemEntity item,String key,String uuid){return !p.isDisconnected()&&!p.isBusy()&&!p.isDead()&&!p.isTeleporting()&&!p.isTrading()&&!p.getBankField().isBanking()&&!p.isPacketReadDisabled()&&resolve(p,key)==item&&uuid.equals(item.getItem().hasRoll()?item.getItem().getRoll().getInstanceId():"")&&Math.abs(p.getX()-item.getX())<=25&&Math.abs(p.getY()-item.getY())<=25;}
 public static boolean handle(Player p,String input){if(!input.equals("arpgloot")&&!input.startsWith("arpgloot "))return false;long token=++requestSequence;REQUESTS.put(p,token);p.walkingToItem=false;String[] args=input.trim().split(" +");if(args.length!=2||args[1].length()>32)return true;final String key=args[1];final GroundItemEntity item=resolve(p,key);if(item==null)return true;final String uuid=item.getItem().hasRoll()?item.getItem().getRoll().getInstanceId():"";if(!ready(p,item,key,uuid))return true;com.koios.game.content.combat.Combat.resetCombat(p);p.walkingToItem=true;
  com.koios.GameService.getTaskScheduler().schedule(new com.koios.task.Task(){int elapsed;protected void execute(){if(!Objects.equals(REQUESTS.get(p),token)||!p.walkingToItem||!ready(p,item,key,uuid)||++elapsed>25){stop();return;}if(p.getX()==item.getX()&&p.getY()==item.getY()){com.koios.GameService.getGroundItemManager().removeExactGroundItem(p,item);stop();}}protected void onStop(){if(Objects.equals(REQUESTS.get(p),token)){REQUESTS.remove(p);p.walkingToItem=false;}}});return true;
 }
 private static void send(Player p,String value){p.getSession().send(new SendStringPacket(value,65522));}
 private GroundLootMetadata(){}
}
