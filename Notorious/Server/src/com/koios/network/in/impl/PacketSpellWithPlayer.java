package com.koios.network.in.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Attack Player
 */
public class PacketSpellWithPlayer implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		player.getCombat().setTarget(null);

		if (!player.mageAllowed) {
			player.mageAllowed = true;
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		int index = packet.readShortA();
		int spell_id = packet.readLEShort();

		player.spellCastId = -1;
		player.spellCasting = false;

		boolean teleOther = MagicData.castOnOtherSpells(player);

		if (index < 0 || index >= Constants.MAX_PLAYERS) {
			return;
		}

		Player target = GameService.getWorld().getPlayer(index);

		if (target == null) {
			return;
		}

		if (!GameService.isOnlineMode()) {
			player.sendMessage("Casting spell @ " + target.getName() + " @ Location (" + target.getLocation().getX() + ", " + target.getLocation().getY() + ")");
			player.sendMessage("Spell ID: " + spell_id + ", Spellbook: " + player.getSpellbook().getName());
		}

		if (GameService.isOnlineMode() && target.isInattackable()) {
			player.sendMessage("Sorry, but you cannot attack this administrator right now.");
			player.getCombat().setTarget(null);
			return;
		}

		if (player.isDead() || target.isDead()) {
			return;
		}

		player.getMovementHandler().reset();

		if (spell_id == 1520 || spell_id == 18026) {
			player.getSummoning().handleAction("Attack", new Object[] { target });
			return;
		}

		if (!player.getController().canAttackPlayer(player, target)) {
			return;
		}

		player.getCombat().setTarget(target);
		player.spellCastId = spell_id;

		if (teleOther) {
			player.getCombat().setTarget(null);
			player.setFaceIndex(index, true);

		}

		for (int i = 0; i < MagicData.MAGIC_SPELLS.length; i++) {
			if (player.spellCastId == MagicData.MAGIC_SPELLS[i][0]) {
				player.spellId = i;
				player.spellCasting = true;
				break;
			}
		}

		player.autocasting = false;

		if (!teleOther) {
			for (int i = 0; i < MagicData.REDUCE_SPELLS.length; i++) {
				// reducing spells, confuse etc
				if (MagicData.REDUCE_SPELLS[i] == MagicData.MAGIC_SPELLS[player.spellId][0]) {
					if (System.currentTimeMillis() - target.reduceSpellDelay[i] < MagicData.REDUCE_SPELL_TIME[i]) {
						player.sendMessage("That player is currently immune to this spell.");
						player.spellCasting = false;
						player.getCombat().setTarget(null);
					}

					break;
				}
			}

			if (System.currentTimeMillis() - target.teleBlockTime < target.teleBlockDuration && MagicData.MAGIC_SPELLS[player.spellId][0] == 12445) {
				player.sendMessage("That player is already affected by this spell.");
				player.spellCasting = false;
				player.getCombat().setTarget(null);
			}

			if (player.spellCasting) {
				player.getCombat().setAttackType(AttackType.MAGIC);
				player.getCombat().setTarget(target);
				player.getFollowing().setFollow(target, FollowType.COMBAT);
			}
		}
	}

}