package com.koios.game.content.questtab.impl;

import com.koios.game.arpg.PrototypePanel;
import com.koios.game.content.questtab.QuestPage;
import com.koios.game.model.player.Player;

/** Local prototype navigation; Exile does not publish legacy forum/store URLs. */
public final class HelpQuestPage extends QuestPage {
    private static final int FIRST_BUTTON = 129003;
    private static final String[] LABELS = {"Guide", "Maps", "Crafting", "Passives", "Help"};
    private static final String[] ACTIONS = {"guide", "maps", "craft", "tree", "guide"};

    @Override
    public void write(Player player) {
        writeLine(player, 1, "[Exile in-game services]");
        for (int index = 0; index < LABELS.length; index++) {
            writeLine(player, index + 2, "@red@" + LABELS[index]);
        }
        writeLine(player, LABELS.length + 3, "[Back to guide]");
    }

    @Override
    public boolean buttonClick(Player player, int buttonId) {
        int index = buttonId - FIRST_BUTTON;
        if (index >= 0 && index < ACTIONS.length) {
            PrototypePanel.handle(player, "arpgproto " + ACTIONS[index]);
            return true;
        }
        if (index == ACTIONS.length + 1) {
            PrototypePanel.handle(player, "arpgproto guide");
            return true;
        }
        return false;
    }

    @Override
    public String title() {
        return "Exile Guide";
    }
}
