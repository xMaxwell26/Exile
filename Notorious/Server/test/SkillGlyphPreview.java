import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.arpg.*;import com.koios.client.graphic.*;

public class SkillGlyphPreview {
 public static void main(String[] args)throws Exception{
  String[] names={"Heavy Strike","Cleave","Whirlwind","Ground Slam","Execute","Splash","Faster Attacks","Brutality"};
  BufferedImage preview=new BufferedImage(880,245,BufferedImage.TYPE_INT_RGB);Graphics2D result=preview.createGraphics();result.setColor(new Color(0x10191F));result.fillRect(0,0,880,245);result.setFont(new Font("SansSerif",Font.PLAIN,12));
  for(int i=0;i<8;i++){
   boolean support=i>=5;int id=support?i-5:i;
   for(int size:new int[]{24,32,48,96}){
    Sprite sprite=SkillGlyphs.icon(id,support,size);if(sprite==null)throw new AssertionError("Missing glyph "+i+" / "+size);int opaque=0;for(int pixel:sprite.myPixels)if(pixel!=0)opaque++;if(opaque<size*size/12||opaque>size*size*.8)throw new AssertionError("Unreadable glyph silhouette "+i+" / "+size);
    if(size==32&&sprite.highResolutionArt==null)throw new AssertionError("32px glyph lacks original source for DPI presentation");
   }
   result.setColor(new Color(0xD4BB87));result.drawString(names[i],i*110+8,20);
   for(int row=0;row<3;row++){
    int size=new int[]{24,32,48}[row],y=new int[]{45,95,155}[row];BufferedImage cell=new BufferedImage(100,70,BufferedImage.TYPE_INT_RGB);int[] pixels=((DataBufferInt)cell.getRaster().getDataBuffer()).getData();Arrays.fill(pixels,0x18232C);DrawingArea.initDrawingArea(70,100,pixels);SkillGlyphs.icon(id,support,size).drawSprite((100-size)/2,6);result.drawImage(cell,i*110+5,y,null);
   }
  }
  result.dispose();ImageIO.write(preview,"png",new File("build/skill-glyphs.png"));System.out.println("Eight letter-free glyphs retain readable native silhouettes at four sizes.");
 }
}
