package com.koios.client.arpg;

import com.koios.client.graphic.DrawingArea;
import com.koios.client.graphic.TextDrawingArea;

/** Exile's shared chrome palette. Skill, damage, rarity and resource colors remain semantic. */
public final class ExileTheme {
 public static final String NAME="Exile";
 public static final int BACKGROUND=0x11100E,PANEL=0x1B1814,SURFACE=0x28221C,SURFACE_DARK=0x161310;
 public static final int HOVER=0x3A2924,SELECTED=0x4A2825,OXBLOOD=0x3E1D1B,CRIMSON=0x8B3029;
 public static final int BORDER=0x5B4634,BORDER_SUBTLE=0x342A23,GOLD=0xB89B68,HEADING=0xE0C591;
 public static final int TEXT=0xDED7C9,MUTED=0xB3A794,DIM=0x84786A;
 public static final int EDGE_LIGHT=0x765D42,EDGE_DARK=0x0B0907,SHADOW=0x060504;

 /** Raised metal-and-leather frame shared by every custom interface. */
 public static void panel(int x,int y,int width,int height,int border,int fill){
  DrawingArea.drawPixels(height,y,x,fill,width);DrawingArea.fillPixels(x,width,height,border,y);
  if(width>4&&height>4){DrawingArea.drawHorizontalLine(x+1,y+1,width-2,EDGE_LIGHT);DrawingArea.drawPixels(height-2,y+1,x+1,EDGE_LIGHT,1);DrawingArea.drawHorizontalLine(x+1,y+height-2,width-2,EDGE_DARK);DrawingArea.drawPixels(height-2,y+1,x+width-2,EDGE_DARK,1);}
  if(width>=100&&height>=50){
   // Worn bronze corner inlays stay inside the existing bounds and never move hit targets.
   for(int side=0;side<2;side++)for(int end=0;end<2;end++){
    int cx=side==0?x+3:x+width-4,cy=end==0?y+3:y+height-4;
    DrawingArea.drawHorizontalLine(side==0?cx:cx-9,cy,10,GOLD);
    DrawingArea.drawPixels(7,end==0?cy:cy-6,cx,GOLD,1);
   }
   DrawingArea.drawHorizontalLine(x+15,y+3,width-30,0x453827);
  }
 }

 /** Recessed socket/field, visually distinct from raised panels and buttons. */
 public static void inset(int x,int y,int width,int height,int border,int fill){
  DrawingArea.drawPixels(height,y,x,fill,width);DrawingArea.fillPixels(x,width,height,border,y);
  if(width>4&&height>4){DrawingArea.drawHorizontalLine(x+1,y+1,width-2,EDGE_DARK);DrawingArea.drawPixels(height-2,y+1,x+1,EDGE_DARK,1);DrawingArea.drawHorizontalLine(x+1,y+height-2,width-2,EDGE_LIGHT);DrawingArea.drawPixels(height-2,y+1,x+width-2,EDGE_LIGHT,1);}
 }

 /** Explicit proportional fonts keep old cache-font behavior unchanged elsewhere. */
 public static TextDrawingArea body(){return TextDrawingArea.exileBody();}
 public static TextDrawingArea small(){return TextDrawingArea.exileSmall();}
 public static TextDrawingArea headingFont(){return TextDrawingArea.exileHeading();}
 public static int textWidth(String value){return body().getTextWidth(value);}
 public static int smallWidth(String value){return small().getTextWidth(value);}
 public static int headingWidth(String value){return headingFont().getTextWidth(value);}
 public static void heading(int x,int baseline,String value,int color){headingFont().method389(true,x,color,value,baseline);}
 public static void heading(int x,int baseline,String value){heading(x,baseline,value,HEADING);}
 /** One antialiased run owns its shadow and device-scale presentation. */
 public static void label(TextDrawingArea font,int x,int y,String value,int color){
  if(font==null)font=body();
  if(font.isExile())font.method389(true,x,color,value,y);
  else{font.method389(false,x+1,SHADOW,value,y+1);font.method389(false,x,color,value,y);}
 }
 private ExileTheme(){}
}
