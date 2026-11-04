import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;
import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;
import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import com.koios.game.def.ItemDefinition;
public class TooltipRenderCheck {
 public static void main(String[] args)throws Exception{
  ItemDefinition.load();
  RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");
  RandomAccessFile idx=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");
  TextDrawingArea font=new TextDrawingArea(false,"p12_full",new StreamLoader(new Decompressor(data,idx,1).decompress(1)));
  com.koios.client.definition.ItemDefinition.unpackConfig(new StreamLoader(new Decompressor(data,idx,1).decompress(2)));
  for(int id:CraftingCurrency.IDS){com.koios.client.definition.ItemDefinition definition=com.koios.client.definition.ItemDefinition.forID(id);if(definition.id!=id||!definition.stackable||definition.modelID<0||!definition.name.equals(ItemDefinition.forId(id).getName()))throw new AssertionError("Client/server currency mismatch: "+id);}
  Item item=PrototypeItems.sword(new Random(1));ItemTooltipCache cache=new ItemTooltipCache();cache.accept(ItemTooltip.payload(3214,0,item));
  for(boolean advanced:new boolean[]{false,true}){
   BufferedImage image=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);
   int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();Arrays.fill(pixels,0x242A2D);
   DrawingArea.initDrawingArea(334,512,pixels);
   ItemTooltipRenderer.draw(font,cache.get(3214,0,22420),advanced,600,250);
   File out=new File("build/tooltip-"+(advanced?"advanced":"normal")+".png");ImageIO.write(image,"png",out);
   if(pixels[6*512+506]==0x242A2D){} // Image is inspected visually after generation.
   System.out.println(out.getAbsolutePath());
  }
  BufferedImage panel=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);
  int[] panelPixels=((DataBufferInt)panel.getRaster().getDataBuffer()).getData();Arrays.fill(panelPixels,0x242A2D);DrawingArea.initDrawingArea(334,512,panelPixels);
  SkillPanel.draw(font,2,new String[]{"38","50","1.8","0.0","5","0","1","1","1","1","1","2.4","3","5"});
  ImageIO.write(panel,"png",new File("build/skills-panel.png"));
  com.koios.client.arpg.SkillDragState dragging=new com.koios.client.arpg.SkillDragState();String[] loadoutPreview={"38","50","1.8","0.0","1","0","1","1","1","1","1","2.4","3","5","2","1","0"};dragging.begin(350,85,loadoutPreview);dragging.move(90,150);com.koios.client.arpg.SkillPanel.draw(font,dragging,loadoutPreview,90,150);ImageIO.write(panel,"png",new File("build/skills-dragging.png"));

  BufferedImage sheet=new BufferedImage(230,300,BufferedImage.TYPE_INT_RGB);int[] sheetPixels=((DataBufferInt)sheet.getRaster().getDataBuffer()).getData();Arrays.fill(sheetPixels,0x242A2D);DrawingArea.initDrawingArea(300,230,sheetPixels);
  CharacterPanel.draw(font,20,15,new String[]{"Character Level: 12","XP: 12100 / 14400","Passive Points: 11","Life: 260 / 280","Energy Shield: 50 / 80","Mana: 38 / 50","Physical: 6-14","Attack Speed: 1.25 / sec","Critical Chance: 7.2%","Crit Multiplier: 150%","Accuracy: 210","Armour: 250","Evasion: 100","Block: 0%","Fire Resist: 25%","Cold Resist: 25%","Lightning Resist: 25%","Chaos Resist: 20%"});
  ImageIO.write(sheet,"png",new File("build/character-sheet.png"));
  com.koios.client.arpg.PrototypePanel prototype=new com.koios.client.arpg.PrototypePanel();
  for(String page:new String[]{"guide","tree"}){
   StringBuilder payload=new StringBuilder(page.equals("tree")?"tree|3|false":"guide\nkit\tClaim starter sword, armour and crafting supplies (once)\nhome\tEnter your private house base\nintro\tBegin the introductory encounter from your base\ntree\tSpend passive points: click a node; Ctrl-click to refund\ncraft\tCrafting bench: choose an item and a currency action\nmaps\tMap device: consume a dropped map to open an instance");
   if(page.equals("tree"))for(PassiveTree.Node node:PassiveTree.NODES.values())payload.append("\n").append(node.id).append("|").append(node.parent).append("|").append(node.id==1).append("|").append(node.name).append("|").append(node.description());
   prototype.accept(java.util.Base64.getEncoder().encodeToString(payload.toString().getBytes("UTF-8")));
   BufferedImage preview=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);int[] buffer=((DataBufferInt)preview.getRaster().getDataBuffer()).getData();Arrays.fill(buffer,0x242A2D);DrawingArea.initDrawingArea(334,512,buffer);prototype.draw(font,211,132);ImageIO.write(preview,"png",new File("build/prototype-"+page+".png"));
  }
  BufferedImage currencies=new BufferedImage(384,64,BufferedImage.TYPE_INT_RGB);int[] cp=((DataBufferInt)currencies.getRaster().getDataBuffer()).getData();Arrays.fill(cp,0x242A2D);DrawingArea.initDrawingArea(64,384,cp);for(int n=0;n<6;n++){CurrencyIcons.sprite(22400+n).drawSprite(n*64+16,8);font.method389(true,n*64+8,0xDDCCAA,new String[]{"Alchemy","Chaos","Exalted","Transmute","Vaal","Wisdom"}[n],55);}ImageIO.write(currencies,"png",new File("build/currency-icons.png"));
  for(boolean done:new boolean[]{false,true}){BufferedImage preview=new BufferedImage(512,334,BufferedImage.TYPE_INT_RGB);int[] buffer=((DataBufferInt)preview.getRaster().getDataBuffer()).getData();Arrays.fill(buffer,0x242A2D);DrawingArea.initDrawingArea(334,512,buffer);MapObjective objective=new MapObjective();String objectiveText=done?"MAP COMPLETE / Forsaken Settlement T1\nSettlement Champion defeated\nCollect loot, then enter the return portal":"MAP OBJECTIVE / Forsaken Settlement T1\nDefeat the Settlement Champion\nBoss: north-east / 12 tiles / Enemies: 19\nPortal Scroll: open a return portal";objective.accept(java.util.Base64.getEncoder().encodeToString(objectiveText.getBytes("UTF-8")));objective.draw(font);ImageIO.write(preview,"png",new File("build/map-objective-"+done+".png"));}
  com.koios.client.definition.ItemDefinition scroll=com.koios.client.definition.ItemDefinition.forID(MapRuns.PORTAL_SCROLL);if(!"Portal Scroll".equals(scroll.name)||!"Read".equals(scroll.itemActions[0]))throw new AssertionError("Portal scroll client definition");
  data.close();idx.close();
 }
}
