package com.koios.game.arpg;
import java.util.*;
import java.util.function.BooleanSupplier;
/** Weapon-based hits. Bounded sampling for extreme rates prevents unbounded server work. */
public final class MeleeRolls {
 public static final class Batch {
  public final long attacks;public final int damage;public final boolean critical;
  Batch(long attacks,int damage,boolean critical){this.attacks=attacks;this.damage=damage;this.critical=critical;}
 }
 public static Batch roll(Map<Stat,Double> stats,long attacks,double effectiveness,Random random,BooleanSupplier accuracy){
  if(attacks<0||!Double.isFinite(effectiveness)||effectiveness<0)throw new IllegalArgumentException("Invalid attack");
  double min=stats.getOrDefault(Stat.PHYSICAL_MIN,0d),max=Math.max(min,stats.getOrDefault(Stat.PHYSICAL_MAX,0d));
  int samples=(int)Math.min(attacks,4096);double total=0;boolean critical=false;
  for(int i=0;i<samples;i++){
   if(!accuracy.getAsBoolean())continue;
   boolean crit=random.nextDouble()*100<stats.getOrDefault(Stat.CRITICAL_CHANCE,0d);
   double damage=(min+(max-min)*random.nextDouble())*effectiveness;
   if(crit){damage*=stats.getOrDefault(Stat.CRITICAL_MULTIPLIER,150d)/100;critical=true;}
   total+=damage;
  }
  if(samples>0)total*=((double)attacks/samples);
  // Stochastic rounding preserves small fractional hits without inflating damage.
  double rounded=Math.floor(total)+(random.nextDouble()<total-Math.floor(total)?1:0);
  return new Batch(attacks,(int)Math.min(Integer.MAX_VALUE,rounded),critical);
 }
}
