package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * Walking packet
 */
public class PacketWalk implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		player.walkingToItem = false;
		player.countdown.reset();

		if (player.isPacketReadDisabled()) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (player.usingFillables) {
			player.walkFromFilling = true;
		}

		if (player.isSkilling[Skill.AGILITY]) {
			return;
		}

		if (player.isPlantingMithrilSeed) {
			return;
		}

		if (player.isTeleportActive()) {
			return;
		}

		if (player.settingUpCannon) {
			return;
		}

		if (!player.getController().canMove(player)) {
			return;
		}

		player.stopTask();

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}

			if (!player.getCurrentSkill().canWalk()) {
				return;
			}
		}

		player.skillCycle[Skill.HERBLORE] = false;
		player.isSkilling[Skill.FLETCHING] = false;
		player.isMining = false;
		player.clickMobType = 0;
		player.clickObjectType = 0;

		if (packet.getOpcode() == 248 || packet.getOpcode() == 164) {
			player.getMovementHandler().setForced(false);
			player.getSession().send(new SendRemoveInterfacePacket());

			if (player.getCombat().getTarget() != null) {
				Combat.resetCombat(player);
			}

			player.setFaceIndex(0, false);

			if (player.getFollowing().getInteracting() != null) {
				player.getFollowing().reset();
			}

		} else {
			player.getMovementHandler().setForced(true);
		}

		if (!player.addStarter) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		if (player.getDuel().isRuleActive(DuelingRules.NO_MOVEMENT)) {
			if (GameService.getWorld().getByUsername(player.getDuel().getDuelingWith()) != null) {
				if (!player.isWithinDistance(GameService.getWorld().getByUsername(player.getDuel().getDuelingWith()).get(), 1) || player.getCombat().getAttackTime() == 0) {
					player.sendMessage("Walking has been disabled in this duel!");
				}
			}

			player.getCombat().setTarget(null);
			return;
		}

		if (!player.getEF().canWalk(player, true)) {
			if (player.getCombat().getTarget() != null && player.getCombat().getTarget().isPlayer()
					&& GameService.getWorld().getPlayer(player.getCombat().getTarget().getIndex()) != null) {
				if (packet.getOpcode() != 98 && player.isWithinDistance(GameService.getWorld().getPlayer(player.getCombat().getTarget().getIndex()), 1)) {
					player.getCombat().setTarget(null);
					return;
				}
			}

			if (packet.getOpcode() != 98) {
				player.getCombat().setTarget(null);
			}

			return;
		}

		if (System.currentTimeMillis() - player.lastSpear < 4000) {
			player.sendMessage("You have been stunned.");
			player.getCombat().setTarget(null);
			return;
		}

		if (packet.getOpcode() == 98) {
			player.mageAllowed = true;
		}

		if (player.respawnTimer > 3) {
			return;
		}

		if (player.getBankField().isBanking()) {
			player.getBankField().setBanking(false);
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		// player.getSession().send(new ToggleWidgetLayer(false, 50097));

		if (player.currentShop > 0) {
			GameService.getShopManager().close(player);
		}

		int length = packet.getSize();

		if (packet.getOpcode() == 248) {
			length -= 14;
		}

		int steps = (length - 5) / 2;
		if(steps < 0) {
			// malformed movement packet
			return;
		}
		
		int[][] path = new int[steps][2];
		int firstStepX = packet.readLEShortA();

		for (int i = 0; i < steps; i++) {
			path[i][0] = packet.readByte();
			path[i][1] = packet.readByte();

		}

		int firstStepY = packet.readLEShort();
		packet.readByteC();

		player.getMovementHandler().reset();

		player.getMovementHandler().addToPath(new Location(firstStepX, firstStepY));

		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
			player.getMovementHandler().addToPath(new Location(path[i][0], path[i][1]));
		}

		if (steps > 0) {
			if (Math.abs(path[steps - 1][0] - player.getLocation().getX()) > 21 || Math.abs(path[steps - 1][1] - player.getLocation().getY()) > 21) {
				player.getMovementHandler().reset();
			}

		} else if (Math.abs(firstStepX - player.getLocation().getX()) > 21 || Math.abs(firstStepY - player.getLocation().getY()) > 21) {
			player.getMovementHandler().reset();
			return;
		}

		player.getMovementHandler().finish();
	}

}
