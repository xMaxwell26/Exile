package com.koios.client.arpg;
import java.util.*;
import com.koios.client.world.Model;
/** Original visible NPC meshes. Cached models supply skeleton pivot statistics, never visible faces. */
public final class CustomNpcModels {
    public static final int CINDER_CONSTRUCT=15001;
    public static final int BRIAR_WARDEN=15007;
    private static final int BARK=hsl(7,2,25);
    private static final int BARK_EDGE=hsl(8,2,38);
    private static final int MOSS=hsl(20,2,27);
    private static final int HEART=hsl(23,4,43);
    private static final int HEART_LIGHT=hsl(22,4,66);
    // Template90 vertex skin groups. Negative X is the hammer side in bind pose.
    private static final int BONE_TORSO=3;
    private static final int BONE_HEAD=1;
    private static final int BONE_HAMMER_UPPER=15;
    private static final int BONE_CLAW_UPPER=8;
    private static final int BONE_HAMMER_FOREARM=12;
    private static final int BONE_CLAW_FOREARM=5;
    private static final int BONE_HIPS=54;
    private static final int BONE_LEFT_THIGH=32;
    private static final int BONE_RIGHT_THIGH=40;
    private static final int BONE_LEFT_SHIN=28;
    private static final int BONE_RIGHT_SHIN=36;
    private static final int BONE_LEFT_FOOT=44;
    private static final int BONE_RIGHT_FOOT=26;
    // Visible points fit inside one tile. Anchors preserve the original skeleton pivot distribution.
    private static final double VISIBLE_WIDTH_SCALE=.82;
    // Leave compensation room: the mathematical minimum made hidden anchors explode outside the body.
    // Do not lower this without all-frame bounds tests. Total vertices must stay below native 8,000.
    private static final int PIVOT_MASS_FACTOR=11;
    private static int hsl(int h,int s,int l) {
        return h<<10|s<<7|l;
    }

    private static final int IRON=hsl(0,0,25);
    private static final int STEEL=hsl(36,1,36);
    private static final int EDGE=hsl(0,0,52);
    private static final int STONE=hsl(36,1,30);
    private static final int DARK=hsl(0,0,12);
    private static final int COPPER=hsl(4,3,37);
    private static final int EMBER=hsl(5,6,57);
    private static final int FIRE=hsl(10,5,76);
    public static boolean owns(int id) {
        return id==CINDER_CONSTRUCT||id==BRIAR_WARDEN;
    }

    /** Coordinates are cache world units: Y=0 ground, negative Y up, negative Z chest front.
     * Every visible vertex/triangle below is original. The source supplies rig statistics only.
     */
    public static Model create(Model source,int id) {
        if(source==null||!owns(id))return source;
        if(source.anIntArray1655==null)throw new IllegalArgumentException("Construct requires an ungrouped template90 skeleton");
        Mesh mesh=new Mesh(source);
        if(id==BRIAR_WARDEN){
            briarTrunk(mesh);
            briarCrown(mesh);
            briarArms(mesh);
            briarLegsAndRoots(mesh);
            return mesh.finish();
        }
        chassis(mesh);
        hood(mesh);
        arms(mesh);
        lowerBody(mesh);
        jointCapsAndVents(mesh);
        return mesh.finish();
    }

