from pathlib import Path
p=Path('Notorious/Client/src/com/koios/client/arpg/SkillPanel.java');p.write_text('''package com.koios.client.arpg;
import com.koios.client.graphic.*;
public final class SkillPanel {
 public static final String[] NAMES={"Heavy Strike","Cleave","Whirlwind"},SUPPORTS={"Splash","Faster Attacks","Brutality"},KEYS={"Q","W","E"};
 private static final int[] COLORS={0xD9B86C,0x7CB9CE,0xAD93DA},SUPPORT_COLORS={0x72B8DB,0x8FD0A0,0xD98B7D};
 private static int value(String[] state,int index,int fallback){try{return Integer.parseInt(state[index]);}catch(Exception ex){return fallback;}}
 public static int assignment(String[] state,int slot){return value(state,14+slot,slot);}
 public static int mask(String[] state,int skill){return value(state,skill==2?12:4+skill,0);}
 public static int level(String[] state,int skill){return value(state,skill==2?13:6+skill,1);}
 private static String cooldown(String[] state,int skill){int i=skill==2?11:2+skill;return i<state.length?state[i]:"0.0";}
 public static int skillAt(int x,int y){if(x<28||x>=320)return -1;for(int s=0;s<3;s++)if(y>=66+s*64&&y<124+s*64)return s;return -1;}
 public static int socketAt(int x,int y){int skill=skillAt(x,y);if(skill<0||y<80+skill*64||y>=109+skill*64)return -1;for(int i=0;i<3;i++)if(x>=198+i*37&&x<228+i*37)return i;return -1;}
 public static int libraryAt(int x,int y){if(x<336||x>=484)return -1;for(int i=0;i<3;i++)if(y>=70+i*49&&y<112+i*49)return i;return -1;}
 public static boolean inLibrary(int x,int y){return x>=330&&x<490&&y>=64&&y<254;}
 public static int hotbarAt(int x,int y){if(y<276||y>=307)return -1;for(int i=0;i<3;i++)if(x>=28+i*153&&x<172+i*153)return i;return -1;}
 public static void draw(TextDrawingArea font,int selected,String[] state){SkillDragState drag=new SkillDragState();drag.selected=selected;draw(font,drag,state,-1,-1);}
 public static void draw(TextDrawingArea font,SkillDragState drag,String[] state,int mouseX,int mouseY){
  box(16,16,480,306,0xB29A69,0x10191F);text(font,28,37,"SKILLS / LOADOUT",0xE9CB89);text(font,476,37,"X",0xC5CCD2);text(font,28,54,"Drop supports onto skills. Drag skills onto the hotbar.",0x9BAAB4);
  int hover=skillAt(mouseX,mouseY);
  for(int skill=0;skill<3;skill++){int y=66+skill*64;boolean target=drag.dragging()&&(drag.kind==2||drag.kind==3)&&hover==skill;int border=target?0x8DE5C9:drag.selected==skill?0xD9B86C:0x425463;box(28,y,292,58,border,0x18232C);glyph(font,skill,false,36,y+11,36);text(font,80,y+18,NAMES[skill],COLORS[skill]);int power=(int)Math.round((skill==0?180:skill==1?90:115)*(1+0.05*(level(state,skill)-1)));text(font,80,y+34,"L"+level(state,skill)+" / "+power+"% damage",0xC3CED7);text(font,80,y+49,cooldown(state,skill)+"s cooldown",0x93A9BA);
   int equipped=mask(state,skill);for(int support=0;support<3;support++){int x=198+support*37;boolean on=(equipped&(1<<support))!=0;box(x,y+14,30,29,on?SUPPORT_COLORS[support]:0x425463,on?0x253A42:0x10191F);text(font,x+7,y+34,on?new String[]{"SP","FA","BR"}[support]:"+",on?SUPPORT_COLORS[support]:0x637884);}text(font,198,y+53,"SUPPORT SOCKETS",0x6F899B);
  }
  for(int support=0;support<3;support++){int y=70+support*49;box(336,y,148,42,libraryAt(mouseX,mouseY)==support?0xD9B86C:0x425463,0x1B2932);glyph(font,support,true,342,y+7,28);text(font,377,y+17,SUPPORTS[support],SUPPORT_COLORS[support]);text(font,377,y+33,"Support L"+value(state,8+support,1),0x8BA3B5);}
  text(font,336,231,"Drop attached supports",0x9BAAB4);text(font,336,245,"here to remove them.",0x9BAAB4);
  String detail="Drag skill tiles onto Q/W/E to swap assignments.";int lib=libraryAt(mouseX,mouseY);if(lib>=0)detail=new String[]{"Splash: nearby damage / larger Whirlwind radius.","Faster Attacks: faster skill use and shorter cooldowns.","Brutality: more physical damage, higher mana cost."}[lib];text(font,28,266,detail,0xA7BBC8);
  for(int slot=0;slot<3;slot++){int x=28+slot*153,skill=assignment(state,slot);boolean target=drag.dragging()&&(drag.kind==1||drag.kind==4)&&hotbarAt(mouseX,mouseY)==slot;box(x,276,144,31,target?0x8DE5C9:0x7C7053,0x25313B);text(font,x+8,296,KEYS[slot]+"  "+NAMES[skill],COLORS[skill]);}
  text(font,28,317,"Drag outside to cancel / K closes / Enter opens chat",0x7E95A6);
  if(drag.dragging()){String label=drag.kind==1||drag.kind==4?NAMES[drag.skill]:SUPPORTS[drag.support];int gx=Math.max(20,Math.min(DrawingArea.width-110,mouseX+12)),gy=Math.max(20,Math.min(DrawingArea.height-30,mouseY+10));box(gx,gy,106,26,0xA4E3D4,0x263C43);text(font,gx+8,gy+18,label,0xE3F4EF);}
 }
 public static void drawBar(TextDrawingArea font,String[] state,String[] character){int y=DrawingArea.height-69;box(12,y,392,60,0x58647A,0x10191F);String life="Life: 100 / 100",es="Energy Shield: 0 / 0";for(String line:character){if(line.startsWith("Life:"))life=line;if(line.startsWith("Energy Shield:"))es=line;}text(font,22,y+14,life+"   "+es,0xD6D9E0);text(font,22,y+29,"Mana "+state[0]+"/"+state[1]+"  /  K: drag skills & supports",0x8DC5FF);for(int slot=0;slot<3;slot++){int skill=assignment(state,slot);text(font,22+slot*127,y+48,KEYS[slot]+" "+NAMES[skill]+" "+cooldown(state,skill)+"s",COLORS[skill]);}}
 private static void glyph(TextDrawingArea font,int id,boolean support,int x,int y,int size){int color=support?SUPPORT_COLORS[id]:COLORS[id];box(x,y,size,size,color,0x263640);String symbol=(support?new String[]{"SP","FA","BR"}:new String[]{"HS","CL","WW"})[id];text(font,x+size/2-6,y+size/2+4,symbol,color);}
 private static void box(int x,int y,int width,int height,int border,int fill){DrawingArea.drawPixels(height,y,x,fill,width);DrawingArea.fillPixels(x,width,height,border,y);}
 private static void text(TextDrawingArea font,int x,int y,String value,int color){font.method389(true,x,color,value,y);}
}
''')
p=Path('Notorious/Client/src/com/koios/client/Client.java');s=p.read_text();a=s.index('            if(arpgSkillsOpen && super.clickMode3==1){');b=s.index('\n        }\n\n\t\trefreshFrameSize',a);s=s[:a]+'''            if(arpgSkillsOpen && super.clickMode3==1){
                int px=super.saveClickX-(frameMode==ScreenMode.FIXED?4:0),py=super.saveClickY-(frameMode==ScreenMode.FIXED?4:0);
                if(px>=460&&px<=496&&py>=16&&py<48){arpgSkillsOpen=false;skillDrag.cancel();}
                else if(awtFocus)skillDrag.begin(px,py,arpgSkillState);
                super.clickMode3=0;
            }'''+s[b:]
