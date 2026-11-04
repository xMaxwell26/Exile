import com.koios.game.map.ObjectDefinition;import java.util.Arrays;
public class PortalNames {public static void main(String[] args){ObjectDefinition.load();for(int id=0;id<ObjectDefinition.streamIndices.length;id++){ObjectDefinition d=ObjectDefinition.getById(id);if(d.name!=null&&(d.name.equalsIgnoreCase("portal")))System.out.println(id+" "+d.name+" "+Arrays.toString(d.actions)+" "+d.xLength+"x"+d.yLength);}}}

