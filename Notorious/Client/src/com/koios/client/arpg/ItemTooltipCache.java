package com.koios.client.arpg;
import java.util.*;
import java.nio.charset.StandardCharsets;
/** Server-authoritative metadata, keyed by displayed interface and slot. */
public final class ItemTooltipCache {
 public static final int CHANNEL=65534;
 public static final class Entry {
  public final int baseId,color;public final String[] normal,advanced;
  Entry(int baseId,int color,String[] normal,String[] advanced){this.baseId=baseId;this.color=color;this.normal=normal;this.advanced=advanced;}
 }
 private final Map<Integer,Map<Integer,Entry>> widgets=new HashMap<>();
 public void clear(){widgets.clear();}
 public Entry get(int widget,int slot,int baseId){Map<Integer,Entry> entries=widgets.get(widget);Entry e=entries==null?null:entries.get(slot);return e!=null&&e.baseId==baseId?e:null;}
 public void accept(String payload){
  String[] f=payload.split("\\|",-1);int widget=Integer.parseInt(f[1]);
  if(f[0].equals("clear")){widgets.remove(widget);return;}
  int slot=Integer.parseInt(f[2]);if(widget<0||slot<0||slot>1000)return;
  Map<Integer,Entry> entries=widgets.computeIfAbsent(widget,k->new HashMap<>());
  if(f[0].equals("remove")){entries.remove(slot);return;}
  if(!f[0].equals("item")||f.length!=7)throw new IllegalArgumentException("Malformed item tooltip");
  entries.put(slot,new Entry(Integer.parseInt(f[3]),Integer.parseInt(f[4]),decode(f[5]),decode(f[6])));
 }
 private String[] decode(String s){return new String(Base64.getDecoder().decode(s),StandardCharsets.UTF_8).split("\n");}
}
