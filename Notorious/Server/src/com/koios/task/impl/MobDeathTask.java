package com.koios.task.impl;

import com.koios.GameService;
import com.koios.game.content.minigames.barrows.BarrowsData;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class MobDeathTask extends Task {

	private final Mob mob;
	private EntityCharacter aggressor;

	public MobDeathTask(Mob mob, EntityCharacter aggressor) {
		this.mob = mob;
		this.mob.setDead(true);
		this.mob.deathTask = true;
		this.aggressor = aggressor;
		this.mob.deathStage = 0;
	}

	public MobDeathTask(Mob mob, int stage) {
		this.mob = mob;
		this.mob.setDead(true);
		this.mob.deathTask = true;
		this.mob.deathStage = stage;
	}

	@Override
	protected void execute() {
		if (mob == null) {
			stop();
			return;
		}

		if (mob.actionTimer > 0) {
			mob.actionTimer--;
		}

		if (mob.actionTimer <= 0) {
			switch (mob.deathStage) {
			case 0:
				mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				mob.setFaceIndex(0, false);
				mob.playAnimation(new Animation(MobManager.getDeadEmote(mob.getId())));
				mob.getEF().handleDeath();
				mob.actionTimer = MobManager.getDeadTime(mob.getId()); // delete
				mob.deathStage = 1;
				if (aggressor != null) {
					aggressor.getCombat().setAggressor(null);
					aggressor.getCombat().setTarget(null);
				}
				break;

			case 1:
				mob.actionTimer = MobManager.getRespawnTime(mob.getId()); // respawn

				mob.death();
				mob.hitpoints = mob.maximumHitpoints;
				mob.playAnimation(new Animation(0x328));

				if (mob.getId() >= 2440 && mob.getId() <= 2446) {
					GameService.getObjectManager().removeAt(mob.getX(), mob.getY());
				}

				mob.deathStage = 2;
				mob.getLocation().setAs(mob.homeLocation);

				if (mob.getId() == 1158 || mob.getId() == 1160) {
					if (mob.getId() == 1158) {
						MobManager.spawnSecondForm(mob.getX(), mob.getY(), mob.getZ());
					} else if (mob.getId() == 1160) {
						MobManager.spawnFirstKalphiteForm(3478, 9498, 0);
					}

					mob.remove();
					stop();
					return;
				}

				/**
				 * if summoned, kill it off now since we don't need to respawn it
				 */
				Player player = mob.summoner != null && mob.summoner.isPlayer() ? mob.summoner.toPlayer() : null;

				if (player != null || mob.inZombiesGame() || BarrowsData.isInBarrowsTomb(mob.getLocation()) || BarrowsData.inBarrows(mob.getLocation())
						|| mob.getId() >= 6142 && mob.getId() <= 6145 || mob.isFromMinigame()) {
					mob.remove();
					stop();
					return;
				}
				break;

			case 2:
				if (mob.getId() == 1158) {
					stop();
					return;
				}

				Mob mobCopy = mob.copy();
				mob.remove();

				if (mobCopy != null) {
					MobManager.spawn(mobCopy);
				}

				stop();
			}
		}
	}

}
