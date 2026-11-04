package com.koios.client.arpg;

import com.koios.client.graphic.*;

/** ARPG destinations in the former quest, prayer and spellbook slots. */
public final class ArpgTabs {
    public static boolean owns(int tab) { return tab == 2 || tab == 5 || tab == 6; }

    public static void draw(TextDrawingArea font, int tab, int x, int y, String[] skills) {
        String title = tab == 2 ? "PASSIVE TREE" : tab == 5 ? "SUPPORTS" : "ACTIVE SKILLS";
        ExileTheme.panel(x,y,190,260,ExileTheme.BORDER,ExileTheme.PANEL);ExileTheme.label(font,x+12,y+22,title,ExileTheme.HEADING);
        DrawingArea.drawPixels(2, y + 31, x + 12, ExileTheme.CRIMSON, 166);
        String[] description = tab == 2
            ? new String[]{"Shape your character with", "connected passive upgrades.", "", "Allocate nodes in the tree.", "Ctrl-click a leaf to refund."}
            : tab == 5
            ? new String[]{"Modify your active skills.", "", "Splash", "Faster Attacks", "Brutality", "", "Drag supports onto skills", "in your loadout panel."}
            : new String[]{"Choose three active skills.", "", "Heavy Strike", "Cleave", "Whirlwind and more", "", "Drag skills to change slots", "in your loadout panel."};
        for (int i = 0; i < description.length; i++)ExileTheme.label(font,x+12,y+52+i*16,description[i],description[i].isEmpty()?ExileTheme.DIM:ExileTheme.TEXT);
        ExileTheme.panel(x+8,y+198,174,30,ExileTheme.GOLD,ExileTheme.OXBLOOD);
        ExileTheme.label(font,x+22,y+218,tab == 2 ? "OPEN PASSIVE TREE" : "OPEN LOADOUT",ExileTheme.HEADING);
    }

    public static String action(int tab, int mouseX, int mouseY, int x, int y) {
        if (!owns(tab) || mouseX < x + 8 || mouseX >= x + 182 || mouseY < y + 198 || mouseY >= y + 228) return null;
        return tab == 2 ? "tree" : "skills";
    }
}
