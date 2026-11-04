from pathlib import Path
r=Path('Notorious/Client/src/com/koios/client')
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a[:50]);p.write_text(s.replace(a,b))
(r/'arpg/PrototypePanel.java').write_text('''package com.koios.client.arpg;
import com.koios.client.graphic.*;
public final class PrototypePanel {
 private String[] lines={"guide"};
 public void accept(String payload){lines=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8).split("\\n");}
 public String actionAt(int x,int y,boolean control){
  if(x<16||x>496||y<16||y>322)return null;
  if(y<46){int tab=Math.min(4,Math.max(0,(x-24)/92));return new String[]{"guide","tree","craft","maps","home"}[tab];}
  if(lines[0].startsWith("tree|")){
   if(y>295)return "free";
   int col=(x-28)/150,row=(y-66)/28;if(col>=0&&col<3&&row>=0&&row<7&&y>=66){int id=col*7+row+1;return (control?"refund ":"allocate ")+id;}return null;
  }
  int row=(y-59)/12+1;if(y>=59&&row<lines.length){String[] entry=lines[row].split("\\t",2);return entry.length==2&&!entry[0].isEmpty()?entry[0]:null;}return null;
 }
 public void draw(TextDrawingArea font,int mouseX,int mouseY){
  DrawingArea.drawPixels(306,16,16,0x10171E,480);DrawingArea.fillPixels(16,480,306,0xAE9460,16);
  String[] tabs={"Guide","Passives","Crafting","Maps","House base"};for(int i=0;i<tabs.length;i++)text(font,27+i*92,36,tabs[i],0xE8CD86);
  DrawingArea.method339(44,0x58616C,464,24);
  if(lines[0].startsWith("tree|")){
   String[] head=lines[0].split("\\\\|");text(font,30,59,"Blade",0xD6D9E0);text(font,180,59,"Survival",0xD6D9E0);text(font,330,59,"Energy",0xD6D9E0);
   for(int i=1;i<lines.length;i++){
    String[] f=lines[i].split("\\\\|",5);if(f.length<5)continue;int id=Integer.parseInt(f[0]),col=(id-1)/7,row=(id-1)%7,x=28+col*150,y=66+row*28;boolean owned=Boolean.parseBoolean(f[2]);
    DrawingArea.drawPixels(24,y,x,owned?0x294441:0x202730,140);DrawingArea.fillPixels(x,140,24,owned?0x8AD8B7:0x65717A,y);text(font,x+5,y+16,f[3],owned?0xABF1CE:0xD6D9E0);
    if(mouseX>=x&&mouseX<x+140&&mouseY>=y&&mouseY<y+24){String detail="Needs "+f[1]+" / "+f[4];text(font,24,286,detail.length()>79?detail.substring(0,79):detail,0xE8CD86);}
   }
   text(font,24,307,"Points: "+head[1]+" / Ctrl-click refund / Free test refunds: "+head[2],0x939FAA);
  }else{
   for(int i=1;i<lines.length&&i<=20;i++){String[] row=lines[i].split("\\t",2);String label=row.length==2?row[1]:row[0];if(label.length()>78)label=label.substring(0,78);text(font,27,66+(i-1)*12,label,row.length==2&&!row[0].isEmpty()?0x9BCDF3:0xA5ADB5);}
   text(font,27,309,"Click an option. P / Escape closes. Enter opens chat.",0x939FAA);
  }
 }
 private void text(TextDrawingArea font,int x,int y,String text,int color){font.method389(true,x,color,text,y);}
}
''')
p=r/'arpg/ChatFocus.java';edit(p,'PANEL, IGNORE','PANEL, PROTOTYPE, IGNORE');edit(p,"if(key=='k'||key=='K')return Action.PANEL;","if(key=='k'||key=='K')return Action.PANEL;\n  if(key=='p'||key=='P')return Action.PROTOTYPE;")
p=r/'Client.java';edit(p,'private boolean arpgSkillsOpen;', 'private boolean arpgSkillsOpen,prototypeOpen;\n    private final com.koios.client.arpg.PrototypePanel prototypePanel=new com.koios.client.arpg.PrototypePanel();')
edit(p,'chatFocus.close();arpgSkillsOpen=false;','chatFocus.close();arpgSkillsOpen=false;prototypeOpen=false;')
edit(p,'if(arpgSkillsOpen && super.clickMode3==1){','if(prototypeOpen&&super.clickMode3==1){int px=super.saveClickX-(frameMode==ScreenMode.FIXED?4:0),py=super.saveClickY-(frameMode==ScreenMode.FIXED?4:0);String action=prototypePanel.actionAt(px,py,arpgControlDown);if(action!=null){sendPrototype(action);if(action.startsWith("open ")||action.equals("intro")||action.equals("resume"))prototypeOpen=false;}super.clickMode3=0;}\n            if(arpgSkillsOpen && super.clickMode3==1){')
edit(p,'if(arpgSkillsOpen){arpgHoverWidget=-1;', 'if(prototypeOpen||arpgSkillsOpen){arpgHoverWidget=-1;')
edit(p,'arpgSkillsOpen=false;continue;}','arpgSkillsOpen=false;prototypeOpen=false;continue;}')
edit(p,'if(action==com.koios.client.arpg.ChatFocus.Action.PANEL)', 'if(action==com.koios.client.arpg.ChatFocus.Action.PROTOTYPE){if(openInterfaceID==-1){prototypeOpen=!prototypeOpen;arpgSkillsOpen=false;if(prototypeOpen)sendPrototype("guide");}continue;}\n                if(action==com.koios.client.arpg.ChatFocus.Action.PANEL)')
edit(p,'int frame = inStream.method435();','int frame = inStream.method435();\n                    if(frame==65531){prototypePanel.accept(text);prototypeOpen=true;arpgSkillsOpen=false;pktType=-1;return true;}')
edit(p,'private void sendArpgSkill(String action){','private void sendPrototype(String action){if(!loggedIn||openInterfaceID!=-1)return;String command="arpgproto "+action;stream.createFrame(103);stream.writeLEShort(command.length()+1);stream.writeString(command);}\n    private void sendArpgSkill(String action){')
edit(p,'private void drawTooltip() {','private void drawTooltip() {\n        if(prototypeOpen){prototypePanel.draw(aTextDrawingArea_1271,mouseX-(frameMode==ScreenMode.FIXED?4:0),mouseY-(frameMode==ScreenMode.FIXED?4:0));return;}')
p=r/'ui/RSApplet.java';edit(p,'i==KeyEvent.VK_K&&Client.instance', '(i==KeyEvent.VK_K||i==KeyEvent.VK_P)&&Client.instance')
