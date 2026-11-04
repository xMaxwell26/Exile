import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.DrawingArea;
import com.koios.client.graphic.TextDrawingArea;
import com.koios.client.graphic.RSFont;

/** Real proportional glyphs, clipping, later-panel occlusion, frame removal and device-scale text. */
public final class ExileTypographyCheck {
    static int checks;
    static void check(boolean value,String reason){checks++;if(!value)throw new AssertionError(reason);}
    static BufferedImage present(int[] pixels,int width,int height,int scale) {
        BufferedImage logical=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        logical.setRGB(0,0,width,height,pixels,0,width);
        BufferedImage result=new BufferedImage(width*scale,height*scale,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=result.createGraphics();g.scale(scale,scale);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(logical,0,0,null);ExileTypography.present(g,pixels,0,0);g.dispose();return result;
    }
    public static void main(String[] args)throws Exception {
        TextDrawingArea body=ExileTheme.body(),small=ExileTheme.small();
        check(body.isExile()&&small.isExile()&&ExileTheme.headingFont().isExile(),"Explicit AA fonts available");
        check(body.getTextWidth("WWW")>body.getTextWidth("iii")*2,"Humanist body is proportional");
        check(body.getTextWidth("A <col=ff0000>red</col> word")==body.getTextWidth("A red word"),"HTML colors do not change measurement");
        check(body.getTextWidth("@red@Hello")==body.getTextWidth("Hello"),"Legacy colors do not change measurement");
        check(RSFont.exileBody().getTextWidth("A <col=ff0000>red</col> word")==body.getTextWidth("A red word"),"RSFont adapter shares exact advances");
        int width=600,height=260,background=ExileTheme.PANEL;
        int[] pixels=new int[width*height];Arrays.fill(pixels,background);DrawingArea.initDrawingArea(height,width,pixels);
        ExileTheme.panel(8,8,584,244,ExileTheme.GOLD,background);
        ExileTheme.heading(28,42,"The Art of Exile",ExileTheme.HEADING);
        ExileTheme.label(body,28,68,"Forge your own path through a fallen world.",ExileTheme.TEXT);
        ExileTheme.label(small,28,90,"Small text: character levels, affixes and map modifiers.",ExileTheme.MUTED);
        RSFont.exileBody().drawBasicString("Life <col=c6846a>84 / 110</col>   Energy Shield 36",28,118,ExileTheme.TEXT,-1);
        ExileTheme.label(body,28,148,"Cleave  /  Sword and dagger",ExileTheme.TEXT);
        int painted=0,antialiased=0;
        for(int y=51;y<74;y++)for(int x=28;x<400;x++)if(pixels[x+y*width]!=background){painted++;if(pixels[x+y*width]!=ExileTheme.TEXT&&pixels[x+y*width]!=ExileTheme.SHADOW)antialiased++;}
        check(painted>100&&antialiased>50,"Body has antialiased coverage, not binary bitmap edges");
        BufferedImage one=present(pixels,width,height,1),two=present(pixels,width,height,2);
        int differences=0;
        for(int y=0;y<height*2;y++)for(int x=0;x<width*2;x++)if(two.getRGB(x,y)!=one.getRGB(x/2,y/2))differences++;
        check(differences>500,"Device scale presents fresh vector glyphs rather than enlarged bitmap text");
        DrawingArea.drawPixels(26,55,110,0x3B6658,180);
        BufferedImage covered=present(pixels,width,height,2);
        for(int y=110;y<162;y++)for(int x=220;x<580;x++)check((covered.getRGB(x,y)&0xFFFFFF)==0x3B6658,"Later panel stays above native text");
        ExileTypography.clear(pixels);Arrays.fill(pixels,background);
        BufferedImage empty=present(pixels,width,height,2);
        for(int y=0;y<height*2;y++)for(int x=0;x<width*2;x++)check((empty.getRGB(x,y)&0xFFFFFF)==background,"New frame removes old text without ghosts");
        DrawingArea.setDrawingArea(160,80,240,50);
        body.method389(false,40,ExileTheme.TEXT,"Clipped proportional typography",110);
        for(int y=0;y<height;y++)for(int x=0;x<width;x++)if(x<80||x>=240||y<50||y>=160)check(pixels[x+y*width]==background,"AA glyphs respect software clip");
        DrawingArea.initDrawingArea(height,width,pixels);
        String stable="Stable UI labels reuse cached glyphs and frame buffers.";
        for(int i=0;i<100;i++){ExileTypography.clear(pixels);Arrays.fill(pixels,background);body.method389(true,20,ExileTheme.TEXT,stable,90);}
        java.lang.management.ThreadMXBean base=java.lang.management.ManagementFactory.getThreadMXBean();
        if(base instanceof com.sun.management.ThreadMXBean){com.sun.management.ThreadMXBean bean=(com.sun.management.ThreadMXBean)base;if(bean.isThreadAllocatedMemorySupported()){bean.setThreadAllocatedMemoryEnabled(true);long id=Thread.currentThread().getId(),before=bean.getThreadAllocatedBytes(id);for(int i=0;i<100;i++){ExileTypography.clear(pixels);Arrays.fill(pixels,background);body.method389(true,20,ExileTheme.TEXT,stable,90);}System.out.println("Warm100 logical font frames allocated "+(bean.getThreadAllocatedBytes(id)-before)+" bytes (includes meter overhead)");}}
        // A centered local overlay must carry its native font records to the final world raster.
        int targetWidth=800,targetHeight=360,dx=80,dy=30;
        int[] target=new int[targetWidth*targetHeight];Arrays.fill(target,background);
        for(int row=0;row<height;row++)System.arraycopy(pixels,row*width,target,(row+dy)*targetWidth+dx,width);
        ExileTypography.copySurface(pixels,target,targetWidth,dx,dy);
        BufferedImage translated=present(target,targetWidth,targetHeight,2);
        int translatedDetail=0;
        for(int y=0;y<targetHeight*2;y++)for(int x=0;x<targetWidth*2;x++)if((translated.getRGB(x,y)&0xFFFFFF)!=target[x/2+y/2*targetWidth])translatedDetail++;
        check(translatedDetail>100,"Translated overlay retains fresh native glyph detail");
        ExileTypography.clear(target);Arrays.fill(target,background);
        BufferedImage removedOverlay=present(target,targetWidth,targetHeight,2);
        check((removedOverlay.getRGB(210,220)&0xFFFFFF)==background,"Closing translated overlay removes font records");
        int[] profilePixels=new int[600*540];Arrays.fill(profilePixels,background);
        DrawingArea.initDrawingArea(540,600,profilePixels);
        String[] profileLabels=new String[30];
        for(int i=0;i<30;i++){profileLabels[i]="Character property "+i+" / a readable proportional label";body.method389(true,20,ExileTheme.TEXT,profileLabels[i],25+i*16);}
        BufferedImage device=new BufferedImage(1200,1080,BufferedImage.TYPE_INT_RGB);
        Graphics2D deviceGraphics=device.createGraphics();deviceGraphics.scale(2,2);
        for(int i=0;i<100;i++)ExileTypography.present(deviceGraphics,profilePixels,0,0);
        long nativeStart=System.nanoTime(),nativeBytes=0;
        com.sun.management.ThreadMXBean nativeBean=base instanceof com.sun.management.ThreadMXBean?(com.sun.management.ThreadMXBean)base:null;
        long thread=Thread.currentThread().getId(),allocatedBefore=nativeBean==null?0:nativeBean.getThreadAllocatedBytes(thread);
        for(int i=0;i<100;i++)ExileTypography.present(deviceGraphics,profilePixels,0,0);
        if(nativeBean!=null)nativeBytes=nativeBean.getThreadAllocatedBytes(thread)-allocatedBefore;
        System.out.println("Warm100 native2x frames /30 labels: "+((System.nanoTime()-nativeStart)/1e6)+" ms, "+nativeBytes+" allocated bytes; diagnostic only");
        deviceGraphics.dispose();
        ImageIO.write(one,"png",new File("build/exile-typography-1x.png"));ImageIO.write(two,"png",new File("build/exile-typography-2x.png"));ImageIO.write(covered,"png",new File("build/exile-typography-occlusion.png"));
        System.out.println("Faces: "+ExileTypography.face(ExileTypography.Style.BODY).family()+" / "+ExileTypography.face(ExileTypography.Style.HEADING).family());
        System.out.println("Passed "+checks+" proportional typography/clip/occlusion/removal checks.");
    }
}
