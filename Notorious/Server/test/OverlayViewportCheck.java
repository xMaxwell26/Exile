import java.awt.image.*;
import java.awt.Graphics2D;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;

/** Real software compositing, clip restoration and device-art translation at fixed/resized viewports. */
public final class OverlayViewportCheck {
 private static int checks;
 private static void check(boolean value,String reason){checks++;if(!value)throw new AssertionError(reason);}
 private static int[] state(){return new int[]{DrawingArea.width,DrawingArea.height,DrawingArea.topX,DrawingArea.topY,DrawingArea.bottomX,DrawingArea.bottomY,DrawingArea.centerX,DrawingArea.centerY,DrawingArea.anInt1387};}
 private static Sprite icon(){Sprite sprite=new Sprite(16,16);for(int y=0;y<16;y++)for(int x=0;x<16;x++)sprite.myPixels[y*16+x]=(x+y)%2==0?0xB44F39:0xE4BD71;sprite.highResolutionArt=new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);Graphics2D g=sprite.highResolutionArt.createGraphics();g.setColor(java.awt.Color.ORANGE);g.fillOval(0,0,64,64);g.dispose();return sprite;}
 public static void main(String[]args)throws Exception{
  for(int[] size:new int[][]{{512,334},{765,503},{1280,720},{1920,1080}}){
   int width=size[0],height=size[1];boolean fixed=width==512;
   int[] pixels=new int[width*height];Arrays.fill(pixels,0x172C31);DrawingArea.initDrawingArea(height,width,pixels);
   DrawingArea.setDrawingArea(height-7,3,width-5,4);DrawingArea.centerX=19;DrawingArea.centerY=23;DrawingArea.anInt1387=29;
   int[] saved=state();Sprite icon=icon();
   OverlayViewport.draw(width,height,fixed,()->{check(DrawingArea.width==512&&DrawingArea.height==334,"Painter always sees logical512x334");DrawingArea.drawPixels(334,0,0,0x33181E,512);icon.drawSprite(40,50);ExileTheme.heading(70,68,"Exile",ExileTheme.GOLD);});
   check(DrawingArea.pixels==pixels&&Arrays.equals(saved,state()),"Drawing raster/clip/centers restored exactly");
   int dx=OverlayViewport.originX(width,height,fixed),dy=OverlayViewport.originY(width,height,fixed);
   check(dx==(fixed?0:Math.max(0,(width-762)/2))&&dy==(fixed?0:Math.max(0,(height-499)/2)),"Origin centers in world excluding sidebar/chat");
   for(int y=0;y<height;y++)for(int x=0;x<width;x++){
    boolean inside=x>=dx+16&&x<dx+496&&y>=dy+16&&y<dy+322&&x>=saved[2]&&x<saved[4]&&y>=saved[3]&&y<saved[5];
    check(inside||pixels[y*width+x]==0x172C31,"No modal pixels outside composite crop or destination clip");
   }
   check(pixels[(dy+50)*width+dx+40]==0xB44F39,"Icon software pixels translated with overlay");
   Field surfaces=HighDpiArt.class.getDeclaredField("SURFACES");surfaces.setAccessible(true);Map<?,?> target=(Map<?,?>)((Map<?,?>)surfaces.get(null)).get(pixels);check(target!=null&&target.containsKey(((long)(dx+40)<<32)|(dy+50)),"HighDPI art stamp transfers to destination coordinate");
   BufferedImage logical=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);logical.setRGB(0,0,width,height,pixels,0,width);
   BufferedImage sharp=new BufferedImage(width*2,height*2,BufferedImage.TYPE_INT_RGB);Graphics2D graphics=sharp.createGraphics();graphics.scale(2,2);graphics.drawImage(logical,0,0,null);HighDpiArt.draw(graphics,pixels,0,0);ExileTypography.present(graphics,pixels,0,0);graphics.dispose();
   int changes=0;for(int yy=(dy+50)*2;yy<(dy+66)*2;yy++)for(int xx=(dx+40)*2;xx<(dx+56)*2;xx++)if((sharp.getRGB(xx,yy)&0xFFFFFF)!=pixels[(yy/2)*width+xx/2])changes++;
   check(changes>100,"Translated item art still presents smoothly at native2x");
   DrawingArea.drawPixels(8,dy+54,dx+44,0x663D7A,8);BufferedImage covered=new BufferedImage(width*2,height*2,BufferedImage.TYPE_INT_RGB);Graphics2D overlay=covered.createGraphics();overlay.scale(2,2);logical.setRGB(0,0,width,height,pixels,0,width);overlay.drawImage(logical,0,0,null);HighDpiArt.draw(overlay,pixels,0,0);ExileTypography.present(overlay,pixels,0,0);overlay.dispose();
   for(int yy=(dy+54)*2;yy<(dy+62)*2;yy++)for(int xx=(dx+44)*2;xx<(dx+52)*2;xx++)check((covered.getRGB(xx,yy)&0xFFFFFF)==0x663D7A,"A later overlay still occludes transferred native art");
   int[] before=pixels.clone();try{OverlayViewport.draw(width,height,fixed,()->{DrawingArea.drawPixels(334,0,0,0xFFFFFF,512);throw new IllegalStateException("test painter");});throw new AssertionError("Expected painter failure");}catch(IllegalStateException expected){check(expected.getMessage().equals("test painter"),"Painter failure propagates");}
   check(DrawingArea.pixels==pixels&&Arrays.equals(saved,state())&&Arrays.equals(before,pixels),"Failed painter leaves destination and drawing context untouched");
   OverlayViewport.draw(width,height,fixed,()->DrawingArea.drawPixels(334,0,0,0x24171D,512));check(Arrays.equals(saved,state()),"Painter guard resets after failure");
   BufferedImage preview=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);preview.setRGB(0,0,width,height,pixels,0,width);ImageIO.write(preview,"png",new File("build/overlay-"+width+".png"));
  }
  check(OverlayViewport.originX(300,200,false)==0&&OverlayViewport.originY(300,200,false)==0,"Small windows clamp origin instead of negative placements");
  System.out.println("Passed "+checks+" responsive modal crop/restore/failure/art checks.");
 }
}
