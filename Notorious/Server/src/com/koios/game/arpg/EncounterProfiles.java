package com.koios.game.arpg;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import com.google.gson.Gson;

/**
 * Map-only enemy balance. Edit data/arpg/enemies.json, then restart the server.
 * The first seven entries define roles0..6. Optional entries after them target
 * one themed NPC ID, so a Foundry variant never changes its Courtyard counterpart.
 */
public final class EncounterProfiles {
    public static final class Profile {
        // npcId=0 means the default role. Overrides must match their ID's role.
        public int role, npcId, attackTicks, reach;
        // -1 uses the aliased NPC's proven default. Custom rigs must verify a sequence in native QA.
        public int attackAnimation = -1;
        // Delay from visible swing start to conditional impact; cadence still includes these ticks.
        public int windupTicks;
        public String id;
        public double healthMultiplier, damageBase, damageGrowth;
        public DamageType damageType;

        public int health(int tier, boolean boss) {
            int baseline = boss ? 120 + 35 * tier : 22 + 8 * tier;
            return Math.max(1, (int) Math.round(baseline * healthMultiplier));
        }

        public int damage(int tier) {
            return Math.max(1, (int) Math.ceil(damageBase + damageGrowth * tier));
        }
    }

    private static volatile Profile[] profiles;

    /** Stable default-role API: existing seven-entry files and callers still work. */
    public static Profile get(int role) {
        if (profiles == null) load();
        if (role < 0 || role >= 7) throw new IllegalArgumentException("Unknown encounter role");
        return profiles[role];
    }

    /** Resolve a narrow per-NPC override, otherwise use the unchanged role default. */
    public static Profile get(int role, int npcId) {
        Profile fallback = get(role);
        for (int index = 7; index < profiles.length; index++) {
            Profile candidate = profiles[index];
            if (candidate.npcId == npcId && candidate.role == role) return candidate;
        }
        return fallback;
    }

    private static synchronized void load() {
        if (profiles != null) return;
        try {
            // Test/content tools can validate staged data without replacing live JSON.
            String path = System.getProperty("arpg.enemies.path", "data/arpg/enemies.json");
            Profile[] next = new Gson().fromJson(new String(Files.readAllBytes(Paths.get(path)),
                    StandardCharsets.UTF_8), Profile[].class);
            if (next == null || next.length < 7 || next.length > 32) {
                throw new IllegalArgumentException("Seven base encounter roles required");
            }
            Set<Integer> overrides = new HashSet<>();
            for (int index = 0; index < next.length; index++) {
                Profile candidate = next[index];
                if (candidate == null) throw new IllegalArgumentException("Missing encounter profile");
                boolean identityValid = index < 7
                        ? candidate.role == index && candidate.npcId == 0
                        : candidate.role >= 0 && candidate.role <= 5
                          && candidate.npcId >= 15000 && candidate.npcId < 15012
                          && (candidate.npcId - 15000) % 6 == candidate.role
                          && overrides.add(candidate.npcId);
                if (!identityValid || candidate.id == null || candidate.id.isEmpty()
                        || candidate.damageType == null
                        || !Double.isFinite(candidate.healthMultiplier)
                        || candidate.healthMultiplier < .25 || candidate.healthMultiplier > 3
                        || !Double.isFinite(candidate.damageBase)
                        || !Double.isFinite(candidate.damageGrowth)
                        || candidate.damageBase < 0 || candidate.damageGrowth < 0
                        || candidate.damageBase > 100 || candidate.damageGrowth > 20
                        || candidate.attackTicks < 2 || candidate.attackTicks > 20
                        || candidate.reach < 1 || candidate.reach > 6
                        || candidate.attackAnimation < -1 || candidate.attackAnimation > 32767
                        || candidate.windupTicks < 0 || candidate.windupTicks >= candidate.attackTicks
                        || candidate.windupTicks > 0 && candidate.attackAnimation < 0) {
                    throw new IllegalArgumentException("Invalid encounter profile " + index);
                }
            }
            // Publish only after the entire file passes validation.
            profiles = next;
        } catch (Exception failure) {
            throw new IllegalStateException("Cannot load encounter profiles", failure);
        }
    }

    private EncounterProfiles() {}
}
