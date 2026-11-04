package com.koios.client.arpg;

import java.util.*;
import com.koios.client.world.Model;

/** Original solid ground artefacts. Inventory illustrations and cache files remain independent. */
public final class CustomConsumableModels {
    private static int hsl(int hue,int saturation,int light){return hue<<10|saturation<<7|light;}
    private static final int GOLD=hsl(9,4,43),GOLD_EDGE=hsl(10,3,61),IRON=hsl(0,0,31),SILVER=hsl(0,0,57),DARK=hsl(0,0,19),COPPER=hsl(7,5,39),RED=hsl(0,5,35),RED_EDGE=hsl(1,4,51),PAPER=hsl(9,2,69),PAPER_EDGE=hsl(8,2,52),INK=hsl(5,2,23),BLUE=hsl(39,5,44),BLUE_EDGE=hsl(36,4,62),TEAL=hsl(30,5,42),TEAL_EDGE=hsl(29,4,62);

    public static boolean owns(int id){return id>=22400&&id<=22416;}

    public static Model create(Model source,int id){
        if(source==null||!owns(id))return source;
        Mesh mesh=new Mesh();
        switch(id){
            case 22400: // Alchemy: engraved gold orb in a raised metal cradle.
                mesh.sphere(0,-15,0,13,12,6,GOLD,GOLD_EDGE);
                mesh.torus(0,-15,0,13,1.6,16,5,GOLD_EDGE,0);
                mesh.torus(0,-15,0,13,1.4,16,5,GOLD_EDGE,Math.PI/2);
                mesh.crystal(0,-1,0,7,7,6,DARK,GOLD);
                break;
            case 22401: // Chaos: interlocked, irregular silver knots with a dark heart.
                mesh.sphere(0,-15,0,8,8,4,DARK,IRON);
                mesh.torus(0,-15,0,11,3.1,16,6,SILVER,.65);
                mesh.rotateY(0,.55);
                int second=mesh.vertices.size();mesh.torus(0,-15,0,11,3.1,16,6,IRON,-.65);mesh.rotateY(second,-.8);
                int third=mesh.vertices.size();mesh.torus(0,-15,0,9.5,2.6,14,6,SILVER,Math.PI/2);mesh.rotateY(third,1.3);
                break;
            case 22402: // Exalted: a black-steel reliquary with a sculpted silver mask.
                mesh.lathe(0,0,0,new double[][]{{3,-35},{10,-31},{13,-18},{11,-5},{5,-1}},8,DARK,IRON);
                mesh.plateXY(new double[][]{{-8,-27},{8,-27},{9,-16},{5,-8},{-5,-8},{-9,-16}},-13,3,SILVER,IRON);
                mesh.plateXY(new double[][]{{0,-26},{3,-18},{0,-12},{-3,-18}},-16,2,IRON,SILVER);
                mesh.sphere(-5,-23,-16,1.8,6,3,GOLD_EDGE,GOLD);mesh.sphere(5,-23,-16,1.8,6,3,GOLD_EDGE,GOLD);
                mesh.torus(0,-5,0,10.8,1.2,12,4,SILVER,0);
                mesh.torus(0,-25,0,11,1.2,12,4,IRON,0);
                mesh.plateXY(new double[][]{{0,-28},{5,-19},{0,-9},{-5,-19}},11,2,SILVER,IRON);
                break;
            case 22403: // Transmutation: copper vessel, bright neck ring and blue stone.
                mesh.lathe(0,0,0,new double[][]{{3,-32},{4,-28},{4,-23},{9,-20},{12,-13},{10,-5},{5,-1}},10,COPPER,GOLD);
                mesh.torus(0,-26,0,4.5,1.4,12,4,GOLD_EDGE,0);
                mesh.sphere(0,-14,-11,3.5,8,4,BLUE,BLUE_EDGE);
                break;
            case 22404: // Vaal: a faceted blood seed with three solid hooked thorns.
                mesh.crystal(0,-1,0,11,34,7,RED,RED_EDGE);
                for(int thorn=0;thorn<3;thorn++){
                    int start=mesh.vertices.size();mesh.spike(new double[]{8,-9,0},new double[]{18,-22,0},4,RED,IRON);mesh.spike(new double[]{18,-22,0},new double[]{13,-29,0},3,RED_EDGE,RED);mesh.rotateY(start,thorn*Math.PI*2/3);
                }
                break;
            case 22405: scroll(mesh,false);break;
            case 22406: scroll(mesh,true);break;
            case 22407: // Alteration: two crossed silver-blue rings around a prismatic heart.
                mesh.crystal(0,-6,0,7,23,6,BLUE,BLUE_EDGE);
                mesh.torus(0,-18,0,13,2.1,20,5,SILVER,.78);
                int alter=mesh.vertices.size();mesh.torus(0,-18,0,13,2.1,20,5,BLUE, -.78);mesh.rotateY(alter,Math.PI/2);
                break;
            case 22408: // Augmentation: jade spire in a solid hexagonal brass socket.
                mesh.lathe(0,0,0,new double[][]{{10,-12},{14,-8},{13,-1}},6,GOLD,COPPER);
                mesh.crystal(0,-6,0,7,31,5,TEAL,TEAL_EDGE);
                for(int arm=0;arm<4;arm++){int begin=mesh.vertices.size();mesh.spike(new double[]{9,-7,0},new double[]{6,-21,0},2.5,GOLD,GOLD_EDGE);mesh.rotateY(begin,arm*Math.PI/2);}
                break;
            case 22409: // Regal: amethyst jewel embraced by a three-point bronze cage.
                mesh.crystal(0,-4,0,12,33,7,hsl(48,4,39),hsl(47,4,58));
                mesh.lathe(0,0,0,new double[][]{{10,-9},{14,-5},{12,-1}},9,COPPER,GOLD);
                for(int arm=0;arm<3;arm++){int begin=mesh.vertices.size();mesh.spike(new double[]{11,-4,0},new double[]{15,-25,0},2.5,GOLD,COPPER);mesh.sphere(15,-25,0,2.7,8,4,GOLD,GOLD_EDGE);mesh.rotateY(begin,arm*Math.PI*2/3);}
                break;
            case 22415: // Scouring: ivory ceramic vessel wrapped by silver seams.
                mesh.sphere(0,-16,0,14,16,8,PAPER,PAPER_EDGE);
                mesh.torus(0,-16,0,14,1.4,20,5,SILVER,.65);
                int scour=mesh.vertices.size();mesh.torus(0,-16,0,14,1.3,20,5,IRON,-.65);mesh.rotateY(scour,Math.PI/2);
                mesh.lathe(0,-30,0,new double[][]{{5,-2},{6,-1},{5,0}},10,SILVER,IRON);
                break;
            case 22416: // Annulment: capped broken obsidian ring, violet inner fracture.
                mesh.torusArc(0,-18,0,12,3.8,26,6,DARK,.42,Math.PI*2-.42,Math.PI/2);
                mesh.torusArc(0,-18,0,9.4,1.3,26,4,hsl(48,4,45),.42,Math.PI*2-.42,Math.PI/2);
                for(int rivet=0;rivet<3;rivet++){double angle=1.3+rivet*1.7;mesh.sphere(Math.cos(angle)*12,-18-Math.sin(angle)*12,3,2,7,4,SILVER,IRON);}
                break;
            case 22410: map(mesh,0);break;
            case 22413: map(mesh,1);break;
            case 22414: map(mesh,2);break;
            case 22411: // Skill gem: one tall blue crystal.
                mesh.crystal(0,-1,0,10,40,7,BLUE,BLUE_EDGE);
                break;
            case 22412: // Support gem: amber cluster matching its inventory illustration.
                mesh.crystal(0,-1,0,8,33,6,COPPER,GOLD_EDGE);
                int left=mesh.vertices.size();mesh.crystal(-10,-1,1,6,23,6,COPPER,GOLD_EDGE);mesh.rotateZ(left,-.18,-10,-1);
                int right=mesh.vertices.size();mesh.crystal(10,-1,-1,6,26,6,GOLD_EDGE,COPPER);mesh.rotateZ(right,.18,10,-1);
                break;
            default:return source;
        }
        return mesh.finish(source);
    }

