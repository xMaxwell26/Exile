package com.koios.game.arpg;
import java.util.*;
import com.koios.GameService;
import com.koios.game.model.*;import com.koios.game.model.mob.*;import com.koios.game.model.player.Player;import com.koios.game.model.item.Item;import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.map.*;import com.koios.game.map.path.StraightPathFinder;
import com.koios.game.content.combat.Combat;import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;import com.koios.network.out.impl.SendObjectPacket;
public final class MapRuns {
 public static final int MAP_BASE=22410, PORTAL_SCROLL=22406, RETURN_PORTAL=2465, MAP_PORTAL=2466;
 private static final Map<Player,Run> RUNS=new HashMap<>();private static final Map<Player,Base> HOMES=new HashMap<>();
 private static final class Base {RegionInstance region;Location spawn;List<Location> fixtures=new ArrayList<>();List<HubServices.ServiceMob> services=Collections.emptyList();}
 public static final class Run {public RegionInstance region;public Location spawn,returnPortal,basePortal,returnLanding;public final List<MapMob> mobs=new ArrayList<>();public Location slam;public int slamAt,nextSlam=10,bossRecoveryUntil;public int layout;public int tier,mods,portals=6,ticks;public boolean intro,complete;long expires;}
 public static Run run(Player p){return RUNS.get(p);}
 public static boolean inside(Player p){Run run=RUNS.get(p);return run!=null&&run.region.contains(p.getLocation());}
 public static boolean isHome(Player p){Base base=HOMES.get(p);return base!=null&&base.region.contains(p.getLocation());}
 public static boolean metadataContains(Player p,Location at){Run run=RUNS.get(p);Base base=HOMES.get(p);if(inside(p))return run.region.contains(at);if(isHome(p))return base.region.contains(at);return at.getX()<8192||at.getY()<8192;}
 public static boolean sendRegion(Player p){Run run=RUNS.get(p);if(run!=null&&run.region.contains(p.getLocation())){run.region.send(p);com.koios.game.model.item.GroundItemManager.syncMetadata(p);return true;}Base base=HOMES.get(p);if(base!=null&&base.region.contains(p.getLocation())){base.region.send(p);com.koios.game.model.item.GroundItemManager.syncMetadata(p);return true;}return false;}
 public static Item map(int tier,int mods){return map(tier,mods,0);}
 public static Item map(int tier,int mods,int layout){Item item=new Item(layout==1?22413:layout==2?22414:MAP_BASE);item.setRoll(ItemRoll.map(tier,mods));return item;}
 public static boolean isGem(Item item){return item!=null&&item.hasRoll()&&item.getRoll().getGem()!=null;}
 public static Item gem(int level,boolean support){return gem(level,support,java.util.concurrent.ThreadLocalRandom.current().nextInt(support?3:5));}
 public static Item gem(int level,boolean support,int kind){Item item=new Item(support?22412:22411);item.setRoll(ItemRoll.gem(level,support,kind));return item;}
 public static boolean isMap(Item item){return item!=null&&item.hasRoll()&&item.getRoll().getMap()!=null;}
 public static String name(Item item){if(isGem(item)){ItemRoll.GemData gem=item.getRoll().getGem();String[] names=gem.support?new String[]{"Splash","Faster Attacks","Brutality"}:new String[]{"Heavy Strike","Cleave","Whirlwind","Ground Slam","Execute"};return names[gem.kind]+" Gem / Level "+gem.level;}return isMap(item)?AuthoredMaps.name(item.getId()==22413?1:item.getId()==22414?2:0)+" / T"+item.getRoll().getMap().tier:EquipmentBases.name(item);}
 public static void grant(Player p,Item item){if(p.getInventory().hasRoomFor(item)&&p.getInventory().add(item))return;if(!Bank.addToBank(p,item)){p.getArpgRewards().add(item);p.sendMessage("Containers full: reward saved in your mailbox (P / Guide).");}else p.sendMessage("Reward sent to your stash.");}
 public static void home(Player p){
  TestArena.cleanup(p);clearStrikes(p);Base base=HOMES.get(p);
  if(base==null){base=new Base();base.region=new RegionInstance(2952,3216);base.spawn=findSpawn(base.region,2952,3216);HOMES.put(p,base);
   List<Location> cells=reachable(base.spawn,8);
   for(int fixtureId:new int[]{409,13704,3193}){
    ObjectDefinition definition=ObjectDefinition.getById(fixtureId);int width=definition.xLength,height=definition.yLength;
    for(Location cell:cells){
     if(Location.getManhattanDistance(cell,base.spawn)<2)continue;boolean clear=true;
     for(int dx=0;dx<width;dx++)for(int dy=0;dy<height;dy++)if(MapRegion.getClipping(cell.getX()+dx,cell.getY()+dy,0)!=0)clear=false;
     if(!clear)continue;base.fixtures.add(cell);MapRegion.getRegion(cell.getX(),cell.getY()).addObject(new RSObject(cell.getX(),cell.getY(),0,fixtureId,10,0));
     for(int dx=0;dx<width;dx++)for(int dy=0;dy<height;dy++)MapRegion.getRegion(cell.getX()+dx,cell.getY()+dy).setClip(cell.getX()+dx,cell.getY()+dy,0,0x100);
     break;
    }
   }
   if(base.fixtures.size()!=3)throw new IllegalStateException("Base fixtures did not fit the layout");
   try{base.services=HubServices.spawn(p,base.spawn,base.fixtures);}catch(RuntimeException ex){HOMES.remove(p);HubServices.cleanup(p);base.region.close();throw ex;}
  }
  clearSlam(p);Combat.resetCombat(p);p.getCombat().setAggressor(null);p.setPosition(base.spawn.copy());p.getArpgVitals().refill(p.getArpgStats());p.getArpgSkills().refill(p.getArpgStats().get(Stat.MANA));if(!Tutorial.active(p))p.sendMessage("Private house base: map altar, crafting bench and stash. Your map portal returns to the active map.");objective(p);
 }
 public static void tutorialStation(Player p,int station){Base base=HOMES.get(p);if(base==null)return;Location at=base.fixtures.get(station);for(Location cell:reachable(base.spawn,8))if(Location.getManhattanDistance(cell,at)<=2&&MapRegion.getObject(cell.getX(),cell.getY(),0)==null){p.setPosition(cell.copy());return;}}
 public static void fixtures(Player p){Run run=RUNS.get(p);if(inside(p))run.region.sendScenery(p);if(inside(p)&&run.returnPortal!=null)sendPortal(p,RETURN_PORTAL,run.returnPortal);Base base=HOMES.get(p);if(!isHome(p))return;if(run!=null&&run.basePortal!=null)sendPortal(p,MAP_PORTAL,run.basePortal);for(int i=0;i<base.fixtures.size();i++){Location l=base.fixtures.get(i);p.getSession().send(new SendObjectPacket(i==0?409:i==1?13704:3193,l.getX(),l.getY(),0,0,10));}}
 public static boolean object(Player p,int id,int x,int y){
  if(!isHome(p)&&!inside(p))return false;
  if(p.isDead()||p.isTeleporting()||p.isPacketReadDisabled())return true;
  Run run=RUNS.get(p);if(run!=null){Location portal=inside(p)?run.returnPortal:run.basePortal;int expected=inside(p)?RETURN_PORTAL:MAP_PORTAL;
   if(portal!=null&&id==expected&&portal.getX()==x&&portal.getY()==y){if(Location.getManhattanDistance(p.getLocation(),portal)>3)p.sendMessage("Move closer to the portal.");else if(!StraightPathFinder.isInteractionPathClear(p.getLocation(),portal))p.sendMessage("Reach the portal from an open side.");else try{if(inside(p))home(p);else enter(p);}catch(IllegalArgumentException ex){p.sendMessage(ex.getMessage());}return true;}
  }
  Base base=HOMES.get(p);if(isHome(p))for(int i=0;i<base.fixtures.size();i++){
   Location at=base.fixtures.get(i);if(at.getX()==x&&at.getY()==y&&Location.getManhattanDistance(p.getLocation(),at)<=3){if(i==0)Tutorial.device(p);if(i==2)Bank.open(p);else PrototypePanel.show(p,i==0?"maps":"craft");return true;}
  }
  p.sendMessage("Use the map device or a portal to travel.");return true;
 }
 public static void start(Player p,int slot,boolean intro){
  if(p.isDead()||p.isTeleporting()||!isHome(p))throw new IllegalArgumentException("Open maps from your private base");
  if(RUNS.containsKey(p))throw new IllegalArgumentException("Finish or abandon the active map first");
  Item item=intro?null:p.getInventory().get(slot);if(!intro&&!p.getAttribute("intro-complete",false))throw new IllegalArgumentException("Finish the introduction first");if(!intro&&!isMap(item))throw new IllegalArgumentException("Select a map item");
  if(intro&&p.getAttribute("intro-complete",false)&&!Tutorial.active(p))throw new IllegalArgumentException("Introduction already completed");
  Run run=new Run();run.intro=intro;run.tier=intro?1:item.getRoll().getMap().tier;run.mods=intro?0:item.getRoll().getMap().mods;run.expires=System.currentTimeMillis()+30*60*1000;
  run.layout=intro?0:item.getId()==22413?1:item.getId()==22414?2:0;run.region=new RegionInstance(3222,3218,run.layout!=0);
  try{run.spawn=run.layout==0?findSpawn(run.region,3222,3218):AuthoredMaps.build(run.region,run.layout);List<Location> cells=reachable(run.spawn,run.layout==0?25:45);if(cells.size()<30)throw new IllegalStateException("Map spawn area is too small");Collections.shuffle(cells,new Random(intro?1:item.getRoll().getInstanceId().hashCode()));
   int count=intro?4:19;
   for(int i=0;i<count;i++){Location at=cells.get(i);boolean boss=i==count-1;if(!intro&&!boss){Location anchor=cells.get((i/6)*Math.max(1,cells.size()/3));List<Location> pack=reachable(anchor,3);List<Location> free=new ArrayList<>();for(Location candidate:pack){boolean used=false;for(MapMob previous:run.mobs)if(previous.getLocation().equals(candidate))used=true;if(!used&&validSpawn(candidate,run,false)&&Location.getManhattanDistance(candidate,run.spawn)>3)free.add(candidate);}if(!free.isEmpty())at=free.get(i%free.size());}if(boss&&!intro){int farthest=-1;for(Location candidate:cells){boolean used=false;for(MapMob previous:run.mobs)if(previous.getLocation().equals(candidate))used=true;int distance=run.layout==0?Location.getManhattanDistance(candidate,run.spawn):1000-Location.getManhattanDistance(candidate,new Location(run.region.baseX+52,run.region.baseY+67,0));if(!used&&validSpawn(candidate,run,true)&&distance>farthest){at=candidate;farthest=distance;}}}if(!validSpawn(at,run,boss)){at=null;for(Location candidate:cells)if(validSpawn(candidate,run,boss)&&Location.getManhattanDistance(candidate,run.spawn)>3){at=candidate;break;}if(at==null)throw new IllegalStateException("Map has no free enemy footprint");}MapMob mob=new MapMob(p,at,run,boss);MobManager.spawn(mob);run.mobs.add(mob);}
   RUNS.put(p,run);if(!intro)p.getInventory().set(slot,null);enter(p);openPortal(p,run.spawn);if(!Tutorial.active(p))p.sendMessage(intro?"Introduction: equip your sword, spend a passive point, try Q/W, defeat the champion.":"Entered "+AuthoredMaps.name(run.layout)+". Defeat the champion, then return through your portal.");
  }catch(RuntimeException e){RUNS.remove(p);for(MapMob mob:run.mobs)remove(mob);run.region.close();if(!intro&&p.getInventory().get(slot)==null)p.getInventory().set(slot,item);throw e;}
 }
 private static String bossName(Run run){return EncounterNpcs.name(run.intro?82:EncounterNpcs.id(run.layout,0,true));}
 private static int enemyId(Location at,Run run,boolean boss){return run.intro?(boss?82:73):EncounterNpcs.id(run.layout,Math.floorMod(at.getX()*31+at.getY()+run.layout,6),boss);}
 private static boolean validSpawn(Location at,Run run,boolean boss){
  int size=Math.max(1,com.koios.game.def.MobDefinition.get(enemyId(at,run,boss)).getSize());
  for(int x=0;x<size;x++)for(int y=0;y<size;y++){
   int tileX=at.getX()+x,tileY=at.getY()+y;
   if(MapRegion.getClipping(tileX,tileY,0)!=0)return false;
   // Keep the entire enemy footprint clear of the portal staging area.
   if(!run.intro&&Math.max(Math.abs(tileX-run.spawn.getX()),Math.abs(tileY-run.spawn.getY()))<=10)return false;
  }
  for(MapMob previous:run.mobs)if(at.getX()<previous.getX()+previous.getSize()&&at.getX()+size>previous.getX()&&at.getY()<previous.getY()+previous.getSize()&&at.getY()+size>previous.getY())return false;
  return true;
 }
 public static void enter(Player p){if(!isHome(p)||p.isDead()||p.isTeleporting())throw new IllegalArgumentException("Enter from your private map device");Run run=RUNS.get(p);if(run==null)throw new IllegalArgumentException("No active map");if(p.getArpgProgress().level()>=10&&!run.intro){if(run.portals<=0)throw new IllegalArgumentException("No portals remain");run.portals--;}
  p.getSession().send(new com.koios.network.out.impl.SendStringPacket("close",65528));Combat.resetCombat(p);p.getCombat().setAggressor(null);p.setPosition((run.returnLanding==null?run.spawn:run.returnLanding).copy());p.getArpgVitals().refill(p.getArpgStats());p.getArpgSkills().refill(p.getArpgStats().get(Stat.MANA));p.setAttribute("tutorial-await-return",false);objective(p);
 }
 public static void abandon(Player p){if(inside(p))home(p);Run run=RUNS.remove(p);if(run!=null){for(MapMob mob:run.mobs)remove(mob);clearBasePortal(p,run);run.region.close();}objective(p);}
 private static void remove(MapMob mob){mob.strikeAt=0;if(mob.getIndex()>0&&MobManager.mobs[mob.getIndex()]==mob)MobManager.remove(mob);}
 public static void cleanup(Player p){clearSlam(p);clearStrikes(p);Run run=RUNS.remove(p);if(run!=null){for(MapMob mob:run.mobs)remove(mob);clearBasePortal(p,run);run.region.close();}Base base=HOMES.remove(p);HubServices.cleanup(p);if(base!=null)base.region.close();}
 public static void tick(Player p){
  objective(p);Run run=RUNS.get(p);if(run!=null&&System.currentTimeMillis()>run.expires){abandon(p);p.sendMessage("Map expired.");return;}
  if(p.getX()>=8192&&p.getY()>=8192&&!inside(p)&&!isHome(p)){p.setPosition(3094,3493,0);return;}
  if(run==null)return;if(!inside(p)||p.isDead()||p.getHitpoints()<=0||p.isTeleporting()){clearStrikes(p);return;}run.ticks++;
  MapMob boss=null;for(MapMob mob:run.mobs)if(mob.boss&&!mob.isDead())boss=mob;
  if(!run.intro&&boss!=null){
   if(run.slam!=null&&run.ticks>=run.slamAt){
    Location impact=run.slam;clearSlam(p);run.bossRecoveryUntil=run.ticks+2;
    boss.playAnimation(new Animation(boss.getDefinition().getAttackAnimation()));double before=resourcePool(p);
    if(Location.getManhattanDistance(p.getLocation(),impact)<=2&&StraightPathFinder.isInteractionPathClear(impact,p.getLocation())){p.setDamage(new Damage(20+4*run.tier,DamageType.PHYSICAL,true),boss);p.sendMessage("The champion's slam hit you!");}else p.sendMessage("You avoided the champion's slam.");
    p.getSession().send(new com.koios.network.out.impl.SendStringPacket("8|"+boss.getX()+"|"+boss.getY()+"|"+impact.getX()+"|"+impact.getY()+"|"+resourceLoss(p,before)+"|0",65525));
   }
   if(run.slam==null&&run.ticks>=run.nextSlam&&run.ticks>=run.bossRecoveryUntil&&Location.getManhattanDistance(p.getLocation(),boss.getLocation())<=8&&StraightPathFinder.isInteractionPathClear(boss.getLocation(),p.getLocation())){run.slam=p.getLocation().copy();run.slamAt=run.ticks+3;run.nextSlam=run.ticks+12;boss.getFollowing().reset();boss.speak("Crush!");hazard(p,run.slam);}
  }
  objective(p);if(p.isDead()||run.intro&&(run.ticks%5!=0||Tutorial.stage(p)==5))return;
  for (MapMob mob : run.mobs) {
   if (mob.isDead() || mob.getHitpoints() <= 0) {
    mob.strikeAt = 0;
    continue;
   }
   EncounterProfiles.Profile profile = EncounterProfiles.get(mob.role, mob.getId());
   // Authored melee enemies commit to the configured windup. Leaving reach/LOS dodges it.
   if (mob.strikeAt > 0) {
    if (run.ticks >= mob.strikeAt) {
     mob.strikeAt = 0;
     if (canResolveStrike(p, mob, run, profile)) strike(p, mob, profile);
    }
    if (p.isDead()) break;
    continue;
   }
   if (!run.intro && run.ticks < mob.nextAttackTick) continue;
   int distance = Location.getManhattanDistance(p.getLocation(), mob.getLocation());
   if (distance > 8) continue;
   if (mob.boss && (run.slam != null || run.ticks < run.bossRecoveryUntil)) continue;
   int reach = run.intro ? 2 : profile.reach;
   boolean clear = StraightPathFinder.isInteractionPathClear(mob.getLocation(), p.getLocation());
   if (distance > reach || !clear) {
    mob.getFollowing().setFollow(p, FollowType.COMBAT);
    continue;
   }
   mob.getFollowing().reset();
   // Verified skeleton-rig heavy swing5487 peaks near the next600ms tick.
   // A per-NPC profile selects the verified sequence without changing the vanilla cache rig.
   int attackAnimation = profile.attackAnimation >= 0 ? profile.attackAnimation
           : mob.role == 2 || mob.role == 5 ? 711 : mob.getDefinition().getAttackAnimation();
   mob.playAnimation(new Animation(attackAnimation));
   // Cadence is measured swing-start to swing-start, including the windup tick.
   mob.nextAttackTick = run.ticks + (run.intro ? 5 : profile.attackTicks);
   if (!run.intro && profile.windupTicks > 0) {
    mob.strikeAt = run.ticks + profile.windupTicks;
    continue;
   }
   strike(p, mob, profile);
   if (p.isDead()) break;
  }
 }
 /** Revalidate delayed attacks without trusting an old NPC index or player position. */
 private static boolean canResolveStrike(Player p, MapMob mob, Run active,
         EncounterProfiles.Profile profile) {
  return !p.isDead() && p.getHitpoints() > 0 && !p.isTeleporting()
          && !mob.isDead() && mob.getHitpoints() > 0 && !mob.isTeleporting()
          && mob.owner == p && mob.run == active && RUNS.get(p) == active
          && active.region.contains(p.getLocation()) && active.region.contains(mob.getLocation())
          && mob.getIndex() > 0 && MobManager.mobs[mob.getIndex()] == mob
          && mob.getZ() == p.getZ()
          && Location.getManhattanDistance(p.getLocation(), mob.getLocation()) <= profile.reach
          && StraightPathFinder.isInteractionPathClear(mob.getLocation(), p.getLocation());
 }

