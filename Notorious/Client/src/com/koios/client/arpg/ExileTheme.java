package com.koios.client.arpg;

import com.koios.client.graphic.DrawingArea;
import com.koios.client.graphic.TextDrawingArea;

/** Exile's shared chrome palette. Skill, damage, rarity and resource colors remain semantic. */
public final class ExileTheme {
 public static final String NAME="Exile";
 public static final int BACKGROUND=0x100D10,PANEL=0x171215,SURFACE=0x25191D,SURFACE_DARK=0x1C1418;
 public static final int HOVER=0x402029,SELECTED=0x50232D,OXBLOOD=0x4A202A,CRIMSON=0x9A3342;
 public static final int BORDER=0x52303A,BORDER_SUBTLE=0x35252C,GOLD=0xC5A36A,HEADING=0xE2C58A;
 public static final int TEXT=0xDED4C4,MUTED=0xB1A295,DIM=0x83746F;
 public static final int EDGE_LIGHT=0x74505A,EDGE_DARK=0x090709,SHADOW=0x060506;

 /** Raised metal-and-leather frame shared by every custom interface. */
 public static void panel(int x,int y,int width,int height,int border,int fill){
  DrawingArea.drawPixels(height,y,x,fill,width);DrawingArea.fillPixels(x,width,height,border,y);
  if(width>4&&height>4){DrawingArea.drawHorizontalLine(x+1,y+1,width-2,EDGE_LIGHT);DrawingArea.drawPixels(height-2,y+1,x+1,EDGE_LIGHT,1);DrawingArea.drawHorizontalLine(x+1,y+height-2,width-2,EDGE_DARK);DrawingArea.drawPixels(height-2,y+1,x+width-2,EDGE_DARK,1);}
 }

 /** Recessed socket/field, visually distinct from raised panels and buttons. */
 public static void inset(int x,int y,int width,int height,int border,int fill){
  DrawingArea.drawPixels(height,y,x,fill,width);DrawingArea.fillPixels(x,width,height,border,y);
  if(width>4&&height>4){DrawingArea.drawHorizontalLine(x+1,y+1,width-2,EDGE_DARK);DrawingArea.drawPixels(height-2,y+1,x+1,EDGE_DARK,1);DrawingArea.drawHorizontalLine(x+1,y+height-2,width-2,EDGE_LIGHT);DrawingArea.drawPixels(height-2,y+1,x+width-2,EDGE_LIGHT,1);}
 }

 /** Crisp one-pixel engraved shadow instead of the old floating debug text. */
 public static void label(TextDrawingArea font,int x,int y,String value,int color){font.method389(false,x+1,SHADOW,value,y+1);font.method389(false,x,color,value,y);}
 private ExileTheme(){}
}
