import com.koios.client.arpg.CustomEquipment;
import com.koios.client.definition.ItemDefinition;
import com.koios.client.world.Model;

/** Quantized worn vertices must stay outside the real torso surface at their final XY. */
public final class ArmourSurfaceCheck {
    private static int checks;
    private static double front(Model model, int x, int y) {
        double closest=Double.POSITIVE_INFINITY;
        for(int face=0;face<model.anInt1630;face++) {
            int a=model.anIntArray1631[face],b=model.anIntArray1632[face],c=model.anIntArray1633[face];
            if(model.anIntArray1655[a]!=8||model.anIntArray1655[b]!=8||model.anIntArray1655[c]!=8)continue;
            double ax=model.anIntArray1627[a],ay=model.anIntArray1628[a];
            double bx=model.anIntArray1627[b],by=model.anIntArray1628[b];
            double cx=model.anIntArray1627[c],cy=model.anIntArray1628[c];
            double determinant=(by-cy)*(ax-cx)+(cx-bx)*(ay-cy);
            if(Math.abs(determinant)<.0001)continue;
            double u=((by-cy)*(x-cx)+(cx-bx)*(y-cy))/determinant;
            double v=((cy-ay)*(x-cx)+(ax-cx)*(y-cy))/determinant,w=1-u-v;
            if(u>=-.001&&v>=-.001&&w>=-.001) closest=Math.min(closest,
                    u*model.anIntArray1629[a]+v*model.anIntArray1629[b]+w*model.anIntArray1629[c]);
        }
        return closest;
    }
    public static void main(String[] args)throws Exception {
        CustomItemVisualCheck.main(new String[0]);
        int failures=0; double worst=0;
        for(int id:new int[]{22424,22425,22426,22433,22434,22438,22439})for(int gender=0;gender<2;gender++) {
            Model template=ItemDefinition.forID(CustomEquipment.template(id)).method196(gender);
            Model decorated=ItemDefinition.forID(id).method196(gender);
            for(int vertex=template.anInt1626;vertex<decorated.anInt1626;vertex++) {
                double surface=front(template,decorated.anIntArray1627[vertex],decorated.anIntArray1628[vertex]);
                if(Double.isInfinite(surface))continue; // Beyond the template silhouette; local visual QA still needed.
                checks++;
                double penetration=decorated.anIntArray1629[vertex]-surface;
                if(penetration>=0) {
                    failures++;worst=Math.max(worst,penetration);
                    if(failures<=5)System.out.println("Clipping vertex: item="+id+" gender="+gender+" vertex="+vertex+" penetration="+penetration);
                }
            }
        }
        if(failures>0)throw new AssertionError(failures+" fitted vertices touch or penetrate torso; worst="+worst);
        System.out.println("Passed "+checks+" quantized armour/torso surface clearance checks. Triangle interiors and all-frame outfit acceptance remain local QA.");
    }
}