    /** Hollow twisted wood protects a recessed living heart; no construct boxes are reused. */
    private static void briarTrunk(Mesh mesh) {
        mesh.branch(-9,-126,14,6,-154,16,18,25,BONE_TORSO,BARK,BARK_EDGE);
        mesh.branch(6,-154,16,-5,-184,12,25,21,BONE_TORSO,BARK,BARK_EDGE);
        // Two winding front ribs leave the green heart visible between them.
        mesh.branch(-21,-135,-5,-28,-159,-16,10,12,BONE_TORSO,BARK,BARK_EDGE);
        mesh.branch(-28,-159,-16,-12,-187,-2,12,8,BONE_TORSO,BARK,BARK_EDGE);
        mesh.branch(18,-136,-6,28,-161,-15,11,12,BONE_TORSO,BARK_EDGE,BARK);
        mesh.branch(28,-161,-15,10,-185,-3,12,8,BONE_TORSO,BARK_EDGE,BARK);
        mesh.disc(0,-160,-20,-12,14,7,BONE_TORSO,HEART,MOSS);
        mesh.disc(0,-160,-21,-20,6,6,BONE_TORSO,HEART_LIGHT,HEART);
        // Bark knots and a mossy rear spine are 3D, readable from the back and sides.
        mesh.branch(-7,-141,33,8,-180,30,9,6,BONE_TORSO,MOSS,BARK_EDGE);
        mesh.branch(-24,-175,10,-39,-184,15,10,6,BONE_TORSO,BARK,BARK_EDGE);
        mesh.branch(24,-174,9,39,-183,18,11,6,BONE_TORSO,BARK_EDGE,BARK);
        mesh.branch(-16,-133,3,15,-116,12,14,13,BONE_HIPS,BARK,BARK_EDGE);
    }

    /** Recessed knot-face and a forked branch crown replace the humanoid head silhouette. */
    private static void briarCrown(Mesh mesh) {
        mesh.branch(0,-189,10,0,-214,9,10,13,BONE_HEAD,BARK,BARK_EDGE);
        mesh.branch(-8,-210,8,-17,-234,10,7,5,BONE_HEAD,BARK,BARK_EDGE);
        mesh.branch(-17,-234,10,-28,-248,7,5,1,BONE_HEAD,BARK_EDGE,BARK);
        mesh.branch(-17,-234,10,-12,-252,13,4,1,BONE_HEAD,BARK,BARK_EDGE);
        mesh.branch(7,-212,9,16,-235,7,7,4,BONE_HEAD,BARK_EDGE,BARK);
        mesh.branch(16,-235,7,29,-244,3,4,1,BONE_HEAD,BARK,BARK_EDGE);
        mesh.branch(16,-235,7,12,-255,9,4,1,BONE_HEAD,BARK_EDGE,BARK);
        mesh.disc(-5,-207,-3,0,3,5,BONE_HEAD,HEART_LIGHT,MOSS);
        mesh.disc(5,-207,-3,0,3,5,BONE_HEAD,HEART_LIGHT,MOSS);
    }

    /** One knotted striking limb and one tapered three-finger branch claw. */
    private static void briarArms(Mesh mesh) {
        mesh.branch(-28,-182,8,-45,-153,9,15,10,BONE_HAMMER_UPPER,BARK,BARK_EDGE);
        mesh.branch(-34,-180,10,-54,-201,13,10,2,BONE_HAMMER_UPPER,MOSS,BARK_EDGE);
        mesh.branch(28,-181,8,45,-151,8,12,8,BONE_CLAW_UPPER,BARK_EDGE,BARK);
        mesh.branch(32,-181,9,53,-194,17,8,1,BONE_CLAW_UPPER,BARK,BARK_EDGE);
        mesh.branch(-45,-149,8,-60,-119,7,12,13,BONE_HAMMER_FOREARM,BARK,BARK_EDGE);
        mesh.branch(-60,-119,7,-67,-103,-4,11,3,BONE_HAMMER_FOREARM,BARK_EDGE,BARK);
        mesh.branch(-60,-119,7,-63,-107,22,9,2,BONE_HAMMER_FOREARM,MOSS,BARK_EDGE);
        mesh.branch(44,-147,7,56,-124,7,9,6,BONE_CLAW_FOREARM,BARK_EDGE,BARK);
        for(int finger=0;finger<3;finger++) {
            int z=-9+finger*15;
            mesh.branch(55,-127,7,68,-105,z,4,1,BONE_CLAW_FOREARM,BARK,BARK_EDGE);
        }
    }

