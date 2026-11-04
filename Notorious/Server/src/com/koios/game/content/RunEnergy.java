package com.koios.game.content;

import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendEnergyPacket;
import com.koios.network.out.impl.SendMessagePacket;
import com.koios.task.Task;

/**
 * Handles resting and restoring energy
 *
 * @author Michael Sasse
 */
public class RunEnergy {

	public static final int RESTORE_TIMER = 2;
	public static final int REST_ANIMATION = 11786;
	public static final int STAND_UP_ANIMATION = 11788;
	private double energy = 100.0D;
	private final int weight = 0;
	private boolean allowed = true;
	private boolean running = false;
	private boolean resting = false;
	private final Player player;

	public RunEnergy(Player player) {
		this.player = player;
	}

	/**
	 * Toggles the resting to start the process
	 */
	public void toggleResting() {

		resting = !resting;
		if (resting) {
			player.playAnimation(new Animation(11786));
			player.standAnim = 11787;
			player.getMovementHandler().reset();
		} else {
			player.playAnimation(new Animation(11788));
			player.standAnim = -1;
			player.getSession().send(new SendConfigPacket(765, 0));
		}

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Deducts energy from the players energy
	 *
	 * @param amount The amount to deduct
	 */
	public void deduct(int amount) {
		if (GameService.isOnlineMode()) {
			energy -= amount;

			if (energy < 0.0D) {
				energy = 0.0D;
			}

			update();
		}
	}

	/**
	 * Deducts energy from the player by percentage
	 *
	 * @param percent The percentage to remove
	 */
	public void deduct(double percent) {
		if (GameService.isOnlineMode()) {
			energy -= (int) (energy * percent);

			if (energy < 0.0D) {
				energy = 0.0D;
			}

			update();
		}
	}

	/**
	 * Adds energy to the players current energy
	 *
	 * @param amount The amount to add
	 */
	public void add(int amount) {
		energy += amount;

		if (energy > 100.0D) {
			energy = 100.0D;
		}

		update();
	}

	/**
	 * Restores the players energy
	 */
	public void restoreAll() {
		energy = 100.0D;
		update();
	}

	/**
	 * Updates the players energy client sided
	 */
	public void update() {
		player.getSession().send(new SendEnergyPacket());
	}

	/**
	 * Handles the energy tick as well as resting
	 */
	public void tick() {
		GameService.getTaskScheduler().schedule(new Task(4) {

			@Override
			public void execute() {
				if (allowed && energy < 100.0D) {
					RunEnergy en = RunEnergy.this;
					en.energy = en.energy + (resting ? 5.0D : (1.0D + player.getSkill().getLevelForExperience(Skill.AGILITY) * 0.011D) * 1.0D);

					if (energy > 100.0D) {
						energy = 100.0D;
					}

					update();
				} else if (!allowed) {
					allowed = true;
				}
				/*
				 * No need to turn off resting as thats not how it works.. or is this because u want to stop the event?
				 */
				if (resting && energy == 100.0D) {
					// toggleResting();
					stop();
				}
			}

			@Override
			public void onStop() {
			}
		});
	}

	/**
	 * Resets the player running
	 */
	public void reset() {
		running = false;
		allowed = true;
	}

	/**
	 * Handles what happens when a player is running
	 */
	public void onRun() {
		if (player.getSkill().getLevelForExperience(Skill.AGILITY) == 99) {
			return;
		}

		if (GameService.isOnlineMode()) {
			energy -= (1.0D - player.getSkill().getLevelForExperience(Skill.AGILITY) * 0.005D) * 1.0D
					* (player.getEquipment().get(Equipment.SLOT_BOOTS) != null && player.getEquipment().get(Equipment.SLOT_BOOTS).getId() == 88 ? 0.8 : 1.0);
			update();
			allowed = false;

			if (energy <= 0.0D) {
				energy = 0.0D;
				player.getSession().send(new SendConfigPacket(173, 0));
				player.getSession().send(new SendMessagePacket("You have run out of energy."));
				running = false;
			}
		}
	}

	/**
	 * Gets if a player can run
	 *
	 * @return
	 */
	public boolean canRun() {
		return energy > 0.0D;
	}

	@Override
	public String toString() {
		return "RunEnergy [energy=" + energy + ", weight=" + weight + "]";
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int getEnergy() {
		return (int) energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;

		if (this.energy > 100.0D) {
			this.energy = 100.0D;
		}
	}

	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	public boolean isResting() {
		return resting;
	}

}
