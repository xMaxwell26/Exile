import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.world.Model;import com.koios.client.definition.ItemDefinition;import com.koios.client.graphic.*;

/** The head must present solid mass from every cardinal view, rather than a cage outline. */
public class MaceSilhouetteCheck {
 public static void main(String[] args)throws Exception{
  CustomItemVisualCheck.main(new String[0]);Rasterizer.method372(.85);
  BufferedImage sheet=new BufferedImage(880,240,BufferedImage.TYPE_INT_RGB);Graphics2D result=sheet.createGraphics();result.setColor(new Color(0x17232C));result.fillRect(0,0,880,240);result.setColor(new Color(0xD4BB87));result.setFont(new Font("SansSerif",Font.PLAIN,12));
  for(int view=0;view<4;view++){
   Model model=ItemDefinition.forID(22422).method196(0);int minX=9999,maxX=-9999,minY=9999,maxY=-9999,minZ=9999;
   for(int i=0;i<model.anInt1626;i++){minX=Math.min(minX,model.anIntArray1627[i]);maxX=Math.max(maxX,model.anIntArray1627[i]);minY=Math.min(minY,model.anIntArray1628[i]);maxY=Math.max(maxY,model.anIntArray1628[i]);minZ=Math.min(minZ,model.anIntArray1629[i]);}
   int centerX=(minX+maxX)/2,centerY=(minY+maxY)/2,centerZ=minZ+18;
   for(int i=0;i<model.anInt1626;i++){model.anIntArray1627[i]-=centerX;model.anIntArray1628[i]-=centerY;model.anIntArray1629[i]-=centerZ;}
   model.method479(96,850,-30,-50,-50,true);BufferedImage image=new BufferedImage(220,220,BufferedImage.TYPE_INT_RGB);int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();Arrays.fill(pixels,0x17232C);DrawingArea.initDrawingArea(220,220,pixels);Rasterizer.method364();model.method482(view*512,0,0,0,0,350);
   int solid=0;for(int y=105;y<115;y++)for(int x=105;x<115;x++)if(pixels[x+y*220]!=0x17232C)solid++;
   if(solid<80)throw new AssertionError("Mace head has a central gap at view "+view+": "+solid+"/100 pixels");
   result.drawImage(image,view*220,20,null);result.drawString("Head / "+view*90+" degrees",view*220+12,15);
  }
  result.dispose();ImageIO.write(sheet,"png",new File("build/mace-silhouette.png"));System.out.println("Mace head remains solid in four cardinal native renders.");
 }
}
