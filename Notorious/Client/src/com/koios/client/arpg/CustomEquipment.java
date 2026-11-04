package com.koios.client.arpg;
import com.koios.client.world.Model;
public final class CustomEquipment {
 public static final int[] IDS={22420,22421,22422,22423,22424,22425,22426,22410,22411,22412};
 private static final int[] TEMPLATES={1277,1205,1420,1351,1115,1129,577,8007,1623,1631};
 private static final String[] NAMES={"Ashbound Sword","Duskfang Dagger","Gravebell Mace","Riftcleaver Axe","Ironward Carapace","Briarhide Vest","Aetherwoven Robe","Forsaken Settlement Map","Uncut Skill Gem","Uncut Support Gem"};
 public static int visualId(int id){return id>=22430&&id<=22439?new int[]{22420,22421,22422,22424,22426,22423,22420,22421,22424,22426}[id-22430]:id==22413||id==22414?22410:id;}
 public static boolean weapon(int id){int i=index(id);return i>=0&&i<4;}
 public static int index(int id){id=visualId(id);for(int i=0;i<IDS.length;i++)if(IDS[i]==id)return i;return -1;}
 public static boolean isCustom(int id){return index(id)>=0;}
 public static int template(int id){int index=index(id);return index<0?id:TEMPLATES[index];}
 public static String name(int id){if(id>=22430&&id<=22439)return new String[]{"Warden Longblade","Nightglass Shiv","Cinderheart Maul","Wayfarer Brigandine","Emberweave Mantle","Bogsteel Cleaver","Dawnbreaker Blade","Gloomfang Dagger","Bastion Plate","Veilweave Coat"}[id-22430];if(id==22413)return "Ashen Foundry Map";if(id==22414)return "Thornwild Courtyard Map";int index=index(id);return index<0?"Artifact":NAMES[index];}
 public static void style(Model model,int id){int index=index(id);if(index<4||model==null||model.anIntArray1640==null)return;model.anIntArray1640=model.anIntArray1640.clone();int[] hue={32,48,5,1,35,15,43,9,34,6},sat={4,5,4,5,3,3,5,4,5,5};for(int i=0;i<model.anIntArray1640.length;i++){int color=model.anIntArray1640[i];if(color<2||color>=65534||model.anIntArray1637!=null&&(model.anIntArray1637[i]&2)!=0)continue;boolean skin=false;for(int body:new int[]{4550,4537,5681,5673,5790,6806,8076,4574})if(color==body)skin=true;if(skin)continue;int light=Math.max(12,Math.min(85,(int)((color&127)*(index>=4&&index<=6?.65:1))));int h=index==4?0:index==5?14:index==6?46:hue[index],s=index==4?0:index==5||index==6?2:sat[index];model.anIntArray1640[i]=(h<<10)|(s<<7)|light;}}
}
