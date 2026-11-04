import java.awt.*;import java.awt.image.*;import java.io.*;import javax.imageio.ImageIO;
import com.koios.client.arpg.*;import com.koios.client.graphic.*;
/** Tests actual resource fill and final device-resolution presentation. */
public final class ManaOrbCheck {
 static int checks;static void check(boolean ok,String text){checks++;if(!ok)throw new AssertionError(text);}
 static int blue(Sprite s){int n=0;BufferedImage a=s.highResolutionArt;for(int y=15;y<98;y++)for(int x=15;x<98;x++){int c=a.getRGB(x,y),r=(c>>16)&255,g=(c>>8)&255,b=c&255;if(b>80&&b>r*1.6&&g>r*1.3)n++;}return n;}
 public static void main(String[] args)throws Exception{
  Sprite empty=ManaOrb.sprite(0,100),half=ManaOrb.sprite(50,100),full=ManaOrb.sprite(100,100);check(blue(empty)<blue(half)&&blue(half)<blue(full),"Liquid fill increases from empty through half to full");
  check(ManaOrb.sprite(-50,100)==empty&&ManaOrb.sprite(150,100)==full,"Resource values clamp to endpoints");for(double max:new double[]{0,-1,Double.NaN})check(ManaOrb.sprite(50,max)==empty,"Invalid maximum safely presents empty");
  check(ManaOrb.number(new String[]{"bad"},0,25)==25&&ManaOrb.number(new String[0],0,25)==25,"Missing/malformed state uses fallback");
  com.koios.client.graphic.ImageProducer surface=new com.koios.client.graphic.ImageProducer(64,64);java.util.Arrays.fill(surface.canvasRaster,0x141B22);half.drawSprite(18,18);BufferedImage crisp=HighDpiArtCheck.present(surface,2);
  BufferedImage legacy=new BufferedImage(128,128,BufferedImage.TYPE_INT_RGB);Graphics2D g=legacy.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);BufferedImage software=new BufferedImage(64,64,BufferedImage.TYPE_INT_RGB);software.setRGB(0,0,64,64,surface.canvasRaster,0,64);g.drawImage(software,0,0,128,128,null);g.dispose();int difference=0;for(int y=36;y<92;y++)for(int x=36;x<92;x++)if(crisp.getRGB(x,y)!=legacy.getRGB(x,y))difference++;
  BufferedImage preview=new BufferedImage(448,160,BufferedImage.TYPE_INT_RGB);g=preview.createGraphics();g.setColor(new Color(0x141B22));g.fillRect(0,0,448,160);g.drawImage(empty.highResolutionArt,0,15,null);g.drawImage(half.highResolutionArt,112,15,null);g.drawImage(full.highResolutionArt,224,15,null);g.drawImage(crisp,330,15,null);g.dispose();ImageIO.write(preview,"png",new File("build/mana-orb.png"));
  check(difference>100,"28px mana orb uses retained source detail at HiDPI, not enlarged software pixels (differences="+difference+")");DrawingArea.drawPixels(12,24,24,0xAF2560,12);BufferedImage covered=HighDpiArtCheck.present(surface,2);for(int y=48;y<72;y++)for(int x=48;x<72;x++)check((covered.getRGB(x,y)&0xFFFFFF)==0xAF2560,"28px art respects later overlay");HighDpiArt.invalidate(18,18,28,28);DrawingArea.drawPixels(28,18,18,0x141B22,28);BufferedImage removed=HighDpiArtCheck.present(surface,2);for(int y=36;y<92;y++)for(int x=36;x<92;x++)check((removed.getRGB(x,y)&0xFFFFFF)==0x141B22,"28px removed orb leaves no ghost");System.out.println("Passed "+checks+" mana-orb fill, state and device-presentation checks.");
 }
}


