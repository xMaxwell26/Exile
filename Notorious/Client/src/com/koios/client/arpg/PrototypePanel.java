package com.koios.client.arpg;
import com.koios.client.graphic.*;
public final class PrototypePanel {
 private static final int[][] POS={{256,155},{211,132},{171,110},{129,88},{86,73},{112,130},{45,60},{67,149},{301,132},{341,110},{383,88},{426,73},{400,130},{467,60},{445,149},{256,187},{256,217},{256,247},{211,243},{301,243},{169,232},{343,232}};
 private static void circle(int x,int y,int radius,int color){for(int dy=-radius;dy<=radius;dy++)for(int dx=-radius;dx<=radius;dx++)if(dx*dx+dy*dy<=radius*radius)pixel(x+dx,y+dy,color);}
 private static void pixel(int x,int y,int color){if(x>=DrawingArea.topX&&y>=DrawingArea.topY&&x<DrawingArea.bottomX&&y<DrawingArea.bottomY)DrawingArea.pixels[x+y*DrawingArea.width]=color;}
 private static void line(int x,int y,int tx,int ty,int color){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++)pixel(x+(tx-x)*i/Math.max(1,steps),y+(ty-y)*i/Math.max(1,steps),color);}
 /** Guide cards keep presentation short while retaining the server-owned action and explanation. */
 private static final class GuideCard {
  final String action,title,detail;final int x,y;
  GuideCard(String action,String title,String detail,int index){this.action=action;this.title=title;this.detail=detail;x=28+index%2*232;y=90+index/2*40;}
  boolean contains(int px,int py){return px>=x&&px<x+220&&py>=y&&py<y+34;}
 }
 private final java.util.List<GuideCard> guideCards=new java.util.ArrayList<>();
 private String guideProgression="",guideLoot="",guideControls="";
 private String[] lines={"guide"};
 private final PanelCards cards=new PanelCards();private boolean cardLayout;
 public void accept(String payload){lines=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8).split("\n");cardLayout=cards.accept(lines);guideCards.clear();if(lines[0].equals("guide"))buildGuide();}
 public String actionAt(int x,int y,boolean control){
  if(x<16||x>496||y<16||y>322)return null;
  if(y<46){for(int tab=0;tab<5;tab++)if(x>=22+tab*93&&x<111+tab*93&&y>=20&&y<44)return new String[]{"guide","tree","craft","maps","home"}[tab];return null;}
  if(lines[0].equals("guide")){for(GuideCard card:guideCards)if(card.contains(x,y))return card.action;return null;}
  if(cardLayout)return cards.actionAt(x,y);
  if(lines[0].startsWith("tree|")){
   if(y>295&&x>335)return "free";
   for(int id=1;id<=21;id++){int[] at=POS[id];if((x-at[0])*(x-at[0])+(y-at[1])*(y-at[1])<=169)return (control?"refund ":"allocate ")+id;}return null;
  }
  int row=(y-59)/12+1;if(y>=59&&row<lines.length){String[] entry=lines[row].split("\t",2);return entry.length==2&&!entry[0].isEmpty()?entry[0]:null;}return null;
 }
 public int hoverInventorySlot(int x,int y){if(!cardLayout)return -1;String action=cards.actionAt(x,y);if(action==null||!action.startsWith("open "))return -1;try{return Integer.parseInt(action.substring(5));}catch(NumberFormatException ex){return -1;}}
 public void draw(TextDrawingArea font,int mouseX,int mouseY){
  ExileTheme.panel(16,16,480,306,ExileTheme.GOLD,ExileTheme.PANEL);
  String[] tabs={"Guide","Passives","Crafting","Maps","Refuge"};int active=lines[0].startsWith("tree|")?1:(lines[0].startsWith("craft")||lines[0].equals("gemcraft")||lines[0].equals("reference"))?2:lines[0].startsWith("maps")?3:lines[0].startsWith("home")?4:0;for(int i=0;i<tabs.length;i++){ExileTheme.inset(22+i*93,20,89,24,i==active?ExileTheme.GOLD:ExileTheme.BORDER_SUBTLE,i==active?ExileTheme.SELECTED:ExileTheme.SURFACE_DARK);text(font,29+i*93,36,tabs[i],i==active?ExileTheme.HEADING:ExileTheme.MUTED);}
  if(cardLayout){cards.draw(font,mouseX,mouseY);return;}
  if(lines[0].equals("guide")){drawGuide(font,mouseX,mouseY);return;}
  if(lines[0].startsWith("tree|")){
   String[] head=lines[0].split("\\|");String[][] nodes=new String[22][];boolean[] owned=new boolean[22];
   for(int i=1;i<lines.length;i++){String[] f=lines[i].split("\\|",5);if(f.length<5)continue;int id=Integer.parseInt(f[0]);nodes[id]=f;owned[id]=Boolean.parseBoolean(f[2]);}
   text(font,92,57,"Blade",ExileTheme.HEADING);text(font,365,57,"Survival",ExileTheme.HEADING);text(font,281,205,"Energy",0x9CAFD1);
   for(int id=1;id<=21;id++){if(nodes[id]==null)continue;int parent=Integer.parseInt(nodes[id][1]);int[] a=POS[parent],b=POS[id];line(a[0],a[1],b[0],b[1],owned[id]?ExileTheme.GOLD:ExileTheme.BORDER);}
   circle(256,155,16,0x80683C);circle(256,155,13,ExileTheme.OXBLOOD);nodeGlyph(256,155,0,ExileTheme.HEADING);
   int hovered=0;
   for(int id=1;id<=21;id++){if(nodes[id]==null)continue;int[] at=POS[id];int parent=Integer.parseInt(nodes[id][1]);boolean available=(parent==0||owned[parent])&&!owned[id]&&Integer.parseInt(head[1])>0;int color=owned[id]?0xE4C779:available?0xA1B7C8:0x45515C;
    circle(at[0],at[1],11,color);circle(at[0],at[1],8,owned[id]?0x62754C:ExileTheme.SURFACE_DARK);nodeGlyph(at[0],at[1],id<=7?0:id<=14?1:2,color);
    if((mouseX-at[0])*(mouseX-at[0])+(mouseY-at[1])*(mouseY-at[1])<=169){hovered=id;circle(at[0],at[1],3,0xFFF0BC);}
   }
   DrawingArea.drawHorizontalLine(28,269,452,ExileTheme.BORDER_SUBTLE);
   if(hovered>0){String[] f=nodes[hovered];text(font,27,281,f[3]+(owned[hovered]?" / Allocated":" / 1 point"),ExileTheme.HEADING);String detail=f[4];text(ExileTheme.small(),27,293,detail,ExileTheme.TEXT);}
   else{text(font,27,281,"Follow a connected path. Hover a node to see its upgrade.",ExileTheme.MUTED);text(ExileTheme.small(),27,293,"Click to allocate. Ctrl-click to refund a leaf node.",ExileTheme.MUTED);}
   text(font,27,310,head[1]+" point"+(head[1].equals("1")?"":"s")+" available",ExileTheme.HEADING);text(ExileTheme.small(),337,310,Boolean.parseBoolean(head[2])?"Free refunds: on":"Free refunds: off",ExileTheme.MUTED);
  }else{
   TextDrawingArea listFont=ExileTheme.small();
   for(int i=1;i<lines.length&&i<=20;i++){String[] row=lines[i].split("\t",2);String label=(row.length==2?row[1]:row[0]).replace("MATERIAL REFERENCE","Crafting rules");text(listFont,27,66+(i-1)*12,fit(listFont,label,444),row.length==2&&!row[0].isEmpty()?ExileTheme.HEADING:ExileTheme.TEXT);}
   text(listFont,27,309,"Choose an option. Escape returns to the world.",ExileTheme.MUTED);
  }
 }
 private void buildGuide(){
  guideProgression="";guideLoot="";guideControls="";
  for(int i=1;i<lines.length;i++){
   String[] row=lines[i].split("\t",2);if(row.length<2)continue;
   if(row[0].isEmpty()){
    // These are game rules, not decorative rows: keep them visible beside and below the actions.
    if(row[1].startsWith("After level "))guideProgression=row[1];
    else if(row[1].contains("loot drops on the ground"))guideLoot=row[1];
    else if(row[1].startsWith("K:")||row[1].startsWith("Enter opens chat"))guideControls="K: loadout/controls. P: guide. Enter: chat. Typing pauses keyboard skills.";
    continue;
   }
   String title=row[1];
   switch(row[0]){
    case "exchange":title="Exchange old materials";break;
    case "scrolls":title="Portal scrolls  /  25 coins";break;
    case "claim":title="Collect stored rewards";break;
    case "kit":title="Starter equipment";break;
    case "home":title="Return to your refuge";break;
    case "intro":title="Guided introduction";break;
    case "tree":title="Grow your passive tree";break;
    case "craft":title="Crafting materials & gems";break;
    case "maps":title="Open the map device";break;
   }
   if(guideCards.size()<10)guideCards.add(new GuideCard(row[0],title,row[1],guideCards.size()));
  }
 }
 private void drawGuide(TextDrawingArea font,int mx,int my){
  ExileTheme.heading(28,64,"Your next expedition",ExileTheme.HEADING);
  text(font,28,80,"Prepare in your refuge. Adventure begins with a map.",ExileTheme.MUTED);
  GuideCard hovered=null;
  for(GuideCard card:guideCards){
   boolean hover=card.contains(mx,my);if(hover)hovered=card;
   ExileTheme.inset(card.x,card.y,220,34,hover?ExileTheme.GOLD:ExileTheme.BORDER,hover?ExileTheme.HOVER:ExileTheme.SURFACE);
   // A small engraved diamond signals an action without filling the panel with debug glyphs.
   line(card.x+12,card.y+13,card.x+16,card.y+17,ExileTheme.GOLD);line(card.x+16,card.y+17,card.x+12,card.y+21,ExileTheme.GOLD);
   text(font,card.x+25,card.y+22,fit(font,card.title,186),hover?ExileTheme.HEADING:ExileTheme.TEXT);
  }
  TextDrawingArea small=ExileTheme.small();
  if(!guideProgression.isEmpty()){
   DrawingArea.drawPixels(30,252,260,ExileTheme.BORDER,1);
   java.util.List<String> rules=ChatWrap.lines(guideProgression,210,small::getTextWidth);
   for(int line=0;line<rules.size();line++)text(small,270,263+line*14,rules.get(line),ExileTheme.MUTED);
  }
  String help=hovered==null?guideControls:hovered.detail;
  if(help.isEmpty())help="K opens your loadout. Enter opens chat. Escape returns to the world.";
  text(small,28,295,help,ExileTheme.MUTED);
  if(!guideLoot.isEmpty())text(small,28,309,guideLoot,ExileTheme.MUTED);
 }
 private static String fit(TextDrawingArea font,String text,int width){
  if(font.getTextWidth(text)<=width)return text;
  String result=text;while(!result.isEmpty()&&font.getTextWidth(result+"...")>width)result=result.substring(0,result.length()-1);return result+"...";
 }
 /** Small stat motifs remain legible even before a node is allocated. */
 private static void nodeGlyph(int x,int y,int branch,int color){
  if(branch==0){line(x-3,y+4,x+3,y-4,color);line(x-4,y,x,y+3,color);}
  else if(branch==1){line(x-4,y-4,x+4,y-4,color);line(x-4,y-4,x-3,y+2,color);line(x+4,y-4,x+3,y+2,color);line(x-3,y+2,x,y+5,color);line(x+3,y+2,x,y+5,color);}
  else{line(x,y-5,x+4,y,color);line(x+4,y,x,y+5,color);line(x,y+5,x-4,y,color);line(x-4,y,x,y-5,color);}
 }
 private void text(TextDrawingArea font,int x,int y,String text,int color){ExileTheme.label(font,x,y,text,color);}
}
