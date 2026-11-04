package com.koios.client.arpg;
import com.koios.client.graphic.*;
public final class PrototypePanel {
 private static final int[][] POS={{256,155},{211,132},{171,110},{129,88},{86,73},{112,130},{45,60},{67,149},{301,132},{341,110},{383,88},{426,73},{400,130},{467,60},{445,149},{256,195},{256,229},{256,263},{211,259},{301,259},{169,248},{343,248}};
 private static void circle(int x,int y,int radius,int color){for(int dy=-radius;dy<=radius;dy++)for(int dx=-radius;dx<=radius;dx++)if(dx*dx+dy*dy<=radius*radius)pixel(x+dx,y+dy,color);}
 private static void pixel(int x,int y,int color){if(x>=DrawingArea.topX&&y>=DrawingArea.topY&&x<DrawingArea.bottomX&&y<DrawingArea.bottomY)DrawingArea.pixels[x+y*DrawingArea.width]=color;}
 private static void line(int x,int y,int tx,int ty,int color){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++)pixel(x+(tx-x)*i/Math.max(1,steps),y+(ty-y)*i/Math.max(1,steps),color);}
 private String[] lines={"guide"};
 private final PanelCards cards=new PanelCards();private boolean cardLayout;
 public void accept(String payload){lines=new String(java.util.Base64.getDecoder().decode(payload),java.nio.charset.StandardCharsets.UTF_8).split("\n");cardLayout=cards.accept(lines);}
 public String actionAt(int x,int y,boolean control){
  if(x<16||x>496||y<16||y>322)return null;
  if(y<46){int tab=Math.min(4,Math.max(0,(x-24)/92));return new String[]{"guide","tree","craft","maps","home"}[tab];}
  if(cardLayout)return cards.actionAt(x,y);
  if(lines[0].startsWith("tree|")){
   if(y>295&&x>335)return "free";
   for(int id=1;id<=21;id++){int[] at=POS[id];if((x-at[0])*(x-at[0])+(y-at[1])*(y-at[1])<=169)return (control?"refund ":"allocate ")+id;}return null;
  }
  int row=(y-59)/12+1;if(y>=59&&row<lines.length){String[] entry=lines[row].split("\t",2);return entry.length==2&&!entry[0].isEmpty()?entry[0]:null;}return null;
 }
 public int hoverInventorySlot(int x,int y){if(!cardLayout)return -1;String action=cards.actionAt(x,y);if(action==null||!action.startsWith("open "))return -1;try{return Integer.parseInt(action.substring(5));}catch(NumberFormatException ex){return -1;}}
 public void draw(TextDrawingArea font,int mouseX,int mouseY){
  DrawingArea.drawPixels(306,16,16,ExileTheme.PANEL,480);DrawingArea.fillPixels(16,480,306,ExileTheme.GOLD,16);
  String[] tabs={"Guide","Passives","Crafting","Maps","House base"};for(int i=0;i<tabs.length;i++)text(font,27+i*92,36,tabs[i],ExileTheme.HEADING);
  DrawingArea.method339(44,0x58616C,464,24);
  if(cardLayout){cards.draw(font,mouseX,mouseY);return;}
  if(lines[0].startsWith("tree|")){
   String[] head=lines[0].split("\\|");String[][] nodes=new String[22][];boolean[] owned=new boolean[22];
   for(int i=1;i<lines.length;i++){String[] f=lines[i].split("\\|",5);if(f.length<5)continue;int id=Integer.parseInt(f[0]);nodes[id]=f;owned[id]=Boolean.parseBoolean(f[2]);}
   text(font,92,57,"BLADE",0xCDB68A);text(font,365,57,"SURVIVAL",0xCDB68A);text(font,281,220,"ENERGY",0x9CAFD1);
   for(int id=1;id<=21;id++){if(nodes[id]==null)continue;int parent=Integer.parseInt(nodes[id][1]);int[] a=POS[parent],b=POS[id];line(a[0],a[1],b[0],b[1],owned[id]?0xB79A5C:0x3B4650);}
   circle(256,155,16,0x80683C);circle(256,155,13,0x25343B);text(font,252,160,"S",0xF0D496);
   int hovered=0;
   for(int id=1;id<=21;id++){if(nodes[id]==null)continue;int[] at=POS[id];int parent=Integer.parseInt(nodes[id][1]);boolean available=(parent==0||owned[parent])&&!owned[id]&&Integer.parseInt(head[1])>0;int color=owned[id]?0xE4C779:available?0xA1B7C8:0x45515C;
    circle(at[0],at[1],11,color);circle(at[0],at[1],8,owned[id]?0x62754C:0x17212A);text(font,at[0]-3,at[1]+4,nodes[id][3].substring(0,1),color);
    if((mouseX-at[0])*(mouseX-at[0])+(mouseY-at[1])*(mouseY-at[1])<=169){hovered=id;circle(at[0],at[1],3,0xFFF0BC);}
   }
   if(hovered>0){String[] f=nodes[hovered];text(font,27,285,f[3]+(owned[hovered]?" / Allocated":" / 1 point"),0xF0D496);String detail=f[4];text(font,27,297,detail.length()>75?detail.substring(0,75):detail,0xCCD6DF);}
   else{text(font,27,285,"Follow a connected path. Hover a node to see its upgrade.",0xA5B0BC);text(font,27,297,"Click to allocate / Ctrl-click to refund a leaf node.",0xA5B0BC);}
   text(font,27,314,"Points: "+head[1],ExileTheme.HEADING);text(font,337,314,"Free test refunds: "+head[2],0x939FAA);
  }else{
   for(int i=1;i<lines.length&&i<=20;i++){String[] row=lines[i].split("\t",2);String label=row.length==2?row[1]:row[0];if(label.length()>78)label=label.substring(0,78);text(font,27,66+(i-1)*12,label,row.length==2&&!row[0].isEmpty()?0x9BCDF3:0xA5ADB5);}
   text(font,27,309,"Click an option. P / Escape closes. Enter opens chat.",0x939FAA);
  }
 }
 private void text(TextDrawingArea font,int x,int y,String text,int color){font.method389(true,x,color,text,y);}
}
