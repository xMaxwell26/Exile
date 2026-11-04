package com.koios.client.arpg;
import com.koios.client.graphic.Sprite;
/** Original compact currency art, drawn independently of the rune/cache sprites. */
public final class CurrencyIcons {
 private static final int[] IDS={22400,22401,22402,22403,22404,22405,22406,22407,22408,22409,22415,22416};
 private static final String[] NAMES={"Orb of Alchemy","Chaos Orb","Exalted Orb","Orb of Transmutation","Vaal Orb","Scroll of Wisdom","Portal Scroll","Orb of Alteration","Orb of Augmentation","Regal Orb","Orb of Scouring","Orb of Annulment"};
 private static final String[] HELP={"Upgrades normal equipment or maps to rare.","Rerolls modifiers on rare equipment or maps.","Adds a modifier to rare equipment or maps.","Upgrades normal equipment or maps to magic.","Corrupts equipment or maps. Corrupted items resist further crafting.","Identifies unidentified equipment or maps.","Read inside a map to open a return portal to your base.","Rerolls modifiers on magic equipment or maps.","Adds a modifier to magic equipment or maps.","Upgrades magic equipment or maps to rare, keeping existing modifiers and adding one.","Removes explicit modifiers and returns equipment or maps to normal rarity.","Removes one random explicit modifier without changing rarity."};
 public static int index(int id){for(int i=0;i<IDS.length;i++)if(IDS[i]==id)return i;return -1;}
 public static boolean supports(int id){return index(id)>=0;}
 public static int template(int id){int n=index(id);return n<0?id:new int[]{575,567,573,571,569,970,970,567,575,573,575,567}[n];}
 public static String name(int id){int n=index(id);return n<0?"":NAMES[n];}
 public static String help(int id){int n=index(id);return n<0?"":HELP[n];}
 private static final Sprite[] CACHE=new Sprite[12];
 public static Sprite sprite(int id){Sprite illustrated=ArtSprites.icon(id);if(illustrated!=null)return illustrated;int n=index(id);if(n<0)return null;if(CACHE[n]!=null)return CACHE[n];Sprite s=new Sprite(32,32);int[] colors={0xC9A74F,0xA0A1A6,0xF5CB69,0x619BCF,0xB34548,0xDCC59A,0x8FBFE4,0x709ECE,0x84B99E,0xA77DB8,0xD4D0BE,0x8F72B0};
  for(int y=0;y<32;y++)for(int x=0;x<32;x++){double dx=x-15.5,dy=y-16;boolean inside=(n==5||n==6)?(x>=7&&x<=24&&y>=5&&y<=27):dx*dx+dy*dy<=121;if(!inside)continue;double light=(n==5||n==6)?0.8+0.15*Math.sin(x*0.7):0.43+0.57*Math.sqrt(Math.max(0,1-(dx*dx+dy*dy)/130))+0.14*(-dx-dy)/22;int c=colors[n],rr=(int)(((c>>16)&255)*light),g=(int)(((c>>8)&255)*light),b=(int)((c&255)*light);s.myPixels[x+y*32]=(Math.min(255,rr)<<16)|(Math.min(255,g)<<8)|Math.min(255,b);}
  if(n==6){line(s,9,7,23,7,0x304B82);line(s,9,25,23,25,0x304B82);line(s,12,12,20,12,0xE6F3FF);line(s,20,12,20,21,0xE6F3FF);line(s,20,21,12,21,0xE6F3FF);line(s,12,21,12,12,0xE6F3FF);}
  else if(n==5){line(s,9,7,23,7,0x7C5632);line(s,9,25,23,25,0x7C5632);for(int y=11;y<23;y+=4)line(s,11,y,21-(y%3),y,0x795A3A);}
  else if(n==0){line(s,15,8,9,22,0xFFF0BB);line(s,15,8,22,22,0xFFF0BB);line(s,11,18,20,18,0xFFF0BB);}
  else if(n==1){line(s,9,10,22,22,0xEDE9DF);line(s,22,10,9,22,0xEDE9DF);line(s,11,8,20,8,0xEDE9DF);}
  else if(n==2){line(s,9,13,12,20,0xFFF1BB);line(s,12,20,21,20,0xFFF1BB);line(s,21,20,24,13,0xFFF1BB);line(s,12,20,16,9,0xFFF1BB);line(s,16,9,21,20,0xFFF1BB);}
  else if(n==3){line(s,10,12,21,12,0xCDEEFF);line(s,21,12,10,21,0xCDEEFF);line(s,10,21,21,21,0xCDEEFF);}
  else{line(s,9,10,16,23,0xFFCFB1);line(s,16,23,23,10,0xFFCFB1);line(s,11,16,21,16,0xFFCFB1);}
  s.maxWidth=33;s.maxHeight=-1;CACHE[n]=s;return s;
 }
 private static void line(Sprite s,int x,int y,int tx,int ty,int c){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++){int px=x+(tx-x)*i/Math.max(1,steps),py=y+(ty-y)*i/Math.max(1,steps);s.myPixels[px+py*32]=c;}}
}
