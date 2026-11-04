import com.koios.game.arpg.*;
import com.koios.game.model.player.Player;
import com.koios.client.arpg.PanelCards;
import com.koios.client.graphic.*;
import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

/** Read actual server packets and render the exact reference with the cache font. */
public final class CraftingReferenceCheck {
    private static int checks;
    private static void check(boolean condition, String reason) {
        checks++; if(!condition) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        PrototypeIntegrationCheck.loaded();
        Player player=PrototypeIntegrationCheck.player("craftreference",87);
        PrototypeIntegrationCheck.idle(player);
        PanelCards cards=new PanelCards();
        PrototypePanel.handle(player,"arpgproto craft");
        cards.accept(CraftingPanelPagingCheck.panel(player).split("\n"));
        check("reference".equals(cards.actionAt(40,295)),"Empty crafting reference is reachable");
        player.getInventory().set(0,MapRuns.gem(1,false));
        PrototypePanel.handle(player,"arpgproto craft");
        cards.accept(CraftingPanelPagingCheck.panel(player).split("\n"));
        check("reference".equals(cards.actionAt(40,260)),"Gem list retains reference navigation");
        PrototypePanel.handle(player,"arpgproto reference");
        Set<String> names=new HashSet<>();
        try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");
            RandomAccessFile index=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r")) {
            TextDrawingArea font=new TextDrawingArea(false,"p12_full",new StreamLoader(new Decompressor(data,index,1).decompress(1)));
            for(int page=0;page<4;page++) {
                PrototypePanel.handle(player,"arpgproto page "+page);
                String packet=CraftingPanelPagingCheck.panel(player);
                String[] rows=packet.split("\n");
                check(rows[0].equals("reference"),"Reference pagination keeps destination");
                check(rows.length<=21,"All rows fit fixed panel");
                for(int i=1;i<rows.length;i++) {
                    String label=rows[i].split("\t",2)[1];
                    check(label.length()<=78&&font.getTextWidth(label)<=450,"Reference row is never truncated: "+label);
                    names.add(label);
                }
                for(int currency=page*3;currency<Math.min(CraftingCurrency.IDS.length,page*3+3);currency++) {
                    String normalized=packet.replaceAll("\\s+"," ");
                    check(normalized.contains(CraftingCurrency.description(currency)),"Full recipe survives wrapping");
                }
                BufferedImage image=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);
                int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();
                DrawingArea.initDrawingArea(334,512,pixels);
                com.koios.client.arpg.PrototypePanel panel=new com.koios.client.arpg.PrototypePanel();
                panel.accept(Base64.getEncoder().encodeToString(packet.getBytes("UTF-8")));
                panel.draw(font,0,0);
                check("craft".equals(panel.actionAt(30,60,false)),"Visible back row has matching hit target");
                ImageIO.write(image,"png",new File("build/crafting-reference-"+(page+1)+".png"));
            }
        }
        for(int id:CraftingCurrency.IDS)check(names.contains(new com.koios.game.model.item.Item(id).getDefinition().getName()),"Every currency documented: "+id);
        check(player.getInventory().size()==1&&MapRuns.isGem(player.getInventory().get(0)),"Browsing preserves inventory");
        PrototypePanel.handle(player,"arpgproto page 999");
        check(player.getAttribute("prototype-page",-1)==3,"Last page clamps safely");
        System.out.println("Passed "+checks+" complete crafting reference/navigation/font checks.");
    }
}
