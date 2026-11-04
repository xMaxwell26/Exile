package com.koios.client.arpg;
import com.koios.client.graphic.*;

public final class MapObjective {
 private String[] lines=new String[0];
 public void accept(String payload){String text=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8);lines=text.isEmpty()?new String[0]:text.split("\n");}
 public void clear(){lines=new String[0];}
 public boolean isVisible(){return lines.length>0;}
 private static boolean narrow(){return DrawingArea.width>765&&DrawingArea.width<1150;}
 private static int left(){return narrow()?12:Math.max(8,DrawingArea.width-290);}
 private static int top(){return DrawingArea.width>765&&!narrow()?184:12;}
 public java.awt.Rectangle bounds(TextDrawingArea font){int width=278,count=0;for(String line:lines)count+=ChatWrap.lines(line,width-24,font::getTextWidth).size();return new java.awt.Rectangle(left(),top(),width,lines.length==0?0:14+count*15);}
 public void draw(TextDrawingArea font){drawAt(font,left(),top(),278);}
 /** Pin instructions below an open panel, leaving the tree and chat input clear. */
 public void drawInChat(TextDrawingArea font,int yOffset){drawAt(font,12,yOffset+10,490);}
 private void drawAt(TextDrawingArea font,int x,int y,int width){
  if(lines.length==0)return;
  java.util.List<String> wrapped=new java.util.ArrayList<>();
  for(String line:lines)wrapped.addAll(ChatWrap.lines(line,width-24,font::getTextWidth));
  int height=14+wrapped.size()*15;
  int accent=lines[0].startsWith("MAP COMPLETE")?0x93DB9B:0xE3C486;
  ExileTheme.panel(x,y,width,height,ExileTheme.BORDER,ExileTheme.PANEL);
  DrawingArea.drawPixels(Math.max(1,height-12),y+6,x+3,accent,1);
  for(int i=0;i<wrapped.size();i++){String line=wrapped.get(i);if(i==0){line=line.replace("TUTORIAL","Journey").replace("MAP COMPLETE","Map complete");ExileTheme.label(font,x+12,y+17,line,accent);}else ExileTheme.label(font,x+12,y+17+i*15,line,ExileTheme.TEXT);}
 }
}
