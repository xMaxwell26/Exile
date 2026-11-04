package com.koios.game.arpg;
public final class CharacterProgress {
 private long xp;
 public static boolean combatSkill(int skill){return skill>=0&&skill<=6||skill==23;}
 public int level(){return Math.min(100,1+(int)Math.sqrt(xp/100d));}
 public long xp(){return xp;}
 public long next(){return level()>=100?xp:100L*level()*level();}
 public int points(){return level()-1;}
 public void restore(long value){if(value<0)throw new IllegalArgumentException("Negative XP");xp=Math.min(980100,value);}
 public void loseProgress(){long floor=100L*(level()-1)*(level()-1);xp=Math.max(floor,xp-Math.max(1,(next()-floor)/10));}
 public void award(long amount){if(amount>0)restore(Math.min(980100,xp+amount));}
 public void gain(int damage){if(damage>0)restore(xp+Math.min(1000000L,damage*5L));}
}
