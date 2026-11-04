import java.awt.*;import java.awt.image.*;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.arpg.*;import com.koios.client.graphic.*;import com.koios.client.cache.Decompressor;import com.koios.client.stream.StreamLoader;
/** Server/client identity and actual retained-source currency presentation. */
public final class CurrencyArtCheck {
 static int checks;static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
 public static void main(String[] args)throws Exception{
  com.koios.game.def.ItemDefinition.load();
  int[] ids={22400,22401,22402,22403,22404,22405,22406,22407,22408,22409,22415,22416};
  BufferedImage atlas=ImageIO.read(new File("../koios-cache/arpg-art/crafting-expansion-v1.png"));check(atlas!=null&&atlas.getColorModel().hasAlpha(),"Original expansion atlas retains alpha");
  int cellW=atlas.getWidth()/3,cellH=atlas.getHeight()/2;for(int c=0;c<5;c++){int solid=0;for(int y=0;y<cellH;y++)for(int x=0;x<cellW;x++)if((atlas.getRGB(c%3*cellW+x,c/3*cellH+y)>>>24)>=220)solid++;check(solid>10000,"Illustrated currency silhouette "+c);for(int[] xy:new int[][]{{1,1},{cellW-2,1},{1,cellH-2},{cellW-2,cellH-2}})check((atlas.getRGB(c%3*cellW+xy[0],c/3*cellH+xy[1])>>>24)<80,"Transparent cell gutter "+c);}
  try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile index=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r")){
   com.koios.client.definition.ItemDefinition.unpackConfig(new StreamLoader(new Decompressor(data,index,1).decompress(2)));
   TextDrawingArea font=new TextDrawingArea(false,"p12_full",new StreamLoader(new Decompressor(data,index,1).decompress(1)));
   com.koios.client.graphic.ImageProducer surface=new com.koios.client.graphic.ImageProducer(520,150);Arrays.fill(surface.canvasRaster,0x141B22);Set<Integer> hashes=new HashSet<>();
   for(int n=0;n<ids.length;n++){int id=ids[n];com.koios.client.definition.ItemDefinition def=com.koios.client.definition.ItemDefinition.forID(id);check(def.id==id&&def.name.equals(com.koios.game.def.ItemDefinition.forId(id).getName()),"Client/server exact currency identity "+id);check(def.stackable&&def.itemActions[4].equals("Drop"),"Usable stackable inventory currency "+id);check(id==22406?"Read".equals(def.itemActions[0]):def.itemActions[0]==null,"Only portal scroll exposes Read");Sprite icon=CurrencyIcons.sprite(id);check(icon!=null&&icon.highResolutionArt!=null,"Retained source icon resolves "+id);check(hashes.add(Arrays.hashCode(icon.myPixels)),"No aliased currency illustration "+id);check(icon.myPixels[0]==0&&icon.myPixels[1023]==0,"Transparent native corners "+id);icon.drawSprite(16+n%6*84,12+n/6*72);font.method389(true,16+n%6*84,0xB9C4CC,Integer.toString(id),65+n/6*72);}
   BufferedImage normal=HighDpiArtCheck.present(surface,1),crisp=HighDpiArtCheck.present(surface,2);int changed=0;for(int y=0;y<crisp.getHeight();y++)for(int x=0;x<crisp.getWidth();x++)if(crisp.getRGB(x,y)!=normal.getRGB(x/2,y/2))changed++;check(changed>3000,"Device-resolution presentation uses original source detail");ImageIO.write(crisp,"png",new File("build/currency-expansion-icons.png"));
  }
  System.out.println("Passed "+checks+" currency identity, alpha, distinctness and device-source checks.");
 }
}
