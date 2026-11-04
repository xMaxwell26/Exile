import com.koios.client.arpg.*;
import com.koios.client.graphic.*;
import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

/** Pure production layout/render contracts. Does not construct an Applet or desktop. */
public final class HeadlessLayoutCheck {
    private static int checks;
    private static void check(boolean condition, String reason) {
        checks++;
        if (!condition) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        try (RandomAccessFile data = new RandomAccessFile("../koios-cache/main_file_cache.dat", "r");
             RandomAccessFile index = new RandomAccessFile("../koios-cache/main_file_cache.idx0", "r")) {
            TextDrawingArea font = new TextDrawingArea(false, "p12_full",
                    new StreamLoader(new Decompressor(data, index, 1).decompress(1)));
            // Include scale breakpoints, portrait displays and odd physical sizes.
            int[][] sizes = {{512,334},{800,600},{1024,650},{1280,720},{1499,900},{1500,900},
                    {1599,900},{1600,900},{1919,1080},{1920,1080},{2533,1407},
                    {2559,1440},{2560,1440},{3440,1440},{3840,2160},{1080,1920}};
            for (int[] size : sizes) {
                boolean fixed = size[0] == 512;
                double scale = ViewportScale.factor(size[0], size[1], fixed);
                int width = ViewportScale.logical(size[0], scale);
                int height = ViewportScale.logical(size[1], scale);
                check(scale >= 1 && width <= size[0] && height <= size[1], "Logical canvas fits physical display");
                check(ViewportScale.factor(size[0], size[1], true) == 1, "Fixed mode retains native coordinates");
                int left = SkillPanel.barOffset(width) + 12, top = SkillPanel.barTop(width,height);
                Rectangle bar = new Rectangle(left,top,392,78);
                check(new Rectangle(0,0,width,height).contains(bar), "Entire hotbar fits " + Arrays.toString(size));
                if (!fixed) check(!bar.intersects(new Rectangle(0,height-165,519,165)), "Hotbar clears chat " + width);
                for (int slot = 0; slot < 3; slot++) {
                    int x = left + 6 + slot * 127, y = top + 35;
                    for (int[] point : new int[][]{{x,y},{x+122,y+36},{x+61,y+18}}) {
                        int px = (int)Math.ceil(point[0]*scale), py = (int)Math.ceil(point[1]*scale);
                        check(SkillPanel.barSlotAt(ViewportScale.logical(px,scale),ViewportScale.logical(py,scale),width,height)==slot,
                                "Physical pointer resolves visible slot at scale " + scale);
                    }
                    check(SkillPanel.barSlotAt(x+123,y,width,height)==-1, "Slot right edge is exclusive");
                }
                check(SkillPanel.barSlotAt(left+10,top+34,width,height)==-1, "Resource text cannot cast");
                check(SkillPanel.barSlotAt(left+10,top+72,width,height)==-1, "Bottom border cannot cast");
                BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
                Arrays.fill(pixels,ExileTheme.BACKGROUND);
                DrawingArea.initDrawingArea(height,width,pixels);
                MapObjective objective = new MapObjective();
                objective.accept(Base64.getEncoder().encodeToString(("MAP OBJECTIVE / Thornwild Courtyard T10\n"
                        + "Defeat the Briar Warden\nBoss: north / 25 tiles / Enemies: 19\nPortal Scroll: open a return portal").getBytes("UTF-8")));
                Rectangle tracker = objective.bounds(font);
                check(new Rectangle(0,0,width,height).contains(tracker), "Objective remains visible");
                if (!fixed) {
                    check(tracker.y>=184, "Objective clears minimap");
                    check(!tracker.intersects(bar), "Objective clears skill bar");
                    check(!tracker.intersects(new Rectangle(0,height-165,519,165)), "Objective clears chat");
                }
                objective.draw(font);
                check(pixels[tracker.x+tracker.y*width]!=ExileTheme.BACKGROUND,"Tracker actually renders");
                int[] before=pixels.clone();
                String[] state=new String[26]; Arrays.fill(state,"1");
                state[0]="38";state[1]="50";state[14]="0";state[15]="1";state[16]="2";
                SkillPanel.setKeys(new String[]{"Ctrl+Alt+Shift+Mouse5","Unbound","Shift+M3"});
                SkillPanel.drawBar(font,state,new String[]{"Life: 1090 / 1090","Energy Shield: 250 / 250"});
                boolean spill=false;int painted=0;
                for(int y=0;y<height;y++)for(int x=0;x<width;x++)if(pixels[x+y*width]!=before[x+y*width]) {
                    painted++;if(!bar.contains(x,y))spill=true;
                }
                check(painted>20000&&!spill,"Actual hotbar text and long rebound labels fit their rectangle");
                SkillPanel.setKeys(new String[]{"Q","W","E"});
                if(size[0]==1920||size[0]==3840) ImageIO.write(image,"png",new File("build/layout-"+size[0]+".png"));
            }
            for(int tab:new int[]{2,5,6}) {
                check(ArpgTabs.action(tab,17,406,9,209)==null,"Button upper boundary excluded");
                check(ArpgTabs.action(tab,17,407,9,209)!=null,"Translated button coordinates work");
                check(ArpgTabs.action(tab,191,408,9,209)==null,"Button right boundary excluded");
            }
        }
        System.out.println("Passed "+checks+" headless fullscreen scale, hotbar, tracker and panel contracts.");
    }
}
