package com.koios.client.arpg;
import com.koios.client.graphic.*;

public final class MapObjective {
 private String[] lines=new String[0];
 public void accept(String payload){String text=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8);lines=text.isEmpty()?new String[0]:text.split("\n");}
 public void clear(){lines=new String[0];}
 public boolean isVisible(){return lines.length>0;}
 private static final class Layout {
  final java.awt.Rectangle bounds;final java.util.List<String> title,body;final int headerHeight;
  Layout(java.awt.Rectangle bounds,java.util.List<String> title,java.util.List<String> body,int headerHeight){this.bounds=bounds;this.title=title;this.body=body;this.headerHeight=headerHeight;}
 }
 private Layout layout(TextDrawingArea font,int x,int y,int width,int maxHeight){
  width=Math.min(width,DrawingArea.width-x-8);java.util.List<String> title=new java.util.ArrayList<>(),body=new java.util.ArrayList<>();
  if(lines.length>0)title.addAll(ChatWrap.lines(lines[0].replace("TUTORIAL","Journey").replace("MAP COMPLETE","Map complete"),width-28,ExileTheme::headingWidth));
  for(int i=1;i<lines.length;i++)body.addAll(ChatWrap.lines(lines[i],width-28,font::getTextWidth));
  int header=14+title.size()*21,height=header+12+body.size()*16+8;
  return new Layout(new java.awt.Rectangle(x,y,width,lines.length==0?0:Math.min(height,maxHeight)),title,body,header);
 }
 private Layout worldLayout(TextDrawingArea font){
  int width=Math.min(278,DrawingArea.width-24);Layout box=layout(font,Math.max(12,DrawingArea.width-width-12),12,width,DrawingArea.height-24);
  if(DrawingArea.width>765){int rightAvailable=DrawingArea.height-336-184-12;if(DrawingArea.width>=1150&&box.bounds.height<=rightAvailable)box=layout(font,DrawingArea.width-width-12,184,width,rightAvailable);else box=layout(font,12,12,width,Math.max(60,DrawingArea.height-195));}
  int safeBottom=DrawingArea.height-8;
  int barX=SkillPanel.barOffset(DrawingArea.width)+12;
  if(box.bounds.x<barX+392&&box.bounds.x+box.bounds.width>barX)safeBottom=Math.min(safeBottom,SkillPanel.barTop(DrawingArea.width,DrawingArea.height)-8);
  if(DrawingArea.width>765&&box.bounds.x<519)safeBottom=Math.min(safeBottom,DrawingArea.height-183);
  if(box.bounds.y+box.bounds.height>safeBottom)box=layout(font,box.bounds.x,box.bounds.y,width,Math.max(40,safeBottom-box.bounds.y));
  return box;
 }
 public java.awt.Rectangle bounds(TextDrawingArea font){return new java.awt.Rectangle(worldLayout(font).bounds);}
 public void draw(TextDrawingArea font){drawLayout(font,worldLayout(font));}
 /** Chat-hosted guidance stops before the typed input row. */
 public void drawInChat(TextDrawingArea font,int yOffset){drawLayout(font,layout(font,12,yOffset+6,490,104));}
 private void drawLayout(TextDrawingArea font,Layout box){
  if(lines.length==0||box.bounds.height<=0)return;java.awt.Rectangle r=box.bounds;int accent=lines[0].startsWith("MAP COMPLETE")?0x93DB9B:ExileTheme.HEADING;
  ExileTheme.panel(r.x,r.y,r.width,r.height,ExileTheme.BORDER,ExileTheme.PANEL);
  for(int i=0;i<box.title.size();i++)if(26+i*21<r.height-5)ExileTheme.heading(r.x+14,r.y+26+i*21,box.title.get(i),accent);
  if(box.headerHeight<r.height-12)DrawingArea.drawHorizontalLine(r.x+12,r.y+box.headerHeight,r.width-24,ExileTheme.BORDER_SUBTLE);
  int available=Math.max(0,(r.height-box.headerHeight-20)/16);
  for(int i=0;i<Math.min(available,box.body.size());i++){String line=box.body.get(i);if(i==available-1&&box.body.size()>available){while(font.getTextWidth(line+"...")>r.width-28&&line.length()>0)line=line.substring(0,line.length()-1);line+="...";}ExileTheme.label(font,r.x+14,r.y+box.headerHeight+22+i*16,line,ExileTheme.TEXT);}
 }
}
