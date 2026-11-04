package com.koios.client.arpg;
import com.koios.client.definition.NPCDefinition;
import com.koios.client.world.Model;
/** Themed map creatures retain proven cache rigs and animation groups. */
public final class CustomNpcs {
 private static final int[] TEMPLATES={73,90,13,59,49,103,73,90,13,59,49,103,82,82,519,540};
 private static final String[] NAMES={"Ashen Thrall","Furnace Sentinel","Cinder Adept","Slag Crawler","Furnace Hound","Sootbound Wraith","Briar Husk","Briar Warden","Thorncaller","Venom Weaver","Briar Hound","Hollow Wisp","Cinder Foreman","Thornbound Warden","Quartermaster","Gemcutter"};
 public static boolean custom(int id){return id>=15000&&id<=15015;}
 public static int template(int id){return custom(id)?TEMPLATES[id-15000]:id;}
 public static void configure(NPCDefinition def,int id){if(!custom(id))return;def.name=NAMES[id-15000];if(id>=15014){def.combatLevel=0;def.actions=new String[]{id==15014?"Supplies":"Gemcraft",null,null,null,null};def.description=(id==15014?"Basic expedition supplies, purchased with coins.":"Fuse equal-level active gems using coins, then learn an upgrade.").getBytes(java.nio.charset.StandardCharsets.UTF_8);}else{def.actions=new String[]{null,"Attack",null,null,null};def.description=(def.name+" inhabits your map.").getBytes(java.nio.charset.StandardCharsets.UTF_8);}}
 public static void style(Model model,int id){
  if(!custom(id)||id>=15014||model==null||model.anIntArray1640==null)return;model.anIntArray1640=model.anIntArray1640.clone();int index=id-15000,role=index<12?index%6:6;boolean foundry=index<6||index==12;int hue=foundry?(role==2?4:role==5?7:3):(role==2?17:role==5?28:18);
  for(int face=0;face<model.anIntArray1640.length;face++){
   int color=model.anIntArray1640[face];if(color<2||color>=65534||model.anIntArray1637!=null&&(model.anIntArray1637[face]&2)!=0)continue;
   int originalHue=color>>10,originalSat=color>>7&7,light=color&127;
   // Keep human skin, bone, metal and bright eyes distinct from the themed cloth/fur.
   if((role==0||role==2)&&originalHue>=2&&originalHue<=10&&originalSat>=2&&light>=28)continue;
   if(originalSat<=1&&role!=5)continue;
   int saturation=role==2?3:2;light=Math.max(10,Math.min(105,(int)(light*(role==5?1.05:.9))));
   model.anIntArray1640[face]=(hue<<10)|(saturation<<7)|light;
  }
 }
}
