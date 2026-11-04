package com.koios.client.arpg;
/** Logical canvas and pointer conversion share the same scale. */
public final class ViewportScale {
 public static double factor(int width,int height,boolean fixed){if(fixed)return 1;double target=width>=2560?2:width>=1920?1.5:width>=1600?1.25:1;return Math.max(1,Math.min(target,Math.min(width/1024.0,height/650.0)));}
 public static int logical(int physical,double scale){return (int)Math.floor(physical/scale);}
}
