package com.koios.game.content.teleport;

import com.koios.Constants;
import com.koios.game.content.activity.PestControl;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.minigames.barrows.BarrowsData;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.content.teleport.Teleport.TeleportPlaces;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendShakeScreen;
import com.koios.task.EntityTask;

/**
 * Handles all forms of teleporting (excluding things like glider and boat)
 * 
 * @author Wolfs Darker
 * @author Timothy Howard <Arithium>
 */
public class TeleportExecutor {

	/**
	 * Checks if the entity can teleport under the current circumstances
	 * 
	 * @param entity The {@link EntityCharacter} attempting to teleport
	 * @param teleport The {@link Teleport} type
	 * @return If the entity can teleport
	 */
	public static boolean canTeleport(EntityCharacter entity, Teleport teleport, TeleportType type) {

		if (type.equals(TeleportType.UNCHECKED)) {
			return true;
		}

		if (entity.isPlayer()) {
			Player player = entity.toPlayer();

			if (player.isTeleportActive()) {
				// prevent the player from teleporting while already performing
				// a teleport
				return false;
			}

			if (player.getMinigame().inMinigameLobby() && !player.getMinigame().getCurrentLobby().canTeleport()) {
				player.sendMessage("You can't teleport out of this lobby!");
				return false;
			}

			if (player.getMinigame().inMinigame() && !player.getMinigame().getCurrentMinigame().canTeleport()) {
				player.sendMessage("You can't teleport during this minigame!");
				return false;
			}

			if (FightPits.getInstance().WAITING_PLAYERS.contains(player)) {
				player.sendMessage("You can not teleport while in the waiting room!");
				return false;
			}

			if (FightPits.getInstance().isParticipant(player)) {
				player.sendMessage("You can not teleport while in a game of fight pits!");
				return false;
			}

			if (player.isTrading()) {
				player.sendMessage("You can't teleport during a trade!");
				return false;
			}

			if (type.equals(TeleportType.REGULAR) && player.inWild() && player.getWildyLevel() > Constants.NO_TELEPORT_WILD_LEVEL && player.getX() != 3067
					&& player.getY() != 10253) {
				player.sendMessage("You can't teleport above level " + Constants.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
				return false;
			}

			if (System.currentTimeMillis() - player.teleBlockTime < player.teleBlockDuration) {
				player.sendMessage("You are teleblocked and can't teleport.");
				return false;
			}

			if (PestControl.isInGame(player)) {
				player.sendMessage("You can't teleport during this mini-game!");
				return false;
			}

			if (player.isSkilling[Skill.AGILITY]) {
				// player.sendMessage("You can't teleport while completing an
				// obstacle!");
				return false;
			}

			if (PestControl.isInPestControlBoat(player)) {
				PestControl.leaveWaitingBoat(player);
			}

			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().canAbort()) {
					player.getCurrentSkill().setMustAbort(true);
				}

				if (!player.getCurrentSkill().canTeleport()) {
					return false;
				}
			}

			if (!player.getController().canTeleport(player)) {
				return false;
			}

			player.getSession().send(new SendRemoveInterfacePacket());
			Combat.resetCombat(player);
			player.getMovementHandler().reset();
			player.getCombat().setTarget(null);
			player.setFaceIndex(0, false);

			if (BarrowsData.isInBarrowsTomb(player.getLocation())) {
				player.getSession().send(new SendShakeScreen(2, 0, 0, 0));
			}

			if (player.getMinigame().inMinigameLobby()) {
				player.getMinigame().getCurrentLobby().handleTeleport(player);
			}

			player.resetGodwarsKc();
		}

		return true;
	}

	public static boolean startTeleportation(EntityCharacter entity, Location location, TeleportModes teleport) {
		return startTeleportation(entity, new Teleport(location, teleport), TeleportType.REGULAR);
	}

	public static boolean startTeleportation(EntityCharacter entity, TeleportPlaces place, TeleportModes teleport) {
		return startTeleportation(entity, new Teleport(place.getLocation(), teleport), TeleportType.REGULAR);
	}

	public static boolean startTeleportation(EntityCharacter entity, Teleport teleport) {
		return startTeleportation(entity, teleport, TeleportType.REGULAR);
	}

	/**
	 * Performs a teleporation for the {@link CharacterEntity}
	 * 
	 * @param entity The {@link CharacterEntity} attempting to teleport
	 * @param teleport The {@link Teleport} with the type of teleport
	 * @param type The {@link TeleportType} of the teleport
	 * @return
	 */
	public static boolean startTeleportation(final EntityCharacter entity, Teleport teleport, TeleportType type) {

		/* Check if the entity can teleport */
		if (!canTeleport(entity, teleport, type)) {
			return false;
		}

		/* Reset the entity in combat */
		if (entity.getCombat().getAggressor() != null) {
			Combat.resetCombat(entity.getCombat().getAggressor());
		}

		/* The animation performed when starting the teleport */
		final Animation startAnimation = teleport.getTravelMode().getStartAnim();

		/* The animation performed when ending the teleport */
		final Animation endAnimation = teleport.getTravelMode().getEndAnim();

		/* The graphic performed when starting the teleport */
		final Graphic startGraphic = teleport.getTravelMode().getStartGfx();

		/* The graphic performed when ending the teleport */
		final Graphic endGraphic = teleport.getTravelMode().getEndGfx();

		/* If start animation isn't null, perform it */
		if (startAnimation != null)
			entity.playAnimation(startAnimation);

		/* If start graphic isn't null, perform it */
		if (startGraphic != null)
			entity.playGraphics(startGraphic);

		/* Set the entity to teleporting */
		entity.setTeleportActive(true);

		/* The total time it will take for the teleport to complete */
		final int initialDelay = teleport.getTravelMode().getTimer() + teleport.getTravelMode().getEndTimer();

		/* Submits the task for the teleport */
		entity.getTaskScheduler().schedule(teleportTask(initialDelay, entity, teleport, endGraphic, endAnimation));

		return true;
	}

	/**
	 * Creates a new {@Link EntityTask} to handle teleporting
	 * 
	 * @param initialDelay The total time before the teleport ends
	 * @param entity The {@link EntityCharacter} teleporting
	 * @param teleport The {@link Teleport} holding the information for teleporting
	 * @param endGraphic The {@link Graphic} to perform when the entity teleports
	 * @param endAnimation The {@link Animation} to perform when the entity teleports
	 * @return A new {@Link EntityTask} to teleport
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static EntityTask teleportTask(int initialDelay, EntityCharacter entity, Teleport teleport, Graphic endGraphic, Animation endAnimation) {
		return new EntityTask(1, entity) {

			int delay = initialDelay;

			@Override
			protected void execute() {
				delay--;
				if (delay == (initialDelay - teleport.getTravelMode().getTimer())) {
					entity.teleport(teleport.getLocation());

					if (endAnimation != null) {
						entity.playAnimation(endAnimation);
					}

					if (endGraphic != null) {
						entity.playGraphics(endGraphic);
					}
				}

				if (delay == 0) {
					entity.setTeleportActive(false);
					stop();
				}
			}

		};

	}

}
