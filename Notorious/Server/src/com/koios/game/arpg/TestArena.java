package com.koios.game.arpg;
import java.util.*;
import com.koios.GameService;
import com.koios.game.map.MapRegion;
import com.koios.game.model.*;
import com.koios.game.model.mob.*;
import com.koios.game.model.player.Player;
import com.koios.game.content.combat.Combat;
public final class TestArena {
 private static final Map<Player,Run> runs=new HashMap<>();
 private static final class Run {Location back,centre;List<ArenaMob> mobs=new ArrayList<>();int ticks;boolean active=true;}
 public static boolean inside(EntityCharacter e){if(e.isPlayer())return runs.containsKey(e.toPlayer());if(e instanceof ArenaMob)return true;return false;}
 public static void enter(Player p){
  if(GameService.isOnlineMode())return;
  Run old=runs.get(p);Location back=old==null?(p.getZ()>3?new Location(3094,3493,0):p.getLocation().copy()):old.back;
  cleanup(p);Run run=new Run();run.back=back;run.centre=findFloor(8+4*Math.max(1,p.getIndex()));runs.put(p,run);p.setPosition(run.centre.copy());
  int[][] offsets={{2,0},{2,1},{2,-1},{3,0},{3,1},{3,-1},{5,0}};
  for(int i=0;i<offsets.length;i++){
   DamageType type=DamageType.values()[i%5];boolean boss=i==6;
   ArenaMob mob=new ArenaMob(p,new Location(run.centre.getX()+offsets[i][0],run.centre.getY()+offsets[i][1],run.centre.getZ()),type,boss);
   MobManager.spawn(mob);run.mobs.add(mob);
  }
  p.getArpgVitals().refill(p.getArpgStats());p.getArpgSkills().refill(p.getArpgStats().get(Stat.MANA));p.sendMessage("Test arena: seven targets. ::arena pause, ::arena reset, ::arena leave");p.sendMessage("Use Q/W and supports. ::arpgarmour armour/evasion/es/hybrid creates test armour.");
 }
 private static Location findFloor(int z){
  for(int x=2980;x<=3010;x++)for(int y=3370;y<=3400;y++){
   boolean clear=true;for(int dx=-1;dx<=7&&clear;dx++)for(int dy=-2;dy<=2;dy++)if(MapRegion.getRegion(x+dx,y+dy)==null||MapRegion.getClipping(x+dx,y+dy,0)!=0){clear=false;break;}
   if(clear)return new Location(x,y,z);
  }
  throw new IllegalStateException("No clear arena floor found in the loaded Falador region");
 }
 public static void leave(Player p){Run run=runs.get(p);Location back=run==null?new Location(3094,3493,0):run.back;cleanup(p);p.setPosition(back);p.getCombat().setAggressor(null);}
 public static void pause(Player p){Run run=runs.get(p);if(run!=null){run.active=!run.active;p.sendMessage("Arena attacks "+(run.active?"enabled":"paused"));}}
 public static void cleanup(Player p){Run run=runs.remove(p);if(run!=null)for(ArenaMob m:run.mobs)if(m.getIndex()>0&&MobManager.mobs[m.getIndex()]==m)MobManager.remove(m);Combat.resetCombat(p);}
 public static void tick(Player p){Run run=runs.get(p);if(run==null)return;
  if(p.isDisconnected()||p.isDead()||p.getZ()!=run.centre.getZ()||Location.getManhattanDistance(p.getLocation(),run.centre)>25){cleanup(p);return;}
  if(!run.active||++run.ticks%5!=0||p.isTeleporting())return;
  for(ArenaMob m:run.mobs)if(!m.isDead()&&m.isVisible()&&Location.getManhattanDistance(p.getLocation(),m.getLocation())<=8){
   m.speak(m.getName());m.playAnimation(new Animation(422));p.setDamage(new Damage(m.boss?18:4,m.type,m.type==DamageType.PHYSICAL),m);if(p.isDead())break;
  }
 }
 public static final class ArenaMob extends Mob {
  final DamageType type;final boolean boss;
  ArenaMob(Player owner,Location location,DamageType type,boolean boss){super(1,location);this.type=type;this.boss=boss;this.summoner=owner;hitpoints=maximumHitpoints=boss?1200:200;}
  public String getName(){return (boss?"Arena champion":"Arena "+type.toString().toLowerCase()+" target");}
  public void update(){} // Test attacks are driven by the owning arena, never legacy AI.
  public void hurt(int damage,EntityCharacter source){hitpoints=Math.max(0,hitpoints-Math.max(0,damage));if(hitpoints==0){setDead(true);setVisible(false);MobManager.remove(this);}}
  public void death(){} // No legacy loot or respawn in the test arena.
 }
}