 /** Apply exactly one confirmed strike; the shared damage/map/elite math is unchanged. */
 private static void strike(Player p, MapMob mob, EncounterProfiles.Profile profile) {
  Run run = mob.run;
  DamageType type = run.intro ? DamageType.PHYSICAL : profile.damageType;
  double multiplier = ((run.mods & 1) != 0 ? 1.3 : 1) * (mob.elite ? 1.35 : 1);
  double before = resourcePool(p);
  int baseDamage = run.intro ? (mob.boss ? 6 + 2 * run.tier : 1 + run.tier)
          : profile.damage(run.tier);
  p.setDamage(new Damage((int) (baseDamage * multiplier), type,
          type == DamageType.PHYSICAL), mob);
  if (mob.role == 2 || mob.role == 5) {
   CombatVisuals.send(p, mob.role == 2 ? 6 : 7, mob, p, resourceLoss(p, before), false);
  }
  if (mob.elite) mob.speak("Empowered");
 }

 /** Pending attacks never survive leaving a map, dying, or closing the instance. */
 private static void clearStrikes(Player p) {
  Run run = RUNS.get(p);
  if (run != null) for (MapMob mob : run.mobs) mob.strikeAt = 0;
 }
 public static void respawn(Player p){
  p.setAttribute("tutorial-await-return",Tutorial.active(p)&&Tutorial.stage(p)>=5&&Tutorial.stage(p)<=8);
  home(p);
 }
 public static void death(Player p){clearSlam(p);clearStrikes(p);Run run=RUNS.get(p);if(run!=null)run.nextSlam=run.ticks+10;if(run!=null&&inside(p)&&p.getArpgProgress().level()>=10&&!run.intro)p.getArpgProgress().loseProgress();}
 private static double resourcePool(Player p){return p.getArpgVitals().life()+p.getArpgVitals().shield();}
 private static int resourceLoss(Player p,double before){return (int)Math.max(0,Math.min(Integer.MAX_VALUE,Math.round(before-resourcePool(p))));}
 private static void drop(Player p,MapMob mob,Item item){com.koios.game.model.item.GroundItemManager.spawnPrivate(p,item,mob.getLocation().copy());}
 /** Commit a monster death once: kill XP and rolled loot are data-driven; mission progression stays here. */
 private static void killed(Player p,MapMob mob){
  if(p.getCombat().getTarget()==mob)Combat.resetCombat(p);
  else if(p.getFollowing().getInteracting()==mob)p.getFollowing().reset();
  Run run=mob.run;
  if(mob.boss)clearSlam(p);
  Random random=new Random();
  MapDropTables.Context context=new MapDropTables.Context(run.tier,mob.getId(),run.mods,run.intro,mob.elite,mob.boss);
  MapDropTables.Reward reward=MapDropTables.roll(context,random);
  p.getArpgProgress().award(reward.experience);
  // Private ground entities preserve each rolled item's UUID and require normal pickup.
  for(Item item:reward.items)drop(p,mob,item);
  // This guaranteed progression map/return portal is separate from optional balance-table rolls.
  if(mob.boss){run.complete=true;if(run.intro){p.setAttribute("intro-complete",true);drop(p,mob,map(1,0,1));}else drop(p,mob,map(Math.min(10,run.tier+1),0,random.nextInt(3)));try{openPortal(p,mob.getLocation());}catch(IllegalArgumentException ex){openPortal(p,run.spawn);}objective(p);p.sendMessage("MAP COMPLETE: "+bossName(run)+" defeated! Collect your loot, then enter the return portal to your base.");}
 }
 public static String objectiveText(Player p){Run run=RUNS.get(p);if(!inside(p))return "";int remaining=0;MapMob boss=null;for(MapMob mob:run.mobs)if(!mob.isDead()){remaining++;if(mob.boss)boss=mob;}
  String heading=(run.complete?"MAP COMPLETE":"MAP OBJECTIVE")+" / "+(run.intro?"Introduction":AuthoredMaps.name(run.layout)+" T"+run.tier);
  if(run.complete)return heading+"\n"+bossName(run)+" defeated\nCollect loot, then enter the return portal";
  String direction="";if(boss!=null){int dx=boss.getX()-p.getX(),dy=boss.getY()-p.getY();direction=(dy>2?"north":dy< -2?"south":"")+(Math.abs(dx)>2?(Math.abs(dy)>2?"-":"")+(dx>0?"east":"west"):"");if(direction.isEmpty())direction="nearby";direction="Boss: "+direction+" / "+Location.getManhattanDistance(p.getLocation(),boss.getLocation())+" tiles";}
  return heading+"\nDefeat the "+bossName(run)+"\n"+direction+" / Enemies: "+remaining+"\n"+(run.slam==null?(run.ticks<run.bossRecoveryUntil?"CHAMPION RECOVERING: attack now":"Portal Scroll: open a return portal"):"SLAM! Leave marked tiles / "+String.format(Locale.ROOT,"%.1f",Math.max(0,run.slamAt-run.ticks)*0.6)+"s");
 }
 public static void objective(Player p){
  Run run=RUNS.get(p);String hint="";MapMob boss=null;
  if(inside(p)){if(run.complete&&run.returnPortal!=null)hint="portal:"+run.returnPortal;else for(MapMob mob:run.mobs)if(mob.boss&&!mob.isDead()){boss=mob;hint="boss:"+mob.getIndex();}}
  if(!hint.equals(p.getAttribute("map-hint",""))){p.setAttribute("map-hint",hint);if(boss!=null)p.getSession().send(new com.koios.network.out.impl.SendEntityHintIconPacket(boss));else if(!hint.isEmpty())p.getSession().send(new com.koios.network.out.impl.SendLocationHintIconPacket(2,run.returnPortal.getX(),run.returnPortal.getY(),60));else p.getSession().send(new com.koios.network.out.impl.SendEntityHintIconPacket(0,0));}
  String text=Tutorial.showing(p)?Tutorial.tracker(p):objectiveText(p);if(text.equals(p.getAttribute("map-objective","~")))return;p.setAttribute("map-objective",text);p.getSession().send(new com.koios.network.out.impl.SendStringPacket(Base64.getEncoder().encodeToString(text.getBytes(java.nio.charset.StandardCharsets.UTF_8)),65530));
 }
 private static void hazard(Player p,Location at){p.getSession().send(new com.koios.network.out.impl.SendStringPacket(at==null?"":at.getX()+"|"+at.getY()+"|2|1800",65527));}
 private static void clearSlam(Player p){Run run=RUNS.get(p);if(run!=null&&run.slam!=null){run.slam=null;hazard(p,null);}}
 public static boolean useScroll(Player p,int slot){Item item=p.getInventory().get(slot);if(item==null||item.getId()!=PORTAL_SCROLL)return false;
  if(!inside(p)||p.isDead()||p.isTeleporting()||p.isTrading()||p.getBankField().isBanking()){p.sendMessage("Portal Scrolls open a return portal while inside your map.");return true;}
  try{openPortal(p,p.getLocation());p.getInventory().remove(PORTAL_SCROLL,1);Tutorial.action(p,"scroll");p.sendMessage("Return portal opened. Enter it to travel to your base; your map stays open.");}catch(IllegalArgumentException ex){p.sendMessage(ex.getMessage());}return true;
 }
 private static void sendPortal(Player p,int id,Location at){p.getSession().send(new SendObjectPacket(id,at.getX(),at.getY(),at.getZ(),0,10));}
 private static Location portalTile(Location near){for(Location at:reachable(near,5))if(Location.getManhattanDistance(at,near)>=1&&MapRegion.getObject(at.getX(),at.getY(),0)==null&&MapRegion.getClipping(at.getX(),at.getY(),0)==0)return at;throw new IllegalArgumentException("Move to an open area to create a portal");}
 private static void placePortal(Location at,int id){MapRegion.getRegion(at.getX(),at.getY()).addObject(new RSObject(at.getX(),at.getY(),at.getZ(),id,10,0));}
 private static void clearBasePortal(Player p,Run run){if(run.basePortal!=null){MapRegion region=MapRegion.getRegion(run.basePortal.getX(),run.basePortal.getY());if(region!=null)region.removeObject(run.basePortal.getX(),run.basePortal.getY(),0);if(isHome(p))sendPortal(p,-1,run.basePortal);run.basePortal=null;}}
 public static void openPortal(Player p,Location near){Run run=RUNS.get(p);if(run==null||!inside(p))throw new IllegalArgumentException("No active map");Location next=portalTile(near);Base base=HOMES.get(p);Location homeTile=run.basePortal==null?portalTile(base.spawn):run.basePortal;
  if(run.returnPortal!=null){MapRegion.getRegion(run.returnPortal.getX(),run.returnPortal.getY()).removeObject(run.returnPortal.getX(),run.returnPortal.getY(),0);sendPortal(p,-1,run.returnPortal);}
  run.returnPortal=next;run.returnLanding=near.copy();run.basePortal=homeTile;placePortal(next,RETURN_PORTAL);placePortal(homeTile,MAP_PORTAL);sendPortal(p,RETURN_PORTAL,next);
 }
 private static Location findSpawn(RegionInstance region,int sourceX,int sourceY){Location desired=region.translate(new Location(sourceX,sourceY,0));for(int radius=0;radius<15;radius++)for(int x=-radius;x<=radius;x++)for(int y=-radius;y<=radius;y++){Location at=new Location(desired.getX()+x,desired.getY()+y,0);if(region.contains(at)&&MapRegion.getClipping(at.getX(),at.getY(),0)==0)return at;}throw new IllegalStateException("No walkable spawn");}
 public static List<Location> reachable(Location start,int radius){List<Location> out=new ArrayList<>();ArrayDeque<Location> queue=new ArrayDeque<>();Set<Long> seen=new HashSet<>();queue.add(start);while(!queue.isEmpty()){Location at=queue.remove();long key=((long)at.getX()<<32)|(at.getY()&0xffffffffL);if(!seen.add(key)||Location.getManhattanDistance(start,at)>radius)continue;if(MapRegion.getClipping(at.getX(),at.getY(),0)!=0)continue;out.add(at);for(int[] direction:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}){Location next=new Location(at.getX()+direction[0],at.getY()+direction[1],0);if(StraightPathFinder.isInteractionPathClear(at,next))queue.add(next);}}return out;}
 private static boolean repositionCaster(MapMob mob){
  Player target=mob.owner;if(!mob.getEF().canWalk(mob,false))return false;
  Location start=mob.getLocation();ArrayDeque<Location> queue=new ArrayDeque<>();Map<Long,Integer> firstSteps=new HashMap<>();
  queue.add(start.copy());firstSteps.put(((long)start.getX()<<32)|(start.getY()&0xffffffffL),-1);
  while(!queue.isEmpty()){
   Location at=queue.remove();int first=firstSteps.get(((long)at.getX()<<32)|(at.getY()&0xffffffffL));
   if(first>=0&&!at.equals(target.getLocation())&&Location.getManhattanDistance(at,target.getLocation())<=6&&StraightPathFinder.isInteractionPathClear(at,target.getLocation())){
    if(mob.getMovementHandler().canMoveTo(first)){Walking.walk(mob,first);return true;}
   }
   for(int[] direction:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}){
    Location next=new Location(at.getX()+direction[0],at.getY()+direction[1],at.getZ());long key=((long)next.getX()<<32)|(next.getY()&0xffffffffL);
    if(firstSteps.containsKey(key)||!mob.run.region.contains(next)||Location.getManhattanDistance(start,next)>12||next.equals(target.getLocation()))continue;
    boolean clear=true;for(int x=0;x<mob.getSize();x++)for(int y=0;y<mob.getSize();y++)if(MapRegion.getClipping(next.getX()+x,next.getY()+y,next.getZ())!=0)clear=false;
    if(!clear||!StraightPathFinder.isInteractionPathClear(at,next))continue;
    int step=first<0?com.koios.Constants.getDirection(start.getX(),start.getY(),next.getX(),next.getY()):first;
    if(first<0&&!mob.getMovementHandler().canMoveTo(step))continue;
    firstSteps.put(key,step);queue.add(next);
   }
  }
  return false;
 }
 public static final class MapMob extends Mob {
  final Player owner;final Run run;final boolean boss;public final int role;public final boolean elite;public int nextAttackTick;
  /** Run-local impact tick; zero means no committed heavy swing. Never persisted. */
  public int strikeAt;
  MapMob(Player owner,Location at,Run run,boolean boss){super(enemyId(at,run,boss),at);this.role=boss?6:run.intro?0:Math.floorMod(at.getX()*31+at.getY()+run.layout,6);this.elite=!run.intro&&!boss&&run.mobs.size()%6==0;this.owner=owner;this.run=run;this.boss=boss;summoner=owner;nextAttackTick=run.ticks+3+Math.floorMod(at.getX()+at.getY(),3);hitpoints=maximumHitpoints=(run.intro?(boss?45:12):EncounterProfiles.get(role,getId()).health(run.tier,boss))*((run.mods&2)!=0?2:1);if(elite)hitpoints=maximumHitpoints=(int)(hitpoints*1.6);setSkill(new Skill(this,20+run.tier*5,run.tier*2,hitpoints));}
  public void update() {
   if (strikeAt > 0) {
    getFollowing().reset();
    return;
   }
   if (!inside(owner) || MapRuns.run(owner) != run) {
    getFollowing().reset();
    return;
   }
   if (boss && (run.slam != null || run.ticks < run.bossRecoveryUntil)) {
    getFollowing().reset();
    return;
   }
   if (!isDead() && owner.getZ() == getZ()) {
    if ((role == 2 || role == 5) && getFollowing().getInteracting() == owner
            && Location.getManhattanDistance(getLocation(), owner.getLocation()) <= 12
            && !StraightPathFinder.isInteractionPathClear(getLocation(), owner.getLocation())
            && repositionCaster(this)) return;
    getFollowing().process();
   }
  }
  public void hurt(int damage,EntityCharacter source){if(isDead())return;hitpoints=Math.max(boss&&Tutorial.stage(owner)==5?1:0,hitpoints-Math.max(0,damage));if(hitpoints==0){strikeAt=0;setDead(true);getFollowing().reset();getMovementHandler().reset();getMovementHandler().resetMoveDirections();Combat.resetCombat(this);setFaceIndex(0,false);playAnimation(new Animation(new int[]{5575,5491,836,5329,6576,5542,67}[role]));killed(owner,this);final MapMob corpse=this;GameService.getTaskScheduler().schedule(new com.koios.task.Task(collapseTicks()){protected void execute(){if(MapRuns.run(owner)==run&&corpse.getIndex()>0&&MobManager.mobs[corpse.getIndex()]==corpse){corpse.setVisible(false);MapRuns.remove(corpse);}stop();}});}}
  /** Full verified cache sequence durations, rounded up from 50Hz cycles to 600ms ticks. */
  public int collapseTicks(){return new int[]{9,6,10,6,7,6,12}[role];}
  public boolean isAttackable(){return !isDead()&&super.isAttackable();}
  public void death(){}
 }
}