    /** Tapered woody legs end in low splayed roots, not humanoid shoes or construct boots. */
    private static void briarLegsAndRoots(Mesh mesh) {
        mesh.branch(-14,-119,9,-26,-73,10,12,9,BONE_LEFT_THIGH,BARK,BARK_EDGE);
        mesh.branch(14,-119,9,26,-73,10,11,9,BONE_RIGHT_THIGH,BARK_EDGE,BARK);
        mesh.branch(-25,-70,10,-33,-23,8,11,8,BONE_LEFT_SHIN,BARK,BARK_EDGE);
        mesh.branch(25,-70,10,33,-23,8,10,8,BONE_RIGHT_SHIN,BARK_EDGE,BARK);
        for(int side:new int[]{-1,1}) {
            int bone=side<0?BONE_LEFT_FOOT:BONE_RIGHT_FOOT;
            mesh.branch(side*33,-24,8,side*32,-10,6,8,7,bone,BARK,BARK_EDGE);
            mesh.branch(side*32,-10,6,side*53,-2,-25,6,2,bone,BARK_EDGE,BARK);
            mesh.branch(side*32,-10,6,side*17,-2,-21,6,2,bone,BARK,BARK_EDGE);
            mesh.branch(side*32,-10,6,side*45,-2,24,6,2,bone,MOSS,BARK_EDGE);
        }
    }

    private static void chassis(Mesh mesh) {
        // Torso: independently modelled rear shell, armoured furnace frame and recessed amber core.
        mesh.box(-51,-185,-11,51,-128,30,BONE_TORSO,STONE,IRON,5);
        mesh.box(-54,-183,-30,-22,-132,24,BONE_TORSO,STEEL,EDGE,4);
        mesh.box(22,-183,-30,54,-132,24,BONE_TORSO,STEEL,EDGE,4);
        mesh.box(-27,-188,-31,27,-177,23,BONE_TORSO,IRON,EDGE,3);
        mesh.box(-32,-145,-31,32,-124,23,BONE_TORSO,IRON,EDGE,3);
        mesh.disc(0,-161,-31,-26,18,10,BONE_TORSO,EMBER,COPPER);
        mesh.disc(0,-161,-32,-31,9,8,BONE_TORSO,FIRE,EMBER);
        for(int bar=-1;bar<=1;bar++)mesh.box(bar*10-2,-178,-35,bar*10+2,-144,-31,BONE_TORSO,IRON,EDGE,1);
        mesh.box(-30,-171,28,30,-148,35,BONE_TORSO,IRON,STEEL,4);
        for(int vent=-1;vent<=1;vent++)mesh.box(vent*14-3,-168,35,vent*14+3,-151,37,BONE_TORSO,DARK,IRON,1);
    }

    private static void hood(Mesh mesh) {
        // Hood shadows an original small iron head, rather than a reskinned skull.
        mesh.box(-18,-215,-8,18,-191,18,BONE_HEAD,DARK,IRON,4);
        mesh.box(-25,-222,-19,25,-212,27,BONE_HEAD,STEEL,EDGE,4);
        mesh.box(-25,-215,-20,-17,-188,23,BONE_HEAD,IRON,STEEL,2);
        mesh.box(17,-215,-20,25,-188,23,BONE_HEAD,IRON,STEEL,2);
        mesh.box(-15,-207,-13,15,-201,-9,BONE_HEAD,COPPER,IRON,1);
        mesh.box(-11,-206,-15,-3,-202,-13,BONE_HEAD,FIRE,EMBER,1);
        mesh.box(3,-206,-15,11,-202,-13,BONE_HEAD,FIRE,EMBER,1);
        mesh.box(-12,-197,-12,12,-190,6,BONE_HEAD,IRON,EDGE,2);
    }

