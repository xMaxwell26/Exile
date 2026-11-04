package com.koios.client.arpg;

import com.koios.client.graphic.*;

/** ARPG destinations in the former quest, prayer and spellbook slots. */
public final class ArpgTabs {
    public static boolean owns(int tab) { return tab == 2 || tab == 5 || tab == 6; }

    public static void draw(TextDrawingArea font, int tab, int x, int y, String[] skills) {
        String title = tab == 2 ? "PASSIVE TREE" : tab == 5 ? "SUPPORTS" : "ACTIVE SKILLS";
        font.method389(true, x + 8, 0xDDC58C, title, y + 20);
        DrawingArea.drawPixels(1, y + 30, x + 8, 0x39434B, 174);
        String[] description = tab == 2
            ? new String[]{"Shape your character with", "connected passive upgrades.", "", "Allocate nodes in the tree.", "Ctrl-click a leaf to refund."}
            : tab == 5
            ? new String[]{"Modify your active skills.", "", "Splash", "Faster Attacks", "Brutality", "", "Drag supports onto skills", "in your loadout panel."}
            : new String[]{"Choose skills for Q / W / E.", "", "Heavy Strike", "Cleave", "Whirlwind", "", "Drag skills to change slots", "in your loadout panel."};
        for (int i = 0; i < description.length; i++)
            font.method389(true, x + 8, 0xB8C5CE, description[i], y + 50 + i * 16);
        DrawingArea.drawPixels(30, y + 198, x + 8, 0x293742, 174);
        DrawingArea.fillPixels(x + 8, 174, 30, 0x9F8B62, y + 198);
        font.method389(true, x + 22, 0xDDC58C, tab == 2 ? "Open passive tree" : "Open skills & supports", y + 218);
    }

    public static String action(int tab, int mouseX, int mouseY, int x, int y) {
        if (!owns(tab) || mouseX < x + 8 || mouseX >= x + 182 || mouseY < y + 198 || mouseY >= y + 228) return null;
        return tab == 2 ? "tree" : "skills";
    }
}
