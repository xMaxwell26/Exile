from pathlib import Path
p=Path('Notorious/Client/src/com/koios/client/arpg/CustomArmourModels.java');p.write_text('''package com.koios.client.arpg;
import java.util.*;import com.koios.client.world.Model;
/** Rig-preserving armour foundation with authored plates, leather reinforcement and robe details. */
public final class CustomArmourModels {
 private static int hsl(int h,int s,int l){return h<<10|s<<7|l;}
 private static final int IRON=hsl(35,1,25),EDGE=hsl(33,1,61),BRASS=hsl(8,3,38),LEATHER=hsl(13,2,28),HIDE=hsl(19,2,24),VIOLET=hsl(46,3,27),SILVER=hsl(34,1,64),CYAN=hsl(32,5,48);
 public static Model create(Model source,int id,boolean worn){if(source==null||id<22424||id>22426)return source;Details details=new Details(source,worn);
  if(id==22424){
   details.piece(new double[][]{{-.77,.12},{0,.25},{.77,.12},{.68,.43},{0,.53},{-.68,.43}},4,8,IRON,EDGE);
   details.piece(new double[][]{{-.66,.45},{0,.54},{.66,.45},{.58,.65},{0,.72},{-.58,.65}},5,8,IRON,BRASS);
   details.piece(new double[][]{{-.57,.66},{0,.74},{.57,.66},{.47,.85},{-.47,.85}},6,8,IRON,EDGE);
   details.piece(new double[][]{{-.5,-.04},{-.32,.02},{-.25,.16},{-.45,.13}},4,2,BRASS,EDGE);details.piece(new double[][]{{.5,-.04},{.32,.02},{.25,.16},{.45,.13}},4,2,BRASS,EDGE);
   for(int side:new int[]{-1,1})details.piece(mirror(new double[][]{{.78,-.01},{1.33,-.07},{1.72,.13},{1.47,.31},{.87,.23}},side),5,details.shoulder(side),IRON,BRASS);
   details.stone(0,.31,.14,.10,7,8,CYAN);details.band(-.63,.63,.89,.94,3,8,BRASS);
  }else if(id==22425){
   details.piece(new double[][]{{-.84,.12},{-.12,.25},{-.18,.72},{-.65,.81},{-.9,.48}},3,8,LEATHER,HIDE);
   details.piece(new double[][]{{.13,.24},{.72,.10},{.88,.49},{.64,.80},{.18,.73}},3,8,HIDE,LEATHER);
   details.piece(new double[][]{{-.08,.24},{.08,.24},{.13,.79},{-.12,.79}},4,8,LEATHER,BRASS);
   for(int side:new int[]{-1,1})details.piece(mirror(new double[][]{{.82,.01},{1.15,-.02},{1.38,.12},{1.24,.24},{.88,.18}},side),3,details.shoulder(side),HIDE,LEATHER);
   details.band(-.91,.91,.81,.91,4,8,LEATHER);details.stone(.12,.86,.17,.055,6,8,BRASS);
   for(int stitch=0;stitch<6;stitch++){double y=.30+stitch*.068;details.band(-.13,.13,y,y+.021,5,8,BRASS);}
  }else{
   for(int side:new int[]{-1,1})details.piece(mirror(new double[][]{{.19,.01},{.51,-.03},{1.12,.16},{.76,.31},{.35,.17}},side),4,8,VIOLET,SILVER);
   details.piece(new double[][]{{-.14,.22},{.14,.22},{.18,.89},{0,1.11},{-.18,.89}},3,8,VIOLET,SILVER);
   details.band(-.75,.75,.77,.82,4,8,SILVER);details.stone(0,.79,.15,.09,7,8,CYAN);
   for(int side:new int[]{-1,1}){for(int rune=0;rune<3;rune++)details.stone(side*(.39+rune*.08),.31+rune*.14,.075,.043,4,8,CYAN);details.piece(mirror(new double[][]{{1.18,.85},{1.79,.83},{1.95,1.12},{1.31,1.13}},side),3,details.cuff(side),VIOLET,SILVER);details.piece(mirror(new double[][]{{.55,.82},{.78,.85},{1.03,1.25},{.37,1.22}},side),2.5,8,VIOLET,SILVER);}
  }
  return details.finish();
 }
 private static double[][] mirror(double[][] input,int side){double[][] result=new double[input.length][2];for(int i=0;i<input.length;i++){result[i][0]=input[i][0]*side;result[i][1]=input[i][1];}return result;}
 private static final class Details {
  final Model source;final boolean worn;final List<double[]> vertices=new ArrayList<>();final List<Integer> bones=new ArrayList<>();final List<int[]> faces=new ArrayList<>();final Set<Integer> existing=new HashSet<>();double centerX,topY,height,halfWidth,frontZ,groundY,centerZ;
  Details(Model source,boolean worn){this.source=source;this.worn=worn;int minX=9999,maxX=-9999,minY=9999,maxY=-9999,minZ=9999,maxZ=-9999;for(int i=0;i<source.anInt1626;i++){int bone=source.anIntArray1655==null?0:source.anIntArray1655[i];existing.add(bone);if(worn&&bone!=8)continue;int x=source.anIntArray1627[i],y=source.anIntArray1628[i],z=source.anIntArray1629[i];minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);minZ=Math.min(minZ,z);maxZ=Math.max(maxZ,z);}if(maxX<minX)throw new IllegalStateException("Armour template has no torso attachment group");centerX=(minX+maxX)/2d;topY=minY;height=maxY-minY;halfWidth=(maxX-minX)/2d;frontZ=minZ-1;groundY=minY-1;centerZ=(minZ+maxZ)/2d;if(!worn){halfWidth*=.66;height=(maxZ-minZ)*.76;}}
  int shoulder(int side){int arm=side<0?21:25;return !worn||existing.contains(arm)?arm:side<0?60:59;}
  int cuff(int side){int arm=side<0?19:22;return !worn||existing.contains(arm)?arm:8;}
  int vertex(double x,double y,double depth,int bone){vertices.add(worn?new double[]{centerX+x*halfWidth,topY+y*height,frontZ-depth}:new double[]{centerX+x*halfWidth,groundY-depth,centerZ+(y-.5)*height});bones.add(worn?bone:0);return source.anInt1626+vertices.size()-1;}
  void face(int a,int b,int c,int color){faces.add(new int[]{a,worn?c:b,worn?b:c,color});}
  void piece(double[][] polygon,double thickness,int bone,int color,int edge){double area=0;for(int i=0;i<polygon.length;i++){double[] a=polygon[i],b=polygon[(i+1)%polygon.length];area+=a[0]*b[1]-b[0]*a[1];}double[][] points=polygon;if(area<0){points=new double[polygon.length][];for(int i=0;i<points.length;i++)points[i]=polygon[points.length-1-i];}List<int[]> triangles=CustomWeaponModels.triangulate(points);int count=points.length,start=source.anInt1626+vertices.size();for(int layer=0;layer<2;layer++)for(double[] point:points)vertex(point[0],point[1],layer==0?0:thickness,bone);for(int[] t:triangles){face(start+t[2],start+t[1],start+t[0],color);face(start+count+t[0],start+count+t[1],start+count+t[2],color);}for(int i=0;i<count;i++){int next=(i+1)%count;face(start+i,start+next,start+count+next,edge);face(start+i,start+count+next,start+count+i,edge);}}
  void band(double x1,double x2,double y1,double y2,double depth,int bone,int color){piece(new double[][]{{x1,y1},{x2,y1},{x2,y2},{x1,y2}},depth,bone,color,color);}
  void stone(double x,double y,double width,double h,double depth,int bone,int color){piece(new double[][]{{x,y-h},{x+width,y},{x,y+h},{x-width,y}},depth,bone,color,SILVER);}
  Model finish(){Model model=new Model(false,false,false,source);int originalVertices=source.anInt1626;model.anInt1626=originalVertices+vertices.size();model.anIntArray1627=Arrays.copyOf(source.anIntArray1627,model.anInt1626);model.anIntArray1628=Arrays.copyOf(source.anIntArray1628,model.anInt1626);model.anIntArray1629=Arrays.copyOf(source.anIntArray1629,model.anInt1626);model.anIntArray1655=source.anIntArray1655==null?new int[model.anInt1626]:Arrays.copyOf(source.anIntArray1655,model.anInt1626);for(int i=0;i<vertices.size();i++){double[] p=vertices.get(i);int target=originalVertices+i;model.anIntArray1627[target]=(int)Math.round(p[0]);model.anIntArray1628[target]=(int)Math.round(p[1]);model.anIntArray1629[target]=(int)Math.round(p[2]);model.anIntArray1655[target]=bones.get(i);}
   List<int[]> valid=new ArrayList<>();for(int[] f:faces){long ax=model.anIntArray1627[f[1]]-model.anIntArray1627[f[0]],ay=model.anIntArray1628[f[1]]-model.anIntArray1628[f[0]],az=model.anIntArray1629[f[1]]-model.anIntArray1629[f[0]],bx=model.anIntArray1627[f[2]]-model.anIntArray1627[f[0]],by=model.anIntArray1628[f[2]]-model.anIntArray1628[f[0]],bz=model.anIntArray1629[f[2]]-model.anIntArray1629[f[0]];if(ay*bz-az*by!=0||az*bx-ax*bz!=0||ax*by-ay*bx!=0)valid.add(f);}int originalFaces=source.anInt1630;model.anInt1630=originalFaces+valid.size();model.anIntArray1631=Arrays.copyOf(source.anIntArray1631,model.anInt1630);model.anIntArray1632=Arrays.copyOf(source.anIntArray1632,model.anInt1630);model.anIntArray1633=Arrays.copyOf(source.anIntArray1633,model.anInt1630);model.anIntArray1640=Arrays.copyOf(source.anIntArray1640,model.anInt1630);model.anIntArray1637=source.anIntArray1637==null?new int[model.anInt1630]:Arrays.copyOf(source.anIntArray1637,model.anInt1630);if(source.anIntArray1638!=null)model.anIntArray1638=Arrays.copyOf(source.anIntArray1638,model.anInt1630);if(source.anIntArray1639!=null)model.anIntArray1639=Arrays.copyOf(source.anIntArray1639,model.anInt1630);if(source.anIntArray1656!=null)model.anIntArray1656=Arrays.copyOf(source.anIntArray1656,model.anInt1630);for(int i=0;i<valid.size();i++){int target=originalFaces+i;int[] f=valid.get(i);model.anIntArray1631[target]=f[0];model.anIntArray1632[target]=f[1];model.anIntArray1633[target]=f[2];model.anIntArray1640[target]=f[3];model.anIntArray1637[target]=1;}model.anIntArray1634=model.anIntArray1635=model.anIntArray1636=null;model.anIntArrayArray1657=model.anIntArrayArray1658=null;model.aClass33Array1660=null;return model;
  }
 }
}
''')
p=Path('Notorious/Client/src/com/koios/client/arpg/CustomWeaponModels.java');s=p.read_text().replace('private static List<int[]> triangulate(', 'static List<int[]> triangulate(');p.write_text(s)
p=Path('Notorious/Client/src/com/koios/client/definition/ItemDefinition.java');s=p.read_text();
for signature,end,worn in [('public Model method196','private void setDefaults',True),('public Model method201','public Model method202',False),('public Model method202','public ItemDefinition()',False)]:
 a=s.index(signature);b=s.index(end,a);section=s[a:b];needle='        com.koios.client.arpg.CustomEquipment.style(model,id);';section=section.replace(needle,needle+'\n        model=com.koios.client.arpg.CustomArmourModels.create(model,id,'+str(worn).lower()+');');s=s[:a]+section+s[b:]
p.write_text(s)
