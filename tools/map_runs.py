from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
p=r/'game/arpg/CharacterProgress.java';s=p.read_text().replace('public void gain(int damage)', 'public void loseProgress(){long floor=100L*(level()-1)*(level()-1);xp=Math.max(floor,xp-Math.max(1,(next()-floor)/10));}\n public void award(long amount){if(amount>0)restore(Math.min(980100,xp+amount));}\n public void gain(int damage)');p.write_text(s)
(r/'game/arpg/MapRuns.java').write_text('''package com.koios.game.arpg;
import java.util.*;
import com.koios.GameService;
import com.koios.game.model.*;import com.koios.game.model.mob.*;import com.koios.game.model.player.Player;import com.koios.game.model.item.Item;import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.map.*;import com.koios.game.map.path.StraightPathFinder;
import com.koios.game.content.combat.Combat;import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;import com.koios.network.out.impl.SendObjectPacket;
public final class MapRuns {
 public static final int MAP_BASE=8007;
 private static final Map<Player,Run> RUNS=new HashMap<>();private static final Map<Player,Base> HOMES=new HashMap<>();
 private static final class Base {RegionInstance region;Location spawn;List<Location> fixtures=new ArrayList<>();}
 public static final class Run {public RegionInstance region;public Location spawn;public final List<MapMob> mobs=new ArrayList<>();public int tier,mods,portals=6,ticks;public boolean intro,complete;long expires;}
 public static Run run(Player p){return RUNS.get(p);}
 public static boolean inside(Player p){Run run=RUNS.get(p);return run!=null&&run.region.contains(p.getLocation());}
 public static boolean isHome(Player p){Base base=HOMES.get(p);return base!=null&&base.region.contains(p.getLocation());}
 public static boolean sendRegion(Player p){Run run=RUNS.get(p);if(run!=null&&run.region.contains(p.getLocation())){run.region.send(p);return true;}Base base=HOMES.get(p);if(base!=null&&base.region.contains(p.getLocation())){base.region.send(p);return true;}return false;}
 public static Item map(int tier,int mods){Item item=new Item(MAP_BASE);item.setRoll(ItemRoll.map(tier,mods));return item;}
 public static boolean isMap(Item item){return item!=null&&item.hasRoll()&&item.getRoll().getMap()!=null;}
 public static String name(Item item){return isMap(item)?"Forsaken Settlement / T"+item.getRoll().getMap().tier:item.getDefinition().getName();}
 public static void grant(Player p,Item item){if(p.getInventory().hasRoomFor(item)&&p.getInventory().add(item))return;if(!Bank.addToBank(p,item))throw new IllegalStateException("Inventory and stash are full");p.sendMessage("Reward sent to your stash.");}
 public static void home(Player p){
  TestArena.cleanup(p);Base base=HOMES.get(p);
  if(base==null){base=new Base();base.region=new RegionInstance(2952,3216);base.spawn=findSpawn(base.region,2952,3216);HOMES.put(p,base);
   List<Location> cells=reachable(base.spawn,8);for(Location cell:cells)if(Location.getManhattanDistance(cell,base.spawn)>=2&&base.fixtures.size()<3){base.fixtures.add(cell);MapRegion region=MapRegion.getRegion(cell.getX(),cell.getY());int id=base.fixtures.size()==1?409:base.fixtures.size()==2?14930:3193;region.addObject(new RSObject(cell.getX(),cell.getY(),0,id,10,0));region.setClip(cell.getX(),cell.getY(),0,0x100);}
  }
  Combat.resetCombat(p);p.getCombat().setAggressor(null);p.setPosition(base.spawn.copy());p.getArpgVitals().refill(p.getArpgStats());p.sendMessage("Private house base: map altar, crafting bench and stash. P opens the prototype panel.");
 }
 public static void fixtures(Player p){Base base=HOMES.get(p);if(!isHome(p))return;for(int i=0;i<base.fixtures.size();i++){Location l=base.fixtures.get(i);p.getSession().send(new SendObjectPacket(i==0?409:i==1?14930:3193,l.getX(),l.getY(),0,0,10));}}
 public static boolean object(Player p,int id,int x,int y){
  if(!isHome(p)&&!inside(p))return false;
  Base base=HOMES.get(p);if(isHome(p))for(int i=0;i<base.fixtures.size();i++){
   Location at=base.fixtures.get(i);if(at.getX()==x&&at.getY()==y&&Location.getManhattanDistance(p.getLocation(),at)<=3){if(i==2)Bank.open(p);else PrototypePanel.show(p,i==0?"maps":"craft");return true;}
  }
  p.sendMessage("Map scenery is decorative in this prototype. Use P for map controls.");return true;
 }
 public static void start(Player p,int slot,boolean intro){
  if(p.isDead()||p.isTeleporting()||!isHome(p))throw new IllegalArgumentException("Open maps from your private base");
  if(RUNS.containsKey(p))throw new IllegalArgumentException("Finish or abandon the active map first");
  Item item=intro?null:p.getInventory().get(slot);if(!intro&&!isMap(item))throw new IllegalArgumentException("Select a map item");
  if(intro&&p.getAttribute("intro-complete",false))throw new IllegalArgumentException("Introduction already completed");
  Run run=new Run();run.intro=intro;run.tier=intro?1:item.getRoll().getMap().tier;run.mods=intro?0:item.getRoll().getMap().mods;run.expires=System.currentTimeMillis()+30*60*1000;
  run.region=new RegionInstance(3222,3218);
  try{run.spawn=findSpawn(run.region,3222,3218);List<Location> cells=reachable(run.spawn,25);if(cells.size()<30)throw new IllegalStateException("Map spawn area is too small");Collections.shuffle(cells,new Random(intro?1:item.getRoll().getInstanceId().hashCode()));
   int count=intro?4:19;
   for(int i=0;i<count;i++){Location at=cells.get(i);boolean boss=i==count-1;MapMob mob=new MapMob(p,at,run,boss);MobManager.spawn(mob);run.mobs.add(mob);}
   RUNS.put(p,run);if(!intro)p.getInventory().set(slot,null);enter(p);p.sendMessage(intro?"Introduction: equip your sword, spend a passive point, try Q/W, defeat the champion.":"Map opened: private settlement with copied terrain, buildings and collision.");
  }catch(RuntimeException e){for(MapMob mob:run.mobs)remove(mob);run.region.close();throw e;}
 }
 public static void enter(Player p){Run run=RUNS.get(p);if(run==null)throw new IllegalArgumentException("No active map");if(p.getArpgProgress().level()>=10&&!run.intro){if(run.portals<=0)throw new IllegalArgumentException("No portals remain");run.portals--;}
  Combat.resetCombat(p);p.getCombat().setAggressor(null);p.setPosition(run.spawn.copy());p.getArpgVitals().refill(p.getArpgStats());
 }
 public static void abandon(Player p){if(inside(p))home(p);Run run=RUNS.remove(p);if(run!=null){for(MapMob mob:run.mobs)remove(mob);run.region.close();}}
 private static void remove(MapMob mob){if(mob.getIndex()>0&&MobManager.mobs[mob.getIndex()]==mob)MobManager.remove(mob);}
 public static void cleanup(Player p){abandon(p);Base base=HOMES.remove(p);if(base!=null)base.region.close();}
 public static void tick(Player p){
  Run run=RUNS.get(p);if(run!=null&&System.currentTimeMillis()>run.expires){abandon(p);p.sendMessage("Map expired.");return;}
  if(p.getX()>=8192&&p.getY()>=8192&&!inside(p)&&!isHome(p)){p.setPosition(3094,3493,0);return;}
  if(run==null||!inside(p)||p.isDead()||p.isTeleporting())return;run.ticks++;
  if(run.ticks%5!=0)return;
  for(MapMob mob:run.mobs){if(mob.isDead())continue;int distance=Location.getManhattanDistance(p.getLocation(),mob.getLocation());
   if(distance<=8){mob.getFollowing().setFollow(p,FollowType.COMBAT);if(distance<=2&&StraightPathFinder.isInteractionPathClear(mob.getLocation(),p.getLocation())){mob.playAnimation(new Animation(422));DamageType type=DamageType.values()[(mob.getIndex()+run.ticks/5)%5];double multiplier=(run.mods&1)!=0?1.3:1;p.setDamage(new Damage((int)((mob.boss?8:2)*Math.max(1,run.tier)*multiplier),type,type==DamageType.PHYSICAL),mob);if(p.isDead())break;}}
  }
 }
 public static void death(Player p){Run run=RUNS.get(p);if(run!=null&&inside(p)&&p.getArpgProgress().level()>=10&&!run.intro)p.getArpgProgress().loseProgress();}
 private static void killed(Player p,MapMob mob){Run run=mob.run;Random random=new Random();int level=run.intro?1:Math.max(1,run.tier*5);
  p.getArpgProgress().award(mob.boss?100L*run.tier:20L*run.tier);grant(p,new Item(995,mob.boss?300:30));
  if(random.nextInt(3)==0||mob.boss)grant(p,AffixCatalog.equipment(level,random.nextBoolean(),random));
  grant(p,new Item(new int[]{561,562,563,564,554,555}[random.nextInt(6)],mob.boss?3:1));
  if(!run.intro&&random.nextInt(12)==0)grant(p,map(run.tier,0));
  if(mob.boss){run.complete=true;if(run.intro){p.setAttribute("intro-complete",true);grant(p,map(1,0));}else grant(p,map(Math.min(10,run.tier+1),0));p.sendMessage("Champion defeated! Rewards collected. P: Return home, then abandon the cleared map.");}
 }
 private static Location findSpawn(RegionInstance region,int sourceX,int sourceY){Location desired=region.translate(new Location(sourceX,sourceY,0));for(int radius=0;radius<15;radius++)for(int x=-radius;x<=radius;x++)for(int y=-radius;y<=radius;y++){Location at=new Location(desired.getX()+x,desired.getY()+y,0);if(region.contains(at)&&MapRegion.getClipping(at.getX(),at.getY(),0)==0)return at;}throw new IllegalStateException("No walkable spawn");}
 public static List<Location> reachable(Location start,int radius){List<Location> out=new ArrayList<>();ArrayDeque<Location> queue=new ArrayDeque<>();Set<Long> seen=new HashSet<>();queue.add(start);while(!queue.isEmpty()){Location at=queue.remove();long key=((long)at.getX()<<32)|(at.getY()&0xffffffffL);if(!seen.add(key)||Location.getManhattanDistance(start,at)>radius)continue;if(MapRegion.getClipping(at.getX(),at.getY(),0)!=0)continue;out.add(at);for(int[] direction:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}){Location next=new Location(at.getX()+direction[0],at.getY()+direction[1],0);if(StraightPathFinder.isInteractionPathClear(at,next))queue.add(next);}}return out;}
 public static final class MapMob extends Mob {
  final Player owner;final Run run;final boolean boss;
  MapMob(Player owner,Location at,Run run,boolean boss){super(boss?82:73,at);this.owner=owner;this.run=run;this.boss=boss;summoner=owner;hitpoints=maximumHitpoints=(run.intro?(boss?45:12):(boss?180:30)*run.tier)*((run.mods&2)!=0?2:1);setSkill(new Skill(this,20+run.tier*5,run.tier*2,hitpoints));}
  public void update(){if(!isDead()&&owner.getZ()==getZ())getFollowing().process();}
  public void hurt(int damage,EntityCharacter source){if(isDead())return;hitpoints=Math.max(0,hitpoints-Math.max(0,damage));if(hitpoints==0){setDead(true);setVisible(false);killed(owner,this);remove(this);}}
  public void death(){}
 }
}
''')
