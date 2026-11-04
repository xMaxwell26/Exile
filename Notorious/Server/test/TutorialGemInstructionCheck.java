import com.koios.game.arpg.*;
import com.koios.game.model.player.Player;
import com.koios.game.model.item.Item;

/** Tutorial copy follows actual dropped-jewel state instead of promising automatic supports. */
public final class TutorialGemInstructionCheck {
 private static int checks;
 private static void check(boolean condition,String message){checks++;if(!condition)throw new AssertionError(message);}
 public static void main(String[]args)throws Exception{
  PrototypeIntegrationCheck.loaded();Player p=PrototypeIntegrationCheck.player("qagemtext",92);p.setAttribute("tutorial-stage",4);
  check(Tutorial.text(p).contains("Take the Uncut Support Jewel from the ground"),"Before pickup, tracker identifies the actual dropped reward");
  Item uncut=MapRuns.gem(1,true);p.getInventory().set(0,uncut);String uuid=uncut.getRoll().getInstanceId();
  check(Tutorial.text(p).contains("Right-click the Uncut Support Jewel, choose Craft"),"Uncut support requires the actual Craft action");
  check(!Tutorial.text(p).contains("Learn"),"No obsolete permanent-learning instruction");
  p.getInventory().set(0,MapRuns.gem(1,true,0));
  check(Tutorial.text(p).contains("Drag active gems into slots, then add the support gem"),"Crafted support guidance switches to socketing");
  p.getInventory().set(0,MapRuns.gem(20,true));
  check(Tutorial.text(p).contains("Take the Uncut Support Jewel"),"Unusable high-level reward does not hide beginner pickup guidance");
  p.getInventory().set(0,MapRuns.gem(1,false));
  check(Tutorial.text(p).contains("Take the Uncut Support Jewel"),"An active jewel cannot satisfy support tutorial instructions");
  p.getInventory().set(0,uncut);for(int i=0;i<10;i++)Tutorial.text(p);
  check(p.getInventory().get(0)==uncut&&uncut.getRoll().getInstanceId().equals(uuid)&&uncut.getRoll().getGem().kind==-1,"Reading tracker never crafts, consumes or replaces inventory rewards");
  p.setAttribute("tutorial-stage",3);check(Tutorial.text(p).contains("Allocate your first passive"),"Other tutorial stage copy preserved");
  System.out.println("Passed "+checks+" state-aware uncut jewel tutorial instruction checks.");
 }
}