    private static void arms(Mesh mesh) {
        // Shoulder and upper-arm blocks share native skeleton joints, with intentionally asymmetric mass.
        mesh.plate(new double[][] {
            {
                -70,-181
            }
            , {
                -62,-198
            }
            , {
                -36,-199
            }
            , {
                -24,-188
            }
            , {
                -26,-174
            }
            , {
                -61,-171
            }
        }
        ,-27,28,BONE_HAMMER_UPPER,STEEL,EDGE);
        mesh.plate(new double[][] {
            {
                24,-183
            }
            , {
                36,-194
            }
            , {
                57,-190
            }
            , {
                66,-177
            }
            , {
                60,-169
            }
            , {
                27,-172
            }
        }
        ,-23,25,BONE_CLAW_UPPER,STONE,EDGE);
        mesh.box(-49,-175,-8,-30,-148,21,BONE_HAMMER_UPPER,IRON,STEEL,3);
        mesh.box(31,-174,-8,50,-148,21,BONE_CLAW_UPPER,IRON,STEEL,3);
        mesh.disc(-45,-147,-15,-9,8,8,BONE_HAMMER_UPPER,IRON,COPPER);
        mesh.disc(45,-147,-15,-9,8,8,BONE_CLAW_UPPER,IRON,COPPER);
        // Right hammer gauntlet: solid head, copper binding and broad striking cap.
        mesh.box(-62,-151,-7,-43,-123,23,BONE_HAMMER_FOREARM,STEEL,IRON,3);
        mesh.box(-70,-147,-22,-41,-104,30,BONE_HAMMER_FOREARM,IRON,EDGE,4);
        mesh.box(-72,-143,-19,-66,-108,27,BONE_HAMMER_FOREARM,STEEL,EDGE,2);
        mesh.box(-62,-126,-20,-46,-119,-17,BONE_HAMMER_FOREARM,COPPER,IRON,1);
        // Opposite open gripping claw has three independently authored hooked fingers.
        mesh.box(43,-151,-7,61,-124,23,BONE_CLAW_FOREARM,STONE,STEEL,3);
        mesh.box(47,-129,-11,66,-116,22,BONE_CLAW_FOREARM,IRON,EDGE,2);
        for(int finger=0;finger<3;finger++) {
            int z=-9+finger*13;
            mesh.hook(z-3,z+4,BONE_CLAW_FOREARM,STEEL,EDGE);
        }
    }

    private static void lowerBody(Mesh mesh) {
        // Separated hip, thigh, shin and squat broad feet make a mechanical silhouette.
        mesh.box(-36,-137,-14,36,-111,26,BONE_HIPS,IRON,STEEL,4);
        mesh.box(-24,-126,-20,24,-115,-14,BONE_HIPS,COPPER,EDGE,2);
        mesh.box(-39,-115,-8,-5,-70,27,BONE_LEFT_THIGH,STONE,IRON,4);
        mesh.box(5,-115,-8,39,-70,27,BONE_RIGHT_THIGH,STONE,IRON,4);
        mesh.box(-44,-70,-11,-8,-22,28,BONE_LEFT_SHIN,IRON,STEEL,4);
        mesh.box(8,-70,-11,44,-22,28,BONE_RIGHT_SHIN,IRON,STEEL,4);
        mesh.box(-39,-69,-13,-17,-55,-7,BONE_LEFT_SHIN,STEEL,EDGE,3);
        mesh.box(17,-69,-13,39,-55,-7,BONE_RIGHT_SHIN,STEEL,EDGE,3);
        mesh.box(-50,-27,-30,-14,-1,24,BONE_LEFT_FOOT,STEEL,IRON,4);
        mesh.box(14,-27,-30,50,-1,24,BONE_RIGHT_FOOT,STEEL,IRON,4);
        mesh.box(-47,-9,-32,-19,0,-22,BONE_LEFT_FOOT,IRON,EDGE,2);
        mesh.box(19,-9,-32,47,0,-22,BONE_RIGHT_FOOT,IRON,EDGE,2);
    }

