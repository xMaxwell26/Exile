package com.koios.game.arpg;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import com.koios.game.model.*;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.*;
import com.koios.game.content.combat.util.CalculateAccuracy;
public final class ArpgMelee {
 public static boolean enabled(Player p){
  Item w=p.getEquipment().get(Equipment.SLOT_WEAPON);
  return w!=null&&w.hasRoll()&&p.getArpgStats().get(Stat.PHYSICAL_MAX)>0;
 }
 public static void attack(Player p,EntityCharacter target){
  if(!target.isMob()||target.toMob().isFamiliar()){Combat.resetCombat(p);return;}
  Item weapon=p.getEquipment().get(Equipment.SLOT_WEAPON);Map<Stat,Double> stats=p.getArpgStats();
  long attacks=p.getArpgAttackClock().advance(weapon.getRoll().getInstanceId(),target,stats.get(Stat.ATTACK_SPEED),0.6);
  p.usingSpecial=false;p.getCombat().setHitDelay(0);p.getCombat().setOldTarget(null);
  p.getCombat().setAttackTimer(1);
  if(attacks==0)return;
  p.playAnimation(new Animation(MeleeData.getWepAnim(p)));
  MeleeRolls.Batch batch=MeleeRolls.roll(stats,attacks,1,ThreadLocalRandom.current(),()->CalculateAccuracy.execute(p,target,AttackType.MELEE));
  ApplyMeleeDamage.execute(p,target,false,batch.damage,batch.critical);
  CombatVisuals.send(p,5,p,target,0,batch.critical);
  // The legacy applier installs a whole-tick cooldown; this path owns timing.
  p.getCombat().setAttackTimer(1);
  if(p.getAttribute("arpg-combat-log",false))p.sendMessage("ARPG: "+attacks+" attack(s), "+batch.damage+" rolled damage"+(batch.critical?" (critical)":"")+" / "+String.format(Locale.ROOT,"%.2f",stats.get(Stat.ATTACK_SPEED))+" APS");
 }
}
