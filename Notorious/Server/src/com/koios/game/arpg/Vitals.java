package com.koios.game.arpg;
import java.util.Map;
/** Real resource pools. Equipment changes clamp current values and never refill them. */
public final class Vitals {
 private double life=100,shield,quiet;
 private boolean initialized;
 public void sync(Map<Stat,Double> stats){
  if(!initialized){life=stats.get(Stat.LIFE);shield=stats.get(Stat.ENERGY_SHIELD);initialized=true;}
  life=Math.min(life,stats.get(Stat.LIFE));shield=Math.min(shield,stats.get(Stat.ENERGY_SHIELD));
 }
 public double life(){return life;}
 public double shield(){return shield;}
 public void setLife(double value,double max){initialized=true;life=Math.max(0,Math.min(max,value));}
 public void refill(Map<Stat,Double> stats){initialized=true;life=stats.get(Stat.LIFE);shield=stats.get(Stat.ENERGY_SHIELD);quiet=0;}
 public void restore(double hp,double es){if(!Double.isFinite(hp)||!Double.isFinite(es)||hp<0||es<0)throw new IllegalArgumentException("Invalid pools");initialized=true;life=Math.max(1,hp);shield=es;quiet=0;}
 public double absorb(double damage,boolean bypass){
  if(damage<=0)return 0;quiet=0;
  double absorbed=bypass?0:Math.min(shield,damage);shield-=absorbed;return damage-absorbed;
 }
 public void tick(Map<Stat,Double> stats,double seconds,boolean dead){
  sync(stats);if(dead||life<=0)return;
  life=Math.min(stats.get(Stat.LIFE),life+stats.get(Stat.LIFE_REGEN)*seconds);
  double old=quiet;quiet+=seconds;
  double rechargeTime=Math.max(0,quiet-2)-Math.max(0,old-2);
  shield=Math.min(stats.get(Stat.ENERGY_SHIELD),shield+stats.get(Stat.ENERGY_SHIELD)*0.333*rechargeTime*(1+stats.get(Stat.ES_RECHARGE)/100));
 }
}