    private static double mapY(double x,double z){double[] xs={-24,-12,0,12,24},ys={-5,-9,-5,-9,-5};int section=Math.min(3,Math.max(0,(int)((x+24)/12)));double t=(x-xs[section])/12;return ys[section]*(1-t)+ys[section+1]*t-Math.max(0,(Math.abs(x)-16)/8)*Math.max(0,(Math.abs(z)-10)/8)*5;}
    private static void map(Mesh mesh,int biome){
        int paper=biome==1?hsl(7,1,55):biome==2?hsl(17,1,66):PAPER;int edge=biome==1?hsl(7,1,40):biome==2?hsl(17,1,47):PAPER_EDGE;int ink=biome==1?DARK:biome==2?hsl(25,2,30):INK;int seal=biome==1?hsl(3,3,39):biome==2?hsl(25,3,35):RED;
        mesh.foldedMap(paper,edge);double[][] route=biome==1?new double[][]{{-17,-10},{-7,-10},{-7,1},{6,1},{6,10}}:biome==2?new double[][]{{-17,9},{-10,4},{-7,-5},{3,-9},{10,-5}}:new double[][]{{-17,-9},{-11,-3},{-2,-4},{3,4},{7,8}};
        for(int i=0;i<route.length-1;i++)mesh.mapRoute(route[i],route[i+1],1.2,ink);
        for(int i=0;i<3;i++){double x=-14+i*10,z=-12;mesh.box(x,mapY(x,z)-1.1,z,3,.7,2,ink);}
        mesh.lathe(14,mapY(14,9)-.8,9,new double[][]{{3.7,-2},{5,-1},{4.5,0}},10,seal,biome==1?GOLD:biome==2?TEAL_EDGE:RED_EDGE);
    }
    private static void scroll(Mesh mesh,boolean portal){
        // The sheet has thickness and a shallow bow; the two rolled edges are solid cylinders.
        mesh.sheet(-20,20,-13,13,portal?1.2:.6,PAPER,PAPER_EDGE);
        mesh.cylinderZ(-20,-6,-14,14,4.8,10,PAPER,PAPER_EDGE);
        mesh.cylinderZ(20,-6,-14,14,4.8,10,PAPER,PAPER_EDGE);
        for(int line=0;line<3;line++)mesh.box(-11,-8.6,-7+line*4,portal?8:12,.65,1.2,INK);
        mesh.lathe(7,-9.8,5,new double[][]{{4,-1.6},{4.8,-.8},{4.2,0}},10,portal?TEAL:RED,portal?TEAL_EDGE:RED_EDGE);
        if(portal){mesh.box(-2,-9,-13,3,.8,26,TEAL);mesh.spike(new double[]{0,-10,4},new double[]{-7,-10,12},1.5,TEAL,TEAL_EDGE);}
    }

