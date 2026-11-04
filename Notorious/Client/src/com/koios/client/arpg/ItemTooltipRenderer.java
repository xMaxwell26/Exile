package com.koios.client.arpg;
import com.koios.client.graphic.*;
/** Native tooltips retain readable row spacing; long fixed-mode details flow into columns. */
public final class ItemTooltipRenderer {
 public static final class Layout {public final java.util.List<String> lines;public final int x,y,width,height,columns,rows,lineHeight=14;Layout(java.util.List<String> lines,int x,int y,int width,int height,int columns,int rows){this.lines=lines;this.x=x;this.y=y;this.width=width;this.height=height;this.columns=columns;this.rows=rows;}}
 public static Layout layout(TextDrawingArea font,ItemTooltipCache.Entry entry,boolean advanced,int mouseX,int mouseY){
  String[] source=advanced?entry.advanced:entry.normal;int desired=260;for(String line:source)desired=Math.max(desired,font.getTextWidth(line)+24);int width=Math.min(desired,DrawingArea.width-16),columns=1,rows=0;java.util.List<String> wrapped=new java.util.ArrayList<>();
  for(int attempt=1;attempt<=3;attempt++){columns=attempt;if(attempt>1)width=Math.min(DrawingArea.width-12,desired*attempt);int content=(width-12*(columns+1))/columns;wrapped.clear();for(String line:source)wrapped.addAll(ChatWrap.lines(line,Math.max(24,content),font::getTextWidth));rows=(wrapped.size()+columns-1)/columns;if(rows*14+24<=DrawingArea.height-12)break;}
  int height=Math.min(rows*14+24,DrawingArea.height-12),x=Math.max(6,Math.min(mouseX-width-16,DrawingArea.width-width-6)),y=Math.max(6,Math.min(mouseY-20,DrawingArea.height-height-6));return new Layout(wrapped,x,y,width,height,columns,rows);
 }
 public static void draw(TextDrawingArea font,ItemTooltipCache.Entry entry,boolean advanced,int mouseX,int mouseY){
  Layout box=layout(font,entry,advanced,mouseX,mouseY);DrawingArea.drawPixels(box.height,box.y,box.x,ExileTheme.PANEL,box.width);DrawingArea.fillPixels(box.x,box.width,box.height,entry.color,box.y);DrawingArea.drawPixels(2,box.y+2,box.x+2,entry.color,box.width-4);int columnWidth=(box.width-12)/box.columns;
  for(int i=0;i<box.lines.size();i++){String line=box.lines.get(i);int column=i/box.rows,row=i%box.rows,baseline=box.y+20+row*box.lineHeight;if(baseline>=box.y+box.height-4)continue;int color=i==0?entry.color:line.startsWith("Hold Ctrl")?0x9397A3:line.contains(" / Tier ")?0xB2A18B:line.contains("orrupted")?0xDD6655:ExileTheme.TEXT;font.method389(true,box.x+12+column*columnWidth,color,line,baseline);}
 }
}
