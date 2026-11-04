package com.koios.client.arpg;

import java.util.*;
import com.koios.client.graphic.DrawingArea;

/** Short, server-confirmed effects projected onto the game world. No combat prediction. */
public final class BattleEffects {
 public interface Projection { int[] project(int worldX,int worldY,int height); }
 private static final class Effect {int kind,x,y,tx,ty;boolean critical;long start;}
 private final ArrayDeque<Effect> effects=new ArrayDeque<>();
 public void clear(){effects.clear();}
 public void accept(String payload){
  String[] fields=payload.split("\\|");if(fields.length<7)return;
  try{Effect e=new Effect();e.kind=Integer.parseInt(fields[0]);e.x=Integer.parseInt(fields[1])*128+64;e.y=Integer.parseInt(fields[2])*128+64;e.tx=Integer.parseInt(fields[3])*128+64;e.ty=Integer.parseInt(fields[4])*128+64;e.critical=Integer.parseInt(fields[6])!=0;e.start=System.currentTimeMillis();if(e.kind<0||e.kind>8)return;if(effects.size()>=32)effects.removeFirst();effects.add(e);}catch(NumberFormatException ignored){}
 }
 public void draw(Projection projection,long now){
  for(Iterator<Effect> it=effects.iterator();it.hasNext();){Effect e=it.next();double t=(now-e.start)/650.0;if(t>=1){it.remove();continue;}t=Math.max(0,t);int alpha=(int)(220*(1-t));
   int color=e.kind==6?0xFF9953:e.kind==7?0x8CDDF1:e.kind==8?0xFF7048:e.kind==2?0xC4B1F2:e.kind==3?0xD7AB70:e.kind==4?0xF39686:0xE5D4A3;
   if(e.kind==6||e.kind==7){double travel=Math.min(1,t*2);int x=(int)(e.x+(e.tx-e.x)*travel),y=(int)(e.y+(e.ty-e.y)*travel);int[] tip=projection.project(x,y,70),tail=projection.project((int)(x-(e.tx-e.x)*.22),(int)(y-(e.ty-e.y)*.22),70);stroke(tail,tip,color,alpha/2,5);glow(tip,17,color,alpha);glow(tip,5,0xFFF4D7,alpha);if(t>.5)spark(projection,e.tx,e.ty,color,alpha,(t-.5)*95,6);continue;}
   if(e.kind==0||e.kind==4||e.kind==5){spark(projection,e.tx,e.ty,color,alpha,12+t*48,e.critical?8:5);if(e.kind!=5)arc(projection,e.tx,e.ty,40+t*90,-1.2,1.2,color,alpha);}
   else {int x=e.kind==2||e.kind==1?e.x:e.tx,y=e.kind==2||e.kind==1?e.y:e.ty;double radius=(e.kind==8?80:e.kind==3?65:45)+t*(e.kind==8?240:e.kind==2?210:150),direction=Math.atan2(e.ty-e.y,e.tx-e.x);if(e.kind==2){for(int arm=0;arm<3;arm++){double a=arm*Math.PI*2/3+t*8;arc(projection,x,y,radius,a,a+1.65,color,alpha);}}else arc(projection,x,y,radius,e.kind==1?direction-1.4:0,e.kind==1?direction+1.4:Math.PI*2,color,alpha);if(e.kind==3||e.kind==8)spark(projection,x,y,color,alpha,20+t*85,8);}
  }
 }
 private static void arc(Projection p,int x,int y,double radius,double start,double end,int color,int alpha){int[] last=null,lastInner=null;for(int i=0;i<=40;i++){double a=start+(end-start)*i/40,inner=Math.max(0,radius-28);int[] point=p.project(x+(int)(Math.cos(a)*radius),y+(int)(Math.sin(a)*radius),8),inside=p.project(x+(int)(Math.cos(a)*inner),y+(int)(Math.sin(a)*inner),8);if(last!=null&&point[0]>=0&&inside[0]>=0&&last[0]>=0&&lastInner[0]>=0){fill(new int[]{last[0],point[0],inside[0],lastInner[0]},new int[]{last[1],point[1],inside[1],lastInner[1]},color,alpha/3);stroke(last,point,color,alpha,3);}last=point;lastInner=inside;}}
 private static void spark(Projection p,int x,int y,int color,int alpha,double size,int spokes){int[] center=p.project(x,y,45);if(center[0]<0)return;glow(center,18,color,alpha/2);for(int i=0;i<spokes;i++){double a=i*Math.PI*2/spokes+.3;int nearX=center[0]+(int)(Math.cos(a)*size*.32),nearY=center[1]+(int)(Math.sin(a)*size*.32),farX=center[0]+(int)(Math.cos(a)*size),farY=center[1]+(int)(Math.sin(a)*size);int w=3;fill(new int[]{nearX-(int)(Math.sin(a)*w),farX,nearX+(int)(Math.sin(a)*w)},new int[]{nearY+(int)(Math.cos(a)*w),farY,nearY-(int)(Math.cos(a)*w)},color,alpha);stroke(new int[]{nearX,nearY},new int[]{farX,farY},0xFFF0CB,alpha/2,1);}}
 private static void glow(int[] point,int radius,int color,int alpha){if(point[0]<0)return;for(int y=-radius;y<=radius;y++)for(int x=-radius;x<=radius;x++){double v=1-(x*x+y*y)/(double)(radius*radius);if(v>0)DrawingArea.drawAlphaPixels(point[0]+x,point[1]+y,1,1,color,(int)(alpha*v*v));}}
 public static void stroke(int[] from,int[] to,int color,int alpha,int width){if(from[0]<0||to[0]<0)return;int steps=Math.max(Math.abs(to[0]-from[0]),Math.abs(to[1]-from[1]));if(steps>4096)return;for(int i=0;i<=steps;i++){int x=from[0]+(to[0]-from[0])*i/Math.max(1,steps),y=from[1]+(to[1]-from[1])*i/Math.max(1,steps);DrawingArea.drawAlphaPixels(x,y,width,width,color,Math.max(0,Math.min(256,alpha)));}}
 public static void fill(int[] xs,int[] ys,int color,int alpha){int min=DrawingArea.bottomY,max=DrawingArea.topY;for(int y:ys){min=Math.min(min,y);max=Math.max(max,y);}for(int y=Math.max(min,DrawingArea.topY);y<=Math.min(max,DrawingArea.bottomY-1);y++){int left=Integer.MAX_VALUE,right=Integer.MIN_VALUE;for(int i=0;i<xs.length;i++){int j=(i+1)%xs.length;if((ys[i]<=y&&ys[j]>y)||(ys[j]<=y&&ys[i]>y)){int x=xs[i]+(y-ys[i])*(xs[j]-xs[i])/(ys[j]-ys[i]);left=Math.min(left,x);right=Math.max(right,x);}}if(right>=left)DrawingArea.drawAlphaPixels(left,y,right-left+1,1,color,alpha);}}
}
