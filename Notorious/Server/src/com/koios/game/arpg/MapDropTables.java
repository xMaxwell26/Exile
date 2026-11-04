package com.koios.game.arpg;

import com.google.gson.Gson;
import com.koios.game.model.item.Item;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Editable map-monster rewards, separate from combat and ground-item ownership.
 *
 * Start with data/arpg/drops.json and BALANCING-GUIDE.md. A table is a list of
 * INDEPENDENT rolls: two successful rows produce two drops. A POOL row instead
 * chooses ONE item using relative weights. No item is added to an inventory here;
 * MapRuns places the returned items on the owner's instance floor.
 *
 * Tutorial checkpoint rewards and the champion's guaranteed progression map are
 * deliberately owned by MapRuns, so editing optional loot cannot break travel.
 */
public final class MapDropTables {
    private static volatile Catalog catalog;

    /** Read-only encounter facts. Tier and monster rank select rewards, not RS levels. */
    public static final class Context {
        public final int tier, npcId, mods;
        public final boolean intro, elite, boss;

        public Context(int tier, int npcId, int mods, boolean intro, boolean elite, boolean boss) {
            if (tier < 1 || tier > 10) throw new IllegalArgumentException("Map tier must be 1..10");
            this.tier = tier;
            this.npcId = npcId;
            this.mods = mods;
            this.intro = intro;
            this.elite = elite;
            this.boss = boss;
        }
    }

    /** Prepared rewards only; the caller awards XP and spawns each ground item once. */
    public static final class Reward {
        public final String tableId;
        public final long experience;
        public final List<Item> items;

        private Reward(String tableId, long experience, List<Item> items) {
            this.tableId = tableId;
            this.experience = experience;
            this.items = Collections.unmodifiableList(items);
        }
    }

    /* These data classes mirror the named JSON fields. Gson ignores _comment fields. */
    private static final class Catalog {
        int version;
        String normalTable, eliteTable, bossTable;
        Map<String, String> npcOverrides;
        Map<String, PoolEntry[]> pools;
        Table[] tables;
        transient Map<String, Table> byId;
    }

    private static final class Table {
        String id;
        int experiencePerTier;
        Roll[] rolls;
    }

    private static final class PoolEntry {
        int itemId, weight;
    }

    private static final class Roll {
        String label, type, pool, baseId;
        int itemId;
        // 1/3 means one independent success in three attempts, not a relative weight.
        int chanceNumerator = 1, chanceDenominator = 1;
        int abundantChanceDenominator;
        int minQuantity = 1, maxQuantity = 1, abundantQuantityMultiplier = 1;
        boolean excludeIntroduction;
        int minimumMapTier = 1;
        // Equipment level = tier * multiplier. Introduction equipment stays level1.
        int equipmentLevelPerTier = 2;
        // Relative rarity weights. Defaults preserve the previous20/40/40 split.
        int normalWeight = 1, magicWeight = 2, rareWeight = 2;
        int identifiedChancePercent = 33;
        // Gem level = base + floor(tier/divisor), clamped to the available level1..20.
        int gemLevelBase = 1, gemLevelTierDivisor = 2;
        // A GEM can be active or support; SUPPORT_GEM always produces support.
        int supportChancePercent = 50;
    }

    /** Load once. Restart the server after editing JSON; invalid settings fail loudly. */
    private static Catalog data() {
        Catalog current = catalog;
        if (current != null) return current;
        synchronized (MapDropTables.class) {
            if (catalog == null) {
                try {
                    catalog = parse(new String(Files.readAllBytes(Paths.get("data/arpg/drops.json")), StandardCharsets.UTF_8));
                } catch (Exception failure) {
                    throw new IllegalStateException("Cannot load data/arpg/drops.json: " + failure.getMessage(), failure);
                }
            }
            return catalog;
        }
    }

    /** Validate edited data independently of a running world (also used by content checks). */
    public static void validate(String json) { parse(json); }

    /** Startup gate: catch a broken edit before players enter a map or kill anything. */
    public static void initialize() { data(); }

    /** Preview an edited table without changing the live catalog or spawning anything. */
    public static Reward preview(String json, Context context, Random random) {
        return roll(parse(json), context, random);
    }

