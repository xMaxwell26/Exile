package com.koios.client.arpg;
import java.util.*;import com.koios.client.graphic.*;
/** Card layouts for map selection and gem choices using the existing server actions. */
public final class PanelCards {
 private static final class Card {String action,label,detail="";int x,y,w,h,glyph=-1,mapId=-1;boolean support,selected;Card(String action,String label,int x,int y,int w,int h){this.action=action;this.label=label;this.x=x;this.y=y;this.w=w;this.h=h;}}
 private final java.util.List<Card> cards=new ArrayList<>();private String title="",detail="";private boolean active,gemCraft;private String secondary="";
 public boolean accept(String[] lines){
  cards.clear();active=false;gemCraft=false;secondary="";if(lines.length==0)return false;if(lines[0].equals("gemcraft"))return acceptGemCraft(lines);boolean maps=lines[0].equals("maps"),supplies=lines[0].equals("supplies"),gem=false;for(String line:lines)if(line.startsWith("learn "))gem=true;boolean crafting=lines[0].equals("craft")&&!gem;if(!maps&&!gem&&!supplies&&!crafting)return false;active=true;title=maps?"MAP DEVICE":supplies?"QUARTERMASTER":crafting?"CRAFTING & GEMS":"GEM WORKSHOP";detail=maps?"Choose a map to open a private encounter.":supplies?"Basic expedition supplies, purchased with coins.":crafting?"Use materials on items in inventory. Select a gem to learn.":"Choose the ability this gem will upgrade.";
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
 /** Gem cutting is a separate layout so changes cannot move map/supply/learning targets. */
 private boolean acceptGemCraft(String[] lines) {
  active=true;gemCraft=true;title="GEMCUTTING";
  detail="Choose what to cut from your uncut jewel.";
  secondary="Filters show compatible skills and support gems.";
  int headers=0,choices=0,pages=0;boolean supportBase=false,activeBase=false;
  for(String line:lines){if(line.contains("UNCUT SUPPORT JEWEL"))supportBase=true;if(line.contains("UNCUT SKILL JEWEL"))activeBase=true;}
  for(int i=1;i<lines.length;i++) {
   String[] fields=lines[i].split("\t",3);
   if(fields.length<2)continue;
   String action=fields[0],label=fields[1];
   if(action.isEmpty()) {
    if(headers++==0)detail=label;
    else if(headers==2)secondary=label;
   } else if(action.startsWith("gemfilter ")) {
    String kind=action.substring(10);
    int index=Arrays.asList("all","sword","dagger","mace","support").indexOf(kind);
    if(index<0)continue;
    Card filter=new Card(action,label.replace("[","").replace("]",""),28+index*92,100,84,24);
    filter.selected=label.startsWith("[")&&label.endsWith("]");
    if(supportBase&&!kind.equals("support")||activeBase&&kind.equals("support")){filter.action="";filter.selected=false;}cards.add(filter);
   } else if(action.startsWith("gemmake ")&&choices<6) {
    Card choice=new Card(action,label,28+(choices%2)*232,134+(choices/2)*50,220,46);
    choice.detail=fields.length>2?fields[2]:"Cut this gem from the selected jewel.";
    choice.mapId=22411;
    // Names remain server-owned; known active/support names get their existing semantic glyph.
    for(int skill=0;skill<SkillPanel.NAMES.length;skill++)if(label.toLowerCase(Locale.ROOT).contains(SkillPanel.NAMES[skill].toLowerCase(Locale.ROOT))){choice.glyph=skill;choice.mapId=-1;break;}
    for(int support=0;support<SkillPanel.SUPPORTS.length;support++)if(label.toLowerCase(Locale.ROOT).contains(SkillPanel.SUPPORTS[support].toLowerCase(Locale.ROOT))){choice.glyph=support;choice.support=true;choice.mapId=-1;break;}
    if(choice.glyph<0&&label.toLowerCase(Locale.ROOT).contains("support"))choice.mapId=22412;
    cards.add(choice);choices++;
   } else if(action.startsWith("page ")&&pages++<2) {
    cards.add(new Card(action,label,label.startsWith("Next")?260:28,286,220,22));
   }
  }
  if(choices==0)secondary="No compatible choices in this filter. Try All.";
  return true;
 }

 /** Fit text to its real font width; never paint descriptions through another card. */
 private static String fit(TextDrawingArea font,String value,int width) {
  if(font.getTextWidth(value)<=width)return value;
  String result=value;
  while(result.length()>0&&font.getTextWidth(result+"...")>width)result=result.substring(0,result.length()-1);
  return result+"...";
 }

 private void drawGemCraft(TextDrawingArea font,int mouseX,int mouseY) {
  text(font,28,62,title,ExileTheme.HEADING);
  text(font,28,77,fit(font,detail,444),ExileTheme.TEXT);
  text(font,28,92,fit(font,secondary,444),ExileTheme.MUTED);
  for(Card card:cards) {
   boolean enabled=!card.action.isEmpty();boolean hover=enabled&&mouseX>=card.x&&mouseX<card.x+card.w&&mouseY>=card.y&&mouseY<card.y+card.h;
   int border=!enabled?ExileTheme.BORDER_SUBTLE:hover||card.selected?ExileTheme.GOLD:ExileTheme.BORDER;
   int fill=!enabled?ExileTheme.SURFACE_DARK:card.selected?ExileTheme.SELECTED:hover?ExileTheme.HOVER:ExileTheme.SURFACE;
   ExileTheme.inset(card.x,card.y,card.w,card.h,border,fill);
   if(!card.action.startsWith("gemmake ")) {
    font.drawText(!enabled?ExileTheme.DIM:card.selected?ExileTheme.HEADING:ExileTheme.TEXT,card.x+card.w/2,fit(font,card.label,card.w-12),card.y+16,true);
    continue;
   }
   Sprite icon=card.glyph>=0?SkillGlyphs.icon(card.glyph,card.support,28):ArtSprites.icon(card.mapId);
   if(icon!=null)icon.drawSprite(card.x+7,card.y+8);
   int x=card.x+43,width=card.w-51;
   text(font,x,card.y+15,fit(font,card.label,width),hover?ExileTheme.HEADING:ExileTheme.TEXT);
   java.util.List<String> wrapped=ChatWrap.lines(card.detail,width,font::getTextWidth);
   for(int line=0;line<wrapped.size()&&line<2;line++) {
    String value=wrapped.get(line);
    if(line==1&&wrapped.size()>2)value=fit(font,value+"...",width);
    text(font,x,card.y+29+line*12,value,ExileTheme.MUTED);
   }
  }
 }

 private static String shorten(String label){if(label.startsWith("Return to"))return "Return to base";if(label.startsWith("Create a basic"))return "Map / 25 coins";if(label.startsWith("Start or resume"))return "Guided introduction";if(label.equals("Choose a different item"))return "Other gems";return label;}
 public String actionAt(int x,int y){if(!active)return null;for(Card c:cards)if(x>=c.x&&x<c.x+c.w&&y>=c.y&&y<c.y+c.h)return c.action.isEmpty()?null:c.action;return null;}
 public void draw(TextDrawingArea font,int mouseX,int mouseY){
  if(gemCraft){drawGemCraft(font,mouseX,mouseY);return;}
  text(font,28,62,title,ExileTheme.HEADING);String d=detail;while(font.getTextWidth(d)>444&&d.length()>3)d=d.substring(0,d.length()-4)+"...";text(font,28,77,d,ExileTheme.MUTED);
  for(Card c:cards){boolean hover=!c.action.isEmpty()&&mouseX>=c.x&&mouseX<c.x+c.w&&mouseY>=c.y&&mouseY<c.y+c.h;ExileTheme.inset(c.x,c.y,c.w,c.h,hover?ExileTheme.GOLD:c.action.isEmpty()?ExileTheme.BORDER_SUBTLE:ExileTheme.BORDER,hover?ExileTheme.HOVER:c.action.isEmpty()?ExileTheme.SURFACE_DARK:ExileTheme.SURFACE);int x=c.x+10;if(c.glyph>=0){Sprite icon=SkillGlyphs.icon(c.glyph,c.support,28);if(icon!=null)icon.drawSprite(x,c.y+8);x+=36;}
   String label=c.label;if(c.action.startsWith("open ")){int at=label.indexOf(": ");if(at>=0)label=label.substring(at+2);}if(c.action.startsWith("learn ")){label=label.replace("Upgrade ","").replace("Claim a ","").replace(" support gem","").replace(" gem","");}
   if(c.mapId>=0){Sprite icon=ArtSprites.icon(c.mapId);if(icon!=null)icon.drawSprite(c.x+6,c.y+6);x=c.x+43;String title=label;while(font.getTextWidth(title)>c.w-51&&title.length()>3)title=title.substring(0,title.length()-4)+"...";int color=c.detail.startsWith("RARE")?0xE6CD84:c.detail.startsWith("MAGIC")?0xA5A5E8:0xB7CBD8;text(font,x,c.y+18,title,color);text(font,x,c.y+33,c.detail,ExileTheme.MUTED);}
   else{java.util.List<String> wrapped=ChatWrap.lines(label,c.w-(x-c.x)-8,font::getTextWidth);int baseline=c.y+(c.h<=30?19:wrapped.size()>1?18:27);for(int i=0;i<wrapped.size()&&i<2;i++)text(font,x,baseline+i*13,wrapped.get(i),hover?0xF0DEB2:0xB7CBD8);}
  }
 }
 private static void text(TextDrawingArea font,int x,int y,String value,int color){ExileTheme.label(font,x,y,value,color);}
}
