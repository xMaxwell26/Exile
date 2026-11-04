package com.koios.client.arpg;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.*;
import com.koios.client.graphic.*;

/** Present original item art at device resolution while retaining software UI occlusion. */
public final class HighDpiArt {
    private static final Map<int[],Map<Long,Stamp>> SURFACES=new WeakHashMap<>();
    public static final class Stamp {
        final java.lang.ref.WeakReference<int[]> raster;
        final int[] before,after;
        final int width,x,y,w,h;
        final Sprite sprite;
        Stamp(Sprite sprite,int x,int y){this.sprite=sprite;this.x=x;this.y=y;w=sprite.myWidth;h=sprite.myHeight;raster=new java.lang.ref.WeakReference<>(DrawingArea.pixels);width=DrawingArea.width;before=snapshot();after=new int[w*h];}
        int[] snapshot(){int[] result=new int[w*h];for(int row=0;row<h;row++)System.arraycopy(raster.get(),(y+row)*width+x,result,row*w,w);return result;}
    }
    public static Stamp begin(Sprite sprite,int x,int y){
        if(sprite.highResolutionArt==null||sprite.myWidth<1||sprite.myHeight<1||sprite.myWidth>128||sprite.myHeight>128||x<DrawingArea.topX||y<DrawingArea.topY||x+sprite.myWidth>DrawingArea.bottomX||y+sprite.myHeight>DrawingArea.bottomY)return null;
        return new Stamp(sprite,x,y);
    }
    public static void end(Stamp stamp){
        if(stamp==null)return;
        int[] after=stamp.snapshot();System.arraycopy(after,0,stamp.after,0,stamp.w*stamp.h);
        SURFACES.computeIfAbsent(stamp.raster.get(),key->new LinkedHashMap<>()).put(((long)stamp.x<<32)|stamp.y,stamp);
    }
    public static void clear(int[] raster){SURFACES.remove(raster);}
    /** Call before repainting a slot, including an empty slot, so removed art cannot persist. */
    public static void invalidate(int x,int y,int width,int height){
        Map<Long,Stamp> stamps=SURFACES.get(DrawingArea.pixels);
        if(stamps==null)return;
        Iterator<Stamp> iterator=stamps.values().iterator();
        while(iterator.hasNext()){
            Stamp stamp=iterator.next();
            if(stamp.x<x+width&&stamp.x+stamp.w>x&&stamp.y<y+height&&stamp.y+stamp.h>y)iterator.remove();
        }
    }
    public static void draw(Graphics graphics,int[] raster,int xOffset,int yOffset){
        if(!(graphics instanceof Graphics2D)||((Graphics2D)graphics).getTransform().getScaleX()<=1.01)return;
        Map<Long,Stamp> stamps=SURFACES.get(raster);if(stamps==null)return;
        for(Stamp stamp:stamps.values()){
            if(stamp.sprite.highResolutionArt==null)continue;
            int visible=0,painted=0;
            Area clip=new Area();
            for(int row=0;row<stamp.h;row++){
                int start=-1;
                for(int col=0;col<=stamp.w;col++){
                    boolean match=col<stamp.w&&raster[(stamp.y+row)*stamp.width+stamp.x+col]==stamp.after[row*stamp.w+col];
                    if(col<stamp.w&&stamp.sprite.myPixels[row*stamp.w+col]!=0){painted++;if(match)visible++;}
                    if(match&&start<0)start=col;
                    if(!match&&start>=0){clip.add(new Area(new Rectangle(xOffset+stamp.x+start,yOffset+stamp.y+row,col-start,1)));start=-1;}
                }
            }
            if(visible<Math.max(8,painted/4))continue;
            Graphics2D g=(Graphics2D)graphics.create();
            g.clip(clip);
            BufferedImage background=new BufferedImage(stamp.w,stamp.h,BufferedImage.TYPE_INT_RGB);
            background.setRGB(0,0,stamp.w,stamp.h,stamp.before,0,stamp.w);
            g.drawImage(background,xOffset+stamp.x,yOffset+stamp.y,null);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(stamp.sprite.highResolutionArt,xOffset+stamp.x,yOffset+stamp.y,stamp.w,stamp.h,null);
            g.dispose();
        }
    }
}
