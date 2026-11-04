package com.koios.game.model.mob.impl;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

public class MobDarkBeast extends Mob {

	public MobDarkBeast(int index, Location location) {
		super(index, 2783, location);
	}

	public MobDarkBeast(Location location) {
		super(2783, location);
		setWalkingType(0);
	}

	@Override
	public MobDarkBeast copy() {
		MobDarkBeast mob = new MobDarkBeast(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		super.dropItems(player);
		player.increaseVariable("kc_dark_beast", 1);
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id == 11235) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				Player player = maybePlayer.get();
				player.sendMessage("<col=800000>A dark bow has dropped on the ground!");
				AchievementManager.performed(player, Achievement.OBTAIN_DARK_BOW);
			}
		}
	}

}
