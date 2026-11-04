import java.util.*;
import com.koios.network.*;
import com.koios.network.codec.PacketDecoder;
import com.koios.client.stream.Stream;
import com.koios.client.utils.ISAACRandomGen;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

/** Exercise the real client cipher and server decoder across TCP fragment boundaries. */
public class PacketFragmentationCheck {
 static final int[] SEED={17,29,43,71};
 static final int[] OPCODES={72,103,103,0,103,41};
 static byte[] wire(){
  Stream out=new Stream(new byte[1024]);out.encryption=new ISAACRandomGen(SEED.clone());
  out.createFrame(72);out.method432(4);
  command(out,"arpgskill slot 0");command(out,"arpgskill slot 1");out.createFrame(0);
  command(out,"arpgskill slot 2");out.createFrame(41);out.writeShort(22420);out.method432(0);out.method432(3214);
  return Arrays.copyOf(out.buffer,out.currentOffset);
 }
 static void command(Stream out,String value){out.createFrame(103);out.writeLEShort(value.length()+1);out.writeString(value);}
 static void verify(byte[] wire,int split,boolean bytewise){
  Session session=new PrototypeIntegrationCheck.SessionStub();session.seedCiphers(SEED.clone());
  EmbeddedChannel channel=new EmbeddedChannel(new PacketDecoder(session));
  try{
   if(bytewise)for(byte value:wire)channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{value}));
   else {channel.writeInbound(Unpooled.wrappedBuffer(Arrays.copyOfRange(wire,0,split)));channel.writeInbound(Unpooled.wrappedBuffer(Arrays.copyOfRange(wire,split,wire.length)));}
   for(int opcode:OPCODES){GamePacket packet=(GamePacket)channel.readInbound();if(packet==null||packet.getOpcode()!=opcode)throw new AssertionError("Decoder lost framing at split "+split+", expected opcode "+opcode+", got "+(packet==null?"none":packet.getOpcode()));
    try {if(opcode==103&&!packet.readString().startsWith("arpgskill slot "))throw new AssertionError("Command payload corrupted");if(opcode==72&&packet.readShortA()!=4)throw new AssertionError("Target index corrupted");if(opcode==41&&(packet.readShort()!=22420||packet.readShortA()!=0||packet.readShortA()!=3214))throw new AssertionError("Equip payload corrupted");}finally{packet.getBuffer().release();}}
   if(channel.readInbound()!=null)throw new AssertionError("Spurious packet decoded");
  }finally{channel.finish();}
 }
 public static void main(String[] args){byte[] wire=wire();for(int split=1;split<wire.length;split++)verify(wire,split,false);verify(wire,0,true);System.out.println("Encrypted combat and skill packets passed every TCP split and bytewise delivery.");}
}

