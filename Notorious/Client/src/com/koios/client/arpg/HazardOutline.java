package com.koios.client.arpg;
import com.koios.client.graphic.DrawingArea;
public final class HazardOutline {public static void line(int x,int y,int tx,int ty,int color){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++){int px=x+(tx-x)*i/Math.max(1,steps),py=y+(ty-y)*i/Math.max(1,steps);if(px>=DrawingArea.topX&&py>=DrawingArea.topY&&px<DrawingArea.bottomX&&py<DrawingArea.bottomY)DrawingArea.pixels[px+py*DrawingArea.width]=color;}}}