    private static void jointCapsAndVents(Mesh mesh) {
        mesh.disc(-26,-66,-17,-12,9,8,BONE_LEFT_SHIN,STEEL,COPPER);
        mesh.disc(26,-66,-17,-12,9,8,BONE_RIGHT_SHIN,STEEL,COPPER);
        for(int vent=0;vent<3;vent++) {
            int z=-8+vent*13;
            mesh.box(-55,-173,z,-52,-145,z+4,BONE_TORSO,IRON,EDGE,1);
            mesh.box(52,-173,z,55,-145,z+4,BONE_TORSO,IRON,EDGE,1);
        }
    }
    /** Reusable authored primitive builder with exact source bone mass/count preservation. */
    private static final class Mesh  {
        final Model source;
        final ArrayList<int[]> points=new ArrayList<>(),faces=new ArrayList<>();
        Mesh(Model source) {
            this.source=source;
        }
        int vertex(int x,int y,int z,int bone) {
            points.add(new int[] {
                x,y,z,bone
            }
            );
            return points.size()-1;
        }
        void face(int a,int b,int c,int color) {
            faces.add(new int[] {
                a,b,c,color
            }
            );
        }
        void quad(int a,int b,int c,int d,int color) {
            face(a,b,c,color);
            face(a,c,d,color);
        }
        /** Solid tapered six-facet branch along any 3D direction. Twisted end-ring facets
         * provide bark-like ridges without textures. One native bone owns the whole segment;
         * author multiple segments at animated joints rather than crossing bone boundaries.
         */
        void branch(double x0,double y0,double z0,double x1,double y1,double z1,
                    double radius0,double radius1,int bone,int color,int ridge) {
            double dx=x1-x0,dy=y1-y0,dz=z1-z0;
            double length=Math.sqrt(dx*dx+dy*dy+dz*dz);
            if(length<.01)throw new IllegalArgumentException("Branch endpoints coincide");
            dx/=length;dy/=length;dz/=length;
            // Build a stable perpendicular basis, including nearly vertical branches.
            double ux=Math.abs(dy)<.9?-dz:0;
            double uy=Math.abs(dy)<.9?0:dz;
            double uz=Math.abs(dy)<.9?dx:-dy;
            double magnitude=Math.sqrt(ux*ux+uy*uy+uz*uz);
            ux/=magnitude;uy/=magnitude;uz/=magnitude;
            double vx=dy*uz-dz*uy,vy=dz*ux-dx*uz,vz=dx*uy-dy*ux;
            int start=points.size(),sides=6;
            for(int end=0;end<2;end++)for(int side=0;side<sides;side++) {
                double angle=side*Math.PI*2/sides+end*.17;
                double radius=end==0?radius0:radius1;
                double a=Math.cos(angle)*radius,b=Math.sin(angle)*radius;
                vertex((int)Math.round((end==0?x0:x1)+a*ux+b*vx),
                       (int)Math.round((end==0?y0:y1)+a*uy+b*vy),
                       (int)Math.round((end==0?z0:z1)+a*uz+b*vz),bone);
            }
            for(int side=1;side<sides-1;side++) {
                face(start,start+side+1,start+side,color);
                face(start+sides,start+sides+side,start+sides+side+1,ridge);
            }
            for(int side=0;side<sides;side++) {
                int next=(side+1)%sides;
                quad(start+side,start+next,start+sides+next,start+sides+side,
                     side%3==0?ridge:color);
            }
        }

