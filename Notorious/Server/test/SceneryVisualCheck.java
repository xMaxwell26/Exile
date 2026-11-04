import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import java.util.zip.GZIPInputStream;import javax.imageio.ImageIO;
import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;import com.koios.client.definition.ObjectDefinition;import com.koios.client.graphic.*;import com.koios.client.world.Model;

/** Collision is insufficient: the actual client object definition must support its placement type. */
public class SceneryVisualCheck {
 static int checks;static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
 static void load(Decompressor cache,int id,Set<Integer> loaded)throws Exception{
  if(!loaded.add(id))return;byte[] packed=cache.decompress(id);check(packed!=null,"Scenery model exists in cache: "+id);
  try(GZIPInputStream zip=new GZIPInputStream(new ByteArrayInputStream(packed));ByteArrayOutputStream bytes=new ByteArrayOutputStream()){byte[] buffer=new byte[8192];int read;while((read=zip.read(buffer))!=-1)bytes.write(buffer,0,read);Model.method460(bytes.toByteArray(),id);}
 }
 static BufferedImage render(Model original){
  Model model=new Model(original.anIntArray1640==null,false,false,original);int minX=99999,maxX=-99999,minY=99999,maxY=-99999,minZ=99999,maxZ=-99999;
  for(int i=0;i<model.anInt1626;i++){minX=Math.min(minX,model.anIntArray1627[i]);maxX=Math.max(maxX,model.anIntArray1627[i]);minY=Math.min(minY,model.anIntArray1628[i]);maxY=Math.max(maxY,model.anIntArray1628[i]);minZ=Math.min(minZ,model.anIntArray1629[i]);maxZ=Math.max(maxZ,model.anIntArray1629[i]);}
  System.out.println("Render bounds "+(maxX-minX)+"x"+(maxY-minY)+"x"+(maxZ-minZ));
  int distance=Math.max(400,Math.max(maxX-minX,Math.max(maxY-minY,maxZ-minZ))*4);
  for(int i=0;i<model.anInt1626;i++){model.anIntArray1627[i]-=(minX+maxX)/2;model.anIntArray1628[i]-=(minY+maxY)/2;model.anIntArray1629[i]-=(minZ+maxZ)/2;}
  if(model.anIntArray1640!=null)model.method479(84,768,-50,-10,-50,true);else{model.anIntArray1634=original.anIntArray1634.clone();model.anIntArray1635=original.anIntArray1635.clone();model.anIntArray1636=original.anIntArray1636.clone();model.method466();}
  BufferedImage image=new BufferedImage(220,200,BufferedImage.TYPE_INT_RGB);int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();Arrays.fill(pixels,0x17232C);DrawingArea.initDrawingArea(200,220,pixels);Rasterizer.method364();int pitch=180;model.method482(256,0,pitch,0,Rasterizer.SINE[pitch]*distance>>16,Rasterizer.COSINE[pitch]*distance>>16);
  int visible=0;for(int color:pixels)if(color!=0x17232C)visible++;check(visible>100,"Scenery is actually visible in native rendering; pixels="+visible+" vertices="+model.anInt1626+" faces="+model.anInt1630);return image;
 }
 public static void main(String[] args)throws Exception{
  PrototypeIntegrationCheck.loaded();
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");RandomAccessFile models=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r")){
   ObjectDefinition.unpackConfig(new StreamLoader(new Decompressor(data,config,1).decompress(2)));Model.method459(0,null);Rasterizer.method368(new StreamLoader(new Decompressor(data,config,1).decompress(6)));Rasterizer.method372(.85);Rasterizer.method367();Decompressor cache=new Decompressor(data,models,2);Set<Integer> loaded=new HashSet<>();
   int[][] placements={{158,10},{2068,0},{357,10},{574,10},{1583,0},{1584,0},{1586,0},{1587,0},{2341,0},{2606,0},{2629,0},{3077,0},{3078,0},{3079,0},{3082,0},{3083,0},{3626,0}};int previewHeight=((placements.length+3)/4)*240;BufferedImage preview=new BufferedImage(880,previewHeight,BufferedImage.TYPE_INT_RGB);Graphics2D graphics=preview.createGraphics();graphics.setColor(new Color(0x17232C));graphics.fillRect(0,0,880,previewHeight);graphics.setFont(new Font("SansSerif",Font.PLAIN,12));
   for(int index=0;index<placements.length;index++){
    int id=placements[index][0],type=placements[index][1];ObjectDefinition definition=ObjectDefinition.forID(id);check(definition.anIntArray773!=null,"Scenery definition has model IDs: "+id);for(int model:definition.anIntArray773)load(cache,model&65535,loaded);
    for(int rotation=0;rotation<4;rotation++){Model model=definition.method578(type,rotation,0,0,0,0,-1);check(model!=null,"Client supports object "+id+" placement type "+type+" rotation "+rotation);check(model.anInt1626>0&&model.anInt1630>0,"Scenery mesh is populated");if(rotation==0)graphics.drawImage(render(model),(index%4)*220,(index/4)*240+30,null);}
    graphics.setColor(new Color(0xD4BB87));graphics.drawString(id+" / "+definition.name+" / type "+type,(index%4)*220+8,(index/4)*240+18);System.out.println("Scenery "+id+" name="+definition.name+" supportedTypes="+Arrays.toString(definition.anIntArray776)+" models="+Arrays.toString(definition.anIntArray773));
   }
   check(ObjectDefinition.forID(158).method578(0,0,0,0,0,0,-1)==null,"Regression: object158 type0 is invisible even though collision can exist");
   graphics.dispose();ImageIO.write(preview,"png",new File("build/scenery-models.png"));
   for(int id:new int[]{159,1583,1584,1586,1587,2341,2606,2629,3077,3078,3079,3082,3083,3626}){ObjectDefinition candidate=ObjectDefinition.forID(id);if(candidate.anIntArray776!=null)for(int type:candidate.anIntArray776)if(type==0){System.out.println("True wall candidate "+id+" name="+candidate.name+" models="+Arrays.toString(candidate.anIntArray773));break;}}
   Set<String> actualPlacements=new HashSet<>();
   for(int layout=1;layout<=2;layout++)try(com.koios.game.arpg.RegionInstance region=new com.koios.game.arpg.RegionInstance(3222,3218,true)){
    com.koios.game.arpg.AuthoredMaps.build(region,layout);
    for(com.koios.game.map.RSObject object:region.scenery){
     String key=object.getId()+":"+object.getType()+":"+object.getFace();if(!actualPlacements.add(key))continue;
     ObjectDefinition definition=ObjectDefinition.forID(object.getId());check(definition.anIntArray773!=null,"Authored scenery has actual client assets: "+key);for(int model:definition.anIntArray773)load(cache,model&65535,loaded);
     check(definition.method578(object.getType(),object.getFace(),0,0,0,0,-1)!=null,"Actual authored map placement is visible to the client: "+key);
    }
   }
   System.out.println("Passed "+checks+" actual client scenery/model-type checks, including "+actualPlacements.size()+" production placement variants.");
  }
 }
}

