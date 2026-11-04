package com.koios.client.arpg;
import com.koios.client.graphic.*;
/** Full stat summary with paired compact defences; native font rows retain breathing room. */
public final class CharacterPanel {
 public static java.util.List<String> rows(String[] lines){
  java.util.List<String> rows=new java.util.ArrayList<>();for(int i=0;i<lines.length;i++){String line=lines[i];if(i+1<lines.length&&line.startsWith("Armour:")&&lines[i+1].startsWith("Evasion:")){
   // Large legitimate defences need separate rows; never hide the second value.
   String paired=line+" / Ev:"+lines[i+1].substring(8);
   if(ExileTheme.small().getTextWidth(paired)<=172){line=paired;i++;}
  }else if(i+1<lines.length&&line.startsWith("Fire Resist:")&&lines[i+1].startsWith("Cold Resist:")){line="Fire:"+line.substring(12)+" / Cold:"+lines[++i].substring(12);}else if(i+1<lines.length&&line.startsWith("Lightning Resist:")&&lines[i+1].startsWith("Chaos Resist:")){line="Light:"+line.substring(17)+" / Chaos:"+lines[++i].substring(13);}rows.add(compact(line));}
  return rows;
 }
 /** Shorten labels before values. Current/max and damage ranges must remain exact. */
 private static String compact(String text){
  if(ExileTheme.small().getTextWidth(text)<=172)return text;
  String[][] labels={{"Energy Shield:","ES:"},{"Physical:","Phys:"},{"Attack Speed:","Speed:"},{"Critical Chance:","Crit:"},{"Crit Multiplier:","Crit Multi:"}};
  for(String[] label:labels)if(text.startsWith(label[0]))return label[1]+text.substring(label[0].length());
  return text;
 }
 public static void draw(TextDrawingArea font,int x,int y,String[] lines){
  font=ExileTheme.small();ExileTheme.panel(x,y,190,260,ExileTheme.BORDER,ExileTheme.PANEL);ExileTheme.heading(x+10,y+20,"Character");DrawingArea.method339(y+26,ExileTheme.GOLD,170,x+10);
  java.util.List<String> rows=rows(lines);
  int row=y+42;for(int i=0;i<rows.size()&&row<y+254;i++,row+=14){String text=rows.get(i);if(i>=3 && i%2==1)DrawingArea.drawPixels(14,row-11,x+5,ExileTheme.SURFACE_DARK,180);if(i==3)DrawingArea.drawHorizontalLine(x+9,row-13,172,ExileTheme.BORDER_SUBTLE);while(font.getTextWidth(text)>172&&text.length()>3)text=text.substring(0,text.length()-4)+"...";ExileTheme.label(font,x+9,row,text,i<3?ExileTheme.HEADING:ExileTheme.TEXT);}
 }
}

