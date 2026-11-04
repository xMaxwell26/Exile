package com.koios.game.arpg;
/** Dedicated IDs isolate map encounters from ordinary world NPC definitions. */
public final class EncounterNpcs {
 public static final int FIRST=15000,LAST=15013;
 private static final int[] TEMPLATES={73,90,13,59,49,103,73,90,13,59,49,103,82,82};
 private static final String[] NAMES={"Ashen Thrall","Furnace Sentinel","Cinder Adept","Slag Crawler","Furnace Hound","Sootbound Wraith","Briar Husk","Briar Warden","Thorncaller","Venom Weaver","Briar Hound","Hollow Wisp","Cinder Foreman","Thornbound Warden"};
 public static boolean custom(int id){return id>=FIRST&&id<=LAST;}
 public static int template(int id){return custom(id)?TEMPLATES[id-FIRST]:id;}
 public static String name(int id){return custom(id)?NAMES[id-FIRST]:"Settlement Champion";}
 public static int id(int layout,int role,boolean boss){if(layout==0)return boss?82:TEMPLATES[role];return boss?15012+layout-1:FIRST+(layout-1)*6+role;}
}
