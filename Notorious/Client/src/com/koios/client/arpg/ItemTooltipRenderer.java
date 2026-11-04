package com.koios.client.arpg;
import com.koios.client.graphic.*;
/** Native tooltips retain readable row spacing; long fixed-mode details flow into columns. */
public final class ItemTooltipRenderer {
 public static final class Layout {
  public final java.util.List<String> lines,header,body;
  public final int x,y,width,height,columns,rows,lineHeight,headerHeight,columnWidth,splitIndex;
  public final TextDrawingArea bodyFont;
  Layout(java.util.List<String> header,java.util.List<String> body,int x,int y,int width,int height,int columns,int rows,int spacing,int headerHeight,TextDrawingArea bodyFont,int splitIndex){
   this.header=header;this.body=body;lines=new java.util.ArrayList<>(header);lines.addAll(body);
   this.x=x;this.y=y;this.width=width;this.height=height;this.columns=columns;this.rows=rows;
   lineHeight=spacing;this.headerHeight=headerHeight;this.bodyFont=bodyFont;this.splitIndex=splitIndex;columnWidth=(width-24-(columns-1)*18)/columns;
  }
 }
 public static Layout layout(TextDrawingArea font,ItemTooltipCache.Entry entry,boolean advanced,int mouseX,int mouseY){
  String[] source=advanced?entry.advanced:entry.normal;int desired=280;
  for(String line:source)desired=Math.max(desired,font.getTextWidth(line)+24);
  if(source.length>0)desired=Math.max(desired,Math.min(420,ExileTheme.headingWidth(source[0])+28));
  int maxWidth=DrawingArea.width-12,maxHeight=DrawingArea.height-12,width=Math.min(desired,maxWidth),columns=1,rows=0,height=0,spacing=16,headerHeight=0,splitIndex=0;
  TextDrawingArea bodyFont=font;java.util.List<String> header=new java.util.ArrayList<>(),body=new java.util.ArrayList<>();
  for(int attempt=0;attempt<4;attempt++){
   if(attempt==1){columns=2;width=maxWidth;}if(attempt==2){bodyFont=ExileTheme.small();spacing=14;}if(attempt==3){columns=3;width=maxWidth;}
   header.clear();body.clear();if(source.length>0)header.addAll(ChatWrap.lines(source[0],width-28,ExileTheme::headingWidth));headerHeight=16+header.size()*22;
   int content=(width-24-(columns-1)*18)/columns;for(int i=1;i<source.length;i++)body.addAll(ChatWrap.lines(source[i],Math.max(24,content),bodyFont::getTextWidth));
   rows=(body.size()+columns-1)/columns;splitIndex=rows;
   if(columns==2){int capacity=Math.max(1,(maxHeight-headerHeight-22)/spacing),bestDistance=Integer.MAX_VALUE;for(int cut=1;cut<body.size();cut++){String line=body.get(cut);boolean group=line.startsWith("Prefix /")||line.startsWith("Suffix /")||line.startsWith("Corruption implicit /")||line.startsWith("Base implicit /");int distance=Math.abs(cut*2-body.size());if(group&&Math.max(cut,body.size()-cut)<=capacity&&distance<bestDistance){splitIndex=cut;bestDistance=distance;}}rows=Math.max(splitIndex,body.size()-splitIndex);}
   height=headerHeight+12+rows*spacing+10;if(height<=maxHeight)break;
  }
  height=Math.min(height,maxHeight);int x=Math.max(6,Math.min(mouseX-width-16,DrawingArea.width-width-6)),y=Math.max(6,Math.min(mouseY-20,DrawingArea.height-height-6));
  return new Layout(new java.util.ArrayList<>(header),new java.util.ArrayList<>(body),x,y,width,height,columns,rows,spacing,headerHeight,bodyFont,splitIndex);
 }
 public static void draw(TextDrawingArea font,ItemTooltipCache.Entry entry,boolean advanced,int mouseX,int mouseY){
  Layout box=layout(font,entry,advanced,mouseX,mouseY);ExileTheme.panel(box.x,box.y,box.width,box.height,ExileTheme.BORDER,ExileTheme.PANEL);
  for(int i=0;i<box.header.size();i++)ExileTheme.heading(box.x+14,box.y+27+i*22,box.header.get(i),entry.color);
  int separator=box.y+box.headerHeight;DrawingArea.drawHorizontalLine(box.x+12,separator,box.width-24,ExileTheme.BORDER);
  for(int col=1;col<box.columns;col++)DrawingArea.drawPixels(Math.max(0,box.height-box.headerHeight-20),separator+10,box.x+12+col*(box.columnWidth+18)-9,ExileTheme.BORDER_SUBTLE,1);
  for(int i=0;i<box.body.size();i++){int column=box.columns==2?(i<box.splitIndex?0:1):i/Math.max(1,box.rows),row=box.columns==2?(i<box.splitIndex?i:i-box.splitIndex):i%Math.max(1,box.rows),baseline=separator+22+row*box.lineHeight;if(baseline+3>=box.y+box.height-5)continue;String line=box.body.get(i);int color=line.startsWith("Hold Ctrl")?ExileTheme.DIM:line.contains(" / Tier ")?ExileTheme.MUTED:line.contains("orrupted")?0xDD8272:ExileTheme.TEXT;ExileTheme.label(box.bodyFont,box.x+12+column*(box.columnWidth+18),baseline,line,color);}
 }
}
