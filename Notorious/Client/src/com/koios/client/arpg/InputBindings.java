package com.koios.client.arpg;

import java.awt.event.*;import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;

/** Three account-local input bindings; independent of skill assignments and event routing. */
public final class InputBindings {
 public static final int SLOTS=3;
 private static final int MODS=InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK|InputEvent.META_DOWN_MASK|InputEvent.ALT_GRAPH_DOWN_MASK;
 public static final class Binding {
  public final boolean mouse;public final int code,modifiers;
  private Binding(boolean mouse,int code,int modifiers){this.mouse=mouse;this.code=code;this.modifiers=modifiers&MODS;}
  public String label(){String prefix=InputEvent.getModifiersExText(modifiers);return (prefix.isEmpty()?"":prefix+" + ")+(mouse?(code==1?"Left mouse":code==2?"Middle mouse":code==3?"Right mouse":"Mouse "+code):KeyEvent.getKeyText(code));}
  public boolean equals(Object other){if(!(other instanceof Binding))return false;Binding b=(Binding)other;return mouse==b.mouse&&code==b.code&&modifiers==b.modifiers;}
  public int hashCode(){return (mouse?100000:0)+code*31+modifiers;}
 }
 public static Binding key(int code,int mods){if(code<=0||code==KeyEvent.VK_ENTER||code==KeyEvent.VK_ESCAPE||modifierKey(code))return null;return new Binding(false,code,mods);}
 public static Binding mouse(int button,int mods){return button<1||button>20?null:new Binding(true,button,mods);}
 public static boolean modifierKey(int code){return code==KeyEvent.VK_SHIFT||code==KeyEvent.VK_CONTROL||code==KeyEvent.VK_ALT||code==KeyEvent.VK_META||code==KeyEvent.VK_ALT_GRAPH;}
 private final Binding[] slots=new Binding[SLOTS];private final Path file;
 private InputBindings(){file=null;reset();}
 public static InputBindings sessionDefaults(){return new InputBindings();}
 public static InputBindings forUser(String username)throws IOException{return new InputBindings(Paths.get(System.getProperty("user.home"),"koios-cache","arpg-controls"),username);}
 /** Directory injection permits isolated persistence tests without touching real accounts. */
 public InputBindings(Path directory,String username)throws IOException{
  if(username==null||username.trim().isEmpty())throw new IllegalArgumentException("An actual logged-in username is required");String normalized=username.trim().toLowerCase(Locale.ROOT);StringBuilder safe=new StringBuilder();for(byte b:normalized.getBytes(StandardCharsets.UTF_8))safe.append(String.format(Locale.ROOT,"%02x",b&255));file=directory.resolve(safe+".properties");reset();load();
 }
 public synchronized Binding get(int slot){valid(slot);return slots[slot];}
 public synchronized String label(int slot){Binding b=get(slot);return b==null?"Unbound":b.label();}
 public synchronized int conflict(int slot,Binding binding){valid(slot);if(binding==null)return -1;for(int i=0;i<SLOTS;i++)if(i!=slot&&binding.equals(slots[i]))return i;return -1;}
 /** Returns conflicting slot without mutation; replace=true explicitly clears that other slot. */
 public synchronized int assign(int slot,Binding binding,boolean replace){int conflict=conflict(slot,binding);if(conflict>=0&&!replace)return conflict;if(conflict>=0)slots[conflict]=null;slots[slot]=binding;return -1;}
 public synchronized void clear(int slot){valid(slot);slots[slot]=null;}
 public synchronized void reset(){for(int i=0;i<SLOTS;i++)slots[i]=key(new int[]{KeyEvent.VK_Q,KeyEvent.VK_W,KeyEvent.VK_E}[i],0);}
 public synchronized int matchKey(int code,int mods){return match(key(code,mods));}
 public synchronized int matchMouse(int button,int mods){return match(mouse(button,mods));}
 private int match(Binding binding){if(binding!=null)for(int i=0;i<SLOTS;i++)if(binding.equals(slots[i]))return i;return -1;}
 public synchronized void save()throws IOException{
  if(file==null)throw new IOException("Session-only controls have no account file");Properties properties=new Properties();properties.setProperty("version","1");for(int i=0;i<SLOTS;i++){Binding b=slots[i];properties.setProperty("slot."+i,b==null?"none":(b.mouse?"mouse":"key")+","+b.code+","+b.modifiers);}Files.createDirectories(file.getParent());Path temporary=Files.createTempFile(file.getParent(),"controls-",".tmp");try{try(OutputStream out=Files.newOutputStream(temporary)){properties.store(out,"Exile skill controls");}try{Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException ex){Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING);}}finally{Files.deleteIfExists(temporary);}
 }
 private void load()throws IOException{if(!Files.exists(file))return;Properties properties=new Properties();try(InputStream in=Files.newInputStream(file)){properties.load(in);}if(!"1".equals(properties.getProperty("version")))return;Binding[] read=new Binding[SLOTS];for(int i=0;i<SLOTS;i++){String value=properties.getProperty("slot."+i);if(value==null){read[i]=slots[i];continue;}if(value.equals("none"))continue;try{String[] bits=value.split(",");if(bits.length!=3)continue;int code=Integer.parseInt(bits[1]),mods=Integer.parseInt(bits[2]);read[i]=bits[0].equals("mouse")?mouse(code,mods):bits[0].equals("key")?key(code,mods):null;}catch(NumberFormatException ignored){}}for(int i=0;i<SLOTS;i++){slots[i]=read[i];for(int previous=0;previous<i;previous++)if(slots[i]!=null&&slots[i].equals(slots[previous]))slots[i]=null;}}
 private static void valid(int slot){if(slot<0||slot>=SLOTS)throw new IllegalArgumentException("Skill slot out of range");}
}


