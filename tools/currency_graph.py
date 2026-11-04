from pathlib import Path
r=Path('Notorious/Client/src/com/koios/client')
p=r/'definition/ItemDefinition.java';s=p.read_text();s=s.replace('if (i < 0 || i >= streamIndices.length)', 'if (i < 0 || (i >= streamIndices.length && !(i>=22400&&i<22406)))');s=s.replace('stream.currentOffset = streamIndices[i];','stream.currentOffset = streamIndices[i>=22400&&i<22406?new int[]{575,567,573,571,569,8007}[i-22400]:i];');s=s.replace('\t\treturn itemDef;\n\t}', '''  if(i>=22400&&i<22406){String[] names={"Orb of Alchemy","Chaos Orb","Exalted Orb","Orb of Transmutation","Vaal Orb","Scroll of Wisdom"};String[] help={"Upgrades normal or magic equipment or maps to rare.","Rerolls modifiers on magic or rare equipment or maps.","Adds a modifier to magic or rare equipment or maps.","Upgrades normal equipment or maps to magic.","Corrupts equipment or maps. Corrupted items resist further crafting.","Identifies unidentified equipment or maps."};itemDef.name=names[i-22400];itemDef.description=help[i-22400]+" Use it on the item.";itemDef.stackable=true;itemDef.stackIDs=null;itemDef.stackAmounts=null;itemDef.certID=-1;itemDef.certTemplateID=-1;itemDef.lendID=-1;itemDef.lendTemplateID=-1;itemDef.itemActions=new String[]{null,null,null,null,"Drop"};itemDef.groundActions=new String[]{null,null,"Take",null,null};}
\t\treturn itemDef;
\t}''',1)
for sig in ['public static Sprite getSprite(int i, int j, int k, int zoom) {','public static Sprite getSprite(int i, int j, int k) {']:
 s=s.replace(sig,sig+'\n        if(i>=22400&&i<22406)return com.koios.client.arpg.CurrencyIcons.sprite(i);')
