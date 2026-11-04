package com.koios.client.arpg;
/** Render directly at the component's native pixel dimensions. */
public final class ViewportScale {
 /**
  * The old 1.25-2x logical scale enlarged a low-resolution game raster and
  * made fullscreen visibly pixelated. It also activated a second AWT art
  * overlay, causing icons and resource art to alternate or "bounce".
  */
 public static double factor(int width,int height,boolean fixed){return 1;}
 public static int logical(int physical,double scale){return (int)Math.floor(physical/scale);}
}
