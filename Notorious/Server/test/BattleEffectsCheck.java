import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.BattleEffects;
import com.koios.client.graphic.DrawingArea;

/** Render the real effects into a wide software viewport, including its right-hand side. */
public final class BattleEffectsCheck {
 static int checks;
 static final int BACKGROUND=0x131E28,WIDTH=1280,HEIGHT=360,ORIGIN=100*128+64;
 static void check(boolean value,String label){checks++;if(!value)throw new AssertionError(label);}
 static BufferedImage canvas(){BufferedImage image=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();Arrays.fill(pixels,BACKGROUND);DrawingArea.initDrawingArea(HEIGHT,WIDTH,pixels);return image;}
 static BattleEffects.Projection rightHandProjection(){return (x,y,height)->new int[]{1040+(x-ORIGIN)/2,180+(y-ORIGIN)/4-height/8};}
 static int painted(BufferedImage image){int result=0;for(int color:((DataBufferInt)image.getRaster().getDataBuffer()).getData())if(color!=BACKGROUND)result++;return result;}
 static String payload(int kind,int target,boolean critical){return kind+"|100|100|"+target+"|100|12|"+(critical?1:0);}
 public static void main(String[] args)throws Exception{
  String[] labels={"Weapon hit","Cleave sweep","Whirlwind ring","Ground Slam","Execute","Impact spark","Fire projectile","Cold projectile","Boss slam"};
  BufferedImage sheet=new BufferedImage(1140,840,BufferedImage.TYPE_INT_RGB);Graphics2D graphic=sheet.createGraphics();graphic.setColor(new Color(BACKGROUND));graphic.fillRect(0,0,sheet.getWidth(),sheet.getHeight());graphic.setFont(new Font("SansSerif",Font.PLAIN,13));
  for(int kind=0;kind<9;kind++){
   BufferedImage image=canvas();BattleEffects effects=new BattleEffects();effects.accept(payload(kind,102,true));effects.draw(rightHandProjection(),System.currentTimeMillis()+250);check(painted(image)>20,"Effect "+kind+" draws visible pixels");int right=0;for(int y=0;y<HEIGHT;y++)for(int x=HEIGHT+1;x<WIDTH;x++)if(image.getRGB(x,y)!=(BACKGROUND|0xFF000000))right++;check(right>20,"Effect "+kind+" renders beyond x=viewport height (landscape clipping regression)");
   int x=(kind%3)*380,y=(kind/3)*280;graphic.drawImage(image,x,y+22,x+380,y+280,900,50,1280,308,null);graphic.setColor(new Color(0xDDD0AC));graphic.drawString(kind+" / "+labels[kind],x+10,y+17);
   BufferedImage expired=canvas();effects.draw(rightHandProjection(),System.currentTimeMillis()+1000);check(painted(expired)==0,"Expired effect "+kind+" leaves no pixels");final int[] calls={0};effects.draw((a,b,h)->{calls[0]++;return new int[]{1000,180};},System.currentTimeMillis());check(calls[0]==0,"Expired effect "+kind+" is removed, not hidden temporarily");
  }
  graphic.dispose();ImageIO.write(sheet,"png",new File("build/battle-effects.png"));
  BufferedImage clipped=canvas();DrawingArea.setDrawingArea(260,970,1170,100);for(int kind=0;kind<9;kind++){BattleEffects effects=new BattleEffects();effects.accept(payload(kind,102,true));effects.draw(rightHandProjection(),System.currentTimeMillis()+250);}int inside=0;for(int y=0;y<HEIGHT;y++)for(int x=0;x<WIDTH;x++)if(clipped.getRGB(x,y)!=(BACKGROUND|0xFF000000)){inside++;check(x>=970&&x<1170&&y>=100&&y<260,"Effects obey active viewport clip bounds");}check(inside>0,"Clipped viewport retains the visible portion");
  canvas();BattleEffects malformed=new BattleEffects();for(String input:new String[]{"","garbage","0|1|2","9|100|100|102|100|1|0","-1|100|100|102|100|1|0","x|100|100|102|100|1|0","0|x|100|102|100|1|0","0|100|100|102|100|1|x"})malformed.accept(input);final int[] malformedCalls={0};malformed.draw((a,b,h)->{malformedCalls[0]++;return new int[]{1000,180};},System.currentTimeMillis());check(malformedCalls[0]==0,"Malformed payloads enqueue no effects");
  canvas();BattleEffects bounded=new BattleEffects();for(int i=0;i<40;i++)bounded.accept(payload(5,i,false));final Set<Integer> projected=new HashSet<>();bounded.draw((x,y,h)->{projected.add(x);return new int[]{1000,180};},System.currentTimeMillis());check(projected.size()==32,"Queue draws only the newest 32 effects");check(!projected.contains(64)&&projected.contains(39*128+64),"Queue evicts oldest effects and retains newest");bounded.clear();final int[] cleared={0};bounded.draw((x,y,h)->{cleared[0]++;return new int[]{1000,180};},System.currentTimeMillis());check(cleared[0]==0,"Clear removes all pending effects");
  BufferedImage hidden=canvas();BattleEffects offscreen=new BattleEffects();for(int kind=0;kind<9;kind++)offscreen.accept(payload(kind,102,false));offscreen.draw((x,y,h)->new int[]{-1,-1},System.currentTimeMillis()+250);check(painted(hidden)==0,"Offscreen projections do not draw or overflow");
  System.out.println("Passed "+checks+" battle-effect rendering, clipping, expiry, malformed-payload and queue checks.");
 }
}
