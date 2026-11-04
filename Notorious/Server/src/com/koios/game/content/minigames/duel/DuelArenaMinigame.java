package com.koios.game.content.minigames.duel;

import java.util.Map;

import com.koios.GameService;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingStatus;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class DuelArenaMinigame extends Minigame {

	private Player player, target;
	private boolean attack;
	private boolean isDone;

	public DuelArenaMinigame(Player player, Player target) {
		super(600);
		this.player = player;
		this.target = target;
		attack = false;
		isDone = false;
	}

	@Override
	public String getMinigameName() {
		return "DUEL_ARENA";
	}

	@Override
	public Map<Player, String> getPlayerList() {
		return null;
	}

	@Override
	public Map<String, Mob> getMobList() {
		return null;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player defeated, WorldObject object) {
		if (object.getID() == 3203) {
			if (defeated.getDuel().getRules()[DuelingRules.NO_FORFEIT.ordinal()]) {
				defeated.sendMessage("You may not forfeit in this duel!");
				return true;
			}

			if (defeated.getIndex() == player.getIndex()) {
				defeated = player;
				player = target;
				target = defeated;
			}

			isDone = true;
			DuelArenaHandler.duelVictory(player);
			DuelArenaHandler.duelDefeat(defeated);
		}

		return true;
	}

	@Override
	public boolean handleMobClick(Player player, Mob mob) {
		return true;
	}

	@Override
	public boolean handleButtonClick(Player player, int buttonId) {
		return false;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public Location getSpawnLocation() {
		return null;
	}

	@Override
	public Location getRespawnLocation() {
		return Location.create(3368 + NumberUtil.random(5), 3267 + NumberUtil.random(3), 0);
	}

	@Override
	public Location getExitLocation() {
		return Location.create(3368 + NumberUtil.random(5), 3267 + NumberUtil.random(3), 0);
	}

	@Override
	public MinigameLobby getLobby() {
		return null;
	}

	@Override
	public void handleLogout(Player defeated) {
		if (defeated.getIndex() == player.getIndex()) {
			defeated = player;
			player = target;
			target = defeated;
		}

		isDone = true;
		DuelArenaHandler.duelVictory(player);
		DuelArenaHandler.duelDefeat(defeated);

	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleDeath(Player player) {
		isDone = true;
		DuelArenaHandler.duelVictory(target);
		DuelArenaHandler.duelDefeat(player);
	}

	@Override
	public void startMinigame() {
		if (player == null || target == null || player.getDuel().getDuelingWith() == null || target.getDuel().getDuelingWith() == null) {
			return;
		}

		if (!GameService.getWorld().isPlayerLoggedIn(target.getName())) {
			isDone = true;

			DuelArenaHandler.duelVictory(player);

			if (target != null) {
				DuelArenaHandler.duelDefeat(target);
			}

			return;
		}

		if (!GameService.getWorld().isPlayerLoggedIn(player.getName())) {
			isDone = true;
			DuelArenaHandler.duelVictory(target);

			if (target != null) {
				DuelArenaHandler.duelDefeat(target);
			}

			return;
		}

		refillPlayer(player);
		refillPlayer(target);

		Location loc = player.getDuel().isRuleActive(DuelingRules.OBSTACLES) ? DuelArenaConstants.OBSTACLES_LOCATION : DuelArenaConstants.NON_OBSTACLES_LOCATION;

		if (player.getDuel().isRuleActive(DuelingRules.NO_MOVEMENT)) {
			player.teleport(loc.transform(NumberUtil.random(12), NumberUtil.random(6), 0));
			target.teleport(player.getLocation().transform(0, 1, 0));
		} else {
			player.teleport(loc.transform(NumberUtil.random(12), NumberUtil.random(6), 0));
			target.teleport(loc.transform(NumberUtil.random(12), NumberUtil.random(6), 0));
		}

		for (int i = 0; i < player.getDuel().getRules().length; i++) {
			if (!player.getDuel().getRules()[i] && !target.getDuel().getRules()[i]) {
				continue;
			}

			DuelingRules rule = DuelingRules.forId(i);

			if (rule.getEquipmentSlot() != -1) {
				if (rule.getEquipmentSlot() == Equipment.SLOT_SHIELD) {
					if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null && player.getEquipment().get(Equipment.SLOT_WEAPON).getDefinition().isTwoHanded()) {
						if (player.getInventory().add(player.getEquipment().get(Equipment.SLOT_WEAPON))) {
							player.getEquipment().set(Equipment.SLOT_WEAPON, null);
						}
					}

					if (target.getEquipment().get(Equipment.SLOT_WEAPON) != null && target.getEquipment().get(Equipment.SLOT_WEAPON).getDefinition().isTwoHanded()) {
						if (target.getInventory().add(target.getEquipment().get(Equipment.SLOT_WEAPON))) {
							target.getEquipment().set(Equipment.SLOT_WEAPON, null);
						}
					}
				}

				if (player.getEquipment().isSlotUsed(rule.getEquipmentSlot())) {
					if (player.getInventory().add(player.getEquipment().get(rule.getEquipmentSlot()))) {
						player.getEquipment().set(rule.getEquipmentSlot(), null);
					}
				}

				if (target.getEquipment().isSlotUsed(rule.getEquipmentSlot())) {
					if (target.getInventory().add(target.getEquipment().get(rule.getEquipmentSlot()))) {
						target.getEquipment().set(rule.getEquipmentSlot(), null);
					}
				}
			}
		}

		PlayerSaver.save(player);
		PlayerSaver.save(target);

		GameService.getTaskScheduler().schedule(new Task(1) {

			int timer = 3;

			@Override
			protected void execute() {
				if (!DuelArenaConstants.insideDuelArena(new Location(player.getX(), player.getY(), player.getZ()))
						|| !DuelArenaConstants.insideDuelArena(new Location(target.getX(), target.getY(), target.getZ()))) {
					stop();
					return;
				}

				if (timer == 3 || timer == 2 || timer == 1) {
					player.speak("" + timer + "..");
					target.speak("" + timer + "..");
				} else {
					player.speak("FIGHT!!");
					target.speak("FIGHT!!");
					attack = true;
					stop();
				}

				timer--;
			}
		});

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		GameService.getTaskScheduler().schedule(this);
	}

	@Override
	public void joinMinigame(Player player) {
	}

	@Override
	public void setLobby(MinigameLobby lobby) {
	}

	@Override
	public void damageHook(EntityCharacter attacker, EntityCharacter target, int damage) {
	}

	@Override
	public void removePlayer(Player player) {
	}

	@Override
	public void execute() {
		if (isDone) {
			stop();
			return;
		}

		Location loc = player.getDuel().isRuleActive(DuelingRules.OBSTACLES) ? DuelArenaConstants.OBSTACLES_LOCATION : DuelArenaConstants.NON_OBSTACLES_LOCATION;

		if (!DuelArenaConstants.insideDuelArena(player.getLocation())) {
			player.teleport(loc.transform(NumberUtil.random(12), NumberUtil.random(6), 0));
		}

		if (!DuelArenaConstants.insideDuelArena(target.getLocation())) {
			target.teleport(loc.transform(NumberUtil.random(12), NumberUtil.random(6), 0));
		}

		if (target == null) {
			handleLogout(target);
			return;
		}

		if (player == null) {
			handleLogout(player);
			return;
		}

		boolean targetIn = GameService.getWorld().isPlayerLoggedIn(target.getName());
		boolean playerIn = GameService.getWorld().isPlayerLoggedIn(player.getName());

		if (!targetIn || !playerIn) {

			Player left = playerIn ? target : player;

			if (left == null) {
				return;
			}

			handleLogout(left);
		}
	}

	/**
	 * Refill the player's stats for the duel.
	 *
	 * @param p
	 */
	public void refillPlayer(Player p) {
		p.getPrayers().clear();
		p.getDuel().setStatus(DuelingStatus.DUELING);
		p.getSession().send(new SendRemoveInterfacePacket());
		p.specialEnergy = 100;
		p.getItems().updateSpecialBar();
		p.getEF().handleDeath();
		p.getSkill().restoreAll();
	}

	@Override
	public boolean canLogout() {
		return false;
	}

	@Override
	public void nextWave(Object[] objects) {
	}

	@Override
	public boolean canAttack() {
		return attack;
	}

	@Override
	public boolean canUsePrayer() {
		return true;
	}

	@Override
	public boolean isOver() {
		return isDone;
	}

}