        /** Solid chamfered rectangular prism; all vertices attach to one native skin group. */
        void box(int x0,int y0,int z0,int x1,int y1,int z1,int bone,int color,int rim,int bevel) {
            int b=Math.min(bevel,Math.min((x1-x0)/3,(y1-y0)/3));
            int[][] p= {
                {
                    x0+b,y0
                }
                , {
                    x1-b,y0
                }
                , {
                    x1,y0+b
                }
                , {
                    x1,y1-b
                }
                , {
                    x1-b,y1
                }
                , {
                    x0+b,y1
                }
                , {
                    x0,y1-b
                }
                , {
                    x0,y0+b
                }
            }
            ;
            int start=points.size();
            for(int layer=0;layer<2;layer++)for(int[] q:p)vertex(q[0],q[1],layer==0?z0:z1,bone);
            for(int i=1;i<7;i++) {
                face(start,start+i+1,start+i,color);
                face(start+8,start+8+i,start+9+i,color);
            }
            for(int i=0;i<8;i++) {
                int next=(i+1)%8;
                quad(start+i,start+next,start+8+next,start+8+i,i%2==0?rim:color);
            }
        }
        /** Extrude a concave-capable XY silhouette through Z; ideal for angled shoulder armour. */
        void plate(double[][] polygon,int z0,int z1,int bone,int color,int edge) {
            int start=points.size(),count=polygon.length;
            for(int layer=0;layer<2;layer++)for(double[] p:polygon)vertex((int)p[0],(int)p[1],layer==0?z0:z1,bone);
            for(int[] t:CustomWeaponModels.triangulate(polygon)) {
                face(start+t[0],start+t[2],start+t[1],color);
                face(start+count+t[0],start+count+t[1],start+count+t[2],color);
            }
            for(int i=0;i<count;i++) {
                int next=(i+1)%count;
                quad(start+i,start+next,start+count+next,start+count+i,edge);
            }
        }
        void hook(int z0,int z1,int bone,int color,int edge) {
            int[][] p= {
                {
                    59,-122
                }
                , {
                    66,-120
                }
                , {
                    71,-105
                }
                , {
                    68,-99
                }
                , {
                    61,-103
                }
                , {
                    63,-110
                }
            }
            ;
            int start=points.size();
            for(int layer=0;layer<2;layer++)for(int[] v:p)vertex(v[0],v[1],layer==0?z0:z1,bone);
            List<int[]> triangles=CustomWeaponModels.triangulate(new double[][] {
                {
                    59,-122
                }
                , {
                    66,-120
                }
                , {
                    71,-105
                }
                , {
                    68,-99
                }
                , {
                    61,-103
                }
                , {
                    63,-110
                }
            }
            );
            for(int[] t:triangles) {
                face(start+t[0],start+t[2],start+t[1],color);
                face(start+6+t[0],start+6+t[1],start+6+t[2],color);
            }
            for(int i=0;i<6;i++) {
                int next=(i+1)%6;
                quad(start+i,start+next,start+6+next,start+6+i,edge);
            }
        }
        /** Closed faceted cylinder along Z, used for furnace cores and mechanical joint caps. */
        void disc(int x,int y,int z0,int z1,int radius,int sides,int bone,int color,int edge) {
            int start=points.size();
            for(int layer=0;layer<2;layer++)for(int side=0;side<sides;side++) {
                double angle=side*Math.PI*2/sides;
                vertex(x+(int)Math.round(Math.cos(angle)*radius),y+(int)Math.round(Math.sin(angle)*radius),layer==0?z0:z1,bone);
            }
            for(int side=1;side<sides-1;side++) {
                face(start,start+side+1,start+side,color);
                face(start+sides,start+sides+side,start+sides+side+1,color);
            }
            for(int side=0;side<sides;side++) {
                int next=(side+1)%sides;
                quad(start+side,start+next,start+sides+next,start+sides+side,edge);
            }
        }
        /** Replace all source geometry, balance invisible rig anchors, then discard stale cache arrays. */
        Model finish() {
            for(int[] point:points)point[0]=(int)Math.round(point[0]*VISIBLE_WIDTH_SCALE);
            TreeMap<Integer,long[]> rig=new TreeMap<>();
            for(int i=0;i<source.anInt1626;i++) {
                int bone=source.anIntArray1655[i];
                long[] group=rig.get(bone);
                if(group==null) {
                    group=new long[4];
                    rig.put(bone,group);
                }
                group[0]++;
                group[1]+=source.anIntArray1627[i];
                group[2]+=source.anIntArray1628[i];
                group[3]+=source.anIntArray1629[i];
            }
            HashMap<Integer,long[]> authored=new HashMap<>();
            for(int[] p:points) {
                long[] group=authored.get(p[3]);
                if(group==null) {
                    group=new long[4];
                    authored.put(p[3],group);
                }
                group[0]++;
                group[1]+=p[0];
                group[2]+=p[1];
                group[3]+=p[2];
            }
            int factor=PIVOT_MASS_FACTOR;
            for(Map.Entry<Integer,long[]> item:authored.entrySet()) {
                long[] original=rig.get(item.getKey());
                if(original==null)throw new IllegalStateException("Missing rig group "+item.getKey());
                factor=Math.max(factor,(int)((item.getValue()[0]+original[0]+1)/original[0]));
            }
            // Invisible skeleton anchors preserve every group's count and coordinate sum by one common factor.
            // Thus combined opcode0 pivots remain exact too; no original template triangles are retained.
            for(Map.Entry<Integer,long[]> item:rig.entrySet()) {
                int bone=item.getKey();
                long[] original=item.getValue(),drawn=authored.get(bone);
                if(drawn==null)drawn=new long[4];
                int count=(int)(original[0]*factor-drawn[0]);
                long[] remaining= {
                    original[1]*factor-drawn[1],original[2]*factor-drawn[2],original[3]*factor-drawn[3]
                }
                ;
                int[] floor=new int[3],remainder=new int[3];
                for(int axis=0;axis<3;axis++) {
                    floor[axis]=(int)(remaining[axis]/count);
                    remainder[axis]=(int)(remaining[axis]-(long)floor[axis]*count);
                }
                for(int i=0;i<count;i++) {
                    int[] p=new int[3];
                    for(int axis=0;axis<3;axis++)p[axis]=floor[axis]+(i<Math.abs(remainder[axis])?Integer.signum(remainder[axis]):0);
                    vertex(p[0],p[1],p[2],bone);
                }
            }
            // Model projects through fixed 8,000-vertex scratch arrays. Reject instead of disappearing.
            if(points.size()>=8000)throw new IllegalStateException("Authored NPC exceeds native vertex budget");
            Model result=new Model(false,false,false,source);
            result.anInt1626=points.size();
            result.anIntArray1627=new int[result.anInt1626];
            result.anIntArray1628=new int[result.anInt1626];
            result.anIntArray1629=new int[result.anInt1626];
            result.anIntArray1655=new int[result.anInt1626];
            for(int i=0;i<points.size();i++) {
                int[] p=points.get(i);
                result.anIntArray1627[i]=p[0];
                result.anIntArray1628[i]=p[1];
                result.anIntArray1629[i]=p[2];
                result.anIntArray1655[i]=p[3];
            }
            ArrayList<int[]> valid=new ArrayList<>();
            for(int[] f:faces) {
                int[] a=points.get(f[0]),b=points.get(f[1]),c=points.get(f[2]);
                long ux=b[0]-a[0],uy=b[1]-a[1],uz=b[2]-a[2],vx=c[0]-a[0],vy=c[1]-a[1],vz=c[2]-a[2];
                if(uy*vz-uz*vy!=0||uz*vx-ux*vz!=0||ux*vy-uy*vx!=0)valid.add(f);
            }
            result.anInt1630=valid.size();
            result.anIntArray1631=new int[valid.size()];
            result.anIntArray1632=new int[valid.size()];
            result.anIntArray1633=new int[valid.size()];
            result.anIntArray1640=new int[valid.size()];
            result.anIntArray1637=new int[valid.size()];
            Arrays.fill(result.anIntArray1637,1);
            for(int i=0;i<valid.size();i++) {
                int[] f=valid.get(i);
                result.anIntArray1631[i]=f[0];
                result.anIntArray1632[i]=f[1];
                result.anIntArray1633[i]=f[2];
                result.anIntArray1640[i]=f[3];
            }
            // No source texture, alpha, normals or face/vertex grouping arrays may leak into the new mesh.
            result.anInt1641=0;
            result.anInt1642=0;
            result.anIntArray1634=result.anIntArray1635=result.anIntArray1636=result.anIntArray1638=result.anIntArray1639=result.anIntArray1643=result.anIntArray1644=result.anIntArray1645=result.anIntArray1656=null;
            result.anIntArrayArray1657=result.anIntArrayArray1658=null;
            result.aClass33Array1660=null;
            return result;
        }
    }
    private CustomNpcModels() {
    }
}
