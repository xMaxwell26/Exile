import java.awt.Rectangle;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;
import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;

/** Native AA font coverage for item title bands, advanced columns, trackers and sidebar tabs. */
public final class InterfaceTextBoundsCheck {
    private static int checks;
    private static final int BACKGROUND=0x33403B;
    private static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    private static BufferedImage canvas(int width,int height) {
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixels,BACKGROUND);DrawingArea.initDrawingArea(height,width,pixels);return image;
    }
    private static void inside(BufferedImage image,Rectangle bounds) {
        int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)
            if(!bounds.contains(x,y))check(pixels[x+y*image.getWidth()]==BACKGROUND,"Text stays inside its own frame");
    }
    public static void main(String[] args)throws Exception {
        PrototypeIntegrationCheck.loaded();TextDrawingArea font=ExileTheme.body();
        Item worst=null;int longest=0;
        for(EquipmentBases.Base base:EquipmentBases.all())for(int seed=0;seed<20;seed++) {
            Item item=EquipmentBases.create(base,30);
            java.util.List<ItemRoll.Affix> affixes=new ArrayList<>(AffixCatalog.roll(item,ItemRoll.Rarity.RARE,new Random(seed)));
            for(int n=1;n<=2;n++)affixes.add(new ItemRoll.Affix("corruption_"+n,ItemRoll.Layer.CORRUPTION_IMPLICIT,1,new Modifier(Stat.LIFE,Modifier.Operation.FLAT,10)));
            item.setRoll(item.getRoll().changed(ItemRoll.Rarity.RARE,true,2,affixes,null));
            int count=ItemTooltip.lines(item,true).size();if(count>longest){longest=count;worst=item;}
        }
        ItemTooltipCache cache=new ItemTooltipCache();cache.accept(ItemTooltip.payload(3214,0,worst));
        ItemTooltipCache.Entry entry=cache.get(3214,0,worst.getId());
        String longName="The Ashbound Relic of the Fallen Star, Tempered in the Ruined Halls of the Forgotten";
        entry.normal[0]=longName;entry.advanced[0]=longName;
        for(int[] size:new int[][]{{512,334},{1280,720}})for(boolean advanced:new boolean[]{false,true}) {
            BufferedImage image=canvas(size[0],size[1]);
            ItemTooltipRenderer.Layout layout=ItemTooltipRenderer.layout(font,entry,advanced,size[0]-1,size[1]-1);
            check(layout.x>=6&&layout.y>=6&&layout.x+layout.width<=size[0]-6&&layout.y+layout.height<=size[1]-6,"Tooltip clamped to viewport");
            check(27+(layout.header.size()-1)*22+5<layout.headerHeight,"Title descenders clear full-width separator");
            check(layout.headerHeight+22+(layout.rows-1)*layout.lineHeight+4<layout.height-5,"All body rows clear bottom border");
            if(advanced&&size[0]==512)check(layout.columns==2,"Actual long advanced item uses two readable columns");
            check(String.join(" ",layout.lines).replaceAll("\\s+","").equals(String.join(" ",advanced?entry.advanced:entry.normal).replaceAll("\\s+","")),"Long names and actual values survive layout");
            ItemTooltipRenderer.draw(font,entry,advanced,size[0]-1,size[1]-1);
            inside(image,new Rectangle(layout.x,layout.y,layout.width,layout.height));
            ImageIO.write(image,"png",new File("build/interface-tooltip-"+size[0]+"-"+(advanced?"advanced":"normal")+".png"));
        }
        MapObjective objective=new MapObjective();
        String instructions="TUTORIAL 7 / 10\nRecover the map dropped by the champion.\nPick it up, then use a portal scroll to return to your refuge.";
        objective.accept(Base64.getEncoder().encodeToString(instructions.getBytes("UTF-8")));
        for(int[] size:new int[][]{{512,334},{900,503},{1280,720},{1920,1080}}) {
            BufferedImage image=canvas(size[0],size[1]);Rectangle bounds=objective.bounds(font);
            Rectangle bar=new Rectangle(SkillPanel.barOffset(size[0])+12,SkillPanel.barTop(size[0],size[1]),392,78);
            check(!bounds.intersects(bar),"Objective avoids hotbar");
            if(size[0]>765){check(!bounds.intersects(new Rectangle(size[0]-250,0,250,184)),"Objective avoids minimap");check(!bounds.intersects(new Rectangle(size[0]-247,size[1]-336,247,336)),"Objective avoids sidebar");check(!bounds.intersects(new Rectangle(0,size[1]-175,519,175)),"Objective avoids chat");}
            objective.draw(font);inside(image,bounds);
            ImageIO.write(image,"png",new File("build/interface-objective-"+size[0]+".png"));
        }
        BufferedImage chat=canvas(512,165);objective.drawInChat(font,0);
        for(int y=116;y<165;y++)for(int x=0;x<512;x++)check((chat.getRGB(x,y)&0xFFFFFF)==BACKGROUND,"Chat-hosted instructions leave input row clear");
        ImageIO.write(chat,"png",new File("build/interface-objective-chat.png"));
        for(int tab:new int[]{2,5,6}) {
            BufferedImage image=canvas(240,300);ArpgTabs.draw(font,tab,20,20,new String[0]);
            inside(image,new Rectangle(20,20,190,260));
            check(ArpgTabs.action(tab,29,219,20,20)!=null,"Tab action target preserved");
            check(ArpgTabs.action(tab,29,218-1,20,20)==null,"Above button does not activate");
            ImageIO.write(image,"png",new File("build/interface-tab-"+tab+".png"));
        }
        System.out.println("Passed "+checks+" native-font tooltip/objective/tab bounds checks. Actual longest item="+longest+" source lines.");
    }
}
