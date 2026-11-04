package com.koios.game.arpg;
import java.util.*;
/** Immutable, versioned per-item data. Corruption is independent of rarity. */
public final class ItemRoll {
 public enum Rarity { NORMAL, MAGIC, RARE, UNIQUE }
 public enum Layer { PREFIX, SUFFIX, BASE_IMPLICIT, CORRUPTION_IMPLICIT, EATER_IMPLICIT, EXARCH_IMPLICIT, ENCHANT }
 public static final class Affix {
  private final String id;
  private final Layer layer;
  private final int tier;
  private final List<Modifier> modifiers;
  public Affix(String id, Layer layer, int tier, Modifier... modifiers) {
   this.id=id;this.layer=layer;this.tier=tier;this.modifiers=Collections.unmodifiableList(Arrays.asList(modifiers.clone()));validate();
  }
  public List<Modifier> getModifiers(){return Collections.unmodifiableList(modifiers);}
  public String getId(){return id;}
  public int getTier(){return tier;}
  public Layer getLayer(){return layer;}
  void validate(){
   if(id==null || id.isEmpty() || layer==null || tier<1 || modifiers==null || modifiers.isEmpty()) throw new IllegalArgumentException("Invalid affix");
   for(Modifier m:modifiers) m.validate();
  }
  public String toString(){return layer+" "+id+((layer==Layer.PREFIX||layer==Layer.SUFFIX)?" T"+tier:"")+" "+modifiers;}
 }
 public static final class MapData {
  public final int tier,mods;
  public MapData(int tier,int mods){this.tier=tier;this.mods=mods;validate();}
  public void validate(){if(tier<1||tier>10||mods<0||mods>7)throw new IllegalArgumentException("Invalid map");}
 }
 public static final class GemData {public final int level;public final boolean support;public GemData(int level,boolean support){this.level=level;this.support=support;validate();}public void validate(){if(level<1||level>20)throw new IllegalArgumentException("Invalid gem level");}}
 private final GemData gem;
 public GemData getGem(){return gem;}
 public ItemRoll withGemLevel(int level){if(gem==null)throw new IllegalArgumentException("Not a gem");GemData next=new GemData(level,gem.support);return new ItemRoll(instanceId,rarity,Math.max(1,level*4-3),Math.max(1,level*4-3),identified,corruption,affixes,map,next);}
 public static ItemRoll gem(int level,boolean support){return new ItemRoll(UUID.randomUUID().toString(),Rarity.NORMAL,Math.max(1,level*4-3),Math.max(1,level*4-3),true,0,Collections.emptyList(),null,new GemData(level,support));}
 private final MapData map;
 public MapData getMap(){return map;}
 public ItemRoll changed(Rarity rarity,boolean identified,int corruption,List<Affix> affixes,MapData map){return new ItemRoll(instanceId,rarity,itemLevel,requiredLevel,identified,corruption,affixes,map,gem);}
 public static ItemRoll map(int tier,int mods){return new ItemRoll(UUID.randomUUID().toString(),mods==0?Rarity.NORMAL:Integer.bitCount(mods)>2?Rarity.RARE:Rarity.MAGIC,tier,1,true,0,Collections.emptyList(),new MapData(tier,mods),null);}
 private final int version;
 private final String instanceId;
 private final Rarity rarity;
 private final int itemLevel, requiredLevel, corruption;
 private final boolean identified;
 private final List<Affix> affixes;
 public ItemRoll(Rarity rarity,int itemLevel,int requiredLevel,boolean identified,int corruption,List<Affix> affixes){
  this(UUID.randomUUID().toString(),rarity,itemLevel,requiredLevel,identified,corruption,affixes,null,null);
 }
 private ItemRoll(String uuid,Rarity rarity,int itemLevel,int requiredLevel,boolean identified,int corruption,List<Affix> affixes,MapData map,GemData gem){
  this.version=1;this.instanceId=uuid;this.rarity=rarity;this.itemLevel=itemLevel;this.requiredLevel=requiredLevel;this.map=map;this.gem=gem;
  this.identified=identified;this.corruption=corruption;this.affixes=Collections.unmodifiableList(new ArrayList<>(affixes));validate();
 }
 public void validate(){
  if(version!=1 || rarity==null || itemLevel<1 || requiredLevel<1 || corruption<0 || corruption>2 || affixes==null) throw new IllegalArgumentException("Invalid item roll");
  UUID.fromString(instanceId);if(map!=null)map.validate();if(gem!=null)gem.validate();
  int prefixes=0,suffixes=0;Set<String> ids=new HashSet<>();
  for(Affix a:affixes){a.validate();if(!ids.add(a.id))throw new IllegalArgumentException("Duplicate affix");if(a.layer==Layer.PREFIX)prefixes++;if(a.layer==Layer.SUFFIX)suffixes++;}
  int limit=rarity==Rarity.NORMAL?0:rarity==Rarity.MAGIC?1:3;
  if(prefixes>limit || suffixes>limit)throw new IllegalArgumentException("Too many affixes");
 }
 public String getInstanceId(){return instanceId;}
 public Rarity getRarity(){return rarity;}
 public int getItemLevel(){return itemLevel;}
 public int getCorruption(){return corruption;}
 public int getRequiredLevel(){return requiredLevel;}
 public boolean isIdentified(){return identified;}
 public List<Affix> getAffixes(){return Collections.unmodifiableList(affixes);}
 public String toString(){return rarity+" ilvl "+itemLevel+" requires "+requiredLevel+" corruption "+corruption+" "+(identified?affixes:"Unidentified");}
}