    private static final class Mesh {
        final List<double[]> vertices=new ArrayList<>();
        final List<int[]> faces=new ArrayList<>();
        int vertex(double x,double y,double z){vertices.add(new double[]{x,y,z});return vertices.size()-1;}
        void face(int a,int b,int c,int color){faces.add(new int[]{a,b,c,color});}
        void doubleFace(int a,int b,int c,int color){face(a,b,c,color);face(a,c,b,color);}
        void quad(int a,int b,int c,int d,int color){face(a,b,c,color);face(a,c,d,color);}

        void foldedMap(int color,int edge){
            int start=vertices.size();for(int layer=0;layer<2;layer++)for(int column=0;column<5;column++)for(int row=0;row<3;row++){double x=-24+column*12,z=-18+row*18;vertex(x,mapY(x,z)+layer*1.5,z);}for(int column=0;column<4;column++)for(int row=0;row<2;row++){int a=start+column*3+row;quad(a,a+3,a+4,a+1,color);quad(a+15,a+16,a+19,a+18,edge);}for(int column=0;column<4;column++){int a=start+column*3,b=a+2;quad(a,a+15,a+18,a+3,edge);quad(b,b+3,b+18,b+15,edge);}for(int row=0;row<2;row++){int a=start+row,b=start+12+row;quad(a,a+1,a+16,a+15,edge);quad(b,b+15,b+16,b+1,edge);}
        }
        void mapRoute(double[] a,double[] b,double width,int color){List<Double> cuts=new ArrayList<>();cuts.add(0d);cuts.add(1d);if(a[0]!=b[0])for(int crease:new int[]{-12,0,12}){double t=(crease-a[0])/(b[0]-a[0]);if(t>0&&t<1)cuts.add(t);}Collections.sort(cuts);for(int i=0;i<cuts.size()-1;i++){double first=cuts.get(i),last=cuts.get(i+1);mapRouteSegment(new double[]{a[0]+(b[0]-a[0])*first,a[1]+(b[1]-a[1])*first},new double[]{a[0]+(b[0]-a[0])*last,a[1]+(b[1]-a[1])*last},width,color);}}
        void mapRouteSegment(double[] a,double[] b,double width,int color){double length=Math.hypot(b[0]-a[0],b[1]-a[1]),dx=-(b[1]-a[1])/length*width/2,dz=(b[0]-a[0])/length*width/2;int start=vertices.size();double[][] points={{a[0]+dx,a[1]+dz},{b[0]+dx,b[1]+dz},{b[0]-dx,b[1]-dz},{a[0]-dx,a[1]-dz}};for(int layer=0;layer<2;layer++)for(double[] point:points)vertex(point[0],mapY(point[0],point[1])-.8+layer*.5,point[1]);quad(start,start+1,start+2,start+3,color);quad(start+4,start+7,start+6,start+5,color);for(int i=0;i<4;i++){int j=(i+1)%4;quad(start+i,start+4+i,start+4+j,start+j,color);}}
        void sphere(double x,double y,double z,double radius,int sides,int rings,int color,int accent){
            int start=vertices.size();int top=vertex(x,y-radius,z);
            for(int ring=1;ring<rings;ring++){double latitude=Math.PI*ring/rings;for(int side=0;side<sides;side++){double longitude=Math.PI*2*side/sides;vertex(x+Math.cos(longitude)*Math.sin(latitude)*radius,y-Math.cos(latitude)*radius,z+Math.sin(longitude)*Math.sin(latitude)*radius);}}
            int bottom=vertex(x,y+radius,z);
            for(int side=0;side<sides;side++){int next=(side+1)%sides;face(top,start+1+side,start+1+next,accent);for(int ring=0;ring<rings-2;ring++){int a=start+1+ring*sides+side,b=start+1+ring*sides+next,c=b+sides,d=a+sides;quad(a,d,c,b,side%4==0?accent:color);}face(bottom,start+1+(rings-2)*sides+next,start+1+(rings-2)*sides+side,color);}
        }

