package com.koios.client.arpg;
import java.util.*;import com.koios.client.graphic.*;
/** Card layouts for map selection and gem choices using the existing server actions. */
public final class PanelCards {
 private static final class Card {String action,label,detail="";int x,y,w,h,glyph=-1,mapId=-1;boolean support;Card(String action,String label,int x,int y,int w,int h){this.action=action;this.label=label;this.x=x;this.y=y;this.w=w;this.h=h;}}
 private final java.util.List<Card> cards=new ArrayList<>();private String title="",detail="";private boolean active;
 public boolean accept(String[] lines){
  cards.clear();active=false;if(lines.length==0)return false;boolean maps=lines[0].equals("maps"),supplies=lines[0].equals("supplies"),gem=false;for(String line:lines)if(line.startsWith("learn "))gem=true;boolean crafting=lines[0].equals("craft")&&!gem;if(!maps&&!gem&&!supplies&&!crafting)return false;active=true;title=maps?"MAP DEVICE":supplies?"QUARTERMASTER":crafting?"CRAFTING & GEMS":"GEM WORKSHOP";detail=maps?"Choose a map to open a private encounter.":supplies?"Basic expedition supplies, purchased with coins.":crafting?"Use materials on items in inventory. Select a gem to learn.":"Choose the ability this gem will upgrade.";
  java.util.List<String[]> main=new ArrayList<>(),utility=new ArrayList<>(),pages=new ArrayList<>();
  for(int i=1;i<lines.length;i++){String[] f=lines[i].split("\t",3);if(f.length<2||f[0].isEmpty())continue;if(f[0].startsWith("page "))pages.add(f);else if(maps&&f[0].startsWith("open ")||gem&&f[0].startsWith("learn ")||supplies&&f[0].startsWith("hub buy ")||crafting&&f[0].startsWith("select "))main.add(f);else if(gem&&f[1].startsWith("Selected:")){detail=f[1].substring(10);}else if(!crafting)utility.add(f);}
  if(crafting&&main.isEmpty()){
   title="MATERIAL REFERENCE";boolean selected=false;
   for(String line:lines)if(line.startsWith("items\tSelected:")){selected=true;detail=line.substring(line.indexOf("Selected:")+10);cards.add(new Card("items","Choose another item",28,84,145,30));break;}
   // Keep this list in the same deliberate order as the crafting vocabulary:
   // broad rarity changes, magic-item refinement, then destructive finishing.
   int[] ids={22400,22401,22402,22403,22405,22407,22408,22409,22415,22416,22404};
   String[] labels={"Alchemy","Chaos","Exalted","Transmutation","Wisdom","Alteration","Augmentation","Regal","Scouring","Annulment","Vaal"};
   String[] effects={"Normal > rare","Reroll rare","Add rare affix","Normal > magic","Identify","Reroll magic","Add magic affix","Magic > rare","Remove affixes","Remove one affix","Corrupt"};
   int top=selected?124:92;
   for(int i=0;i<ids.length;i++){Card card=new Card("",labels[i],28+i%3*153,top+i/3*49,145,40);card.mapId=ids[i];card.detail=effects[i];cards.add(card);}
   if(!selected)detail="Use a material on an inventory item. Failed crafts spend nothing.";
   cards.add(new Card("reference","Full crafting rules",selected?181:28,selected?84:286,selected?298:452,selected?30:22));
   return true;
  }
  int utilityCount=Math.min(3,utility.size());for(int i=0;i<utilityCount;i++){String[] f=utility.get(i);cards.add(new Card(f[0],shorten(f[1]),28+i*153,84,145,30));}
  if(main.isEmpty()){
   cards.clear();int count=0;boolean resume=false;for(String[] f:utility){if(f[0].equals("resume"))resume=true;int col=count%2,row=count++/2;cards.add(new Card(f[0],f[1],28+col*232,92+row*58,220,48));}detail=resume?"Resume your active map or close it before opening another.":"No maps here. Create a recovery map or finish the introduction.";
  }else{
   for(int i=0;i<main.size()&&i<6;i++){String[] f=main.get(i);Card card=new Card(f[0],f[1],28+(i%2)*232,(crafting?96:124)+(i/2)*52,220,44);if(gem){int value=Integer.parseInt(f[0].substring(6));card.support=f[1].contains("support")||f[1].contains("gem");card.glyph=card.support?value-2:value<2?value:value-3;}else if(crafting){card.mapId=f[1].contains("Support")?22412:22411;int at=f[1].lastIndexOf(" / ");if(at>=0){card.label=f[1].substring(0,at).replace("Uncut ","");card.detail=f[1].substring(at+3);}}else if(supplies){card.mapId=f[0].endsWith("portals")?22406:f[0].endsWith("wisdom")?22405:22410;int at=f[1].lastIndexOf(" / ");if(at>=0){card.label=f[1].substring(0,at);card.detail=f[1].substring(at+3);}}else{card.mapId=f[1].contains("Ashen Foundry")?22413:f[1].contains("Thornwild")?22414:22410;card.detail=f.length>2?f[2]:"";}cards.add(card);}
  }
  if(crafting)cards.add(new Card("reference","Full crafting rules",28,252,452,26));
  for(int i=0;i<pages.size()&&i<2;i++){String[] f=pages.get(i);cards.add(new Card(f[0],f[1],f[1].startsWith("Next")?260:28,286,220,22));}return true;
 }
 private static String shorten(String label){if(label.startsWith("Return to"))return "Return to base";if(label.startsWith("Create a basic"))return "Map / 25 coins";if(label.startsWith("Start or resume"))return "Guided introduction";if(label.equals("Choose a different item"))return "Other gems";return label;}
 public String actionAt(int x,int y){if(!active)return null;for(Card c:cards)if(x>=c.x&&x<c.x+c.w&&y>=c.y&&y<c.y+c.h)return c.action.isEmpty()?null:c.action;return null;}
 public void draw(TextDrawingArea font,int mouseX,int mouseY){
  text(font,28,62,title,ExileTheme.HEADING);String d=detail;while(font.getTextWidth(d)>444&&d.length()>3)d=d.substring(0,d.length()-4)+"...";text(font,28,77,d,ExileTheme.MUTED);
  for(Card c:cards){boolean hover=!c.action.isEmpty()&&mouseX>=c.x&&mouseX<c.x+c.w&&mouseY>=c.y&&mouseY<c.y+c.h;ExileTheme.inset(c.x,c.y,c.w,c.h,hover?ExileTheme.GOLD:c.action.isEmpty()?ExileTheme.BORDER_SUBTLE:ExileTheme.BORDER,hover?ExileTheme.HOVER:c.action.isEmpty()?ExileTheme.SURFACE_DARK:ExileTheme.SURFACE);int x=c.x+10;if(c.glyph>=0){Sprite icon=SkillGlyphs.icon(c.glyph,c.support,28);if(icon!=null)icon.drawSprite(x,c.y+8);x+=36;}
   String label=c.label;if(c.action.startsWith("open ")){int at=label.indexOf(": ");if(at>=0)label=label.substring(at+2);}if(c.action.startsWith("learn ")){label=label.replace("Upgrade ","").replace("Claim a ","").replace(" support gem","").replace(" gem","");}
   if(c.mapId>=0){Sprite icon=ArtSprites.icon(c.mapId);if(icon!=null)icon.drawSprite(c.x+6,c.y+6);x=c.x+43;String title=label;while(font.getTextWidth(title)>c.w-51&&title.length()>3)title=title.substring(0,title.length()-4)+"...";int color=c.detail.startsWith("RARE")?0xE6CD84:c.detail.startsWith("MAGIC")?0xA5A5E8:0xB7CBD8;text(font,x,c.y+18,title,color);text(font,x,c.y+33,c.detail,ExileTheme.MUTED);}
   else{java.util.List<String> wrapped=ChatWrap.lines(label,c.w-(x-c.x)-8,font::getTextWidth);int baseline=c.y+(c.h<=30?19:wrapped.size()>1?18:27);for(int i=0;i<wrapped.size()&&i<2;i++)text(font,x,baseline+i*13,wrapped.get(i),hover?0xF0DEB2:0xB7CBD8);}
  }
 }
 private static void text(TextDrawingArea font,int x,int y,String value,int color){ExileTheme.label(font,x,y,value,color);}
}


