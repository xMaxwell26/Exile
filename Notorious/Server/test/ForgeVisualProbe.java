import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;import com.koios.client.definition.ObjectDefinition;import com.koios.client.graphic.*;import com.koios.client.world.Model;

/** Small cache-backed art-direction probe; no production placements are changed. */
public class ForgeVisualProbe {
 public static void main(String[] args)throws Exception{
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");RandomAccessFile models=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r")){
   ObjectDefinition.unpackConfig(new StreamLoader(new Decompressor(data,config,1).decompress(2)));Model.method459(0,null);Rasterizer.method368(new StreamLoader(new Decompressor(data,config,1).decompress(6)));Rasterizer.method372(.85);Rasterizer.method367();Decompressor cache=new Decompressor(data,models,2);Set<Integer> loaded=new HashSet<>();
   int[] ids={2781,2785,3044,11009,11010,11017,11018,21879,26814};int height=((ids.length+3)/4)*250;BufferedImage preview=new BufferedImage(880,height,BufferedImage.TYPE_INT_RGB);Graphics2D graphics=preview.createGraphics();graphics.setColor(new Color(0x17232C));graphics.fillRect(0,0,880,height);graphics.setFont(new Font("SansSerif",Font.PLAIN,12));
   for(int index=0;index<ids.length;index++){
    ObjectDefinition definition=ObjectDefinition.forID(ids[index]);System.out.println(ids[index]+" name="+definition.name+" footprint="+definition.sizeX+"x"+definition.sizeY+" types="+Arrays.toString(definition.anIntArray776)+" models="+Arrays.toString(definition.anIntArray773));
    graphics.setColor(new Color(0xD4BB87));graphics.drawString(ids[index]+" / "+definition.name,(index%4)*220+8,(index/4)*250+18);graphics.drawString("footprint "+definition.sizeX+" x "+definition.sizeY,(index%4)*220+8,(index/4)*250+35);
    if(definition.anIntArray773==null)continue;for(int model:definition.anIntArray773)SceneryVisualCheck.load(cache,model&65535,loaded);Model model=definition.method578(10,0,0,0,0,0,-1);if(model==null){System.out.println("  No type10 render");continue;}graphics.drawImage(SceneryVisualCheck.render(model),(index%4)*220,(index/4)*250+45,null);
   }
   graphics.dispose();ImageIO.write(preview,"png",new File("build/forge-props.png"));
   ObjectDefinition furnace=ObjectDefinition.forID(26814);BufferedImage turns=new BufferedImage(880,250,BufferedImage.TYPE_INT_RGB);Graphics2D angleGraphics=turns.createGraphics();angleGraphics.setColor(new Color(0x17232C));angleGraphics.fillRect(0,0,880,250);angleGraphics.setFont(new Font("SansSerif",Font.PLAIN,12));
   for(int rotation=0;rotation<4;rotation++){angleGraphics.setColor(new Color(0xD4BB87));angleGraphics.drawString("26814 brick furnace / rotation "+rotation,rotation*220+8,18);angleGraphics.drawImage(SceneryVisualCheck.render(furnace.method578(10,rotation,0,0,0,0,-1)),rotation*220,40,null);}
   angleGraphics.dispose();ImageIO.write(turns,"png",new File("build/furnace-angles.png"));
   Model raw=Model.method462(1192);double x=0,z=0;int warm=0;if(raw.anIntArray1640!=null)for(int face=0;face<raw.anInt1630;face++){int color=raw.anIntArray1640[face],hue=color>>10,saturation=color>>7&7,light=color&127;if(hue<12&&saturation>=3&&light>=30){for(int vertex:new int[]{raw.anIntArray1631[face],raw.anIntArray1632[face],raw.anIntArray1633[face]}){x+=raw.anIntArray1627[vertex];z+=raw.anIntArray1629[vertex];warm++;}}}System.out.println("Raw furnace warm-face centroid x="+(x/Math.max(1,warm))+" z="+(z/Math.max(1,warm))+" samples="+warm);
   for(int id=0;id<27283;id++){ObjectDefinition candidate=ObjectDefinition.forID(id);if(candidate.name!=null&&(candidate.name.equalsIgnoreCase("Furnace")||candidate.name.equalsIgnoreCase("Brazier")))System.out.println("Forge candidate "+id+" name="+candidate.name+" size="+candidate.sizeX+"x"+candidate.sizeY+" types="+Arrays.toString(candidate.anIntArray776)+" models="+Arrays.toString(candidate.anIntArray773));}
  }
 }
}