        void torus(double x,double y,double z,double radius,double tube,int sides,int tubeSides,int color,double tilt){
            int start=vertices.size();double cos=Math.cos(tilt),sin=Math.sin(tilt);
            for(int side=0;side<sides;side++){double angle=Math.PI*2*side/sides;for(int point=0;point<tubeSides;point++){double round=Math.PI*2*point/tubeSides;double vx=Math.cos(angle)*(radius+Math.cos(round)*tube),vy=Math.sin(round)*tube,vz=Math.sin(angle)*(radius+Math.cos(round)*tube);vertex(x+vx,y+vy*cos-vz*sin,z+vy*sin+vz*cos);}}
            for(int side=0;side<sides;side++)for(int point=0;point<tubeSides;point++){int next=(side+1)%sides,round=(point+1)%tubeSides;quad(start+side*tubeSides+point,start+next*tubeSides+point,start+next*tubeSides+round,start+side*tubeSides+round,color);}
        }

        void torusArc(double x,double y,double z,double radius,double tube,int sides,int tubeSides,int color,double first,double last,double tilt){
            int start=vertices.size();double cos=Math.cos(tilt),sin=Math.sin(tilt);for(int side=0;side<=sides;side++){double angle=first+(last-first)*side/sides;for(int point=0;point<tubeSides;point++){double round=Math.PI*2*point/tubeSides,vx=Math.cos(angle)*(radius+Math.cos(round)*tube),vy=Math.sin(round)*tube,vz=Math.sin(angle)*(radius+Math.cos(round)*tube);vertex(x+vx,y+vy*cos-vz*sin,z+vy*sin+vz*cos);}}
            for(int side=0;side<sides;side++)for(int point=0;point<tubeSides;point++){int next=(point+1)%tubeSides;quad(start+side*tubeSides+point,start+(side+1)*tubeSides+point,start+(side+1)*tubeSides+next,start+side*tubeSides+next,color);}for(int end=0;end<2;end++){int ring=start+end*sides*tubeSides;double angle=end==0?first:last;int center=vertex(x+Math.cos(angle)*radius,y-Math.sin(angle)*radius*sin,z+Math.sin(angle)*radius*cos);for(int point=0;point<tubeSides;point++)doubleFace(center,ring+point,ring+(point+1)%tubeSides,color);}
        }

