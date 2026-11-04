package com.koios.game.content.minigames;

import com.koios.Constants;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;

public class MinigameMob extends Mob {

	protected MinigameMob(int index, int id, Location loc, Minigame minigame) {
		super(index, id, loc);
		this.minigame = minigame;
	}

	private Minigame minigame = null;

	public Minigame getMinigame() {
		return minigame;
	}

	@Override
	public boolean isFromMinigame() {
		return true;
	}

	@Override
	public void update() {
		if (minigame == null || minigame.isOver()) {
			remove();
			return;
		}

		super.update();
	}

	/**
	 * Spawns a mob inside the minigame.
	 *
	 * @param npc_id
	 * @param loc
	 * @param combat_stats
	 * @param minigame
	 * @return
	 */
	public static Mob spawnMob(int npc_id, Location loc, int[] combat_stats, Minigame minigame) {
		if (loc == null || npc_id < 0 || combat_stats == null || combat_stats.length < 4 || minigame == null) {
			return null;
		}

		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; ++i) {
			if (MobManager.mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			return null;
		}

		MinigameMob mob = new MinigameMob(index, npc_id, loc, minigame);
		mob.hitpoints = combat_stats[2];
		mob.maximumHitpoints = combat_stats[2];
		mob.setMaxHit(combat_stats[3]);
		mob.setAttack(combat_stats[0]);
		mob.defence = combat_stats[1];
		mob.setSkill(new Skill(mob, mob.getAttack(), mob.getDefence(), mob.hitpoints));
		mob.setWalkingType(0);
		MobManager.mobs[index] = mob;
		return mob;
	}

}
