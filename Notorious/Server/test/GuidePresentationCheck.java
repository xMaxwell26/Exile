import java.util.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;

/** Presentation-owned guide geometry must retain every server action and exact click boundary. */
public final class GuidePresentationCheck {
 private static int checks;
 private static void check(boolean ok,String reason){checks++;if(!ok)throw new AssertionError(reason);}
 private static String encode(String text){return Base64.getEncoder().encodeToString(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));}
 private static BufferedImage canvas(){BufferedImage image=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);DrawingArea.initDrawingArea(334,512,((DataBufferInt)image.getRaster().getDataBuffer()).getData());return image;}
 public static void main(String[]args)throws Exception{
  PrototypePanel panel=new PrototypePanel();String[] actions={"exchange","scrolls","claim","kit","home","intro","tree","craft","maps"};
  StringBuilder payload=new StringBuilder("guide");for(String action:actions)payload.append("\n").append(action).append("\tServer explanation for ").append(action);payload.append("\n\tK: skills, loadout and controls / P: field guide");payload.append("\n\tEnter opens chat. All keyboard skills pause while typing.");payload.append("\n\tAfter level 10: limited map entries and death XP penalties.");payload.append("\n\tMonster loot drops on the ground. Pick it up before closing a map.");panel.accept(encode(payload.toString()));
  for(int i=0;i<actions.length;i++){
   int x=28+i%2*232,y=90+i/2*40;
   for(int[] point:new int[][]{{x,y},{x+219,y+33},{x+110,y+17}})check(actions[i].equals(panel.actionAt(point[0],point[1],false)),"Visible guide card retains action "+actions[i]);
   check(!actions[i].equals(panel.actionAt(x+220,y+17,false))&&!actions[i].equals(panel.actionAt(x+110,y+34,false)),"Guide targets exclude spacing gutters");
  }
  for(int i=0;i<5;i++)check(new String[]{"guide","tree","craft","maps","home"}[i].equals(panel.actionAt(22+i*93,20,false)),"Mixed-case tabs retain protocol navigation");
  check(panel.actionAt(112,25,false)==null,"Tab gutters do not steal an unrelated action");
  java.lang.reflect.Field progression=PrototypePanel.class.getDeclaredField("guideProgression"),loot=PrototypePanel.class.getDeclaredField("guideLoot"),controls=PrototypePanel.class.getDeclaredField("guideControls");progression.setAccessible(true);loot.setAccessible(true);controls.setAccessible(true);
  String progressionText=(String)progression.get(panel),lootText=(String)loot.get(panel),controlText=(String)controls.get(panel);
  check(progressionText.contains("After level 10")&&progressionText.contains("limited map entries")&&progressionText.contains("death XP penalties"),"Visible guide retains level threshold, limited entries and death penalty semantics");
  java.util.List<String> ruleLines=ChatWrap.lines(progressionText,210,ExileTheme.small()::getTextWidth);
  check(ruleLines.size()==2&&263+(ruleLines.size()-1)*14<=277,"Progression rules fit unused guide column with real padding");
  check(lootText.contains("loot drops on the ground")&&lootText.contains("before closing a map"),"Visible guide retains ground-drop and map-close warning");
  check(controlText.contains("Typing pauses keyboard skills"),"Guide keeps chat keyboard ownership instruction");
  check(ExileTheme.small().getTextWidth(lootText)<=444&&ExileTheme.small().getTextWidth(controlText)<=444,"Both complete footer lines fit without truncation");
  check(panel.actionAt(270,263,false)==null,"Progression notice is informational and leaves action-card targets unchanged");
  canvas();ExileTheme.label(ExileTheme.small(),28,309,lootText,ExileTheme.MUTED);int guideLastInk=-1;for(int y=298;y<334;y++)for(int x=20;x<485;x++)if(DrawingArea.pixels[y*512+x]!=0)guideLastInk=Math.max(guideLastInk,y);check(318-guideLastInk>=5,"Complete loot warning retains lower-frame clearance");
  for(String action:actions)check(ExileTheme.small().getTextWidth("Server explanation for "+action)<=444,"Action explanation fits footer without hiding rules");
  BufferedImage guide=canvas();panel.draw(ExileTheme.body(),-1,-1);ImageIO.write(guide,"png",new File("build/guide-polish.png"));BufferedImage guideHover=canvas();panel.draw(ExileTheme.body(),60,105);ImageIO.write(guideHover,"png",new File("build/guide-hover.png"));
  StringBuilder treePayload=new StringBuilder("tree|3|false");
  for(com.koios.game.arpg.PassiveTree.Node node:com.koios.game.arpg.PassiveTree.NODES.values())treePayload.append("\n").append(node.id).append("|").append(node.parent).append("|").append(node.id==1||node.id==2||node.id==8).append("|").append(node.name).append("|").append(node.description());
  panel.accept(encode(treePayload.toString()));
  check("allocate 1".equals(panel.actionAt(211,132,false))&&"refund 1".equals(panel.actionAt(211,132,true)),"Passive node symbols retain allocate/refund routes");
  int[][] energy={{256,187},{256,217},{256,247},{211,243},{301,243},{169,232},{343,232}};for(int i=0;i<energy.length;i++)check(("allocate "+(15+i)).equals(panel.actionAt(energy[i][0],energy[i][1],false)),"Spaced energy branch retains actual node targets");
  check(panel.actionAt(40,100,false)==null,"Old guide cards clear when changing to the tree");
  BufferedImage tree=canvas();panel.draw(ExileTheme.body(),211,132);ImageIO.write(tree,"png",new File("build/passive-polish.png"));
  BufferedImage hovered=canvas();panel.draw(ExileTheme.body(),67,149);ImageIO.write(hovered,"png",new File("build/interface-states/tree-hover.png"));
  canvas();ExileTheme.label(ExileTheme.body(),27,310,"0 points available",ExileTheme.HEADING);ExileTheme.label(ExileTheme.small(),337,310,"Free refunds: off",ExileTheme.MUTED);
  int lastInk=-1;for(int y=295;y<334;y++)for(int x=20;x<485;x++)if(DrawingArea.pixels[y*512+x]!=0)lastInk=Math.max(lastInk,y);
  check(318-lastInk>=5,"Footer text and engraved shadow retain five pixels before lower inlay; last ink="+lastInk);
  System.out.println("Passed "+checks+" guide/passive presentation routing checks.");
 }
}