        void lathe(double x,double y,double z,double[][] profile,int sides,int color,int accent){
            int start=vertices.size();for(double[] ring:profile)for(int side=0;side<sides;side++){double angle=side*Math.PI*2/sides;vertex(x+Math.cos(angle)*ring[0],y+ring[1],z+Math.sin(angle)*ring[0]);}
            int top=vertex(x,y+profile[0][1],z),bottom=vertex(x,y+profile[profile.length-1][1],z);
            for(int side=0;side<sides;side++){int next=(side+1)%sides;doubleFace(top,start+side,start+next,accent);for(int ring=0;ring<profile.length-1;ring++)quad(start+ring*sides+side,start+(ring+1)*sides+side,start+(ring+1)*sides+next,start+ring*sides+next,side%3==0?accent:color);doubleFace(bottom,start+(profile.length-1)*sides+next,start+(profile.length-1)*sides+side,color);}
        }

        void crystal(double x,double y,double z,double radius,double height,int sides,int color,int accent){
            lathe(x,y,z,new double[][]{{0,-height},{radius*.72,-height*.72},{radius,-height*.25},{radius*.55,-height*.08},{0,0}},sides,color,accent);
        }

        void plateXY(double[][] polygon,double z,double thickness,int color,int edge){
            List<int[]> triangles=CustomWeaponModels.triangulate(polygon);int start=vertices.size(),count=polygon.length;for(int layer=0;layer<2;layer++)for(double[] point:polygon)vertex(point[0],point[1],z+layer*thickness);
            for(int[] triangle:triangles){doubleFace(start+triangle[0],start+triangle[1],start+triangle[2],color);doubleFace(start+count+triangle[2],start+count+triangle[1],start+count+triangle[0],color);}for(int i=0;i<count;i++){int next=(i+1)%count;quad(start+i,start+next,start+count+next,start+count+i,edge);}
        }

        void box(double x,double y,double z,double width,double height,double depth,int color){
            int start=vertices.size();for(int layer=0;layer<2;layer++){vertex(x,y+layer*height,z);vertex(x+width,y+layer*height,z);vertex(x+width,y+layer*height,z+depth);vertex(x,y+layer*height,z+depth);}
            quad(start,start+3,start+2,start+1,color);quad(start+4,start+5,start+6,start+7,color);for(int side=0;side<4;side++){int next=(side+1)%4;quad(start+side,start+next,start+4+next,start+4+side,color);}
        }

        void cylinderZ(double x,double y,double first,double last,double radius,int sides,int color,int edge){
            int start=vertices.size();for(int layer=0;layer<2;layer++)for(int side=0;side<sides;side++){double angle=side*Math.PI*2/sides;vertex(x+Math.cos(angle)*radius,y+Math.sin(angle)*radius,layer==0?first:last);}
            int a=vertex(x,y,first),b=vertex(x,y,last);for(int side=0;side<sides;side++){int next=(side+1)%sides;doubleFace(a,start+next,start+side,edge);doubleFace(b,start+sides+side,start+sides+next,edge);quad(start+side,start+next,start+sides+next,start+sides+side,color);}
        }

