package com.koios.game.arpg;

import com.koios.game.map.*;
import com.koios.game.model.Location;

/** Original room compositions assembled from client-verified cache scenery. */
public final class AuthoredMaps {
    public static String name(int layout){return layout==1?"Ashen Foundry":layout==2?"Thornwild Courtyard":"Forsaken Settlement";}

    public static Location build(RegionInstance region,int layout){
        region.arenaLayout=layout;
        for(int x=0;x<104;x++)for(int y=0;y<104;y++)
            if(x<32||x>72||y<32||y>72)MapRegion.getRegion(region.baseX+x,region.baseY+y).setClip(region.baseX+x,region.baseY+y,0,0xFFFFFF);
        int wall=layout==1?1583:2068;
        for(int n=32;n<=72;n++){
            place(region,wall,n,32,0,1);place(region,wall,n,72,0,1);
            if(n>32&&n<72){place(region,wall,32,n,0,0);place(region,wall,72,n,0,0);}
        }
        if(layout==1)foundry(region);else courtyard(region);
        return new Location(region.baseX+52,region.baseY+38,0);
    }

    private static void foundry(RegionInstance region){
        // A wide central arrival yard, two practical workshop bays and an assembly hall.
        // Workshop doors are three tiles wide; none of their furniture occupies the entry route.
        for(int y=34;y<=44;y++)if(y<38||y>40){place(region,1583,42,y,0,0);place(region,1583,62,y,0,0);}
        for(int x=33;x<72;x++){
            if(x<48||x>55)place(region,1583,x,46,0,1);
            if(!(x>=40&&x<=45)&&!(x>=59&&x<=64))place(region,1583,x,57,0,1);
        }

        // West finishing bay: a vice bench, striking anvil, stock crates and sealed barrels.
        place(region,609,35,43,10,0);place(region,2783,39,42,10,0);
        crates(region,new int[][]{{34,34},{35,34},{34,35}});
        barrels(region,new int[][]{{39,34},{40,34}});

        // East forging bay: a second bench and a pair of working anvils.
        place(region,609,66,43,10,0);place(region,2783,66,36,10,0);place(region,2783,69,39,10,0);
        crates(region,new int[][]{{64,34},{65,34},{70,34}});
        barrels(region,new int[][]{{69,43},{70,43}});

        // Assembly hall stations sit at the sides, with one central inspection bench.
        place(region,609,35,52,10,0);place(region,609,66,52,10,0);place(region,609,51,52,10,0);
        crates(region,new int[][]{{34,49},{35,49},{34,50},{69,49},{70,49},{70,50}});
        barrels(region,new int[][]{{38,54},{39,54},{64,54},{65,54}});
        place(region,2783,39,50,10,0);place(region,2783,64,50,10,0);

        // Northern supply alcoves frame a generous, uncluttered foreman's arena.
        crates(region,new int[][]{{34,61},{35,61},{34,62},{69,61},{70,61},{70,62}});
        place(region,609,35,68,10,0);place(region,609,66,69,10,0);
        barrels(region,new int[][]{{39,68},{40,68},{64,68},{70,68}});
        place(region,2783,38,64,10,0);place(region,2783,66,64,10,0);
        place(region,2783,52,70,10,0);
    }

    private static void courtyard(RegionInstance region){
        // The Courtyard is a ruined cloister rather than an empty fenced field.
        // A broad central nave always connects arrival (52,38) to the Warden at
        // (52,67); side openings create loops without hiding the objective route.
        for(int x=33;x<72;x++)if(!(x>=48&&x<=56)&&!(x>=36&&x<=40)&&!(x>=64&&x<=68))place(region,1583,x,46,0,1);

        // Low garden railings divide the four overgrown beds. Every run has two
        // openings, so ranged enemies cannot trap a player behind a decorative row.
        for(int y=48;y<=60;y++)if(y!=52&&y!=57){place(region,2068,44,y,0,0);place(region,2068,60,y,0,0);}
        for(int x=34;x<=43;x++)if(x!=38)place(region,2068,x,61,0,1);
        for(int x=61;x<=70;x++)if(x!=66)place(region,2068,x,61,0,1);

        // Broken north cloister: staggered wall fragments imply rooms while the
        // five-tile nave and both flanking gates remain readable at combat speed.
        for(int x=33;x<=47;x++)if(x<38||x>41)place(region,1583,x,64,0,1);
        for(int x=57;x<=71;x++)if(x<63||x>66)place(region,1583,x,64,0,1);
        for(int y=65;y<=70;y++){if(y!=67)place(region,1583,43,y,0,0);if(y!=68)place(region,1583,61,y,0,0);}

        // Four sentinels frame the boss lawn. Crates and barrels are confined to
        // collapsed storage corners, leaving a generous dodge envelope around it.
        for(int x:new int[]{47,57})for(int y:new int[]{62,69})place(region,574,x,y,10,0);
        crates(region,new int[][]{{34,35},{35,35},{34,42},{69,35},{70,35},{70,42},{35,67},{69,68}});
        barrels(region,new int[][]{{37,35},{67,35},{36,69},{68,66}});
    }

    private static void crates(RegionInstance region,int[][] cells){for(int[] cell:cells)place(region,357,cell[0],cell[1],10,0);}
    private static void barrels(RegionInstance region,int[][] cells){for(int[] cell:cells)place(region,362,cell[0],cell[1],10,0);}

    /** Footprints match the verified client definitions, including rotated two-tile workbenches. */
    private static void place(RegionInstance region,int id,int dx,int dy,int type,int face){
        int width=id==609||id==13704?2:1,height=1;
        if((face&1)!=0&&type==10){int swap=width;width=height;height=swap;}
        if(dx<32||dy<32||dx+width-1>72||dy+height-1>72)throw new IllegalArgumentException("Scenery leaves arena bounds");
        int x=region.baseX+dx,y=region.baseY+dy;
        RSObject object=new RSObject(x,y,0,id,type,face);MapRegion.getRegion(x,y).addObject(object);
        for(int a=0;a<width;a++)for(int b=0;b<height;b++){
            MapRegion.getRegion(x+a,y+b).setClip(x+a,y+b,0,0xFFFFFF);
            MapRegion.getRegion(x+a,y+b).addShootable(x+a,y+b,0,0xFFFFFF);
        }
        region.scenery.add(object);
    }
}
