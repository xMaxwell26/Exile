package com.koios.game.arpg;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import com.koios.GameService;
import com.koios.game.model.*;
import com.koios.game.model.mob.*;
import com.koios.game.model.player.Player;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.content.combat.*;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.*;
import com.koios.game.content.combat.util.*;
import com.koios.game.map.path.StraightPathFinder;
import com.koios.network.out.impl.SendStringPacket;
public final class ArpgSkills {
 public static void prepareLogin(Player p){if(p.getAttribute("starter-claimed",false)||Tutorial.stage(p)>0)p.setAttribute("arpg-home-on-login",true);}
 public static void tick(Player p){
  if(p.getAttribute("arpg-home-on-login",false)&&!p.isTeleporting()&&!p.isDead()){p.setAttribute("arpg-home-on-login",false);MapRuns.home(p);}

  p.getArpgSkills().reconcileOwnership();CraftingCurrency.migrateLegacyInventory(p);PrototypeAppearance.migrate(p);Map<Stat,Double> stats=p.getArpgStats();p.getArpgVitals().tick(stats,0.6,p.isDead());TestArena.tick(p);Tutorial.tick(p);MapRuns.tick(p);CharacterSheet.sync(p);p.getArpgSkills().tick(stats.get(Stat.MANA),stats.get(Stat.MANA_REGEN));sync(p);
 }
 public static void sync(Player p){String payload=p.getArpgSkills().update(p.getArpgStats().get(Stat.MANA));if(payload!=null)p.getSession().send(new SendStringPacket(payload,65533));}
 public static boolean handle(Player p,String input){
  if(!input.startsWith("arpgskill "))return false;
  String[] args=input.split(" ");if(args.length<2)return true;
  try{
   if(!args[1].equals("status")&&!args[1].equals("bindings")&&(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting()))return true;
   if(args.length==2&&args[1].equals("status"))p.getArpgSkills().activate();
   else if(args.length==3&&args[1].equals("bindings")){String[] labels=new String(Base64.getDecoder().decode(args[2]),java.nio.charset.StandardCharsets.UTF_8).split("\\|",-1);if(labels.length!=3)throw new IllegalArgumentException("Three binding labels required");for(String label:labels)if(!label.matches("[\\p{L}\\p{N} +()_:/.-]{1,64}"))throw new IllegalArgumentException("Invalid binding label");p.setAttribute("arpg-binding-labels",labels);}
   else if(args.length==4&&args[1].equals("support")){p.getArpgSkills().toggle(Integer.parseInt(args[2]),Integer.parseInt(args[3]));if(p.getArpgSkills().getSupport(0)!=0)Tutorial.action(p,"support");}
   else if(args.length==3&&args[1].equals("clear"))p.getArpgSkills().clear(Integer.parseInt(args[2]));
   else if(args.length==4&&args[1].equals("assign"))p.getArpgSkills().assign(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
   else if(args.length==4&&args[1].equals("socketactive")){int inventorySlot=Integer.parseInt(args[2]),loadoutSlot=Integer.parseInt(args[3]);com.koios.game.model.item.Item gem=p.getInventory().get(inventorySlot);if(!MapRuns.isGem(gem)||gem.getRoll().getGem().support)throw new IllegalArgumentException("Drop an active skill gem into that slot");p.getArpgSkills().socketActive(loadoutSlot,gem.getRoll().getGem().kind,gem.getRoll().getGem().level,p.getArpgProgress().level());p.getInventory().set(inventorySlot,null);}
   else if(args.length==3&&args[1].equals("unsocketactive")){int loadoutSlot=Integer.parseInt(args[2]),skill=p.getArpgSkills().assigned(loadoutSlot);com.koios.game.model.item.Item gem=MapRuns.gem(p.getArpgSkills().skillLevel(skill),false,skill);if(!p.getInventory().hasRoomFor(gem))throw new IllegalArgumentException("Make inventory space first");p.getArpgSkills().unsocketActive(loadoutSlot);if(!p.getInventory().add(gem)){p.getArpgSkills().socketActive(loadoutSlot,skill,gem.getRoll().getGem().level,p.getArpgProgress().level());throw new IllegalArgumentException("Make inventory space first");}}
   else if(args.length==5&&args[1].equals("socket")){int inventorySlot=Integer.parseInt(args[2]),loadoutSlot=Integer.parseInt(args[3]);com.koios.game.model.item.Item gem=p.getInventory().get(inventorySlot);if(!MapRuns.isGem(gem)||!gem.getRoll().getGem().support)throw new IllegalArgumentException("Drop a support gem into that socket");int skill=p.getArpgSkills().assigned(loadoutSlot),support=gem.getRoll().getGem().kind;if(skill<0)throw new IllegalArgumentException("Socket an active skill gem first");if((p.getArpgSkills().getSupport(skill)&1<<support)!=0)throw new IllegalArgumentException("That support is already socketed");p.getArpgSkills().learnSupport(support,gem.getRoll().getGem().level,p.getArpgProgress().level());try{p.getArpgSkills().attach(skill,support,true);p.getInventory().set(inventorySlot,null);}catch(RuntimeException failure){p.getArpgSkills().removeSupport(support);throw failure;}Tutorial.action(p,"support");}
   else if(args.length==4&&args[1].equals("unsocket")){int skill=Integer.parseInt(args[2]),socket=Integer.parseInt(args[3]);if((p.getArpgSkills().getSupport(skill)&1<<socket)==0)throw new IllegalArgumentException("That socket is empty");com.koios.game.model.item.Item gem=MapRuns.gem(p.getArpgSkills().supportLevel(socket),true);if(!p.getInventory().hasRoomFor(gem))throw new IllegalArgumentException("Make inventory space first");p.getArpgSkills().attach(skill,socket,false);try{p.getArpgSkills().removeSupport(socket);if(!p.getInventory().add(gem))throw new IllegalArgumentException("Make inventory space first");}catch(RuntimeException failure){p.getArpgSkills().learnSupport(socket,gem.getRoll().getGem().level,p.getArpgProgress().level());p.getArpgSkills().attach(skill,socket,true);throw failure;}}
   else if(args.length==4&&(args[1].equals("attach")||args[1].equals("detach"))){p.getArpgSkills().attach(Integer.parseInt(args[2]),Integer.parseInt(args[3]),args[1].equals("attach"));if(p.getArpgSkills().getSupport(0)!=0)Tutorial.action(p,"support");}
   else if(args.length==5&&args[1].equals("move")){p.getArpgSkills().moveSupport(Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));if(p.getArpgSkills().getSupport(0)!=0)Tutorial.action(p,"support");}
   else if(args.length==3&&args[1].equals("slot"))cast(p,p.getArpgSkills().assigned(Integer.parseInt(args[2])));
   else if(args.length==3&&args[1].equals("cast"))cast(p,Integer.parseInt(args[2]));
  }catch(IllegalArgumentException ignored){}
  sync(p);return true;
 }
 private static boolean valid(Player p,Mob m){
  if(m==null||m.isDead()||m.getHitpoints()<=0||!m.isVisible()||m.getZ()!=p.getZ()||m.isTeleporting()||m.isFamiliar()||!m.getDefinition().isAttackable())return false;
  boolean attackOption=false;for(int i=0;i<5;i++)if(m.getDefinition().getAction(i).map(a->a.equalsIgnoreCase("attack")).orElse(false))attackOption=true;
  if(!attackOption)return false;
  if(m.summoner!=null&&m.summoner!=p)return false;
  int id=m.getId();if(id==6222||id==6225||id==6227)return false;
  if(!m.inMulti()&&m.getCombat().getAggressor()!=null&&m.getCombat().getAggressor()!=p)return false;
  return StraightPathFinder.isInteractionPathClear(p.getLocation(),m.getLocation());
 }
 public static void cast(Player p,int skill){
  if(skill<0||skill>=SkillState.SKILLS||p.isDead()||p.isTeleporting()||!p.hasFocus()||!ArpgMelee.enabled(p)||p.getBankField().isBanking()||p.isTrading()||p.inDuelArena())return;
  EntityCharacter selected=p.getCombat().getTarget();
  if(skill==2&&(selected==null||!selected.isMob()||!valid(p,selected.toMob())||Location.getManhattanDistance(p.getLocation(),selected.getLocation())>2))for(Mob candidate:MobManager.getMobs())if(candidate!=null&&valid(p,candidate)&&Location.getManhattanDistance(p.getLocation(),candidate.getLocation())<=2){selected=candidate;break;}
  if(selected==null||!selected.isMob()){p.sendMessage("Select an enemy before using a skill.");return;}
  Mob primary=selected.toMob();
  if(!valid(p,primary)||(skill==2?Location.getManhattanDistance(p.getLocation(),primary.getLocation())>2:!CombatDistance.inCombatDistance(p,primary,AttackType.MELEE,true))){p.sendMessage("Move into melee range to use that skill.");return;}
  SkillState state=p.getArpgSkills();Map<Stat,Double> stats=p.getArpgStats();
  if(!state.spend(skill,stats.get(Stat.ATTACK_SPEED))){
   long now=System.currentTimeMillis();if(now-p.<Long>getAttribute("arpg-skill-feedback-at",0L)>=1000){p.setAttribute("arpg-skill-feedback-at",now);p.sendMessage(state.getMana()+1e-9<state.cost(skill)?"Not enough mana: this skill costs "+String.format(Locale.ROOT,"%.1f",state.cost(skill))+".":state.getCooldown(skill)>0?"That skill is recovering.":"Finish your current attack first.");}return;
  }
  List<Mob> targets=new ArrayList<>();targets.add(primary);
  boolean splash=(state.getSupport(skill)&1)!=0;
  if(skill==1||skill==2||skill==3||splash){
   int dx=Integer.signum(primary.getX()-p.getX()),dy=Integer.signum(primary.getY()-p.getY());
   for(Mob m:MobManager.getMobs()){
    if(m==null||m==primary||!valid(p,m)||Location.getManhattanDistance(p.getLocation(),m.getLocation())>3)continue;
    boolean cone=skill==1&&Location.getManhattanDistance(p.getLocation(),m.getLocation())<=2&&(m.getX()-p.getX())*dx+(m.getY()-p.getY())*dy>=0;
    boolean adjacent=splash&&Location.getManhattanDistance(primary.getLocation(),m.getLocation())<=1;
    if(cone||adjacent||skill==3&&Location.getManhattanDistance(primary.getLocation(),m.getLocation())<=2||skill==2&&Location.getManhattanDistance(p.getLocation(),m.getLocation())<=(splash?3:2))targets.add(m);
   }
  }
  p.getCombat().setAttackType(AttackType.MELEE);p.usingSpecial=false;
  p.playAnimation(new Animation(SkillAnimations.sequence(skill,p.getEquipment().get(Equipment.SLOT_WEAPON).getId())));p.setFaceIndex(primary.getIndex(),false);
  boolean critical=false;
  for(Mob m:targets){
   double effectiveness=state.effectiveness(skill)*(skill==4&&primary.getHitpoints()*100L<=primary.getMaxHitpoints()*35L?2:1)*(m!=primary&&skill==0?0.5:1)*(splash&&m!=primary?1+0.02*(state.supportLevel(0)-1):1);
   MeleeRolls.Batch hit=MeleeRolls.roll(stats,1,effectiveness,ThreadLocalRandom.current(),()->CalculateAccuracy.execute(p,m,AttackType.MELEE));
   ApplyMeleeDamage.execute(p,m,false,hit.damage,hit.critical);
   critical|=hit.critical;
  }
  CombatVisuals.send(p,skill,p,primary,targets.size(),critical);
  if(skill<2)Tutorial.action(p,skill==0?"q":"w");
  p.getArpgAttackClock().reset();p.getCombat().setHitDelay(0);p.getCombat().setOldTarget(null);p.getCombat().setAttackTimer(state.getActionLock());
 }
}
