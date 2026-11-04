package com.koios.client.arpg;
import java.util.*;import java.awt.Rectangle;import java.nio.charset.StandardCharsets;import com.koios.client.graphic.*;

/** Owner-filtered server loot metadata. Tile coordinates survive local scene/base changes. */
public final class GroundLootLabels {
 private static final int[] HORIZONTAL_OFFSETS={0,-32,32,-64,64,-96,96,-128,128,-192,192,-256,256};
 /** Projection receives absolute world fine coordinates (tile*128+64), as BattleEffects does. */
 public interface Projection {int[] project(int worldX,int worldY,int height);}
 public static final class Entry {
  public final String key,uuid,name;public final int worldX,worldY,plane,id,color,quantity;
  private Entry(String key,int x,int y,int plane,int id,String uuid,int color,int quantity,String name){this.key=key;worldX=x;worldY=y;this.plane=plane;this.id=id;this.uuid=uuid;this.color=color;this.quantity=quantity;this.name=name;}
 }
 private static final class Hit {final Entry entry;final int x,y,width,height;Hit(Entry e,int x,int y,int w,int h){entry=e;this.x=x;this.y=y;width=w;height=h;}boolean contains(int px,int py){return px>=x&&px<x+width&&py>=y&&py<y+height;}boolean overlaps(int left,int top,int w,int h){return left<x+width+2&&left+w+2>x&&top<y+height+2&&top+h+2>y;}}
 private final LinkedHashMap<String,Entry> entries=new LinkedHashMap<>();private final ArrayList<Hit> hits=new ArrayList<>();private final ArrayList<Rectangle> exclusions=new ArrayList<>();
 public synchronized void setExclusions(java.util.List<Rectangle> rectangles){exclusions.clear();if(rectangles!=null)for(Rectangle rectangle:rectangles)if(rectangle!=null&&rectangle.width>0&&rectangle.height>0)exclusions.add(new Rectangle(rectangle));hits.clear();}
 public synchronized void clear(){entries.clear();hits.clear();}
 public synchronized void clearHits(){hits.clear();}
 public synchronized int size(){return entries.size();}
 public synchronized int visibleCount(){return hits.size();}
 public synchronized java.util.List<Entry> snapshot(){return new ArrayList<>(entries.values());}
 public synchronized Entry hit(int x,int y){for(int i=hits.size()-1;i>=0;i--)if(hits.get(i).contains(x,y))return hits.get(i).entry;return null;}
 public synchronized void accept(String payload){if(payload==null||payload.length()>2048)return;if(payload.equals("clear")){clear();return;}String[] fields=payload.split("\\|",-1);if(fields.length==2&&fields[0].equals("remove")){entries.remove(fields[1]);hits.clear();return;}if(fields.length!=10||!fields[0].equals("add")||!fields[1].matches("[A-Za-z0-9_.:-]{1,96}"))return;
  try{int x=Integer.parseInt(fields[2]),y=Integer.parseInt(fields[3]),plane=Integer.parseInt(fields[4]),id=Integer.parseInt(fields[5]),color=Integer.parseInt(fields[7]),quantity=Integer.parseInt(fields[8]);if(x<0||x>65535||y<0||y>65535||plane<0||plane>3||id<0||id>65535||quantity<1||fields[6].length()>80)return;String name=new String(Base64.getDecoder().decode(fields[9]),StandardCharsets.UTF_8).replaceAll("@[A-Za-z0-9]{3}@|<[^>]*>|[\\p{Cntrl}]","").trim();if(name.isEmpty())return;if(name.length()>96)name=name.substring(0,96);Entry entry=new Entry(fields[1],x,y,plane,id,fields[6],color&0xFFFFFF,quantity,name);if(!entries.containsKey(entry.key)&&entries.size()>=512)entries.remove(entries.keySet().iterator().next());entries.put(entry.key,entry);hits.clear();}catch(IllegalArgumentException ignored){}
 }
 private static int priority(Entry e){if(e.id==22410||e.id==22413||e.id==22414)return 0;if(e.id==22411||e.id==22412)return 1;if(e.color==0xFFFF77||e.color==0xAF6025)return 2;if(CurrencyIcons.supports(e.id))return 3;return e.id==995?5:4;}
 private boolean blocked(int x,int y,int width,int height){for(Hit hit:hits)if(hit.overlaps(x,y,width,height))return true;for(Rectangle exclusion:exclusions)if(x<exclusion.x+exclusion.width+2&&x+width+2>exclusion.x&&y<exclusion.y+exclusion.height+2&&y+height+2>exclusion.y)return true;return false;}
 private static boolean important(Entry e){return CurrencyIcons.supports(e.id)||e.id>=22410&&e.id<=22414||e.color==0xFFFF77||e.color==0xAF6025;}
 /** playerWorldX/Y are absolute tile coordinates. Caller draws only in an unobstructed world viewport. */
 public synchronized void draw(TextDrawingArea font,Projection projection,int currentPlane,int playerWorldX,int playerWorldY,boolean showAll,int mouseX,int mouseY){
  hits.clear();if(font==null||projection==null)return;ArrayList<Entry> candidates=new ArrayList<>();for(Entry entry:entries.values()){int distance=Math.max(Math.abs(entry.worldX-playerWorldX),Math.abs(entry.worldY-playerWorldY));if(entry.plane==currentPlane&&distance<=(showAll?12:6)&&(showAll||important(entry)))candidates.add(entry);}Collections.sort(candidates,(a,b)->{int da=Math.max(Math.abs(a.worldX-playerWorldX),Math.abs(a.worldY-playerWorldY)),db=Math.max(Math.abs(b.worldX-playerWorldX),Math.abs(b.worldY-playerWorldY));int order=Integer.compare(priority(a),priority(b));if(order==0)order=Integer.compare(da,db);return order!=0?order:a.key.compareTo(b.key);});
  int left=DrawingArea.topX+5,right=DrawingArea.bottomX-5,top=DrawingArea.topY+5,bottom=DrawingArea.bottomY-5;if(right-left<48||bottom-top<20)return;Map<Long,Integer> stacks=new HashMap<>();
  for(Entry entry:candidates){if(hits.size()>=24)break;int[] point=projection.project(entry.worldX*128+64,entry.worldY*128+64,12);if(point==null||point.length<2||point[0]<left||point[0]>=right||point[1]<top||point[1]>=bottom)continue;String quantity=entry.quantity>1?" x"+entry.quantity:"",name=fontSafe(entry.name);int maxWidth=Math.min(260,right-left);boolean shortened=font.getTextWidth(name+quantity)>maxWidth-14;while(font.getTextWidth(name+(shortened?"..":"")+quantity)>maxWidth-14&&name.length()>2)name=name.substring(0,name.length()-1);if(shortened)name+="..";String text=name+quantity;int width=Math.min(maxWidth,font.getTextWidth(text)+14),height=20,x=Math.max(left,Math.min(right-width,point[0]-width/2));long cell=((long)entry.worldX<<32)|(entry.worldY&0xFFFFFFFFL);int stack=stacks.containsKey(cell)?stacks.get(cell):0;stacks.put(cell,stack+1);int targetY=point[1]-27-stack*22,y=targetY,best=Integer.MAX_VALUE;boolean placed=false;
   // Search around the anchor, including below/sideways when HUD blocks upward stacks.
   int anchorX=x;for(int row=0;row<=30;row++)for(int direction=0;direction<(row==0?1:2);direction++){int candidateY=targetY+(direction==0?-1:1)*row*22;if(candidateY<top||candidateY+height>bottom)continue;for(int offset:HORIZONTAL_OFFSETS){int candidateX=Math.max(left,Math.min(right-width,anchorX+offset));int dx=Math.abs(candidateX-anchorX),dy=Math.abs(candidateY-targetY),score=dx*6+dy*5+(candidateY>targetY?1:0);if(score>=best||blocked(candidateX,candidateY,width,height))continue;best=score;x=candidateX;y=candidateY;placed=true;}}
   if(!placed)continue;Hit hit=new Hit(entry,x,y,width,height);hits.add(hit);boolean hovered=hit.contains(mouseX,mouseY);int color=muted(entry.color),border=hovered?0xD9C594:0x48545C;DrawingArea.drawPixels(height,y,x,hovered?ExileTheme.HOVER:ExileTheme.PANEL,width);DrawingArea.fillPixels(x,width,height,border,y);DrawingArea.drawPixels(height-4,y+2,x+2,color,2);font.method389(true,x+8,hovered?bright(color):color,text,y+14);
  }
 }
 private static String fontSafe(String name){StringBuilder safe=new StringBuilder(name.length());for(int i=0;i<name.length();i++){char c=name.charAt(i);safe.append(c<256?c:(char)63);}return safe.toString();}
 private static int muted(int color){if(color==0xFFFF77)return 0xDCCB83;if(color==0x8888FF)return 0x9DACE2;if(color==0xAF6025)return 0xCB946C;if(color==0xD5D5D5)return 0xBFC7CC;return color;}
 private static int bright(int color){int r=Math.min(255,(color>>16&255)+24),g=Math.min(255,(color>>8&255)+24),b=Math.min(255,(color&255)+24);return r<<16|g<<8|b;}
}




