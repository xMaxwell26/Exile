import com.google.gson.*;
import com.koios.game.arpg.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Fast author-facing data validation. No server, map, player, save or network is created. */
public final class ValidateContent {
    private static String read(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get("data/arpg",file)),StandardCharsets.UTF_8);
    }
    private static void require(boolean condition,String context) {
        if (!condition) throw new IllegalArgumentException(context);
    }
    public static void main(String[] args) throws Exception {
        // Use the same loader as kills, including pool references and typed rolled-item guards.
        MapDropTables.validate(read("drops.json"));
        System.out.println("OK drops.json: tables, chances, pools, quantities, gates and generated-item rules.");

        // Loading every role/alias validates the entire enemy array atomically.
        for (int role=0;role<7;role++) EncounterProfiles.get(role);
        for (int id=15000;id<15012;id++) EncounterProfiles.get((id-15000)%6,id);
        System.out.println("OK enemies.json: roles, overrides, damage, life, cadence and windup bounds.");

        Set<String> baseNames=new HashSet<>();
        Set<Integer> itemIds=new HashSet<>();
        JsonArray bases=new JsonParser().parse(read("bases.json")).getAsJsonArray();
        for (JsonElement element:bases) {
            JsonObject base=element.getAsJsonObject();
            String id=base.get("id").getAsString();
            String at="bases.json / "+id;
            require(baseNames.add(id),at+": duplicate base id");
            require(itemIds.add(base.get("itemId").getAsInt()),at+": duplicate itemId");
            require(!base.get("name").getAsString().trim().isEmpty(),at+": empty display name");
            // Original starter bases omit minLevel; gameplay treats that legacy default as level1.
            int level=base.has("minLevel")?base.get("minLevel").getAsInt():1;
            require(level>=1&&level<=100,at+": minLevel must be1..100");
            for (JsonElement statElement:base.getAsJsonArray("stats")) {
                JsonObject stat=statElement.getAsJsonObject();
                Stat.valueOf(stat.get("stat").getAsString());
                require(Double.isFinite(stat.get("base").getAsDouble())&&Double.isFinite(stat.get("growth").getAsDouble()),at+": non-finite base/growth");
            }
        }
        System.out.println("OK bases.json: "+bases.size()+" unique bases, stat names and finite scaling values.");

        Set<String> families=new HashSet<>();
        JsonArray affixes=new JsonParser().parse(read("affixes.json")).getAsJsonArray();
        for (JsonElement element:affixes) {
            JsonObject family=element.getAsJsonObject();
            String id=family.get("id").getAsString();
            String at="affixes.json / "+id;
            require(families.add(id),at+": duplicate family");
            ItemRoll.Layer layer=ItemRoll.Layer.valueOf(family.get("layer").getAsString());
            require(layer==ItemRoll.Layer.PREFIX||layer==ItemRoll.Layer.SUFFIX,at+": explicit family must be PREFIX or SUFFIX");
            require(Arrays.asList("weapon","armour").contains(family.get("slots").getAsString()),at+": slots must be weapon or armour");
            Modifier.Operation.valueOf(family.get("operation").getAsString());
            JsonArray stats=family.getAsJsonArray("stats");
            require(stats.size()>0,at+": missing stats");
            for (JsonElement stat:stats) Stat.valueOf(stat.getAsString());
            Set<Integer> tiers=new HashSet<>();
            long totalWeight=0;
            for (JsonElement tierElement:family.getAsJsonArray("tiers")) {
                JsonObject tier=tierElement.getAsJsonObject();
                int number=tier.get("tier").getAsInt(),level=tier.get("level").getAsInt(),weight=tier.get("weight").getAsInt();
                require(number>=1&&tiers.add(number),at+": duplicate/invalid tier");
                require(level>=1&&level<=100&&weight>0,at+" / T"+number+": invalid level/weight");
                totalWeight+=weight;
                JsonArray low=tier.getAsJsonArray("low"),high=tier.getAsJsonArray("high");
                require(low.size()==stats.size()&&high.size()==stats.size(),at+" / T"+number+": ranges must match stat count");
                for (int index=0;index<stats.size();index++) {
                    double minimum=low.get(index).getAsDouble(),maximum=high.get(index).getAsDouble();
                    require(Double.isFinite(minimum)&&Double.isFinite(maximum)&&minimum<=maximum,at+" / T"+number+": invalid roll range");
                }
            }
            require(!tiers.isEmpty()&&totalWeight<=Integer.MAX_VALUE,at+": empty/overflowing tier weights");
        }
        System.out.println("OK affixes.json: "+affixes.size()+" families, layers, stat ranges, tiers and weights.");
        System.out.println("Exile content validation passed. Restart the server to apply data edits.");
    }
}
