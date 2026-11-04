from pathlib import Path
r=Path('Notorious/Client/src/com/koios/client')
p=r/'definition/ItemDefinition.java';s=p.read_text().replace('i<22406','i<22407').replace('575,567,573,571,569,970','575,567,573,571,569,970,970').replace('"Scroll of Wisdom"};','"Scroll of Wisdom","Portal Scroll"};').replace('"Identifies unidentified equipment or maps."};','"Identifies unidentified equipment or maps.","Read inside a map to open a return portal to your base."};').replace('itemDef.itemActions=new String[]{null,null,null,null,"Drop"};itemDef.groundActions=', 'itemDef.itemActions=new String[]{i==22406?"Read":null,null,null,null,"Drop"};itemDef.groundActions=');p.write_text(s)
p=r/'arpg/CurrencyIcons.java';s=p.read_text().replace('new Sprite[6]','new Sprite[7]').replace('0xB34548,0xDCC59A','0xB34548,0xDCC59A,0x8FBFE4').replace('n==5?','n>=5?').replace('  if(n==5){','  if(n==6){line(s,9,7,23,7,0x304B82);line(s,9,25,23,25,0x304B82);line(s,12,12,20,12,0xE6F3FF);line(s,20,12,20,21,0xE6F3FF);line(s,20,21,12,21,0xE6F3FF);line(s,12,21,12,12,0xE6F3FF);}\n  else if(n==5){');p.write_text(s)
p=r/'definition/ObjectDefinition.java';s=p.read_text();idx=s.index('\t\treturn class46;',s.index('public static ObjectDefinition forID'));# actualfirstreturninside? inspect after
# apply at final return before next method
idx=s.index('\n\t\treturn class46;\n\t}',s.index('public static ObjectDefinition forID'))
s=s[:idx]+'''\n        if(i==2465||i==2466){class46.name=i==2465?"Return portal":"Map portal";class46.actions=new String[]{i==2465?"Return-to-base":"Re-enter-map",null,null,null,null};}
'''+s[idx:];p.write_text(s)
p=r/'arpg/MapObjective.java';p.write_text('''package com.koios.client.arpg;
import com.koios.client.graphic.*;
public final class MapObjective {
 private String[] lines=new String[0];
 public void accept(String payload){String text=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8);lines=text.isEmpty()?new String[0]:text.split("\\n");}
 public void clear(){lines=new String[0];}
 public void draw(TextDrawingArea font){if(lines.length==0)return;int x=Math.max(8,DrawingArea.width-290),y=12,w=278,h=14+lines.length*15;boolean complete=lines[0].startsWith("MAP COMPLETE");int accent=complete?0x93DB9B:0xE3C486;DrawingArea.drawPixels(h,y,x,0x101A22,w);DrawingArea.fillPixels(x,w,h,accent,y);for(int i=0;i<lines.length;i++){String text=lines[i];if(text.length()>47)text=text.substring(0,47);font.method389(true,x+8,i==0?accent:0xD5DFE5,text,y+17+i*15);}}
}
''')
p=r/'Client.java';s=p.read_text().replace('chatFocus.close();arpgSkillsOpen=false;prototypeOpen=false;','chatFocus.close();arpgSkillsOpen=false;prototypeOpen=false;mapObjective.clear();');s=s.replace('if(openInterfaceID==-1)com.koios.client.arpg.SkillPanel.drawBar(aTextDrawingArea_1271,arpgSkillState,arpgCharacterLines);','if(openInterfaceID==-1){com.koios.client.arpg.SkillPanel.drawBar(aTextDrawingArea_1271,arpgSkillState,arpgCharacterLines);mapObjective.draw(aTextDrawingArea_1271);}')
s=s.replace('if(frame==65531)', 'if(frame==65530){mapObjective.accept(text);pktType=-1;return true;}\n                    if(frame==65531)');s=s.replace('    private boolean arpgSkillsOpen,prototypeOpen;', '    private final com.koios.client.arpg.MapObjective mapObjective=new com.koios.client.arpg.MapObjective();\n    private boolean arpgSkillsOpen,prototypeOpen;');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/MapRuns.java');s=p.read_text().replace('set(slot,null);enter(p);p.sendMessage','set(slot,null);enter(p);openPortal(p,run.spawn);p.sendMessage');p.write_text(s)