p.write_text(s)
p=r/'arpg/CurrencyIcons.java';p.write_text('''package com.koios.client.arpg;
import com.koios.client.graphic.Sprite;
/** Original compact currency art, drawn independently of the rune/cache sprites. */
public final class CurrencyIcons {
 private static final Sprite[] CACHE=new Sprite[6];
 public static Sprite sprite(int id){int n=id-22400;if(CACHE[n]!=null)return CACHE[n];Sprite s=new Sprite(32,32);int[] colors={0xC9A74F,0xA0A1A6,0xF5CB69,0x619BCF,0xB34548,0xDCC59A};
  for(int y=0;y<32;y++)for(int x=0;x<32;x++){double dx=x-15.5,dy=y-16;boolean inside=n==5?(x>=7&&x<=24&&y>=5&&y<=27):dx*dx+dy*dy<=121;if(!inside)continue;double light=n==5?0.8+0.15*Math.sin(x*0.7):0.43+0.57*Math.sqrt(Math.max(0,1-(dx*dx+dy*dy)/130))+0.14*(-dx-dy)/22;int c=colors[n],rr=(int)(((c>>16)&255)*light),g=(int)(((c>>8)&255)*light),b=(int)((c&255)*light);s.myPixels[x+y*32]=(Math.min(255,rr)<<16)|(Math.min(255,g)<<8)|Math.min(255,b);}
  if(n==5){line(s,9,7,23,7,0x7C5632);line(s,9,25,23,25,0x7C5632);for(int y=11;y<23;y+=4)line(s,11,y,21-(y%3),y,0x795A3A);}
  else if(n==0){line(s,15,8,9,22,0xFFF0BB);line(s,15,8,22,22,0xFFF0BB);line(s,11,18,20,18,0xFFF0BB);}
  else if(n==1){line(s,9,10,22,22,0xEDE9DF);line(s,22,10,9,22,0xEDE9DF);line(s,11,8,20,8,0xEDE9DF);}
  else if(n==2){line(s,9,13,12,20,0xFFF1BB);line(s,12,20,21,20,0xFFF1BB);line(s,21,20,24,13,0xFFF1BB);line(s,12,20,16,9,0xFFF1BB);line(s,16,9,21,20,0xFFF1BB);}
  else if(n==3){line(s,10,12,21,12,0xCDEEFF);line(s,21,12,10,21,0xCDEEFF);line(s,10,21,21,21,0xCDEEFF);}
  else{line(s,9,10,16,23,0xFFCFB1);line(s,16,23,23,10,0xFFCFB1);line(s,11,16,21,16,0xFFCFB1);}
  s.maxWidth=33;s.maxHeight=-1;CACHE[n]=s;return s;
 }
 private static void line(Sprite s,int x,int y,int tx,int ty,int c){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++){int px=x+(tx-x)*i/Math.max(1,steps),py=y+(ty-y)*i/Math.max(1,steps);s.myPixels[px+py*32]=c;}}
}
''')
# graph replace rendering with full file
p=r/'arpg/PrototypePanel.java';s=p.read_text();a=s.index('   if(y>295)');b=s.index('  }\n  int row=',a);s=s[:a]+'''   if(y>295&&x>335)return "free";
   for(int id=1;id<=21;id++){int[] at=POS[id];if((x-at[0])*(x-at[0])+(y-at[1])*(y-at[1])<=169)return (control?"refund ":"allocate ")+id;}return null;
'''+s[b:];a=s.index('   String[] head=');b=s.index('  }else{\n   for(int i=1;',a);s=s[:a]+'''   String[] head=lines[0].split("\\\\|");String[][] nodes=new String[22][];boolean[] owned=new boolean[22];
   for(int i=1;i<lines.length;i++){String[] f=lines[i].split("\\\\|",5);if(f.length<5)continue;int id=Integer.parseInt(f[0]);nodes[id]=f;owned[id]=Boolean.parseBoolean(f[2]);}
   text(font,92,57,"BLADE",0xCDB68A);text(font,365,57,"SURVIVAL",0xCDB68A);text(font,228,183,"ENERGY",0x9CAFD1);
   for(int id=1;id<=21;id++){if(nodes[id]==null)continue;int parent=Integer.parseInt(nodes[id][1]);int[] a=POS[parent],b=POS[id];line(a[0],a[1],b[0],b[1],owned[id]?0xB79A5C:0x3B4650);}
   circle(256,155,16,0x80683C);circle(256,155,13,0x25343B);text(font,252,160,"S",0xF0D496);
   int hovered=0;
   for(int id=1;id<=21;id++){if(nodes[id]==null)continue;int[] at=POS[id];int parent=Integer.parseInt(nodes[id][1]);boolean available=(parent==0||owned[parent])&&!owned[id]&&Integer.parseInt(head[1])>0;int color=owned[id]?0xE4C779:available?0xA1B7C8:0x45515C;
    circle(at[0],at[1],11,color);circle(at[0],at[1],8,owned[id]?0x62754C:0x17212A);text(font,at[0]-3,at[1]+4,nodes[id][3].substring(0,1),color);
    if((mouseX-at[0])*(mouseX-at[0])+(mouseY-at[1])*(mouseY-at[1])<=169){hovered=id;circle(at[0],at[1],3,0xFFF0BC);}
   }
   if(hovered>0){String[] f=nodes[hovered];text(font,27,285,f[3]+(owned[hovered]?" / Allocated":" / 1 point"),0xF0D496);String detail=f[4];text(font,27,297,detail.length()>75?detail.substring(0,75):detail,0xCCD6DF);}
   else{text(font,27,285,"Follow a connected path. Hover a node to see its upgrade.",0xA5B0BC);text(font,27,297,"Click to allocate / Ctrl-click to refund a leaf node.",0xA5B0BC);}
   text(font,27,314,"Points: "+head[1],0xE8CD86);text(font,337,314,"Free test refunds: "+head[2],0x939FAA);
'''+s[b:];s=s.replace(' private String[] lines=', ''' private static final int[][] POS={{256,155},{211,132},{171,110},{129,88},{86,73},{112,130},{45,60},{67,149},{301,132},{341,110},{383,88},{426,73},{400,130},{467,60},{445,149},{256,195},{256,229},{256,263},{211,259},{301,259},{169,248},{343,248}};
 private static void circle(int x,int y,int radius,int color){for(int dy=-radius;dy<=radius;dy++)for(int dx=-radius;dx<=radius;dx++)if(dx*dx+dy*dy<=radius*radius)pixel(x+dx,y+dy,color);}
 private static void pixel(int x,int y,int color){if(x>=DrawingArea.topX&&y>=DrawingArea.topY&&x<DrawingArea.bottomX&&y<DrawingArea.bottomY)DrawingArea.pixels[x+y*DrawingArea.width]=color;}
 private static void line(int x,int y,int tx,int ty,int color){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++)pixel(x+(tx-x)*i/Math.max(1,steps),y+(ty-y)*i/Math.max(1,steps),color);}
 private String[] lines=''');p.write_text(s)
