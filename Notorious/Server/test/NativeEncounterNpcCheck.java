import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;import com.koios.client.definition.NPCDefinition;import com.koios.client.arpg.CustomNpcs;import com.koios.client.graphic.Rasterizer;import com.koios.client.world.Model;import com.koios.game.arpg.EncounterNpcs;import com.koios.game.def.MobDefinition;
/** Actual cache rigs, themed aliases, palette isolation and client/server footprint agreement. */
public final class NativeEncounterNpcCheck {
 static int checks;static void check(boolean ok,String text){checks++;if(!ok)throw new AssertionError(text);}
 static int[] palette(Model m){return m.anIntArray1640==null?null:m.anIntArray1640.clone();}
 public static void main(String[] args)throws Exception{
  PrototypeIntegrationCheck.loaded();try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");RandomAccessFile models=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r")){
   StreamLoader definitions=new StreamLoader(new Decompressor(data,config,1).decompress(2));NPCDefinition.unpackConfig(definitions);Model.method459(0,null);Rasterizer.method368(new StreamLoader(new Decompressor(data,config,1).decompress(6)));Rasterizer.method372(.85);Rasterizer.method367();Decompressor cache=new Decompressor(data,models,2);Set<Integer> loaded=new HashSet<>();
   BufferedImage preview=new BufferedImage(880,960,BufferedImage.TYPE_INT_RGB);Graphics2D g=preview.createGraphics();g.setColor(new Color(0x17232C));g.fillRect(0,0,880,960);g.setFont(new Font("SansSerif",Font.PLAIN,12));
   for(int id=15000;id<=15013;id++){
    int template=CustomNpcs.template(id);check(template==EncounterNpcs.template(id),"Client/server template agreement "+id);NPCDefinition base=NPCDefinition.forID(template);String baseName=base.name;int[] baseModels=base.npcModels.clone();for(int model:baseModels)SceneryVisualCheck.load(cache,model&65535,loaded);
    Model raw=Model.method462(baseModels[0]&65535);int[] before=palette(raw);NPCDefinition alias=NPCDefinition.forID(id);check(alias.name.equals(EncounterNpcs.name(id)),"Client/server alias name agreement "+id);check(alias.interfaceType==id,"Alias has isolated cache key "+id);check(alias.npcSizeInSquares==MobDefinition.get(id).getSize(),"Client/server footprint agreement "+id);check(Arrays.equals(alias.npcModels,baseModels),"Alias retains proven rig "+id);
    Model actual=alias.method164(-1,-1,null);check(actual!=null&&actual.anInt1626>0&&actual.anInt1630>0,"Alias produces populated actual mesh "+id);check(actual.anIntArray1634!=null&&actual.anIntArray1634.length>0,"Alias produces shaded faces "+id);check(Arrays.equals(before,palette(Model.method462(baseModels[0]&65535))),"Themed palette does not mutate raw shared cache model "+id);
    int index=id-15000;g.drawImage(SceneryVisualCheck.render(actual),(index%4)*220,(index/4)*240+30,null);g.setColor(new Color(0xD4BB87));g.drawString(id+" / "+alias.name,(index%4)*220+8,(index/4)*240+18);NPCDefinition again=NPCDefinition.forID(template);check(again.name.equals(baseName)&&Arrays.equals(again.npcModels,baseModels),"Base definition remains unchanged "+id);
   }
   g.dispose();ImageIO.write(preview,"png",new File("build/encounter-npcs.png"));System.out.println("Passed "+checks+" themed NPC cache/rig/footprint isolation checks. Geometry reuses existing cache rigs.");
  }
 }
}
