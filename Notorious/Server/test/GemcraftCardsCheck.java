import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;
import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;

/** Exact gemcraft action targets and native-font bounds, plus existing-mode isolation. */
public final class GemcraftCardsCheck {
    private static int checks;
    private static void check(boolean value,String reason){checks++;if(!value)throw new AssertionError(reason);}
    private static String[] payload(boolean support) {
        ArrayList<String> lines=new ArrayList<>();
        lines.add("gemcraft");
        lines.add("\tUNCUT "+(support?"SUPPORT":"SKILL")+" JEWEL / LEVEL 7");
        lines.add("\tChoose its permanent identity. Jewel level and identity are preserved.");
        String[] filters={"all","sword","dagger","mace","support"};
        for(String filter:filters)lines.add("gemfilter "+filter+"\t"+((support?filter.equals("support"):filter.equals("all"))?"["+filter+"]":filter));
        String[] names=support?new String[]{"Splash","Faster Attacks","Brutality"}:SkillPanel.NAMES;
        String[] details=support?new String[]{"Nearby enemies take splash damage.","Faster skill use and shorter cooldowns.","More physical damage with a higher mana cost; this deliberately long text must stay inside its own card."}:new String[]{"Sword / Mace","Sword","Sword / Dagger","Mace","Sword / Dagger"};
        for(int i=0;i<names.length;i++)lines.add("gemmake "+i+"\t"+names[i]+"\t"+details[i]);
        return lines.toArray(new String[0]);
    }
    private static void target(PanelCards cards,int x,int y,int width,int height,String action) {
        for(int[] point:new int[][]{{x,y},{x+width-1,y+height-1},{x+width/2,y+height/2}})
            check(action.equals(cards.actionAt(point[0],point[1])),"Exact target "+action);
        check(!action.equals(cards.actionAt(x+width,y+height/2)),"Far X boundary excluded");
        check(!action.equals(cards.actionAt(x+width/2,y+height)),"Far Y boundary excluded");
    }
    public static void main(String[] args)throws Exception {
        PanelCards cards=new PanelCards();
        check(cards.accept(payload(false)),"Dedicated gemcraft mode accepted");
        for(int filter=0;filter<4;filter++)target(cards,28+filter*92,100,84,24,"gemfilter "+new String[]{"all","sword","dagger","mace"}[filter]);
        check(cards.actionAt(410,110)==null,"Active jewel cannot click support-only filter");
        for(int choice=0;choice<5;choice++)target(cards,28+choice%2*232,134+choice/2*50,220,46,"gemmake "+choice);
        check(cards.actionAt(270,245)==null,"Unused sixth choice has no stale target");
        cards.accept(payload(true));target(cards,396,100,84,24,"gemfilter support");
        for(int filter=0;filter<4;filter++)check(cards.actionAt(35+filter*92,110)==null,"Support jewel disables active weapon filter");
        for(int choice=0;choice<3;choice++)target(cards,28+choice%2*232,134+choice/2*50,220,46,"gemmake "+choice);
        check(cards.actionAt(270,195)==null,"Shorter support list clears old choices");
        check(!cards.accept(new String[]{"guide","home\tBase"})&&cards.actionAt(30,140)==null,"Other modes clear gemcraft hitboxes");
        check(cards.accept(new String[]{"maps","open 2\tAshen Foundry Map\tRARE / 2 modifiers"}),"Existing map mode remains accepted");
        target(cards,28,124,220,44,"open 2");
        cards.accept(new String[]{"supplies","hub buy portals\tPortal scrolls / 25 coins"});
        target(cards,28,124,220,44,"hub buy portals");
        try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");
            RandomAccessFile index=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r")) {
            TextDrawingArea font=ExileTheme.body();
            for(boolean support:new boolean[]{false,true}) {
                cards.accept(payload(support));
                BufferedImage image=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);
                int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();
                Arrays.fill(pixels,ExileTheme.BACKGROUND);DrawingArea.initDrawingArea(334,512,pixels);
                ExileTheme.panel(16,16,480,306,ExileTheme.GOLD,ExileTheme.PANEL);
                int[] underlay=pixels.clone();
                cards.draw(font,270,support?195:145);
                // Description baselines and glyphs must not leak into the four-pixel row gaps.
                for(int y:new int[]{124,125,126,127,128,129,180,181,182,183,230,231,232,233,280,281,282,283})
                    for(int x=28;x<480;x++)check(pixels[x+y*512]==underlay[x+y*512],"No text/glyph overlaps card gaps");
                ImageIO.write(image,"png",new File("build/gemcraft-"+(support?"support":"active")+".png"));
            }
        }
        System.out.println("Passed "+checks+" gemcraft card/filter/native layout checks.");
    }
}
