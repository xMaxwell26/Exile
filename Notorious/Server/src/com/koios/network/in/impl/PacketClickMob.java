package com.koios.network.in.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.def.MobDefinition;
import com.koios.game.map.MapRegion;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.EntityInteraction;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.task.EntityTask;

/**
 * Interact with a world entity mob.
 */
public class PacketClickMob implements IncomingPacketDecoder {

	private static final int OPCODE_ATTACK_MOB = 72;
	private static final int OPCODE_FIRST_CLICK = 155;
	private static final int OPCODE_MAGE_MOB = 131;
	private static final int OPCODE_SECOND_CLICK = 17;
	private static final int OPCODE_THIRD_CLICK = 21;

	@Override
	public void handle(Player player, GamePacket packet) {
		// Reset current target variables
		player.getCombat().setTarget(null);
		player.mobClickIndex = 0;
		player.clickMobType = 0;
		player.getFollowing().reset();

		if (player.getCurrentSkill() != null) {
			player.getCurrentSkill().setMustAbort(true);
		}

		if (packet.getOpcode() == OPCODE_ATTACK_MOB) {

			final int index = packet.readShortA();

			if (player.isBusy()) {
				return;
			}
			
			if (index < 0 || index >= MobManager.mobs.length) {
				return;
			}

			Mob mob = MobManager.mobs[index];

			if (mob == null) {
				return;
			}

			if (!player.isWithinDistance(mob, 16)) {
				return;
			}

			if (!GameService.isOnlineMode() && player.getAttribute("arpg-combat-log",false)) {
				MobDefinition definition = mob.getDefinition();
				player.sendMessage("Attacking NPC [id=" + definition.getID() + ", name='" + definition.getName() + "']");
			}

			if (mob.getMaxHitpoints() <= 0) {
				player.getCombat().setTarget(null);
				System.out.println("Mob cannot be attacked = " + mob + ", hitpoints: " + mob.getMaxHitpoints());
				return;
			}

			if (!mob.isAttackable()) {
				player.sendMessage("You can't attack this npc.");
				return;
			}

			if (!player.getController().canAttackNPC(player)) {
				return;
			}

			if (player.getSummoning().getFamiliar() != null && player.getSummoning().getFamiliar().getIndex() == mob.getIndex()) {
				return;
			}

			if (!player.getSlayer().canFight(mob)) {
				player.sendMessage("You do not have a high enough Slayer level to harm this monster.");
				player.getCombat().setTarget(null);
				return;
			}

			if (player.getMinigame().inMinigame() && !player.getMinigame().getCurrentMinigame().canAttack()) {
				player.getCombat().setTarget(null);
				return;
			}

			if (player.autocastId > 0) {
				player.autocasting = true;
			}

			if (!player.autocasting && player.spellId > 0) {
				player.spellId = 0;
			}
			
			if (CombatDistance.inCombatDistance(player, mob, player.getCombat().getAttackType(), false)) {
				player.getMovementHandler().reset();
			}

			player.spellCasting = false;

			player.getCombat().setAttackType(player.autocasting ? AttackType.MAGIC : CombatExtras.getAttackType(player));
			player.getCombat().setTarget(mob);
			player.getFollowing().setFollow(mob, FollowType.COMBAT);

		} else if (packet.getOpcode() == OPCODE_MAGE_MOB) {

			int targetIndex = packet.readLEShortA();
			int castingSpellId = packet.readShortA();

			player.getMovementHandler().reset();

			if (player.isBusy()) {
				return;
			}

			if (!player.mageAllowed) {
				player.mageAllowed = true;
				return;
			}

			if (targetIndex < 0 || targetIndex >= MobManager.mobs.length) {
				return;
			}

			Mob mob = MobManager.mobs[targetIndex];

			if (mob == null) {
				return;
			}

			if (!player.isWithinDistance(mob, 16)) {
				return;
			}

			if (!mob.isAttackable()) {
				player.sendMessage("You can't attack this npc.");
				return;
			}

			if (!player.getSlayer().canFight(mob)) {
				player.sendMessage("You do not have a high enough Slayer level to harm this monster.");
				player.getCombat().setTarget(null);
				return;
			}

			if (player.getSummoning().getFamiliar() != null && player.getSummoning().getFamiliar().getIndex() == mob.getIndex()) {
				return;
			}

			if (!GameService.isOnlineMode()) {
				int x = mob.getX();
				int y = mob.getY();

				player.sendMessage("Casting spell @ " + mob.getName() + " @ Location (" + x + ", " + y + ")");
				player.sendMessage("Spell ID: " + castingSpellId + ", Spellbook: " + player.getSpellbook().getName());
			}

			if (castingSpellId == 1520 || castingSpellId == 18026) {
				player.getSummoning().handleAction("Attack", new Object[] { mob });
				return;
			}

			for (int i = 0; i < MagicData.MAGIC_SPELLS.length; i++) {
				if (castingSpellId == MagicData.MAGIC_SPELLS[i][0]) {
					player.spellId = i;
					player.spellCasting = true;
					break;
				}
			}

			if (castingSpellId == 1171) {
				for (int mobId : Constants.UNDEAD_MOB_ID) {
					if (mob.getId() != mobId) {
						player.sendMessage("You can only attack undead monsters with this spell.");
						player.spellCasting = false;
						player.getMovementHandler().reset();
						break;
					}
				}
			}

			if (player.autocasting) {
				player.autocasting = false;
			}

			if (player.spellCasting) {
				player.getCombat().setAttackType(AttackType.MAGIC);
				player.getCombat().setTarget(mob);
				player.getFollowing().setFollow(mob, FollowType.COMBAT);
			}

		} else if (packet.getOpcode() == OPCODE_FIRST_CLICK) {

			if (player.isBusy()) {
				return;
			}

			int index = packet.readLEShort();

			if (index < 0 || index >= MobManager.mobs.length) {
				return;
			}

			Mob mob = MobManager.mobs[index];

			if (mob == null) {
				return;
			}

			if (player.getAttribute("tutorial", false) && mob.getId() != 945) {
				return;
			}

			if (!player.isWithinDistance(mob, 16)) {
				return;
			}

			final int mobId = mob.getId();
			player.mobClickIndex = index;
			player.mobType = mobId;

			if (player.isWithinDistance(MobManager.mobs[player.mobClickIndex], mob.getInteractionDistance())) {
				player.setFacePosition(MobManager.mobs[player.mobClickIndex].getX(), MobManager.mobs[player.mobClickIndex].getY());
				MobManager.mobs[player.mobClickIndex].setFacePosition(player.getX(), player.getY());
				int otherX = MobManager.mobs[player.mobClickIndex].getX();
				int otherY = MobManager.mobs[player.mobClickIndex].getY();

				if (otherX == player.getX() && otherY == player.getY()) {

					MapRegion region = MapRegion.getRegion(player.getX(), player.getY());

					if (region.canMove(player.getX(), player.getY(), player.getZ(), 1)) {
						player.getMovementHandler().walkTo(-1, 0);
					} else if (region.canMove(player.getX(), player.getY(), player.getZ(), 3)) {
						player.getMovementHandler().walkTo(1, 0);
					} else if (region.canMove(player.getX(), player.getY(), player.getZ(), 4)) {
						player.getMovementHandler().walkTo(0, -1);
					} else if (region.canMove(player.getX(), player.getY(), player.getZ(), 6)) {
						player.getMovementHandler().walkTo(0, 1);
					} else {
						player.getMovementHandler().walkTo(-1, -1);
					}
				}

				EntityInteraction.firstClickMob(player, player.mobType);
			} else {
				player.clickMobType = 1;

				player.schedule(new EntityTask<Player>(1, player, true) {

					@Override
					public void execute() {
						if (player.isDisconnected()) {
							stop();
							return;
						}

						if (player.clickMobType != 1) {
							stop();
							return;
						}

						if (player.clickMobType == 1 && MobManager.mobs[player.mobClickIndex] != null) {
							if (player.isWithinDistance(MobManager.mobs[player.mobClickIndex], MobManager.mobs[player.mobClickIndex].getInteractionDistance())) {
								player.setFacePosition(MobManager.mobs[player.mobClickIndex].getX(), MobManager.mobs[player.mobClickIndex].getY());
								MobManager.mobs[player.mobClickIndex].setFacePosition(player.getX(), player.getY());
								EntityInteraction.firstClickMob(player, player.mobType);
								stop();
							}
						}
					}

					@Override
					public void onStop() {
						player.clickMobType = 0;
					}

				});
			}
		} else if (packet.getOpcode() == OPCODE_SECOND_CLICK) {
			if (player.isBusy()) {
				return;
			}

			int index = packet.readLEShortA();

			if (index < 0 || index >= MobManager.mobs.length) {
				return;
			}

			Mob mob = MobManager.mobs[index];

			if (mob == null) {
				return;
			}

			if (!player.isWithinDistance(mob, 16)) {
				return;
			}

			int mobId = mob.getId();
			player.mobClickIndex = index;
			player.mobType = mobId;

			if (player.isWithinDistance(mob, mob.getInteractionDistance())) {
				player.setFacePosition(mob.getX(), mob.getY());
				MobManager.mobs[player.mobClickIndex].setFacePosition(player.getX(), player.getY());
				EntityInteraction.secondClickMob(player, player.mobType);
			} else {
				player.clickMobType = 2;

				player.schedule(new EntityTask<Player>(1, player, true) {

					@Override
					public void execute() {
						if (player.isDisconnected()) {
							stop();
							return;
						}

						if (player.clickMobType != 2) {
							stop();
							return;
						}

						if (player.clickMobType == 2 && MobManager.mobs[player.mobClickIndex] != null) {
							if (player.isWithinDistance(mob, mob.getInteractionDistance())) {
								player.setFacePosition(mob.getX(), mob.getY());
								mob.setFacePosition(player.getX(), player.getY());
								EntityInteraction.secondClickMob(player, player.mobType);
								stop();
							}
						}
					}

					@Override
					public void onStop() {
						player.clickMobType = 0;
					}

				});
			}
		} else if (packet.getOpcode() == OPCODE_THIRD_CLICK) {
			if (player.isBusy()) {
				return;
			}

			int index = packet.readShort();

			if (index < 0 || index >= MobManager.mobs.length) {
				return;
			}

			Mob mob = MobManager.mobs[index];

			if (mob == null) {
				return;
			}

			if (!player.isWithinDistance(mob, 16)) {
				return;
			}

			player.mobClickIndex = index;
			player.mobType = mob.getId();

			if (player.isWithinDistance(mob, mob.getInteractionDistance())) {
				player.setFacePosition(mob.getX(), mob.getY());
				mob.setFacePosition(player.getX(), player.getY());
				EntityInteraction.thirdClickMob(player, player.mobType);
			} else {
				player.clickMobType = 3;

				player.schedule(new EntityTask<Player>(1, player, true) {

					@Override
					public void execute() {
						if (player.isDisconnected()) {
							stop();
							return;
						}

						if (player.clickMobType < 3) {
							stop();
							return;
						}

						if (player.clickMobType == 3 && MobManager.mobs[player.mobClickIndex] != null) {
							if (player.isWithinDistance(mob, mob.getInteractionDistance())) {
								player.setFacePosition(mob.getX(), mob.getY());
								mob.setFacePosition(player.getX(), player.getY());
								EntityInteraction.thirdClickMob(player, player.mobType);
								stop();
							}
						}
					}

					@Override
					public void onStop() {
						player.clickMobType = 0;
					}

				});
			}
		}
	}

}
