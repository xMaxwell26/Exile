import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import java.util.*;

/** Every authored base: computed properties, concise normal view and unidentified privacy. */
public final class TooltipContentCheck {
    private static int checks;
    private static void check(boolean condition,String reason) {
        checks++; if(!condition)throw new AssertionError(reason);
    }
    private static long count(List<String> lines,String prefix) {
        return lines.stream().filter(line->line.startsWith(prefix)).count();
    }
    public static void main(String[] args)throws Exception {
        com.koios.game.def.ItemDefinition.load();
        for(EquipmentBases.Base base:EquipmentBases.all())for(int seed=0;seed<12;seed++) {
            Item item=EquipmentBases.create(base,30);
            item.setRoll(item.getRoll().changed(ItemRoll.Rarity.RARE,true,0,
                    AffixCatalog.roll(item,ItemRoll.Rarity.RARE,new Random(seed)),null));
            String before=ItemRollCodec.encode(item);
            for(boolean advanced:new boolean[]{false,true}) {
                List<String> lines=ItemTooltip.lines(item,advanced);
                Map<Stat,Double> stats=ArpgStats.equipment(new Item[]{item});
                if(stats.get(Stat.PHYSICAL_MAX)>0) {
                    String expected=String.format(Locale.ROOT,"Physical Damage: %.0f-%.0f",
                            Math.floor(stats.get(Stat.PHYSICAL_MIN)),Math.ceil(stats.get(Stat.PHYSICAL_MAX)));
                    check(lines.contains(expected)&&count(lines,"Physical Damage:")==1,"Single attainable weapon range: "+base.id);
                }
                for(Stat stat:new Stat[]{Stat.ARMOUR,Stat.EVASION,Stat.ENERGY_SHIELD}) {
                    String title=stat==Stat.ARMOUR?"Armour:":stat==Stat.EVASION?"Evasion:":"Energy shield:";
                    check(count(lines,title)==(stats.get(stat)>0?1:0),"Single computed defence: "+base.id+" "+stat);
                }
                if(!advanced) {
                    check(lines.stream().noneMatch(line->line.startsWith("Base ")||line.contains(" / Tier ")||line.startsWith("Prefix /")||line.startsWith("Suffix /")),
                            "Normal view omits implementation offsets and affix metadata");
                    check(lines.get(lines.size()-1).equals("Hold Ctrl for affix details"),"Advanced-view hint remains visible");
                } else check(lines.stream().noneMatch(line->line.startsWith("Base implicit /")&&line.contains("Tier")),"Base properties have no explicit tier");
            }
            check(before.equals(ItemRollCodec.encode(item)),"Rendering never mutates rolled metadata");
            ItemRoll rolled=item.getRoll();
            item.setRoll(rolled.changed(rolled.getRarity(),false,0,rolled.getAffixes(),null));
            Item hidden=item.copy();
            List<ItemRoll.Affix> bases=new ArrayList<>();
            for(ItemRoll.Affix affix:rolled.getAffixes())if(affix.getLayer()==ItemRoll.Layer.BASE_IMPLICIT)bases.add(affix);
            hidden.setRoll(item.getRoll().changed(rolled.getRarity(),false,0,bases,null));
            for(boolean advanced:new boolean[]{false,true})check(ItemTooltip.lines(item,advanced).equals(ItemTooltip.lines(hidden,advanced)),
                    "Unidentified tooltip never reveals explicit values, names or tiers: "+base.id);
        }
        System.out.println("Passed "+checks+" all-base tooltip property, metadata and unidentified privacy checks.");
    }
}
