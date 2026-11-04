import java.io.*;import java.awt.*;import java.awt.image.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.arpg.*;import com.koios.client.graphic.*;import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;

public class ChatChromePreview {
 public static void main(String[] args)throws Exception{
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile index=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r")){
   StreamLoader archive=new StreamLoader(new Decompressor(data,index,1).decompress(1));TextDrawingArea small=new TextDrawingArea(false,"p11_full",archive);RSFont regular=new RSFont(false,"p12_full",archive);
   BufferedImage preview=new BufferedImage(1048,220,BufferedImage.TYPE_INT_RGB);Graphics2D result=preview.createGraphics();result.setColor(new Color(0x10171E));result.fillRect(0,0,1048,220);result.setColor(new Color(0xD4BB87));result.setFont(new Font("SansSerif",Font.PLAIN,13));result.drawString("Opaque chat",10,20);result.drawString("Transparent chat over a sample world",535,20);
   for(int mode=0;mode<2;mode++){
    BufferedImage panel=new BufferedImage(519,165,BufferedImage.TYPE_INT_RGB);int[] pixels=((DataBufferInt)panel.getRaster().getDataBuffer()).getData();for(int y=0;y<165;y++)for(int x=0;x<519;x++)pixels[x+y*519]=mode==0?0:((x/32+y/24)%2==0?0x52613A:0x756855);
    DrawingArea.initDrawingArea(165,519,pixels);ChatChrome.backdrop(0,0,mode==1);ChatChrome.channels(small,0,0,2,new int[]{0,1,2,0});ChatChrome.scrollbar(114,40,7,496,260);
    regular.drawBasicString("The Settlement Champion has been defeated.",11,33,ChatChrome.TEXT,0);regular.drawBasicString("A support gem dropped nearby. Pick it up to learn a support.",11,49,ChatChrome.TEXT,0);regular.drawBasicString("Qaember: ",11,79,ChatChrome.GOLD,0);regular.drawBasicString("Ready for the next map.",75,79,ChatChrome.INPUT,0);regular.drawBasicString(ChatChrome.readable("<col=800000>You need more mana.</col>"),11,109,ChatChrome.TEXT,0);regular.drawBasicString("Qaember:",11,133,ChatChrome.GOLD,0);regular.drawBasicString("Press Enter or click to chat",75,133,ChatChrome.INPUT,0);
    result.drawImage(panel,mode*529,35,null);
   }
   result.dispose();ImageIO.write(preview,"png",new File("build/chat-chrome.png"));System.out.println("Rendered opaque and transparent chat with actual client fonts.");
  }
 }
}
