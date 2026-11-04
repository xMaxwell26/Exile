package com.koios.network.in.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.dialogue.impl.admin.RottenPotatoDialogue;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.duel.DuelArenaHandler;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.minigames.prophunt.PropHunt;
import com.koios.game.model.item.container.Trade;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.RequestManager.RequestState;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

public class PacketSelectPlayerOption implements IncomingPacketDecoder {

	private void attack(Player player, Player target) {
		player.getMovementHandler().reset();

		if (player.getMinigame().inMinigame() && !player.getMinigame().getCurrentMinigame().canAttack()) {
			return;
		}

		if (target.isInattackable()) {
			player.sendMessage("Sorry, but this administrator is currently not attackable.");
			return;
		}

		if (!player.getController().canAttackPlayer(player, target)) {
			return;
		}

		if (player.autocastId > 0) {
			player.autocasting = true;
		}

		if (!player.autocasting && player.spellId > 0) {
			player.spellId = 0;
		}

		player.spellCasting = false;
		AttackType attack_type = player.autocasting ? AttackType.MAGIC : CombatExtras.getAttackType(player);

		if (player.getDuel().isDueling()) {
			if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
				if (player.getDuel().isRuleActive(DuelingRules.FUN_WEPAONS_ONLY)) {
					boolean canUseWeapon = false;

					for (int funWeapon : Constants.FUN_WEAPONS) {
						if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == funWeapon) {
							canUseWeapon = true;
						}
					}

					if (!canUseWeapon) {
						player.sendMessage("You can only use fun weapons in this duel!");
						return;
					}
				}
			}

			if (player.getDuel().isRuleActive(DuelingRules.NO_RANGED) && attack_type == AttackType.RANGE) {
				player.sendMessage("Range has been disabled in this duel!");
				return;
			}

			if (player.getDuel().isRuleActive(DuelingRules.NO_MELEE) && attack_type == AttackType.MELEE) {
				player.sendMessage("Melee has been disabled in this duel!");
				return;
			}

			if (player.getDuel().isRuleActive(DuelingRules.NO_MAGIC) && attack_type == AttackType.MAGIC) {
				player.sendMessage("Magic has been disabled in this duel!");
				return;
			}
		}
		
		if (CombatDistance.inCombatDistance(player, target, player.getCombat().getAttackType(), false)) {
			player.getMovementHandler().reset();
		}

		player.getCombat().setTarget(target);
		player.getCombat().setAttackType(attack_type);
		player.getFollowing().setFollow(target, FollowType.COMBAT);
	}

	private void challenge(Player player, Player target) {
		if (player.inArena()) {
			player.sendMessage("You cannot request a challenge inside the Duel Arena!");
			return;
		}

		if (player.isDead() || player.getHitpoints() <= 0 || target.isDead() || target.getHitpoints() <= 0) {
			return;
		}

		DuelArenaHandler.challengePlayer(player, target);
	}

	private void follow(Player player, Player target) {
		player.setFaceIndex(target.getIndex(), true);
		player.getFollowing().setFollow(target, FollowType.DEFAULT);
	}

	@Override
	public void handle(Player player, GamePacket packet) {
		int targetId;
		int selectOption;

		// Reset all current happenings
		player.getCombat().setTarget(null);
		player.getFollowing().reset();
		player.getSession().send(new SendRemoveInterfacePacket());

		if (player.getCurrentSkill() != null) {
			player.getCurrentSkill().setMustAbort(true);
		}
		// Decode target id and set selection type.

		switch (packet.getOpcode()) {
		case 139: // "Trade With"
			targetId = packet.readLEShort();
			selectOption = 5;
			break;

		case 73: // "Attack" AND "Challenge"
			targetId = packet.readLEShort();
			selectOption = 3;
			break;

		case 128: // "Someone wishes to duel with you."
			targetId = packet.readShort();
			selectOption = 3;
			break;

		case 39: // "Follow"
			targetId = packet.readLEShort();
			selectOption = 4;
			break;

		default:
			throw new AssertionError(String.valueOf(packet.getOpcode()));
		}

		/* Packet content validation start -- no responsive code below */

		if (!player.hasFocus()) {
			// Game applet must be in focus to interact with someone.
			return;
		}

		if (targetId == 2047 || targetId == player.index()) {
			// The 2047 index is internally in the client used to represent
			// "self".
			player.sendMessage("Nothing interesting happens.");
			return;
		}

		if (targetId < 0 || targetId >= Constants.MAX_PLAYERS) {
			// Malformed target id.
			return;
		}

		Player target = GameService.getWorld().getPlayer(targetId);

		if (target == null || target.isDead()) {
			// Target not available.
			return;
		}

		if (!player.isWithinDistance(target, 16)) {
			// Target is out of reach.
			return;
		}

		/* Packet content validation end -- below is safe */

		switch (selectOption) {
		case 3: // Attack and Challenge
			if ("Attack".equals(player.atPlayerActions[2])) {
				attack(player, target);
			} else if ("Challenge".equals(player.atPlayerActions[2])) {
				challenge(player, target);
			} else if ("Rotten Potato".equals(player.atPlayerActions[2])) {
				player.dialogue().start(new RottenPotatoDialogue(player, 5, target));
			} else if ("Shoot".equals(player.atPlayerActions[2])) {
				PropHunt.shootTarget(player, target);
			}
			break;

		case 4: // Follow
			if ("Follow".equals(player.atPlayerActions[4])) {
				follow(player, target);
			}
			break;

		case 5: // Trade With
			if (target == player) {
				return;
			}
			if (player.inWild()) {
				player.sendMessage("You can't trade inside the wilderness.");
				return;
			}
			if (player.getMinigame().inMinigameLobby() && !player.getMinigame().getCurrentLobby().canTrade()) {
				player.sendMessage("You can't trade inside this lobby!");
				return;
			}

			if (player.inArena()) {
				player.sendMessage("You cannot trade in the arena.");
				return;
			}

			if (!player.getController().canTrade(player)) {
				return;
			}

			if (!"Trade With".equals(player.atPlayerActions[3])) {
				return;
			}

			player.setFaceIndex(target.getIndex(), true);
			if (target.getRequestManager().getAcquaintance() == player) {
				player.getRequestManager().setAcquaintance(target);
				target.getRequestManager().setAcquaintance(player);
				Trade.openFirstInterface(new Player[] { player, target });
			} else if (target.getRequestManager().getState() != RequestState.PARTICIPATING) {
				player.sendMessage("Sending trade request...");
				target.sendMessage(player.getName() + ":tradereq:");
				player.getRequestManager().setState(RequestState.REQUESTED);
				player.getRequestManager().setAcquaintance(target);
			} else {
				player.sendMessage("That player is currently busy.");
			}
		}
	}

}
