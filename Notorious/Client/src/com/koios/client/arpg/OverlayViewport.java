package com.koios.client.arpg;

import java.util.Arrays;
import com.koios.client.graphic.DrawingArea;

/** A single logical modal surface keeps drawing and input coordinates identical in every window mode. */
public final class OverlayViewport {
    public static final int WIDTH=512, HEIGHT=334;
    private static final int[] LOCAL=new int[WIDTH*HEIGHT];
    private static boolean painting;

    /** Leave the inventory/minimap column and chat strip available in resized windows. */
    public static int originX(int width,int height,boolean fixed){return fixed?0:Math.max(0,(width-250-WIDTH)/2);}
    public static int originY(int width,int height,boolean fixed){return fixed?0:Math.max(0,(height-165-HEIGHT)/2);}

    public static void draw(int width,int height,boolean fixed,Runnable painter){
        if(painter==null)throw new IllegalArgumentException("An overlay painter is required");
        if(painting)throw new IllegalStateException("Modal overlay painting cannot be nested");
        int[] destination=DrawingArea.pixels;
        int oldWidth=DrawingArea.width,oldHeight=DrawingArea.height;
        int left=DrawingArea.topX,top=DrawingArea.topY,right=DrawingArea.bottomX,bottom=DrawingArea.bottomY;
        int centerX=DrawingArea.centerX,centerY=DrawingArea.centerY,midY=DrawingArea.anInt1387;
        boolean complete=false;
        painting=true;
        try{
            Arrays.fill(LOCAL,0);HighDpiArt.clear(LOCAL);ExileTypography.clear(LOCAL);
            DrawingArea.initDrawingArea(HEIGHT,WIDTH,LOCAL);
            // Only the modal rectangle is composited; nothing outside it may leave native-art stamps.
            DrawingArea.setDrawingArea(322,16,496,16);
            painter.run();complete=true;
        }finally{
            DrawingArea.pixels=destination;DrawingArea.width=oldWidth;DrawingArea.height=oldHeight;
            DrawingArea.topX=left;DrawingArea.topY=top;DrawingArea.bottomX=right;DrawingArea.bottomY=bottom;
            DrawingArea.centerX=centerX;DrawingArea.centerY=centerY;DrawingArea.anInt1387=midY;
            painting=false;
        }
        if(!complete)return;
        int dx=originX(width,height,fixed),dy=originY(width,height,fixed);
        int x0=Math.max(left,dx+16),x1=Math.min(right,dx+496);
        int y0=Math.max(top,dy+16),y1=Math.min(bottom,dy+322);
        if(x0>=x1||y0>=y1)return;
        // Repainting a modal must invalidate old destination stamps before copying its new frame.
        HighDpiArt.invalidate(x0,y0,x1-x0,y1-y0);
        for(int y=y0;y<y1;y++)System.arraycopy(LOCAL,(y-dy)*WIDTH+x0-dx,destination,y*oldWidth+x0,x1-x0);
        HighDpiArt.copySurface(LOCAL,destination,dx,dy);
        ExileTypography.copySurface(LOCAL,destination,dx,dy);
    }
    private OverlayViewport(){}
}
