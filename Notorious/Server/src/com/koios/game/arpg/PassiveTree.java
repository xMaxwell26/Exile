package com.koios.game.arpg;
import java.util.*;
public final class PassiveTree {
 public static final class Node {
  public final int id,parent;public final String name;public final List<Modifier> modifiers;
  Node(int id,int parent,String name,Modifier...mods){this.id=id;this.parent=parent;this.name=name;modifiers=Arrays.asList(mods);}
  public String description(){List<String> parts=new ArrayList<>();for(Modifier m:modifiers){if(m.getStat()==Stat.PHYSICAL_MAX)continue;String label=m.getStat()==Stat.PHYSICAL_MIN?"physical damage":m.getStat().name().toLowerCase(Locale.ROOT).replace('_',' ');String value=String.format(Locale.ROOT,"%.0f",Math.abs(m.getValue()));parts.add((m.getValue()<0?"-":"+")+value+(m.getOperation()==Modifier.Operation.FLAT?" ":"% ")+(m.getOperation()==Modifier.Operation.MORE?"more ":m.getOperation()==Modifier.Operation.INCREASED?"increased ":"")+label);}return String.join(", ",parts);}
 }
 public static final Map<Integer,Node> NODES=new LinkedHashMap<>();
 private static Modifier inc(Stat s,double v){return new Modifier(s,Modifier.Operation.INCREASED,v);}
 private static Modifier flat(Stat s,double v){return new Modifier(s,Modifier.Operation.FLAT,v);}
 static{
  add(1,0,"Weapon training",inc(Stat.PHYSICAL_MIN,10),inc(Stat.PHYSICAL_MAX,10));add(2,1,"Quick hands",inc(Stat.ATTACK_SPEED,8));add(3,2,"Precision",flat(Stat.ACCURACY,40));add(4,3,"Keen edge",inc(Stat.CRITICAL_CHANCE,30));add(5,3,"Heavy blade",inc(Stat.PHYSICAL_MIN,15),inc(Stat.PHYSICAL_MAX,15));add(6,4,"Executioner",flat(Stat.CRITICAL_MULTIPLIER,25));add(7,5,"Colossus",new Modifier(Stat.PHYSICAL_MIN,Modifier.Operation.MORE,40),new Modifier(Stat.PHYSICAL_MAX,Modifier.Operation.MORE,40),inc(Stat.ATTACK_SPEED,-20));
  add(8,0,"Vitality",inc(Stat.LIFE,8));add(9,8,"Iron skin",inc(Stat.ARMOUR,25));add(10,9,"Endurance",flat(Stat.LIFE_REGEN,2));add(11,10,"Fortress",flat(Stat.ARMOUR,100));add(12,10,"Fleet foot",inc(Stat.EVASION,30));add(13,11,"Bulwark",flat(Stat.BLOCK_CHANCE,8));add(14,12,"Survivor",inc(Stat.LIFE,15),inc(Stat.EVASION,20));
  add(15,0,"Reservoir",flat(Stat.MANA,15));add(16,15,"Energy barrier",flat(Stat.ENERGY_SHIELD,20));add(17,16,"Clarity",flat(Stat.MANA_REGEN,2));add(18,17,"Aegis",inc(Stat.ENERGY_SHIELD,25));add(19,17,"Rapid recharge",flat(Stat.ES_RECHARGE,30));add(20,18,"Cold ward",flat(Stat.COLD_RESISTANCE,12));add(21,19,"Chaos ward",flat(Stat.CHAOS_RESISTANCE,12));
 }
 private static void add(int id,int parent,String name,Modifier...mods){NODES.put(id,new Node(id,parent,name,mods));}
 private final Set<Integer> allocated=new LinkedHashSet<>();
 public int spent(){return allocated.size();}public boolean has(int id){return allocated.contains(id);}
 public void allocate(int id,int earned){Node n=NODES.get(id);if(n==null||allocated.contains(id)||spent()>=earned||n.parent!=0&&!has(n.parent))throw new IllegalArgumentException("Allocate a connected node with an available point");allocated.add(id);}
 public void refund(int id){if(!has(id))throw new IllegalArgumentException("Node is not allocated");for(Node n:NODES.values())if(n.parent==id&&has(n.id))throw new IllegalArgumentException("Refund dependent nodes first");allocated.remove(id);}
 public List<Modifier> modifiers(){List<Modifier> out=new ArrayList<>();for(int id:allocated)out.addAll(NODES.get(id).modifiers);return out;}
 public String save(){return allocated.toString().replace("[","").replace("]","").replace(" ","");}
 public void load(String value,int earned){Set<Integer> old=new LinkedHashSet<>(allocated);allocated.clear();try{if(!value.isEmpty())for(String id:value.split(","))allocate(Integer.parseInt(id),earned);}catch(RuntimeException e){allocated.clear();allocated.addAll(old);throw e;}}
}
