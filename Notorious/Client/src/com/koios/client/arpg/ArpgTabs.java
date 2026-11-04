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
            ? new String[]{"Shape your character with connected passive upgrades.","Allocate nodes in the tree. Ctrl-click a leaf to refund."}
            : tab == 5
            ? new String[]{"Change how your skills fight.","Find an uncut jewel and craft the support you want.","Drag it from your satchel into a support socket in your loadout."}
            : new String[]{"Build your own fighting style with three active gem sockets.","Craft your skills from uncut jewels.","Drag gems into the loadout. Drag them back out to remove them."};
        java.util.List<String> rows=new java.util.ArrayList<>();
        for(String paragraph:description){if(!rows.isEmpty())rows.add("");rows.addAll(ChatWrap.lines(paragraph,166,font::getTextWidth));}
        int required=0;for(String row:rows)required+=row.isEmpty()?7:16;
        TextDrawingArea body=font;
        if(required>142){body=ExileTheme.small();rows.clear();for(String paragraph:description){if(!rows.isEmpty())rows.add("");rows.addAll(ChatWrap.lines(paragraph,166,body::getTextWidth));}}
        int baseline=y+52;
        for(String row:rows){if(row.isEmpty()){baseline+=7;continue;}if(baseline+3>=y+190)break;ExileTheme.label(body,x+12,baseline,row,ExileTheme.TEXT);baseline+=16;}
        ExileTheme.panel(x+8,y+198,174,30,ExileTheme.GOLD,ExileTheme.OXBLOOD);
        String action=tab==2?"Open passive tree":"Open loadout";ExileTheme.label(font,x+95-font.getTextWidth(action)/2,y+218,action,ExileTheme.HEADING);
    }

    public static String action(int tab, int mouseX, int mouseY, int x, int y) {
        if (!owns(tab) || mouseX < x + 8 || mouseX >= x + 182 || mouseY < y + 198 || mouseY >= y + 228) return null;
        return tab == 2 ? "tree" : "skills";
    }
}
