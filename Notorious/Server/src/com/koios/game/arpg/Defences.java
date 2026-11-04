package com.koios.game.arpg;
import java.util.*;
import com.koios.game.model.*;
import com.koios.game.model.player.Player;
public final class Defences {
 public static double hitChance(double accuracy,double evasion){return Math.max(0.05,Math.min(1,accuracy/(Math.max(1,accuracy)+Math.max(0,evasion))));}
 public static double mitigate(double raw,DamageType type,Map<Stat,Double> stats,boolean dot){
  if(raw<=0)return 0;
  if(type==DamageType.PHYSICAL){if(dot)return raw;double armour=stats.get(Stat.ARMOUR);return raw*(1-Math.min(0.9,armour/(armour+5*raw)));}
  Stat resistance=type==DamageType.FIRE?Stat.FIRE_RESISTANCE:type==DamageType.COLD?Stat.COLD_RESISTANCE:type==DamageType.LIGHTNING?Stat.LIGHTNING_RESISTANCE:Stat.CHAOS_RESISTANCE;
  return raw*(1-stats.get(resistance)/100);
 }
 public static int receive(Player p,Damage damage,EntityCharacter source){
  Map<Stat,Double> stats=p.getArpgStats();p.getArpgVitals().sync(stats);
  if(p.isInvulnerable()||p.isTeleporting()){damage.setDamage(0);return 0;}
  boolean dot=damage.getHitMask()==Damage.HitMasks.POISON;
  DamageType type=damage.getType();
  if(type==null)type=dot?DamageType.CHAOS:damage.getHitIcon()==Damage.HitIcons.MAGIC?DamageType.FIRE:DamageType.PHYSICAL;
  boolean attack=damage.isAttack() || damage.getHitIcon()==Damage.HitIcons.MELEE || damage.getHitIcon()==Damage.HitIcons.RANGE;
  Random random=java.util.concurrent.ThreadLocalRandom.current();
  double accuracy=source!=null&&source.isMob()?100+Math.max(1,source.getAttack())*10:100;
  if(!dot&&attack&&(random.nextDouble()>hitChance(accuracy,stats.get(Stat.EVASION))||random.nextDouble()*100<stats.get(Stat.BLOCK_CHANCE))){damage.setDamage(0);return 0;}
  double taken=mitigate(damage.getDamage(),type,stats,dot);
  // Round once after mitigation so tiny hits do not become free damage.
  int rounded=(int)Math.min(Integer.MAX_VALUE,Math.floor(taken)+(random.nextDouble()<taken-Math.floor(taken)?1:0));
  double lifeLoss=p.getArpgVitals().absorb(rounded,type==DamageType.CHAOS);
  int hpLoss=(int)Math.min(p.getHitpoints(),Math.ceil(lifeLoss));
  damage.setDamage(rounded);return hpLoss;
 }
}
