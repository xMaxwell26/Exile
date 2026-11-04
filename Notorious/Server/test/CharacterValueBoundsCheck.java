import com.koios.client.arpg.*;
import com.koios.client.graphic.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

/** Integer-valued character stats are allowed up to Integer.MAX_VALUE by the current sheet. */
public final class CharacterValueBoundsCheck {
    private static int checks;
    private static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception {
        String max="2147483647";
        String[] sheet={"Character Level: 100","XP: 980100 / 980100","Passive Points: 100",
            "Life: "+max+" / "+max,"Energy Shield: "+max+" / "+max,"Mana: "+max+" / "+max,
            "Physical: "+max+"-"+max,"Attack Speed: 1000.00 / sec","Critical Chance: 100.0%",
            "Crit Multiplier: 1000%","Accuracy: "+max,"Armour: "+max,"Evasion: "+max,
            "Block: 75%","Fire Resist: -200%","Cold Resist: -200%","Lightning Resist: -200%","Chaos Resist: -200%"};
        java.util.List<String> rows=CharacterPanel.rows(sheet);
        check(rows.size()==16,"Large defences use separate rows within the existing panel");
        check(rows.contains("Armour: "+max)&&rows.contains("Evasion: "+max),"Both defence values survive");
        check(rows.contains("ES: "+max+" / "+max),"Energy shield retains exact current and maximum");
        for(String row:rows)check(ExileTheme.small().getTextWidth(row)<=172,"Complete row fits: "+row);
        check(42+(rows.size()-1)*14+3<260-4,"Every row clears the frame");
        for(int[] size:new int[][]{{512,334},{1280,720}}){
            BufferedImage image=new BufferedImage(size[0],size[1],BufferedImage.TYPE_INT_RGB);
            int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();
            Arrays.fill(pixels,0x33403B);DrawingArea.initDrawingArea(size[1],size[0],pixels);
            int x=size[0]-202,y=20;CharacterPanel.draw(ExileTheme.body(),x,y,sheet);
            for(int py=0;py<size[1];py++)for(int px=0;px<size[0];px++)
                if(px<x||px>=x+190||py<y||py>=y+260)check(pixels[px+py*size[0]]==0x33403B,"Character panel stays in frame");
            ImageIO.write(image,"png",new File("build/character-large-values-"+size[0]+".png"));
        }
        System.out.println("Passed "+checks+" exact character-value and native frame checks.");
    }
}
