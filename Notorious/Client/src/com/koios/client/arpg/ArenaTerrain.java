package com.koios.client.arpg;

import com.koios.client.definition.FloorDefinition;

/** Flat, themed terrain for the server-authored arena layouts. Cache files stay unchanged. */
public final class ArenaTerrain {
    public static void apply(int layout,int[][][] heights,byte[][][] settings,byte[][][] underlay,byte[][][] overlay,byte[][][] shapes,byte[][][] rotations) {
        if(layout<1||layout>2)return;
        int outside=floor(0x343A31),base=floor(layout==1?0x58514A:0x41583A),path=floor(layout==1?0x80776A:0x817D69),edge=floor(0x685E4B);
        int ash=textureFloor(43,floor(0x53564F,true)),workshopBrick=textureFloor(3,floor(0x6D5039,true)),stone=textureFloor(11,floor(0x77776F,true)),brass=floor(0x625F57,true),coal=floor(0x3F4441,true),inlay=floor(0x636B63,true);
        int courtyardStone=textureFloor(11,floor(0x62685B,true)),moss=floor(0x354A31,true),garden=floor(0x2C402B,true),earth=floor(0x4A4032,true),jade=floor(0x526851,true);
        for(int plane=0;plane<4;plane++) {
            for(int x=0;x<105;x++)for(int y=0;y<105;y++)heights[plane][x][y]=-240*plane;
            for(int x=0;x<104;x++)for(int y=0;y<104;y++){
                settings[plane][x][y]=0;overlay[plane][x][y]=0;shapes[plane][x][y]=0;rotations[plane][x][y]=0;
                if(plane>0){underlay[plane][x][y]=0;continue;}
                boolean inside=x>=32&&x<=72&&y>=32&&y<=72;
                boolean lane=layout==1?(x>=49&&x<=55||y>=62&&x>=44&&x<=61):(x>=49&&x<=55||y>=49&&y<=53||y>=62&&x>=46&&x<=58);
                boolean border=x==32||x==72||y==32||y==72;
                underlay[0][x][y]=(byte)(!inside?outside:border?edge:lane?path:base);
                if(layout==1&&inside&&!border){
                    // Broad, deliberate material zones. Overlays keep doorway/pad edges
                    // crisp instead of blending every room into the same muddy underlay.
                    int material=ash;
                    boolean workshop=(x<=41||x>=63)&&(y<=45||y>=60);
                    boolean benchBay=(x<=41||x>=63)&&y>=48&&y<=55;
                    if(workshop||benchBay)material=workshopBrick;
                    if(x>=49&&x<=55&&y<=55)material=stone;
                    if((x==48||x==56)&&y<=55)material=brass;
                    if(y>=53&&y<=55&&x>=40&&x<=64)material=stone;
                    if((y==52||y==56)&&x>=40&&x<=64)material=brass;
                    if(x>=47&&x<=57&&y>=49&&y<=54)material=coal;
                    if((x==47||x==57)&&y>=49&&y<=54||(y==49||y==54)&&x>=47&&x<=57)material=brass;
                    if(x>=50&&x<=54&&y>=50&&y<=53)material=inlay;
                    // The northern octagonal striking floor is a recognizable endpoint.
                    int dx=Math.abs(x-52),dy=Math.abs(y-66);
                    if(dx<=9&&dy<=5&&dx+dy<=12){material=coal;if(dx==9||dy==5||dx+dy>=11)material=brass;else if(dx+dy==4)material=inlay;}
                    if(y==57&&(x>=40&&x<=45||x>=59&&x<=64))material=stone;
                    overlay[0][x][y]=(byte)material;
                }else if(layout==2&&inside&&!border){
                    // Broad authored zones avoid the diagonal blended-underlay
                    // striping that made the first Courtyard look procedural.
                    int material=moss;
                    boolean nave=x>=48&&x<=56;
                    boolean crossing=y>=45&&y<=48;
                    boolean northCourt=y>=62;
                    boolean westBed=x>=34&&x<=43&&y>=48&&y<=60;
                    boolean eastBed=x>=61&&x<=70&&y>=48&&y<=60;
                    if(nave||crossing)material=courtyardStone;
                    if(westBed||eastBed)material=garden;
                    if((westBed||eastBed)&&((x+y)&3)==0)material=earth;
                    if(northCourt)material=moss;
                    int dx=Math.abs(x-52),dy=Math.abs(y-67);
                    if(dx<=8&&dy<=5&&dx+dy<=11){material=courtyardStone;if(dx==8||dy==5||dx+dy>=10)material=jade;}
                    // Worn side passages visually connect both garden loops.
                    if((x>=36&&x<=40||x>=64&&x<=68)&&y>=43&&y<=66)material=earth;
                    overlay[0][x][y]=(byte)material;
                }
            }
        }
    }

    /** These textures were inspected from this cache: ash pavers, firebrick and low-contrast stone. */
    private static int textureFloor(int texture,int fallback){
        for(int i=0;i<FloorDefinition.cache.length&&i<255;i++)if(FloorDefinition.cache[i].anInt391==texture&&i!=54&&i!=63&&i!=111)return i+1;
        return fallback;
    }

    private static int floor(int target){return floor(target,false);}
    private static int floor(int target,boolean overlay){
        int best=1;long distance=Long.MAX_VALUE;
        for(int i=0;i<FloorDefinition.cache.length&&i<255;i++){
            FloorDefinition candidate=FloorDefinition.cache[i];
            if(overlay&&(candidate.anInt391>=0||i==54||i==63||i==111||candidate.anInt390==0x333333))continue;
            // Opcode-7 overlay colors can replace the underlay hue multiplier
            // with its weighted hue. Blending those floors produces rainbow bands.
            if(candidate.anInt398<=0||candidate.anInt397>=candidate.anInt398)continue;
            int rgb=candidate.anInt390;
            if(rgb==0||rgb==0xFF00FF)continue;
            long red=(rgb>>16&255)-(target>>16&255),green=(rgb>>8&255)-(target>>8&255),blue=(rgb&255)-(target&255);
            long delta=red*red+green*green+blue*blue;
            if(delta<distance){distance=delta;best=i+1;}
        }
        return best;
    }
}
