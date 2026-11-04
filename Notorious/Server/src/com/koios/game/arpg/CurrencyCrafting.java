package com.koios.game.arpg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import com.koios.game.model.item.Item;

/**
 * Pure currency recipes. This class prepares an immutable roll; it never spends
 * materials or edits inventory. PrototypePanel.craft commits only after source,
 * quantity and complete affix validation succeed.
 *
 * To add a recipe: register its ID/action/description in CraftingCurrency, add a
 * guarded case below, then test both successful output and rejection atomicity.
 * Explicit layers are PREFIX/SUFFIX; every other layer survives Scouring/Annul.
 */
public final class CurrencyCrafting {
    private static boolean explicit(ItemRoll.Affix affix) {
        return affix.getLayer() == ItemRoll.Layer.PREFIX
                || affix.getLayer() == ItemRoll.Layer.SUFFIX;
    }

    private static List<ItemRoll.Affix> implicit(ItemRoll roll) {
        List<ItemRoll.Affix> retained = new ArrayList<>();
        for (ItemRoll.Affix affix : roll.getAffixes()) {
            if (!explicit(affix)) retained.add(affix);
        }
        return retained;
    }

    private static void rarity(ItemRoll old, ItemRoll.Rarity required) {
        if (old.getRarity() != required) {
            throw new IllegalArgumentException("Requires a "
                    + required.name().toLowerCase(Locale.ROOT) + " item");
        }
    }

    /** Existing prototype maps have three modifier bits, not a full PoE affix pool. */
    private static ItemRoll reroll(Item item, ItemRoll.Rarity rarity, Random random) {
        ItemRoll old = item.getRoll();
        if (MapRuns.isMap(item)) {
            int mods = 1 + random.nextInt(7);
            if (rarity == ItemRoll.Rarity.MAGIC && Integer.bitCount(mods) > 2) {
                mods &= ~Integer.lowestOneBit(mods);
            }
            return old.changed(rarity, true, 0, old.getAffixes(),
                    new ItemRoll.MapData(old.getMap().tier, mods));
        }
        return old.changed(rarity, true, 0, AffixCatalog.roll(item, rarity, random), null);
    }

    /** Add exactly one new explicit while retaining existing values and layer limits. */
    private static ItemRoll add(Item item, ItemRoll.Rarity rarity, Random random) {
        ItemRoll old = item.getRoll();
        if (MapRuns.isMap(item)) {
            int available = (~old.getMap().mods) & 7;
            int limit = rarity == ItemRoll.Rarity.MAGIC ? 2 : 3;
            if (available == 0 || Integer.bitCount(old.getMap().mods) >= limit) {
                throw new IllegalArgumentException("No map modifier slot available");
            }
            List<Integer> bits = new ArrayList<>();
            for (int bit = 1; bit <= 4; bit <<= 1) {
                if ((available & bit) != 0) bits.add(bit);
            }
            return old.changed(rarity, true, 0, old.getAffixes(), new ItemRoll.MapData(
                    old.getMap().tier, old.getMap().mods | bits.get(random.nextInt(bits.size()))));
        }

        List<ItemRoll.Affix> existing = new ArrayList<>(old.getAffixes());
        Set<String> ids = new HashSet<>();
        int prefixes = 0, suffixes = 0;
        int limit = rarity == ItemRoll.Rarity.MAGIC ? 1 : 3;
        for (ItemRoll.Affix affix : existing) {
            ids.add(affix.getId());
            if (affix.getLayer() == ItemRoll.Layer.PREFIX) prefixes++;
            if (affix.getLayer() == ItemRoll.Layer.SUFFIX) suffixes++;
        }
        if (prefixes >= limit && suffixes >= limit) {
            throw new IllegalArgumentException("No affix slot available");
        }
        // Bounded sampling reuses the catalog's eligible tiers/weights. Existing
        // family IDs and conflicting legacy resistance groups are never duplicated.
        for (int tries = 0; tries < 128; tries++) {
            for (ItemRoll.Affix candidate : AffixCatalog.roll(item, ItemRoll.Rarity.RARE, random)) {
                boolean hasRoom = candidate.getLayer() == ItemRoll.Layer.PREFIX
                        ? prefixes < limit : suffixes < limit;
                if (!explicit(candidate) || ids.contains(candidate.getId()) || !hasRoom) continue;
                List<ItemRoll.Affix> next = new ArrayList<>(existing);
                next.add(candidate);
                ItemRoll roll = old.changed(rarity, true, 0, next, null);
                try {
                    AffixRules.validateGenerated(roll);
                    return roll;
                } catch (IllegalArgumentException conflictingFamily) {
                    // Try another eligible family; inventory has not been touched.
                }
            }
        }
        throw new IllegalArgumentException("No eligible affix available");
    }

