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
  StringBuilder payload=new StringBuilder("guide");for(String action:actions)payload.append("\n").append(action).append("\tServer explanation for ").append(action);payload.append("\n\tInstructional copy is inert");panel.accept(encode(payload.toString()));
  for(int i=0;i<actions.length;i++){
   int x=28+i%2*232,y=90+i/2*40;
   for(int[] point:new int[][]{{x,y},{x+219,y+33},{x+110,y+17}})check(actions[i].equals(panel.actionAt(point[0],point[1],false)),"Visible guide card retains action "+actions[i]);
   check(!actions[i].equals(panel.actionAt(x+220,y+17,false))&&!actions[i].equals(panel.actionAt(x+110,y+34,false)),"Guide targets exclude spacing gutters");
  }
  for(int i=0;i<5;i++)check(new String[]{"guide","tree","craft","maps","home"}[i].equals(panel.actionAt(22+i*93,20,false)),"Mixed-case tabs retain protocol navigation");
  check(panel.actionAt(112,25,false)==null,"Tab gutters do not steal an unrelated action");
  BufferedImage guide=canvas();panel.draw(ExileTheme.body(),60,105);ImageIO.write(guide,"png",new File("build/guide-polish.png"));
  StringBuilder treePayload=new StringBuilder("tree|3|false");
  for(com.koios.game.arpg.PassiveTree.Node node:com.koios.game.arpg.PassiveTree.NODES.values())treePayload.append("\n").append(node.id).append("|").append(node.parent).append("|").append(node.id==1||node.id==2||node.id==8).append("|").append(node.name).append("|").append(node.description());
  panel.accept(encode(treePayload.toString()));
  check("allocate 1".equals(panel.actionAt(211,132,false))&&"refund 1".equals(panel.actionAt(211,132,true)),"Passive node symbols retain allocate/refund routes");
  check(panel.actionAt(40,100,false)==null,"Old guide cards clear when changing to the tree");
  BufferedImage tree=canvas();panel.draw(ExileTheme.body(),211,132);ImageIO.write(tree,"png",new File("build/passive-polish.png"));
  System.out.println("Passed "+checks+" guide/passive presentation routing checks.");
 }
}
