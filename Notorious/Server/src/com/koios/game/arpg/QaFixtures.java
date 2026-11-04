package com.koios.game.arpg;

import java.util.Locale;
import com.koios.GameService;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Offline fixture setup for reproducible playtests, never a public reward command.
 * All setup actions share the same offline/name/lifecycle guards before mutation.
 */
public final class QaFixtures {
    public static boolean handle(Player player, String input) {
        if (!input.startsWith("arpgqa ")) return false;
        if (GameService.isOnlineMode()
                || !player.getName().toLowerCase(Locale.ROOT).matches("qa[a-z0-9]{3,9}")) return true;
        if (player.isDead() || player.isTeleporting() || player.isTrading()
                || player.getBankField().isBanking()) return true;

        String[] args = input.split(" ");
        try {
            if (args.length == 3 && args[1].equals("level")) {
                int target = Integer.parseInt(args[2]);
                if (target < 1 || target > 30) throw new IllegalArgumentException("QA level range: 1-30");
                while (player.getArpgProgress().level() < target) {
                    player.getArpgProgress().award(player.getArpgProgress().next()
                            - player.getArpgProgress().xp());
                }
                player.sendMessage("QA character level: " + player.getArpgProgress().level());
            } else if (args.length == 3 && args[1].equals("base")) {
                int id = Integer.parseInt(args[2]);
                boolean found = false;
                for (EquipmentBases.Base base : EquipmentBases.all()) {
                    if (base.itemId == id) {
                        MapRuns.grant(player, EquipmentBases.create(base,
                                Math.max(base.minLevel, player.getArpgProgress().level())));
                        found = true;
                    }
                }
                if (!found) throw new IllegalArgumentException("Unknown equipment base");
            } else if (args.length == 2 && args[1].equals("gems")) {
                // Ordinary drops are uncut: QA must exercise right-click Craft before socketing.
                MapRuns.grant(player, MapRuns.gem(1, false));
                MapRuns.grant(player, MapRuns.gem(1, false));
                MapRuns.grant(player, MapRuns.gem(1, true));
                MapRuns.grant(player, new Item(995, 100));
            } else if (args.length == 2 && args[1].equals("materials")) {
                // Small, predictable stacks exercise ordinary Use-item recipes.
                // The registry is authoritative, so future currencies join QA too.
                for (int id : CraftingCurrency.IDS) MapRuns.grant(player, new Item(id, 5));
            }
        } catch (IllegalArgumentException failure) {
            player.sendMessage(failure.getMessage());
        }
        return true;
    }
}
