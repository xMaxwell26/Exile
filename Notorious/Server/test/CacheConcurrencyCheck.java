import com.koios.client.cache.Decompressor;
import com.koios.client.stream.StreamLoader;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/** Exercises shared sector-file transactions. Writes are confined to a temporary directory. */
public final class CacheConcurrencyCheck {
    private static final AtomicInteger checks=new AtomicInteger();
    private static void check(boolean value,String message){checks.incrementAndGet();if(!value)throw new AssertionError(message);}
    private static byte[] bytes(int seed,int length){byte[] b=new byte[length];new Random(seed).nextBytes(b);return b;}
    private static void together(Runnable... tasks)throws Exception {
        ExecutorService pool=Executors.newFixedThreadPool(tasks.length);
        CountDownLatch start=new CountDownLatch(1);List<Future<?>> futures=new ArrayList<>();
        try {
            for(Runnable task:tasks)futures.add(pool.submit(()->{try{start.await();task.run();}catch(InterruptedException e){Thread.currentThread().interrupt();throw new AssertionError(e);}}));
            start.countDown();for(Future<?> future:futures)future.get(60,TimeUnit.SECONDS);
        }finally{pool.shutdownNow();check(pool.awaitTermination(10,TimeUnit.SECONDS),"Cache workers finish before handles close");}
    }
    private static void temporaryWrites(Path dir)throws Exception {
        try(RandomAccessFile data=new RandomAccessFile(dir.resolve("cache.dat").toFile(),"rw");
            RandomAccessFile ia=new RandomAccessFile(dir.resolve("a.idx").toFile(),"rw");
            RandomAccessFile ib=new RandomAccessFile(dir.resolve("b.idx").toFile(),"rw");
            RandomAccessFile otherData=new RandomAccessFile(dir.resolve("other.dat").toFile(),"rw");
            RandomAccessFile ic=new RandomAccessFile(dir.resolve("c.idx").toFile(),"rw")) {
            Decompressor[] cache={new Decompressor(data,ia,1),new Decompressor(data,ib,5),new Decompressor(otherData,ic,3)};
            Runnable[] workers=new Runnable[cache.length];
            for(int k=0;k<cache.length;k++){
                final int index=k;workers[k]=()->{
                    // Same archive ID in different indices must not share sector chains.
                    // Sizes cross sector boundaries and repeatedly grow/shrink existing chains.
                    int[] lengths={1,511,512,513,4097,30,2048,8193};
                    for(int n=0;n<240;n++){
                        byte[] expected=bytes(index*1000+n,lengths[n%lengths.length]);
                        check(cache[index].method234(expected.length,expected,424),"Temporary write succeeds");
                        check(Arrays.equals(expected,cache[index].decompress(424)),"Concurrent grow/shrink roundtrip retains exact bytes");
                    }
                };
            }
            together(workers);
            check(cache[0].decompress(65530)==null,"Missing archive remains null");
            // Read through a fresh wrapper to ensure no in-memory shortcut hides file corruption.
            check(Arrays.equals(cache[0].decompress(424),new Decompressor(data,ia,1).decompress(424)),"On-disk archive survives wrapper reload");
        }
    }
    private static void productionReadOnly()throws Exception {
        try(RandomAccessFile data=new RandomAccessFile("../koios-cache/main_file_cache.dat","r");
            RandomAccessFile configIndex=new RandomAccessFile("../koios-cache/main_file_cache.idx0","r");
            RandomAccessFile mapIndex=new RandomAccessFile("../koios-cache/main_file_cache.idx4","r")) {
            Decompressor config=new Decompressor(data,configIndex,1),maps=new Decompressor(data,mapIndex,5);
            byte[] archive=config.decompress(2),terrain=maps.decompress(424);
            check(archive!=null&&terrain!=null,"Actual configuration and home terrain exist");
            check(new StreamLoader(archive).getDataForName("flo.dat")!=null,"Actual floor configuration still decodes");
            try(GZIPInputStream zip=new GZIPInputStream(new ByteArrayInputStream(terrain))){int length=0;byte[] block=new byte[4096];int read;while((read=zip.read(block))!=-1)length+=read;check(length==31467,"Actual home terrain gzip remains intact");}
            together(()->{for(int n=0;n<700;n++)check(Arrays.equals(archive,config.decompress(2)),"Concurrent configuration bytes intact");},
                ()->{for(int n=0;n<700;n++)check(Arrays.equals(terrain,maps.decompress(424)),"Concurrent terrain bytes intact");});
        }
    }
    public static void main(String[] args)throws Exception {
        Path dir=Files.createTempDirectory(Paths.get("build"),"cache-concurrency-");
        try{temporaryWrites(dir);productionReadOnly();}
        finally{try(java.util.stream.Stream<Path> files=Files.walk(dir)){for(Path p:(Iterable<Path>)files.sorted(Comparator.reverseOrder())::iterator)Files.delete(p);}}
        System.out.println("Passed "+checks+" cache concurrency/sector-format checks; production cache was read-only.");
    }
}