    private static Catalog parse(String json) {
        // Validate the author-facing shape before Gson can coerce quoted values or
        // silently ignore a misspelled balance field.
        Catalog next = new Gson().fromJson(DropSchema.read(json), Catalog.class);
        require(next != null && next.version == 1, "Expected drop-table version1");
        require(next.tables != null && next.tables.length > 0 && next.tables.length <= 128, "Expected 1..128 tables");
        require(next.pools != null, "Missing pools object");
        if (next.npcOverrides == null) next.npcOverrides = new HashMap<>();
        next.byId = new HashMap<>();
        for (Map.Entry<String, PoolEntry[]> pool : next.pools.entrySet()) {
            require(pool.getValue() != null && pool.getValue().length > 0 && pool.getValue().length <= 256, "Empty/oversized pool: " + pool.getKey());
            long total = 0;
            for (PoolEntry entry : pool.getValue()) {
                require(entry != null && entry.itemId > 0 && entry.itemId < 22500 && entry.weight > 0 && entry.weight <= 1000000, "Invalid weighted item in " + pool.getKey());
                validateRawItem(entry.itemId, "pool " + pool.getKey());
                total += entry.weight;
            }
            require(total <= Integer.MAX_VALUE, "Pool weights overflow: " + pool.getKey());
        }
        for (Table table : next.tables) {
            require(table != null && table.id != null && table.id.matches("[a-z0-9_]{1,48}"), "Invalid table id");
            require(next.byId.put(table.id, table) == null, "Duplicate table: " + table.id);
            require(table.experiencePerTier >= 0 && table.experiencePerTier <= 1000000, "Invalid XP in " + table.id);
            require(table.rolls != null && table.rolls.length <= 32, "Expected at most32 rolls in " + table.id);
            for (Roll roll : table.rolls) {
                require(roll != null && roll.label != null && !roll.label.trim().isEmpty(), "Every roll needs a readable label");
                String at = table.id + " / " + roll.label;
                require(roll.chanceDenominator > 0 && roll.chanceDenominator <= 1000000 && roll.chanceNumerator >= 0 && roll.chanceNumerator <= roll.chanceDenominator, "Invalid chance: " + at);
                require(roll.abundantChanceDenominator == 0 || roll.abundantChanceDenominator >= roll.chanceNumerator && roll.abundantChanceDenominator <= 1000000, "Invalid abundant chance: " + at);
                require(roll.minQuantity > 0 && roll.maxQuantity >= roll.minQuantity && roll.maxQuantity <= 1000000 && roll.abundantQuantityMultiplier >= 1 && roll.abundantQuantityMultiplier <= 100, "Invalid quantity: " + at);
                require(roll.type != null && Arrays.asList("ITEM", "POOL", "EQUIPMENT", "BASE", "GEM", "SUPPORT_GEM", "MAP").contains(roll.type), "Unknown roll type: " + at);
                require(!"ITEM".equals(roll.type) || roll.itemId > 0 && roll.itemId < 22500, "Invalid itemId: " + at);
                if ("ITEM".equals(roll.type)) validateRawItem(roll.itemId, at);
                require(!"POOL".equals(roll.type) || next.pools.containsKey(roll.pool), "Unknown pool: " + at);
                require(roll.equipmentLevelPerTier >= 1 && roll.equipmentLevelPerTier <= 10, "Invalid equipment level multiplier: " + at);
                require(roll.minimumMapTier >= 1 && roll.minimumMapTier <= 10, "Invalid minimum map tier: " + at);
                require(roll.normalWeight >= 0 && roll.magicWeight >= 0 && roll.rareWeight >= 0 && (long)roll.normalWeight + roll.magicWeight + roll.rareWeight > 0 && (long)roll.normalWeight + roll.magicWeight + roll.rareWeight <= 1000000, "Invalid rarity weights: " + at);
                require(roll.identifiedChancePercent >= 0 && roll.identifiedChancePercent <= 100, "Invalid identified chance: " + at);
                require(!"BASE".equals(roll.type) || base(roll.baseId) != null, "Unknown equipment base: " + at);
                require(roll.gemLevelBase >= 0 && roll.gemLevelBase <= 20 && roll.gemLevelTierDivisor >= 1 && roll.gemLevelTierDivisor <= 100 && roll.supportChancePercent >= 0 && roll.supportChancePercent <= 100, "Invalid gem rule: " + at);
                // Rolled items each require their own UUID; quantities on these rows are rejected.
                require("ITEM".equals(roll.type) || "POOL".equals(roll.type) || roll.minQuantity == 1 && roll.maxQuantity == 1 && roll.abundantQuantityMultiplier == 1, "Rolled item quantities must be1: " + at);
            }
        }
        for (String id : new String[]{next.normalTable, next.eliteTable, next.bossTable}) require(next.byId.containsKey(id), "Unknown rank table: " + id);
        for (Map.Entry<String, String> override : next.npcOverrides.entrySet()) {
            require(override.getKey().matches("0|[1-9][0-9]{0,4}") && next.byId.containsKey(override.getValue()), "Invalid NPC override: " + override.getKey());
        }
        return next;
    }

