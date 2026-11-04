package com.koios.client.arpg;
import com.koios.client.graphic.*;

public final class MapObjective {
 private String[] lines=new String[0];
 public void accept(String payload){String text=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8);lines=text.isEmpty()?new String[0]:text.split("\n");}
 public void clear(){lines=new String[0];}
 public boolean isVisible(){return lines.length>0;}
 public java.awt.Rectangle bounds(TextDrawingArea font){int width=278,count=0;for(String line:lines)count+=ChatWrap.lines(line,width-16,font::getTextWidth).size();return new java.awt.Rectangle(Math.max(8,DrawingArea.width-290),DrawingArea.width>765?184:12,width,lines.length==0?0:14+count*15);}
 public void draw(TextDrawingArea font){drawAt(font,Math.max(8,DrawingArea.width-290),DrawingArea.width>765?184:12,278);}
 /** Pin instructions below an open panel, leaving the tree and chat input clear. */
 public void drawInChat(TextDrawingArea font,int yOffset){drawAt(font,12,yOffset+10,490);}
 private void drawAt(TextDrawingArea font,int x,int y,int width){
  if(lines.length==0)return;
  java.util.List<String> wrapped=new java.util.ArrayList<>();
  for(String line:lines)wrapped.addAll(ChatWrap.lines(line,width-16,font::getTextWidth));
  int height=14+wrapped.size()*15;
  int accent=lines[0].startsWith("MAP COMPLETE")?0x93DB9B:0xE3C486;
  ExileTheme.panel(x,y,width,height,accent,ExileTheme.PANEL);
  DrawingArea.drawPixels(Math.max(1,height-6),y+3,x+3,accent,2);
  for(int i=0;i<wrapped.size();i++)ExileTheme.label(font,x+10,y+17+i*15,wrapped.get(i),i==0?accent:ExileTheme.TEXT);
 }
}
