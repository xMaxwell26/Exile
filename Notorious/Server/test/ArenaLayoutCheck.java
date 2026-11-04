import java.lang.reflect.*;
import com.koios.game.map.*;
import com.koios.game.model.Location;
import com.koios.game.arpg.TestArena;
public class ArenaLayoutCheck {
 public static void main(String[] args)throws Exception{
  ObjectDefinition.load();ObjectAlternates.loadAlternateIds();MapLoading.load();MapRegion.sort();
  Method method=TestArena.class.getDeclaredMethod("findFloor",int.class);method.setAccessible(true);
  Location location=(Location)method.invoke(null,12);System.out.println("Verified clear test arena floor: "+location.getX()+","+location.getY()+","+location.getZ());
 }
}
