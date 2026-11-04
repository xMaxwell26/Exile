import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.DrawingArea;

/** Chat controls and long drafts must stay inside their reserved strips. */
public final class ChatChromeLayoutCheck {
 public static void main(String[] args)throws Exception{
  int checks=0;
  BufferedImage image=new BufferedImage(519,165,BufferedImage.TYPE_INT_RGB);
  int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();
  for(int mode=0;mode<4;mode++){
   Arrays.fill(pixels,ExileTheme.BACKGROUND);DrawingArea.initDrawingArea(165,519,pixels);
   ChatChrome.channels(ExileTheme.small(),0,mode,mode,new int[]{mode,mode,mode,mode});
   for(int y=0;y<143;y++)for(int x=0;x<519;x++)if(pixels[x+y*519]!=ExileTheme.BACKGROUND)throw new AssertionError("Channel text crosses its control strip");
   checks++;
  }
  StringBuilder draft=new StringBuilder();for(int i=0;i<80;i++)draft.append('W');draft.append('*');
  String visible=ChatChrome.draft(ExileTheme.body(),draft.toString(),320);
  if(!visible.endsWith("*")||ExileTheme.textWidth(visible)>320)throw new AssertionError("Long draft caret must stay visible");checks++;
  ChatChrome.backdrop(0,0,false);ChatChrome.channels(ExileTheme.small(),0,0,-1,new int[]{0,1,2,3});
  ExileTheme.label(ExileTheme.body(),12,134,visible,ExileTheme.TEXT);
  ImageIO.write(image,"png",new File("build/chat-controls-audit.png"));
  System.out.println("Passed "+checks+" chat strip and long-draft layout checks.");
 }
}
