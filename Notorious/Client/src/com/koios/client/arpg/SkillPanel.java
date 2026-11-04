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
 public static int skillAt(int x,int y){return skillAt(x,y,0);}
 public static int skillAt(int x,int y,int page){if(x<28||x>=320)return -1;for(int s=0;s<3;s++)if(y>=66+s*64&&y<124+s*64)return page*3+s<NAMES.length?page*3+s:-1;return -1;}
 public static int socketAt(int x,int y){int skill=skillAt(x,y);if(skill<0||y<80+skill*64||y>=109+skill*64)return -1;for(int i=0;i<3;i++)if(x>=198+i*37&&x<228+i*37)return i;return -1;}
 public static int libraryAt(int x,int y){if(x<336||x>=484)return -1;for(int i=0;i<3;i++)if(y>=70+i*49&&y<112+i*49)return i;return -1;}
 public static boolean inLibrary(int x,int y){return x>=330&&x<490&&y>=64&&y<254;}
 public static int hotbarAt(int x,int y){if(y<276||y>=307)return -1;for(int i=0;i<3;i++)if(x>=28+i*153&&x<172+i*153)return i;return -1;}
 public static int barSlotAt(int x,int y,int height){return barSlotAt(x,y,512,height);}
 public static int barSlotAt(int x,int y,int width,int height){int top=barTop(width,height),ox=barOffset(width);if(y<top+35||y>=top+72)return -1;for(int slot=0;slot<3;slot++)if(x>=ox+18+slot*127&&x<ox+141+slot*127)return slot;return -1;}
 public static int barTop(int width,int height){return height-(width>765&&width<1500?253:87);}
 public static int barOffset(int width){return width>765?(width-392)/2-12:0;}
 public static void draw(TextDrawingArea font,int selected,String[] state){SkillDragState drag=new SkillDragState();drag.selected=selected;draw(font,drag,state,-1,-1);}
 public static void draw(TextDrawingArea font,SkillDragState drag,String[] state,int mouseX,int mouseY){
  box(16,16,480,306,ExileTheme.GOLD,ExileTheme.PANEL);text(font,28,37,"SKILLS / LOADOUT",ExileTheme.HEADING);text(font,476,37,"X",0xC5CCD2);text(font,28,54,"Drop supports onto skills. Empty slots are allowed.",ExileTheme.MUTED);
  box(302,23,112,22,ExileTheme.BORDER,ExileTheme.OXBLOOD);text(font,310,38,drag.page==0?"More skills  >":"<  Previous",ExileTheme.GOLD);int hover=skillAt(mouseX,mouseY,drag.page);
  box(200,23,90,22,ExileTheme.BORDER,ExileTheme.OXBLOOD);text(font,216,38,"Controls",ExileTheme.TEXT);
  for(int row=0;row<3;row++){int skill=drag.page*3+row;if(skill>=NAMES.length)break;int y=66+row*64;boolean target=drag.dragging()&&(drag.kind==2||drag.kind==3)&&hover==skill;int border=target?0x8DE5C9:drag.selected==skill?ExileTheme.GOLD:ExileTheme.BORDER;box(28,y,292,58,border,ExileTheme.SURFACE);glyph(font,skill,false,36,y+11,36);text(font,80,y+18,name(skill),color(skill));int power=(int)Math.round(new int[]{180,90,115,130,200}[skill]*(1+0.05*(level(state,skill)-1))*((mask(state,skill)&4)!=0?1.3+0.01*(value(state,10,1)-1):1));text(font,80,y+34,"L"+level(state,skill)+" / "+power+"% damage",ExileTheme.TEXT);text(font,80,y+49,String.format(java.util.Locale.ROOT,"%.1f",new int[]{12,18,24,20,16}[skill]*(1+0.03*(level(state,skill)-1))*((mask(state,skill)&4)!=0?1.4:1)*((mask(state,skill)&1)!=0?1.2:1))+" mana / "+cooldown(state,skill)+"s",ExileTheme.MUTED);
   int equipped=mask(state,skill);for(int support=0;support<3;support++){int x=198+support*37;boolean on=(equipped&(1<<support))!=0;box(x,y+14,30,29,on?SUPPORT_COLORS[support]:ExileTheme.BORDER,on?ExileTheme.SELECTED:ExileTheme.PANEL);if(on)SkillGlyphs.icon(support,true,22).drawSprite(x+4,y+17);else text(font,x+10,y+34,"+",0x637884);}
  }
  for(int support=0;support<3;support++){int y=70+support*49;box(336,y,148,42,available(state,support)==0?ExileTheme.BORDER_SUBTLE:libraryAt(mouseX,mouseY)==support?ExileTheme.GOLD:ExileTheme.BORDER,ExileTheme.SURFACE);glyph(font,support,true,342,y+7,28);text(font,377,y+17,SUPPORTS[support],owned(state,support)>0?SUPPORT_COLORS[support]:ExileTheme.DIM);text(font,377,y+33,owned(state,support)==0?"Find support gem":available(state,support)==0?"All copies socketed":"L"+value(state,8+support,1)+" / Free: "+available(state,support),ExileTheme.MUTED);}
  text(font,336,231,"Find gems in maps.",ExileTheme.MUTED);text(font,336,245,"Learn them from inventory.",ExileTheme.MUTED);
  int described=hover>=0?hover:drag.selected/3==drag.page?drag.selected:drag.page*3;String detail=new String[]{"Heavy Strike: a powerful hit against one enemy.","Cleave: sweep enemies in a forward cone.","Whirlwind: strike nearby enemies around your character.","Ground Slam: hit a cluster around your selected enemy.","Execute: double damage against enemies below 35% life."}[described];int lib=libraryAt(mouseX,mouseY);if(lib>=0)detail=new String[]{"Splash: nearby damage / larger Whirlwind radius.","Faster Attacks: faster skill use and shorter cooldowns.","Brutality: more physical damage, higher mana cost."}[lib];text(font,28,266,detail,ExileTheme.MUTED);
  for(int slot=0;slot<3;slot++){int x=28+slot*153,skill=assignment(state,slot);boolean target=drag.dragging()&&(drag.kind==1||drag.kind==4)&&hotbarAt(mouseX,mouseY)==slot;box(x,276,144,31,target?0x8DE5C9:0x7C7053,ExileTheme.OXBLOOD);text(font,x+8,288,fit(font,KEYS[slot],128),ExileTheme.MUTED);text(font,x+8,302,name(skill),color(skill));}
  text(font,28,317,"Drag hotbar skills out to remove / K closes / Enter chats",ExileTheme.DIM);
  if(drag.dragging()){String label=drag.kind==1||drag.kind==4?NAMES[drag.skill]:SUPPORTS[drag.support];int gx=Math.max(20,Math.min(DrawingArea.width-110,mouseX+12)),gy=Math.max(20,Math.min(DrawingArea.height-30,mouseY+10));box(gx,gy,106,26,0xA4E3D4,0x263C43);text(font,gx+8,gy+18,label,0xE3F4EF);}
 }
 public static void drawBar(TextDrawingArea font,String[] state,String[] character){int y=barTop(DrawingArea.width,DrawingArea.height),ox=barOffset(DrawingArea.width);box(ox+12,y,392,78,ExileTheme.BORDER,ExileTheme.PANEL);String life="Life: 100 / 100",es="Energy Shield: 0 / 0";for(String line:character){if(line.startsWith("Life:"))life=line;if(line.startsWith("Energy Shield:"))es=line;}text(font,ox+22,y+14,life+"   "+es,ExileTheme.TEXT);text(font,ox+22,y+29,"Mana "+state[0]+"/"+state[1]+"  /  Skills & controls: K or mana orb",0x8DC5FF);for(int slot=0;slot<3;slot++){int skill=assignment(state,slot);box(ox+18+slot*127,y+35,123,37,ExileTheme.BORDER,ExileTheme.SURFACE);text(font,ox+24+slot*127,y+49,fit(font,KEYS[slot]+(skill<0?"":" / "+cooldown(state,skill)+"s"),111),ExileTheme.MUTED);text(font,ox+24+slot*127,y+65,name(skill),color(skill));}}
 private static void glyph(TextDrawingArea font,int id,boolean support,int x,int y,int size){int color=support?SUPPORT_COLORS[id]:COLORS[id];box(x,y,size,size,color,ExileTheme.SURFACE);SkillGlyphs.icon(id,support,size-4).drawSprite(x+2,y+2);}

 private static void box(int x,int y,int width,int height,int border,int fill){DrawingArea.drawPixels(height,y,x,fill,width);DrawingArea.fillPixels(x,width,height,border,y);}
 private static void text(TextDrawingArea font,int x,int y,String value,int color){font.method389(true,x,color,value,y);}
}



