package com.koios.game.arpg;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;
/** Local-player presentation of server-confirmed actions; never simulates damage. */
public final class CombatVisuals {
 public static void send(Player player,int kind,EntityCharacter from,EntityCharacter to,int value,boolean critical){player.getSession().send(new SendStringPacket(kind+"|"+from.getX()+"|"+from.getY()+"|"+to.getX()+"|"+to.getY()+"|"+Math.max(0,value)+"|"+(critical?1:0),65525));}
}
