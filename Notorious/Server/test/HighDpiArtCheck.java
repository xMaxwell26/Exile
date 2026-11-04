import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;

/** Check the final device-resolution composite, including overlays and removed items. */
public class HighDpiArtCheck {
    static int checks;
    static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    static BufferedImage present(com.koios.client.graphic.ImageProducer surface,double scale){
        BufferedImage output=new BufferedImage((int)(surface.canvasWidth*scale),(int)(surface.canvasHeight*scale),BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics=output.createGraphics();graphics.scale(scale,scale);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        surface.drawGraphics(0,graphics,0);graphics.dispose();return output;
    }
    public static void main(String[] args)throws Exception{
        Sprite icon=ArtSprites.icon(22424);
        check(icon!=null&&icon.highResolutionArt!=null,"Approved source art is retained at high resolution");
        com.koios.client.graphic.ImageProducer surface=new com.koios.client.graphic.ImageProducer(96,80);
        Arrays.fill(surface.canvasRaster,0x141B22);icon.drawSprite(20,12);
        BufferedImage crisp=present(surface,2);
        BufferedImage software=new BufferedImage(surface.canvasWidth,surface.canvasHeight,BufferedImage.TYPE_INT_RGB);
        software.setRGB(0,0,surface.canvasWidth,surface.canvasHeight,surface.canvasRaster,0,surface.canvasWidth);
        BufferedImage legacy=new BufferedImage(192,160,BufferedImage.TYPE_INT_RGB);Graphics2D oldGraphics=legacy.createGraphics();oldGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);oldGraphics.drawImage(software,0,0,192,160,null);oldGraphics.dispose();
        int differences=0;for(int y=24;y<88;y++)for(int x=40;x<104;x++)if(crisp.getRGB(x,y)!=legacy.getRGB(x,y))differences++;
        check(differences>200,"Fullscreen presentation uses source detail instead of enlarged 32px pixels");
        DrawingArea.drawPixels(24,12,34,0xAF2560,24);
        BufferedImage occluded=present(surface,2);
        for(int y=24;y<72;y++)for(int x=68;x<116;x++)check((occluded.getRGB(x,y)&0xFFFFFF)==0xAF2560,"Tooltip/panel pixels remain above the item art");
        HighDpiArt.invalidate(18,10,36,36);DrawingArea.drawPixels(36,10,18,0x141B22,36);
        BufferedImage removed=present(surface,2);
        for(int y=20;y<92;y++)for(int x=36;x<108;x++)check((removed.getRGB(x,y)&0xFFFFFF)==0x141B22,"Removing a slot item cannot leave high-resolution ghost pixels");
        BufferedImage preview=new BufferedImage(600,210,BufferedImage.TYPE_INT_RGB);Graphics2D graphics=preview.createGraphics();graphics.setColor(new Color(0x141B22));graphics.fillRect(0,0,600,210);graphics.setColor(new Color(0xDDC58C));graphics.setFont(new Font("SansSerif",Font.PLAIN,13));graphics.drawString("Enlarged 32px",12,20);graphics.drawString("Original art at 2x",212,20);graphics.drawString("Occlusion preserved",412,20);graphics.drawImage(legacy,4,30,null);graphics.drawImage(crisp,204,30,null);graphics.drawImage(occluded,404,30,null);graphics.dispose();ImageIO.write(preview,"png",new File("build/high-dpi-items.png"));
        System.out.println("Passed "+checks+" high-DPI presentation, occlusion and removal checks.");
    }
}