    /** Annul removes a single explicit and deliberately never downgrades rarity. */
    private static ItemRoll annul(ItemRoll old, Random random) {
        if (old.getMap() != null) {
            List<Integer> bits = new ArrayList<>();
            for (int bit = 1; bit <= 4; bit <<= 1) {
                if ((old.getMap().mods & bit) != 0) bits.add(bit);
            }
            if (bits.isEmpty()) throw new IllegalArgumentException("No explicit modifier to remove");
            return old.changed(old.getRarity(), true, 0, old.getAffixes(), new ItemRoll.MapData(
                    old.getMap().tier, old.getMap().mods & ~bits.get(random.nextInt(bits.size()))));
        }
        List<ItemRoll.Affix> affixes = new ArrayList<>(old.getAffixes());
        List<ItemRoll.Affix> candidates = new ArrayList<>();
        for (ItemRoll.Affix affix : affixes) if (explicit(affix)) candidates.add(affix);
        if (candidates.isEmpty()) throw new IllegalArgumentException("No explicit modifier to remove");
        affixes.remove(candidates.get(random.nextInt(candidates.size())));
        return old.changed(old.getRarity(), true, 0, affixes, null);
    }

    public static ItemRoll apply(Item item, String action, Random random) {
        if (item == null || !item.hasRoll() || MapRuns.isGem(item)) {
            throw new IllegalArgumentException("Choose equipment or a map");
        }
        ItemRoll old = item.getRoll(), next;
        if (old.getCorruption() > 0 && !action.equals("identify") && !action.equals("corrupt")) {
            throw new IllegalArgumentException("Corrupted items cannot be crafted");
        }
        if (!old.isIdentified() && !action.equals("identify")) {
            throw new IllegalArgumentException("Identify this item first");
        }
        switch (action) {
            case "identify":
                if (old.isIdentified()) throw new IllegalArgumentException("Already identified");
                next = old.changed(old.getRarity(), true, old.getCorruption(),
                        old.getAffixes(), old.getMap());
                break;
            case "magic":
                rarity(old, ItemRoll.Rarity.NORMAL);
                next = reroll(item, ItemRoll.Rarity.MAGIC, random);
                break;
            case "rare":
                rarity(old, ItemRoll.Rarity.NORMAL);
                next = reroll(item, ItemRoll.Rarity.RARE, random);
                break;
            case "reroll":
                rarity(old, ItemRoll.Rarity.RARE);
                next = reroll(item, ItemRoll.Rarity.RARE, random);
                break;
            case "alteration":
                rarity(old, ItemRoll.Rarity.MAGIC);
                next = reroll(item, ItemRoll.Rarity.MAGIC, random);
                break;
            case "augmentation":
                rarity(old, ItemRoll.Rarity.MAGIC);
                next = add(item, ItemRoll.Rarity.MAGIC, random);
                break;
            case "regal":
                rarity(old, ItemRoll.Rarity.MAGIC);
                next = add(item, ItemRoll.Rarity.RARE, random);
                break;
            case "add":
                rarity(old, ItemRoll.Rarity.RARE);
                next = add(item, ItemRoll.Rarity.RARE, random);
                break;
            case "scour":
                requireRemovableRarity(old);
                next = old.changed(ItemRoll.Rarity.NORMAL, true, 0, implicit(old),
                        old.getMap() == null ? null : new ItemRoll.MapData(old.getMap().tier, 0));
                break;
            case "annul":
                requireRemovableRarity(old);
                next = annul(old, random);
                break;
            case "corrupt":
                if (old.getCorruption() >= 2) throw new IllegalArgumentException("Already twice corrupted");
                List<ItemRoll.Affix> affixes = new ArrayList<>(old.getAffixes());
                if (!MapRuns.isMap(item)) {
                    affixes.add(new ItemRoll.Affix("corruption_" + (old.getCorruption() + 1),
                            ItemRoll.Layer.CORRUPTION_IMPLICIT, 1,
                            new Modifier(Stat.LIFE, Modifier.Operation.FLAT, 5 + random.nextInt(6))));
                }
                next = old.changed(old.getRarity(), true, old.getCorruption() + 1,
                        affixes, old.getMap());
                break;
            default:
                throw new IllegalArgumentException("Unknown crafting action");
        }
        AffixRules.validateGenerated(next);
        return next;
    }

    private static void requireRemovableRarity(ItemRoll roll) {
        if (roll.getRarity() != ItemRoll.Rarity.MAGIC && roll.getRarity() != ItemRoll.Rarity.RARE) {
            throw new IllegalArgumentException("Requires a magic or rare item");
        }
    }

    private CurrencyCrafting() {}
}
