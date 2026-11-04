import com.google.gson.*;
import com.koios.game.arpg.*;
import com.koios.game.model.item.Item;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Exercises the author's JSON, rank rules, weighted choices and copy/paste override recipe. */
public final class DropTableCheck {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static JsonObject edit(String json) { return new JsonParser().parse(json).getAsJsonObject(); }
    private static JsonObject row(JsonObject root) {
        return root.getAsJsonArray("tables").get(0).getAsJsonObject().getAsJsonArray("rolls").get(0).getAsJsonObject();
    }
    private static void reject(JsonObject root, String reason) {
        try { MapDropTables.validate(root.toString()); throw new AssertionError("Accepted " + reason); }
        catch (IllegalArgumentException expected) { check(!expected.getMessage().isEmpty(), "Readable error for " + reason); }
    }
    public static void main(String[] args) throws Exception {
        PrototypeIntegrationCheck.loaded();
        String json = new String(Files.readAllBytes(Paths.get("data/arpg/drops.json")), StandardCharsets.UTF_8);
        MapDropTables.validate(json);
        Random random = new Random(48721);
        Set<String> identities = new HashSet<>();
        Map<Integer, Integer> currencies = new TreeMap<>();
        int equipment = 0, gems = 0, maps = 0;
        for (int kill = 0; kill < 2000; kill++) {
            MapDropTables.Reward reward = MapDropTables.roll(new MapDropTables.Context(3,15001,0,false,false,false), random);
            check(reward.experience == 60 && reward.tableId.equals("map_normal"), "Normal rank XP and table");
            int coins = 0, currencyStacks = 0;
            for (Item item : reward.items) {
                if (item.getId() == 995) coins += item.getCount();
                if (CraftingCurrency.index(item.getId()) >= 0) {
                    currencyStacks++;
                    currencies.put(item.getId(), currencies.getOrDefault(item.getId(),0) + 1);
                    check(item.getCount() == 1, "Normal material quantity");
                }
                if (item.hasRoll()) {
                    check(identities.add(item.getRoll().getInstanceId()), "Every rolled drop has a fresh identity");
                    AffixRules.validateGenerated(item.getRoll());
                    if (MapRuns.isGem(item)) gems++;
                    else if (MapRuns.isMap(item)) maps++;
                    else { equipment++; check(item.getRoll().getItemLevel() == 6, "Configured equipment level scaling"); }
                }
            }
            check(coins == 30 && currencyStacks == 1, "Guaranteed coins and one weighted material choice");
        }
        check(equipment > 540 && equipment < 800, "One-in-three equipment remains plausible across deterministic sample");
        check(gems > 450 && maps > 100, "Independent gem and spare-map rows remain active");
        check(currencies.size() == 11, "Every configured material can be selected");
        check(currencies.get(22402) * 10 < currencies.get(22405), "Rare Exalted weight is substantially below Wisdom");

        for (boolean elite : new boolean[]{false,true}) for (boolean boss : new boolean[]{false,true}) {
            MapDropTables.Reward reward = MapDropTables.roll(new MapDropTables.Context(2,15012,4,false,elite,boss), new Random(31));
            check(reward.experience == (boss ? 200 : elite ? 80 : 40), "Rank XP at tier2");
            int coins = 0, material = 0, equipmentCount = 0, portals = 0;
            for (Item item : reward.items) {
                if (item.getId() == 995) coins += item.getCount();
                if (CraftingCurrency.index(item.getId()) >= 0) material += item.getCount();
                if (item.getId() == 22406) portals += item.getCount();
                if (item.hasRoll() && !MapRuns.isGem(item) && !MapRuns.isMap(item)) equipmentCount++;
            }
            check(coins == (boss ? 600 : 60), "Abundant coin multiplier applies to configured row");
            check(material == (boss ? 3 : elite ? 2 : 1), "Rank material stack quantity");
            if (boss || elite) check(equipmentCount == 1, "Elite/champion equipment guarantee");
            if (boss) check(portals == 2, "Champion portal supplies guaranteed");
        }

        // This is the documented author recipe, executed rather than just parsed.
        JsonObject custom = edit(json), table = new JsonObject(), signature = new JsonObject();
        table.addProperty("id","furnace_sentinel"); table.addProperty("experiencePerTier",77);
        signature.addProperty("label","Signature Cinderheart Maul"); signature.addProperty("type","BASE"); signature.addProperty("baseId","siege_maul");
        signature.addProperty("minimumMapTier",3); signature.addProperty("normalWeight",0); signature.addProperty("magicWeight",0); signature.addProperty("rareWeight",1); signature.addProperty("identifiedChancePercent",100);
        JsonArray rows = new JsonArray(); rows.add(signature); table.add("rolls",rows); custom.getAsJsonArray("tables").add(table);
        custom.getAsJsonObject("npcOverrides").addProperty("15001","furnace_sentinel");
        MapDropTables.Reward low = MapDropTables.preview(custom.toString(),new MapDropTables.Context(2,15001,0,false,false,false),new Random(1));
        check(low.items.isEmpty() && low.experience == 154, "Override rank and tier gate are explicit");
        MapDropTables.Reward high = MapDropTables.preview(custom.toString(),new MapDropTables.Context(3,15001,0,false,true,false),new Random(1));
        check(high.items.size() == 1 && high.items.get(0).getId() == 22432, "Named base override produces the intended weapon");
        check(high.items.get(0).getRoll().getRarity() == ItemRoll.Rarity.RARE && high.items.get(0).getRoll().isIdentified(), "Configured rarity and identification");
        signature.addProperty("minimumMapTier",1);
        check(MapDropTables.preview(custom.toString(),new MapDropTables.Context(1,15001,0,false,false,false),new Random(1)).items.isEmpty(), "Base unlock cannot be bypassed by a mistaken tier gate");
        check(MapDropTables.preview(custom.toString(),new MapDropTables.Context(1,15001,0,true,false,false),new Random(1)).tableId.equals("map_normal"), "Tutorial ignores custom NPC overrides");

        JsonObject bad = edit(json); bad.addProperty("version",2); reject(bad,"unknown version");
        bad=edit(json); bad.addProperty("normalTable","missing"); reject(bad,"unknown rank table");
        bad=edit(json); bad.getAsJsonArray("tables").add(bad.getAsJsonArray("tables").get(0)); reject(bad,"duplicate table");
        bad=edit(json); row(bad).addProperty("chanceDenominator",0); reject(bad,"zero denominator");
        bad=edit(json); row(bad).addProperty("minQuantity",0); reject(bad,"zero quantity");
        bad=edit(json); row(bad).addProperty("minimumMapTier",0); reject(bad,"invalid tier gate");
        bad=edit(json); row(bad).addProperty("type","UNKNOWN"); reject(bad,"unknown drop type");
        bad=edit(json); row(bad).addProperty("type","POOL"); row(bad).addProperty("pool","missing"); reject(bad,"unknown pool");
        bad=edit(json); row(bad).addProperty("type","EQUIPMENT"); reject(bad,"rolled quantity greater than one");
        bad=edit(json); row(bad).addProperty("type","BASE"); row(bad).addProperty("baseId","missing"); reject(bad,"unknown named base");
        bad=edit(json); bad.getAsJsonObject("pools").getAsJsonArray("crafting_materials").get(0).getAsJsonObject().addProperty("weight",-1); reject(bad,"negative pool weight");
        check(MapDropTables.roll(new MapDropTables.Context(1,15001,0,false,false,false),new Random(1)).tableId.equals("map_normal"), "Preview/invalid edits never mutate the live catalog");

        // A raw custom base would bypass both its UUID and level gate. Every
        // reserved rolled item must be generated through its typed drop recipe.
        List<Integer> rolledIds = new ArrayList<>(Arrays.asList(22410,22411,22412,22413,22414));
        for (EquipmentBases.Base equipmentBase : EquipmentBases.all()) rolledIds.add(equipmentBase.itemId);
        for (int itemId : rolledIds) for (boolean pool : new boolean[]{false,true}) {
            JsonObject raw = edit(json);
            if (pool) raw.getAsJsonObject("pools").getAsJsonArray("crafting_materials")
                    .get(0).getAsJsonObject().addProperty("itemId",itemId);
            else row(raw).addProperty("itemId",itemId);
            try {
                MapDropTables.validate(raw.toString());
                throw new AssertionError("Raw rolled item accepted: " + itemId);
            } catch (IllegalArgumentException expected) {
                check(expected.getMessage().contains("BASE") && expected.getMessage().contains("GEM")
                        && expected.getMessage().contains("MAP"), "Raw rolled ID rejected with author recipe guidance");
            }
        }

        StringBuilder report = new StringBuilder("# Drop balance reference\n\nSeed48721,2000 normal tier3 kills using data/arpg/drops.json. This is a deterministic sample, not final economy validation.\n\n");
        report.append("Equipment: ").append(equipment).append("; gems: ").append(gems).append("; spare maps: ").append(maps).append(".\n\n|Material ID|Stacks|\n|---|---|\n");
        for (Map.Entry<Integer,Integer> entry : currencies.entrySet()) report.append('|').append(entry.getKey()).append('|').append(entry.getValue()).append("|\n");
        report.append("\nXP/coin/elite/boss guarantees, named-base copy recipe, unlocks, preview isolation and malformed edits checked. Mandatory progression-map/exit behavior remains covered by PrototypeIntegrationCheck/MapDeathCheck.\n");
        Files.write(Paths.get("../../logs/drop-balance.md"),report.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("Passed " + checks + " editable drop-table reward, author-recipe and validation checks.");
    }
}