needle='\n\t\trefreshFrameSize();';idx=s.index(needle,s.index('private void mainGameProcessor()'));s=s[:idx]+'''
        if(arpgSkillsOpen&&awtFocus&&!messagePromptRaised&&inputDialogState==0&&backDialogID==-1&&openInterfaceID==-1){
            int offset=frameMode==ScreenMode.FIXED?4:0;
            if(skillDrag.active()){if(super.clickMode2==1)skillDrag.move(mouseX-offset,mouseY-offset);else{String change=skillDrag.release(super.releasedX-offset,super.releasedY-offset);if(change!=null)sendArpgSkill(change);}}
        }else skillDrag.cancel();
'''+s[idx:]
s=s.replace('sendArpgSkill("cast 0")','sendArpgSkill("slot 0")').replace('sendArpgSkill("cast 1")','sendArpgSkill("slot 1")').replace('sendArpgSkill("cast 2")','sendArpgSkill("slot 2")');s=s.replace('com.koios.client.arpg.SkillPanel.draw(aTextDrawingArea_1271,arpgSelectedSkill,arpgSkillState);','com.koios.client.arpg.SkillPanel.draw(aTextDrawingArea_1271,skillDrag,arpgSkillState,mouseX-(frameMode==ScreenMode.FIXED?4:0),mouseY-(frameMode==ScreenMode.FIXED?4:0));');s=s.replace('    private int arpgSelectedSkill;','    private final com.koios.client.arpg.SkillDragState skillDrag=new com.koios.client.arpg.SkillDragState();');s=s.replace('mapObjective.clear();arpgHazardX=-1;','mapObjective.clear();arpgHazardX=-1;skillDrag.cancel();');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/ArpgSkills.java');s=p.read_text().replace('  String[] args=input.split(" ");','  String[] args=input.split(" ");if(args.length<2)return true;');p.write_text(s)
