package com.koios.client.arpg;
import com.koios.client.graphic.*;
/** Native tooltips retain readable row spacing; long fixed-mode details flow into columns. */
public final class ItemTooltipRenderer {
 public static final class Layout {public final java.util.List<String> lines;public final int x,y,width,height,columns,rows,lineHeight=16;Layout(java.util.List<String> lines,int x,int y,int width,int height,int columns,int rows){this.lines=lines;this.x=x;this.y=y;this.width=width;this.height=height;this.columns=columns;this.rows=rows;}}
 public static Layout layout(TextDrawingArea font,ItemTooltipCache.Entry entry,boolean advanced,int mouseX,int mouseY){
  String[] source=advanced?entry.advanced:entry.normal;int desired=260;for(String line:source)desired=Math.max(desired,font.getTextWidth(line)+24);int width=Math.min(desired,DrawingArea.width-16),columns=1,rows=0;java.util.List<String> wrapped=new java.util.ArrayList<>();
  for(int attempt=1;attempt<=3;attempt++){columns=attempt;if(attempt>1)width=Math.min(DrawingArea.width-12,desired*attempt);int content=(width-12*(columns+1))/columns;wrapped.clear();for(String line:source)wrapped.addAll(ChatWrap.lines(line,Math.max(24,content),font::getTextWidth));rows=(wrapped.size()+columns-1)/columns;if(rows*16+32<=DrawingArea.height-12)break;}
  int height=Math.min(rows*16+32,DrawingArea.height-12),x=Math.max(6,Math.min(mouseX-width-16,DrawingArea.width-width-6)),y=Math.max(6,Math.min(mouseY-20,DrawingArea.height-height-6));return new Layout(wrapped,x,y,width,height,columns,rows);
 }
 public static void draw(TextDrawingArea font,ItemTooltipCache.Entry entry,boolean advanced,int mouseX,int mouseY){
  Layout box=layout(font,entry,advanced,mouseX,mouseY);ExileTheme.panel(box.x,box.y,box.width,box.height,ExileTheme.BORDER,ExileTheme.PANEL);DrawingArea.drawHorizontalLine(box.x+12,box.y+31,box.width-24,ExileTheme.BORDER);int columnWidth=(box.width-12)/box.columns;
  for(int i=0;i<box.lines.size();i++){String line=box.lines.get(i);int column=i/box.rows,row=i%box.rows,baseline=box.y+23+row*box.lineHeight+(row>0?8:0);if(baseline>=box.y+box.height-4)continue;int color=i==0?entry.color:line.startsWith("Hold Ctrl")?ExileTheme.DIM:line.contains(" / Tier ")?ExileTheme.MUTED:line.contains("orrupted")?0xDD8272:ExileTheme.TEXT;if(i==0&&ExileTheme.headingWidth(line)<columnWidth-24)ExileTheme.heading(box.x+12,baseline,line,color);else ExileTheme.label(font,box.x+12+column*columnWidth,baseline,line,color);}
 }
}