    /**
     * Raw ITEM/POOL rewards cannot manufacture an ARPG base without its roll.
     * Authors must use a typed generator so UUIDs, affixes and unlock gates apply.
     * This validation reads base data only and does not require a loaded world.
     */
    private static void validateRawItem(int itemId, String where) {
        boolean needsRoll = itemId >= 22410 && itemId <= 22414;
        for (EquipmentBases.Base equipment : EquipmentBases.all()) {
            if (equipment.itemId == itemId) {
                needsRoll = true;
                break;
            }
        }
        require(!needsRoll, "Raw item " + itemId + " requires rolled identity in " + where
                + "; use BASE/EQUIPMENT, GEM/SUPPORT_GEM or MAP instead of ITEM/POOL");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    /** Resolve rank, optionally override one NPC, then make each configured roll once. */
    public static Reward roll(Context context, Random random) {
        return roll(data(), context, random);
    }

    private static Reward roll(Catalog data, Context context, Random random) {
        String id = context.boss ? data.bossTable : context.elite ? data.eliteTable : data.normalTable;
        // Tutorial rewards keep the proven default tables, unaffected by custom encounter overrides.
        if (!context.intro) id = data.npcOverrides.getOrDefault(Integer.toString(context.npcId), id);
        Table table = data.byId.get(id);
        boolean abundant = (context.mods & 4) != 0;
        List<Item> items = new ArrayList<>();
        for (Roll row : table.rolls) {
            if (row.excludeIntroduction && context.intro) continue;
            if (context.tier < row.minimumMapTier) continue;
            int itemLevel = context.intro ? 1 : context.tier * row.equipmentLevelPerTier;
            // An explicit base never bypasses its unlock level, even if minimumMapTier is mistyped.
            if (row.type.equals("BASE") && base(row.baseId).minLevel > itemLevel) continue;
            int denominator = abundant && row.abundantChanceDenominator > 0 ? row.abundantChanceDenominator : row.chanceDenominator;
            if (random.nextInt(denominator) >= row.chanceNumerator) continue;
            int quantity = row.minQuantity + random.nextInt(row.maxQuantity - row.minQuantity + 1);
            if (abundant) quantity *= row.abundantQuantityMultiplier;
            switch (row.type) {
                case "ITEM": items.add(new Item(row.itemId, quantity)); break;
                case "POOL": items.add(new Item(choose(data.pools.get(row.pool), random), quantity)); break;
                case "EQUIPMENT": items.add(equipment(row, EquipmentBases.select(random.nextBoolean(), random, itemLevel), itemLevel, random)); break;
                case "BASE": items.add(equipment(row, base(row.baseId), itemLevel, random)); break;
                case "MAP": items.add(MapRuns.map(context.tier, 0)); break;
                case "GEM":
                case "SUPPORT_GEM":
                    int level = Math.max(1, Math.min(20, row.gemLevelBase + context.tier / row.gemLevelTierDivisor));
                    boolean support = row.type.equals("SUPPORT_GEM") || random.nextInt(100) < row.supportChancePercent;
                    items.add(MapRuns.gem(level, support));
                    break;
                default: throw new AssertionError("Validated roll type changed");
            }
        }
        return new Reward(id, (long)table.experiencePerTier * context.tier, items);
    }

    /** A named base lets one monster drop a particular weapon without losing roll identity. */
    private static EquipmentBases.Base base(String id) {
        if (id == null) return null;
        for (EquipmentBases.Base candidate : EquipmentBases.all()) if (candidate.id.equals(id)) return candidate;
        return null;
    }

    private static Item equipment(Roll row, EquipmentBases.Base base, int level, Random random) {
        Item item = EquipmentBases.create(base, level);
        int ticket = random.nextInt(row.normalWeight + row.magicWeight + row.rareWeight);
        ItemRoll.Rarity rarity = ticket < row.normalWeight ? ItemRoll.Rarity.NORMAL : ticket < row.normalWeight + row.magicWeight ? ItemRoll.Rarity.MAGIC : ItemRoll.Rarity.RARE;
        boolean identified = random.nextInt(100) < row.identifiedChancePercent;
        item.setRoll(item.getRoll().changed(rarity, identified, 0, AffixCatalog.roll(item, rarity, random), null));
        AffixRules.validateGenerated(item.getRoll());
        return item;
    }

    /** Relative weights: weight20 is twice as common as weight10 within this one pool. */
    private static int choose(PoolEntry[] entries, Random random) {
        int total = 0;
        for (PoolEntry entry : entries) total += entry.weight;
        int ticket = random.nextInt(total);
        for (PoolEntry entry : entries) {
            if (ticket < entry.weight) return entry.itemId;
            ticket -= entry.weight;
        }
        throw new AssertionError("Validated weighted pool exhausted");
    }

    private MapDropTables() { }
}
