package com.koios.client.arpg;
import java.util.*;import com.koios.client.world.Model;
/** Rig-preserving armour foundation with authored plates, leather reinforcement and robe details. */
public final class CustomArmourModels {
 private static int hsl(int h,int s,int l){return h<<10|s<<7|l;}
 private static final int IRON=hsl(0,0,26),EDGE=hsl(0,0,49),BRASS=hsl(8,2,32),LEATHER=hsl(8,2,24),HIDE=hsl(16,2,20),VIOLET=hsl(46,2,25),SILVER=hsl(0,0,57),CYAN=hsl(32,4,40);
 public static Model create(Model source,int id,boolean worn){int originalId=id;id=CustomEquipment.visualId(id);if(source==null||id<22424||id>22426)return source;if(!worn){com.koios.client.definition.ItemDefinition definition=com.koios.client.definition.ItemDefinition.forID(originalId);if(!definition.method195(0))return null;Model fitted=definition.method196(0);if(fitted==null)return null;Model ground=new Model(false,false,false,fitted);int minY=9999,maxY=-9999,minZ=9999,maxZ=-9999;for(int i=0;i<ground.anInt1626;i++){minY=Math.min(minY,ground.anIntArray1628[i]);maxY=Math.max(maxY,ground.anIntArray1628[i]);minZ=Math.min(minZ,ground.anIntArray1629[i]);maxZ=Math.max(maxZ,ground.anIntArray1629[i]);}double cy=(minY+maxY)/2d,cz=(minZ+maxZ)/2d;for(int i=0;i<ground.anInt1626;i++){int y=ground.anIntArray1628[i],z=ground.anIntArray1629[i];ground.anIntArray1627[i]=(int)Math.round(ground.anIntArray1627[i]*.6);ground.anIntArray1628[i]=(int)Math.round((z-cz)*.6-12);ground.anIntArray1629[i]=(int)Math.round(-(y-cy)*.6);}ground.anIntArray1655=null;ground.anIntArrayArray1657=null;return ground;}if(originalId==22438||originalId==22439){source=new Model(false,false,false,source);for(int face=0;face<source.anInt1630;face++){int color=source.anIntArray1640[face];boolean skin=false;for(int skinColor:new int[]{4550,4537,5681,5673,5790,6806,8076,4574})if(color==skinColor)skin=true;if(skin||color<2||color>=65534||source.anIntArray1637!=null&&(source.anIntArray1637[face]&2)!=0)continue;int light=color&127;source.anIntArray1640[face]=originalId==22438?hsl(37,1,Math.max(20,Math.min(49,light))):hsl(43,2,Math.max(18,Math.min(38,light)));}}Details details=new Details(source,worn);
  if(originalId==22438){
   // Split fitted breastplate, low gorget and articulated waist; no floating shoulder slabs.
   int steel=hsl(37,1,40),rim=hsl(0,0,57),slate=hsl(37,1,30);
   for(int side:new int[]{-1,1}){details.piece(mirror(new double[][]{{.035,.22},{.59,.19},{.57,.52},{.10,.68},{.035,.62}},side),1.2,8,steel,rim);details.piece(mirror(new double[][]{{.08,.70},{.51,.58},{.48,.85},{.10,.94}},side),.8,8,slate,steel);details.piece(mirror(new double[][]{{.60,.19},{.84,.26},{.79,.40},{.61,.34}},side),.7,8,steel,rim);}
   details.piece(new double[][]{{-.39,.13},{0,.20},{.39,.13},{.31,.21},{0,.28},{-.31,.21}},.8,8,slate,rim);
   details.band(-.54,.54,.86,.90,.8,8,LEATHER);details.stone(0,.88,.07,.035,1.3,8,BRASS);
  }else if(originalId==22439){
   int indigo=hsl(43,2,29),teal=hsl(35,2,31),seam=hsl(0,0,49);
   details.piece(new double[][]{{-.18,.27},{.18,.27},{.23,.73},{0,.80},{-.23,.73}},.6,8,teal,teal);
   for(int side:new int[]{-1,1}){details.piece(mirror(new double[][]{{.12,.25},{.33,.20},{.47,.30},{.23,.54},{.17,.51}},side),.8,8,indigo,seam);details.piece(mirror(new double[][]{{.26,.56},{.49,.47},{.47,.72},{.28,.77}},side),.6,8,indigo,seam);}
   details.band(-.64,.64,.79,.86,.9,8,LEATHER);details.stone(0,.82,.09,.035,1.5,8,SILVER);
  }else if(id==22424){
   // Shallow fitted plates; the cache body supplies the shoulder and sleeve silhouette.
   details.piece(new double[][]{{-.64,.19},{0,.25},{.64,.19},{.57,.56},{0,.67},{-.57,.56}},1.0,8,hsl(0,0,37),hsl(0,0,49));
   details.piece(new double[][]{{-.47,.67},{0,.72},{.47,.67},{.40,.84},{-.40,.84}},.8,8,IRON,hsl(0,0,41));
   details.stone(0,.35,.075,.052,1.8,8,CYAN);
   details.band(-.60,.60,.89,.925,.9,8,hsl(8,1,30));
  }else if(id==22425){
   // Follow the fitted leather rather than covering it with thick floating panels.
   details.piece(new double[][]{{-.58,.16},{-.45,.13},{.44,.77},{.28,.82}},.9,8,LEATHER,HIDE);
   details.band(-.77,.77,.83,.90,1,8,LEATHER);
   details.stone(.08,.86,.095,.04,1.6,8,BRASS);
   for(int stitch=0;stitch<4;stitch++){double y=.37+stitch*.085;details.band(-.055,.055,y,y+.016,1,8,hsl(8,2,35));}
  }else{
   // Keep the robe front closed, with inset trim and a restrained clasp.
   details.piece(new double[][]{{-.82,.65},{.82,.65},{.82,1.10},{.58,1.23},{-.58,1.23},{-.82,1.10}},.7,8,VIOLET,VIOLET);
   // Avoid a long silver-edged front strip: its thin side faces read as a diagonal seam.
   details.band(-.69,.69,.78,.81,.9,8,hsl(0,0,41));
   details.stone(0,.79,.095,.048,1.5,8,CYAN);
  }
  return details.finish();
 }
 private static double[][] mirror(double[][] input,int side){double[][] result=new double[input.length][2];for(int i=0;i<input.length;i++){result[i][0]=input[i][0]*side;result[i][1]=input[i][1];}return result;}
 private static final class Details {
  final Model source;final boolean worn;final List<double[]> vertices=new ArrayList<>();final List<Integer> bones=new ArrayList<>();final List<int[]> faces=new ArrayList<>();final Set<Integer> existing=new HashSet<>();double centerX,topY,height,halfWidth,frontZ,groundY,centerZ;
  Details(Model source,boolean worn){this.source=source;this.worn=worn;int minX=9999,maxX=-9999,minY=9999,maxY=-9999,minZ=9999,maxZ=-9999;for(int i=0;i<source.anInt1626;i++){int bone=source.anIntArray1655==null?0:source.anIntArray1655[i];existing.add(bone);if(worn&&bone!=8)continue;int x=source.anIntArray1627[i],y=source.anIntArray1628[i],z=source.anIntArray1629[i];minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);minZ=Math.min(minZ,z);maxZ=Math.max(maxZ,z);}if(maxX<minX)throw new IllegalStateException("Armour template has no torso attachment group");centerX=(minX+maxX)/2d;topY=minY;height=maxY-minY;halfWidth=(maxX-minX)/2d;frontZ=minZ-1;groundY=minY-1;centerZ=(minZ+maxZ)/2d;if(!worn){halfWidth*=.66;height=(maxZ-minZ)*.76;}}
  int shoulder(int side){int arm=side<0?21:25;return !worn||existing.contains(arm)?arm:side<0?60:59;}
  int cuff(int side){int arm=side<0?19:22;return !worn||existing.contains(arm)?arm:8;}
  double surface(double x,double y,int bone){int group=existing.contains(bone)?bone:8;
   // Interpolate the actual front triangles at this point. A nearest-vertex minimum
   // can pick an unrelated fold and push an entire plate away from the chest.
   double projected=Double.MAX_VALUE;
   if(source.anIntArray1655!=null)for(int face=0;face<source.anInt1630;face++){
    int a=source.anIntArray1631[face],b=source.anIntArray1632[face],c=source.anIntArray1633[face];
    if(source.anIntArray1655[a]!=group||source.anIntArray1655[b]!=group||source.anIntArray1655[c]!=group)continue;
    double ax=source.anIntArray1627[a],ay=source.anIntArray1628[a],bx=source.anIntArray1627[b],by=source.anIntArray1628[b],cx=source.anIntArray1627[c],cy=source.anIntArray1628[c];
    double determinant=(by-cy)*(ax-cx)+(cx-bx)*(ay-cy);if(Math.abs(determinant)<.0001)continue;
    double u=((by-cy)*(x-cx)+(cx-bx)*(y-cy))/determinant,v=((cy-ay)*(x-cx)+(ax-cx)*(y-cy))/determinant,w=1-u-v;
    if(u>=-.001&&v>=-.001&&w>=-.001)projected=Math.min(projected,u*source.anIntArray1629[a]+v*source.anIntArray1629[b]+w*source.anIntArray1629[c]);
   }
   if(projected<Double.MAX_VALUE)return projected-.6;
   double nearest=Double.MAX_VALUE;for(int i=0;i<source.anInt1626;i++)if(source.anIntArray1655!=null&&source.anIntArray1655[i]==group){double dx=x-source.anIntArray1627[i],dy=y-source.anIntArray1628[i];nearest=Math.min(nearest,dx*dx+dy*dy);}double z=Double.MAX_VALUE;for(int i=0;i<source.anInt1626;i++)if(source.anIntArray1655!=null&&source.anIntArray1655[i]==group){double dx=x-source.anIntArray1627[i],dy=y-source.anIntArray1628[i];if(dx*dx+dy*dy<=nearest+36)z=Math.min(z,source.anIntArray1629[i]);}return z==Double.MAX_VALUE?frontZ:z-1;}
  int vertex(double x,double y,double depth,int bone){
   // Project at the final integer XY used by the legacy renderer. Projecting
   // first and then rounding XY moved some vertices across sloped chest folds,
   // leaving them on or behind the actual torso (16 measured cache fixtures).
   // surface() includes 0.6 units of stand-off: nearest-integer Z rounding then
   // still leaves positive clearance. Keep it shallow to avoid floating plates.
   double fittedX=Math.round(centerX+x*halfWidth),fittedY=Math.round(topY+y*height);
   vertices.add(worn?new double[]{fittedX,fittedY,surface(fittedX,fittedY,bone)-depth}:new double[]{centerX+x*halfWidth,groundY-depth,centerZ+(y-.5)*height});
   bones.add(worn?bone:0);return source.anInt1626+vertices.size()-1;
  }
  void face(int a,int b,int c,int color){faces.add(new int[]{a,worn?c:b,worn?b:c,color});}
  void piece(double[][] polygon,double thickness,int bone,int color,int edge){double area=0;for(int i=0;i<polygon.length;i++){double[] a=polygon[i],b=polygon[(i+1)%polygon.length];area+=a[0]*b[1]-b[0]*a[1];}double[][] points=polygon;if(area<0){points=new double[polygon.length][];for(int i=0;i<points.length;i++)points[i]=polygon[points.length-1-i];}List<int[]> triangles=CustomWeaponModels.triangulate(points);int count=points.length,start=source.anInt1626+vertices.size();for(int layer=0;layer<2;layer++)for(double[] point:points)vertex(point[0],point[1],layer==0?0:thickness,bone);for(int[] t:triangles){face(start+t[2],start+t[1],start+t[0],color);face(start+count+t[0],start+count+t[1],start+count+t[2],color);}for(int i=0;i<count;i++){int next=(i+1)%count;face(start+i,start+next,start+count+next,edge);face(start+i,start+count+next,start+count+i,edge);}}
  void band(double x1,double x2,double y1,double y2,double depth,int bone,int color){piece(new double[][]{{x1,y1},{x2,y1},{x2,y2},{x1,y2}},depth,bone,color,color);}
  void stone(double x,double y,double width,double h,double depth,int bone,int color){piece(new double[][]{{x,y-h},{x+width,y},{x,y+h},{x-width,y}},depth,bone,color,SILVER);}
  Model finish(){Model model=new Model(false,false,false,source);int originalVertices=source.anInt1626;model.anInt1626=originalVertices+vertices.size();model.anIntArray1627=Arrays.copyOf(source.anIntArray1627,model.anInt1626);model.anIntArray1628=Arrays.copyOf(source.anIntArray1628,model.anInt1626);model.anIntArray1629=Arrays.copyOf(source.anIntArray1629,model.anInt1626);model.anIntArray1655=source.anIntArray1655==null?new int[model.anInt1626]:Arrays.copyOf(source.anIntArray1655,model.anInt1626);for(int i=0;i<vertices.size();i++){double[] p=vertices.get(i);int target=originalVertices+i;model.anIntArray1627[target]=(int)Math.round(p[0]);model.anIntArray1628[target]=(int)Math.round(p[1]);model.anIntArray1629[target]=(int)Math.round(p[2]);model.anIntArray1655[target]=bones.get(i);}
   List<int[]> valid=new ArrayList<>();for(int[] f:faces){long ax=model.anIntArray1627[f[1]]-model.anIntArray1627[f[0]],ay=model.anIntArray1628[f[1]]-model.anIntArray1628[f[0]],az=model.anIntArray1629[f[1]]-model.anIntArray1629[f[0]],bx=model.anIntArray1627[f[2]]-model.anIntArray1627[f[0]],by=model.anIntArray1628[f[2]]-model.anIntArray1628[f[0]],bz=model.anIntArray1629[f[2]]-model.anIntArray1629[f[0]];if(ay*bz-az*by!=0||az*bx-ax*bz!=0||ax*by-ay*bx!=0)valid.add(f);}int originalFaces=source.anInt1630;model.anInt1630=originalFaces+valid.size();model.anIntArray1631=Arrays.copyOf(source.anIntArray1631,model.anInt1630);model.anIntArray1632=Arrays.copyOf(source.anIntArray1632,model.anInt1630);model.anIntArray1633=Arrays.copyOf(source.anIntArray1633,model.anInt1630);model.anIntArray1640=Arrays.copyOf(source.anIntArray1640,model.anInt1630);model.anIntArray1637=source.anIntArray1637==null?new int[model.anInt1630]:Arrays.copyOf(source.anIntArray1637,model.anInt1630);if(source.anIntArray1638!=null)model.anIntArray1638=Arrays.copyOf(source.anIntArray1638,model.anInt1630);if(source.anIntArray1639!=null)model.anIntArray1639=Arrays.copyOf(source.anIntArray1639,model.anInt1630);if(source.anIntArray1656!=null)model.anIntArray1656=Arrays.copyOf(source.anIntArray1656,model.anInt1630);for(int i=0;i<valid.size();i++){int target=originalFaces+i;int[] f=valid.get(i);model.anIntArray1631[target]=f[0];model.anIntArray1632[target]=f[1];model.anIntArray1633[target]=f[2];model.anIntArray1640[target]=f[3];model.anIntArray1637[target]=0;}model.anIntArray1634=model.anIntArray1635=model.anIntArray1636=null;model.anIntArrayArray1657=model.anIntArrayArray1658=null;model.aClass33Array1660=null;return model;
  }
 }
}



