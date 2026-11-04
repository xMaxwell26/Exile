import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.game.arpg.*;import com.koios.game.map.MapRegion;import com.koios.game.map.RSObject;import com.koios.game.model.Location;
import com.koios.client.CollisionMap;import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;import com.koios.client.definition.*;import com.koios.client.graphic.*;import com.koios.client.world.*;

/** Verify real scenery footprints, room access and the actual native scene presentation. */
public class CourtyardEnvironmentCheck {
 static int checks;static void check(boolean value,String label){checks++;if(!value)throw new AssertionError(label);}
 public static void main(String[] args)throws Exception{
  PrototypeIntegrationCheck.loaded();
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");RandomAccessFile models=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r");RegionInstance region=new RegionInstance(3222,3218,true)){
   WorldController.lowMem=false;Rasterizer.lowMem=false;ObjectDefinition.lowMem=false;
   StreamLoader archive=new StreamLoader(new Decompressor(data,config,1).decompress(2));ObjectDefinition.unpackConfig(archive);FloorDefinition.unpackConfig(archive);Model.method459(0,null);Rasterizer.method368(new StreamLoader(new Decompressor(data,config,1).decompress(6)));Rasterizer.method372(.85);Rasterizer.method367();Decompressor modelCache=new Decompressor(data,models,2);Set<Integer> loaded=new HashSet<>();
   Location spawn=AuthoredMaps.build(region,2);check(spawn.getX()==region.baseX+52&&spawn.getY()==region.baseY+38,"Courtyard arrival anchor preserved");Set<Long> reachable=new HashSet<>();for(Location cell:MapRuns.reachable(spawn,70))reachable.add(((long)cell.getX()<<32)|(cell.getY()&0xffffffffL));
   for(int[] point:new int[][]{{38,43},{66,43},{38,54},{66,54},{38,60},{66,60},{39,66},{65,66},{52,67}})check(reachable.contains(((long)(region.baseX+point[0])<<32)|((region.baseY+point[1])&0xffffffffL)),"Workshop/assembly/foreman destination remains reachable");
   for(int x:new int[]{44,60})for(int y:new int[]{52,53,57,58})check(MapRegion.getClipping(region.baseX+x,region.baseY+y,0)==0,"Garden crossings remain two tiles wide");
   for(int x:new int[]{38,39,66,67})check(MapRegion.getClipping(region.baseX+x,region.baseY+61,0)==0,"Northern garden exits remain two tiles wide");
   for(int x=48;x<=56;x++)for(int y=34;y<=70;y++)check(MapRegion.getClipping(region.baseX+x,region.baseY+y,0)==0,"Full central nave remains clear for movement and ranged attacks");
   for(int x=49;x<=55;x++)for(int y=63;y<=70;y++)check(MapRegion.getClipping(region.baseX+x,region.baseY+y,0)==0,"Boss footprint and close dodge space remain clear");
   int[][][] heights=new int[4][105][105];byte[][][] settings=new byte[4][104][104];ObjectManager manager=new ObjectManager(settings,heights);manager.applyArpgArena(2);CollisionMap[] collision={new CollisionMap(),new CollisionMap(),new CollisionMap(),new CollisionMap()};WorldController scene=new WorldController(heights);ObjectManager.lowMem=false;manager.method171(collision,scene);scene.method275(0);
   for(RSObject object:region.scenery){ObjectDefinition definition=ObjectDefinition.forID(object.getId());for(int model:definition.anIntArray773)SceneryVisualCheck.load(modelCache,model&65535,loaded);check(definition.method578(object.getType(),object.getFace(),0,0,0,0,-1)!=null,"Production furnishing supplies a real client mesh");int width=definition.sizeX,height=definition.sizeY;if((object.getFace()&1)!=0){int swap=width;width=height;height=swap;}if(object.getType()==10)for(int x=0;x<width;x++)for(int y=0;y<height;y++)check(MapRegion.getClipping(object.getX()+x,object.getY()+y,0)!=0,"Entire actual client furnishing footprint is blocked");ObjectManager.method188(scene,object.getFace(),object.getY()-region.baseY,object.getType(),0,collision[0],heights,object.getX()-region.baseX,object.getId(),0);}
   int width=960,height=640;int[] visibility=new int[9];for(int i=0;i<9;i++){int angle=128+i*32+15,distance=600+angle*3;visibility[i]=distance*Rasterizer.SINE[angle]>>16;}WorldController.method310(500,800,width,height,visibility);WorldController.viewDistance=9;
   BufferedImage sheet=new BufferedImage(width*2,height,BufferedImage.TYPE_INT_RGB);Graphics2D result=sheet.createGraphics();
   for(int view=0;view<2;view++){BufferedImage frame=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);int[] pixels=((DataBufferInt)frame.getRaster().getDataBuffer()).getData();Arrays.fill(pixels,0x182029);DrawingArea.initDrawingArea(height,width,pixels);Rasterizer.method364();scene.method313(52*128,(view==0?34:52)*128,0,-1900,0,320);scene.clearObj5Cache();int visible=0;for(int pixel:pixels)if(pixel!=0x182029)visible++;check(visible>width*height/3,"Native Courtyard scene fills the viewport");result.drawImage(frame,view*width,0,null);}
   result.dispose();ImageIO.write(sheet,"png",new File("build/courtyard-environment.png"));System.out.println("Passed "+checks+" Courtyard loop, dodge-space, real footprint and native scene checks.");
  }
 }
}
