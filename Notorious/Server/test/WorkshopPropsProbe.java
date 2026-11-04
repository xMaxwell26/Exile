import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;import com.koios.client.definition.ObjectDefinition;import com.koios.client.graphic.*;import com.koios.client.world.Model;
public class WorkshopPropsProbe {
 public static void main(String[] args)throws Exception{
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");RandomAccessFile models=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r")){
   ObjectDefinition.unpackConfig(new StreamLoader(new Decompressor(data,config,1).decompress(2)));Model.method459(0,null);Rasterizer.method368(new StreamLoader(new Decompressor(data,config,1).decompress(6)));Rasterizer.method372(.85);Rasterizer.method367();Decompressor cache=new Decompressor(data,models,2);Set<Integer> loaded=new HashSet<>();
   Set<String> targets=new HashSet<>(Arrays.asList("workbench","tool rack","weapon rack","armour stand","barrel"));Map<String,Integer> counts=new HashMap<>();java.util.List<Integer> ids=new ArrayList<>(Arrays.asList(2783,13704));
   for(int id=0;id<27283;id++){ObjectDefinition definition=ObjectDefinition.forID(id);String name=definition.name==null?"":definition.name.toLowerCase(java.util.Locale.ROOT);if(targets.contains(name)&&definition.anIntArray773!=null&&definition.anIntArray776==null&&counts.getOrDefault(name,0)<2){ids.add(id);counts.put(name,counts.getOrDefault(name,0)+1);}}
   int height=((ids.size()+3)/4)*250;BufferedImage preview=new BufferedImage(880,height,BufferedImage.TYPE_INT_RGB);Graphics2D graphics=preview.createGraphics();graphics.setColor(new Color(0x17232C));graphics.fillRect(0,0,880,height);graphics.setFont(new Font("SansSerif",Font.PLAIN,12));
   for(int index=0;index<ids.size();index++){int id=ids.get(index);ObjectDefinition definition=ObjectDefinition.forID(id);System.out.println(id+" name="+definition.name+" size="+definition.sizeX+"x"+definition.sizeY+" models="+Arrays.toString(definition.anIntArray773));for(int model:definition.anIntArray773)SceneryVisualCheck.load(cache,model&65535,loaded);Model mesh=definition.method578(10,0,0,0,0,0,-1);if(mesh==null)continue;graphics.drawImage(SceneryVisualCheck.render(mesh),(index%4)*220,(index/4)*250+40,null);graphics.setColor(new Color(0xD4BB87));graphics.drawString(id+" / "+definition.name,(index%4)*220+8,(index/4)*250+18);graphics.drawString(definition.sizeX+" x "+definition.sizeY,(index%4)*220+8,(index/4)*250+34);}
   graphics.dispose();ImageIO.write(preview,"png",new File("build/workshop-props.png"));
  }
 }
}
