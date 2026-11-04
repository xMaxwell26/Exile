from pathlib import Path
p=Path('Notorious/Server/src/com/koios/game/arpg/RegionInstance.java');s=p.read_text().replace('private final Set<Integer> regions','private final boolean tiled;\n public final java.util.List<RSObject> scenery=new java.util.ArrayList<>();\n private final Set<Integer> regions');s=s.replace('public RegionInstance(int sourceX,int sourceY){','public RegionInstance(int sourceX,int sourceY){this(sourceX,sourceY,false);}\n public RegionInstance(int sourceX,int sourceY,boolean tiled){\n  this.tiled=tiled;if(tiled){int[] patch=openPatch(sourceX,sourceY);sourceX=patch[0]+48;sourceY=patch[1]+48;}');s=s.replace('int sx=this.sourceX+dx,sy=this.sourceY+dy,','int sx=this.sourceX+(tiled?dx%8:dx),sy=this.sourceY+(tiled?dy%8:dy),');s=s.replace('(((sourceX>>3)+x)<<14)|(((sourceY>>3)+y)<<3)','(((sourceX>>3)+(tiled?0:x))<<14)|(((sourceY>>3)+(tiled?0:y))<<3)');idx=s.index(' public Location translate');s=s[:idx]+''' private static int[] openPatch(int centreX,int centreY){
  for(int radius=0;radius<=12;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dy=-radius;dy<=radius;dy++){
   int x=(centreX>>3)*8+dx*8,y=(centreY>>3)*8+dy*8;boolean clear=true;
   for(int a=0;a<8&&clear;a++)for(int b=0;b<8;b++)if(MapRegion.getClipping(x+a,y+b,0)!=0||MapRegion.getObject(x+a,y+b,0)!=null){clear=false;break;}
   if(clear)return new int[]{x,y};
  }throw new IllegalStateException("No clear terrain patch for authored arena");
 }
 public void sendScenery(Player p){for(RSObject object:scenery)p.getSession().send(new com.koios.network.out.impl.SendObjectPacket(object.getId(),object.getX(),object.getY(),0,object.getFace(),object.getType()));}
'''+s[idx:];p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/AuthoredMaps.java');p.write_text('''package com.koios.game.arpg;
import com.koios.game.map.*;import com.koios.game.model.Location;
/** Original walkable room/arena layouts assembled on a repeated clear cache terrain patch. */
public final class AuthoredMaps {
 public static String name(int layout){return layout==1?"Ashen Foundry":layout==2?"Thornwild Courtyard":"Forsaken Settlement";}
 public static Location build(RegionInstance region,int layout){
  for(int x=0;x<104;x++)for(int y=0;y<104;y++)if(x<32||x>72||y<32||y>72)MapRegion.getRegion(region.baseX+x,region.baseY+y).setClip(region.baseX+x,region.baseY+y,0,0xFFFFFF);
  int wall=layout==1?158:2068;
  for(int n=32;n<=72;n++){place(region,wall,n,32,0,1);place(region,wall,n,72,0,1);if(n>32&&n<72){place(region,wall,32,n,0,0);place(region,wall,72,n,0,0);}}
  if(layout==1){
   for(int x=33;x<72;x++){if(x<48||x>55)place(region,158,x,46,0,1);if(!(x>=40&&x<=45)&&!(x>=59&&x<=64))place(region,158,x,57,0,1);}
   for(int y=34;y<45;y++){place(region,158,42,y,0,0);place(region,158,62,y,0,0);}
   for(int x:new int[]{36,38,66,68})for(int y:new int[]{37,40,61})place(region,357,x,y,10,0);
  }else{
   for(int y=38;y<68;y++)if(y<48||y>53){place(region,2068,42,y,0,0);place(region,2068,62,y,0,0);}
   for(int x:new int[]{47,57})for(int y:new int[]{60,68})place(region,574,x,y,10,0);
   for(int x:new int[]{36,68})for(int y:new int[]{42,49,58,65})place(region,357,x,y,10,0);
  }
  return new Location(region.baseX+52,region.baseY+38,0);
 }
 private static void place(RegionInstance region,int id,int dx,int dy,int type,int face){int x=region.baseX+dx,y=region.baseY+dy;RSObject object=new RSObject(x,y,0,id,type,face);MapRegion.getRegion(x,y).addObject(object);MapRegion.getRegion(x,y).setClip(x,y,0,0xFFFFFF);MapRegion.getRegion(x,y).addShootable(x,y,0,0xFFFFFF);region.scenery.add(object);}
}
''')
p=Path('Notorious/Server/src/com/koios/game/arpg/MapRuns.java');s=p.read_text().replace('public int tier,mods,portals','public int layout;public int tier,mods,portals');s=s.replace(' public static Item map(int tier,int mods){Item item=new Item(MAP_BASE);',' public static Item map(int tier,int mods){return map(tier,mods,0);}\n public static Item map(int tier,int mods,int layout){Item item=new Item(layout==1?22413:layout==2?22414:MAP_BASE);');s=s.replace('"Forsaken Settlement / T"+item.getRoll().getMap().tier','AuthoredMaps.name(item.getId()==22413?1:item.getId()==22414?2:0)+" / T"+item.getRoll().getMap().tier');s=s.replace('public static void fixtures(Player p){Run run=RUNS.get(p);','public static void fixtures(Player p){Run run=RUNS.get(p);if(inside(p))run.region.sendScenery(p);');s=s.replace('run.region=new RegionInstance(3222,3218);','run.layout=intro?0:item.getId()==22413?1:item.getId()==22414?2:0;run.region=new RegionInstance(3222,3218,run.layout!=0);');s=s.replace('run.spawn=findSpawn(run.region,3222,3218);','run.spawn=run.layout==0?findSpawn(run.region,3222,3218):AuthoredMaps.build(run.region,run.layout);');s=s.replace('drop(p,mob,map(1,0))','drop(p,mob,map(1,0,1))').replace('drop(p,mob,map(Math.min(10,run.tier+1),0))','drop(p,mob,map(Math.min(10,run.tier+1),0,random.nextInt(3)))');s=s.replace('String heading=','String heading=');s=s.replace('"Defeat the Settlement Champion\\n"','"Defeat the map boss\\n"');s=s.replace('new int[]{73,90,13,59}[Math.floorMod(at.getX()*31+at.getY(),4)]','new int[]{73,90,13,59,49,103}[Math.floorMod(at.getX()*31+at.getY()+run.layout,6)]').replace('Math.floorMod(at.getX()*31+at.getY(),4);','Math.floorMod(at.getX()*31+at.getY()+run.layout,6);');s=s.replace('this.role=boss?4:','this.role=boss?6:');s=s.replace('mob.role==2?6:2','mob.role==2||mob.role==5?6:2').replace('mob.role==3?DamageType.CHAOS','mob.role==5?DamageType.COLD:mob.role==3?DamageType.CHAOS');s=s.replace('if(random.nextInt((run.mods&4)!=0?2:3)==0','if(random.nextInt((run.mods&4)!=0?2:3)==0');s=s.replace('drop(p,mob,gem(Math.min(20,Math.max(2,1+run.tier/2)),random.nextBoolean()))','drop(p,mob,gem(Math.min(20,Math.max(1,1+run.tier/2)),random.nextBoolean()))');s=s.replace('  if(mob.boss||random.nextInt(10)==0)','  if(!mob.boss&&random.nextInt(5)==0)drop(p,mob,gem(Math.max(1,run.tier/2),true));\n  if(mob.boss||random.nextInt(10)==0)');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/def/ItemDefinition.java');s=p.read_text().replace('for(com.koios.game.arpg.EquipmentBases.Base base:', 'custom(22413,8007,"Ashen Foundry Map");custom(22414,8007,"Thornwild Courtyard Map");\n  for(com.koios.game.arpg.EquipmentBases.Base base:',1);p.write_text(s)
p=Path('Notorious/Server/src/com/koios/network/in/impl/PacketCommand.java');s=p.read_text().replace('if(input.equalsIgnoreCase("arpgbases")', 'if(input.equalsIgnoreCase("arpgmaps")&&!GameService.isOnlineMode()){for(int layout=0;layout<3;layout++)com.koios.game.arpg.MapRuns.grant(player,com.koios.game.arpg.MapRuns.map(1,0,layout));player.sendMessage("Added one map of each layout.");return;}\n        if(input.equalsIgnoreCase("arpgbases")',1);p.write_text(s)
