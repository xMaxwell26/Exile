package com.koios.game.arpg;
import com.koios.game.model.player.*;import com.koios.game.model.UpdateFlags.UpdateFlag;
public final class PrototypeAppearance {public static void migrate(Player p){if(!p.getAttribute("starter-claimed",false)||p.getAttribute("appearance-cleanup",false))return;Appearance a=p.getAppearance();if(a.getHairColour()==7&&a.getTorsoColour()==8&&a.getLegColour()==9&&a.getFeetColour()==5){a.setLook(10,2);p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);}p.setAttribute("appearance-cleanup",true);}}
