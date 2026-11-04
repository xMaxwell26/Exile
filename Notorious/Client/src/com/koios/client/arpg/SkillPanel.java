package com.koios.client.arpg;
import com.koios.client.graphic.*;
public final class SkillPanel {
 public static final String[] NAMES={"Heavy Strike","Cleave","Whirlwind","Ground Slam","Execute"},SUPPORTS={"Splash","Faster Attacks","Brutality"},KEYS={"Q","W","E"};
 private static final int[] COLORS={0xD9B86C,0x7CB9CE,0xAD93DA,0xC58B65,0xD98989},SUPPORT_COLORS={0x72B8DB,0x8FD0A0,0xD98B7D};
 public static void setKeys(String[] labels){for(int i=0;i<3;i++)KEYS[i]=labels!=null&&i<labels.length&&labels[i]!=null?labels[i]:"Unbound";}
 private static String fit(TextDrawingArea font,String value,int width){while(font.getTextWidth(value)>width&&value.length()>3)value=value.substring(0,value.length()-4)+"...";return value;}
 private static int value(String[] state,int index,int fallback){try{return Integer.parseInt(state[index]);}catch(Exception ex){return fallback;}}
 public static int assignment(String[] state,int slot){return value(state,14+slot,slot);}
 public static int available(String[] state,int support){return value(state,23+support,0);}
 public static int owned(String[] state,int support){int total=available(state,support);for(int skill=0;skill<NAMES.length;skill++)if((mask(state,skill)&(1<<support))!=0)total++;return total;}
 public static String name(int skill){return skill<0||skill>=NAMES.length?"Empty":NAMES[skill];}
 public static int color(int skill){return skill<0||skill>=COLORS.length?0x738492:COLORS[skill];}
 public static int mask(String[] state,int skill){return value(state,skill>=3?18+(skill-3)*3:skill==2?12:4+skill,0);}
 public static int level(String[] state,int skill){return value(state,skill>=3?19+(skill-3)*3:skill==2?13:6+skill,1);}
 private static String cooldown(String[] state,int skill){int i=skill>=3?17+(skill-3)*3:skill==2?11:2+skill;return i<state.length?state[i]:"0.0";}
 public static int loadoutSlotAt(int x,int y){if(x<28||x>=320)return -1;for(int slot=0;slot<3;slot++)if(y>=66+slot*64&&y<124+slot*64)return slot;return -1;}
 public static int activeSocketAt(int x,int y){int slot=loadoutSlotAt(x,y);return slot>=0&&x>=36&&x<72&&y>=77+slot*64&&y<113+slot*64?slot:-1;}
 public static int skillAt(int x,int y){return loadoutSlotAt(x,y);}
 public static int skillAt(int x,int y,int page){return loadoutSlotAt(x,y);}
 public static int socketAt(int x,int y){int slot=loadoutSlotAt(x,y);if(slot<0||y<80+slot*64||y>=109+slot*64)return -1;for(int i=0;i<3;i++)if(x>=198+i*37&&x<228+i*37)return i;return -1;}
 public static int socketAt(int x,int y,int page){return socketAt(x,y);}
 public static int libraryAt(int x,int y){if(x<336||x>=484)return -1;for(int i=0;i<3;i++)if(y>=70+i*49&&y<112+i*49)return i;return -1;}
 public static boolean inLibrary(int x,int y){return x>=330&&x<490&&y>=64&&y<254;}
 public static int hotbarAt(int x,int y){if(y<276||y>=307)return -1;for(int i=0;i<3;i++)if(x>=28+i*153&&x<172+i*153)return i;return -1;}
 public static int barSlotAt(int x,int y,int height){return barSlotAt(x,y,512,height);}
 public static int barSlotAt(int x,int y,int width,int height){int top=barTop(width,height),ox=barOffset(width);if(y<top+35||y>=top+72)return -1;for(int slot=0;slot<3;slot++)if(x>=ox+18+slot*127&&x<ox+141+slot*127)return slot;return -1;}
 public static int barTop(int width,int height){return height-(width>765&&width<1500?253:87);}
 /** Compact resizable mode reserves204px for the inventory sidebar. */
 public static int barOffset(int width){int playWidth=width>765&&width<1500?width-204:width;return width>765?(playWidth-392)/2-12:0;}
 public static void draw(TextDrawingArea font,int selected,String[] state){SkillDragState drag=new SkillDragState();drag.selected=selected;draw(font,drag,state,-1,-1);}
 public static void draw(TextDrawingArea font,SkillDragState drag,String[] state,int mouseX,int mouseY){
  font=ExileTheme.body();box(16,16,480,306,ExileTheme.GOLD,ExileTheme.PANEL);ExileTheme.heading(28,38,"Arsenal");text(font,476,37,"X",0xC5CCD2);text(font,28,54,"Craft jewels, then drag gems into your loadout.",ExileTheme.MUTED);
  DrawingArea.drawHorizontalLine(28,59,456,ExileTheme.BORDER_SUBTLE);
  int hoverSlot=loadoutSlotAt(mouseX,mouseY);
  box(200,23,90,22,ExileTheme.BORDER,ExileTheme.OXBLOOD);text(font,216,38,"Controls",ExileTheme.TEXT);
  for(int row=0;row<3;row++){int skill=assignment(state,row),y=66+row*64;int border=hoverSlot==row?ExileTheme.GOLD:ExileTheme.BORDER;box(28,y,292,58,border,hoverSlot==row?ExileTheme.HOVER:ExileTheme.SURFACE);DrawingArea.drawPixels(54,y+2,30,skill<0?ExileTheme.BORDER:color(skill),2);if(skill>=0)glyph(font,skill,false,36,y+11,36);else{box(36,y+11,36,36,ExileTheme.BORDER,ExileTheme.PANEL);text(font,48,y+35,"+",ExileTheme.MUTED);}text(font,80,y+18,fit(font,skill<0?"Choose a skill":name(skill),110),skill<0?ExileTheme.DIM:color(skill));if(skill>=0){int power=(int)Math.round(new int[]{180,90,115,130,200}[skill]*(1+0.05*(level(state,skill)-1))*((mask(state,skill)&4)!=0?1.3+0.01*(value(state,10,1)-1):1));text(font,80,y+34,fit(font,"L"+level(state,skill)+" / "+power+"% damage",110),ExileTheme.TEXT);text(font,80,y+49,String.format(java.util.Locale.ROOT,"%.1f",new int[]{12,18,24,20,16}[skill]*(1+0.03*(level(state,skill)-1))*((mask(state,skill)&4)!=0?1.4:1)*((mask(state,skill)&1)!=0?1.2:1))+" mana / "+cooldown(state,skill)+"s",ExileTheme.MUTED);}else{text(font,80,y+39,"Drag a gem here",ExileTheme.MUTED);}
   int equipped=skill<0?0:mask(state,skill);for(int support=0;support<3;support++){int x=198+support*37;boolean on=(equipped&(1<<support))!=0;box(x,y+14,30,29,on?SUPPORT_COLORS[support]:ExileTheme.BORDER,on?ExileTheme.SELECTED:ExileTheme.PANEL);if(on)SkillGlyphs.icon(support,true,22).drawSprite(x+4,y+17);else text(font,x+10,y+34,"+",0x637884);}
  }
  box(336,70,148,174,ExileTheme.BORDER,ExileTheme.SURFACE_DARK);ExileTheme.heading(348,91,"Gemcraft",ExileTheme.GOLD);text(font,348,116,"Craft an uncut jewel",ExileTheme.TEXT);text(font,348,132,"to choose its power.",ExileTheme.TEXT);text(font,348,156,"Drag a skill to the",ExileTheme.TEXT);text(font,348,172,"large socket at left.",ExileTheme.TEXT);text(font,348,196,"Small sockets accept",ExileTheme.TEXT);text(font,348,212,"support gems.",ExileTheme.TEXT);text(font,348,232,"Drag back to remove",ExileTheme.MUTED);
  int described=hoverSlot>=0?assignment(state,hoverSlot):-1;String detail=described<0?"Three active sockets define your entire combat loadout.":new String[]{"Heavy Strike: a powerful hit against one enemy.","Cleave: sweep enemies in a forward cone.","Whirlwind: strike nearby enemies around your character.","Ground Slam: hit a cluster around your selected enemy.","Execute: double damage against enemies below 35% life."}[described];text(font,28,266,fit(font,detail,456),ExileTheme.MUTED);
  for(int slot=0;slot<3;slot++){int x=28+slot*153,skill=assignment(state,slot);box(x,276,144,31,0x7C7053,ExileTheme.OXBLOOD);text(font,x+8,288,fit(font,KEYS[slot],128),ExileTheme.MUTED);text(font,x+8,302,name(skill),color(skill));}
  text(font,28,317,"K  Close arsenal     /     Enter  Chat",ExileTheme.DIM);
  if(drag.dragging()){String label=drag.kind==5?NAMES[drag.skill]:SUPPORTS[drag.support];int gx=Math.max(20,Math.min(DrawingArea.width-110,mouseX+12)),gy=Math.max(20,Math.min(DrawingArea.height-30,mouseY+10));box(gx,gy,106,26,0xA4E3D4,0x263C43);text(font,gx+8,gy+18,label,0xE3F4EF);}
 }
 /** Resource fills report authoritative values; skill rectangles retain their input contract. */
 public static void drawBar(TextDrawingArea font,String[] state,String[] character){
  font=ExileTheme.small();int y=barTop(DrawingArea.width,DrawingArea.height),ox=barOffset(DrawingArea.width);
  box(ox+12,y,392,78,ExileTheme.BORDER,ExileTheme.PANEL);
  double[] life={100,100},shield={0,0};
  for(String line:character){if(line.startsWith("Life:"))life=resource(line.substring(5));if(line.startsWith("Energy Shield:"))shield=resource(line.substring(14));}
  resourceBar(font,ox+18,y+5,123,"Life",life,0x8D303B);
  resourceBar(font,ox+145,y+5,123,"Mana",resource((state.length>0?state[0]:"0")+"/"+(state.length>1?state[1]:"0")),0x315B91);
  resourceBar(font,ox+272,y+5,123,"Shield",shield,0x6A91A0);
  for(int slot=0;slot<3;slot++){
   int skill=assignment(state,slot),x=ox+18+slot*127;
   ExileTheme.inset(x,y+35,123,37,ExileTheme.BORDER,ExileTheme.SURFACE_DARK);
   if(skill>=0)SkillGlyphs.icon(skill,false,26).drawSprite(x+4,y+40);
   String remaining=skill<0?"":cooldown(state,skill);boolean cooling=!remaining.isEmpty()&&!remaining.equals("0.0")&&!remaining.equals("0");
   text(font,x+34,y+49,fit(font,KEYS[slot],cooling?46:82),ExileTheme.HEADING);
   text(font,x+34,y+64,fit(font,name(skill),82),color(skill));
   if(cooling)text(font,x+84,y+49,fit(font,remaining+"s",34),ExileTheme.TEXT);
  }
 }
 private static double[] resource(String text){try{String[] pair=text.trim().split("/");return new double[]{Double.parseDouble(pair[0].trim()),Double.parseDouble(pair[1].trim())};}catch(Exception malformed){return new double[]{0,0};}}
 private static void resourceBar(TextDrawingArea font,int x,int y,int width,String label,double[] amount,int fill){
  ExileTheme.inset(x,y,width,24,ExileTheme.BORDER,ExileTheme.SURFACE_DARK);
  int pixels=amount[1]<=0?0:(int)Math.round((width-4)*Math.max(0,Math.min(1,amount[0]/amount[1])));
  if(pixels>0){DrawingArea.drawPixels(20,y+2,x+2,fill,pixels);DrawingArea.drawHorizontalLine(x+2,y+2,pixels,ExileTheme.EDGE_LIGHT);}
  String caption=label+" "+(int)amount[0]+" / "+(int)amount[1];text(font,x+5,y+16,fit(font,caption,width-10),ExileTheme.TEXT);
 }
 private static void glyph(TextDrawingArea font,int id,boolean support,int x,int y,int size){int color=support?SUPPORT_COLORS[id]:COLORS[id];box(x,y,size,size,color,ExileTheme.SURFACE);SkillGlyphs.icon(id,support,size-4).drawSprite(x+2,y+2);}

 private static void box(int x,int y,int width,int height,int border,int fill){ExileTheme.panel(x,y,width,height,border,fill);}
 private static void text(TextDrawingArea font,int x,int y,String value,int color){ExileTheme.label(font,x,y,value,color);}
}



