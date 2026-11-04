package com.koios.game.arpg;
import java.util.*;
import com.koios.game.map.*;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.GamePacketBuilder.AccessType;
/** A translated 104x104 map with independent collision and object records. */
public final class RegionInstance implements AutoCloseable {
 private static final BitSet USED=new BitSet(256);
 public final int slot,sourceX,sourceY,baseX,baseY,centreX,centreY;
 private final boolean tiled;
 public int arenaLayout;
 public final java.util.List<RSObject> scenery=new java.util.ArrayList<>();
 private final Set<Integer> regions=new HashSet<>();private boolean closed;
 public RegionInstance(int sourceX,int sourceY){this(sourceX,sourceY,false);}
 public RegionInstance(int sourceX,int sourceY,boolean tiled){
  this.tiled=tiled;if(tiled){int[] patch=openPatch(sourceX,sourceY);sourceX=patch[0]+48;sourceY=patch[1]+48;}
  int available=USED.nextClearBit(0);if(available>=256)throw new IllegalStateException("All map instances are occupied");slot=available;USED.set(slot);
  this.sourceX=((sourceX>>3)-6)*8;this.sourceY=((sourceY>>3)-6)*8;
  centreX=8256+(slot%16)*128;centreY=8256+(slot/16)*128;baseX=((centreX>>3)-6)*8;baseY=((centreY>>3)-6)*8;
  try{
   MapRegion[] table=MapRegion.getRegions();if(MapLoading.regionIdTable.length<70000)MapLoading.regionIdTable=Arrays.copyOf(MapLoading.regionIdTable,70000);
   for(int dx=0;dx<104;dx++)for(int dy=0;dy<104;dy++){
    int sx=this.sourceX+(tiled?dx%8:dx),sy=this.sourceY+(tiled?dy%8:dy),x=baseX+dx,y=baseY+dy,id=((x>>6)<<8)+(y>>6);
    if(!regions.contains(id)){if(table[id]!=null)throw new IllegalStateException("Instance region already occupied");MapRegion region=new MapRegion(id);table[id]=region;MapLoading.regionIdTable[id]=region;regions.add(id);}
    MapRegion from=MapRegion.getRegion(sx,sy),to=table[id];
    for(int z=0;z<4;z++){
     to.setClip(x,y,z,from==null?0xFFFFFF:from.getClip(sx,sy,z));
     if(from!=null){to.addShootable(x,y,z,from.getShootable(sx,sy,z));RSObject object=MapRegion.getObject(sx,sy,z);if(object!=null)to.addObject(new RSObject(x,y,z,object.getId(),object.getType(),object.getFace()));}
    }
   }
  }catch(RuntimeException e){close();throw e;}
 }
 private static int[] openPatch(int centreX,int centreY){
  for(int radius=0;radius<=12;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dy=-radius;dy<=radius;dy++){
   int x=(centreX>>3)*8+dx*8,y=(centreY>>3)*8+dy*8;boolean clear=true;
   for(int a=0;a<8&&clear;a++)for(int b=0;b<8;b++)if(MapRegion.getClipping(x+a,y+b,0)!=0||MapRegion.getObject(x+a,y+b,0)!=null){clear=false;break;}
   if(clear)return new int[]{x,y};
  }throw new IllegalStateException("No clear terrain patch for authored arena");
 }
 public void sendScenery(Player p){for(RSObject object:scenery)p.getSession().send(new com.koios.network.out.impl.SendObjectPacket(object.getId(),object.getX(),object.getY(),0,object.getFace(),object.getType()));}
 public Location translate(Location source){return new Location(baseX+source.getX()-sourceX,baseY+source.getY()-sourceY,source.getZ());}
 public boolean contains(Location p){return !closed&&p.getX()>=baseX&&p.getX()<baseX+104&&p.getY()>=baseY&&p.getY()<baseY+104;}
 public void send(Player p){
  p.getSession().send(new com.koios.network.out.impl.SendStringPacket(arenaLayout+"|"+baseX+"|"+baseY,65526));
  p.getCurrentRegion().setAs(new Location(centreX,centreY,0));p.setPlacementNeeded(true);
  GamePacketBuilder packet=new GamePacketBuilder(241,PacketType.SHORT).writeShortA(centreY>>3).initializeAccess(AccessType.BIT);
  for(int z=0;z<4;z++)for(int x=0;x<13;x++)for(int y=0;y<13;y++)packet.writeBits(true).writeBits(26,(z<<24)|(((sourceX>>3)+(tiled?0:x))<<14)|(((sourceY>>3)+(tiled?0:y))<<3));
  packet.initializeAccess(AccessType.BYTE).writeShort(centreX>>3);p.write(packet);
 }
 public void close(){if(closed)return;closed=true;com.koios.game.model.item.GroundItemManager.clearArea(baseX,baseY,104);for(int id:regions){MapRegion.getRegions()[id]=null;MapLoading.regionIdTable[id]=null;}USED.clear(slot);}
}
