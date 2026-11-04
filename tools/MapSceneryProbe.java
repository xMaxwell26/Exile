import com.koios.game.map.*;public class MapSceneryProbe {public static void main(String[] args)throws Exception{PrototypeIntegrationCheck.loaded();int found=0;for(int i=0;i<15000&&found<15;i++){ObjectDefinition d=ObjectDefinition.getById(i);if(d!=null&&d.name!=null&&(d.name.equalsIgnoreCase("Wall"))){System.out.println(i+" "+d.name+" "+d.xLength+"x"+d.yLength);found++;}}}}

