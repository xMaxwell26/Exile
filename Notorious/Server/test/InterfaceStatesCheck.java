import java.util.*;
import java.lang.reflect.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;
import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/** Review every current panel state using real server payloads and measured text, not pixel-count claims. */
public final class InterfaceStatesCheck {
 private static int checks,states;
 private static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}
 private static String payload(Player p,String tab){return PanelCardsCheck.payload(p,tab);}
 private static String[] lines(String encoded){return new String(Base64.getDecoder().decode(encoded),java.nio.charset.StandardCharsets.UTF_8).split("\n");}
 private static void reset(Player p){for(int i=0;i<28;i++)p.getInventory().set(i,null);p.setAttribute("craft-slot",-1);p.setAttribute("prototype-page",0);}
 private static Object value(Object object,String name)throws Exception{Field field=object.getClass().getDeclaredField(name);field.setAccessible(true);return field.get(object);}
 private static void render(String name,String encoded)throws Exception{
  com.koios.client.arpg.PrototypePanel panel=new com.koios.client.arpg.PrototypePanel();panel.accept(encoded);
  BufferedImage image=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);DrawingArea.initDrawingArea(334,512,((DataBufferInt)image.getRaster().getDataBuffer()).getData());panel.draw(ExileTheme.body(),name.equals("tree-hover")?67:-1,name.equals("tree-hover")?149:-1);
  File output=new File("build/interface-states/"+name+".png");output.getParentFile().mkdirs();ImageIO.write(image,"png",output);states++;
  PanelCards cards=(PanelCards)value(panel,"cards");
  int recipes=0;
  for(Object card:(java.util.List<?>)value(cards,"cards")){
   int x=(Integer)value(card,"x"),y=(Integer)value(card,"y"),w=(Integer)value(card,"w"),h=(Integer)value(card,"h");
   check(x>=16&&y>=16&&x+w<=496&&y+h<=322,name+": card fits panel");
   String action=(String)value(card,"action"),label=(String)value(card,"label"),detail=(String)value(card,"detail");int icon=(Integer)value(card,"mapId");
   if(!action.isEmpty())check(action.equals(cards.actionAt(x+w/2,y+h/2)),name+": painted action remains clickable");
   if((Boolean)value(cards,"reference")&&icon>=0){recipes++;check(ChatWrap.lines(detail,394,ExileTheme.body()::getTextWidth).size()<=2,name+": complete recipe fits two readable lines");check(ExileTheme.body().getTextWidth(label)<=394,name+": material name fits beside icon");}
   if(action.isEmpty()&&icon>=0&&w==145){check(ExileTheme.small().getTextWidth(label)<=94&&ExileTheme.small().getTextWidth(detail)<=94,name+": compact material name and effect remain complete");}
   if(action.startsWith("open "))check(ExileTheme.body().getTextWidth(label)<=w-51,name+": complete map title fits beside icon");
   if(h==48&&icon<0)check(ChatWrap.lines(label,w-20,ExileTheme.body()::getTextWidth).size()<=2,name+": utility card retains full text in two padded lines");
   if(h<=30)check(ExileTheme.body().getTextWidth(label)<=w-20,name+": utility/navigation text fits a single button line");
  }
  if((Boolean)value(cards,"reference")){check(recipes>=1&&recipes<=3,name+": only actual recipe rows become material cards");check(((java.util.List<?>)value(cards,"notes")).size()==3,name+": all three general restrictions stay separate");}
  if((Boolean)value(cards,"reference"))for(String note:(java.util.List<String>)value(cards,"notes"))check(ExileTheme.small().getTextWidth(note)<=444,name+": restriction remains complete");
  if((Boolean)value(cards,"selectedItem")){int baseline=147;for(String note:(java.util.List<String>)value(cards,"notes")){baseline+=ChatWrap.lines(note,424,ExileTheme.body()::getTextWidth).size()*16+5;}check(baseline<=262,name+": all selected-item instructions fit without truncation");}
 }
 public static void main(String[]args)throws Exception{
  PrototypeIntegrationCheck.loaded();Player p=PrototypeIntegrationCheck.player("qauireview",94);p.allowSaveRequests=false;p.setAttribute("tutorial-stage",11);PrototypeIntegrationCheck.idle(p);
  render("guide",payload(p,"guide"));render("tree",payload(p,"tree"));render("tree-hover",payload(p,"tree"));
  for(PassiveTree.Node node:PassiveTree.NODES.values())check(ExileTheme.small().getTextWidth(node.description())<=444,"Complete passive detail fits: "+node.name);
  for(int page=0;page<4;page++){p.setAttribute("prototype-page",page);render("reference-"+page,payload(p,"reference"));}
  reset(p);render("craft-empty",payload(p,"craft"));
  for(int i=0;i<8;i++)p.getInventory().set(i,MapRuns.gem(1,i%2==0));render("craft-gems",payload(p,"craft"));p.setAttribute("prototype-page",1);render("craft-gems-page2",payload(p,"craft"));
  for(int kind=0;kind<4;kind++){reset(p);Item item=kind==0?EquipmentBases.create(EquipmentBases.starter(true),20):kind==3?MapRuns.gem(1,false):MapRuns.gem(1,kind==2,kind==2?0:1);p.getInventory().set(0,item);p.setAttribute("craft-slot",0);p.setAttribute("craft-uuid",item.getRoll().getInstanceId());render("selected-"+kind,payload(p,"craft"));}
  reset(p);render("maps-empty",payload(p,"maps"));for(int i=0;i<8;i++)p.getInventory().set(i,MapRuns.map(3,i%8,i%3));render("maps-populated",payload(p,"maps"));p.setAttribute("prototype-page",1);render("maps-page2",payload(p,"maps"));
  reset(p);for(int i=0;i<3;i++)p.getInventory().set(i,MapRuns.map(10,7,i));render("maps-tier10",payload(p,"maps"));
  render("maps-resume",Base64.getEncoder().encodeToString("maps\nhome\tReturn to private house base\ntraining\tCreate a basic training map - 25 coins\nresume\tRe-enter active map / portals: 5 / champion defeated\nabandon\tClose completed map (keep rewards)".getBytes("UTF-8")));
  render("maps-abandon",Base64.getEncoder().encodeToString("maps\nhome\tReturn to private house base\ntraining\tCreate a basic training map - 25 coins\nresume\tRe-enter active map / portals: 5\nabandon\tAbandon active map and release its instance".getBytes("UTF-8")));
  reset(p);p.getInventory().set(0,MapRuns.gem(7,false));p.setAttribute("gem-craft-slot",0);p.setAttribute("gem-craft-uuid",p.getInventory().get(0).getRoll().getInstanceId());for(String filter:new String[]{"all","sword","dagger","mace"}){p.setAttribute("gem-craft-filter",filter);render("jewel-"+filter,payload(p,"gemcraft"));}
  p.getInventory().set(0,MapRuns.gem(7,true));p.setAttribute("gem-craft-uuid",p.getInventory().get(0).getRoll().getInstanceId());p.setAttribute("gem-craft-filter","support");render("jewel-support",payload(p,"gemcraft"));
  render("supplies",Base64.getEncoder().encodeToString("supplies\nhub buy portals\t5 Portal Scrolls / 25 coins\nhub buy map\tTraining Map T1 / 25 coins\nhub buy wisdom\tWisdom Sigil / 20 coins\nguide\tBack to guide".getBytes("UTF-8")));
  System.out.println("Reviewed "+states+" native panel states; passed "+checks+" measured content/action checks.");
 }
}