        void sheet(double left,double right,double first,double last,double curl,int color,int edge){
            int start=vertices.size(),strips=8;for(int layer=0;layer<2;layer++)for(int strip=0;strip<=strips;strip++){double fraction=strip/(double)strips,x=left+(right-left)*fraction,y=-5-Math.sin(fraction*Math.PI)*(2+curl)+layer*1.1;vertex(x,y,first);vertex(x,y,last);}
            int row=(strips+1)*2;for(int strip=0;strip<strips;strip++){int a=start+strip*2;quad(a,a+2,a+3,a+1,color);quad(a+row+1,a+row+3,a+row+2,a+row,edge);quad(a,a+row,a+row+2,a+2,edge);quad(a+1,a+3,a+row+3,a+row+1,edge);}quad(start,start+1,start+row+1,start+row,edge);int end=start+strips*2;quad(end,end+row,end+row+1,end+1,edge);
        }

        void spike(double[] base,double[] tip,double radius,int color,int edge){
            int start=vertices.size();vertex(base[0]-radius,base[1],base[2]-radius);vertex(base[0]+radius,base[1],base[2]-radius);vertex(base[0]+radius,base[1],base[2]+radius);vertex(base[0]-radius,base[1],base[2]+radius);int point=vertex(tip[0],tip[1],tip[2]);quad(start,start+3,start+2,start+1,edge);for(int side=0;side<4;side++)doubleFace(start+side,start+(side+1)%4,point,side%2==0?color:edge);
        }
        void rotateY(int start,double angle){double cosine=Math.cos(angle),sine=Math.sin(angle);for(int i=start;i<vertices.size();i++){double[] point=vertices.get(i);double x=point[0],z=point[2];point[0]=x*cosine-z*sine;point[2]=x*sine+z*cosine;}}
        void rotateZ(int start,double angle,double x,double y){double cosine=Math.cos(angle),sine=Math.sin(angle);for(int i=start;i<vertices.size();i++){double[] point=vertices.get(i);double dx=point[0]-x,dy=point[1]-y;point[0]=x+dx*cosine-dy*sine;point[1]=y+dx*sine+dy*cosine;}}

        Model finish(Model source){
            Model model=new Model(false,false,false,source);model.anInt1626=vertices.size();model.anIntArray1627=new int[model.anInt1626];model.anIntArray1628=new int[model.anInt1626];model.anIntArray1629=new int[model.anInt1626];
            for(int i=0;i<vertices.size();i++){double[] vertex=vertices.get(i);model.anIntArray1627[i]=(int)Math.round(vertex[0]);model.anIntArray1628[i]=(int)Math.round(vertex[1]);model.anIntArray1629[i]=(int)Math.round(vertex[2]);}
            List<int[]> valid=new ArrayList<>();for(int[] face:faces){long ax=model.anIntArray1627[face[1]]-model.anIntArray1627[face[0]],ay=model.anIntArray1628[face[1]]-model.anIntArray1628[face[0]],az=model.anIntArray1629[face[1]]-model.anIntArray1629[face[0]],bx=model.anIntArray1627[face[2]]-model.anIntArray1627[face[0]],by=model.anIntArray1628[face[2]]-model.anIntArray1628[face[0]],bz=model.anIntArray1629[face[2]]-model.anIntArray1629[face[0]];if(ay*bz-az*by!=0||az*bx-ax*bz!=0||ax*by-ay*bx!=0)valid.add(face);}
            model.anInt1630=valid.size();model.anIntArray1631=new int[model.anInt1630];model.anIntArray1632=new int[model.anInt1630];model.anIntArray1633=new int[model.anInt1630];model.anIntArray1640=new int[model.anInt1630];model.anIntArray1637=new int[model.anInt1630];Arrays.fill(model.anIntArray1637,1);
            for(int i=0;i<valid.size();i++){int[] face=valid.get(i);model.anIntArray1631[i]=face[0];model.anIntArray1632[i]=face[1];model.anIntArray1633[i]=face[2];model.anIntArray1640[i]=face[3];}
            model.anInt1641=0;model.anInt1642=0;model.anIntArray1634=model.anIntArray1635=model.anIntArray1636=model.anIntArray1638=model.anIntArray1639=model.anIntArray1655=model.anIntArray1656=model.anIntArray1643=model.anIntArray1644=model.anIntArray1645=null;model.anIntArrayArray1657=model.anIntArrayArray1658=null;model.aClass33Array1660=null;return model;
        }
    }
}




