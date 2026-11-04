import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.CustomNpcModels;
import com.koios.client.definition.NPCDefinition;
import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;
import com.koios.client.world.Model;
import com.koios.client.graphic.Rasterizer;
/** Original visible topology and exact skeleton pivot mass, independently of runtime animation checks. */
public final class CustomBriarMeshCheck  {
    static int checks;
    static void check(boolean v,String m) {
        checks++;
        if(!v)throw new AssertionError(m);
    }
    static Map<Integer,long[]> groups(Model model) {
        Map<Integer,long[]> groups=new TreeMap<>();
        for(int i=0;i<model.anInt1626;i++) {
            int bone=model.anIntArray1655[i];
            long[] g=groups.get(bone);
            if(g==null) {
                g=new long[4];
                groups.put(bone,g);
            }
            g[0]++;
            g[1]+=model.anIntArray1627[i];
            g[2]+=model.anIntArray1628[i];
            g[3]+=model.anIntArray1629[i];
        }
        return groups;
    }

    public static void main(String[]args)throws Exception {
        try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");RandomAccessFile index=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r")) {
            NPCDefinition.unpackConfig(new StreamLoader(new Decompressor(data,config,1).decompress(2)));
            Model.method459(0,null);
            Rasterizer.method372(.85);
            NPCDefinition base=NPCDefinition.forID(90);
            Decompressor cache=new Decompressor(data,index,2);
            Set<Integer> loaded=new HashSet<>();
            Model[] pieces=new Model[base.npcModels.length];
            for(int i=0;i<pieces.length;i++) {
                SceneryVisualCheck.load(cache,base.npcModels[i],loaded);
                pieces[i]=Model.method462(base.npcModels[i]);
            }
            Model source=pieces.length==1?pieces[0]:new Model(pieces.length,pieces);
            int[] originalX=source.anIntArray1627.clone(),originalColors=source.anIntArray1640.clone();
            Model mesh=CustomNpcModels.create(source,15007);
            check(mesh.anInt1630!=source.anInt1630&&!Arrays.equals(mesh.anIntArray1631,source.anIntArray1631),"Visible topology is entirely authored");
            check(Arrays.equals(source.anIntArray1627,originalX)&&Arrays.equals(source.anIntArray1640,originalColors),"Cached template remains untouched");
            Map<Integer,long[]> before=groups(source),after=groups(mesh);
            long factor=mesh.anInt1626/source.anInt1626;
            for(int bone:before.keySet())for(int axis=0;axis<4;axis++)check(after.get(bone)[axis]==before.get(bone)[axis]*factor,"Every bone count/coordinate sum preserves native combined pivot "+bone+"/"+axis);
            Set<Integer> visible=new HashSet<>();
            for(int f=0;f<mesh.anInt1630;f++) {
                int a=mesh.anIntArray1631[f],b=mesh.anIntArray1632[f],c=mesh.anIntArray1633[f];
                visible.add(a);
                visible.add(b);
                visible.add(c);
                long ux=mesh.anIntArray1627[b]-mesh.anIntArray1627[a],uy=mesh.anIntArray1628[b]-mesh.anIntArray1628[a],uz=mesh.anIntArray1629[b]-mesh.anIntArray1629[a],vx=mesh.anIntArray1627[c]-mesh.anIntArray1627[a],vy=mesh.anIntArray1628[c]-mesh.anIntArray1628[a],vz=mesh.anIntArray1629[c]-mesh.anIntArray1629[a];
                check(uy*vz-uz*vy!=0||uz*vx-ux*vz!=0||ux*vy-uy*vx!=0,"Every authored face nondegenerate");
            }
            int minX=9999,maxX=-9999,minY=9999,maxY=-9999,minZ=9999,maxZ=-9999;
            for(int v:visible) {
                minX=Math.min(minX,mesh.anIntArray1627[v]);
                maxX=Math.max(maxX,mesh.anIntArray1627[v]);
                minY=Math.min(minY,mesh.anIntArray1628[v]);
                maxY=Math.max(maxY,mesh.anIntArray1628[v]);
                minZ=Math.min(minZ,mesh.anIntArray1629[v]);
                maxZ=Math.max(maxZ,mesh.anIntArray1629[v]);
            }
            System.out.println("Original Briar Warden: "+visible.size()+" visiblevertices / "+mesh.anInt1630+" authoredfaces / "+mesh.anInt1626+" totalpivotvertices; bounds "+minX+".."+maxX+", "+minY+".."+maxY+", "+minZ+".."+maxZ);
            check(maxY==0,"Visible feet reach fixed ground0");
            mesh.method469();
            mesh.method479(84,1000,-40,-70,-40,true);
            BufferedImage image=new BufferedImage(1200,380,BufferedImage.TYPE_INT_RGB);
            Graphics2D g=image.createGraphics();
            g.setColor(new Color(0x17232C));
            g.fillRect(0,0,1200,380);
            g.setFont(new Font("SansSerif",0,13));
            for(int angle=0;angle<4;angle++) {
                g.drawImage(CustomNpcRigCheck.render(mesh,angle*512,false),angle*300,20,null);
                g.setColor(new Color(0xD4BB87));
                g.drawString("Original Briar Warden / "+angle*90+"deg",angle*300+5,16);
            }
            g.dispose();
            ImageIO.write(image,"png",new File("build/custom-briar-bind.png"));
            System.out.println("Passed "+checks+" original NPC geometry/pivot checks.");
        }
    }
}


