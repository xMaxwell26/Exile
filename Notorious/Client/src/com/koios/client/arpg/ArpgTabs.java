package com.koios.client.arpg;

import com.koios.client.graphic.*;

/** ARPG destinations in the former quest, prayer and spellbook slots. */
public final class ArpgTabs {
    public static boolean owns(int tab) { return tab == 2 || tab == 5 || tab == 6; }

    public static void draw(TextDrawingArea font, int tab, int x, int y, String[] skills) {
        String title = tab == 2 ? "Passive tree" : tab == 5 ? "Support gems" : "Active skills";
        ExileTheme.panel(x,y,190,260,ExileTheme.BORDER,ExileTheme.PANEL);ExileTheme.heading(x+12,y+23,title,ExileTheme.HEADING);
        DrawingArea.drawPixels(2, y + 31, x + 12, ExileTheme.CRIMSON, 166);
        String[] description = tab == 2
            ? new String[]{"Shape your character with", "connected passive upgrades.", "", "Allocate nodes in the tree.", "Ctrl-click a leaf to refund."}
            : tab == 5
            ? new String[]{"Change how your skills fight.", "", "Find an uncut jewel.", "Craft the support you want.", "", "Drag it from your satchel", "into a support socket", "in your loadout."}
            : new String[]{"Build your own fighting style.", "", "Three active gem sockets.", "Choose and craft your skills.", "", "Drag gems from your satchel", "into the loadout. Drag them", "back out to remove them."};
        for (int i = 0; i < description.length; i++)ExileTheme.label(font,x+12,y+52+i*16,description[i],description[i].isEmpty()?ExileTheme.DIM:ExileTheme.TEXT);
        ExileTheme.panel(x+8,y+198,174,30,ExileTheme.GOLD,ExileTheme.OXBLOOD);
        String action=tab==2?"Open passive tree":"Open loadout";ExileTheme.label(font,x+95-font.getTextWidth(action)/2,y+218,action,ExileTheme.HEADING);
    }

    public static String action(int tab, int mouseX, int mouseY, int x, int y) {
        if (!owns(tab) || mouseX < x + 8 || mouseX >= x + 182 || mouseY < y + 198 || mouseY >= y + 228) return null;
        return tab == 2 ? "tree" : "skills";
    }
}
