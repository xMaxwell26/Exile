from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
def put(p,s):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a[:50]);p.write_text(s.replace(a,b))
put(r/'game/arpg/RegionInstance.java','''package com.koios.game.arpg;
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
 private final Set<Integer> regions=new HashSet<>();private boolean closed;
 public RegionInstance(int sourceX,int sourceY){
  int available=USED.nextClearBit(0);if(available>=256)throw new IllegalStateException("All map instances are occupied");slot=available;USED.set(slot);
  this.sourceX=((sourceX>>3)-6)*8;this.sourceY=((sourceY>>3)-6)*8;
  centreX=8256+(slot%16)*128;centreY=8256+(slot/16)*128;baseX=((centreX>>3)-6)*8;baseY=((centreY>>3)-6)*8;
  try{
   MapRegion[] table=MapRegion.getRegions();if(MapLoading.regionIdTable.length<70000)MapLoading.regionIdTable=Arrays.copyOf(MapLoading.regionIdTable,70000);
   for(int dx=0;dx<104;dx++)for(int dy=0;dy<104;dy++){
    int sx=this.sourceX+dx,sy=this.sourceY+dy,x=baseX+dx,y=baseY+dy,id=((x>>6)<<8)+(y>>6);
    if(!regions.contains(id)){if(table[id]!=null)throw new IllegalStateException("Instance region already occupied");MapRegion region=new MapRegion(id);table[id]=region;MapLoading.regionIdTable[id]=region;regions.add(id);}
    MapRegion from=MapRegion.getRegion(sx,sy),to=table[id];
    for(int z=0;z<4;z++){
     to.setClip(x,y,z,from==null?0xFFFFFF:from.getClip(sx,sy,z));
     if(from!=null){to.addShootable(x,y,z,from.getShootable(sx,sy,z));RSObject object=MapRegion.getObject(sx,sy,z);if(object!=null)to.addObject(new RSObject(x,y,z,object.getId(),object.getType(),object.getFace()));}
    }
   }
  }catch(RuntimeException e){close();throw e;}
 }
 public Location translate(Location source){return new Location(baseX+source.getX()-sourceX,baseY+source.getY()-sourceY,source.getZ());}
 public boolean contains(Location p){return !closed&&p.getX()>=baseX&&p.getX()<baseX+104&&p.getY()>=baseY&&p.getY()<baseY+104;}
 public void send(Player p){
  p.getCurrentRegion().setAs(p.getLocation());p.setPlacementNeeded(true);
  GamePacketBuilder packet=new GamePacketBuilder(241,PacketType.SHORT).writeShortA(centreY>>3).initializeAccess(AccessType.BIT);
  for(int z=0;z<4;z++)for(int x=0;x<13;x++)for(int y=0;y<13;y++)packet.writeBits(1,true).writeBits(26,(z<<24)|(((sourceX>>3)+x)<<14)|(((sourceY>>3)+y)<<3));
  packet.initializeAccess(AccessType.BYTE).writeShort(centreX>>3);p.write(packet);
 }
 public void close(){if(closed)return;closed=true;for(int id:regions){MapRegion.getRegions()[id]=null;MapLoading.regionIdTable[id]=null;}USED.clear(slot);}
}
''')
# Immutable map item metadata and identity-preserving crafting.
p=r/'game/arpg/ItemRoll.java';s=p.read_text();s=s.replace('private final int version;','''public static final class MapData {
  public final int tier,mods;
  public MapData(int tier,int mods){this.tier=tier;this.mods=mods;validate();}
  public void validate(){if(tier<1||tier>10||mods<0||mods>7)throw new IllegalArgumentException("Invalid map");}
 }
 private final MapData map;
 public MapData getMap(){return map;}
 public ItemRoll changed(Rarity rarity,boolean identified,int corruption,List<Affix> affixes,MapData map){return new ItemRoll(instanceId,rarity,itemLevel,requiredLevel,identified,corruption,affixes,map);}
 public static ItemRoll map(int tier,int mods){return new ItemRoll(UUID.randomUUID().toString(),mods==0?Rarity.NORMAL:Rarity.MAGIC,tier,1,true,0,Collections.emptyList(),new MapData(tier,mods));}
 private final int version;''');old='''this.version=1;this.instanceId=UUID.randomUUID().toString();this.rarity=rarity;this.itemLevel=itemLevel;this.requiredLevel=requiredLevel;
  this.identified=identified;this.corruption=corruption;this.affixes=Collections.unmodifiableList(new ArrayList<>(affixes));validate();''';new='''this(UUID.randomUUID().toString(),rarity,itemLevel,requiredLevel,identified,corruption,affixes,null);
 }
 private ItemRoll(String uuid,Rarity rarity,int itemLevel,int requiredLevel,boolean identified,int corruption,List<Affix> affixes,MapData map){
  this.version=1;this.instanceId=uuid;this.rarity=rarity;this.itemLevel=itemLevel;this.requiredLevel=requiredLevel;this.map=map;
  this.identified=identified;this.corruption=corruption;this.affixes=Collections.unmodifiableList(new ArrayList<>(affixes));validate();''';assert old in s;s=s.replace(old,new);s=s.replace('UUID.fromString(instanceId);','UUID.fromString(instanceId);if(map!=null)map.validate();');p.write_text(s)
p=r/'network/out/impl/SendMapRegionPacket.java';edit(p,'public void execute(Session client) {','public void execute(Session client) {\n        if(com.koios.game.arpg.MapRuns.sendRegion(client.getPlayer()))return;')
