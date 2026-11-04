import java.awt.*;import java.awt.image.*;import java.io.*;import javax.imageio.ImageIO;
import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;import com.koios.client.definition.FloorDefinition;import com.koios.client.graphic.*;
public class FoundryMaterialProbe {
 public static void main(String[] args)throws Exception{
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r")){
   Decompressor cache=new Decompressor(data,config,1);FloorDefinition.unpackConfig(new StreamLoader(cache.decompress(2)));Rasterizer.method368(new StreamLoader(cache.decompress(6)));
   BufferedImage sheet=new BufferedImage(800,700,BufferedImage.TYPE_INT_RGB);Graphics2D g=sheet.createGraphics();g.setColor(new Color(0x17232C));g.fillRect(0,0,800,700);g.setFont(new Font("SansSerif",Font.PLAIN,12));
   for(int id=0;id<51;id++){Background texture=Rasterizer.aBackgroundArray1474s[id];if(texture==null)continue;BufferedImage tile=new BufferedImage(texture.anInt1452,texture.anInt1453,BufferedImage.TYPE_INT_RGB);for(int y=0;y<texture.anInt1453;y++)for(int x=0;x<texture.anInt1452;x++)tile.setRGB(x,y,texture.anIntArray1451[texture.aByteArray1450[x+y*texture.anInt1452]&255]);int left=id%8*100,top=id/8*100;g.drawImage(tile,left+8,top+20,80,72,null);g.setColor(new Color(0xD4BB87));g.drawString("Texture "+id,left+8,top+15);}
   g.dispose();ImageIO.write(sheet,"png",new File("build/foundry-textures.png"));for(int i=0;i<FloorDefinition.cache.length&&i<255;i++){FloorDefinition floor=FloorDefinition.cache[i];if(floor.anInt391>=0)System.out.println("Floor "+(i+1)+" texture="+floor.anInt391+" rgb="+Integer.toHexString(floor.anInt390)+" coherent="+(floor.anInt398>0&&floor.anInt397<floor.anInt398));}
  }
 }
}
