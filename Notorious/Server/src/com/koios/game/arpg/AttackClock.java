package com.koios.game.arpg;
/** Simulation-time credit: no idle catch-up and no whole-tick rounding of APS. */
public final class AttackClock {
 private double credit;
 private String weapon;
 private Object target;
 public void reset(){credit=0;weapon=null;target=null;}
 public long advance(String weapon,Object target,double aps,double seconds){
  if(!Double.isFinite(aps)||aps<0||!Double.isFinite(seconds)||seconds<0)throw new IllegalArgumentException("Invalid timing");
  if(aps==0){reset();return 0;}
  if(!weapon.equals(this.weapon)||target!=this.target){credit=1;this.weapon=weapon;this.target=target;}
  else credit+=aps*seconds;
  double whole=Math.floor(credit+1e-9);
  long due=(long)Math.min(Long.MAX_VALUE,whole);
  credit=Math.max(0,credit-whole);return due;
 }
}
