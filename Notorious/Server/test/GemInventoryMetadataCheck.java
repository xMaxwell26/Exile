import java.util.*;
import java.nio.charset.StandardCharsets;
import com.koios.client.arpg.ItemTooltipCache;

/** Slot-authoritative public metadata selects gem names/actions without exposing private details. */
public final class GemInventoryMetadataCheck {
 private static int checks;
 private static void check(boolean ok,String reason){checks++;if(!ok)throw new AssertionError(reason);}
 private static String encode(String text){return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));}
 private static void put(ItemTooltipCache cache,int widget,int slot,int id,String normal,String advanced){cache.accept("item|"+widget+"|"+slot+"|"+id+"|123|"+encode(normal)+"|"+encode(advanced));}
 public static void main(String[]args){
  ItemTooltipCache cache=new ItemTooltipCache();String fallback="Uncut Skill Gem";
  check(cache.inventoryName(3214,0,22411,fallback).equals("Skill Gem")&&!cache.canCraftGem(3214,0,22411),"Missing metadata preserves fallback name and cannot invent a Craft action");
  check(cache.inventoryName(3214,0,22412,"Uncut Support Gem").equals("Support Gem"),"Missing support metadata uses a neutral category rather than an invented uncut state");
  for(int id:new int[]{22411,22412})for(int level:new int[]{1,20}){
   String name=(id==22411?"Uncut Skill Jewel":"Uncut Support Jewel")+" / Level "+level;put(cache,3214,0,id,name,"Secret advanced text");
   check(cache.inventoryName(3214,0,id,fallback).equals(name)&&cache.canCraftGem(3214,0,id),"Eligible uncut gem gets exact public name and Craft");
  }
  for(String name:new String[]{"Heavy Strike Gem / Level 1","Cleave Gem / Level 20","Whirlwind Gem / Level 5","Ground Slam Gem / Level 2","Execute Gem / Level 7"}){put(cache,3214,0,22411,name,"Private metadata");check(cache.inventoryName(3214,0,22411,fallback).equals(name)&&!cache.canCraftGem(3214,0,22411),"Crafted active skill retains exact identity and loses Craft");}
  for(String name:new String[]{"Splash Gem / Level 1","Faster Attacks Gem / Level 20","Brutality Gem / Level 4"}){put(cache,3214,0,22412,name,"Private metadata");check(cache.inventoryName(3214,0,22412,fallback).equals(name)&&!cache.canCraftGem(3214,0,22412),"Crafted support retains exact identity and loses Craft");}
  put(cache,3214,0,22411,"Uncut Skill Jewel / Level 1\nUnidentified","Execute Gem / Level 20");check(cache.inventoryName(3214,0,22411,fallback).equals("Skill Gem")&&!cache.canCraftGem(3214,0,22411),"Unidentified entry cannot expose advanced identity or actions");
  put(cache,3214,0,22411,"Cleave Gem / Level 2","Private affix details");check(cache.inventoryName(3214,1,22411,fallback).equals("Skill Gem")&&cache.inventoryName(5382,0,22411,fallback).equals("Skill Gem")&&!cache.canCraftGem(3214,0,22412),"Slot/widget/base-id isolation prevents stale neighboring metadata");
  cache.accept("remove|3214|0");check(cache.inventoryName(3214,0,22411,fallback).equals("Skill Gem")&&!cache.canCraftGem(3214,0,22411),"Removal clears dynamic name/action");
  put(cache,3214,0,22411,"Uncut Skill Jewel / Level 1","details");put(cache,3214,0,22411,"Ground Slam Gem / Level 1","details");check(!cache.canCraftGem(3214,0,22411),"Craft update in same slot immediately removes obsolete Craft action");
  for(String invalid:new String[]{"Uncut Skill Jewel / Level 0","Uncut Skill Jewel / Level 21","Uncut Skill Jewel / Level bad"}){put(cache,3214,0,22411,invalid,"details");check(!cache.canCraftGem(3214,0,22411),"Malformed level cannot create Craft action");}
  put(cache,3214,0,22420,"Private invented equipment name","details");check(cache.inventoryName(3214,0,22420,"Sword").equals("Sword"),"Change stays scoped to physical gems and leaves equipment naming/privacy unchanged");
  System.out.println("Passed "+checks+" gem inventory public-name/action/privacy/cache checks.");
 }
}
