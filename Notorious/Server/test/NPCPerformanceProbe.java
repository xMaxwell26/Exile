import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.util.*;
import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;
import com.koios.client.definition.*;
import com.koios.client.world.Model;
import com.koios.client.graphic.*;

/** Diagnostic only: no timing assertions and no registration in the ordinary test suite.
 * Each timed operation uses the actual three-argument NPC model path and optionally
 * the native rasterizer. Caches, scratch arrays and the pixel surface are reused.
 */
public final class NPCPerformanceProbe {
    private static final int WIDTH=512, HEIGHT=334, BATCH_SIZE=32, BATCHES=41;
    private static volatile long checksum;

    private static final class Case {
        final int id, sequence;
        final String role;
        final NPCDefinition definition;
        final int[] frames;
        final long[] nanos=new long[BATCHES], allocations=new long[BATCHES];
        int cursor;
        Case(int id,int sequence,String role) {
            this.id=id;this.sequence=sequence;this.role=role;
            definition=NPCDefinition.forID(id);
            frames=Animation.anims[sequence].anIntArray353;
        }
        void render(int count,boolean draw,int[] pixels) {
            for(int n=0;n<count;n++) {
                int index=cursor++;
                Model model=definition.method164(-1,frames[index%frames.length],null);
                if(draw) {
                    Arrays.fill(pixels,0x17232C);
                    model.method482((index&3)*512,0,0,0,100,700);
                    checksum+=pixels[WIDTH/2+HEIGHT/2*WIDTH];
                } else {
                    checksum+=model.anIntArray1627[index%model.anInt1626];
                }
            }
        }
    }

    private static long percentile(long[] values,double percentile) {
        long[] sorted=values.clone();Arrays.sort(sorted);
        return sorted[(int)Math.ceil(percentile*sorted.length)-1];
    }

    private static void measure(Case[] cases,boolean draw,int[] pixels,
                                com.sun.management.ThreadMXBean bean) {
        // Warm every case before collecting samples. No image or model snapshots are created.
        for(int pass=0;pass<16;pass++)for(Case item:cases)item.render(128,draw,pixels);
        long thread=Thread.currentThread().getId();
        for(int batch=0;batch<BATCHES;batch++) {
            // Rotate order to reduce bias from JIT, background work and temperature changes.
            for(int offset=0;offset<cases.length;offset++) {
                Case item=cases[(batch+offset)%cases.length];
                long bytesBefore=bean==null?0:bean.getThreadAllocatedBytes(thread);
                long started=System.nanoTime();
                item.render(BATCH_SIZE,draw,pixels);
                item.nanos[batch]=System.nanoTime()-started;
                item.allocations[batch]=bean==null?-1:bean.getThreadAllocatedBytes(thread)-bytesBefore;
            }
        }
        System.out.println("MODE "+(draw?"pose + clear + native draw":"pose only"));
        System.out.println("id,role,sequence,batch32_median_ms,batch32_p95_ms,perNPC_median_us,perNPC_p95_us,median_alloc_bytes_perNPC");
        for(Case item:cases) {
            long median=percentile(item.nanos,.5),p95=percentile(item.nanos,.95);
            System.out.printf(Locale.ROOT,"%d,%s,%d,%.4f,%.4f,%.3f,%.3f,%.1f%n",
                item.id,item.role,item.sequence,median/1e6,p95/1e6,
                median/(BATCH_SIZE*1e3),p95/(BATCH_SIZE*1e3),
                percentile(item.allocations,.5)/(double)BATCH_SIZE);
        }
    }

    public static void main(String[] args)throws Exception {
        try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");
            RandomAccessFile config=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");
            RandomAccessFile models=new RandomAccessFile("../koios-cache/main_file_cache.idx1","r");
            RandomAccessFile animation=new RandomAccessFile("../koios-cache/main_file_cache.idx2","r")) {
            StreamLoader archive=new StreamLoader(new Decompressor(data,config,1).decompress(2));
            NPCDefinition.unpackConfig(archive);Animation.unpackConfig(archive);Model.method459(0,null);
            Rasterizer.method368(new StreamLoader(new Decompressor(data,config,1).decompress(6)));
            Rasterizer.method372(.85);Rasterizer.method367();
            Decompressor cache=new Decompressor(data,models,2);
            Set<Integer> loaded=new HashSet<>();
            for(int id:new int[]{90,15001,15007})for(int model:NPCDefinition.forID(id).npcModels)
                SceneryVisualCheck.load(cache,model&65535,loaded);
            SkillAnimationProbe.animations=new Decompressor(data,animation,3);
            ArrayList<Case> list=new ArrayList<>();
            for(int id:new int[]{90,15001,15007}) {
                Model bind=NPCDefinition.forID(id).method164(-1,-1,null);
                System.out.println("MODEL "+id+" vertices="+bind.anInt1626+" faces="+bind.anInt1630);
                if(id!=90&&bind.anInt1626!=4950)throw new IllegalStateException("Wrong authored candidate loaded");
                int index=0;
                for(int sequence:new int[]{5483,5481,5487}) {
                    for(int frame:Animation.anims[sequence].anIntArray353)SkillAnimationProbe.loadFrame(frame);
                    list.add(new Case(id,sequence,new String[]{"idle","walk","attack"}[index++]));
                }
            }
            int[] pixels=new int[WIDTH*HEIGHT];
            DrawingArea.initDrawingArea(HEIGHT,WIDTH,pixels);Rasterizer.method364();
            com.sun.management.ThreadMXBean bean=null;
            java.lang.management.ThreadMXBean candidate=ManagementFactory.getThreadMXBean();
            if(candidate instanceof com.sun.management.ThreadMXBean) {
                bean=(com.sun.management.ThreadMXBean)candidate;
                if(bean.isThreadAllocatedMemorySupported())bean.setThreadAllocatedMemoryEnabled(true);
                else bean=null;
            }
            System.out.println("JVM "+System.getProperty("java.version")+"; fixed "+WIDTH+"x"+HEIGHT+
                "; "+BATCHES+" batches x "+BATCH_SIZE+" NPC operations per case; allocation meter="+(bean!=null));
            Case[] cases=list.toArray(new Case[0]);
            measure(cases,false,pixels,bean);
            measure(cases,true,pixels,bean);
            System.out.println("checksum="+checksum);
            System.out.println("Diagnostic only. No full scene traversal, occlusion, entities, UI, AWT presentation or server logic.");
        }
    }
}

