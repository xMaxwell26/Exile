package com.koios.client.arpg;
import com.koios.client.graphic.*;
/** Full stat summary with paired compact defences; native font rows retain breathing room. */
public final class CharacterPanel {
 public static java.util.List<String> rows(String[] lines){
  java.util.List<String> rows=new java.util.ArrayList<>();for(int i=0;i<lines.length;i++){String line=lines[i];if(i+1<lines.length&&line.startsWith("Armour:")&&lines[i+1].startsWith("Evasion:")){line+=" / Ev:"+lines[++i].substring(8);}else if(i+1<lines.length&&line.startsWith("Fire Resist:")&&lines[i+1].startsWith("Cold Resist:")){line="Fire:"+line.substring(12)+" / Cold:"+lines[++i].substring(12);}else if(i+1<lines.length&&line.startsWith("Lightning Resist:")&&lines[i+1].startsWith("Chaos Resist:")){line="Light:"+line.substring(17)+" / Chaos:"+lines[++i].substring(13);}rows.add(line);}
  return rows;
 }
 public static void draw(TextDrawingArea font,int x,int y,String[] lines){
  DrawingArea.drawPixels(260,y,x,ExileTheme.PANEL,190);font.method389(true,x+10,ExileTheme.HEADING,"CHARACTER",y+18);DrawingArea.method339(y+26,0x59616B,170,x+10);
  java.util.List<String> rows=rows(lines);
  int row=y+42;for(int i=0;i<rows.size()&&row<y+254;i++,row+=14){String text=rows.get(i);while(font.getTextWidth(text)>172&&text.length()>3)text=text.substring(0,text.length()-4)+"...";font.method389(true,x+9,i<3?ExileTheme.HEADING:ExileTheme.TEXT,text,row);}
 }
}

