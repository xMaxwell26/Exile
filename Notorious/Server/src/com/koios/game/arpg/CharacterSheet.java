package com.koios.game.arpg;
import java.util.*;
import java.nio.charset.StandardCharsets;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;
public final class CharacterSheet {
 public static List<String> lines(Player p){
  Map<Stat,Double> s=p.getArpgStats();List<String> out=new ArrayList<>();CharacterProgress progress=p.getArpgProgress();
  out.add("Character Level: "+progress.level());out.add("XP: "+progress.xp()+" / "+progress.next());out.add("Passive Points: "+Math.max(0,progress.points()-p.getArpgPassives().spent()));
  out.add("Life: "+(int)Math.ceil(p.getArpgVitals().life())+" / "+s.get(Stat.LIFE).intValue());out.add("Energy Shield: "+(int)Math.ceil(p.getArpgVitals().shield())+" / "+s.get(Stat.ENERGY_SHIELD).intValue());
  out.add("Mana: "+(int)p.getArpgSkills().getMana()+" / "+s.get(Stat.MANA).intValue());
  out.add("Physical: "+s.get(Stat.PHYSICAL_MIN).intValue()+"-"+s.get(Stat.PHYSICAL_MAX).intValue());
  out.add(String.format(Locale.ROOT,"Attack Speed: %.2f / sec",s.get(Stat.ATTACK_SPEED)));
  out.add(String.format(Locale.ROOT,"Critical Chance: %.1f%%",s.get(Stat.CRITICAL_CHANCE)));
  out.add(String.format(Locale.ROOT,"Crit Multiplier: %.0f%%",s.get(Stat.CRITICAL_MULTIPLIER)));
  out.add("Accuracy: "+s.get(Stat.ACCURACY).intValue());out.add("Armour: "+s.get(Stat.ARMOUR).intValue());out.add("Evasion: "+s.get(Stat.EVASION).intValue());out.add("Block: "+s.get(Stat.BLOCK_CHANCE).intValue()+"%");
  for(Stat stat:new Stat[]{Stat.FIRE_RESISTANCE,Stat.COLD_RESISTANCE,Stat.LIGHTNING_RESISTANCE,Stat.CHAOS_RESISTANCE})out.add(stat.name().split("_")[0].substring(0,1)+stat.name().split("_")[0].substring(1).toLowerCase()+" Resist: "+s.get(stat).intValue()+"%");
  return out;
 }
 public static void sync(Player p){
  String value=Base64.getEncoder().encodeToString(String.join("\n",lines(p)).getBytes(StandardCharsets.UTF_8));
  if(!value.equals(p.getAttribute("arpg-sheet",""))){p.setAttribute("arpg-sheet",value);p.getSession().send(new SendStringPacket(value,65532));}
 }
}
