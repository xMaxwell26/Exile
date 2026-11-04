import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import com.koios.client.Client;
import com.koios.client.arpg.*;
import com.koios.client.definition.RSInterface;
import com.koios.client.stream.Stream;
import com.koios.client.ui.ScreenMode;
import com.koios.client.utils.ISAACRandomGen;

/** Exercises the real inventory press and release routing without desktop input or a server. */
public final class InventoryGemDragCheck {
    private static Client client;
    private static Stream wire;
    private static int checks,pressX,pressY;
    private static Field field(String name)throws Exception {
        for(Class<?> type=Client.class;type!=null;type=type.getSuperclass())try {
            Field field=type.getDeclaredField(name);field.setAccessible(true);return field;
        }catch(NoSuchFieldException ignored){}
        throw new NoSuchFieldException(name);
    }
    private static void set(String name,Object value)throws Exception {field(name).set(client,value);}
    private static Object get(String name)throws Exception {return field(name).get(client);}
    private static Object call(String name)throws Exception {
        Method method=Client.class.getDeclaredMethod(name);method.setAccessible(true);return method.invoke(client);
    }
    private static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    private static String sent(){return new String(wire.buffer,0,wire.currentOffset,StandardCharsets.ISO_8859_1);}
    private static void press(int id)throws Exception {
        wire.currentOffset=0;
        RSInterface.interfaceCache[3214].container[0]=id+1;
        set("arpgSkillsOpen",true);set("prototypeOpen",false);set("controlsPanel",null);
        set("activeInterfaceType",0);set("awtFocus",true);set("tabID",3);set("menuOpen",false);
        set("inputDialogState",0);set("messagePromptRaised",false);set("backDialogID",-1);set("openInterfaceID",-1);
        set("saveClickX",pressX);set("saveClickY",pressY);set("clickMode3",1);
        ((int[])get("menuActionID"))[0]=447;
        ((int[])get("menuActionCmd2"))[0]=0;((int[])get("menuActionCmd3"))[0]=3214;
        set("menuActionRow",1);call("processMenuClick");
        check((Integer)get("arpgInventoryGemId")==id,"Actual inventory press marks an Arsenal gem gesture");
    }
    private static boolean release(int x,int y)throws Exception {
        set("releasedX",x);set("releasedY",y);set("activeInterfaceType",0);
        return (Boolean)call("finishArpgInventoryDrag");
    }
    public static void main(String[] args)throws Exception {
        client=new Client();Client.instance=client;Client.loggedIn=true;
        wire=new Stream(new byte[65536]);wire.encryption=new ISAACRandomGen(new int[]{1,2,3,4});set("stream",wire);
        RSInterface.interfaceCache=new RSInterface[70000];RSInterface bag=new RSInterface();
        bag.id=3214;bag.allowSwapItems=true;bag.container=new int[28];bag.containerSizes=new int[28];
        RSInterface.interfaceCache[3214]=bag;
        for(ScreenMode mode:new ScreenMode[]{ScreenMode.FIXED,ScreenMode.RESIZABLE}) {
            Client.frameMode=mode;Client.frameWidth=mode==ScreenMode.FIXED?765:1280;Client.frameHeight=mode==ScreenMode.FIXED?503:720;
            int dx=mode==ScreenMode.FIXED?4:OverlayViewport.originX(1280,720,false);
            int dy=mode==ScreenMode.FIXED?4:OverlayViewport.originY(1280,720,false);
            pressX=mode==ScreenMode.FIXED?580:1120;pressY=mode==ScreenMode.FIXED?240:460;
            press(22411);
            check(release(dx+52,dy+94)&&sent().contains("arpgskill socketactive 0 0"),"Active socket receives exact source and slot in "+mode);
            int sentOnce=wire.currentOffset;
            check(!release(dx+52,dy+94)&&wire.currentOffset==sentOnce,"A release cannot socket twice");
            press(22412);
            check(release(dx+209,dy+158)&&sent().contains("arpgskill socket 0 1 0"),"Support socket routes to selected skill in "+mode);
            press(22411);
            check(release(dx+400,dy+180)&&wire.currentOffset==0,"Missed socket is consumed without a ground-drop packet");
            press(22411);
            check(!release(pressX+40,pressY)&&wire.currentOffset==0,"Inventory reorder retains legacy inventory routing");
            press(22411);set("arpgSkillsOpen",false);
            check(release(dx+52,dy+94)&&wire.currentOffset==0,"Closing Arsenal cancels a pending gem release");
            press(22412);bag.container[0]=995+1;
            check(release(dx+209,dy+94)&&wire.currentOffset==0,"A changed source cannot socket another item");
            press(22411);client.arpgFocusLost();
            Object queue=get("controlActions");Runnable action;
            while((action=((java.util.Queue<Runnable>)queue).poll())!=null)action.run();
            check((Integer)get("activeInterfaceType")==0&&(Integer)get("arpgInventoryGemId")==0&&wire.currentOffset==0,"Focus loss clears held inventory gesture without a packet");
        }
        System.out.println("Passed "+checks+" actual inventory gem press/release/cancellation checks.");
        System.exit(0);
    }
}
